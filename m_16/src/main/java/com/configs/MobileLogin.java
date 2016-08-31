package com.configs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class MobileLogin {

	
	private static long tempotoken = 604800000; // 1 semana ou 1 mes, nao lembro
	
	public static void loginMobile(HttpServletRequest request, HttpServletResponse response, Connection conn, String user, String pass) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String sql = "select * from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, user);
		st.setString(2, pass);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			objRetorno.put("msg", "ok");
			MobileLogin mob = new MobileLogin();
			objRetorno.put("token", mob.criaToken(user, pass, tempotoken, conn));//

		} else {
			objRetorno.put("erro", "Login inválido!");
		}

		out.print(objRetorno.toJSONString());

	}

	public static void loginMobileFace(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		Sys_parametros sys = new Sys_parametros(conn);

		String code = request.getParameter("zcode");

		/*
		 * GET graph.facebook.com/debug_token? input_token={token-to-inspect} &access_token={app-token-or-admin-token}
		 */

		// validacao1
		String url = "https://graph.facebook.com/v2.3/oauth/access_token?client_id=" + sys.getFACE_APP_ID() + "&redirect_uri=" + sys.getFACE_REDIRECT_URI() + "&client_secret=" + sys.getFACE_APP_SECRETKEY() + "&code=" + code;
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

		
		System.out.println(tokentest);
		String sql = "select * from usuario where ID_USER_FACE = ?  and FLAG_FACEUSER = 'S'  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, userid);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			objRetorno.put("msg", "ok");
			MobileLogin mob = new MobileLogin();
			objRetorno.put("token", mob.criaToken(rs.getString("DESC_USER"), rs.getString("DESC_SENHA"), tempotoken, conn));//
			
		}else{
			
			cadastrausuario(request, response, tokentest);
		}

		

		out.print(objRetorno.toJSONString());

	}
	
	private  static JSONObject cadastrausuario(HttpServletRequest request, HttpServletResponse response, String tokentest) throws Exception {
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
		
		
		System.out.println(name);
		System.out.println(id);
		System.out.println(email);
		
		/*INSERT INTO usuario
		  (`DESC_NOME`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, `COD_CIDADE`, `FLAG_FACEUSER`, `ID_USER_FACE`)
		VALUES
		  (?);*/
		
		//TODO
		
		
		objjson.put("","");
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

	public static long parseJWT(HttpServletRequest request, HttpServletResponse response, Connection conn, String jwt) throws Exception {

		try {
			Sys_parametros sys = new Sys_parametros(conn);
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(sys.getDesc_key())).parseClaimsJws(jwt.replaceFirst("\\.", "")).getBody();
			String user = claims.getId();
			String pass = claims.getSubject();

			String sql = "select * from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? ";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
			st.setString(2, pass);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				long codusuer = rs.getLong("id_usuario");
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

}
