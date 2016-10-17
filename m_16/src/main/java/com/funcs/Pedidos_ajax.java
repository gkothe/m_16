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
import org.json.simple.parser.JSONParser;

public class Pedidos_ajax {

	// public static int horas_fim_pedido = 6;

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
		JSONObject retorno = new JSONObject();
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
		String flag_visu = request.getParameter("flag_visu") == null ? "" : request.getParameter("flag_visu");
		String flag_pedido_ret_entre = request.getParameter("flag_pedido_ret_entre") == null ? "" : request.getParameter("flag_pedido_ret_entre");

		String temfiltro = "N";

		String sql = "select * from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido  where ID_DISTRIBUIDORA = ? and (flag_status = 'A' or flag_status = 'E' or (flag_status = 'C' and FLAG_CONFIRMADO_DISTRIBUIDORA = 'N' ) ) ";

		if (!num_pedido_aberto.equalsIgnoreCase("")) {
			sql = sql + " and NUM_PED  = ? ";
			temfiltro = "S";
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql = sql + " and flag_status  = ? ";
			temfiltro = "S";
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql = sql + " and id_pedido in (select id_pedido from pedido_item where id_prod = ? ) ";
			temfiltro = "S";
		}

		if (!cod_bairro_aberto.equalsIgnoreCase("")) {
			sql = sql + " and pedido. COD_BAIRRO = ? ";
			temfiltro = "S";
		}

		if (!(data_pedido_ini.equalsIgnoreCase("")) && data_pedido_ini_hora != null) {
			sql = sql + "  and  data_pedido >= ?";
			temfiltro = "S";
		}

		if (!(data_pedido_fim.equalsIgnoreCase("")) && data_pedido_fim_hora != null) {
			sql = sql + "  and  data_pedido <= ?";
			temfiltro = "S";
		}

		if (!val_ini_aberto.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD >= ? ";
			temfiltro = "S";
		}

		if (!val_fim_aberto.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD <= ? ";
			temfiltro = "S";
		}

		if (!flag_visu.equalsIgnoreCase("")) {
			sql = sql + "  and  flag_vizualizado = ? ";
			temfiltro = "S";
		}

		if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			sql = sql + "  and  flag_pedido_ret_entre = ? ";
			temfiltro = "S";
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

		if (!flag_visu.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_visu));
			contparam++;
		}

		if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_pedido_ret_entre));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		PreparedStatement st2;
		ResultSet rs2;
		while (rs.next()) {
			JSONObject objRetorno = new JSONObject();
			sql = " SELECT cONCAT('',QTD_PROD ,' x ', DESC_ABREVIADO) AS DESCPROD FROM PEDIDO_ITEM INNER JOIN produtos ON produtos.ID_PROD  =  PEDIDO_ITEM.ID_PROD AND ID_PEDIDO = ?  ";
			st2 = conn.prepareStatement(sql);
			st2.setInt(1, rs.getInt("id_pedido"));
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
			objRetorno.put("FLAG_STATUS", rs.getString("FLAG_STATUS"));

			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
				objRetorno.put("DESC_BAIRRO", "Retirar no local");
				objRetorno.put("FLAG_STATUS", rs.getString("FLAG_STATUS").equalsIgnoreCase("E") ? "S" : rs.getString("FLAG_STATUS"));
			} else {
				objRetorno.put("DESC_BAIRRO", rs.getString("DESC_BAIRRO"));
			}

			objRetorno.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));

			objRetorno.put("ID_PEDIDO", rs.getString("ID_PEDIDO"));

			if (rs.getString("FLAG_POPUPINICIAL") != null && !rs.getString("FLAG_POPUPINICIAL").equalsIgnoreCase("")) {
				if (rs.getString("FLAG_POPUPINICIAL").equalsIgnoreCase("N")) {
					retorno.put("canc_vizu", true);
				}
				objRetorno.put("flag_visu", rs.getString("FLAG_POPUPINICIAL"));
			} else {
				objRetorno.put("flag_visu", rs.getString("flag_vizualizado"));
			}

			pedidos.add(objRetorno);
		}
		retorno.put("temfiltro", temfiltro);
		retorno.put("pedidos", pedidos);
		out.print(retorno.toJSONString());

	}

	public static void carregaPedidoshistoricos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		JSONArray pedidos = new JSONArray();
		PrintWriter out = response.getWriter();

		String num_pedido_historico = request.getParameter("num_pedido_historico") == null ? "" : request.getParameter("num_pedido_historico"); //
		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto");
		String cod_bairro_historico = request.getParameter("cod_bairro_historico") == null ? "" : request.getParameter("cod_bairro_historico");
		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_reposta_ini = request.getParameter("data_reposta_ini") == null ? "" : request.getParameter("data_reposta_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String data_reposta_fim = request.getParameter("data_reposta_fim") == null ? "" : request.getParameter("data_reposta_fim");
		String val_ini_historico = request.getParameter("val_ini_historico") == null ? "" : request.getParameter("val_ini_historico");
		String val_fim_historico = request.getParameter("val_fim_historico") == null ? "" : request.getParameter("val_fim_historico");
		String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");
		String flag_pedido_ret_entre = request.getParameter("flag_pedido_ret_entre") == null ? "" : request.getParameter("flag_pedido_ret_entre");

		String sql = "select * from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro where ID_DISTRIBUIDORA = ? and (flag_status = 'O' or flag_status = 'R') ";

		if (!num_pedido_historico.equalsIgnoreCase("")) {
			sql = sql + " and NUM_PED  = ? ";
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql = sql + " and flag_status  = ? ";
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql = sql + " and id_pedido in (select id_pedido from pedido_item where id_prod = ? ) ";
		}

		if (!cod_bairro_historico.equalsIgnoreCase("")) {
			sql = sql + " and pedido. COD_BAIRRO = ? ";
		}

		if (!(data_pedido_ini.equalsIgnoreCase(""))) {
			sql = sql + "  and  data_pedido >= ?";
		}

		if (!(data_pedido_fim.equalsIgnoreCase(""))) {
			sql = sql + "  and  data_pedido <= ?";
		}

		if (!(data_reposta_ini.equalsIgnoreCase(""))) {
			sql = sql + "  and  DATA_PEDIDO_RESPOSTA >= ?";
		}

		if (!(data_reposta_fim.equalsIgnoreCase(""))) {
			sql = sql + "  and  DATA_PEDIDO_RESPOSTA <= ?";
		}

		if (!val_ini_historico.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD >= ? ";
		}

		if (!val_fim_historico.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD <= ? ";
		}

		if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			sql = sql + "  and  flag_pedido_ret_entre = ? ";

		}

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);

		int contparam = 2;
		if (!num_pedido_historico.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(num_pedido_historico));
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

		if (!cod_bairro_historico.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(cod_bairro_historico));
			contparam++;
		}

		if (!(data_pedido_ini.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_pedido_ini + " 00:00");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_pedido_fim.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_pedido_fim + " 23:59");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_reposta_ini.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_reposta_ini + " 00:00");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_reposta_fim.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_reposta_fim + " 23:59");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!val_ini_historico.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_ini_historico));
			contparam++;
		}

		if (!val_fim_historico.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_fim_historico));
			contparam++;
		}

		if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_pedido_ret_entre));
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
			objRetorno.put("data_formatada_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));
			objRetorno.put("NUM_PED", rs.getString("NUM_PED"));

			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
				objRetorno.put("DESC_BAIRRO", "Retirar no local");
			} else {

				objRetorno.put("DESC_BAIRRO", rs.getString("DESC_BAIRRO"));
			}

			objRetorno.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			objRetorno.put("FLAG_STATUS", rs.getString("FLAG_STATUS"));
			objRetorno.put("ID_PEDIDO", rs.getString("ID_PEDIDO"));

			pedidos.add(objRetorno);
		}

		out.print(pedidos.toJSONString());

	}

	public static void carregaPedido_fechado(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //

		String sql = "select * from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro where ID_DISTRIBUIDORA = ? and id_pedido = ? and (flag_status = 'O' or flag_status = 'R')  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_pedido));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			objRetorno.put("tipo_servico", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
				objRetorno.put("desc_bairro", "Retirar no local");
			} else {
				objRetorno.put("m_tempo_max", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_DESEJADO")));
				objRetorno.put("desc_bairro", rs.getString("DESC_BAIRRO"));
			}

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

			if (status.equalsIgnoreCase("O")) {

				objRetorno.put("DESC_NOME", rs.getString("NOME_PESSOA"));
				objRetorno.put("DESC_TELEFONE", rs.getString("NUM_TELEFONECONTATO_CLIENTE"));
				String end = rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega");
				String num = rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega");
				String compl = rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega");

				objRetorno.put("DESC_ENDERECO", end + " " + num + " " + compl);

				objRetorno.put("tipo_servico", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
					objRetorno.put("desc_bairro_ret", "Retirar no local");
				} else {

					objRetorno.put("desc_bairro_ret", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));
				}

//				objRetorno.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));

				objRetorno.put("m_tempo_entrega", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));
				objRetorno.put("m_data_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));

			}
			if (status.equalsIgnoreCase("R")) {

				st2 = conn.prepareStatement(" SELECT * from pedido_motivos_recusa inner join motivos_recusa on motivos_recusa.cod_motivo = pedido_motivos_recusa.cod_motivo  where ID_PEDIDO =  ?");
				st2.setInt(1, Integer.parseInt(id_pedido));
				rs2 = st2.executeQuery();
				JSONArray motivos = new JSONArray();
				while (rs2.next()) {

					motivos.add(rs2.getString("DESC_MOTIVO"));
				}

				objRetorno.put("motivos", motivos);
			}

		}

		out.print(objRetorno.toJSONString());

	}

	public static void carregaPedido_AbertoEnvio(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //

		StringBuffer varname1 = new StringBuffer();
		varname1.append("SELECT * ");
		varname1.append("FROM   pedido ");
		varname1.append("       INNER JOIN bairros ");
		varname1.append("               ON bairros.cod_bairro = pedido.cod_bairro ");
		varname1.append("       LEFT JOIN pedido_motivo_cancelamento ");
		varname1.append("              ON pedido_motivo_cancelamento.id_pedido = pedido.id_pedido ");
		varname1.append("WHERE  id_distribuidora = ? ");
		varname1.append("       AND pedido.id_pedido = ? ");
		varname1.append("       AND ( flag_status = 'A' ");
		varname1.append("              OR flag_status = 'E' ");
		varname1.append("              OR ( flag_status = 'C' ");
		varname1.append("                   AND flag_confirmado_distribuidora = 'N' ) )");

		String sql = "";
		PreparedStatement st = conn.prepareStatement(varname1.toString());
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_pedido));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			objRetorno.put("tipo_servico", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
				objRetorno.put("desc_bairro", "Retirar no local");
			} else {
				objRetorno.put("m_tempo_max", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_DESEJADO")));
				objRetorno.put("desc_bairro", rs.getString("DESC_BAIRRO"));
			}

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
			Sys_parametros sys = new Sys_parametros(conn);
			if (status.equalsIgnoreCase("E") || status.equalsIgnoreCase("C")) {

				objRetorno.put("DESC_NOME", rs.getString("NOME_PESSOA"));
				objRetorno.put("DESC_TELEFONE", rs.getString("NUM_TELEFONECONTATO_CLIENTE"));
				String end = rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega");
				String num = rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega");
				String compl = rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega");

				objRetorno.put("DESC_ENDERECO", end + " " + num + " " + compl);

				objRetorno.put("tipo_servico", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
					objRetorno.put("desc_bairro", "Retirar no local");
				} else {

					objRetorno.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));
				}

//				objRetorno.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));

				objRetorno.put("m_tempo_entrega", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));
				objRetorno.put("m_data_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));

				Calendar data6 = Calendar.getInstance();
				data6.setTime(rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
				data6.add(Calendar.HOUR_OF_DAY, sys.getPED_HORASOKEY());

				if (data6.getTime().before(new Date())) {
					objRetorno.put("darok", true);

				}

			}

			if (status.equalsIgnoreCase("C")) {

				sql = " update  pedido_motivo_cancelamento  set FLAG_POPUPINICIAL = 'S' where id_pedido = ?  ";
				st = conn.prepareStatement(sql);
				st.setInt(1, Integer.parseInt(id_pedido));
				st.executeUpdate();

				objRetorno.put("DATA_CANCELAMENTO", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_CANCELAMENTO")));

				objRetorno.put("darok", true);

			}

			sql = " update  pedido  set flag_vizualizado = 'S' where id_pedido = ?  ";
			st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(id_pedido));
			st.executeUpdate();

		}

		out.print(objRetorno.toJSONString());

	}

	public static void finalizandoPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		Sys_parametros sys = new Sys_parametros(conn);
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido"); //

		String sql = " select * from  pedido  left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido  where pedido.id_pedido = ? and ID_DISTRIBUIDORA = ? and (flag_status = 'E' or flag_status = 'S' or (flag_status = 'C' and FLAG_CONFIRMADO_DISTRIBUIDORA = 'N' ) ) ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_pedido));
		st.setInt(2, coddistr);
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Pedido inválido! Entre em contato com o suporte");
		} else {

			Calendar data6 = Calendar.getInstance();
			data6.setTime(rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
			data6.add(Calendar.HOUR_OF_DAY, sys.getPED_HORASOKEY());

			
			if(rs.getString("flag_status").equalsIgnoreCase("C")){
				
				sql = " update  pedido_motivo_cancelamento  set FLAG_CONFIRMADO_DISTRIBUIDORA = 'S' where id_pedido = ?  ";
				st = conn.prepareStatement(sql);
				st.setInt(1, Integer.parseInt(id_pedido));
				st.executeUpdate();
				
			}else{
			
				if (data6.getTime().after(new Date())) {
					throw new Exception("A hora atual deve exceder em " + sys.getPED_HORASOKEY() + "h a data de resposta para o pedido ser finalizado manualmente.");
				}
				
				sql = " update  pedido  set flag_status = 'O' where id_pedido = ? and ID_DISTRIBUIDORA = ? and flag_status = 'E' ";
				st = conn.prepareStatement(sql);
				st.setInt(1, Integer.parseInt(id_pedido));
				st.setInt(2, coddistr);
				st.executeUpdate();
				
				
			}
			

		}

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	public static void responderPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //
		String motivos_json = request.getParameter("motivos_json") == null ? "" : request.getParameter("motivos_json");
		// String hora_entrega = request.getParameter("hora_entrega") == null ? "" : request.getParameter("hora_entrega");
		// String min_entrega = request.getParameter("min_entrega") == null ? "" : request.getParameter("min_entrega");
		String resposta = request.getParameter("resposta") == null ? "" : request.getParameter("resposta");
		String m_tempo_entrega_inp = request.getParameter("m_tempo_entrega_inp") == null ? "" : request.getParameter("m_tempo_entrega_inp");

		/*
		 * if (hora_entrega.equalsIgnoreCase("")) { hora_entrega = "0"; }
		 * 
		 * if (min_entrega.equalsIgnoreCase("")) { min_entrega = "0"; }
		 */

		String sql = " 	select * from pedido  where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A'  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_pedido));
		ResultSet rs = st.executeQuery();

		if (!resposta.equalsIgnoreCase("A") && !resposta.equalsIgnoreCase("R")) {
			throw new Exception("Tipo de reposta inválida.");
		}

		if (!rs.next()) {
			throw new Exception("Pedido inválido, contate o suporte.");
		} else {
			String status = "";
		
			if(status.equalsIgnoreCase("E") || status.equalsIgnoreCase("R") || status.equalsIgnoreCase("S")){
				throw new Exception("Pedido já respondido");
			}
			if(status.equalsIgnoreCase("C")){
				throw new Exception("Este pedido foi cancelado.");
			}
			
			if (resposta.equalsIgnoreCase("A")) {

				/*
				 * if (hora_entrega == "0" && min_entrega == "0") { throw new Exception("Você deve preencher o(s) campo(s) de tempo estimado para poder responder o pedido."); }
				 * 
				 * if (!Utilitario.isNumeric(hora_entrega) || !Utilitario.isNumeric(min_entrega)) { throw new Exception("Você deve preencher o(s) campo(s) de tempo estimado corretamente poder responder o pedido."); }
				 */

				// fazer whatever tem q fazer e setar o pedido para E //TODO
				// pagamento acho
				// String tempoentrega = hora_entrega + ":" + min_entrega;

				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {

					m_tempo_entrega_inp = "00:00";

				} else {
					Date datatempoentregateste = Utilitario.testeHora("HH:mm", m_tempo_entrega_inp, "Tempo de entrega inválido!");// tempo de entrega da distri
					Date datatempoentregateste2 = Utilitario.testeHora("HH:mm", rs.getString("TEMPO_ESTIMADO_DESEJADO"), "");
					;// tempo de entrega do usuario

					if (datatempoentregateste.after(datatempoentregateste2)) {// ;/
						throw new Exception("Tempo de entrega é acima do desejado!");
					}
				}

				sql = "update  pedido  set flag_status = 'E', `TEMPO_ESTIMADO_ENTREGA` =  ? , `DATA_PEDIDO_RESPOSTA` = NOW()  where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A' ";
				st = conn.prepareStatement(sql);
				st.setString(1, m_tempo_entrega_inp);
				st.setInt(2, coddistr);
				st.setInt(3, Integer.parseInt(id_pedido));
				st.executeUpdate();

				objRetorno.put("msg", "ok");

			} else if (resposta.equalsIgnoreCase("R")) {

				JSONArray motivos = (JSONArray) new JSONParser().parse(motivos_json);

				if (motivos.size() == 0) {
					throw new Exception("Você deve escolher pelo menos um motivo para recusar o pedido.");
				}

				sql = "update  pedido  set flag_status = 'R', DATA_PEDIDO_RESPOSTA = NOW() where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A' ";
				st = conn.prepareStatement(sql);
				st.setInt(1, coddistr);
				st.setInt(2, Integer.parseInt(id_pedido));
				st.executeUpdate();

				int codmotivo = 0;
				for (int i = 0; i < motivos.size(); i++) {

					try {
						codmotivo = Integer.parseInt(motivos.get(i).toString());
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

			} else {
				throw new Exception("Resposta inválida, contate o suporte.");
			}
		}

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

}
