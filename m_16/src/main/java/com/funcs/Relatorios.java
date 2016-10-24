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

	// Dias são fixos, 1 segunda feira ate 7 domingo, 8=custom

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

	public static JRMapCollectionDataSource relPedidosDataSource(int coddistr, Connection conn, Map hmParams,HttpServletRequest request, HttpServletResponse response) throws Exception {

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
			
			StringBuffer  sql = new StringBuffer();
			
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
			}

			if (!(datafim.equalsIgnoreCase("")) && datafim != null) {
				sql.append(" and  data_pedido <= ?");
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
			while (rs.next()) {
				hmFat = new HashMap<String, Object>();
				hmFat.put("flag_status", (rs.getString("flag_status")));
				hmFat.put("desc_status", Utilitario .returnStatusPedidoFlag(rs.getString("flag_status")));
				hmFat.put("data_pedido", rs.getTimestamp("DATA_PEDIDO"));
				hmFat.put("data_pedido_resposta", rs.getTimestamp("DATA_PEDIDO_RESPOSTA"));
				hmFat.put("num_ped", rs.getLong("NUM_PED"));
				hmFat.put("val_totalprod", new BigDecimal(rs.getDouble("VAL_TOTALPROD")));
				hmFat.put("val_entrega", new BigDecimal(rs.getDouble("VAL_ENTREGA")));
				hmFat.put("flag_modopagamento", rs.getString("FLAG_MODOPAGAMENTO"));
				hmFat.put("flag_pedido_ret_entre", rs.getString("FLAG_PEDIDO_RET_ENTRE"));
				hmFat.put("tragoaqui_perc", new BigDecimal(rs.getDouble("tragoaqui_perc")));
				hmFat.put("desc_bairro", rs.getString("DESC_BAIRRO"));
				hmFat.put("id_pedido", rs.getLong("id_pedido"));
				hmFat.put("desc_prod", rs.getString("desc_prod"));
				hmFat.put("val_unit", new BigDecimal(rs.getDouble("val_unit")));
				hmFat.put("qtd_prod", rs.getLong("QTD_PROD"));
				hmFat.put("val_subtotalprod", new BigDecimal(rs.getDouble("VAL_SUBTOTALPROD")));
				hmFat.put("nome_pessoa", rs.getString("NOME_PESSOA"));
				hmFat.put("desc_endereco_entrega", rs.getString("DESC_ENDERECO_ENTREGA"));
				hmFat.put("desc_endereco_num_entrega", rs.getString("DESC_ENDERECO_NUM_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_NUM_ENTREGA"));
				hmFat.put("desc_endereco_complemento_entrega", rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA"));
				hmFat.put("num_telefonecontato_cliente", rs.getString("NUM_TELEFONECONTATO_CLIENTE") == null ? "" : rs.getString("NUM_TELEFONECONTATO_CLIENTE"));
				
				hmFat.put("data_cancelamento", rs.getTimestamp("data_cancelamento"));
				hmFat.put("desc_obs", rs.getString("desc_obs"));
				hmFat.put("desc_motivo", rs.getString("desc_motivo"));
				
				
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
			
			if(flag_opc.equalsIgnoreCase("A")){
				hmParams.put("show_prods", true);
				hmParams.put("show_info", true);
			}else{
				hmParams.put("show_prods", false);
				hmParams.put("show_info", false);
			}
			
			hmParams.put("nome_distribuidora", Utilitario.getNomeDistr(conn, coddistr, false));
			
			rodaRel("rel_pedidos", relPedidosDataSource(coddistr, conn, hmParams,request,response), hmParams, request, response);

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
