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

public class Pedidos_ajax {

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
				rs2 = st.executeQuery();

				while (rs2.next()) {

					objRetorno.put("DESC_NOME", rs.getString("DESC_NOME") );
					objRetorno.put("DESC_TELEFONE", rs.getString("DESC_TELEFONE"));
					objRetorno.put("DESC_ENDERECO", rs.getString("DESC_ENDERECO"));
					objRetorno.put("desc_bairro",rs.getString("desc_bairro") );
					objRetorno.put("m_tempo_entrega", rs.getTime("TEMPO_ESTIMADO_ENTREGA") );
					objRetorno.put("m_data_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));
					
					
				}

			}

		}

		out.print(objRetorno.toJSONString());

	}

}
