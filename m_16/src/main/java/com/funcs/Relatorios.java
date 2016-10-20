package com.funcs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.configs.Conexao;

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
						

						horario = "\n" + horaini + " - " + horafim +   horario;
						

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

	public static void relGradeHorarios(HttpServletRequest request, HttpServletResponse response, int coddistr) {

		Connection conn = null;
		File file = null;
		try {

			conn = Conexao.getConexao();

			List listaReport = new LinkedList();
			Map hmParams = new HashMap();

			hmParams.put("nome_distribuidora", Utilitario.getNomeDistr(conn, coddistr, false));

			JasperReport objRelJasper_ordemprod = null;
			objRelJasper_ordemprod = JasperCompileManager.compileReport("D:/phonegap_projects/m_16/m_16/src/main/webapp/rels/rel_gradehorario.jrxml");
			listaReport.add(JasperFillManager.fillReport(objRelJasper_ordemprod, hmParams, relGradeHorariosDataSource(coddistr, conn)));

			JRPdfExporter exporter = new JRPdfExporter();

			String arq = "" + new File("D:/phonegap_projects/m_16/m_16/src/main/webapp/rels/rel_gradehorario.pdf");
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
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
