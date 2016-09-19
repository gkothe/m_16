package com.funcs;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
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

import com.configs.Conexao;

public class Utilitario {

	
	
	
	
	public static JSONArray payments_ids(){
		JSONArray payids = new JSONArray();
		JSONObject obj = new JSONObject();
		
		obj = new JSONObject();
		obj.put("payid", "");
		obj.put("desc", "Escolha um tipo de cartão de crédito");
		payids.add(obj);
		
		obj = new JSONObject();
		obj.put("payid", "master");
		obj.put("desc", "Mastercard");
		payids.add(obj);
		
		obj = new JSONObject();
		obj.put("payid", "visa");
		obj.put("desc", "Visa");
		payids.add(obj);
		
		
	
		
		return payids;
		
	}
	
	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static void sendEmail(String para, String html, String subject, Connection conn) throws Exception {

		Sys_parametros sys = new Sys_parametros(conn);

		HtmlEmail mailService = new HtmlEmail();

		mailService.setHostName(sys.getSys_host_name_smtp());
		mailService.setSmtpPort(sys.getSys_smtp_port());
		mailService.setAuthenticator(new DefaultAuthenticator(sys.getSys_email(), sys.getSys_senha()));
		mailService.setFrom(sys.getSys_fromemail(), sys.getSys_fromdesc());
		mailService.setStartTLSEnabled(sys.getSys_tls());
		mailService.setSubject(subject);
		mailService.setHtmlMsg(html);
		mailService.addTo(para);
		mailService.send();

	}

	public static String StringGen(int a, int b) {
		SecureRandom random = new SecureRandom();
		return new BigInteger(a, random).toString(b);
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

	public static long retornaIdinsertLong(String tabela, String coluna, Connection conn) throws Exception {
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
		long id = 1;
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			id = rs2.getLong("missing");
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
		} else {
			throw new Exception("Distribuidora não existe.");
		}
		return dia;
	}

	public static String getDescDiaSemana(Connection conn, int cod_dia) throws Exception {

		String varname1 = " select * from  dias_semana where cod_dia = ? ";
		PreparedStatement st = conn.prepareStatement(varname1);
		st.setInt(1, cod_dia);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			return rs2.getString("DESC_DIA");
		} else {
			throw new Exception("Dia não existe.");
		}

	}

	public static String getNomeBairro(Connection conn, int cod_bairro, int ID_DISTR_BAIRRO) throws Exception {

		String desc_bairro = "";

		if (cod_bairro != 0) {

			String varname1 = " select * from bairros where cod_bairro =  " + cod_bairro;
			PreparedStatement st = conn.prepareStatement(varname1);
			ResultSet rs2 = st.executeQuery();
			if (rs2.next()) {
				desc_bairro = rs2.getString("DESC_BAIRRO");
			} else {
				throw new Exception("Bairro não existe.");
			}

		} else if (ID_DISTR_BAIRRO != 0) {

			String varname1 = " select * from bairros inner join distribuidora_bairro_entrega where ID_DISTR_BAIRRO = " + ID_DISTR_BAIRRO + " limit 1";
			PreparedStatement st = conn.prepareStatement(varname1);
			ResultSet rs2 = st.executeQuery();
			if (rs2.next()) {
				desc_bairro = rs2.getString("DESC_BAIRRO");
			} else {
				throw new Exception("Bairro não existe.");
			}

		}

		return desc_bairro;
	}

	public static String getNomeProd(Connection conn, long id_prod, boolean abreviado) throws Exception {

		String desc_produto = "";
		String varname1 = " select * from produtos where id_prod =  " + id_prod;
		PreparedStatement st = conn.prepareStatement(varname1);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {

			if (!abreviado) {
				desc_produto = rs2.getString("DESC_PROD");
			} else {
				desc_produto = rs2.getString("DESC_ABREVIADO");
			}
		} else {
			throw new Exception("Produto não existe.");
		}

		return desc_produto;
	}

	public static String getNomeProdIdProdDistr(Connection conn, long ID_PROD_DIST, boolean abreviado) throws Exception {

		String desc_produto = "";
		String varname1 = " select DESC_ABREVIADO,DESC_PROD from produtos inner join produtos_distribuidora on produtos_distribuidora.id_prod = produtos.id_prod where ID_PROD_DIST =  " + ID_PROD_DIST;
		PreparedStatement st = conn.prepareStatement(varname1);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {

			if (!abreviado) {
				desc_produto = rs2.getString("DESC_PROD");
			} else {
				desc_produto = rs2.getString("DESC_ABREVIADO");
			}
		} else {
			throw new Exception("Produto não existe.");
		}

		return desc_produto;
	}

	public static java.sql.Timestamp getTimeStamp(Date data) {
		return new java.sql.Timestamp(data.getTime());
	}

	public static int retornaIdinsertChaveSecundaria(String tabela, String nomechaveprimaria, String valchaveprimaria, String coluna, Connection conn) throws Exception {
		String varname1 = "";
		// so funciona para pk single

		varname1 += " SELECT z.expected AS missing  ";
		varname1 += " FROM   ( ";
		varname1 += " SELECT   @rownum:=@rownum+1                         AS expected,IF(@rownum=" + coluna + ", 0, @rownum:=" + coluna + ") as got";
		varname1 += " FROM     ( ";
		varname1 += " SELECT @rownum:=0) AS a ";
		varname1 += " JOIN     " + tabela + " ";
		varname1 += " where " + nomechaveprimaria + " = " + valchaveprimaria + " ";
		varname1 += " ORDER BY " + coluna + " ) AS z ";
		varname1 += " WHERE  z.got!=0 ";
		varname1 += " UNION ";
		varname1 += " SELECT COALESCE(max(" + coluna + "+1),1) AS missing ";
		varname1 += " FROM   " + tabela + " where " + nomechaveprimaria + " = " + valchaveprimaria + " limit 1 ";

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
			// sendEmail("g.kothe@hotmail.com", "aaaa", "Recuperação de senha");

			String validacao = Utilitario.StringGen(1000, 32).substring(0, 99);
			Sys_parametros sys = new Sys_parametros(Conexao.getConexao());
			String texto = " Bem vindo ao S.O.S Trago, para validar seu e-mail clique no link abaixo: <br> " + sys.getUrl_system() + "mobile/ac=validar&token=" + validacao;
			// Utilitario.sendEmail("g.kothe@hotmail.com", texto, "Ativação da sua conta no S.O.S Trago!");

			System.out.println("Horario_1".toString().substring("Horario_1".toString().length()-1, "Horario_1".toString().length()));
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
