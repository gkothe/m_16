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
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.funcs.Sys_parametros;
import com.funcs.Utilitario;
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	public void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		try {

			System.out.println("----------entro mob");

			Map map = request.getParameterMap();
			for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
				String type = (String) iterator.next();
				System.out.println(type + " : " + request.getParameter(type));
			}

			String strTipo = request.getParameter("ac"); // acho que aqui soh vai ter ajax, mas vo dexa assim por enqto.
			if (strTipo == null) {
				strTipo = "ajax";
			}
			request.setCharacterEncoding("UTF-8");
			if (strTipo.equalsIgnoreCase("ajax")) {
				ajax(request, response);
			} else if (strTipo.equalsIgnoreCase("validar")) {
				MobileLogin.validarConta(request, response);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				// exibeErro(request, response, ex);
			} catch (Exception e) {
			}
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
				String flag_face = request.getParameter("flag_face") == null ? "" : request.getParameter("flag_face");

				if (!(flag_face.equalsIgnoreCase("S")) && !(flag_face.equalsIgnoreCase("N"))) {
					throw new Exception("Parâmetros de de login incorretos, ta metendo a mão onde não deve mané! ");
				}

				if (flag_face.equalsIgnoreCase("S")) {
					MobileLogin.loginMobileFace(request, response, conn);
				} else {
					String user = request.getParameter("user");
					String pass = request.getParameter("pass");
					MobileLogin.loginMobile(request, response, conn, user, pass);
				}

			} else if (cmd.equalsIgnoreCase("inserir_user")) {

				inserirUser(request, response, conn);

			} else if (cmd.equalsIgnoreCase("rec_senha")) {

				recSenha(request, response, conn);

			} else {

				long cod_usuario = MobileLogin.parseJWT(request, response, conn, request.getHeader("X-Auth-Token"));

				if (cod_usuario == 0) {
					throw new Exception("Usuário inválido");
				}

				Sys_parametros sys = new Sys_parametros(conn);
				if (sys.getFlag_manutencao().equalsIgnoreCase("S") && sys.getId_usuario_admin() != cod_usuario) {
					throw new Exception("Servidor está em manutenção. Tente novamente mais tarde. Cheers! ");
				}

				if (cmd.equalsIgnoreCase("carregaProdutos")) {
					carregaProdutos(request, response, conn, cod_usuario, true);
				} else if (cmd.equalsIgnoreCase("detalheProdutos")) {
					carregaProdutos(request, response, conn, cod_usuario, false);
				} else if (cmd.equalsIgnoreCase("carrega_bairros")) {
					carregaBairros(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_cidade")) {
					carregaCidade(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaLocationUser")) {
					carregaLocationUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("detalheProdutos")) {
					carregaProdutos(request, response, conn, cod_usuario, false);
				} else if (cmd.equalsIgnoreCase("saveLocationUser")) {
					saveLocationUser(request, response, conn, cod_usuario);
				} else if (cod_usuario == -1) {// operações daqui para cima, são validas para visitante.
					objRetorno.put("guest", "true");
					throw new Exception("Você está acessando como visitante. Para poder realizar esta operação você deve criar uma conta no ChamaTrago.");
				} else if (cmd.equalsIgnoreCase("carregaPayCreditIds")) {
					carregaPayCreditIds(request, response, conn);
				}else if (cmd.equalsIgnoreCase("save_user")) {
					updateUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_user")) {
					carregaUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("doOrder")) {
					payment(request, response, conn);
				} else if (cmd.equalsIgnoreCase("carregaPedidos")) {
					carregaPedidos(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaPedidoUnico")) {
					carregaPedidoUnico(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaCarrinho")) {
					carregaCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("addCarrinho")) {
					addCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("criarPedido")) {
					criarPedido(request, response, conn, cod_usuario);
				} else {
					throw new Exception("Ação inválida");
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
	
	private static void carregaPayCreditIds(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		PrintWriter out = response.getWriter();
		out.print(Utilitario.payments_ids().toJSONString());
	}

	

	private static void inserirUser(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		// TODO tela de inserir nao foi definada ainda como vai ser.
		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();
		String desc_usuario = request.getParameter("c_username") == null ? "" : request.getParameter("c_username");
		String desc_senha = request.getParameter("c_password") == null ? "" : request.getParameter("c_password");
		String desc_senha_conf = request.getParameter("c_passwordconfirm") == null ? "" : request.getParameter("c_passwordconfirm");
		String desc_email = request.getParameter("c_email") == null ? "" : request.getParameter("c_email");
		String desc_nome = request.getParameter("c_nome") == null ? "" : request.getParameter("c_nome");

		if (desc_usuario.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de usuário.");
		}

		if (desc_senha.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de senha.");
		}

		if (desc_email.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de email.");
		}

		if (!desc_senha.equalsIgnoreCase(desc_senha_conf)) {
			throw new Exception("Erro. As senhas são diferentes.");
		}

		PreparedStatement st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_user = ? ");
		st.setString(1, desc_usuario);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			throw new Exception("Nome de usuário ja existente!");
		}

		st = conn.prepareStatement("SELECT * from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		rs = st.executeQuery();

		if (rs.next()) {
			if (rs.getString("FLAG_FACEUSER").equalsIgnoreCase("S")) {
				throw new Exception("Este e-mail já está relacionado a uma conta do Facebook. Se você não possui mais uma conta de Facebook, você pode voltar a tela de login e clicar que recuperar senha.");
			} else {
				throw new Exception("E-mail já cadastrado! Se você se esqueceu de sua senha, volte a tela de login e clique em recuperar senha.");
			}
		}

		String validacao = Utilitario.StringGen(1000, 32).substring(0, 99);

		st = conn.prepareStatement("SELECT * from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		rs = st.executeQuery();

		if (rs.next()) {
			if (rs.getString("FLAG_FACEUSER").equalsIgnoreCase("F")) {
				throw new Exception("Este e-mail já está relacionado a uma conta do Facebook. Se você não possui mais uma conta de Facebook, você pode voltar a tela de login e clicar que recuperar senha");
			} else {
				throw new Exception("E-mail já cadastrado! Se você se esqueceu de sua senha, volte a tela de login e clique em recuperar senha.");
			}
		}

		String sql = "INSERT INTO usuario ( `DESC_NOME`,    `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, FLAG_FACEUSER, FLAG_ATIVADO, `CHAVE_ATIVACAO` ) VALUES (?,?,?,?,?,?,?)";
		st = conn.prepareStatement(sql);
		st.setString(1, desc_nome);
		st.setString(2, desc_usuario);
		st.setString(3, desc_senha);
		st.setString(4, desc_email);
		st.setString(5, "N");
		st.setString(6, "N");
		st.setString(7, validacao);

		st.executeUpdate();

		Sys_parametros sys = new Sys_parametros(conn);

		String texto = " Bem vindo ao ChamaTrago, para validar seu e-mail clique no link abaixo: /n " + sys.getUrl_system() + "/mobile?ac=validar&token=" + validacao;

		Utilitario.sendEmail(desc_email, texto, "Ativação da sua conta no ChamaTrago!", conn);

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	private static void recSenha(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		// TODO tela de inserir nao foi definada ainda como vai ser.
		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String desc_email = request.getParameter("c_email") == null ? "" : request.getParameter("c_email");

		if (desc_email.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de email.");
		}

		PreparedStatement st = conn.prepareStatement("SELECT * from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			String texto = " Olá, seguem abaixo suas informações de acesso: <br> Usuário: " + rs.getString("DESC_User") + " <br> Senha: " + rs.getString("DESC_SENHA");
			Utilitario.sendEmail(desc_email, texto, "Recuperação de informações de acesso do ChamaTrago!", conn);
			objRetorno.put("msg", "ok");

		} else {
			throw new Exception("E-mail não encontrado.");
		}

		out.print(objRetorno.toJSONString());

	}

	private static void saveLocationUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		// TODO tela de inserir nao foi definada ainda como vai ser.
		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String codbairro = request.getParameter("codbairro") == null ? "" : request.getParameter("codbairro");
		String codcidade = request.getParameter("codcidade") == null ? "" : request.getParameter("codcidade");

		String sql = "SELECT COD_CIDADE from  cidade where COD_CIDADE  = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, Long.parseLong(codcidade));
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Cidade não encontrada. Você deve escolher uma cidade válida.");
		}

		st = conn.prepareStatement("SELECT * from bairros where cod_cidade  = ? and cod_bairro = ? ");
		st.setLong(1, Long.parseLong(codcidade));
		st.setLong(2, Long.parseLong(codbairro));
		rs = st.executeQuery();

		if (!rs.next()) {
			throw new Exception("Bairro não encontrado. Você deve escolher um bairro válido.");
		}

		StringBuffer sql2 = new StringBuffer();
		sql2.append("UPDATE usuario ");
		sql2.append("   SET  ");
		sql2.append("       `COD_BAIRRO` = ?, ");
		sql2.append("       `COD_CIDADE` = ? ");
		sql2.append("WHERE  `ID_USUARIO` = ?;");
		
		st = conn.prepareStatement(sql2.toString());
		st.setLong(2, Long.parseLong(codcidade));
		st.setLong(1, Long.parseLong(codbairro));
		st.setLong(3, cod_usuario);

		st.executeUpdate();

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	private static void updateUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		// if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
		// throw new Exception("Usuário inválido.");
		// } else
		{

			
			String desc_nome = request.getParameter("c_nome") == null ? "" : request.getParameter("c_nome");
			String desc_endereco = request.getParameter("c_endereco") == null ? "" : request.getParameter("c_endereco");
			String desc_endereco_num = request.getParameter("c_desc_endereco_num") == null ? "" : request.getParameter("c_desc_endereco_num");
			String desc_endereco_complemento = request.getParameter("c_desc_endereco_complemento") == null ? "" : request.getParameter("c_desc_endereco_complemento");
			String cod_bairro = request.getParameter("c_bairro") == "" ? null : request.getParameter("c_bairro");
			String desc_telefone = request.getParameter("c_telefone") == null ? "" : request.getParameter("c_telefone");
			
			
			String desc_cartao = request.getParameter("c_numcartao") == null ? "" : request.getParameter("c_numcartao");
			String data_exp_mes = request.getParameter("c_data_exp_mes") == null ? "" : request.getParameter("c_data_exp_mes");
			String data_exp_ano = request.getParameter("c_data_exp_ano") == null ? "" : request.getParameter("c_data_exp_ano");
			String desc_cardholdername = request.getParameter("c_desc_cardholdername") == null ? "" : request.getParameter("c_desc_cardholdername");
			String pay_id = request.getParameter("c_pay_id") == null ? "" : request.getParameter("c_pay_id");
			String desc_cpf = request.getParameter("c_desc_cpf") == null ? "" : request.getParameter("c_desc_cpf");


			if (cod_bairro.equalsIgnoreCase("")) {
				throw new Exception("Você deve preencher o campo de bairro.");
			}
			
			long codcidade =0;
			PreparedStatement st = null;
			ResultSet rs = null;
			
			String sql2 = "SELECT * from  usuario where ID_USUARIO  = ? ";
			st = conn.prepareStatement(sql2);
			st.setLong(1, cod_usuario);
			rs = st.executeQuery();
			if (rs.next()) {
				codcidade = rs.getLong("cod_cidade");
			}
		
			st = conn.prepareStatement("SELECT * from bairros where cod_cidade  = ? and cod_bairro = ? ");
			st.setLong(1, codcidade);
			st.setLong(2, Long.parseLong(cod_bairro));
			rs = st.executeQuery();

			if (!rs.next()) {
				throw new Exception("Bairro não encontrado. Você deve escolher um bairro válido.");
			}
			

		/*	PreparedStatement st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_email = ? and id_usuario != ?  ");
			st.setString(1, desc_email);
			st.setLong(2, cod_usuario);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				throw new Exception("Email já cadastrado!.");
			}*/

		/*	st = conn.prepareStatement("SELECT 1 from  cidade where   COD_CIDADE = ?   ");
			st.setInt(1, Integer.parseInt(cod_cidade));
			rs = st.executeQuery();
			if (!rs.next()) {
				throw new Exception("Cidade inválida!");
			}*/

			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE usuario ");
			sql.append("   SET `DESC_NOME` = ?, ");
			sql.append("       `DESC_TELEFONE` = ?, ");
			sql.append("       `DESC_ENDERECO` = ?, ");
			sql.append("       `DESC_ENDERECO_NUM` = ?, ");
			sql.append("       `DESC_ENDERECO_COMPLEMENTO` = ?, ");
			sql.append("       `COD_BAIRRO` = ?, ");
			sql.append("       `DESC_CARTAO` = ?, ");
			sql.append("       `DESC_CARDHOLDERNAME` = ?, ");
			sql.append("       `DATA_EXP_MES` = ?, ");
			sql.append("       `DATA_EXP_ANO` = ?, ");

			sql.append("       `PAY_ID` = ?, ");
			sql.append("       `DESC_CPF` = ? ");
			sql.append("WHERE  `ID_USUARIO` = ?;");

			st = conn.prepareStatement(sql.toString());
			st.setString(1, desc_nome);
			st.setString(2, desc_telefone);
			st.setString(3, desc_endereco);
			st.setString(4, desc_endereco_num);
			st.setString(5, desc_endereco_complemento);
			st.setInt(6, Integer.parseInt(cod_bairro));
			st.setString(7, desc_cartao);
			st.setString(8, desc_cardholdername);
			if (data_exp_mes.equalsIgnoreCase(""))
				st.setNull(9, java.sql.Types.INTEGER);
			else
				st.setInt(9, Integer.parseInt(data_exp_mes));

			if (data_exp_mes.equalsIgnoreCase(""))
				st.setNull(10, java.sql.Types.INTEGER);
			else
				st.setInt(10, Integer.parseInt(data_exp_ano));

			st.setString(11, pay_id);
			st.setString(12, desc_cpf);
			st.setLong(13, cod_usuario);

			st.executeUpdate();

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void carregaUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		// if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
		// throw new Exception("Usuário inválido.");
		// } else
		{

			PreparedStatement st = conn.prepareStatement(" select * from usuario  where `ID_USUARIO` = ?  ");
			st.setLong(1, cod_usuario);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				objRetorno.put("ID_USUARIO", rs.getString("ID_USUARIO"));
				objRetorno.put("DESC_NOME", rs.getString("DESC_NOME"));
				objRetorno.put("DESC_TELEFONE", rs.getString("DESC_TELEFONE"));
				objRetorno.put("COD_BAIRRO", rs.getString("COD_BAIRRO"));
				objRetorno.put("COD_CIDADE", rs.getString("COD_CIDADE"));
				objRetorno.put("DESC_ENDERECO", rs.getString("DESC_ENDERECO"));
				objRetorno.put("DESC_USER", rs.getString("DESC_USER"));
				objRetorno.put("DESC_EMAIL", rs.getString("DESC_EMAIL"));
				objRetorno.put("DESC_CARTAO", rs.getString("DESC_CARTAO"));
				objRetorno.put("DESC_ENDERECO_NUM", rs.getString("DESC_ENDERECO_NUM"));
				objRetorno.put("DESC_ENDERECO_COMPLEMENTO", rs.getString("DESC_ENDERECO_COMPLEMENTO"));
				objRetorno.put("DATA_EXP_MES", rs.getString("DATA_EXP_MES"));
				objRetorno.put("DATA_EXP_ANO", rs.getString("DATA_EXP_ANO"));
				objRetorno.put("DESC_CARDHOLDERNAME", rs.getString("DESC_CARDHOLDERNAME"));
				objRetorno.put("PAY_ID", rs.getString("PAY_ID"));
				objRetorno.put("DESC_CPF", rs.getString("DESC_CPF"));

			}

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void carregaLocationUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String sql = "SELECT * from usuario where id_usuario = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();

		Sys_parametros sys = new Sys_parametros(conn);

		int codcidade = 0;
		int codbairro = 0;

		if (rs.next()) {
			codbairro = rs.getInt("cod_bairro");
			codcidade = rs.getInt("cod_cidade");
		}

		if (codcidade == 0) {
			codcidade = sys.getCod_cidade();
		}

		retorno.put("codbairro", codbairro);
		retorno.put("codcidade", codcidade);

		out.print(retorno.toJSONString());
	}

	private static void carregaBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String sql = "SELECT cod_cidade from  usuario where id_usuario  = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		int codcidade = 0;// para o momento vai ser sempre 1 igual.
		if (rs.next()) {

			codcidade = rs.getInt("cod_cidade");
		}

		if (codcidade == 0) {// TODO
			codcidade = 1;
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

	private static void carregaCidade(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String sql = "SELECT * from cidade ";
		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject bairro = new JSONObject();
			bairro.put("cod_cidade", rs.getString("cod_cidade"));
			bairro.put("desc_cidade", rs.getString("desc_cidade"));
			retorno.add(bairro);
		}

		out.print(retorno.toJSONString());
	}

	private static void carregaProdutos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, boolean listagem) throws Exception {

		PrintWriter out = response.getWriter();

		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String distribuidora = request.getParameter("distribuidora") == null ? "" : request.getParameter("distribuidora");
		String desc_pesquisa = request.getParameter("desc_pesquisa") == null ? "" : request.getParameter("desc_pesquisa");
		String valini = request.getParameter("val_ini") == null ? "" : request.getParameter("val_ini");
		String valfim = request.getParameter("val_fim") == null ? "" : request.getParameter("val_fim");
		String idproduto = request.getParameter("idproduto") == null ? "" : request.getParameter("idproduto");

		if (cod_bairro.equalsIgnoreCase("")) {
			throw new Exception("Nenhum bairro informado!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select id_distribuidora from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append("inner join produtos_distribuidora ");
		sql.append("on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST ");
		sql.append("where id_usuario = ? limit 1 ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			distribuidora = rs.getString("id_distribuidora"); // se ja tem produtos no carrinho, ele vai usar a distribuidora que la se encontra
		}

		sql = new StringBuffer();
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
		st = conn.prepareStatement(sql.toString());
		st.setInt(1, Integer.parseInt(cod_bairro));

		st.setInt(2, Utilitario.diaSemana(conn, Integer.parseInt(distribuidora))); //
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

		rs = st.executeQuery();
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

		out.print(prods.toJSONString());
	}

	private static void carregaPedidos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

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

		st.setLong(contparam, cod_usuario);

		ResultSet rs = st.executeQuery();
		JSONArray prods = new JSONArray();
		while (rs.next()) {

			// NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,DESC_NOME_ABREV,id_pedido

			JSONObject prod = new JSONObject();
			prod.put("NUM_PED", rs.getString("NUM_PED"));
			prod.put("DATA_PEDIDO", rs.getString("DATA_PEDIDO"));
			prod.put("VAL_TOTALPROD", rs.getString("VAL_TOTALPROD"));
			prod.put("FLAG_STATUS", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
			prod.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));
			prod.put("id_pedido", rs.getString("id_pedido"));

			prods.add(prod);
		}

		out.print(prods.toJSONString());
	}

	private static void carregaPedidoUnico(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

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
		sql.append("       TEMPO_ESTIMADO_ENTREGA, ");
		sql.append("       DESC_NOME_ABREV, ");
		sql.append("       flag_status,id_pedido,bairros.desc_bairro ,desc_endereco_num_entrega,desc_endereco_complemento_entrega ");
		sql.append("FROM   pedido ");
		sql.append("       INNER JOIN distribuidora ");
		sql.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
		sql.append("       INNER JOIN bairros ");
		sql.append("               ON bairros.cod_bairro = pedido.cod_bairro ");
		sql.append("WHERE  id_pedido = ? ");
		sql.append("       AND id_usuario = ?");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_pedido));
		st.setLong(2, (cod_usuario));

		ResultSet rs = st.executeQuery();
		JSONObject ped = new JSONObject();
		if (rs.next()) {

			// NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,DESC_NOME_ABREV,id_pedido
			ped.put("num_ped", rs.getString("NUM_PED"));
			ped.put("data_pedido", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO")));
			ped.put("val_totalprod", rs.getString("VAL_TOTALPROD"));
			ped.put("flag_status", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
			ped.put("desc_nome_abrev", rs.getString("DESC_NOME_ABREV"));
			ped.put("id_pedido", rs.getString("id_pedido"));
			ped.put("val_entrega", rs.getString("val_entrega"));
			ped.put("desc_razao_social", rs.getString("desc_razao_social"));
			ped.put("desc_bairro", rs.getString("desc_bairro"));
			ped.put("desc_endereco_entrega", rs.getString("desc_endereco_entrega"));
			ped.put("desc_endereco_num_entrega", rs.getString("desc_endereco_num_entrega"));
			ped.put("desc_endereco_complemento_entrega", rs.getString("desc_endereco_complemento_entrega"));
			ped.put("tempo_entrega", new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));

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

				prod.put("desc_prod", rs2.getString("DESC_PROD"));
				prod.put("val_unit", rs2.getString("VAL_UNIT"));
				prod.put("qtd_prod", rs2.getString("QTD_PROD"));
				prod.put("total", rs2.getString("total"));
				prod.put("id_prod", rs2.getString("id_prod"));
				prod.put("desc_abreviado", rs2.getString("DESC_ABREVIADO"));

				produtos.add(prod);
			}

			ped.put("produtos", produtos);

		} else {
			throw new Exception("Pedido não encontrado!");
		}

		out.print(ped.toJSONString());
	}

	private static void carregaCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();

		StringBuffer sql = new StringBuffer();
		sql.append("select carrinho_item.id_carrinho, distribuidora_bairro_entrega.cod_bairro,desc_bairro, seq_item, qtd,val_prod,produtos_distribuidora.id_distribuidora, DESC_RAZAO_SOCIAL,VAL_ENTREGA_MIN , produtos.DESC_PROD, produtos.DESC_ABREVIADO, ");
		sql.append(" ");
		sql.append("case when FLAG_TELEBAIRRO = 'S' then distribuidora_bairro_entrega.VAL_TELE_ENTREGA else distribuidora.VAL_TELE_ENTREGA end as VAL_TELE_ENTREGA ");
		sql.append(" ");
		sql.append("from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append("inner join produtos_distribuidora ");
		sql.append("on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST ");
		sql.append(" ");
		sql.append("inner join produtos ");
		sql.append("on produtos_distribuidora.ID_PROD = produtos.ID_PROD ");
		sql.append(" ");
		sql.append("inner join distribuidora ");
		sql.append("on distribuidora.id_distribuidora = produtos_distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" ");
		sql.append("inner join bairros ");
		sql.append("on bairros.cod_bairro = carrinho.cod_bairro ");
		sql.append(" ");
		sql.append("inner join distribuidora_bairro_entrega ");
		sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" ");
		sql.append("where id_usuario = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		JSONObject carrinho = new JSONObject();

		String id_carrinho = "";
		String cod_bairro = "";
		String desc_bairro = "";
		String id_distribuidora = "";
		String DESC_RAZAO_SOCIAL = "";
		String VAL_ENTREGA_MIN = "";

		JSONArray carrinhoitem = new JSONArray();

		while (rs.next()) {
			JSONObject obj = new JSONObject();
			// preguiça
			id_carrinho = rs.getString("id_carrinho");
			cod_bairro = rs.getString("cod_bairro");
			desc_bairro = rs.getString("desc_bairro");
			obj.put("seq_item", rs.getString("seq_item"));
			obj.put("qtd", rs.getString("qtd"));
			obj.put("val_prod", rs.getString("val_prod"));
			obj.put("desc_prod", rs.getString("DESC_PROD"));
			obj.put("desc_abreviado", rs.getString("DESC_ABREVIADO"));
			id_distribuidora = rs.getString("id_distribuidora");
			DESC_RAZAO_SOCIAL = rs.getString("DESC_RAZAO_SOCIAL");
			VAL_ENTREGA_MIN = rs.getString("VAL_ENTREGA_MIN");

			carrinhoitem.add(obj);

		}

		carrinho.put("id_carrinho", id_carrinho);
		carrinho.put("cod_bairro", cod_bairro);
		carrinho.put("desc_bairro", desc_bairro);
		carrinho.put("id_distribuidora", id_distribuidora);
		carrinho.put("DESC_RAZAO_SOCIAL", DESC_RAZAO_SOCIAL);
		carrinho.put("VAL_ENTREGA_MIN", VAL_ENTREGA_MIN);

		carrinho.put("produtos", carrinhoitem);

		// System.out.println(carrinho.toJSONString());
		out.print(carrinho.toJSONString());
	}

	private static void addCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		// String id_prod = request.getParameter("id_prod") == null ? "" : request.getParameter("id_prod");
		String id_proddistr = request.getParameter("id_proddistr") == null ? "" : request.getParameter("id_proddistr"); // precisa receber
		// String id_ditribuidora = request.getParameter("id_ditribuidora") == null ? "" : request.getParameter("id_ditribuidora");
		String qtd = request.getParameter("qtd") == null ? "" : request.getParameter("qtd"); // precisa receber
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro"); // precisa receber

		boolean jatemcarrinho = false;
		long idcarrinho = 0;
		int id_distribuidora_prod = 0;
		double valunit = 0;

		if (!Utilitario.isNumeric(id_proddistr)) {
			throw new Exception("Produto inválido!");
		}

		if (!Utilitario.isNumeric(cod_bairro)) {
			throw new Exception("Bairro inválido!");
		}

		if (!Utilitario.isNumeric(qtd)) {
			throw new Exception("Quantidade inválida!");
		}

		if (cod_bairro.equalsIgnoreCase("0")) {
			throw new Exception("Bairro inválido!");
		}

		if (id_proddistr.equalsIgnoreCase("0")) {
			throw new Exception("Produto inválido!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id_carrinho from carrinho where id_usuario = ? ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();

		if (rs.next()) {// ja tem carrinho
			jatemcarrinho = true;
			idcarrinho = rs.getLong("id_carrinho");
		}

		sql = new StringBuffer();// testa se o produto existe e pega id da distribuidora relacionada ao produto
		sql.append(" select * from produtos_distribuidora ");
		sql.append(" where ID_PROD_DIST = ? and flag_ativo = 'S' ");
		st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_proddistr));
		rs = st.executeQuery();
		if (rs.next()) {
			id_distribuidora_prod = rs.getInt("ID_DISTRIBUIDORA");
			valunit = rs.getDouble("VAL_PROD");
		} else {
			throw new Exception("Produto inválido!");
		}

		validacaoFlagsDistribuidora(request, response, conn, id_distribuidora_prod);

		if (jatemcarrinho && id_distribuidora_prod != 0) {

			validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, id_distribuidora_prod);

		}

		validacaoDisBairroHora(request, response, conn, Long.parseLong(cod_bairro), id_distribuidora_prod);

		if (jatemcarrinho) {
			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho_item ");
			sql.append(" where ID_PROD_DIST = ? and id_carrinho  = ?  ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, Long.parseLong(id_proddistr));
			st.setLong(2, (idcarrinho));
			st.executeUpdate();
		}

		if (!jatemcarrinho && Integer.parseInt(qtd) != 0) {// criar carrinho

			idcarrinho = Utilitario.retornaIdinsert("carrinho", "id_carrinho", conn); // add carrinho
			sql = new StringBuffer();// insere no carrinho
			sql.append(" INSERT INTO carrinho (`ID_CARRINHO`, `ID_USUARIO`, `COD_BAIRRO`, `DATA_CRIACAO`) VALUES (?, ?, ?, now()); ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, idcarrinho);
			st.setLong(2, (cod_usuario));
			st.setLong(3, Long.parseLong(cod_bairro));
			st.executeUpdate();
			jatemcarrinho = true;

		}

		if (jatemcarrinho && Integer.parseInt(qtd) != 0) {

			sql = new StringBuffer();// insere no carrinho
			sql.append(" INSERT INTO carrinho_item (`ID_CARRINHO`, `SEQ_ITEM`, `ID_PROD_DIST`, `QTD`, VAL_PROD) VALUES (?, ?, ?, ?, ?); ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, (idcarrinho));
			st.setLong(2, Utilitario.retornaIdinsertChaveSecundaria("carrinho_item", "id_carrinho", idcarrinho + "", "seq_item", conn));
			st.setLong(3, Long.parseLong(id_proddistr));
			st.setLong(4, Integer.parseInt(qtd));
			st.setDouble(5, (valunit));
			st.executeUpdate();

		}

		if (Integer.parseInt(qtd) == 0) {// se qtd é 0, é pq o item foi removido do carrinho, entao aqui é testado se ainda existe algum item no carrinho, caso nao tenha, remove o carrinho

			sql = new StringBuffer();
			sql.append(" select 1 from carrinho_item ");
			sql.append("where id_carrinho = ?  ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, (idcarrinho));
			rs = st.executeQuery();
			if (!rs.next()) {

				sql = new StringBuffer();//
				sql.append(" delete  from carrinho ");
				sql.append("where id_carrinho = ?  ");
				st = conn.prepareStatement(sql.toString());
				st.setLong(1, (idcarrinho));
				st.executeUpdate();
			}

		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	private static void validacaoDisBairroHora(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_bairro, int id_distribuidora_prod) throws Exception {

		{
			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual

			sql.append(" select 1 from distribuidora_bairro_entrega ");
			sql.append(" inner join distribuidora_horario_dia_entre ");
			sql.append(" on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO   =   distribuidora_bairro_entrega.ID_DISTR_BAIRRO ");
			sql.append(" where cod_bairro = ? and distribuidora_bairro_entrega.id_distribuidora  = ? and (now() between horario_ini and horario_fim) and cod_dia = ?  ");
			PreparedStatement st = conn.prepareStatement(sql.toString());

			st.setLong(1, (cod_bairro));
			st.setLong(2, (id_distribuidora_prod));
			st.setLong(3, Utilitario.diaSemana(conn, id_distribuidora_prod));
			ResultSet rs = st.executeQuery();
			if (!rs.next()) {
				throw new Exception("Distribuidora não se encontra disponível para o bairro escolhido.");
			}
		}

	}

	private static void validacaoFlagsDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, int id_distribuidora_prod) throws Exception {
		{

			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual
			sql.append(" select FLAG_ATIVO,FLAG_ATIVO_MASTER from  distribuidora where id_distribuidora = ? ");
			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setLong(1, (id_distribuidora_prod));
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				if (!rs.getString("FLAG_ATIVO").equalsIgnoreCase("S")) {
					throw new Exception("Distribuidora está offline no momento.");
				}

				if (!rs.getString("FLAG_ATIVO_MASTER").equalsIgnoreCase("S")) {
					throw new Exception("Distribuidora está desativada.");
				}
			}

		}

	}

	private static void validacaoTesteCarrinhoDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_carrinho, int id_distribuidora_prod) throws Exception {
		{

			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é a mesma do carrinho

			sql.append(" select ID_DISTRIBUIDORA from carrinho_item ");
			sql.append("inner join produtos_distribuidora ");
			sql.append("on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST ");
			sql.append("where id_carrinho = ?  ");
			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setLong(1, (id_carrinho));
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (id_distribuidora_prod != rs.getInt("ID_DISTRIBUIDORA")) {
					throw new Exception("Produto selecionado é de uma distribuidora diferente da que se encontra no carrinho!");
				}
			}

		}

	}

	private static void criarPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		String tipo_pagamento = request.getParameter("tipo_pagamento") == null ? "" : request.getParameter("tipo_pagamento");

		if (!tipo_pagamento.equalsIgnoreCase("D") && !tipo_pagamento.equalsIgnoreCase("C")) {
			throw new Exception("Tipo de pagamento inválido.");
		}

		long idcarrinho = 0;

		StringBuffer sql = new StringBuffer();
		sql.append("select usuario.DESC_EMAIL, usuario. DESC_NOME, carrinho_item.id_carrinho,distribuidora_bairro_entrega.ID_DISTRIBUIDORA,carrinho.cod_bairro,usuario.DESC_TELEFONE,usuario.DESC_ENDERECO,DESC_ENDERECO_NUM,DESC_ENDERECO_COMPLEMENTO,sum(produtos_distribuidora.val_prod * carrinho_item.qtd ) as val_prod, ");
		sql.append("  ");
		sql.append("case when FLAG_TELEBAIRRO = 'S' then distribuidora_bairro_entrega.VAL_TELE_ENTREGA else distribuidora.VAL_TELE_ENTREGA end as VAL_TELE_ENTREGA ");
		sql.append("  ");
		sql.append("from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append("inner join produtos_distribuidora ");
		sql.append("on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST ");
		sql.append(" ");
		sql.append("inner join produtos ");
		sql.append("on produtos_distribuidora.ID_PROD = produtos.ID_PROD ");
		sql.append("  ");
		sql.append("inner join distribuidora ");
		sql.append("on distribuidora.id_distribuidora = produtos_distribuidora.ID_DISTRIBUIDORA ");
		sql.append("  ");
		sql.append("inner join bairros ");
		sql.append("on bairros.cod_bairro = carrinho.cod_bairro ");
		sql.append("");
		sql.append("inner join distribuidora_bairro_entrega ");
		sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" ");
		sql.append("inner join usuario ");
		sql.append("on usuario.id_usuario = carrinho.id_usuario ");
		sql.append("  ");
		sql.append("where usuario.id_usuario = ? ");
		sql.append(" ");
		sql.append("group by usuario.DESC_EMAIL, usuario. DESC_NOME, carrinho_item.id_carrinho,distribuidora_bairro_entrega.ID_DISTRIBUIDORA,carrinho.cod_bairro,usuario.DESC_TELEFONE,usuario.DESC_ENDERECO,DESC_ENDERECO_NUM,DESC_ENDERECO_COMPLEMENTO, VAL_TELE_ENTREGA");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		PreparedStatement st2 = null;
		PreparedStatement st3 = null;
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		String email = "";
		if (rs.next()) {
			idcarrinho = rs.getLong("id_carrinho");

			validacaoFlagsDistribuidora(request, response, conn, rs.getInt("ID_DISTRIBUIDORA"));
			validacaoDisBairroHora(request, response, conn, rs.getInt("cod_bairro"), rs.getInt("ID_DISTRIBUIDORA"));
			validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, rs.getInt("ID_DISTRIBUIDORA"));// se um item do carrinho tiver uma distribuidora diferente, ele vai dar um erro

			if (rs.getDouble("val_prod") == 0) {
				throw new Exception("Erro, o valor dos produtos de seu pedido é R$ 0,00!.");
			}

			sql = new StringBuffer();
			sql.append("INSERT INTO pedido ");
			sql.append("  (`ID_PEDIDO`, `ID_DISTRIBUIDORA`, `ID_USUARIO`, `DATA_PEDIDO`, `FLAG_STATUS`, `VAL_TOTALPROD`, `VAL_ENTREGA`,  `NUM_PED`, `COD_BAIRRO`, `NUM_TELEFONECONTATO_CLIENTE`,  `DESC_ENDERECO_ENTREGA`, `DESC_ENDERECO_NUM_ENTREGA`, `DESC_ENDERECO_COMPLEMENTO_ENTREGA`, flag_vizualizado,FLAG_MODOPAGAMENTO,NOME_PESSOA) ");
			sql.append("VALUES ");
			sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?);");

			st = conn.prepareStatement(sql.toString());

			long idped = Utilitario.retornaIdinsertLong("pedido", "ID_PEDIDO", conn);
			st.setLong(1, idped);
			st.setLong(2, rs.getInt("ID_DISTRIBUIDORA"));
			st.setLong(3, cod_usuario);
			st.setTimestamp(4, Utilitario.getTimeStamp(new Date()));
			st.setString(5, "A");
			st.setDouble(6, rs.getDouble("val_prod"));
			st.setDouble(7, rs.getDouble("VAL_TELE_ENTREGA"));
			st.setLong(8, idped);
			st.setInt(9, rs.getInt("cod_bairro"));
			st.setString(10, rs.getString("DESC_TELEFONE"));
			st.setString(11, rs.getString("DESC_ENDERECO"));
			st.setString(12, rs.getString("DESC_ENDERECO_NUM"));
			st.setString(13, rs.getString("DESC_ENDERECO_COMPLEMENTO"));
			st.setString(14, "N");
			st.setString(15, tipo_pagamento);
			st.setString(16, rs.getString("DESC_NOME"));

			email = rs.getString("DESC_EMAIL");

			st.executeUpdate();

			sql = new StringBuffer();
			sql.append("select * from carrinho_item inner join produtos_distribuidora on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST  where id_carrinho = ? ");

			st2 = conn.prepareStatement(sql.toString());
			st2.setLong(1, idcarrinho);
			rs2 = st2.executeQuery();
			while (rs2.next()) {

				sql = new StringBuffer();// teste para ver se o valor do carrinho é igual ao valor do produto.
				sql.append("select  * from produtos_distribuidora  where  ID_PROD_DIST = ? and flag_ativo = 'S' ");
				st3 = conn.prepareStatement(sql.toString());
				st3.setLong(1, rs2.getLong("ID_PROD_DIST"));
				rs3 = st3.executeQuery();
				if (rs3.next()) {
					if (rs3.getDouble("VAL_PROD") != rs2.getDouble("VAL_PROD")) {
						throw new Exception("O produto " + Utilitario.getNomeProdIdProdDistr(conn, rs2.getLong("ID_PROD_DIST"), true) + " que está no carrinho tem um valor diferente que se encontra na distribuida. Isto pode acontecer quando a distribuidora modifica o valor de um produto durante sua compra. Por favor clique em recalcular carrinho.");
					}
				} else {
					throw new Exception("O produto " + Utilitario.getNomeProdIdProdDistr(conn, rs2.getLong("ID_PROD_DIST"), true) + " não se encontra disponível. Por favor remova-o de seu carrinho.");
				}

				sql = new StringBuffer();
				sql.append(" INSERT INTO pedido_item ");
				sql.append("   (`ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`) ");
				sql.append(" VALUES ");
				sql.append("  (?, ?, ?, ?, ?);");

				st = conn.prepareStatement(sql.toString());
				st.setLong(1, idped);
				st.setLong(2, Utilitario.retornaIdinsertChaveSecundaria("pedido_item", "id_pedido", idped + "", "SEQ_ITEM", conn));
				st.setDouble(3, rs2.getDouble("VAL_PROD"));
				st.setLong(4, rs2.getLong("ID_PROD"));
				st.setLong(5, rs2.getLong("QTD"));

				st.executeUpdate();

			}

			if (tipo_pagamento.equalsIgnoreCase("C")) {// se for do tipo cartao de credito , tem que salvar as infos

				String token = request.getParameter("token") == null ? "" : request.getParameter("token");
				String paymentMethodId = request.getParameter("paymentMethodId") == null ? "" : request.getParameter("paymentMethodId");

				if (paymentMethodId.equalsIgnoreCase("")) {
					throw new Exception("Seu tipo de cartão não está preenchido");// TODO, tipo? outro nome
				}

				if (token.equalsIgnoreCase("")) {
					throw new Exception("Erro durante criação do pedido. Token inválida.");
				}

				if (email.equalsIgnoreCase("")) {
					throw new Exception("Email em branco.");
				}

				sql = new StringBuffer();
				sql.append("UPDATE pedido ");
				sql.append("   SET `PAG_TOKEN` = ?, ");
				sql.append("       `PAG_MAIL` = ?, ");
				sql.append("       `PAG_PAYID_TIPOCARTAO` = ? ");
				sql.append("WHERE  `ID_PEDIDO` = ?;");

				st = conn.prepareStatement(sql.toString());
				st.setString(1, token);
				st.setString(2, email);
				st.setString(3, paymentMethodId);
				st.setLong(4, idped);

				st.executeUpdate();

			}

			// realizar pagamento, pensar TODO

		} else {
			throw new Exception("Ocorreu um erro com o processamento de seu carrinho! Entre em contato com o suporte.");
		}

		{
			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho_item where ID_CARRINHO = ? ");
			st.setLong(1, (idcarrinho));
			st.executeUpdate();

			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho  where ID_CARRINHO = ? ");
			st.setLong(1, (idcarrinho));
			st.executeUpdate();
		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	public static void main(String[] args) {

		Connection conn = null;
		try {
			conn = Conexao.getConexao();

			Utilitario.retornaIdinsertChaveSecundaria("pedido_item", "id_pedido", "5", "seq_item", conn);
			/*
			 * SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey(); // get base64 encoded version of the key String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
			 * 
			 * System.out.println(encodedKey);
			 */

			/// System.out.println(new GregorianCalendar().get(Calendar.DAY_OF_WEEK));

		} catch (Exception e) {
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
				}

			}
		}

	}

}