package com.funcs;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.configs.Conexao;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class Utilitario {

	public static JSONArray payments_ids() {
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

	public static JSONArray FlagEntreRet() {
		JSONArray payids = new JSONArray();
		JSONObject obj = new JSONObject();
		/*
		 * obj = new JSONObject(); obj.put("flag_entre_ret", "A"); obj.put("desc", "Todos - Retirada no local e entrega"); payids.add(obj);
		 */
		obj = new JSONObject();
		obj.put("flag_entre_ret", "L");
		obj.put("desc", "Retirada no local");
		payids.add(obj);

		obj = new JSONObject();
		obj.put("flag_entre_ret", "T");
		obj.put("desc", "Entrega");
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
		} else if (flag.equalsIgnoreCase("S")) {
			return "Em espera";
		} else if (flag.equalsIgnoreCase("C")) {
			return "Cancelado";
		} else if (flag.equalsIgnoreCase("")) {
			return "Todas";
		}

		return "";
	}

	public static String getDescMes(int mes) {

		if (mes == 1) {
			return "Janeiro";
		} else if (mes == 2) {
			return "Fevereiro";
		} else if (mes == 3) {
			return "Março";
		} else if (mes == 4) {
			return "Abril";
		} else if (mes == 5) {
			return "Maio";
		} else if (mes == 6) {
			return "Junho";
		} else if (mes == 7) {
			return "Julho";
		} else if (mes == 8) {
			return "Agosto";
		} else if (mes == 9) {
			return "Setembro";
		} else if (mes == 10) {
			return "Outubro";
		} else if (mes == 11) {
			return "Novembro";
		} else if (mes == 12) {
			return "Dezembro";
		}

		return "Inválido";
	}

	public static int getnumMes(String mes) {

		if (mes.equalsIgnoreCase("janeiro")) {
			return 1;
		} else if (mes.equalsIgnoreCase("Fevereiro")) {
			return 2;
		} else if (mes.equalsIgnoreCase("Março")) {
			return 3;
		} else if (mes.equalsIgnoreCase("Abril")) {
			return 4;
		} else if (mes.equalsIgnoreCase("Maio")) {
			return 5;
		} else if (mes.equalsIgnoreCase("Junho")) {
			return 6;
		} else if (mes.equalsIgnoreCase("Julho")) {
			return 7;
		} else if (mes.equalsIgnoreCase("Agosto")) {
			return 8;
		} else if (mes.equalsIgnoreCase("Setembro")) {
			return 9;
		} else if (mes.equalsIgnoreCase("Outubro")) {
			return 10;
		} else if (mes.equalsIgnoreCase("novembro")) {
			return 11;
		} else if (mes.equalsIgnoreCase("dezembro")) {
			return 12;
		}
		return 0;
	}

	public static String returnDistrTiposPedido(String flag) { // , flag_entre_ret

		if (flag.equalsIgnoreCase("L")) {
			return "Retirada no local";
		} else if (flag.equalsIgnoreCase("T")) {
			return "Entrega";
		} else if (flag.equalsIgnoreCase("A")) {
			return "Ambos - Retirada no local e tele-entrega";
		} else if (flag.equalsIgnoreCase("")) {
			return "Todos";
		}

		return "";
	}

	public static String returntipoPedido(String flag) { // , FLAG_PEDIDO_RET_ENTRE

		if (flag.equalsIgnoreCase("L")) {
			// return "Somente retirada no local";
			return "Retirada no local";
		} else if (flag.equalsIgnoreCase("T")) {
			// return "Somente tele-entrega";
			return "Entrega";
		} else if (flag.equalsIgnoreCase("")) {
			return "Todos";
		}

		return "";
	}

	//

	public static JSONArray returnJsonStatusPedidoFlag() {

		JSONArray retornoarray = new JSONArray();

		JSONObject obj = new JSONObject();

		obj = new JSONObject();
		obj.put("id", "");
		obj.put("desc", "Escolha uma situação");
		retornoarray.add(obj);

		obj = new JSONObject();
		obj.put("id", "A");
		obj.put("desc", "Aberto");
		retornoarray.add(obj);

		obj = new JSONObject();
		obj.put("id", "F");
		obj.put("desc", "Finalizado");
		retornoarray.add(obj);

		obj = new JSONObject();
		obj.put("id", "E");
		obj.put("desc", "Em envio");
		retornoarray.add(obj);

		// obj = new JSONObject();
		// obj.put("id", "S");
		// obj.put("desc", "Em espera");
		// retornoarray.add(obj);

		obj = new JSONObject();
		obj.put("id", "R");
		obj.put("desc", "Recusado");
		retornoarray.add(obj);

		return retornoarray;
	}

	public static String returnModoPagamento(String flag) {

		if (flag.equalsIgnoreCase("D")) {
			return "Dinhero";
		} else if (flag.equalsIgnoreCase("C")) {
			return "Cartão";
		} else if (flag.equalsIgnoreCase("A")) {
			return "Ambos";
		} else if (flag.equalsIgnoreCase("")) {
			return "Todos";
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

	public static Integer diaSemanaSimple(Connection conn) throws Exception {

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

		return new GregorianCalendar().get(Calendar.DAY_OF_WEEK) == 1 ? 7 : new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
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

	public static int getNextNumpad(Connection conn, int distribuidora) throws Exception {

		String varname1 = " select coalesce(max(num_ped),0)+1 as num_ped from  pedido where id_distribuidora = ?  ";
		PreparedStatement st = conn.prepareStatement(varname1);
		st.setInt(1, distribuidora);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			return rs2.getInt("NUM_PED");
		}

		return 0;
	}

	public static Date testeHora(String mask, String hora, String msg) throws Exception {

		Date datatempoentregateste2;// tempo de entrega do usuario
		try {
			datatempoentregateste2 = new SimpleDateFormat(mask).parse(hora);
		} catch (Exception e) {
			if (msg.equalsIgnoreCase("")) {
				throw new Exception("Horario inválido");
			} else {
				throw new Exception(msg);
			}

		}

		if (mask.equalsIgnoreCase("HH:mm")) {

			int t = Integer.parseInt(hora.substring(0, 2));
			if (t > 24)
				throw new Exception("As horas não pode ser maior que 24.");

			t = Integer.parseInt(hora.substring(3, 5));
			if (t > 59)
				throw new Exception("Os minutos não podem ser maior que 59.");

		}

		if (mask.equalsIgnoreCase("HHmm")) {

			int t = Integer.parseInt(hora.substring(0, 2));
			if (t > 24)
				throw new Exception("As horas não pode ser maior que 24.");

			t = Integer.parseInt(hora.substring(2, 4));
			if (t > 59)
				throw new Exception("Os minutos não podem ser maior que 59.");

		}

		return datatempoentregateste2;

	}

	public static String getDescDiaSemana(Connection conn, int cod_dia, boolean tratadia) throws Exception {
		if (tratadia) {
			if (cod_dia == 1) {
				cod_dia = 7;
			} else {
				cod_dia = cod_dia - 1;
			}
		}

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
		String varname1 = " select * from produtos where id_prod = ?";

		PreparedStatement st = conn.prepareStatement(varname1);
		st.setLong(1, id_prod);
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

	public static void getFullProdname(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String desc_produto = request.getParameter("param") == null ? "" : request.getParameter("param");
		String varname1 = " select * from produtos where DESC_ABREVIADO = ? ";
		PreparedStatement st = conn.prepareStatement(varname1);
		st.setString(1, desc_produto);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {
			retorno.put("name", rs2.getString("DESC_PROD"));
		}

		out.print(retorno.toJSONString());
	}

	public static String getNomeProdIdProdDistr(Connection conn, long ID_PROD_DIST, boolean abreviado) throws Exception {

		String desc_produto = "";
		String varname1 = " select DESC_ABREVIADO,DESC_PROD from produtos inner join produtos_distribuidora on produtos_distribuidora.id_prod = produtos.id_prod where ID_PROD_DIST =  ? ";
		PreparedStatement st = conn.prepareStatement(varname1);
		st.setLong(1, ID_PROD_DIST);
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

	public static String getNomeDistr(Connection conn, long id_distribuidora, boolean abreviado) throws Exception {

		String desc_nome = "";
		String varname1 = " select * from distribuidora where id_distribuidora = ? ";
		PreparedStatement st = conn.prepareStatement(varname1);
		st.setLong(1, id_distribuidora);
		ResultSet rs2 = st.executeQuery();
		if (rs2.next()) {

			if (!abreviado) {
				desc_nome = rs2.getString("DESC_RAZAO_SOCIAL");
			} else {
				desc_nome = rs2.getString("DESC_NOME_ABREV");
			}
		} else {
			throw new Exception("Distribuidora não existe.");
		}

		return desc_nome;
	}

	public static java.sql.Timestamp getTimeStamp(Date data) {
		return new java.sql.Timestamp(data.getTime());
	}

	public static void copiaStream(InputStream in, OutputStream out, boolean blnFecharIn, boolean blnFecharOut) throws IOException {
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1)
						break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
		if (blnFecharIn) {
			in.close();
		}
		if (blnFecharOut) {
			out.close();
		}
	}

	public static void copiaStream(InputStream in, OutputStream out) throws IOException {
		copiaStream(in, out, false, false);
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

	private static final int maxsize = 100;

	public static void sizeimage(String cod) {
		try {
			// mudei direitorio para o m16_/***/produtos
			BufferedImage originalImage = ImageIO.read(new File("D:\\Program Files\\Mydocs\\Visual Studio 2015\\Projects\\chamaTrago\\chamaTrago\\www\\img\\prods\\" + cod + ".jpg"));
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			Double IMG_WIDTH = new Double(maxsize);
			Double IMG_HEIGHT = new Double(maxsize);

			BufferedImage resizedImage = ImageIO.read(new File("D:\\Program Files\\Mydocs\\Visual Studio 2015\\Projects\\chamaTrago\\chamaTrago\\www\\img\\prods\\" + cod + ".jpg"));
			int width = resizedImage.getWidth();
			int height = resizedImage.getHeight();

			if (width > maxsize || height > maxsize) {
				if (height > width) {

					double perc = (width * 100) / height;
					IMG_WIDTH = (maxsize * perc) / 100;

				} else if (height < width) {

					double perc = (height * 100) / width;
					IMG_HEIGHT = (maxsize * perc) / 100;

				}
				resizedImage = new BufferedImage(IMG_WIDTH.intValue(), IMG_HEIGHT.intValue(), type);

				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(originalImage, 0, 0, IMG_WIDTH.intValue(), IMG_HEIGHT.intValue(), null);
				g.dispose();

				ImageIO.write(resizedImage, "jpg", new File("D:\\Program Files\\Mydocs\\Visual Studio 2015\\Projects\\chamaTrago\\chamaTrago\\www\\img\\prodsmin\\" + cod + "_min.jpg"));

			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public static void resizeAllimage() throws Exception {
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			String varname1 = "";

			varname1 = " select * from produtos ";

			PreparedStatement st = conn.prepareStatement(varname1);
			ResultSet rs2 = st.executeQuery();
			while (rs2.next()) {
				try {
					sizeimage(rs2.getInt("ID_PROD") + "");
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void main(String[] args) {
		try {
			String jsonResponse;

			URL url = new URL("https://onesignal.com/api/v1/notifications");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Authorization", "Basic NmJmN2VhMmQtNjJkMi00OTRkLTg4M2UtODhhNjkzNzcyYTg5");
			con.setRequestMethod("POST");

			String strJsonBody = "{" + "\"app_id\": \"6b1103df-ebeb-43e1-860b-e2903a9b12ae\"," + "\"included_segments\": [\"All\"]," + "\"data\": {\"foo\": \"bar\"}," + "\"contents\": {\"en\": \"English Message\"}" + "}";

			System.out.println("strJsonBody:\n" + strJsonBody);

			byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);

			OutputStream outputStream = con.getOutputStream();
			outputStream.write(sendBytes);

			int httpResponse = con.getResponseCode();
			System.out.println("httpResponse: " + httpResponse);

			if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
				Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			} else {
				Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}
			System.out.println("jsonResponse:\n" + jsonResponse);

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * Connection conn =null; try { // sendEmail("g.kothe@hotmail.com", "aaaa", "Recuperação de senha"); conn = Conexao.getConexao(); // String validacao = Utilitario.StringGen(1000, 32).substring(0, 99); // Sys_parametros sys = new Sys_parametros(Conexao.getConexao()); // String texto = " Bem vindo ao TragoAqui, para validar seu e-mail clique no link abaixo: <br> " + sys.getUrl_system() + "mobile/ac=validar&token=" + validacao; // Utilitario.sendEmail("g.kothe@hotmail.com", texto, "Ativação da sua conta no TragoAqui!");
	 * 
	 * // System.out.println("Horario_1".toString().substring("Horario_1".toString().length() - 1, "Horario_1".toString().length())); // String hora = "1234";
	 * 
	 * // System.out.println(hora.substring(0, 2)); // System.out.println(hora.substring(2, 4));
	 * 
	 * String sql = " 	select * from pedido  where id_pedido = 1   ";
	 * 
	 * PreparedStatement st = conn.prepareStatement(sql);
	 * 
	 * ResultSet rs = st.executeQuery();
	 * 
	 * if (rs.next()) {
	 * 
	 * Date datatempoentregateste; Date datatempoentregateste2; try { datatempoentregateste = new SimpleDateFormat("HH:mm").parse("01:00"); datatempoentregateste2 = new SimpleDateFormat("HH:mm").parse(rs.getString("TEMPO_ESTIMADO_DESEJADO")); System.out.println(datatempoentregateste); System.out.println(datatempoentregateste2);
	 * 
	 * 
	 * } catch (Exception e) { throw new Exception("Tempo de entrega inválidos!"); }
	 * 
	 * if(datatempoentregateste.after(datatempoentregateste2)){ throw new Exception("Tempo de entrega é acima do desejado!"); }
	 * 
	 * } } catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
	 * 
	 * try { conn.close(); } catch (Exception e) { // TODO: handle exception } }
	 */

}
