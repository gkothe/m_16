package com.funcs;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.configs.MobileController;

public class Pedidos_ajax {

	// public static int horas_fim_pedido = 6;

	public static DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
	public static NumberFormat df = new DecimalFormat("###,###.#", dfs);
	public static NumberFormat df2 = new DecimalFormat("#,###,##0.00", dfs);

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

	public static void InsertPedidoDistri(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();
		String prodsjson = request.getParameter("prodsjson") == null ? "" : request.getParameter("prodsjson"); //
		String mod_pagamento = request.getParameter("mod_pagamento") == null ? "" : request.getParameter("mod_pagamento"); //
		JSONArray prods = (JSONArray) new JSONParser().parse(prodsjson);

		JSONObject objRetorno = new JSONObject();
		String sql = " select * from distribuidora where ID_DISTRIBUIDORA = ? ";
		PreparedStatement st2 = conn.prepareStatement(sql);
		st2.setInt(1, coddistr);
		ResultSet rs2 = st2.executeQuery();
		long iduser = 0;
		if (rs2.next()) {

			iduser = rs2.getLong("ID_USUARIO_INSERT");

		} else {
			throw new Exception("Nenhum usuário cadastro para a loja.");
		}

		for (int i = 0; i < prods.size(); i++) {

			JSONObject obj = (JSONObject) prods.get(i);
			int idprod = Integer.parseInt(obj.get("id").toString());
			int qtd = Integer.parseInt(obj.get("qtd").toString());

			MobileController.addCarrinho(request, response, conn, iduser, getidProddistr(coddistr, idprod, conn) + "", qtd + "", "", "L", false);

		}

		JSONObject param = new JSONObject();

		param.put("tipo_pagamento", mod_pagamento);
		param.put("desc_endereco", "");
		param.put("desc_endereco_num", "");
		param.put("desc_endereco_complemento", "");
		param.put("tempomax", "");
		param.put("choiceserv", "L");
		param.put("trocopara", "");
		param.put("modoentrega", "");
		param.put("dataagendamento", "");
		param.put("dataagendamentohora", "");
		param.put("bairro", "");

		param = MobileController.criarPedido(request, response, conn, iduser, param, false);

		param.put("motivos_json", "");
		param.put("resposta", "A");
		param.put("m_tempo_entrega_inp", "");
		param.put("prodsrecusadosjson", "");
		param.put("flag_usartempomax", "");

		responderPedido(request, response, conn, coddistr, param, false);

		finalizandoPedido(request, response, conn, coddistr, param.get("id_pedido").toString(), false, true);

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	public static long getidProddistr(int coddistr, int idprod, Connection conn) throws Exception {

		JSONObject objRetorno = new JSONObject();
		String sql = " select * from produtos_distribuidora where ID_DISTRIBUIDORA = ? and ID_PROD = ? ";
		PreparedStatement st2 = conn.prepareStatement(sql);
		st2.setInt(1, coddistr);
		st2.setInt(2, idprod);
		ResultSet rs2 = st2.executeQuery();

		if (rs2.next()) {

			return rs2.getLong("ID_PROD_DIST");

		} else {
			throw new Exception("Produto inválido");
		}

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

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT *, ");
		sql.append("       Addtime(COALESCE(data_agenda_entrega, data_pedido), tempo_estimado_desejado)                                                  AS tempoteste, ");
		sql.append("       Timestampdiff(second, Now(), Addtime (COALESCE(data_agenda_entrega, data_pedido), tempo_estimado_desejado))                   AS secs ");
		sql.append("FROM   pedido ");
		sql.append("       LEFT JOIN bairros ");
		sql.append("              ON bairros.cod_bairro = pedido.cod_bairro ");
		sql.append("       LEFT JOIN pedido_motivo_cancelamento ");
		sql.append("              ON pedido_motivo_cancelamento.id_pedido = pedido.id_pedido ");
		sql.append("WHERE  id_distribuidora = ? ");
		sql.append("       AND ( flag_status = 'A' ");
		sql.append("              OR flag_status = 'E' ");
		sql.append("              OR ( flag_status = 'C' ");
		sql.append("                   AND flag_confirmado_distribuidora = 'N' ) )");

		if (!num_pedido_aberto.equalsIgnoreCase("")) {
			sql.append(" and NUM_PED  = ? ");
			temfiltro = "S";
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql.append(" and flag_status  = ? ");
			temfiltro = "S";
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql.append(" and id_pedido in (select id_pedido from pedido_item where id_prod = ? ) ");
			temfiltro = "S";
		}

		if (!cod_bairro_aberto.equalsIgnoreCase("")) {
			sql.append(" and pedido. COD_BAIRRO = ? ");
			temfiltro = "S";
		}

		if (!(data_pedido_ini.equalsIgnoreCase("")) && data_pedido_ini_hora != null) {
			sql.append("  and  data_pedido >= ?");
			temfiltro = "S";
		}

		if (!(data_pedido_fim.equalsIgnoreCase("")) && data_pedido_fim_hora != null) {
			sql.append("  and  data_pedido <= ?");
			temfiltro = "S";
		}

		if (!val_ini_aberto.equalsIgnoreCase("")) {
			sql.append("  and  VAL_TOTALPROD >= ? ");
			temfiltro = "S";
		}

		if (!val_fim_aberto.equalsIgnoreCase("")) {
			sql.append("  and  VAL_TOTALPROD <= ? ");
			temfiltro = "S";
		}

		if (!flag_visu.equalsIgnoreCase("")) {
			if (flag_visu.equalsIgnoreCase("N"))
				sql.append("  and  (flag_vizualizado = ? or FLAG_VIZUALIZADO_CANC = ? ) ");
			else if (flag_visu.equalsIgnoreCase("S")) {
				sql.append(" and flag_vizualizado = ? and Coalesce(FLAG_VIZUALIZADO_CANC,'S') = ? ");
			}
			temfiltro = "S";
		}

		if (flag_pedido_ret_entre.equalsIgnoreCase("A")) {

			sql.append("  and  flag_pedido_ret_entre = 'T' and flag_modoentrega = 'A' ");
			temfiltro = "S";
		} else if (flag_pedido_ret_entre.equalsIgnoreCase("T")) {

			sql.append("  and  flag_pedido_ret_entre = 'T' and flag_modoentrega = 'T' ");
			temfiltro = "S";
		} else if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			sql.append("  and  flag_pedido_ret_entre = ? ");
			temfiltro = "S";
		}

		sql.append("  order by flag_status asc , data_pedido asc ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
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

		if (!flag_visu.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_visu));
			contparam++;
		}
		if (flag_pedido_ret_entre.equalsIgnoreCase("A") || flag_pedido_ret_entre.equalsIgnoreCase("T")) {
			// faz nada
		} else if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_pedido_ret_entre));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		PreparedStatement st2;
		ResultSet rs2;
		String sql2;
		while (rs.next()) {
			JSONObject objRetorno = new JSONObject();
			sql2 = " SELECT cONCAT('',QTD_PROD ,' x ', DESC_ABREVIADO) AS DESCPROD FROM PEDIDO_ITEM INNER JOIN produtos ON produtos.ID_PROD  =  PEDIDO_ITEM.ID_PROD AND ID_PEDIDO = ?  ";
			st2 = conn.prepareStatement(sql2);
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
			
			
			
			
			if (rs.getTimestamp("DATA_AGENDA_ENTREGA") != null) {
				objRetorno.put("data_agenda_entrega", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_AGENDA_ENTREGA")));
			} else {

			}
			objRetorno.put("flag_modoentrega", rs.getString("flag_modoentrega") == null ? "" : rs.getString("flag_modoentrega"));

			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
				objRetorno.put("DESC_BAIRRO", "Retirar no local");
				objRetorno.put("FLAG_STATUS", rs.getString("FLAG_STATUS").equalsIgnoreCase("E") ? "S" : rs.getString("FLAG_STATUS"));
			} else {

				if (rs.getString("flag_modoentrega").equalsIgnoreCase("A")) {
					objRetorno.put("DESC_BAIRRO", rs.getString("DESC_BAIRRO") + " - Agendamento");
				} else {
					objRetorno.put("DESC_BAIRRO", rs.getString("DESC_BAIRRO"));
				}
			}

			objRetorno.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));

			objRetorno.put("ID_PEDIDO", rs.getString("ID_PEDIDO"));

			objRetorno.put("atrasado", false);
			if (rs.getString("flag_resposta_usuario") != null && rs.getString("flag_resposta_usuario").equalsIgnoreCase("N") && rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T")) {

				Calendar data6 = Calendar.getInstance();
				data6.setTime(rs.getTimestamp("tempoteste"));
				if (data6.getTime().before(new Date())) {
					objRetorno.put("atrasado", true);
				}
			}

			if (rs.getString("FLAG_VIZUALIZADO_CANC") != null && !rs.getString("FLAG_VIZUALIZADO_CANC").equalsIgnoreCase("")) {
				if (rs.getString("FLAG_VIZUALIZADO_CANC").equalsIgnoreCase("N")) {
					// retorno.put("canc_vizu", true);
				}
				objRetorno.put("flag_visu", rs.getString("FLAG_VIZUALIZADO_CANC"));
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

		JSONObject ret = new JSONObject();
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

		String pag = request.getParameter("pag") == null ? "" : request.getParameter("pag");
		String size = request.getParameter("size") == null ? "" : request.getParameter("size");

		String name = request.getParameter("sort") == null ? "" : request.getParameter("sort");
		String order = request.getParameter("order") == null ? "" : request.getParameter("order");

		String sql = "select * from pedido LEFT join bairros on bairros.cod_bairro = pedido.cod_bairro left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido where ID_DISTRIBUIDORA = ? and (flag_status = 'O' or flag_status = 'R' or (flag_status = 'C' and FLAG_CONFIRMADO_DISTRIBUIDORA = 'S' )) ";
		String sql2 = " select count(*) as total from pedido LEFT join bairros on bairros.cod_bairro = pedido.cod_bairro left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido where ID_DISTRIBUIDORA = ? and (flag_status = 'O' or flag_status = 'R' or (flag_status = 'C' and FLAG_CONFIRMADO_DISTRIBUIDORA = 'S' ))";
		if (!num_pedido_historico.equalsIgnoreCase("")) {
			sql = sql + " and NUM_PED  = ? ";
			sql2 = sql2 + " and NUM_PED  = ? ";
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql = sql + " and flag_status  = ? ";
			sql2 = sql2 + " and flag_status  = ? ";
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql = sql + " and id_pedido in (select id_pedido from pedido_item where id_prod = ? ) ";
			sql2 = sql2 + " and id_pedido in (select id_pedido from pedido_item where id_prod = ? ) ";
		}

		if (!cod_bairro_historico.equalsIgnoreCase("")) {
			sql = sql + " and pedido. COD_BAIRRO = ? ";
			sql2 = sql2 + " and pedido. COD_BAIRRO = ? ";
		}

		if (!(data_pedido_ini.equalsIgnoreCase(""))) {
			sql = sql + "  and  data_pedido >= ?";
			sql2 = sql2 + "  and  data_pedido >= ?";
		}

		if (!(data_pedido_fim.equalsIgnoreCase(""))) {
			sql = sql + "  and  data_pedido <= ?";
			sql2 = sql2 + "  and  data_pedido <= ?";
		}

		if (!(data_reposta_ini.equalsIgnoreCase(""))) {
			sql = sql + "  and  DATA_PEDIDO_RESPOSTA >= ?";
			sql2 = sql2 + "  and  DATA_PEDIDO_RESPOSTA >= ?";
		}

		if (!(data_reposta_fim.equalsIgnoreCase(""))) {
			sql = sql + "  and  DATA_PEDIDO_RESPOSTA <= ?";
			sql2 = sql2 + "  and  DATA_PEDIDO_RESPOSTA <= ?";
		}

		if (!val_ini_historico.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD >= ? ";
			sql2 = sql2 + "  and  VAL_TOTALPROD >= ? ";
		}

		if (!val_fim_historico.equalsIgnoreCase("")) {
			sql = sql + "  and  VAL_TOTALPROD <= ? ";
			sql2 = sql2 + "  and  VAL_TOTALPROD <= ? ";
		}

		if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			sql = sql + "  and  flag_pedido_ret_entre = ? ";
			sql2 = sql2 + "  and  flag_pedido_ret_entre = ? ";
		}

		try {
			Integer.parseInt(pag);
			Integer.parseInt(size);
		} catch (Exception e) {
			throw new Exception("Parâmetros de paginação inválidos.");
		}

		if (!order.equalsIgnoreCase("desc") && !order.equalsIgnoreCase("asc")) {
			throw new Exception("Parâmetros de paginação inválidos.");
		}

		if (name.equalsIgnoreCase("num_ped")) {
			sql = sql + "  ORDER BY num_ped " + order;
		} else if (name.equalsIgnoreCase("data_formatada")) {
			sql = sql + "  ORDER BY DATA_PEDIDO " + order;
		} else if (name.equalsIgnoreCase("data_formatada_resposta")) {
			sql = sql + "  ORDER BY DATA_PEDIDO_RESPOSTA " + order;
		} else if (name.equalsIgnoreCase("DESC_BAIRRO")) {
			sql = sql + "  ORDER BY DESC_BAIRRO " + order;
		} else if (name.equalsIgnoreCase("VAL_TOTALPROD")) {
			sql = sql + "  ORDER BY VAL_TOTALPROD " + order;
		} else if (name.equalsIgnoreCase("FLAG_STATUS")) {
			sql = sql + "  ORDER BY FLAG_STATUS " + order;
		} else {
			sql = sql + "  ORDER BY num_ped " + order;
		}

		sql = sql + "  limit " + size + " OFFSET " + (Integer.parseInt(size) * (Integer.parseInt(pag) - 1)) + " ";

		PreparedStatement st = conn.prepareStatement(sql);
		PreparedStatement st2 = conn.prepareStatement(sql2);
		st.setInt(1, coddistr);
		st2.setInt(1, coddistr);

		int contparam = 2;
		if (!num_pedido_historico.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(num_pedido_historico));
			st2.setInt(contparam, Integer.parseInt(num_pedido_historico));
			contparam++;
		}

		if (!flag_situacao.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_situacao));
			st2.setString(contparam, (flag_situacao));
			contparam++;
		}

		if (!id_produto.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_produto));
			st2.setInt(contparam, Integer.parseInt(id_produto));
			contparam++;
		}

		if (!cod_bairro_historico.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(cod_bairro_historico));
			st2.setInt(contparam, Integer.parseInt(cod_bairro_historico));
			contparam++;
		}

		if (!(data_pedido_ini.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_pedido_ini + " 00:00");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			st2.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_pedido_fim.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(data_pedido_fim + " 23:59:59");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data));
			st2.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data));
			contparam++;
		}

		if (!(data_reposta_ini.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_reposta_ini + " 00:00");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			st2.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_reposta_fim.equalsIgnoreCase(""))) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data_reposta_fim + " 23:59:59");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data));
			st2.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data));
			contparam++;
		}

		if (!val_ini_historico.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_ini_historico));
			st2.setDouble(contparam, Double.parseDouble(val_ini_historico));
			contparam++;
		}

		if (!val_fim_historico.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_fim_historico));
			st2.setDouble(contparam, Double.parseDouble(val_fim_historico));
			contparam++;
		}

		if (!flag_pedido_ret_entre.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_pedido_ret_entre));
			st2.setString(contparam, (flag_pedido_ret_entre));
			contparam++;
		}

		ResultSet rs = st2.executeQuery();
		if (rs.next()) {
			ret.put("total", rs.getLong("total"));
		}

		rs = st.executeQuery();
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
			if (rs.getTimestamp("DATA_PEDIDO_RESPOSTA") != null)
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

		ret.put("rows", pedidos);

		out.print(ret.toJSONString());

	}

	public static void carregaPedido_fechado(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //

		// String sql = "select * from pedido left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido left join bairros on bairros.cod_bairro = pedido.cod_bairro where ID_DISTRIBUIDORA = ? and pedido.id_pedido = ? and (flag_status = 'O' or flag_status = 'R' or (flag_status = 'C' and FLAG_CONFIRMADO_DISTRIBUIDORA = 'S' ) ) ";

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * ");
		sql.append("FROM pedido ");
		sql.append("  LEFT JOIN pedido_motivo_cancelamento ON pedido_motivo_cancelamento.id_pedido = pedido.id_pedido ");
		sql.append("  LEFT JOIN bairros ON bairros.cod_bairro = pedido.cod_bairro ");
		sql.append("  left join motivos_cancelamento ON motivos_cancelamento.cod_motivo = pedido_motivo_cancelamento.cod_motivo ");
		sql.append("WHERE ID_DISTRIBUIDORA = ? ");
		sql.append("AND   pedido.id_pedido = ? ");
		sql.append("AND   (flag_status = 'O' OR flag_status = 'R' OR (flag_status = 'C' AND FLAG_CONFIRMADO_DISTRIBUIDORA = 'S'))");

		PreparedStatement st = conn.prepareStatement(sql.toString());
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
			objRetorno.put("num_ped", rs.getString("num_ped"));
			objRetorno.put("FLAG_MODOPAGAMENTO", Utilitario.returnModoPagamento(rs.getString("FLAG_MODOPAGAMENTO")));

			if (rs.getDouble("NUM_TROCOPARA") != 0.0) {
				objRetorno.put("num_trocopara", "R$ " + df2.format(rs.getDouble("NUM_TROCOPARA")));
			}
			if (rs.getTimestamp("DATA_AGENDA_ENTREGA") != null) {
				objRetorno.put("data_agenda_entrega", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_AGENDA_ENTREGA")));
			} else {

			}
			objRetorno.put("flag_modoentrega", rs.getString("flag_modoentrega") == null ? "" : rs.getString("flag_modoentrega"));

			int user = rs.getInt("id_usuario");
			String status = rs.getString("FLAG_STATUS");

			objRetorno.put("flag_status", status);
			sql = new StringBuffer();
			sql.append(" 	SELECT *, VAL_UNIT * QTD_PROD as VAL_TOTAL FROM PEDIDO_ITEM INNER JOIN produtos ON produtos.ID_PROD  =  PEDIDO_ITEM.ID_PROD AND ID_PEDIDO = ? ");

			PreparedStatement st2 = conn.prepareStatement(sql.toString());
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
				obj.put("FLAG_RECUSADO", rs2.getString("FLAG_RECUSADO"));
				obj.put("RECUSADO_DISPONIVEL", rs2.getString("RECUSADO_DISPONIVEL"));

				prods.add(obj);

			}

			objRetorno.put("prods", prods);

			if (status.equalsIgnoreCase("O") || status.equalsIgnoreCase("C")) {

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

				// objRetorno.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));

				objRetorno.put("m_tempo_entrega", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));
				objRetorno.put("m_data_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));
				if (status.equalsIgnoreCase("C")) {
					objRetorno.put("DATA_CANCELAMENTO", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_CANCELAMENTO")));
					objRetorno.put("DESC_MOTIVO", rs.getString("desc_motivo"));
					objRetorno.put("DESC_OBS", rs.getString("desc_obs"));
				}
			}
			if (status.equalsIgnoreCase("R")) {

				st2 = conn.prepareStatement(" SELECT * from pedido_motivos_recusa inner join motivos_recusa on motivos_recusa.cod_motivo = pedido_motivos_recusa.cod_motivo  where ID_PEDIDO =  ?");
				st2.setInt(1, Integer.parseInt(id_pedido));
				rs2 = st2.executeQuery();
				JSONArray motivos = new JSONArray();
				while (rs2.next()) {
					JSONObject obj = new JSONObject();
					obj.put("desc_motivo", rs2.getString("desc_motivo"));
					obj.put("cod_motivo", rs2.getString("cod_motivo"));

					motivos.add(obj);
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
		varname1.append("SELECT *, ");
		varname1.append("       Addtime(COALESCE(data_agenda_entrega, data_pedido), tempo_estimado_desejado)                                                  AS tempoteste, ");
		varname1.append("       Timestampdiff(second, Now(), Addtime (COALESCE(data_agenda_entrega, data_pedido), tempo_estimado_desejado))                   AS secs, ");
		varname1.append("       case when flag_modoentrega = 'T' then SEC_TO_TIME( abs(TIMESTAMPDIFF(second,NOW(),Addtime (COALESCE(data_agenda_entrega,data_pedido),tempo_estimado_desejado)))) else '00:00:00' end as timedif, "); // tempo para entrega antes de responder
		varname1.append(" tIMESTAMPDIFF(SECOND,Addtime(DATA_PEDIDO_RESPOSTA,TEMPO_ESTIMADO_ENTREGA),now() ) as sec2, ");// diferença em segs para campo abaixo
		varname1.append(" SEC_TO_TIME( abs(tIMESTAMPDIFF(SECOND,Addtime(DATA_PEDIDO_RESPOSTA,TEMPO_ESTIMADO_ENTREGA),now() ))) as temprest "); // (data_resposta + tempo_entrega) - now() ; tempo restante para entrega do pedido a partir da reposta da loja (TEMPO_ESTIMADO_ENTREGA) dps de respondido

		varname1.append(" FROM   pedido ");
		varname1.append("       left JOIN bairros ");
		varname1.append("               ON bairros.cod_bairro = pedido.cod_bairro ");
		varname1.append("       LEFT JOIN pedido_motivo_cancelamento ");
		varname1.append("              ON pedido_motivo_cancelamento.id_pedido = pedido.id_pedido ");
		varname1.append("  left join motivos_cancelamento ON motivos_cancelamento.cod_motivo = pedido_motivo_cancelamento.cod_motivo ");
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

				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T") && rs.getString("flag_modoentrega").equalsIgnoreCase("T")) {
					if (rs.getInt("secs") > 0) {
						objRetorno.put("m_tempo_max", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("timedif")));
					} else {
						objRetorno.put("m_tempo_max", "Expirado!");
					}

					if (rs.getInt("sec2") != 0 && rs.getTimestamp("data_pedido_resposta") != null) {
						if (rs.getInt("sec2") > 0) {
							objRetorno.put("m_tempo_max", "Expirado!");
						} else {
							objRetorno.put("m_tempo_max", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("temprest")));
						}

					}
				} else {
					objRetorno.put("m_tempo_max", "");
				}
				objRetorno.put("desc_bairro", rs.getString("DESC_BAIRRO"));
			}

			objRetorno.put("data_pedido", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO")));
			objRetorno.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			objRetorno.put("VAL_ENTREGA", rs.getString("VAL_ENTREGA"));
			objRetorno.put("num_ped", rs.getString("num_ped"));
			objRetorno.put("ID_PEDIDO", rs.getString("ID_PEDIDO"));
			objRetorno.put("FLAG_MODOPAGAMENTO", Utilitario.returnModoPagamento(rs.getString("FLAG_MODOPAGAMENTO")));
			objRetorno.put("m_observ", rs.getString("DESC_OBSERVACAO"));
			if (rs.getTimestamp("DATA_AGENDA_ENTREGA") != null) {
				objRetorno.put("data_agenda_entrega", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_AGENDA_ENTREGA")));
			} else {

			}
			objRetorno.put("flag_modoentrega", rs.getString("flag_modoentrega") == null ? "" : rs.getString("flag_modoentrega"));

			int user = rs.getInt("id_usuario");
			String status = rs.getString("FLAG_STATUS");

			objRetorno.put("flag_status", status);

			sql = " 	SELECT *, VAL_UNIT * QTD_PROD as VAL_TOTAL FROM PEDIDO_ITEM INNER JOIN produtos ON produtos.ID_PROD  =  PEDIDO_ITEM.ID_PROD AND ID_PEDIDO = ? order by DESC_PROD";

			PreparedStatement st2 = conn.prepareStatement(sql);
			st2.setInt(1, Integer.parseInt(id_pedido));
			ResultSet rs2 = st2.executeQuery();
			JSONArray prods = new JSONArray();

			while (rs2.next()) {
				JSONObject obj = new JSONObject();

				obj.put("SEQ_ITEM", rs2.getInt("SEQ_ITEM"));
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

			objRetorno.put("DESC_NOME", rs.getString("NOME_PESSOA"));
			objRetorno.put("DESC_TELEFONE", rs.getString("NUM_TELEFONECONTATO_CLIENTE"));
			String end = rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega");
			String num = rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega");
			String compl = rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega");

			objRetorno.put("DESC_ENDERECO", end + " " + num + " " + compl);
			if (status.equalsIgnoreCase("E") || status.equalsIgnoreCase("C")) {
				if (rs.getDouble("NUM_TROCOPARA") != 0.0) {
					objRetorno.put("num_trocopara", "R$ " + df2.format(rs.getDouble("NUM_TROCOPARA")));
				}

				objRetorno.put("tipo_servico", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
					objRetorno.put("desc_bairro", "Retirar no local");
				} else {

					objRetorno.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));
				}

				// objRetorno.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));
				
				objRetorno.put("m_tempo_entrega", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));
				objRetorno.put("m_data_resposta", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));

				Calendar data6 = Calendar.getInstance();

				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
					objRetorno.put("darok", true);
				} else {
					if (rs.getTimestamp("DATA_AGENDA_ENTREGA") != null) {
						data6.setTime(rs.getTimestamp("DATA_AGENDA_ENTREGA"));
					} else {
						data6.setTime(rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
					}
					data6.add(Calendar.HOUR_OF_DAY, sys.getPED_HORASOKEY());

					if (data6.getTime().before(new Date())) {
						objRetorno.put("darok", true);

					}
				}

			}

			if (status.equalsIgnoreCase("C")) {
				objRetorno.put("m_tempo_max", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("tempo_estimado_desejado")));
				sql = " update  pedido_motivo_cancelamento  set FLAG_VIZUALIZADO_CANC = 'S' where id_pedido = ?  ";
				st = conn.prepareStatement(sql);
				st.setInt(1, Integer.parseInt(id_pedido));
				st.executeUpdate();

				objRetorno.put("DATA_CANCELAMENTO", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_CANCELAMENTO")));
				objRetorno.put("DESC_MOTIVO", rs.getString("desc_motivo"));
				objRetorno.put("DESC_OBS", rs.getString("desc_obs"));

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

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido"); //
		finalizandoPedido(request, response, conn, coddistr, id_pedido, true, false);

	}

	public static void finalizandoPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr, String id_pedido, boolean outprint, boolean avoidteste) throws Exception {
		Sys_parametros sys = new Sys_parametros(conn);
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String sql = " select * from  pedido  left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido  where pedido.id_pedido = ? and ID_DISTRIBUIDORA = ? and (flag_status = 'E'  or (flag_status = 'C' and FLAG_CONFIRMADO_DISTRIBUIDORA = 'N' ) ) ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_pedido));
		st.setInt(2, coddistr);
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Pedido inválido! Entre em contato com o suporte");
		} else {

			Calendar data6 = Calendar.getInstance();
			if (rs.getTimestamp("DATA_AGENDA_ENTREGA") != null) {
				data6.setTime(rs.getTimestamp("DATA_AGENDA_ENTREGA"));
			} else {
				data6.setTime(rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
			}

			data6.add(Calendar.HOUR_OF_DAY, sys.getPED_HORASOKEY());

			if (rs.getString("flag_status").equalsIgnoreCase("C")) {

				sql = " update  pedido_motivo_cancelamento  set FLAG_CONFIRMADO_DISTRIBUIDORA = 'S' where id_pedido = ?  ";
				st = conn.prepareStatement(sql);
				st.setInt(1, Integer.parseInt(id_pedido));
				st.executeUpdate();

			} else {

				if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T")) {
					if (!avoidteste)
						if (data6.getTime().after(new Date())) {
							throw new Exception("A hora atual deve exceder em " + sys.getPED_HORASOKEY() + "h a data de resposta para o pedido ser finalizado manualmente.");
						}
				}

				sql = " update  pedido  set flag_status = 'O' where id_pedido = ? and ID_DISTRIBUIDORA = ?  ";
				st = conn.prepareStatement(sql);
				st.setInt(1, Integer.parseInt(id_pedido));
				st.setInt(2, coddistr);
				st.executeUpdate();

			}

		}

		objRetorno.put("msg", "ok");
		if (outprint)
			out.print(objRetorno.toJSONString());

	}

	public static void finalizandoPedidoMobile(HttpServletRequest request, HttpServletResponse response, Connection conn, long idusario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido"); //

		String sql = " select * from  pedido  left join pedido_motivo_cancelamento on pedido_motivo_cancelamento.id_pedido = pedido.id_pedido  where pedido.id_pedido = ? and ID_USUARIO = ? and (flag_status = 'E' or flag_status = 'S'  ) ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_pedido));
		st.setLong(2, idusario);
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Pedido inválido! Entre em contato com o suporte");
		} else {

			sql = " update  pedido  set flag_status = 'O' , flag_resposta_usuario = 'S'  where id_pedido = ? and ID_USUARIO = ?  ";
			st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(id_pedido));
			st.setLong(2, idusario);
			st.executeUpdate();

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	public static void responderPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //
		String motivos_json = request.getParameter("motivos_json") == null ? "" : request.getParameter("motivos_json");
		String resposta = request.getParameter("resposta") == null ? "" : request.getParameter("resposta");
		String m_tempo_entrega_inp = request.getParameter("m_tempo_entrega_inp") == null ? "" : request.getParameter("m_tempo_entrega_inp");
		String prodsrecusadosjson = request.getParameter("prodsrecusadosjson") == null ? "" : request.getParameter("prodsrecusadosjson");
		String flag_usartempomax = request.getParameter("flag_usartempomax") == null ? "N" : request.getParameter("flag_usartempomax");

		JSONObject param = new JSONObject();

		param.put("id_pedido", id_pedido);
		param.put("motivos_json", motivos_json);
		param.put("resposta", resposta);
		param.put("m_tempo_entrega_inp", m_tempo_entrega_inp);
		param.put("prodsrecusadosjson", prodsrecusadosjson);
		param.put("flag_usartempomax", flag_usartempomax);

		responderPedido(request, response, conn, coddistr, param, true);
	}

	public static void responderPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr, JSONObject param, boolean outprint) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		Sys_parametros sys = new Sys_parametros(conn);

		String id_pedido = param.get("id_pedido").toString();
		String motivos_json = param.get("motivos_json").toString();
		String resposta = param.get("resposta").toString();
		String m_tempo_entrega_inp = param.get("m_tempo_entrega_inp").toString();
		String prodsrecusadosjson = param.get("prodsrecusadosjson").toString();
		String flag_usartempomax = param.get("flag_usartempomax").toString();

		/*
		 * if (hora_entrega.equalsIgnoreCase("")) { hora_entrega = "0"; }
		 * 
		 * if (min_entrega.equalsIgnoreCase("")) { min_entrega = "0"; }
		 */

		String sql = " ";

		StringBuffer varname1 = new StringBuffer();
		varname1.append("SELECT *, ");
		varname1.append("       Timestampdiff(second, Now(), Addtime (COALESCE(data_agenda_entrega, data_pedido), tempo_estimado_desejado))                   AS secs, ");
		varname1.append("       case when flag_modoentrega = 'T' then SEC_TO_TIME( abs(TIMESTAMPDIFF(second,NOW(),Addtime (COALESCE(data_agenda_entrega,data_pedido),tempo_estimado_desejado)))) else '00:00:00' end as timedif ");
		varname1.append("FROM   pedido ");
		varname1.append("       INNER JOIN usuario ");
		varname1.append("               ON usuario.id_usuario = pedido.id_usuario ");
		varname1.append("WHERE  id_distribuidora = ? ");
		varname1.append("       AND id_pedido = ? ");
		varname1.append("       AND flag_status = 'A'");

		PreparedStatement st = conn.prepareStatement(varname1.toString());
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

			if (status.equalsIgnoreCase("E") || status.equalsIgnoreCase("R") || status.equalsIgnoreCase("S")) {
				throw new Exception("Pedido já respondido");
			}
			if (status.equalsIgnoreCase("C")) {
				throw new Exception("Este pedido foi cancelado.");
			}
			JSONObject data = new JSONObject();
			data.put("id_ped", rs.getString("ID_PEDIDO"));
			data.put("num_ped", rs.getString("NUM_PED"));
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
					if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T") && rs.getString("flag_modoentrega").equalsIgnoreCase("T") && flag_usartempomax.equalsIgnoreCase("N")) {

						// Date datatempoentregateste = Utilitario.testeHora("HH:mm", m_tempo_entrega_inp, "Tempo de entrega inválido!");// tempo de entrega da distri
						// Date datatempoentregateste2 = Utilitario.testeHora("HH:mm", rs.getString("TEMPO_ESTIMADO_DESEJADO"), "");
						// if (datatempoentregateste.after(datatempoentregateste2) && rs.getString("flag_modoentrega").equalsIgnoreCase("T")) {// ;/
						// throw new Exception("Tempo de entrega é acima do desejado!");
						// }

						Date datatempoentregateste = Utilitario.testeHora("HH:mm", m_tempo_entrega_inp, "Tempo de entrega inválido!");// tempo de entrega da distri
						Date datatempoentregateste2 = Utilitario.testeHora("HH:mm", rs.getString("timedif"), "");
						if ((datatempoentregateste.after(datatempoentregateste2)) && rs.getString("flag_modoentrega").equalsIgnoreCase("T")) {// ;/
							throw new Exception("Tempo de entrega é acima do desejado!");
						}

					} else if (flag_usartempomax.equalsIgnoreCase("S")) {
						m_tempo_entrega_inp = new SimpleDateFormat("HH:mm").format(rs.getTimestamp("timedif"));
					} else {
						m_tempo_entrega_inp = "00:00";
					}
				}

				if (!rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("L")) {
					try {
						if (rs.getLong("secs") < 0) {
							throw new Exception("Tempo de entrega expirou!");
						}
					} catch (Exception e) {
						throw new Exception("Tempo de entrega expirou!");
					}
				}

				sql = "update  pedido  set flag_status = 'E', `TEMPO_ESTIMADO_ENTREGA` =  ? , `DATA_PEDIDO_RESPOSTA` = NOW()  where ID_DISTRIBUIDORA = ? and id_pedido = ? and flag_status = 'A' ";
				st = conn.prepareStatement(sql);
				st.setString(1, m_tempo_entrega_inp);
				st.setInt(2, coddistr);
				st.setInt(3, Integer.parseInt(id_pedido));
				st.executeUpdate();

				Utilitario.oneSginal(sys, rs.getString("DESC_EMAIL"), "Seu pedido foi aceito!", data);

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
				boolean recusaestoque = false;
				for (int i = 0; i < motivos.size(); i++) {

					try {
						codmotivo = Integer.parseInt(motivos.get(i).toString());
					} catch (Exception e) {
						throw new Exception("Erro no processamento dos 'motivos'. Entre em contato com suporte. ");
					}

					if (codmotivo == sys.getCod_recusa_estoque()) {
						recusaestoque = true;
					}

					sql = "INSERT INTO pedido_motivos_recusa (`ID_PEDIDO`, `COD_MOTIVO`) VALUES (?, ?) ";
					st = conn.prepareStatement(sql);
					st.setInt(1, Integer.parseInt(id_pedido));
					st.setInt(2, codmotivo);
					st.executeUpdate();

				}

				if (recusaestoque) {
					int seqitem = 0;
					int qtd = 0;
					JSONArray prodsrecusados = (JSONArray) new JSONParser().parse(prodsrecusadosjson);
					if (prodsrecusados.size() == 0) {
						throw new Exception("Você escolheu recusar o pedido por falta de estoque. Por favor informe qual produto que está em falta. ");
					}
					PreparedStatement st2;
					ResultSet rs2;
					for (int i = 0; i < prodsrecusados.size(); i++) {
						JSONObject obj = (JSONObject) prodsrecusados.get(i);

						try {
							seqitem = Integer.parseInt(obj.get("seq").toString());

						} catch (Exception e) {
							throw new Exception("Erro no processamento dos 'produtos recusados'. Entre em contato com suporte. ");
						}

						try {

							qtd = Integer.parseInt(obj.get("qtd").toString());
						} catch (Exception e) {
							qtd = 0;
						}

						sql = " select * from pedido_item inner join produtos on pedido_item.id_prod = pedido_item.id_prod where SEQ_ITEM = ? and id_pedido = ?  ";

						st2 = conn.prepareStatement(sql);
						st2.setInt(1, (seqitem));
						st2.setInt(2, Integer.parseInt(id_pedido));
						rs2 = st2.executeQuery();
						if (!rs2.next()) {
							throw new Exception("Item do pedido inválido.");
						} else {

							if (rs2.getInt("QTD_PROD") <= qtd) {
								throw new Exception("A quantidade disponível informada (" + qtd + ") é maior/igual quantidade no pedido (" + rs2.getInt("QTD_PROD") + ") para o produto " + rs2.getString("DESC_PROD"));
							}

							sql = "UPDATE pedido_item set FLAG_RECUSADO = 'S' , RECUSADO_DISPONIVEL = ? where SEQ_ITEM = ? and id_pedido = ?  ";
							st = conn.prepareStatement(sql);
							st.setInt(1, qtd);
							st.setInt(2, (seqitem));
							st.setInt(3, Integer.parseInt(id_pedido));

							st.executeUpdate();
						}
					}

				}
				Utilitario.oneSginal(sys, rs.getString("DESC_EMAIL"), "Seu pedido foi recusado!", data);
				objRetorno.put("msg", "ok");

			} else {
				throw new Exception("Resposta inválida, contate o suporte.");
			}
		}

		objRetorno.put("msg", "ok");
		if (outprint)
			out.print(objRetorno.toJSONString());

	}

}
