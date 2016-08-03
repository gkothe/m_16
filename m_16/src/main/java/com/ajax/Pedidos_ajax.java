package com.ajax;

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
import org.json.simple.parser.JSONParser;

public class Pedidos_ajax {

	public static int horas_fim_pedido = 6;

	public static void carregaBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String sql = "SELECT cod_cidade from  distribuidora where id_distribuidora  =?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();
		int codcidade = 0;
		while (rs.next()) {

			codcidade = rs.getInt("cod_cidade");
		}

		st = conn.prepareStatement("SELECT cod_bairro, desc_bairro from bairros where cod_cidade  = ? order by desc_bairro asc");
		st.setInt(1, codcidade);
		rs = st.executeQuery();

		while (rs.next()) {
			JSONObject bairro = new JSONObject();
			bairro.put("cod_bairro", rs.getString("cod_bairro"));
			bairro.put("desc_bairro", rs.getString("desc_bairro"));

			retorno.add(bairro);
		}

		out.print(retorno.toJSONString());
	}

	public static void carregaPedidosAbertos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		JSONArray pedidos = new JSONArray();
		PrintWriter out = response.getWriter();

		String num_pedido_aberto = request.getParameter("num_pedido_aberto") == null ? "" : request.getParameter("num_pedido_aberto"); //
		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto");
		String cod_bairro_aberto = request.getParameter("cod_bairro_aberto") == null ? "" : request.getParameter("cod_bairro_aberto");
		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_ini_hora = request.getParameter("data_pedido_ini_hora") == null ? "" : request.getParameter("data_pedido_ini_hora");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String data_pedido_fim_hora = request.getParameter("data_pedido_fim_hora") == null ? "" : request.getParameter("data_pedido_fim_hora");
		String val_ini_aberto = request.getParameter("val_ini_aberto") == null ? "" : request.getParameter("val_ini_aberto");
		String val_fim_aberto = request.getParameter("val_fim_aberto") == null ? "" : request.getParameter("val_fim_aberto");
		String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");

		String sql = "select * from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro where ID_DISTRIBUIDORA = ? and (flag_status = 'A' or flag_status = 'E') ";

		if (!num_pedido_aberto.equalsIgnoreCase("")) {
			sql = sql + " and NUM_PED  = ? ";
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql = sql + " and flag_status  = ? ";
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql = sql + " and id_pedido in (select id_pedido from pedido_item where id_prod = ? ) ";
		}

		if (!cod_bairro_aberto.equalsIgnoreCase("")) {
			sql = sql + " and pedido. COD_BAIRRO = ? ";
		}

		if (!(data_pedido_ini.equalsIgnoreCase("")) && data_pedido_ini_hora != null) {
			sql = sql + "  and  data_pedido >= ?";
		}

		if (!(data_pedido_fim.equalsIgnoreCase("")) && data_pedido_fim_hora != null) {
			sql = sql + "  and  data_pedido <= ?";
		}

		if (!val_ini_aberto.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD >= ? ";
		}

		if (!val_fim_aberto.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD <= ? ";
		}
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);

		int contparam = 2;
		if (!num_pedido_aberto.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(num_pedido_aberto));
			contparam++;
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_situacao));
			contparam++;
		}

		if (!id_produto.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_produto));
			contparam++;
		}

		if (!cod_bairro_aberto.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(cod_bairro_aberto));
			contparam++;
		}

		if (!(data_pedido_ini.equalsIgnoreCase("")) && data_pedido_ini_hora != null) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_pedido_ini + " " + data_pedido_ini_hora);
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_pedido_fim.equalsIgnoreCase("")) && data_pedido_fim_hora != null) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_pedido_fim + " " + data_pedido_fim_hora);
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!val_ini_aberto.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_ini_aberto));
			contparam++;
		}

		if (!val_fim_aberto.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_fim_aberto));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		PreparedStatement st2;
		ResultSet rs2;
		while (rs.next()) {
			JSONObject objRetorno = new JSONObject();
			sql = " SELECT cONCAT('',QTD_PROD ,' x ', DESC_ABREVIADO) AS DESCPROD FROM PEDIDO_ITEM INNER JOIN produtos ON produtos.ID_PROD  =  PEDIDO_ITEM.ID_PROD AND ID_PEDIDO = ?  ";
			st2 = conn.prepareStatement(sql);
			st2.setInt(1, coddistr);
			rs2 = st2.executeQuery();
			String prods = "";
			int qtdprod = 0;
			while (rs2.next()) {

				qtdprod++;
				prods = prods + ", " + rs2.getString("DESCPROD");

			}

			prods = prods.replaceFirst(",", "");

			objRetorno.put("DESCPROD", prods);
			objRetorno.put("qtdprod", qtdprod);
			objRetorno.put("data_formatada", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO")));
			objRetorno.put("NUM_PED", rs.getString("NUM_PED"));
			objRetorno.put("DESC_BAIRRO", rs.getString("DESC_BAIRRO"));
			objRetorno.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			objRetorno.put("FLAG_STATUS", rs.getString("FLAG_STATUS"));
			objRetorno.put("ID_PEDIDO", rs.getString("ID_PEDIDO"));

			pedidos.add(objRetorno);
		}

		out.print(pedidos.toJSONString());

	}

	public static void carregaPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //

		String sql = "select * from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro where ID_DISTRIBUIDORA = ? and id_pedido = ? and  (flag_status = 'A' or flag_status = 'E')  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_pedido));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			objRetorno.put("desc_bairro", rs.getString("DESC_BAIRRO"));
			objRetorno.put("data_pedido", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO")));
			objRetorno.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			objRetorno.put("VAL_ENTREGA", rs.getString("VAL_ENTREGA"));
			objRetorno.put("ID_PEDIDO", rs.getString("ID_PEDIDO"));

			int user = rs.getInt("id_usuario");
			String status = rs.getString("FLAG_STATUS");

			objRetorno.put("flag_status", status);

			sql = " 	SELECT *, VAL_UNIT * QTD_PROD as VAL_TOTAL FROM PEDIDO_ITEM INNER JOIN produtos ON produtos.ID_PROD  =  PEDIDO_ITEM.ID_PROD AND ID_PEDIDO = ? ";

			PreparedStatement st2 = conn.prepareStatement(sql);
			st2.setInt(1, Integer.parseInt(id_pedido));
			ResultSet rs2 = st2.executeQuery();
			JSONArray prods = new JSONArray();

			while (rs2.next()) {
				JSONObject obj = new JSONObject();

				obj.put("ID_PROD", rs2.getInt("ID_PROD"));
				obj.put("DESC_PROD", rs2.getString("DESC_PROD"));
				obj.put("QTD_PROD", rs2.getInt("QTD_PROD"));
				obj.put("VAL_UNIT", rs2.getDouble("VAL_UNIT"));
				obj.put("VAL_TOTAL", rs2.getDouble("VAL_TOTAL"));

				prods.add(obj);

			}

			objRetorno.put("prods", prods);
			objRetorno.put("darok", false);

			if (status.equalsIgnoreCase("E")) {

				st2 = conn.prepareStatement("SELECT DESC_NOME, DESC_TELEFONE,DESC_ENDERECO, desc_bairro from usuario inner join bairros on bairros.cod_bairro = usuario.cod_bairro  where ID_usuario = ?");
				st2.setInt(1, (user));
				rs2 = st2.executeQuery();

				while (rs2.next()) {

					objRetorno.put("DESC_NOME", rs2.getString("DESC_NOME"));
					objRetorno.put("DESC_TELEFONE", rs2.getString("DESC_TELEFONE"));
					objRetorno.put("DESC_ENDERECO", rs2.getString("DESC_ENDERECO"));
					objRetorno.put("desc_bairro", rs2.getString("desc_bairro"));
					objRetorno.put("m_tempo_entrega", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));
					objRetorno.put("m_data_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));

					Calendar data6 = Calendar.getInstance();
					data6.setTime(rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
					data6.add(Calendar.HOUR_OF_DAY, horas_fim_pedido);

					if (data6.getTime().before(new Date())) {
						objRetorno.put("darok", true);

					}

				}

			}

		}

		out.print(objRetorno.toJSONString());

	}

	public static void finalizandoPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido"); //

		String sql = " select * from  pedido where id_pedido = ? and ID_DISTRIBUIDORA = ? and flag_status = 'E'  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_pedido));
		st.setInt(2, coddistr);
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Pedido inválido! Entre em contato com o suporte");
		} else {

			Calendar data6 = Calendar.getInstance();
			data6.setTime(rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
			data6.add(Calendar.HOUR_OF_DAY, horas_fim_pedido);

			if (data6.getTime().after(new Date())) {
				throw new Exception("A hora atual deve exceder em " + horas_fim_pedido + "h a data de resposta para o pedido ser finalizado manualmente.");
			}

			sql = " update  pedido  set flag_status = 'O' where id_pedido = ? and ID_DISTRIBUIDORA = ? and flag_status = 'E' ";
			st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(id_pedido));
			st.setInt(2, coddistr);
 			st.executeUpdate();

		}

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	public static void responderPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //
		String motivos_json = request.getParameter("motivos_json") == null ? "" : request.getParameter("motivos_json");
		String hora_entrega = request.getParameter("hora_entrega") == null ? "" : request.getParameter("hora_entrega");
		String min_entrega = request.getParameter("min_entrega") == null ? "" : request.getParameter("min_entrega");
		String resposta = request.getParameter("resposta") == null ? "" : request.getParameter("resposta");

		if(hora_entrega.equalsIgnoreCase("")){
			hora_entrega = "0";	
		}
		
		if(min_entrega.equalsIgnoreCase("")){
			min_entrega = "0";	
		}
		
		String sql = " 	select * from pedido  where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A'  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_pedido));
		ResultSet rs = st.executeQuery();

		if (!rs.next()) {
			throw new Exception("Pedido inválido, contate o suporte.");
		} else {

			if (resposta.equalsIgnoreCase("A")) {
				
				if (hora_entrega == "0" && min_entrega == "0") {
					throw new Exception("Você deve preencher o(s) campo(s) de tempo estimado para poder responder o pedido.");
				}
				
				if(!Utilitario.isNumeric(hora_entrega) || !Utilitario.isNumeric(min_entrega)){
					throw new Exception("Você deve preencher o(s) campo(s) de tempo estimado corretamente poder responder o pedido.");
				}
				
				
				// fazer whatever tem q fazer e setar o pedido para E  //TODO pagamento acho
				String tempoentrega = hora_entrega + ":" + min_entrega;
				sql = "update  pedido  set flag_status = 'E', `TEMPO_ESTIMADO_ENTREGA` =  ? , `DATA_PEDIDO_RESPOSTA` = NOW()  where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A' ";
				st = conn.prepareStatement(sql);
				st.setString(1, tempoentrega);
				st.setInt(2, coddistr);
				st.setInt(3, Integer.parseInt(id_pedido));
				st.executeUpdate();
				
				
				objRetorno.put("msg", "ok");

			} else if (resposta.equalsIgnoreCase("R")) {
				
				JSONArray motivos = (JSONArray)new JSONParser().parse(motivos_json);
				
				if(motivos.size()==0){
					throw new Exception ( "Você deve escolher pelo menos um motivo para recusar o pedido." );
				}
				
				sql = "update  pedido  set flag_status = 'R', DATA_PEDIDO_RESPOSTA = NOW() where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A' ";
				st = conn.prepareStatement(sql);
				st.setInt(1, coddistr);
				st.setInt(2, Integer.parseInt(id_pedido));
				st.executeUpdate();
				
				int codmotivo =0;
				for (int i = 0; i < motivos.size(); i++) {
					
					try {
						codmotivo = Integer.parseInt( motivos.get(i).toString());	
					} catch (Exception e) {
						throw new Exception("Erro no processamento dos 'motivos'. Entre em contato com suporte. ");
					}
					
					
					sql = "INSERT INTO pedido_motivos_recusa (`ID_PEDIDO`, `COD_MOTIVO`) VALUES (?, ?) ";
					st = conn.prepareStatement(sql);
					st.setInt(1, Integer.parseInt(id_pedido));
					st.setInt(2, codmotivo);
					st.executeUpdate();
					
				}
				
				objRetorno.put("msg", "ok");

			}else{
				throw new Exception ( "Resposta inválida, contate o suporte." );
			}
		}

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

}
