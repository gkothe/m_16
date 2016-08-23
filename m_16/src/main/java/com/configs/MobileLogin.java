package com.configs;

import java.io.PrintWriter;
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

import com.funcs.Sys_parametros;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class MobileLogin {
	
	
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
			MobileLogin mob = new MobileLogin();
			objRetorno.put("token", mob.criaToken(user, pass, 604800000, conn));// 1 semana ou 1 mes, nao lembro

		} else {
			objRetorno.put("erro", "Login inválido!");
		}

		out.print(objRetorno.toJSONString());

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
