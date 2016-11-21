package com.funcs;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Home_ajax {

	public static void checkPedidos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		JSONArray pedidos_todos = new JSONArray();

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String sql = "select count(id_pedido) as qtd from pedido where ID_DISTRIBUIDORA = ? and flag_status = 'A'";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();

		objRetorno.put("tem", "false");

		if (rs.next()) {
			if (rs.getInt("qtd") != 0) {

				objRetorno.put("qtd", rs.getInt("qtd"));
				objRetorno.put("tem", "true");

			}

		}

		sql = "select * from pedido join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido  where ID_DISTRIBUIDORA = ? and (flag_status = 'C' and FLAG_POPUPINICIAL = 'N')  ";
		st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		rs = st.executeQuery();
		objRetorno.put("canc_pop", false);

		if (rs.next()) {
			objRetorno.put("canc_pop", true);
			objRetorno.put("id_pedpop", rs.getString("id_pedido"));
			objRetorno.put("num_pedpop", rs.getString("num_ped"));

			sql = " UPDATE pedido_motivo_cancelamento  SET FLAG_POPUPINICIAL = 'S'  where   id_pedido = " + rs.getString("id_pedido");

			st = conn.prepareStatement(sql.toString());
			st.executeUpdate();
		}

		sql = "select * from pedido join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido  where ID_DISTRIBUIDORA = ? and (flag_status = 'C' and FLAG_VIZUALIZADO_CANC = 'N')  ";
		st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		rs = st.executeQuery();
		objRetorno.put("canc_vizu", false);
		if (rs.next()) {
			objRetorno.put("canc_vizu", true);
		}

		objRetorno.put("flag_vizualizado", "S");
		if (objRetorno.get("tem").toString().equalsIgnoreCase("true")) {

			sql = "" + "SELECT " + "           COALESCE(flag_vizualizado,'N') AS flag_vizualizado " + "FROM       pedido " + " inner join distribuidora on pedido.id_distribuidora = distribuidora.id_distribuidora left JOIN bairros " + "ON         bairros.cod_bairro = pedido.cod_bairro " + "WHERE     distribuidora.id_distribuidora = ? " + "AND        flag_status = 'A' " + "AND        Coalesce(bairros.cod_cidade,distribuidora.cod_cidade) = "
					+ request.getSession(false).getAttribute("cod_cidade").toString() + " and  COALESCE(flag_vizualizado,'N') = 'N' group by COALESCE(flag_vizualizado,'N');";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);

			rs = st.executeQuery();
			if (rs.next()) {
				if (rs.getString("flag_vizualizado").equalsIgnoreCase("N")) {
					objRetorno.put("flag_vizualizado", "N");
				}
			}

			sql = "select id_pedido,DESC_BAIRRO,NUM_PED,VAL_TOTALPROD,DATA_PEDIDO, NOW() as agora, Coalesce(flag_vizualizado,'N') as flag_vizualizado  from pedido inner join distribuidora on pedido.id_distribuidora = distribuidora.id_distribuidora left join bairros on bairros.cod_bairro = pedido.cod_bairro where distribuidora.ID_DISTRIBUIDORA = ? and flag_status = \'A\' and Coalesce(bairros.cod_cidade,distribuidora.cod_cidade) = " + request.getSession(false).getAttribute("cod_cidade").toString()
					+ "  order by data_pedido asc limit 5";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);
			rs = st.executeQuery();
			String flagvizu;
			while (rs.next()) {

				JSONObject pedidos = new JSONObject();

				pedidos.put("num_ped", rs.getString("NUM_PED"));
				pedidos.put("desc_bairro", rs.getString("DESC_BAIRRO")==null?"Retirada no local" : rs.getString("DESC_BAIRRO")) ;
				pedidos.put("valor", rs.getString("VAL_TOTALPROD"));
				pedidos.put("id_pedido", rs.getString("id_pedido"));
				flagvizu = "";
				if (rs.getString("flag_vizualizado") != null) {
					flagvizu = rs.getString("flag_vizualizado");
				}
				pedidos.put("flag_vizualizado", flagvizu == "" ? "N" : flagvizu);

				Date date_pedido = rs.getTimestamp("DATA_PEDIDO");
				Date agora = rs.getTimestamp("agora");
				String texto_minutos = "";
				// double time =

				Calendar date_pedidocal = Calendar.getInstance();
				date_pedidocal.setTime(date_pedido);

				Calendar agoracal = Calendar.getInstance();
				agoracal.setTime(agora);

				long diferenca = agoracal.getTimeInMillis() - date_pedidocal.getTimeInMillis();
				long diferencaMin = diferenca / (60 * 1000); // DIFERENCA EM
																// MINUTOS

				if (diferencaMin <= 60) {
					texto_minutos = "Há " + diferencaMin + " minuto(s).";
				}

				if (diferencaMin >= 60 && diferencaMin <= 1440) {
					diferencaMin = diferencaMin / 60;
					texto_minutos = "Há " + diferencaMin + " horas(s).";

				}

				if (diferencaMin > 1440) {
					diferencaMin = diferencaMin / 1440;
					texto_minutos = "Há " + diferencaMin + " dia(s).";

				}

				pedidos.put("texto_minutos", texto_minutos);
				pedidos_todos.add(pedidos);
			}
		}

		objRetorno.put("pedidos", pedidos_todos);

		out.print(objRetorno.toJSONString());

	}

	public static void getLogo(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String sql = "select * from distribuidora  where	 ID_DISTRIBUIDORA = ?";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			objRetorno.put("desc_nome", rs.getString("DESC_NOME_ABREV"));
		}

		objRetorno.put("nome_img", "images/logos/logo_" + coddistr + ".jpg");
		out.print(objRetorno.toJSONString());

	}

	public static void loadMotivos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objret = new JSONObject();

		JSONArray motivos = new JSONArray();

		String sql = "select * from  motivos_recusa order by DESC_MOTIVO";

		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();

		while (rs.next()) {

			JSONObject obj = new JSONObject();
			obj.put("COD_MOTIVO", rs.getString("COD_MOTIVO"));
			obj.put("DESC_MOTIVO", rs.getString("DESC_MOTIVO"));

			motivos.add(obj);
		}

		Sys_parametros sys = new Sys_parametros(conn);
		
		objret.put("mot", motivos);
		objret.put("estoque", sys.getCod_recusa_estoque());

		out.print(objret.toJSONString());

	}

	public static void autoComplete(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();

		String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd");
		org.json.simple.JSONArray objRetornoArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject objValor = new org.json.simple.JSONObject();
		if (cmd.equalsIgnoreCase("autocomplete")) {

			String campo = request.getParameter("campo") == null ? "" : request.getParameter("campo");
			String q = request.getParameter("q") == null ? "" : request.getParameter("q");

			if (campo.equals("id_produto")) {

				String sql = "select produtos.id_prod, produtos.desc_prod from produtos_distribuidora inner join produtos on produtos_distribuidora.id_prod =  produtos.id_prod where id_distribuidora = ? and produtos_distribuidora.flag_ativo = 'S' and produtos.FLAG_ATIVO = 'S' and produtos_distribuidora.id_prod = ? limit 10";

				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, coddistr);
				st.setString(2, q);
				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					objValor.put("descr", rs.getString("desc_prod"));
				}

				out.print(objValor.toJSONString());
			} else if (campo.equals("desc_produto")) {

				String sql = "select produtos.id_prod as id, produtos.desc_prod as descr from produtos_distribuidora inner join produtos on produtos_distribuidora.id_prod =  produtos.id_prod where id_distribuidora = ? and produtos_distribuidora.flag_ativo = 'S' and produtos.FLAG_ATIVO = 'S' and (produtos.desc_prod like  ?  or produtos.desc_abreviado like ?) limit 10";

				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, coddistr);
				st.setString(2, "%" + q + "%");
				st.setString(3, "%" + q + "%");
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					objValor = new org.json.simple.JSONObject();
					objValor.put("descr", rs.getString("descr"));
					objValor.put("id", rs.getString("id"));
					objRetornoArray.add(objValor);
				}
				out.print(objRetornoArray.toJSONString());

			} else if (campo.equals("id_produto_listagem")) {
				// em relação ao autocomplete do 'id_produto' e do 'desc_produto' muda o join, a distribuidora vai na ligação.
				// considerando os ativos do distribuidora e do sistema, caso precisar separar os flags, tem q mandar algum parametro extra.

				String sql = "select produtos.id_prod, desc_prod  from produtos  left join produtos_distribuidora on produtos.id_prod = produtos_distribuidora.id_prod	 and ID_DISTRIBUIDORA = ? where (produtos.flag_ativo = 'S') and  produtos_distribuidora.id_prod = ? order by desc_prod asc limit 10";

				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, coddistr);
				st.setString(2, q);
				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					objValor.put("descr", rs.getString("desc_prod"));
				}

				out.print(objValor.toJSONString());

			} else if (campo.equals("desc_produto_listagem")) {
				// em relação ao autocomplete do 'id_produto' e do 'desc_produto' muda o join, a distribuidora vai na ligação.
				// considerando os ativos do distribuidora e do sistema, caso precisar separar os flags, tem q mandar algum parametro extra.

				String sql = "select produtos.id_prod as id, desc_prod as descr from produtos  left join produtos_distribuidora on produtos.id_prod = produtos_distribuidora.id_prod	 and ID_DISTRIBUIDORA = ? where (produtos.flag_ativo = 'S') and  (produtos.desc_prod like  ?  or produtos.desc_abreviado like ?) order by desc_prod asc  limit 10";

				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, coddistr);
				st.setString(2, "%" + q + "%");
				st.setString(3, "%" + q + "%");
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					objValor = new org.json.simple.JSONObject();
					objValor.put("descr", rs.getString("descr"));
					objValor.put("id", rs.getString("id"));
					objRetornoArray.add(objValor);
				}
				out.print(objRetornoArray.toJSONString());

			}
		}

	}

}
