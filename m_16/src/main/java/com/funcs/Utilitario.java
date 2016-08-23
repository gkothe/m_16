package com.funcs;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Utilitario {

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static void sendEmail(String para, String html, String subject) throws Exception {

		HtmlEmail mailService = new HtmlEmail();

		mailService.setHostName("smtp.live.com");
		mailService.setSmtpPort(587);
		mailService.setAuthenticator(new DefaultAuthenticator("g.kothe@hotmail.com", ""));
		mailService.setFrom("g.kothe@hotmail.com", "Chama Trago");
		mailService.setStartTLSEnabled(true);
		mailService.setSubject(subject);
		mailService.setHtmlMsg(html);
		mailService.addTo(para);
		mailService.send();

	}

	public static String returnStatusPedidoFlag(String flag) {

		if (flag.equalsIgnoreCase("E")) {
			return "Em envio";
		} else if (flag.equalsIgnoreCase("R")) {
			return "Recusado";
		} else if (flag.equalsIgnoreCase("O")) {
			return "Finalizado";
		} else if (flag.equalsIgnoreCase("A")) {
			return "Aberto";
		}

		return "";
	}
	
	public static String returnModoPagamento(String flag) {

		if (flag.equalsIgnoreCase("D")) {
			return "Dinhero";
		} else if (flag.equalsIgnoreCase("C")) {
			return "Cartão";
		} else if (flag.equalsIgnoreCase("A")) {
			return "Ambos";
		}

		return "";
	}

	public static int retornaIdinsert(String tabela, String coluna, Connection conn) throws Exception {
		String varname1 = "";
		// so funciona para pk single
		varname1 = "";
		varname1 += "SELECT ";
		varname1 += " z.expected AS missing ";
		varname1 += "FROM ( ";
		varname1 += " SELECT ";
		varname1 += "  @rownum:=@rownum+1 AS expected, ";
		varname1 += "  IF(@rownum=" + coluna + ", 0, @rownum:=" + coluna + ") AS got ";
		varname1 += " FROM ";
		varname1 += "  (SELECT @rownum:=0) AS a ";
		varname1 += "  JOIN  " + tabela;
		varname1 += "  ORDER BY  " + coluna;
		varname1 += " ) AS z ";
		varname1 += " WHERE z.got!=0 ";
		varname1 += "union ";
		varname1 += " select  Coalesce(max(" + coluna + "+1),1)     AS missing from  " + tabela;
		varname1 += " limit 1";

		PreparedStatement st = conn.prepareStatement(varname1);
		int id = 1;
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			id = rs2.getInt("missing");
		}

		return id;
	}

	public static Integer diaSemana(Connection conn, int distribuidora) throws Exception {

		// de acordo com o que ta no banco de dados, se for DAY_OF_WEEK = 1 vai ser domingo (codigo 7 no banco), resto é o dia menos 1.
		// 1 Segunda-feira
		// 2 Terça-feira
		// 3 Quarta-feira
		// 4 Quinta-feira
		// 5 Sexta-feira
		// 6 Sábado
		// 7 Domingo
		// 8 Feriado/custom

		// Calendar
		// SUNDAY, 1
		// MONDAY, 2
		// TUESDAY, 3
		// WEDNESDAY 4
		// THURSDAY, 5
		// FRIDAY, 6
		// SATURDAY. 7

		String varname1 = " select FLAG_CUSTOM from distribuidora where id_distribuidora = ? ";
		int dia = 0;
		PreparedStatement st = conn.prepareStatement(varname1);
		st.setInt(1, distribuidora);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			if (rs2.getString("FLAG_CUSTOM").equalsIgnoreCase("S")) {
				dia = 8;// se o flag custom ta ativo, retorna dia feriado/custom.

			} else {
				// ex: se day of week é 4, WEDNESDAY , o retorno vai ser day of week - 1 =3 Qarta feira.
				dia = new GregorianCalendar().get(Calendar.DAY_OF_WEEK) == 1 ? 7 : new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
			}
		}else{
			throw new Exception("Distribuidora não existe.");
		}
		return dia;
	}

	public static int retornaIdinsertChaveSecundaria(String tabela, String chaveprimaria, String id_chaveprimaria, String coluna, Connection conn) throws Exception {
		String varname1 = "";
		// so funciona para pk single

		varname1 += " SELECT z.expected AS missing  ";
		varname1 += " FROM   ( ";
		varname1 += " SELECT   @rownum:=@rownum+1                         AS expected,IF(@rownum=" + coluna + ", 0, @rownum:=" + coluna + ") as got";
		varname1 += " FROM     ( ";
		varname1 += " SELECT @rownum:=0) AS a ";
		varname1 += " JOIN     " + tabela + " ";
		varname1 += " where " + chaveprimaria + " = " + id_chaveprimaria + " ";
		varname1 += " ORDER BY " + coluna + " ) AS z ";
		varname1 += " WHERE  z.got!=0 ";
		varname1 += " UNION ";
		varname1 += " SELECT COALESCE(max(" + coluna + "+1),1) AS missing ";
		varname1 += " FROM   " + tabela + " where " + chaveprimaria + " = " + id_chaveprimaria + " limit 1 ";

		PreparedStatement st = conn.prepareStatement(varname1);
		int id = 1;
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			id = rs2.getInt("missing");
		}

		return id;
	}

	public static void main(String[] args) {
		try {
			sendEmail("g.kothe@hotmail.com", "aaaa", "Recuperação de senha");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
