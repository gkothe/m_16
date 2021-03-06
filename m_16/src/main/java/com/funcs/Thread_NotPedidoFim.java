package com.funcs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

import com.configs.Conexao;

public class Thread_NotPedidoFim extends Thread {

	Connection conn = null;
	PreparedStatement st = null;
	ResultSet rs = null;
	Date date = null;
	Date agora = null;
	long secs_param = 0;
	long seconds = 0;
	long rodateste = 0;
	Calendar data6;
	JSONObject data;
	StringBuffer varname1;
	JSONObject obj = null;
	public boolean rodar = true;
	String html = "";
	String title = "";
	

	public void run() {

		try {
			conn = Conexao.getConexao();

			Sys_parametros sys = new Sys_parametros(conn);
			secs_param = sys.getNum_segs_not_final_exec();
			rodateste = secs_param * 1000;
			conn.close();
			while (true) {
				conn = Conexao.getConexao();
				sendNotificacao(conn, sys);
				checkExpired(conn, sys);
				checkAgendamentosNaoResp(conn, sys);
				checkLembretes(conn, sys);
				conn.close();
				this.sleep(rodateste);
			}

		} catch (Exception e) {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}

			try {
				this.sleep(5000);
				if (rodar) {
					run();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doShutdown() {

		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}

	}

	private void sendNotificacao(Connection conn, Sys_parametros sys) {

		varname1 = new StringBuffer();
		varname1.append("select id_pedido,desc_endereco_entrega,desc_endereco_num_entrega,desc_endereco_complemento_entrega,pedido.cod_bairro, ");
		varname1.append("       num_ped, ");
		varname1.append("       usuario.desc_email       as mail, ");
		varname1.append("       addtime(coalesce(data_agenda_entrega, data_pedido), ");
		varname1.append("       tempo_estimado_entrega) as tempoteste ");
		varname1.append(" from   pedido ");
		varname1.append("       inner join usuario ");
		varname1.append("               on usuario.id_usuario = pedido.id_usuario ");
		varname1.append(" WHERE  flag_status = 'E' and FLAG_PEDIDO_RET_ENTRE = 'T' ");// flag_resposta_usuario?
		varname1.append("       AND flag_not_final = 'N'");

		try {
			data6 = Calendar.getInstance();
			st = conn.prepareStatement(varname1.toString());
			rs = st.executeQuery();
			while (rs.next()) {

				data6.setTime(rs.getTimestamp("tempoteste"));
				data6.add(Calendar.MINUTE, sys.getNum_minutos_not_final());

				if (data6.getTime().before(new Date())) {

					data = new JSONObject();
					data.put("id_ped_fim", rs.getString("ID_PEDIDO"));
					data.put("num_ped_fim", rs.getString("NUM_PED"));

					String endereco = rs.getString("DESC_ENDERECO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_ENTREGA");
					String num = rs.getString("DESC_ENDERECO_NUM_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_NUM_ENTREGA");
					String compl = rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA");
					String bairro = Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0);

					String endfinal = endereco + ", " + num + " " + compl + ", " + bairro + "";

					Utilitario.oneSginal(sys, rs.getString("mail"), "Você recebeu o pedido no endereço " + endfinal + "? Por favor nos informe!", data);

					st = conn.prepareStatement("update pedido set FLAG_NOT_FINAL = 'S'  where id_pedido = ? ");
					st.setInt(1, rs.getInt("id_pedido"));
					st.executeUpdate();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	
	
	
	
	
	private void checkLembretes(Connection conn, Sys_parametros sys) {//testar e botar na thead para rodaer.

		varname1 = new StringBuffer();
		varname1.append("SELECT ");
		varname1.append(" distribuidora.id_distribuidora, distribuidora.desc_mail as emaildis, distribuidora_mobile.desc_mail as emailmobile, id_pedido, num_ped ");
		varname1.append("FROM   pedido ");
		varname1.append("       inner join distribuidora ");
		varname1.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
		varname1.append("       left join distribuidora_mobile ");
		varname1.append("               ON distribuidora_mobile.id_distribuidora = distribuidora.id_distribuidora and flag_role = 'A' ");
		varname1.append("WHERE  flag_status = 'A' ");
		
		varname1.append("       AND data_proxnot < now() ");
		varname1.append("       AND flag_pedido_ret_entre = 'T' ");
		varname1.append("       AND flag_modoentrega = 'A' ");
		varname1.append(" ");
		
		
		try {
			st = conn.prepareStatement(varname1.toString());
			rs = st.executeQuery();
			
			while (rs.next()) {
				
				MobileController. msgLojasMobile(conn, sys, rs.getInt("id_pedido"), 4);
		
				varname1 = new StringBuffer();
				varname1.append(" update pedido set data_proxnot = null where id_pedido = ? ");
				st = conn.prepareStatement(varname1.toString());
				st.setLong(1, (rs.getInt("id_pedido")));
				st.executeUpdate();
			}

			
			
			//fazer update na distribui
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}
	
	
	
	private void checkAgendamentosNaoResp(Connection conn, Sys_parametros sys) {//testar e botar na thead para rodaer.

		varname1 = new StringBuffer();
		varname1.append("SELECT ");
		varname1.append(" ");
		varname1.append("distribuidora.id_distribuidora, distribuidora.desc_mail as emaildis, distribuidora_mobile.desc_mail as emailmobile ");
		varname1.append(" ");
		varname1.append("FROM   pedido ");
		varname1.append("       inner join distribuidora ");
		varname1.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
		varname1.append("       left join distribuidora_mobile ");
		varname1.append("               ON distribuidora_mobile.id_distribuidora = distribuidora.id_distribuidora and flag_role = 'A' ");
		varname1.append(" ");
		varname1.append(" ");
		varname1.append("WHERE  flag_status = 'A' ");
		varname1.append("       AND Date_add(data_pedido, interval ? minute) < now() ");
		varname1.append("       AND Date_add(coalesce(data_mob_notific_last,'20150101'), interval ? minute)  < now() ");
		varname1.append("       AND flag_pedido_ret_entre = 'T' ");
		varname1.append("       AND flag_modoentrega = 'A' ");
		varname1.append(" ");
		varname1.append(" ");
		varname1.append("group by distribuidora.id_distribuidora, distribuidora.desc_mail, distribuidora_mobile.desc_mail");
	
		html = "Olá, você possui pedidos de agendamentos que ainda nao foram não respondidos. <br> Por favor clique <a href='" + sys.getUrl_system() + "'> AQUI </a> para acessar nosso sistema e responde-los.";
		title = sys.getSys_fromdesc() + " - Você possui pedidos de agendamento que ainda não foram respondidos!";
		data = new JSONObject();
		data.put("gominhaloja", true);
		try {
			st = conn.prepareStatement(varname1.toString());
			st.setInt(1, sys.getSys_minutes_agen_not_resp());
			st.setInt(2, sys.getSys_minutes_agen_not_resp());
			rs = st.executeQuery();
			
			while (rs.next()) {
				
				Utilitario.sendEmail(rs.getString("emaildis"), html, title, conn);
				if (rs.getString("emailmobile") != null && !(rs.getString("emailmobile").equalsIgnoreCase(""))) {

					Utilitario.oneSginal(sys, rs.getString("emailmobile"), title, data);
					if (!rs.getString("emailmobile").equalsIgnoreCase(rs.getString("emaildis"))) {
						Utilitario.sendEmail(rs.getString("emailmobile"), html, title, conn);
					}
				}
				
				
				varname1 = new StringBuffer();
				varname1.append(" update distribuidora set DATA_MOB_NOTIFIC_LAST = now() where id_distribuidora = ? ");
				st = conn.prepareStatement(varname1.toString());
				st.setLong(1, (rs.getInt("id_distribuidora")));
				st.executeUpdate();
			}

			
			
			//fazer update na distribui
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}
	
	private void checkExpired(Connection conn, Sys_parametros sys) {

		varname1 = new StringBuffer();
		varname1.append("SELECT *,usuario.desc_email as mail, ");
		varname1.append("       Timestampdiff(second, Now(), Addtime (COALESCE(data_agenda_entrega, data_pedido), tempo_estimado_desejado))                   AS secs, ");
		varname1.append("       case when flag_modoentrega = 'T' then SEC_TO_TIME( abs(TIMESTAMPDIFF(second,NOW(),Addtime (COALESCE(data_agenda_entrega,data_pedido),tempo_estimado_desejado)))) else '00:00:00' end as timedif ");
		varname1.append("FROM   pedido ");
		varname1.append("       INNER JOIN usuario ");
		varname1.append("               ON usuario.id_usuario = pedido.id_usuario ");
		varname1.append("WHERE ");
		varname1.append("       flag_status = 'A' and   ((flag_modoentrega = 'T'   and  (Timestampdiff(second, Now(), Addtime ( data_pedido, tempo_estimado_desejado)) < 0))   or  ( flag_modoentrega = 'A' and (Timestampdiff(second, Now(),data_agenda_entrega )) < 0)) ");

		try {
			st = conn.prepareStatement(varname1.toString());
			rs = st.executeQuery();
			while (rs.next()) {
				data = new JSONObject();
				data.put("id_ped", rs.getString("ID_PEDIDO"));
				data.put("num_ped", rs.getString("NUM_PED"));
				obj = MobileController.cancelaPedido(null, null, conn, rs.getInt("ID_USUARIO"), sys, rs.getString("ID_PEDIDO"), "Cancelado pelo sistema", sys.getCod_cancelamentosys() + "", false);

				if (obj.get("msg").toString().equalsIgnoreCase("ok")) {

					Utilitario.oneSginal(sys, rs.getString("mail"), "Aviso. Pedido cancelado automaticamente. Motivo: A loja não respondeu dentro do tempo desejado de entrega.", data);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

}