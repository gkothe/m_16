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
import com.ajax.Utilitario;
import com.mercadopago.MP;

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
			objRetorno.put("token", mob.criaToken(user, pass, 604800000));// 1
																			// semana
																			// ou
																			// 1
																			// mes,
																			// nao
																			// lembro

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

	private String parseJWT(HttpServletRequest request, HttpServletResponse response, Connection conn, String jwt) throws Exception {

		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key)).parseClaimsJws(jwt.replaceFirst("\\.", "")).getBody();
			String user = claims.getId();
			String pass = claims.getSubject();

			String sql = "select * from usuario where Binary DESC_USER = ?  and Binary DESC_SENHA = ? ";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
			st.setString(2, pass);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getString("id_usuario");
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
			} else if (cmd.equalsIgnoreCase("inserir_user")) {

			} else {
				String cod_usuario = parseJWT(request, response, conn, request.getHeader("X-Auth-Token"));

				if (cmd.equalsIgnoreCase("update_user")) {
					updateUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_user")) {
					carregaUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_bairros")) {
					carregaBairros(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("inserir_user")) {
					inserirUser(request, response, conn);
				} else if (cmd.equalsIgnoreCase("doOrder")) {
					payment(request, response, conn);
				} else if (cmd.equalsIgnoreCase("carregaProdutos")) {
					carregaProdutos(request, response, conn, cod_usuario, true);
				} else if (cmd.equalsIgnoreCase("detalheProdutos")) {
					carregaProdutos(request, response, conn, cod_usuario, false);
				} else if (cmd.equalsIgnoreCase("carregaPedidos")) {
					carregaPedidos(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaPedidoUnico")) {
					carregaPedidoUnico(request, response, conn, cod_usuario);
				} else {
					throw new Exception("Ação invalida");
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

	private static void payment(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		MP mp = new MP("TEST-3928083096731492-072914-2aa78c35c6f210a6322c4acf9abe4d14__LD_LC__-222772872");

		String token = request.getParameter("token");
		String email = request.getParameter("email");
		String paymentMethodId = request.getParameter("paymentMethodId");

		org.codehaus.jettison.json.JSONObject payment = mp.post("/v1/payments", "{" + "'transaction_amount': 100," + "'token': " + token + "," + "'description': 'Title of what you are paying for'," + "'installments': 1," + "'payment_method_id': '" + paymentMethodId + "'," + "'payer': {" + "'email': '" + email + "'" + "}" + "}");

		System.out.println(payment);

		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void inserirUser(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();
		String desc_usuario = request.getParameter("c_desc_usuario") == null ? "" : request.getParameter("c_desc_usuario");
		String desc_senha = request.getParameter("c_desc_senha") == null ? "" : request.getParameter("c_desc_senha");
		String desc_senha_conf = request.getParameter("c_desc_senha_conf") == null ? "" : request.getParameter("c_desc_senha_conf");
		String desc_email = request.getParameter("c_desc_email") == null ? "" : request.getParameter("c_desc_email");
		String desc_nome = request.getParameter("c_desc_nome") == null ? "" : request.getParameter("c_desc_nome");
		String desc_telefone = request.getParameter("c_desc_telefone") == null ? "" : request.getParameter("c_desc_telefone");
		String cod_cidade = request.getParameter("c_cod_cidade") == null ? "" : request.getParameter("c_cod_cidade");

		String cod_bairro = request.getParameter("c_cod_bairro") == "" ? null : request.getParameter("c_cod_bairro");
		String desc_endereco = request.getParameter("c_desc_endereco") == null ? "" : request.getParameter("c_desc_endereco");

		if (desc_usuario.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de usuário.");
		}

		if (desc_senha.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de senha.");
		}

		if (desc_email.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de email.");
		}

		if (desc_senha.equalsIgnoreCase(desc_senha_conf)) {
			throw new Exception("Erro. As senhas são diferentes.");
		}

		PreparedStatement st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_user = ? ");
		st.setString(1, desc_usuario);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			throw new Exception("Nome de usuário ja existente!");
		}

		st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		rs = st.executeQuery();

		if (rs.next()) {
			throw new Exception("Email já cadastrado!");
		}

		st = conn.prepareStatement("SELECT 1 from  cidade where   COD_CIDADE = ? ");
		st.setInt(1, Integer.parseInt(cod_cidade));

		rs = st.executeQuery();

		if (!rs.next()) {
			throw new Exception("Cidade inválida!");
		}

		String sql = "INSERT INTO usuario ( `DESC_NOME`, `DESC_TELEFONE`, `COD_BAIRRO`, `COD_CIDADE`, `DESC_ENDERECO`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`) VALUES (?,?,?,?,?,?,?,?)";
		st = conn.prepareStatement(sql);
		st.setString(1, desc_nome);
		st.setString(2, desc_telefone);
		st.setInt(3, Integer.parseInt(cod_bairro));
		st.setInt(4, Integer.parseInt(cod_cidade));
		st.setString(5, desc_endereco);
		st.setString(6, desc_usuario);
		st.setString(7, desc_senha);
		st.setString(8, desc_email);
		st.executeUpdate();

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	private static void updateUser(HttpServletRequest request, HttpServletResponse response, Connection conn, String cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
			throw new Exception("Usuário inválido.");
		} else {

			String desc_senha = request.getParameter("c_password") == null ? "" : request.getParameter("c_password");
			String desc_senha_conf = request.getParameter("c_desc_senha_conf") == null ? "" : request.getParameter("c_desc_senha_conf");
			String desc_email = request.getParameter("c_email") == null ? "" : request.getParameter("c_email");
			String desc_nome = request.getParameter("c_nome") == null ? "" : request.getParameter("c_nome");
			String desc_telefone = request.getParameter("c_telefone") == null ? "" : request.getParameter("c_telefone");
			String cod_cidade = request.getParameter("c_cidade") == null ? "" : request.getParameter("c_cidade");
			String cod_bairro = request.getParameter("c_bairro") == "" ? null : request.getParameter("c_bairro");
			String desc_endereco = request.getParameter("c_endereco") == null ? "" : request.getParameter("c_endereco");
			String desc_cartao = request.getParameter("c_numcartao") == null ? "" : request.getParameter("c_numcartao");

			if (desc_senha.equalsIgnoreCase("")) {
				throw new Exception("Você deve preencher o campo de senha.");
			}

			if (desc_email.equalsIgnoreCase("")) {
				throw new Exception("Você deve preencher o campo de email.");
			}

			if (!desc_senha.equalsIgnoreCase(desc_senha_conf)) {
				throw new Exception("Erro. As senhas são diferentes.");
			}

			PreparedStatement st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_email = ? and id_usuario != ?  ");
			st.setString(1, desc_email);
			st.setInt(2, Integer.parseInt(cod_usuario));
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				throw new Exception("Email já cadastrado!.");
			}

			st = conn.prepareStatement("SELECT 1 from  cidade where   COD_CIDADE = ?   ");
			st.setInt(1, Integer.parseInt(cod_cidade));
			rs = st.executeQuery();
			if (!rs.next()) {
				throw new Exception("Cidade inválida!");
			}

			String sql = " UPDATE usuario SET `DESC_NOME` = ?, `DESC_TELEFONE` = ?, `COD_BAIRRO` = ?, `COD_CIDADE` = ?, `DESC_ENDERECO` = ?, `DESC_SENHA` = ?, `DESC_EMAIL` = ? , desc_cartao = ? WHERE `ID_USUARIO` = ? ";
			st = conn.prepareStatement(sql);
			st.setString(1, desc_nome);
			st.setString(2, desc_telefone);
			st.setInt(3, Integer.parseInt(cod_bairro));
			st.setInt(4, Integer.parseInt(cod_cidade));
			st.setString(5, desc_endereco);
			st.setString(6, desc_senha);
			st.setString(7, desc_email);
			st.setString(8, desc_cartao);
			st.setInt(9, Integer.parseInt(cod_usuario));
			st.executeUpdate();

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void carregaUser(HttpServletRequest request, HttpServletResponse response, Connection conn, String cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
			throw new Exception("Usuário inválido.");
		} else {

			PreparedStatement st = conn.prepareStatement(" select * from usuario  where `ID_USUARIO` = ?  ");
			st.setInt(1, Integer.parseInt(cod_usuario));
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				objRetorno.put("ID_USUARIO", rs.getString("ID_USUARIO"));
				objRetorno.put("DESC_NOME", rs.getString("DESC_NOME"));
				objRetorno.put("DESC_TELEFONE", rs.getString("DESC_TELEFONE"));
				objRetorno.put("COD_BAIRRO", rs.getString("COD_BAIRRO"));
				objRetorno.put("COD_CIDADE", rs.getString("COD_CIDADE"));
				objRetorno.put("DESC_ENDERECO", rs.getString("DESC_ENDERECO"));
				objRetorno.put("DESC_USER", rs.getString("DESC_USER"));
				objRetorno.put("DESC_SENHA", rs.getString("DESC_SENHA"));
				objRetorno.put("DESC_EMAIL", rs.getString("DESC_EMAIL"));
				objRetorno.put("ID_CIDADE", rs.getString("ID_CIDADE"));
				objRetorno.put("DESC_CARTAO", rs.getString("DESC_CARTAO"));

			}

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void carregaBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, String cod_usuario) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String sql = "SELECT cod_cidade from  usuario where id_usuario  = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(cod_usuario));
		ResultSet rs = st.executeQuery();
		int codcidade = 0;
		while (rs.next()) {

			codcidade = rs.getInt("cod_cidade");
		}

		st = conn.prepareStatement("SELECT cod_bairro, desc_bairro from bairros where cod_cidade  = ? order by desc_bairro asc");
		st.setInt(1, codcidade);
		rs = st.executeQuery();

		while (rs.next()) {
			JSONObject bairro = new JSONObject();
			bairro.put("cod_bairro", rs.getString("cod_bairro"));
			bairro.put("desc_bairro", rs.getString("desc_bairro"));

			retorno.add(bairro);
		}

		out.print(retorno.toJSONString());
	}

	private static void carregaProdutos(HttpServletRequest request, HttpServletResponse response, Connection conn, String cod_usuario, boolean listagem) throws Exception {

		PrintWriter out = response.getWriter();

		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String distribuidora = request.getParameter("distribuidora") == null ? "" : request.getParameter("distribuidora"); // pesquisar a partir de produtos no carrinho ou algo do tipo //TODO
		String desc_pesquisa = request.getParameter("desc_pesquisa") == null ? "" : request.getParameter("desc_pesquisa");
		String valini = request.getParameter("val_ini") == null ? "" : request.getParameter("val_ini");
		String valfim = request.getParameter("val_fim") == null ? "" : request.getParameter("val_fim");
		String idproduto = request.getParameter("idproduto") == null ? "" : request.getParameter("idproduto");
		
		
		if(cod_bairro.equalsIgnoreCase("")){
			throw new Exception("Nenhum bairro informado!");
		}
		

		StringBuffer sql = new StringBuffer();
		sql.append(" select ");
		sql.append(" produtos.ID_PROD, ");
		sql.append(" DESC_PROD, ");
		sql.append(" DESC_ABREVIADO, ");
		sql.append(" produtos_distribuidora.VAL_PROD, ");
		sql.append(" distribuidora.ID_DISTRIBUIDORA, ");
		sql.append(" produtos_distribuidora.ID_PROD_DIST, ");
		sql.append(" DESC_NOME_ABREV ");
		sql.append(" from produtos ");
		sql.append(" inner join produtos_distribuidora ");
		sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append(" inner join distribuidora ");
		sql.append(" on produtos_distribuidora.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" inner join distribuidora_bairro_entrega ");
		sql.append(" on distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" inner join distribuidora_horario_dia_entre ");
		sql.append(" on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO   = distribuidora_bairro_entrega.ID_DISTR_BAIRRO");
		sql.append(" where produtos_distribuidora.FLAG_ATIVO = 'S' and distribuidora.FLAG_ATIVO_MASTER ='S' and distribuidora.FLAG_ATIVO='S' and cod_bairro = ?  and cod_dia = ? and ? between horario_ini and horario_fim ");
		if (listagem) {// se for listagem de todos produtos ou de um especifico, tela de detalhes.
			sql.append(" and desc_prod  like ? ");
		} else {
			sql.append(" and produtos_distribuidora.id_prod  =  ?  ");
		}

		if (!distribuidora.equalsIgnoreCase("")) {
			sql.append(" and id_distribuidora = ?");
		}

		if (!valini.equalsIgnoreCase("")) {
			sql.append("  and  produtos_distribuidora.VAL_PROD >= ? ");
		}

		if (!valfim.equalsIgnoreCase("")) {
			sql.append("  and  produtos_distribuidora.VAL_PROD <= ? ");
		}

		sql.append("  order by  data_pedido desc limit 20 ");

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, Integer.parseInt(cod_bairro));

		st.setInt(2, new GregorianCalendar().get(Calendar.DAY_OF_WEEK) == 1 ? 7 : new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1); // de acordo com o que ta no banco de dados, se for 1 vai ser domingo (codigo 7 no banco), resto é o dia menos 1.
		st.setString(3, sdf.format(cal.getTime()));

		if (listagem) {
			st.setString(4, "%" + desc_pesquisa + "%");
		} else {
			st.setInt(4, Integer.parseInt(idproduto));
		}

		int contparam = 5;
		if (!distribuidora.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(distribuidora));
			contparam++;
		}

		if (!valini.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(valini));
			contparam++;
		}

		if (!valfim.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(valfim));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		JSONArray prods = new JSONArray();
		while (rs.next()) {
			JSONObject prod = new JSONObject();
			prod.put("ID_PROD", rs.getString("ID_PROD"));
			prod.put("DESC_PROD", rs.getString("DESC_PROD"));
			prod.put("DESC_ABREVIADO", rs.getString("DESC_ABREVIADO"));// abreviado do produto
			prod.put("VAL_PROD", rs.getString("VAL_PROD"));// TODO trazer formatado se nao conseguir tratar no front end.
			prod.put("ID_DISTRIBUIDORA", rs.getString("ID_DISTRIBUIDORA"));
			prod.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));/// abreviado da distribuidora

			prods.add(prod);
		}

		System.out.println(prods.toJSONString());
		out.print(prods.toJSONString());
	}

	private static void carregaPedidos(HttpServletRequest request, HttpServletResponse response, Connection conn, String cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();

		String numero = request.getParameter("numero") == null ? "" : request.getParameter("numero");
		String dataini = request.getParameter("dataini") == null ? "" : request.getParameter("dataini");
		String datafim = request.getParameter("datafim") == null ? "" : request.getParameter("datafim");
		String valini = request.getParameter("val_ini") == null ? "" : request.getParameter("val_ini");
		String valfim = request.getParameter("val_fim") == null ? "" : request.getParameter("val_fim");
		String flagsituacao = request.getParameter("flagsituacao") == null ? "" : request.getParameter("flagsituacao");
		String distribuidora = request.getParameter("flagsituacao") == null ? "" : request.getParameter("flagsituacao");
		String idproduto = request.getParameter("idproduto") == null ? "" : request.getParameter("idproduto");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");

		StringBuffer sql = new StringBuffer();
		sql.append(" select NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,DESC_NOME_ABREV,id_pedido from pedido ");
		sql.append(" inner join distribuidora ");
		sql.append(" on distribuidora.ID_DISTRIBUIDORA  = pedido.ID_DISTRIBUIDORA ");

		if (!numero.equalsIgnoreCase("")) {
			sql.append(" and numero = ? ");
		}

		if (!(dataini.equalsIgnoreCase(""))) {
			sql.append("  and  data_pedido >= ?");
		}

		if (!(datafim.equalsIgnoreCase("")) && datafim != null) {
			sql.append(" and  data_pedido <= ?");
		}

		if (!valini.equalsIgnoreCase("")) {
			sql.append("  and  VAL_TOTALPROD >= ? ");
		}

		if (!valfim.equalsIgnoreCase("")) {
			sql.append("  and  VAL_TOTALPROD <= ? ");
		}

		if (!flagsituacao.equalsIgnoreCase("")) {
			sql.append("  and  FLAG_STATUS = ? ");
		}

		if (!distribuidora.equalsIgnoreCase("")) {
			sql.append("  and  pedido.ID_DISTRIBUIDORA = ? ");
		}

		if (!idproduto.equalsIgnoreCase("")) {
			sql.append(" and exists (select 1 from pedido_item where pedido_item.id_pedido  = 	pedido.id_pedido and id_prod = ? )");
		}

		if (!cod_bairro.equalsIgnoreCase("")) {
			sql.append("  and  pedido.cod_bairro = ? ");
		}

		sql.append("  and  id_usuario = ? ");

		sql.append("  order by  data_pedido desc limit 20 ");
		System.out.println(sql.toString());
		PreparedStatement st = conn.prepareStatement(sql.toString());

		int contparam = 1;

		if (!numero.equalsIgnoreCase("")) {
			st.setLong(contparam, Long.parseLong(numero));
			contparam++;
		}

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

		if (!valini.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(valini));
			contparam++;
		}

		if (!valfim.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(valfim));
			contparam++;
		}

		if (!flagsituacao.equalsIgnoreCase("")) {
			st.setString(contparam, flagsituacao);
			contparam++;
		}

		if (!distribuidora.equalsIgnoreCase("")) {
			st.setLong(contparam, Long.parseLong(distribuidora));
			contparam++;
		}

		if (!idproduto.equalsIgnoreCase("")) {
			st.setLong(contparam, Long.parseLong(idproduto));
			contparam++;
		}

		if (!cod_bairro.equalsIgnoreCase("")) {
			st.setLong(contparam, Long.parseLong(cod_bairro));
			contparam++;
		}

		st.setLong(contparam, Long.parseLong(cod_usuario));

		ResultSet rs = st.executeQuery();
		JSONArray prods = new JSONArray();
		while (rs.next()) {

			// NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,DESC_NOME_ABREV,id_pedido

			JSONObject prod = new JSONObject();
			prod.put("NUM_PED", rs.getString("NUM_PED"));
			prod.put("DATA_PEDIDO", rs.getString("DATA_PEDIDO"));
			prod.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			prod.put("FLAG_STATUS", rs.getString("FLAG_STATUS")); // TODO
			prod.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));
			prod.put("id_pedido", rs.getString("id_pedido"));

			prods.add(prod);
		}

		System.out.println(prods.toJSONString());
		out.print(prods.toJSONString());
	}

	private static void carregaPedidoUnico(HttpServletRequest request, HttpServletResponse response, Connection conn, String cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido");

		if (!Utilitario.isNumeric(id_pedido)) {
			throw new Exception("Pedido inválido!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT val_entrega, ");
		sql.append("       num_ped, ");
		sql.append("       val_totalprod, ");
		sql.append("       desc_razao_social, ");
		sql.append("       desc_bairro, ");
		sql.append("       desc_endereco_entrega, ");
		sql.append("       num_telefonecontato_cliente, ");
		sql.append("       tempo_estimado_entrega, ");
		sql.append("       data_pedido, ");
		sql.append("       data_pedido_resposta, ");
		sql.append("       DESC_NOME_ABREV, ");
		sql.append("       flag_status,id_pedido ");
		sql.append("FROM   pedido ");
		sql.append("       INNER JOIN distribuidora ");
		sql.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
		sql.append("       INNER JOIN bairros ");
		sql.append("               ON bairros.cod_bairro = pedido.cod_bairro ");
		sql.append("WHERE  id_pedido = ? ");
		sql.append("       AND id_usuario = ?");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_pedido));
		st.setLong(2, Long.parseLong(cod_usuario));

		ResultSet rs = st.executeQuery();
		JSONObject ped = new JSONObject();
		if (rs.next()) {

			// NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,DESC_NOME_ABREV,id_pedido
			ped.put("NUM_PED", rs.getString("NUM_PED"));
			ped.put("DATA_PEDIDO", rs.getString("DATA_PEDIDO"));
			ped.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			ped.put("FLAG_STATUS", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
			ped.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));
			ped.put("id_pedido", rs.getString("id_pedido"));
			ped.put("desc_razao_social", rs.getString("desc_razao_social"));

			StringBuffer sql2 = new StringBuffer();
			sql2.append("select DESC_PROD, VAL_UNIT, QTD_PROD, QTD_PROD * VAL_UNIT   as total, DESC_ABREVIADO, produtos.id_prod from pedido_item ");
			sql2.append("inner join produtos ");
			sql2.append("on produtos.ID_PROD  = pedido_item.ID_PROD ");
			sql2.append("where id_pedido = ? ");

			JSONArray produtos = new JSONArray();

			PreparedStatement st2 = conn.prepareStatement(sql2.toString());
			st2.setLong(1, Long.parseLong(id_pedido));
			ResultSet rs2 = st2.executeQuery();
			
			while (rs2.next()) {
				JSONObject prod = new JSONObject();

				prod.put("DESC_PROD", rs2.getString("DESC_PROD"));
				prod.put("VAL_UNIT", rs2.getString("VAL_UNIT"));
				prod.put("QTD_PROD", rs2.getString("QTD_PROD"));
				prod.put("total", rs2.getString("total"));
				prod.put("id_prod", rs2.getString("id_prod"));
				prod.put("DESC_ABREVIADO", rs2.getString("DESC_ABREVIADO"));

				produtos.add(prod);
			}

			ped.put("produtos", produtos);

		} else {
			throw new Exception("Pedido não encontrado!");
		}

		System.out.println(ped.toJSONString());
		out.print(ped.toJSONString());
	}

	public static void main(String[] args) {
		try {

			/*
			 * SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey(); // get base64 encoded version of the key String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
			 * 
			 * System.out.println(encodedKey);
			 */

			///System.out.println(new GregorianCalendar().get(Calendar.DAY_OF_WEEK));

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}