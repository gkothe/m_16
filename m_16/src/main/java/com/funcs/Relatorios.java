package com.funcs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.configs.Conexao;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class Relatorios {

	public static DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
	public static NumberFormat df = new DecimalFormat("###,###.#", dfs);
	public static NumberFormat df2 = new DecimalFormat("#,###,##0.00", dfs);
	public static NumberFormat df3 = new DecimalFormat("#,###,##0.0", dfs);

	public static String parametrosDash(String sql, String hora_inicial, String hora_final, String dias_semana, String cod_bairro, String data_pedido_ini, String data_pedido_fim, String flag_servico) throws Exception {

		if (!hora_inicial.equalsIgnoreCase(""))
			Utilitario.testeHora("HH:mm", hora_inicial, "Tempo de entrega inválido!");// tempo de entrega da distri
		if (!hora_final.equalsIgnoreCase(""))
			Utilitario.testeHora("HH:mm", hora_final, "Tempo de entrega inválido!");

		if (!dias_semana.equalsIgnoreCase("") && !dias_semana.equalsIgnoreCase("null")) {
			String filtro = "";
			JSONArray dias = (JSONArray) new JSONParser().parse(dias_semana);

			if (dias != null)
				for (int i = 0; i < dias.size(); i++) {
					if (!Utilitario.isNumeric(dias.get(i).toString())) {
						throw new Exception("Dia inválido.");
					}
				}

			for (int i = 0; i < dias.size(); i++) {
				String day = (Integer.parseInt(dias.get(i).toString())) + "";// Returns the weekday index for date (1 = Monday, 2 = Tuesday, … 7 = Sunday). , monday no sistema começa em 1.
				filtro = filtro + "," + day;

			}
			filtro = filtro.replaceFirst(",", "");
			sql = sql + ("  and  DAYOFWEEK(DATA_PEDIDO) in (" + filtro + ")  ");
		}

		if (!cod_bairro.equalsIgnoreCase("")) {
			sql = sql + " and pedido. COD_BAIRRO = ? ";
		}

		if (!(data_pedido_ini.equalsIgnoreCase("")) && data_pedido_ini != null) {
			sql = sql + "  and  data_pedido >= ? ";
		}

		if (!(data_pedido_fim.equalsIgnoreCase("")) && data_pedido_fim != null) {
			sql = sql + "  and  data_pedido <= ? ";
		}

		if (!flag_servico.equalsIgnoreCase("")) {
			sql = sql + "  and  flag_pedido_ret_entre = ? ";
		}

		if (!hora_inicial.equalsIgnoreCase("")) {
			sql = sql + "  and   HOUR(DATA_PEDIDO) >= ?   ";
		}

		if (!hora_final.equalsIgnoreCase("")) {
			sql = sql + "  and   HOUR(DATA_PEDIDO) <= ?   ";
		}

		return sql;
	}

	public static PreparedStatement parametrosDashSt(PreparedStatement st, int contparam, String hora_inicial, String hora_final, String dias_semana, String cod_bairro, String data_pedido_ini, String data_pedido_fim, String flag_servico) throws Exception {

		if (!cod_bairro.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(cod_bairro));
			contparam++;
		}

		if (!(data_pedido_ini.equalsIgnoreCase("")) && data_pedido_ini != null) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(data_pedido_ini + " " + "00:00:00");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!(data_pedido_fim.equalsIgnoreCase("")) && data_pedido_fim != null) {

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(data_pedido_fim + " " + "23:59:59");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
			contparam++;
		}

		if (!flag_servico.equalsIgnoreCase("")) {
			st.setString(contparam, (flag_servico));
			contparam++;
		}

		if (!hora_inicial.equalsIgnoreCase("")) {
			st.setString(contparam, (hora_inicial));
			contparam++;
		}

		if (!hora_final.equalsIgnoreCase("")) {
			st.setString(contparam, (hora_final));
			contparam++;
		}

		return st;
	}

	public static void dashInfosBasicas(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String sql = "select count(id_pedido) as qtdped, sum(VAL_TOTALPROD) as val_total, sum(VAL_TOTALPROD)  / count(id_pedido)    as media from pedido   where id_distribuidora = ? and flag_status = 'O' ";

		sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		int contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			retorno.put("qtdped", df.format(rs.getDouble("qtdped")));
			retorno.put("val_total", "R$ " + df2.format(rs.getDouble("val_total")));
			retorno.put("media", "R$ " + df2.format(rs.getDouble("media")));
		} else {
			retorno.put("qtdped", df.format(0));
			retorno.put("val_total", "R$ " + df2.format(0));
			retorno.put("media", "R$ " + df2.format(0));
		}

		sql = "select TIME_FORMAT(SEC_TO_TIME(AVG(TIME_TO_SEC(TEMPO_ESTIMADO_DESEJADO))),'%H:%i:%s' ) as TEMPO_ESTIMADO_DESEJADO_MEDIO  , TIME_FORMAT(SEC_TO_TIME(AVG(TIME_TO_SEC(TEMPO_ESTIMADO_ENTREGA))),'%H:%i:%s' ) as TEMPO_ESTIMADO_ENTREGA_MEDIO   from pedido  where FLAG_PEDIDO_RET_ENTRE = 'T'  and id_distribuidora = ? and flag_status = 'O' ";

		sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		rs = st.executeQuery();
		if (rs.next()) {
			if (rs.getTimestamp("TEMPO_ESTIMADO_DESEJADO_MEDIO") != null)
				retorno.put("tempo_estimado_desejado_medio", new SimpleDateFormat("HH:mm:ss").format(rs.getTimestamp("TEMPO_ESTIMADO_DESEJADO_MEDIO")));
			else
				retorno.put("tempo_estimado_desejado_medio", "");
			if (rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA_MEDIO") != null)
				retorno.put("tempo_estimado_entrega_medio", new SimpleDateFormat("HH:mm:ss").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA_MEDIO")));
			else
				retorno.put("tempo_estimado_entrega_medio", "");

		} else {
			retorno.put("tempo_estimado_desejado_medio", "");
			retorno.put("tempo_estimado_entrega_medio", "");
		}

		sql = " Select TIMESTAMPDIFF(SECOND,DATA_PEDIDO,DATA_PEDIDO_RESPOSTA)  as secs from PEDIDO   where    id_distribuidora = ? and flag_status = 'O' ";

		sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		int secs = 0;
		int qtdpeds = 0;
		rs = st.executeQuery();
		while (rs.next()) {
			secs = secs + rs.getInt("secs");
			qtdpeds++;
		}
		if (qtdpeds != 0) {
			secs = secs / qtdpeds;

			int hours = secs / 3600;
			int minutes = (secs % 3600) / 60;
			int seconds = secs % 60;

			retorno.put("tempo_resposta_ped", String.format("%02d:%02d:%02d", hours, minutes, seconds));
		} else {
			retorno.put("tempo_resposta_ped", "");
		}
		out.print(retorno.toJSONString());
	}

	public static void dashServico(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String sql = "select distinct FLAG_PEDIDO_RET_ENTRE, count(FLAG_PEDIDO_RET_ENTRE) as qtd from pedido  where id_distribuidora = ? and flag_status = 'O' ";

		sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		sql = sql + " group by FLAG_PEDIDO_RET_ENTRE ;";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		int contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("desc", rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T") ? "Entrega" : "Retirada");
			obj.put("qtd", rs.getInt("qtd"));
			obj.put("qtddf", df.format(rs.getInt("qtd")));
			retorno.add(obj);
		}

		double total = 0;
		for (int i = 0; i < retorno.size(); i++) {
			JSONObject obj = (JSONObject) retorno.get(i);
			total = total + (Integer) obj.get("qtd");
		}

		for (int i = 0; i < retorno.size(); i++) {
			JSONObject obj = (JSONObject) retorno.get(i);
			int qtd = (Integer) obj.get("qtd");
			obj.put("perc", df3.format((100 * qtd) / total));

		}

		out.print(retorno.toJSONString());
	}

	public static void dashHoraPed(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		PreparedStatement st;
		ResultSet rs;
		ResultSet rs2;
		String sql;
		String hora;
		String hora2;
		sql = "SELECT  * from generator_256 where n between 0 and 23  ";
		st = conn.prepareStatement(sql);
		rs2 = st.executeQuery();
		while (rs2.next()) {

			sql = "SELECT  HOUR(DATA_PEDIDO) as hora , TIME_FORMAT(DATA_PEDIDO,'%H:00' ) as horaformated ,TIME_FORMAT(DATE_ADD(DATA_PEDIDO,interval 1 HOUR ),'%H:00' )  as HORA2, COUNT(*) as qtd, sum(VAL_TOTALPROD) as VAL_TOTALPROD FROM pedido where id_distribuidora = ?  and flag_status = 'O' and HOUR(DATA_PEDIDO) = " + rs2.getInt("n");

			sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

			sql = sql + " group by  HOUR(DATA_PEDIDO) ";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);
			int contparam = 2;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

			rs = st.executeQuery();
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("hora", rs.getString("hora"));
				obj.put("horaformated", rs.getString("horaformated") + "-" + rs.getString("HORA2"));
				obj.put("qtd", rs.getInt("qtd"));
				obj.put("val_totalprod", rs.getDouble("VAL_TOTALPROD"));
				retorno.add(obj);
			} else {
				JSONObject obj = new JSONObject();

				if (rs2.getInt("n") < 10) {
					hora = "0" + rs2.getInt("n") + ":00";
				} else {
					hora = rs2.getInt("n") + ":00";
				}

				if (rs2.getInt("n") + 1 < 10) {
					hora2 = "0" + (rs2.getInt("n") + 1) + ":00";
				} else {
					hora2 = rs2.getInt("n") + 1 + ":00";
				}
				obj.put("val_totalprod", 0);
				obj.put("hora", hora);
				obj.put("horaformated", hora + "-" + hora2);
				obj.put("qtd", "0");
				retorno.add(obj);

			}

		}

		out.print(retorno.toJSONString());
	}

	public static void dashDayPed(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		PreparedStatement st;
		ResultSet rs;
		ResultSet rs2;
		String sql;

		sql = "SELECT  * from dias_semana where COD_DIA != 8  ";
		st = conn.prepareStatement(sql);
		rs2 = st.executeQuery();
		while (rs2.next()) {

			sql = "SELECT DAYOFWEEK(DATA_PEDIDO) as dia, COUNT(*) as qtd,  sum(VAL_TOTALPROD) as VAL_TOTALPROD FROM pedido  where id_distribuidora = ?  and flag_status = 'O' and DAYOFWEEK(DATA_PEDIDO) =  " + rs2.getInt("cod_dia");

			sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

			sql = sql + "  GROUP BY DAYOFWEEK(DATA_PEDIDO) ";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);
			int contparam = 2;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

			rs = st.executeQuery();
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("dia", Utilitario.getDescDiaSemana(conn, rs.getInt("dia")));
				obj.put("qtd", rs.getInt("qtd"));
				obj.put("val_totalprod", rs.getDouble("VAL_TOTALPROD"));
				retorno.add(obj);
			} else {
				JSONObject obj = new JSONObject();
				obj.put("dia", Utilitario.getDescDiaSemana(conn, rs2.getInt("cod_dia")));
				obj.put("qtd", 0);
				obj.put("val_totalprod", 0);
				retorno.add(obj);
			}

		}

		out.print(retorno.toJSONString());
	}

	public static void dashListaBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");
		PreparedStatement st;
		ResultSet rs;
		ResultSet rs2;
		String sql;
		int contparam;
		StringBuffer sql2 = new StringBuffer();
		sql2.append("SELECT * from bairros where cod_cidade =  " + request.getSession(false).getAttribute("cod_cidade").toString() + " order by desc_bairro");
		st = conn.prepareStatement(sql2.toString());
		rs2 = st.executeQuery();
		while (rs2.next()) {

			sql = "select  count(id_pedido) as qtd,  COALESCE(desc_bairro, '* Distribuidora') AS desc_bairro ,sum(VAL_TOTALPROD) as valprod from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro  where  id_distribuidora = ?  and flag_status = 'O' and pedido.cod_bairro =  " + rs2.getInt("cod_bairro");
			// se quiser trazer os pedidos retirados em local, coloar left join no bairro.
			sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, "", data_pedido_ini, data_pedido_fim, flag_servico);

			sql = sql + "  group by pedido.cod_bairro order by   desc_bairro ";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);
			contparam = 2;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, "", data_pedido_ini, data_pedido_fim, flag_servico);

			rs = st.executeQuery();
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("desc", rs.getString("desc_bairro"));
				obj.put("qtd_f", df.format(rs.getInt("qtd")));
				obj.put("valtotal_f", df2.format(rs.getDouble("valprod")));
				obj.put("qtd", (rs.getInt("qtd")));
				obj.put("valtotal", (rs.getDouble("valprod")));
				retorno.add(obj);
			} else {
				JSONObject obj = new JSONObject();
				obj.put("desc", rs2.getString("desc_bairro"));
				obj.put("qtd_f", df.format(0));
				obj.put("valtotal_f", df2.format(0));
				obj.put("qtd", 0);
				obj.put("valtotal", (0));
				retorno.add(obj);
			}

		}

		out.print(retorno.toJSONString());
	}

	public static void dashProdBairro_single(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String id_prod = request.getParameter("id_prod") == null ? "" : request.getParameter("id_prod");

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT  sum(QTD_PROD) as qtd, ");
		sql.append("       COALESCE(pedido.cod_bairro, 0)           AS cod_bairro, ");
		sql.append("       COALESCE(desc_bairro, '* Distribuidora') AS desc_bairro, ");
		sql.append("       sum(QTD_PROD * Val_unit ) as valtotal ");
		sql.append("FROM   pedido ");
		sql.append("       INNER JOIN pedido_item ");
		sql.append("               ON pedido_item .id_pedido = pedido.id_pedido ");
		sql.append("       LEFT JOIN bairros ");
		sql.append("              ON bairros.cod_bairro = pedido.cod_bairro");
		sql.append("            where id_distribuidora = ?  and flag_status = 'O'  and id_prod = ? and bairros.cod_bairro is null");

		sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, "", data_pedido_ini, data_pedido_fim, flag_servico));

		sql.append(" group by cod_bairro order by desc_bairro");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_prod));
		int contparam = 3;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, "", data_pedido_ini, data_pedido_fim, flag_servico);
		ResultSet rs2;
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("desc", rs.getString("desc_bairro"));
			obj.put("qtd", rs.getInt("qtd"));
			obj.put("valtotal", rs.getDouble("valtotal"));
			retorno.add(obj);
		}
		sql = new StringBuffer();
		sql.append("SELECT * from bairros where cod_cidade =  " + request.getSession(false).getAttribute("cod_cidade").toString() + " order by desc_bairro");
		st = conn.prepareStatement(sql.toString());
		rs = st.executeQuery();
		while (rs.next()) {
			sql = new StringBuffer();

			sql.append("SELECT  sum(QTD_PROD) as qtd, ");
			sql.append("       COALESCE(pedido.cod_bairro, 0)           AS cod_bairro, ");
			sql.append("       COALESCE(desc_bairro, '* Distribuidora') AS desc_bairro, ");
			sql.append("       sum(QTD_PROD * Val_unit ) as valtotal ");
			sql.append("FROM   pedido ");
			sql.append("       INNER JOIN pedido_item ");
			sql.append("               ON pedido_item .id_pedido = pedido.id_pedido ");
			sql.append("       LEFT JOIN bairros ");
			sql.append("              ON bairros.cod_bairro = pedido.cod_bairro");
			sql.append("            where id_distribuidora = ?  and flag_status = 'O'  and id_prod = ? and bairros.cod_bairro = " + rs.getInt("cod_bairro"));

			sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, "", data_pedido_ini, data_pedido_fim, flag_servico));

			st = conn.prepareStatement(sql.toString());
			st.setInt(1, coddistr);
			st.setInt(2, Integer.parseInt(id_prod));
			contparam = 3;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, "", data_pedido_ini, data_pedido_fim, flag_servico);

			rs2 = st.executeQuery();
			while (rs2.next()) {
				JSONObject obj = new JSONObject();
				obj.put("desc", rs2.getString("desc_bairro"));
				obj.put("qtd", rs2.getInt("qtd"));
				obj.put("valtotal", rs2.getDouble("valtotal"));
				retorno.add(obj);
			}

		}

		out.print(retorno.toJSONString());
	}

	public static void dashProdInfosgerais_single(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONObject obj = new JSONObject();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String id_prod = request.getParameter("id_prod") == null ? "" : request.getParameter("id_prod");

		StringBuffer sql = new StringBuffer();
		sql.append(" select    sum(QTD_PROD) as qtd,sum(QTD_PROD)/7 as media, sum(QTD_PROD * Val_unit ) as totalfat,Val_unit  ");
		sql.append(" FROM   pedido ");
		sql.append("       INNER JOIN pedido_item ");
		sql.append("               ON pedido_item .id_pedido = pedido.id_pedido ");
		sql.append("            where id_distribuidora = ?  and flag_status = 'O'  and id_prod = ? ");

		sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico));

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_prod));
		int contparam = 3;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			// qtd,media,totalfat,Val_unit

			obj.put("qtd", rs.getInt("qtd"));
			obj.put("media", df2.format(rs.getInt("media")));
			obj.put("totalfat", "R$ " + df2.format(rs.getDouble("totalfat")));
			obj.put("valunit", df2.format(rs.getDouble("Val_unit")));

		}

		out.print(obj.toJSONString());
	}

	public static void dashProdDia_single(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String id_prod = request.getParameter("id_prod") == null ? "" : request.getParameter("id_prod");

		PreparedStatement st;
		ResultSet rs;
		ResultSet rs2;
		String sql2;
		StringBuffer sql = new StringBuffer();
		sql2 = "SELECT  * from dias_semana where COD_DIA != 8  ";
		st = conn.prepareStatement(sql2);
		rs2 = st.executeQuery();
		while (rs2.next()) {

			sql = new StringBuffer();
			sql.append(" select  DAYOFWEEK(DATA_PEDIDO) as dia,  sum(QTD_PROD) as qtd, sum(val_unit * qtd_prod) as VAL_TOTALPROD  from pedido");
			sql.append(" ");
			sql.append(" inner join pedido_item ");
			sql.append(" on pedido_item .id_pedido = pedido.id_pedido ");
			sql.append(" ");
			sql.append(" ");
			sql.append(" where id_distribuidora = ? and id_prod = ?  and flag_status = 'O'  and DAYOFWEEK(DATA_PEDIDO) =  " + rs2.getInt("cod_dia"));
			sql.append(" ");

			sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico));

			sql.append(" group by   DAYOFWEEK(DATA_PEDIDO)   order by  DAYOFWEEK(DATA_PEDIDO) ");

			st = conn.prepareStatement(sql.toString());
			st.setInt(1, coddistr);
			st.setInt(2, Integer.parseInt(id_prod));
			int contparam = 3;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

			rs = st.executeQuery();
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("dia", Utilitario.getDescDiaSemana(conn, rs.getInt("dia")));
				obj.put("qtd", df.format(rs.getInt("qtd")));
				obj.put("val_totalprod", (rs.getDouble("VAL_TOTALPROD")));
				retorno.add(obj);
			} else {
				JSONObject obj = new JSONObject();
				obj.put("dia", Utilitario.getDescDiaSemana(conn, rs2.getInt("cod_dia")));
				obj.put("qtd", 0);
				obj.put("val_totalprod", 0);
				retorno.add(obj);
			}
		}

		out.print(retorno.toJSONString());
	}

	public static void dashProdHora_single(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String id_prod = request.getParameter("id_prod") == null ? "" : request.getParameter("id_prod");

		PreparedStatement st;
		ResultSet rs;
		ResultSet rs2;
		String hora;
		String hora2;
		JSONObject obj;
		String sql2 = "SELECT  * from generator_256 where n between 0 and 23  ";
		st = conn.prepareStatement(sql2);
		StringBuffer sql = new StringBuffer();
		rs2 = st.executeQuery();
		while (rs2.next()) {

			sql = new StringBuffer();

			sql.append("select sum(val_unit * qtd_prod) as VAL_TOTALPROD , HOUR(DATA_PEDIDO) as hora , TIME_FORMAT(DATA_PEDIDO,'%H:00' ) as horaformated , TIME_FORMAT(DATE_ADD(DATA_PEDIDO,interval 1 HOUR ),'%H:00' )  as HORA2, sum(QTD_PROD) as qtd  from pedido ");
			sql.append(" ");
			sql.append("inner join pedido_item ");
			sql.append("on pedido_item .id_pedido = pedido.id_pedido ");
			sql.append(" ");
			sql.append(" ");
			sql.append("where id_distribuidora = ? and id_prod = ?  and flag_status = 'O' and HOUR(DATA_PEDIDO) = " + rs2.getInt("n") + "  ");
			sql.append(" ");

			sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico));

			sql.append("group by HOUR(DATA_PEDIDO) order by HOUR(DATA_PEDIDO)");

			st = conn.prepareStatement(sql.toString());
			st.setInt(1, coddistr);
			st.setInt(2, Integer.parseInt(id_prod));
			int contparam = 3;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

			rs = st.executeQuery();
			if (rs.next()) {
				obj = new JSONObject();
				obj.put("hora", rs.getString("hora"));
				obj.put("horaformated", rs.getString("horaformated") + "-" + rs.getString("HORA2"));
				obj.put("qtd", rs.getInt("qtd"));
				obj.put("val_totalprod", rs.getDouble("VAL_TOTALPROD"));
				retorno.add(obj);
			} else {
				obj = new JSONObject();
				if (rs2.getInt("n") < 10) {
					hora = "0" + rs2.getInt("n") + ":00";
				} else {
					hora = rs2.getInt("n") + ":00";
				}

				if (rs2.getInt("n") + 1 < 10) {
					hora2 = "0" + (rs2.getInt("n") + 1) + ":00";
				} else {
					hora2 = (rs2.getInt("n") + 1) + ":00";
				}

				obj.put("hora", hora);
				obj.put("horaformated", hora + "-" + hora2);
				obj.put("qtd", "0");
				obj.put("val_totalprod", 0);
				retorno.add(obj);
			}

		}

		System.out.println(retorno.toJSONString());

		out.print(retorno.toJSONString());
	}

	public static void dashProdutos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");
		String consulta = request.getParameter("consulta") == null ? "" : request.getParameter("consulta");
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT pedido_item.id_prod, ");
		sql.append("       Sum(qtd_prod) as qtd, ");
		sql.append("       DESC_ABREVIADO as desc_prod ");
		sql.append("FROM   pedido_item ");
		sql.append("       INNER JOIN pedido ");
		sql.append("               ON pedido.id_pedido = pedido_item.id_pedido ");
		sql.append("       INNER JOIN produtos ");
		sql.append("               ON produtos.id_prod = pedido_item.id_prod ");
		sql.append("         where id_distribuidora = ?  and flag_status = 'O'  ");

		sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico));

		if (consulta.equalsIgnoreCase("-")) {
			sql.append(" group by pedido_item.id_prod ,desc_prod order by sum(QTD_PROD) asc limit 20 ;");
		} else {
			sql.append(" group by pedido_item.id_prod ,desc_prod order by sum(QTD_PROD) desc limit 20 ;");
		}

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, coddistr);
		int contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("desc", rs.getString("desc_prod"));
			obj.put("qtd", rs.getInt("qtd"));
			obj.put("qtddf", df.format(rs.getInt("qtd")));
			retorno.add(obj);
		}

		out.print(retorno.toJSONString());
	}

	public static void dashProdutosVal(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");
		String consulta = request.getParameter("consulta") == null ? "" : request.getParameter("consulta");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pedido_item.id_prod, ");
		sql.append("       Sum(val_unit * qtd_prod ) as valsold,  ");
		sql.append("       DESC_ABREVIADO as desc_prod ");
		sql.append("FROM   pedido_item ");
		sql.append("       INNER JOIN pedido ");
		sql.append("               ON pedido.id_pedido = pedido_item.id_pedido ");
		sql.append("       INNER JOIN produtos ");
		sql.append("               ON produtos.id_prod = pedido_item.id_prod ");
		sql.append("         where id_distribuidora = ?  and flag_status = 'O'  ");

		sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico));

		if (consulta.equalsIgnoreCase("-")) {
			sql.append(" group by pedido_item.id_prod ,desc_prod order by sum(val_unit * qtd_prod ) asc limit 20 ;");
		} else {
			sql.append(" group by pedido_item.id_prod ,desc_prod order by sum(val_unit * qtd_prod ) desc limit 20 ;");
		}
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, coddistr);
		int contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("desc", rs.getString("desc_prod"));
			obj.put("valsold", rs.getInt("valsold"));
			obj.put("valsolddesc", df2.format(rs.getInt("valsold")));
			retorno.add(obj);
		}

		out.print(retorno.toJSONString());
	}

	public static void dashPagamento(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		String sql = "select distinct FLAG_MODOPAGAMENTO, count(FLAG_MODOPAGAMENTO) as qtd from pedido  where id_distribuidora = ?  and flag_status = 'O' ";

		sql = parametrosDash(sql, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		sql = sql + " group by FLAG_MODOPAGAMENTO ;";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		int contparam = 2;

		parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, data_pedido_ini, data_pedido_fim, flag_servico);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("desc", rs.getString("FLAG_MODOPAGAMENTO").equalsIgnoreCase("C") ? "Cartão Créd." : "Dinheiro");
			obj.put("qtd", rs.getInt("qtd"));
			obj.put("qtddf", df.format(rs.getInt("qtd")));
			retorno.add(obj);
		}

		double total = 0;
		for (int i = 0; i < retorno.size(); i++) {
			JSONObject obj = (JSONObject) retorno.get(i);
			total = total + (Integer) obj.get("qtd");
		}

		for (int i = 0; i < retorno.size(); i++) {
			JSONObject obj = (JSONObject) retorno.get(i);
			int qtd = (Integer) obj.get("qtd");
			obj.put("perc", df3.format((100 * qtd) / total));

		}

		out.print(retorno.toJSONString());
	}

	public static void dashMeses(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String data_pedido_ini = request.getParameter("data_pedido_ini") == null ? "" : request.getParameter("data_pedido_ini");
		String data_pedido_fim = request.getParameter("data_pedido_fim") == null ? "" : request.getParameter("data_pedido_fim");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String hora_final = request.getParameter("hora_final") == null ? "" : request.getParameter("hora_final");
		String hora_inicial = request.getParameter("hora_inicial") == null ? "" : request.getParameter("hora_inicial");
		String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");
		String dias_semana = request.getParameter("dias_semana") == null ? "" : request.getParameter("dias_semana");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		StringBuffer sql;
		int cont = 0;
		PreparedStatement st;
		ResultSet rs;
		int contparam;
		while (cont < 13) {

			sql = new StringBuffer();
			sql.append("SELECT mes, ");
			sql.append("       ano, ");
			sql.append("       Sum(qtd)         AS qtd, ");
			sql.append("       Sum(total)       AS total, ");
			sql.append("       Sum(entregas)    AS entrega, ");
			sql.append("       Sum(retirada)    AS retirada, ");
			sql.append("       Sum(valentregas) AS valentregas, ");
			sql.append("       Sum(valretirada) AS valretirada ");
			sql.append("FROM   (SELECT Month(data_pedido) AS mes, ");
			sql.append("               Year(data_pedido)  AS ano, ");
			sql.append("               Count(*)           AS qtd, ");
			sql.append("               flag_pedido_ret_entre, ");
			sql.append("               Sum(val_totalprod) AS total, ");
			sql.append("               CASE flag_pedido_ret_entre ");
			sql.append("                 WHEN 'T' THEN Sum(1) ");
			sql.append("                 ELSE Sum(0) ");
			sql.append("               end                AS entregas, ");
			sql.append("               CASE flag_pedido_ret_entre ");
			sql.append("                 WHEN 'L' THEN Sum(1) ");
			sql.append("                 ELSE Sum(0) ");
			sql.append("               end                AS retirada, ");
			sql.append("               CASE flag_pedido_ret_entre ");
			sql.append("                 WHEN 'T' THEN Sum(val_totalprod) ");
			sql.append("                 ELSE Sum(0) ");
			sql.append("               end                AS valentregas, ");
			sql.append("               CASE flag_pedido_ret_entre ");
			sql.append("                 WHEN 'L' THEN Sum(val_totalprod) ");
			sql.append("                 ELSE Sum(0) ");
			sql.append("               end                AS valretirada ");
			sql.append("        FROM   pedido ");
			sql.append("        WHERE  id_distribuidora = ? ");
			sql.append("               AND flag_status = 'O' and Month(data_pedido) = ? and  Year(data_pedido) = ? ");

			sql = new StringBuffer(parametrosDash(sql.toString(), hora_inicial, hora_final, dias_semana, cod_bairro, "", "", ""));

			sql.append("        GROUP  BY Month(data_pedido), ");
			sql.append("                  Year(data_pedido), ");
			sql.append("                  flag_pedido_ret_entre ");
			sql.append("        ORDER  BY Year(data_pedido) DESC, ");
			sql.append("                  Month(data_pedido) DESC) AS tab ");
			sql.append("GROUP  BY tab.ano, ");
			sql.append("          tab.mes ");
			sql.append("ORDER  BY tab.ano DESC, ");
			sql.append("          tab.mes DESC ");

			st = conn.prepareStatement(sql.toString());
			st.setInt(1, coddistr);
			st.setInt(2, month);
			st.setInt(3, year);
			contparam = 4;

			parametrosDashSt(st, contparam, hora_inicial, hora_final, dias_semana, cod_bairro, "", "", "");

			rs = st.executeQuery();
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("desc", Utilitario.getDescMes(rs.getInt("mes")) + " / " + rs.getInt("ano"));
				obj.put("qtd", rs.getInt("qtd"));
				
				obj.put("entrega", rs.getInt("entrega"));
				obj.put("retirada", rs.getInt("retirada"));
				obj.put("total", rs.getDouble("total"));
				obj.put("valentregas", rs.getDouble("valentregas"));
				obj.put("valretirada", rs.getDouble("valretirada"));

				retorno.add(obj);
			} else {
				JSONObject obj = new JSONObject();
				obj.put("desc", Utilitario.getDescMes(month) + "/" + year);
				obj.put("qtd", 0);
			
				obj.put("entrega", 0);
				obj.put("retirada", 0);
				obj.put("total", 0);
				obj.put("valentregas", 0);
				obj.put("valretirada", 0);
				retorno.add(obj);
			}

			month = month - 1;
			if (month == 0) {
				month = 12;
				year = year - 1;
			}
			cont++;
		}
		out.print(retorno.toJSONString());

	}

	public static JRMapCollectionDataSource relGradeHorariosDataSource(int coddistr, Connection conn) throws Exception {

		Collection<Map<String, ?>> arrLinhas = new ArrayList<Map<String, ?>>();
		try {

			PreparedStatement st3 = null;
			ResultSet rs3 = null;
			PreparedStatement st2 = null;
			ResultSet rs2 = null;

			String sql = " select * from bairros order by desc_bairro asc ";
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				HashMap<String, Object> hmFat = new HashMap<String, Object>();
				hmFat.put("desc_bairro", rs.getString("desc_bairro"));

				sql = " select * from dias_semana ";
				st2 = conn.prepareStatement(sql);
				rs2 = st2.executeQuery();
				while (rs2.next()) {

					String horario = "";

					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from distribuidora_bairro_entrega ");
					sql2.append(" ");
					sql2.append("left join distribuidora_horario_dia_entre ");
					sql2.append("on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO = distribuidora_bairro_entrega.ID_DISTR_BAIRRO ");
					sql2.append(" ");
					sql2.append("where Coalesce(distribuidora_bairro_entrega.id_distribuidora," + coddistr + ") = " + coddistr + " and distribuidora_bairro_entrega.cod_bairro = " + rs.getString("cod_bairro") + " and cod_dia = " + rs2.getString("cod_dia") + " ORDER BY HORARIO_INI desc");

					st3 = conn.prepareStatement(sql2.toString());
					rs3 = st3.executeQuery();
					while (rs3.next()) {
						String horaini = new SimpleDateFormat("HH:mm").format(rs3.getTimestamp("HORARIO_INI"));
						String horafim = new SimpleDateFormat("HH:mm").format(rs3.getTimestamp("HORARIO_FIM"));

						horario = "\n" + horaini + " - " + horafim + horario;

					}
					horario = horario.replaceFirst("\n", "");
					hmFat.put(rs2.getString("cod_dia") + "_horas", horario);
				}

				arrLinhas.add(hmFat);
			}

		} catch (Exception e) {
			throw e;
		} finally {
		}

		JRMapCollectionDataSource objRetorno = new JRMapCollectionDataSource(arrLinhas);
		return objRetorno;

	}

	public static JRMapCollectionDataSource relPedidosDataSource(int coddistr, Connection conn, Map hmParams, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Collection<Map<String, ?>> arrLinhas = new ArrayList<Map<String, ?>>();
		try {

			PreparedStatement st3 = null;
			ResultSet rs3 = null;
			PreparedStatement st2 = null;
			ResultSet rs2 = null;

			String dataini = request.getParameter("dataini") == null ? "" : request.getParameter("dataini");
			String datafim = request.getParameter("datafim") == null ? "" : request.getParameter("datafim");
			String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");
			String infocliente = request.getParameter("infocliente") == null ? "" : request.getParameter("infocliente");
			String chk_prods = request.getParameter("chk_prods") == null ? "" : request.getParameter("chk_prods");
			String flag_pagamento = request.getParameter("flag_pagamento") == null ? "" : request.getParameter("flag_pagamento");
			String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
			String flag_servico = request.getParameter("flag_servico") == null ? "" : request.getParameter("flag_servico");

			hmParams.put("dataini", "");
			hmParams.put("datafim", "");
			hmParams.put("situacao", Utilitario.returnStatusPedidoFlag(flag_situacao));
			hmParams.put("servico", Utilitario.returnDistrTiposPedido(flag_servico));
			hmParams.put("modo_pay", Utilitario.returnModoPagamento(flag_pagamento));
			if (!cod_bairro.equalsIgnoreCase(""))
				hmParams.put("bairro", Utilitario.getNomeBairro(conn, Integer.parseInt(cod_bairro), 0));
			else
				hmParams.put("bairro", "Todos");

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT desc_prod, ");
			sql.append("       pedido_item.val_unit, ");
			sql.append("       qtd_prod, ");
			sql.append("       pedido_item.val_unit * qtd_prod          AS val_subtotalprod, ");
			sql.append("       pedido.id_pedido, ");
			sql.append("       COALESCE(bairros.desc_bairro, '-')       AS desc_bairro, ");
			sql.append("       pedido.flag_status, ");
			sql.append("       data_pedido, ");
			sql.append("       val_totalprod, ");
			sql.append("       val_entrega, ");
			sql.append("       data_pedido_resposta, ");
			sql.append("       num_ped, ");
			sql.append("       pedido.flag_modopagamento, ");
			sql.append("       flag_pedido_ret_entre, ");
			sql.append("       perc_pagamento, ");
			sql.append("       nome_pessoa, ");
			sql.append("       desc_endereco_entrega, ");
			sql.append("       desc_endereco_num_entrega, ");
			sql.append("       desc_endereco_complemento_entrega, ");
			sql.append("       num_telefonecontato_cliente, ");
			sql.append("       data_cancelamento, ");
			sql.append("       desc_obs, ");
			sql.append("       desc_motivo, ");
			sql.append("       ( perc_pagamento * val_totalprod ) / 100 AS tragoaqui_perc ");
			sql.append("FROM   pedido ");
			sql.append("       INNER JOIN distribuidora ");
			sql.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
			sql.append("       LEFT JOIN bairros ");
			sql.append("              ON bairros.cod_bairro = pedido.cod_bairro ");
			sql.append("       INNER JOIN pedido_item ");
			sql.append("               ON pedido.id_pedido = pedido_item.id_pedido ");
			sql.append("       INNER JOIN produtos_distribuidora ");
			sql.append("               ON produtos_distribuidora.id_prod = pedido_item.id_prod ");
			sql.append("                  AND produtos_distribuidora.id_distribuidora =  " + coddistr);
			sql.append("       INNER JOIN produtos ");
			sql.append("               ON produtos.id_prod = produtos_distribuidora.id_prod ");
			sql.append("       LEFT JOIN pedido_motivo_cancelamento ");
			sql.append("              ON pedido_motivo_cancelamento.id_pedido = pedido.id_pedido ");
			sql.append("       LEFT JOIN motivos_cancelamento ");
			sql.append("              ON pedido_motivo_cancelamento.cod_motivo = ");
			sql.append("                 motivos_cancelamento.cod_motivo");

			sql.append("			where distribuidora.id_distribuidora =  " + coddistr);

			if (!(dataini.equalsIgnoreCase(""))) {
				sql.append("  and  data_pedido >= ?");
				hmParams.put("dataini", dataini);

			}

			if (!(datafim.equalsIgnoreCase("")) && datafim != null) {
				sql.append(" and  data_pedido <= ?");
				hmParams.put("datafim", datafim);
			}

			if (!flag_situacao.equalsIgnoreCase("")) {
				sql.append("  and  FLAG_STATUS = ? ");
			}

			if (!cod_bairro.equalsIgnoreCase("")) {
				sql.append("  and  pedido.cod_bairro = ? ");
			}

			if (!flag_servico.equalsIgnoreCase("")) {
				sql.append("  and  pedido.FLAG_PEDIDO_RET_ENTRE = ? ");
			}

			if (!flag_pagamento.equalsIgnoreCase("")) {
				sql.append("  and  pedido.FLAG_MODOPAGAMENTO = ? ");
			}

			sql.append(" order by num_ped");

			PreparedStatement st = conn.prepareStatement(sql.toString());
			int contparam = 1;

			if (!(dataini.equalsIgnoreCase(""))) {
				Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dataini + " " + "00:00");
				st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
				contparam++;
			}

			if (!(datafim.equalsIgnoreCase(""))) {
				Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(datafim + " " + "23:59:59");
				st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data));
				contparam++;
			}

			if (!flag_situacao.equalsIgnoreCase("")) {
				st.setString(contparam, flag_situacao);
				contparam++;
			}

			if (!cod_bairro.equalsIgnoreCase("")) {
				st.setLong(contparam, Long.parseLong(cod_bairro));
				contparam++;
			}

			if (!flag_servico.equalsIgnoreCase("")) {
				st.setString(contparam, flag_servico);
				contparam++;
			}

			if (!flag_pagamento.equalsIgnoreCase("")) {
				st.setString(contparam, flag_pagamento);
				contparam++;
			}

			ResultSet rs = st.executeQuery();
			HashMap<String, Object> hmFat = new HashMap<String, Object>();
			double val_totalprod = 0;

			double val_entrega = 0;
			double tragoaqui_perc = 0;
			long id_ped = 0;
			long qtd_ped = 0;

			while (rs.next()) {
				hmFat = new HashMap<String, Object>();
				hmFat.put("flag_status", (rs.getString("flag_status")));
				hmFat.put("desc_status", Utilitario.returnStatusPedidoFlag(rs.getString("flag_status")));
				hmFat.put("data_pedido", rs.getTimestamp("DATA_PEDIDO"));
				hmFat.put("data_pedido_resposta", rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
				hmFat.put("num_ped", rs.getLong("NUM_PED"));
				hmFat.put("val_totalprod", new BigDecimal(rs.getDouble("VAL_TOTALPROD")));
				hmFat.put("flag_modopagamento", rs.getString("FLAG_MODOPAGAMENTO"));
				hmFat.put("flag_pedido_ret_entre", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
				hmFat.put("tragoaqui_perc", new BigDecimal(rs.getDouble("tragoaqui_perc")));
				hmFat.put("desc_bairro", rs.getString("DESC_BAIRRO"));
				hmFat.put("id_pedido", rs.getLong("id_pedido"));
				hmFat.put("desc_prod", rs.getString("desc_prod"));
				hmFat.put("val_unit", new BigDecimal(rs.getDouble("val_unit")));
				hmFat.put("qtd_prod", rs.getLong("QTD_PROD"));
				hmFat.put("val_subtotalprod", new BigDecimal(rs.getDouble("VAL_SUBTOTALPROD")));
				hmFat.put("val_entrega", new BigDecimal(rs.getDouble("VAL_ENTREGA")));
				hmFat.put("nome_pessoa", rs.getString("NOME_PESSOA"));
				hmFat.put("desc_endereco_entrega", rs.getString("DESC_ENDERECO_ENTREGA"));
				hmFat.put("desc_endereco_num_entrega", rs.getString("DESC_ENDERECO_NUM_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_NUM_ENTREGA"));
				hmFat.put("desc_endereco_complemento_entrega", rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA"));
				hmFat.put("num_telefonecontato_cliente", rs.getString("NUM_TELEFONECONTATO_CLIENTE") == null ? "" : rs.getString("NUM_TELEFONECONTATO_CLIENTE"));
				hmFat.put("data_cancelamento", rs.getTimestamp("data_cancelamento"));
				hmFat.put("desc_obs", rs.getString("desc_obs"));
				hmFat.put("desc_motivo", rs.getString("desc_motivo"));

				if (rs.getLong("id_pedido") != id_ped) {
					qtd_ped++;
					val_totalprod = val_totalprod + rs.getDouble("VAL_TOTALPROD");
					val_entrega = val_entrega + rs.getDouble("val_entrega");
					tragoaqui_perc = tragoaqui_perc + rs.getDouble("tragoaqui_perc");

					id_ped = rs.getLong("id_pedido");
				}

				hmFat.put("qtd_pedido", qtd_ped);
				hmFat.put("summary_prodtotal", new BigDecimal(val_totalprod));
				hmFat.put("summary_fretetotal", new BigDecimal(val_entrega));
				hmFat.put("summary_total", new BigDecimal(val_totalprod + val_entrega));
				hmFat.put("summary_perc", new BigDecimal(tragoaqui_perc));

				arrLinhas.add(hmFat);
			}

		} catch (Exception e) {
			throw e;
		} finally {
		}

		JRMapCollectionDataSource objRetorno = new JRMapCollectionDataSource(arrLinhas);
		return objRetorno;

	}

	public static void rodaRel(String nome, JRMapCollectionDataSource datasource, Map hmParams, HttpServletRequest request, HttpServletResponse response) {

		File file = null;
		try {

			List listaReport = new LinkedList();

			JasperReport objRelJasper_ordemprod = null;
			objRelJasper_ordemprod = JasperCompileManager.compileReport("D:/phonegap_projects/m_16/m_16/src/main/webapp/rels/" + nome + ".jrxml");
			hmParams.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
			listaReport.add(JasperFillManager.fillReport(objRelJasper_ordemprod, hmParams, datasource));

			JRPdfExporter exporter = new JRPdfExporter();

			String arq = "" + new File("D:/phonegap_projects/m_16/m_16/src/main/webapp/rels/" + nome + ".pdf");
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, listaReport);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_FILE_NAME, arq);
			exporter.exportReport();

			file = new File(arq);
			// file = File.createTempFile("grade_horarios", ".pdf");

			response.setContentType("application/pdf");
			response.setContentLength((int) file.length());
			response.setDateHeader("Expires", 0);
			response.setHeader("Pragma", "no-cache");
			response.addHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-Disposition", "inline; filename=oi" + file.getName());

			ServletOutputStream ouputStream = response.getOutputStream();

			FileInputStream fileInputStream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			Utilitario.copiaStream(fileInputStream, ouputStream);

			ouputStream.flush();
			ouputStream.close();
			fileInputStream.close();
			file.delete();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void relGradeHorarios(HttpServletRequest request, HttpServletResponse response, int coddistr) {

		Connection conn = null;
		File file = null;
		try {

			conn = Conexao.getConexao();
			Map hmParams = new HashMap();
			hmParams.put("nome_distribuidora", Utilitario.getNomeDistr(conn, coddistr, false));
			rodaRel("rel_gradehorario", relGradeHorariosDataSource(coddistr, conn), hmParams, request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	public static void relPedidos(HttpServletRequest request, HttpServletResponse response, int coddistr) {

		Connection conn = null;
		try {

			String flag_opc = request.getParameter("flag_opc") == null ? "" : request.getParameter("flag_opc");

			conn = Conexao.getConexao();
			Map hmParams = new HashMap();

			if (flag_opc.equalsIgnoreCase("A")) {
				hmParams.put("show_prods", true);
				hmParams.put("show_info", true);
				hmParams.put("opcao_exib", "Analítica");
			} else {
				hmParams.put("show_prods", false);
				hmParams.put("show_info", false);
				hmParams.put("opcao_exib", "Sintética");
			}

			hmParams.put("nome_distribuidora", Utilitario.getNomeDistr(conn, coddistr, false));

			rodaRel("rel_pedidos", relPedidosDataSource(coddistr, conn, hmParams, request, response), hmParams, request, response);

		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
