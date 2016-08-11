package com.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.ajax.Home_ajax;
import com.ajax.Parametros_ajax;
import com.ajax.Pedidos_ajax;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import io.jsonwebtoken.*;
import java.util.Date;

@SuppressWarnings("unchecked")
@WebServlet(urlPatterns = { "/mobile" })
public class MobileController extends javax.servlet.http.HttpServlet {
	;
	private final String key = "LxLnKYU3QbR6HmLHCyAGKQ=="; // guardar no banco

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	public void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		try {

			System.out.println("----------entro mobile");

			Map map = request.getParameterMap();
			for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
				String type = (String) iterator.next();
				System.out.println(type + " : " + request.getParameter(type));
			}

			String strTipo = request.getParameter("ac"); // acho que aqui soh
															// vai ter ajax, mas
															// vo dexa assim por
															// enqto.
			if (strTipo == null) {
				strTipo = "ajax";
			}
			request.setCharacterEncoding("UTF-8");
			if (strTipo.equalsIgnoreCase("ajax")) {
				ajax(request, response);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				// exibeErro(request, response, ex);
			} catch (Exception e) {
			}
		}
	}

	public static void loginMobile(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		String user = request.getParameter("user");
		String pass = request.getParameter("pass");

		JSONObject objRetorno = new JSONObject();

		String sql = "select * from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, user);
		st.setString(2, pass);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			objRetorno.put("msg", "ok");
			MobileController mob = new MobileController();
			objRetorno.put("token", mob.criaToken(user, pass, 604800000));
			objRetorno.put("token2", mob.criaToken(user, pass, 604800000));
			// mob.parseJWT(mob.criaToken(user, pass, 604800000));

		} else {
			objRetorno.put("erro", "Login inválido!");
		}

		out.print(objRetorno.toJSONString());

	}

	private String returnKey() {

		SecretKey secretKey = null;
		try {
			secretKey = KeyGenerator.getInstance("AES").generateKey();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} // get base64 encoded version of the key

		return Base64.getEncoder().encodeToString(secretKey.getEncoded());

	}

	// http://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
	private String criaToken(String user, String pass, long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		// JwtBuilder builder =
		// Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer).signWith(signatureAlgorithm,
		// signingKey);

		JwtBuilder builder = Jwts.builder().setId(user).setSubject(pass).setIssuedAt(now).signWith(signatureAlgorithm, signingKey);
		// if it has been specified, let's add the expiration
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	private void parseJWT(HttpServletRequest request, HttpServletResponse response, Connection conn, String jwt) throws Exception {

		try {
			System.out.println(jwt);
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key)).parseClaimsJws(jwt.replaceFirst("\\.", "")).getBody();
			String user = claims.getId();
			String pass = claims.getSubject();

			String sql = "select * from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? ";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
			st.setString(2, pass);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return;
			} else {
				throw new Exception("Dados de acesso invalido");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Dados de acesso invalido");
		}

	}

	private void ajax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();

		response.setContentType("text/x-json; charset=UTF-8");
		response.setDateHeader("Expires", 0);
		response.setDateHeader("Last-Modified", new java.util.Date().getTime());
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST");
		response.setHeader("Access-Control-Allow-Origin", "*");

		Connection conn = null;
		JSONObject objRetorno = new JSONObject();

		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd");

			if (cmd.equalsIgnoreCase("login")) {
				loginMobile(request, response, conn);
			} else {
				parseJWT(request, response, conn, request.getHeader("X-Auth-Token"));

				if (cmd.equalsIgnoreCase("teste")) {
					getalgo(request, response, conn);
				} else if (cmd.equalsIgnoreCase("")) {

				}

			}

			conn.commit();
		} catch (Exception ex) {
			if (ex.getMessage() == null || ex.getMessage().equals("")) {
				objRetorno.put("erro", "Erro, por favor entrar em contato com suporte.");
			} else {
				objRetorno.put("erro", ex.getMessage());
			}

			ex.printStackTrace();
			out.print(objRetorno.toJSONString());
			try {
				conn.rollback();
			} catch (Exception exr) {
			}
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void getalgo(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		objRetorno.put("msg", "yoooo");
		objRetorno.put("myman", "hood");
		out.print(objRetorno.toJSONString());

	}

	public static void main(String[] args) {
		try {

			SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
			// get base64 encoded version of the key
			String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

			System.out.println(encodedKey);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}