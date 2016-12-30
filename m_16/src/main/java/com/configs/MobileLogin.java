package com.configs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.funcs.Sys_parametros;
import com.funcs.Utilitario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class MobileLogin {

	private static long tempotoken = 604800000; // 1 semana ou 1 mes, nao lembro

	public static void loginMobile(HttpServletRequest request, HttpServletResponse response, Connection conn, String user, String pass,Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String sql = "select  *, Coalesce(FLAG_MAIORIDADE,'N') as maior18 from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? and (FLAG_ATIVADO = 'S' or FLAG_ATIVADO = 'V')";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, user);
		st.setString(2, pass);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			objRetorno.put("msg", "ok");
			MobileLogin mob = new MobileLogin();
			objRetorno.put("token", mob.criaToken(user, pass, tempotoken, conn));//
			objRetorno.put("maioridade", rs.getString("maior18").equalsIgnoreCase("")?"N":rs.getString("maior18"));//
			objRetorno.put("email", rs.getString("DESC_EMAIL"));//
			if(rs.getLong("id_usuario")==sys.getId_usuario_admin()){
				objRetorno.put("usrtype","admin" );//
			}else if(rs.getLong("id_usuario")==sys.getSys_id_visistante()){
				objRetorno.put("usrtype","visitante" );//
				objRetorno.put("maioridade", "N");//
			}else{
				objRetorno.put("usrtype","user" );//
			} 
			
			

		} else {
			objRetorno.put("erro", "Login inválido!");
		}

		out.print(objRetorno.toJSONString());

	}

	public static void loginMobileFace(HttpServletRequest request, HttpServletResponse response, Connection conn, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String code = request.getParameter("zcode");

		/*
		 * GET graph.facebook.com/debug_token? input_token={token-to-inspect} &access_token={app-token-or-admin-token}
		 */

		// validacao1
		String url = "https://graph.facebook.com/v2.3/oauth/access_token?client_id=" + sys.getFACE_APP_ID() + "&redirect_uri=" + sys.getFACE_REDIRECT_URI() + "&client_secret=" + sys.getFACE_APP_SECRETKEY() + "&code=" + code;
		
		System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();

		if (responseCode == 400) {
			throw new Exception("Erro 400 na busca de credencias.");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer responsestr = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			responsestr.append(inputLine);
		}
		in.close();

		JSONObject json = (JSONObject) new JSONParser().parse(responsestr.toString());

		String tokentest = json.get("access_token").toString();

		// validacao2

		url = "https://graph.facebook.com/debug_token?input_token=" + tokentest + "&access_token=" + sys.getFACE_APP_TOKEN();
		obj = new URL(url);
		con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		responseCode = con.getResponseCode();

		if (responseCode == 400) {
			throw new Exception("Erro 400 na busca de credencias.");
		}

		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		responsestr = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			responsestr.append(inputLine);
		}
		in.close();

		json = (JSONObject) new JSONParser().parse(responsestr.toString());

		json = (JSONObject) json.get("data");
		// System.out.println(json.get("user_id"));
		// System.out.println(json.get("app_id"));
		long userid = Long.parseLong(json.get("user_id").toString());
		long app_id = Long.parseLong(json.get("app_id").toString());

		if (app_id != sys.getFACE_APP_ID()) {
			throw new Exception("Erro nas credenciais");
		}

		String sql = "select  *, Coalesce(FLAG_MAIORIDADE,'N') as maior18 from usuario where ID_USER_FACE = ?  and FLAG_FACEUSER = 'S'  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, userid);
		ResultSet rs = st.executeQuery();
		MobileLogin mob = new MobileLogin();

		if (rs.next()) {
			atualizaFaceUser(request, response, tokentest, conn, sys, userid);
			objRetorno.put("token", mob.criaToken(rs.getString("DESC_USER"), rs.getString("DESC_SENHA"), tempotoken, conn));//
			objRetorno.put("name", rs.getString("DESC_NOME").split(" ")[0]);
			objRetorno.put("usrtype","user" );//
			objRetorno.put("maioridade", rs.getString("maior18").equalsIgnoreCase("")?"N":rs.getString("maior18"));//
			objRetorno.put("email", rs.getString("DESC_EMAIL"));//
		} else {

			JSONObject info = cadastrausuario(request, response, tokentest, conn, sys);
			objRetorno.put("token", mob.criaToken(info.get("user").toString(), info.get("pass").toString(), tempotoken, conn));//
			objRetorno.put("name", info.get("name").toString());
			objRetorno.put("usrtype","user" );//
			objRetorno.put("maioridade", "N");//
			objRetorno.put("email", info.get("email"));//

		}
		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void atualizaFaceUser(HttpServletRequest request, HttpServletResponse response, String tokentest, Connection conn, Sys_parametros sys,long useridface) throws Exception {
		
		String url = "https://graph.facebook.com/me?fields=name,id,email&access_token=" + tokentest;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();

		if (responseCode == 400) {
			throw new Exception("Erro 400 na busca de credencias.");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer responsestr = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			responsestr.append(inputLine);
		}
		in.close();

		JSONObject json = (JSONObject) new JSONParser().parse(responsestr.toString());

		String name = json.get("name").toString();
		String id = json.get("id").toString();
		String email = json.get("email").toString();

		
		String sql = "update usuario set DESC_EMAIL = ? ,  DESC_NOME= ?, DESC_USER = ?  where  ID_USER_FACE = ?  ";

		PreparedStatement 	st = conn.prepareStatement(sql.toString());
		st.setString(1, email);
		st.setString(2, name);
		st.setString(3, email);
		st.setLong(4, Long.parseLong(id));
				
		st.executeUpdate();
		
	}
	
	private static JSONObject cadastrausuario(HttpServletRequest request, HttpServletResponse response, String tokentest, Connection conn, Sys_parametros sys) throws Exception {
		JSONObject objjson = new JSONObject();

		String url = "https://graph.facebook.com/me?fields=name,id,email&access_token=" + tokentest;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();

		if (responseCode == 400) {
			throw new Exception("Erro 400 na busca de credencias.");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer responsestr = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			responsestr.append(inputLine);
		}
		in.close();

		JSONObject json = (JSONObject) new JSONParser().parse(responsestr.toString());

		String name = json.get("name").toString();
		String id = json.get("id").toString();
		String email = json.get("email").toString();

		PreparedStatement st = conn.prepareStatement(" select * from usuario where DESC_EMAIL = ?  and FLAG_ATIVADO = 'S' ");
		st.setString(1, email);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {

			StringBuffer sql = new StringBuffer();
			sql.append("update usuario  set `FLAG_FACEUSER` = ? , `ID_USER_FACE` = ?, DESC_NOME= ?, FLAG_ATIVADO = ? , CHAVE_ATIVACAO = ?  where  DESC_EMAIL = ?  ");

			st = conn.prepareStatement(sql.toString());
			st.setString(1, "S");
			st.setLong(2, Long.parseLong(id));
			st.setString(3, name);
			st.setString(4, "S");
			st.setString(5, "");
			st.setString(6, email);
			
			st.executeUpdate();

			objjson.put("name", name.split(" ")[0]);
			objjson.put("pass", rs.getString("DESC_SENHA"));
			objjson.put("user", rs.getString("DESC_USER"));

		} else {

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO usuario (`DESC_NOME`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`,  `FLAG_FACEUSER`, `ID_USER_FACE`, FLAG_ATIVADO)  ");
			sql.append(" 	VALUES    (?,?,?,?,?,?,?) ");
			String pass = Utilitario.StringGen(130, 32);
			st = conn.prepareStatement(sql.toString());
			st.setString(1, name);
			st.setString(2, email);
			st.setString(3, pass);
			st.setString(4, email);
			st.setString(5, "S");
			st.setLong(6, Long.parseLong(id));
			st.setString(7, "S");

			st.executeUpdate();

			objjson.put("name", name.split(" ")[0]);
			objjson.put("pass", pass);
			objjson.put("user", email);
			objjson.put("email", email);

		}

		return objjson;
	}

	private String returnKey() {// nao esta sendo usado, server para gerar uma key. Qem sabe mudar a key quando liga o servidor?
		SecretKey secretKey = null;
		try {
			secretKey = KeyGenerator.getInstance("AES").generateKey();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} // get base64 encoded version of the key
		return Base64.getEncoder().encodeToString(secretKey.getEncoded());
	}

	// http://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
	private String criaToken(String user, String pass, long ttlMillis, Connection conn) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		Sys_parametros sys = new Sys_parametros(conn);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(sys.getDesc_key());
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

	public static long parseJWT(HttpServletRequest request, HttpServletResponse response, Connection conn, String jwt,Sys_parametros sys) throws Exception {

		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(sys.getDesc_key())).parseClaimsJws(jwt).getBody();
			String user = claims.getId();
			String pass = claims.getSubject();

			String sql = "select * from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? ";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
			st.setString(2, pass);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				long codusuer = rs.getLong("id_usuario");
				if(rs.getString("FLAG_ATIVADO").equalsIgnoreCase("v")){
					return sys.getSys_id_visistante();
				}
				if (rs.wasNull()) {// acho que nao tem como isso acontecer, mas por via das duvida...
					return 0;
				} else {
					return codusuer;
				}

			} else {
				throw new Exception("Dados de acesso inválidos");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Dados de acesso inválidos");
		}

	}
	
	
	public static void validarEmailNovo(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		boolean erro = false;
		String msg = "";
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);

			String token = request.getParameter("token") == null ? "" : request.getParameter("token");
		

		
			if (!token.equalsIgnoreCase("")) {
				msg = "Token inválido.";
				erro = true;

			}

			String sql = "select * from usuario where Binary CHAVE_ATIVACAO_NOVOEMAIL = ?   ";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, token);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				sql = "update usuario  set  CHAVE_ATIVACAO_NOVOEMAIL = ? ,  DESC_NOVOEMAILVALIDACAO = ? , DESC_EMAIL = ? where  id_usuario = ?  ";

				st = conn.prepareStatement(sql);
				st.setString(1, "");
				st.setString(2, "");
				st.setString(3, rs.getString("DESC_NOVOEMAILVALIDACAO"));
				st.setLong(4, rs.getLong("id_usuario"));
				st.executeUpdate();

				msg = "Seu e-mail foi verificado e atualizado! ";
				erro = false;
			} else {
				if (!token.equalsIgnoreCase("")) {
					msg = "Token inválido.";
					erro = true;
				}
			}
			
		

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
			}

			try {
				conn.close();
			} catch (Exception e2) {
			}
			erro = true;
			msg = e.getMessage();
			
		}
		
		try {
			
			
			if (erro)
				request.setAttribute("erro", erro);
			else
				request.setAttribute("erro", erro);
			

			request.setAttribute("msg",msg);
			request.getRequestDispatcher("/WEB-INF/msg.jsp").forward(request, response);
		} catch (Exception e2) {
		}
		

	}

	

	public static void validarConta(HttpServletRequest request, HttpServletResponse response) {

		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);

			String token = request.getParameter("token") == null ? "" : request.getParameter("token");
			String msg = "";

			boolean erro = false;
			if (!token.equalsIgnoreCase("")) {
				msg = "Token inválido.";
				erro = true;

			}

			String sql = "select * from usuario where Binary CHAVE_ATIVACAO = ?   ";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, token);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				sql = "update usuario  set `CHAVE_ATIVACAO` = ? ,  FLAG_ATIVADO = ?  where  CHAVE_ATIVACAO = ?  ";

				st = conn.prepareStatement(sql.toString());
				st.setString(1, "");
				st.setString(2, "S");
				st.setString(3, token);
				st.executeUpdate();

				msg = "Seu e-mail foi verificado e sua conta foi ativada. Você já pode logar no TragoAqui! :-)";
				erro = false;
			} else {
				if (!token.equalsIgnoreCase("")) {
					msg = "Token inválido.";
					erro = true;
				}
			}
			
			if (erro)
				request.setAttribute("erro", erro);
			else
				request.setAttribute("erro", erro);

			request.setAttribute("msg", msg);
			request.getRequestDispatcher("/WEB-INF/msg.jsp").forward(request, response);

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}

			try {
				conn.close();
			} catch (Exception e2) {
			}
		}

	}

}
