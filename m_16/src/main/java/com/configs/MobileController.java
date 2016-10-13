package com.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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

	public static DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
	public static NumberFormat df = new DecimalFormat("###,###.#", dfs);
	public static NumberFormat df2 = new DecimalFormat("#,###,##0.00", dfs);

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
			} else if (strTipo.equalsIgnoreCase("validarEmail")) {
				MobileLogin.validarEmailNovo(request, response);
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
			Sys_parametros sys = new Sys_parametros(conn);
			String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd");

			if (cmd.equalsIgnoreCase("login")) {
				String flag_face = request.getParameter("flag_face") == null ? "" : request.getParameter("flag_face");

				if (!(flag_face.equalsIgnoreCase("S")) && !(flag_face.equalsIgnoreCase("N"))) {
					throw new Exception("Parâmetros de de login incorretos, ta metendo a mão onde não deve mané! ");
				}

				if (flag_face.equalsIgnoreCase("S")) {
					MobileLogin.loginMobileFace(request, response, conn, sys);
				} else {
					String user = request.getParameter("user");
					String pass = request.getParameter("pass");
					MobileLogin.loginMobile(request, response, conn, user, pass, sys);
				}

			} else if (cmd.equalsIgnoreCase("inserir_user")) {

				inserirUser(request, response, conn, sys);

			} else if (cmd.equalsIgnoreCase("rec_senha")) {

				recSenha(request, response, conn);

			} else {
				long cod_usuario = MobileLogin.parseJWT(request, response, conn, request.getHeader("X-Auth-Token"), sys);

				if (cod_usuario == 0) {
					throw new Exception("Usuário inválido");
				}

				if (sys.getFlag_manutencao().equalsIgnoreCase("S") && sys.getId_usuario_admin() != cod_usuario) {
					throw new Exception("Servidor está em manutenção. Tente novamente mais tarde. Cheers! ");
				}

				if (cmd.equalsIgnoreCase("descbairro")) {
					descBairro(request, response, conn);
				} else if (cmd.equalsIgnoreCase("valprodmax")) {
					getValprodMax(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaProdutos")) {
					carregaProdutos(request, response, conn, cod_usuario, true);
				} else if (cmd.equalsIgnoreCase("carrega_bairros")) {
					carregaBairros(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_distribuidoras")) {
					carregaDistribuidora(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_cidade")) {
					carregaCidade(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaLocationUser")) {
					carregaLocationUser(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("detalheProdutos")) {
					carregaProdutoUnico(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("saveLocationUser")) {
					saveLocationUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("confirmaIdade")) {
					confirmaIdade(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("carrega_flagentreret")) {
					carregaFlagEntreRet(request, response, conn);
				} else if (cod_usuario == sys.getSys_id_visistante()) {// operações daqui para cima, são validas para visitante.
					objRetorno.put("guest", "true");
					throw new Exception("Você está acessando como visitante. Para poder realizar esta operação você deve criar uma conta no S.O.S Trago.");
				} else if (cmd.equalsIgnoreCase("carrega_situacaoes")) {
					carregaSitucaoes(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("trocarEmail")) {
					trocarEmail(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("trocarSenha")) {
					trocarSenha(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("carregaPayCreditIds")) {
					carregaPayCreditIds(request, response, conn);
				} else if (cmd.equalsIgnoreCase("save_user")) {
					updateUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carrega_user")) {
					carregaUser(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaPedidos")) {
					carregaPedidos(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaPedidoUnico")) {
					carregaPedidoUnico(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaItemCarrinho")) {
					carregaCarrinho(request, response, conn, cod_usuario, true);
				} else if (cmd.equalsIgnoreCase("carregaCarrinho")) {
					carregaCarrinho(request, response, conn, cod_usuario, false);
				} else if (cmd.equalsIgnoreCase("addCarrinho")) {
					addCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("recalcularCarrinho")) {
					recalcularCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaValCarrinho")) {
					carregaValCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("removerItemCarrinho")) {
					removerItemCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("criarPedido")) {
					criarPedido(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("testesMudaBairroCarrinho")) {
					testesMudaBairroCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("limparCarrinho")) {
					limparCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaEnderecos")) {
					carregaEnderecos(request, response, conn, cod_usuario);
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

	private static void payment(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_usuario, String email) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		MP mp = new MP("TEST-3928083096731492-072914-2aa78c35c6f210a6322c4acf9abe4d14__LD_LC__-222772872");

		String token = request.getParameter("token");
		String paymentMethodId = request.getParameter("paymentMethodId");

		org.codehaus.jettison.json.JSONObject payment = mp.post("/v1/payments", "{" + "'transaction_amount': 100," + "'token': " + token + "," + "'description': 'Title of what you are paying for'," + "'installments': 1," + "'payment_method_id': '" + paymentMethodId + "'," + "'payer': {" + "'email': '" + email + "'" + "}" + "}");
		System.out.println(payment);
		if (payment.get("status").toString().equalsIgnoreCase("400") || payment.get("status").toString().equalsIgnoreCase("404") || payment.get("status").toString().equalsIgnoreCase("403")) {
			throw new Exception(((org.codehaus.jettison.json.JSONObject) payment.get("response")).get("message").toString());
		}

		// objRetorno.put("msg", "ok");
		// out.print(objRetorno.toJSONString());

	}

	private static void carregaPayCreditIds(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		PrintWriter out = response.getWriter();
		out.print(Utilitario.payments_ids().toJSONString());
	}

	private static void carregaFlagEntreRet(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		PrintWriter out = response.getWriter();
		out.print(Utilitario.FlagEntreRet().toJSONString());
	}

	private static void descBairro(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
		PrintWriter out = response.getWriter();
		String cod = request.getParameter("cod") == null ? "" : request.getParameter("cod");

		JSONObject objRetorno = new JSONObject();
		objRetorno.put("descbairro", Utilitario.getNomeBairro(conn, Integer.parseInt(cod), 0));
		out.print(objRetorno.toJSONString());
	}

	private static void confirmaIdade(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		if (sys.getSys_id_visistante() != cod_usuario) {
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE usuario ");
			sql.append("   SET `FLAG_MAIORIDADE` = ? ");

			sql.append(" WHERE   `ID_USUARIO` = ? ");

			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setString(1, "S");
			st.setLong(2, cod_usuario);
			st.executeUpdate();
		}
		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void recSenha(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

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
			Utilitario.sendEmail(desc_email, texto, "Recuperação de informações de acesso do TragoAqui!", conn);
			objRetorno.put("msg", "ok");

		} else {
			throw new Exception("E-mail não encontrado.");
		}

		out.print(objRetorno.toJSONString());

	}

	private static void inserirUser(HttpServletRequest request, HttpServletResponse response, Connection conn, Sys_parametros sys) throws Exception {

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

		String texto = " Bem vindo ao TragoAqui, para validar seu e-mail clique no link abaixo: <br> " + sys.getUrl_system() + "mobile?ac=validar&token=" + validacao;

		Utilitario.sendEmail(desc_email, texto, "Ativação da sua conta no TragoAqui!", conn);

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	private static void trocarSenha(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String ts_passatual = request.getParameter("ts_passatual") == null ? "" : request.getParameter("ts_passatual");
		String ts_newpassword = request.getParameter("ts_newpassword") == null ? "" : request.getParameter("ts_newpassword");
		String ts_newpasswordconfirm = request.getParameter("ts_newpasswordconfirm") == null ? "" : request.getParameter("ts_newpasswordconfirm");

		PreparedStatement st = conn.prepareStatement("SELECT * from  usuario where  ID_USUARIO = ? ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			if (!rs.getString("desc_senha").toString().equals(ts_passatual.toString()))
				throw new Exception("Sua senha está incorreta");

			if (!ts_newpassword.toString().equals(ts_newpasswordconfirm.toString())) {
				throw new Exception("Suas senhas novas estão diferentes");
			}

			String sql = "UPDATE usuario SET desc_senha = ?  WHERE ID_USUARIO = ? ";
			st = conn.prepareStatement(sql);
			st.setString(1, ts_newpassword);
			st.setLong(2, cod_usuario);
			st.executeUpdate();

			MobileLogin.loginMobile(request, response, conn, rs.getString("desc_user"), ts_newpassword, sys);

		} else {

			out.print(objRetorno.toJSONString());
		}

	}

	private static void trocarEmail(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String te_email = request.getParameter("te_email") == null ? "" : request.getParameter("te_email");
		String te_passatual = request.getParameter("te_passatual") == null ? "" : request.getParameter("te_passatual");

		PreparedStatement st = conn.prepareStatement("SELECT * from  usuario where  Binary DESC_SENHA =  ? and ID_USUARIO = ? ");
		st.setString(1, te_passatual);
		st.setLong(2, cod_usuario);
		ResultSet rs = st.executeQuery();

		if (!rs.next()) {
			throw new Exception("Sua senha está incorreta");
		} else {

			if (rs.getString("FLAG_FACEUSER") != null && rs.getString("FLAG_FACEUSER").equalsIgnoreCase("S")) {
				throw new Exception("Sua conta está associada a uma conta do Facebook, para mudar seu E-mail, por favor mude seu E-mail no Facebook.");
			} else {

				PreparedStatement st2 = conn.prepareStatement("SELECT * from  usuario where  DESC_EMAIL =  ? and FLAG_ATIVADO = 'S'  ");
				st2.setString(1, te_email);
				ResultSet rs2 = st2.executeQuery();
				if (rs2.next()) {
					throw new Exception("Este E-mail já esta sendo usado no TragoAqui");
				}

				String validacao = Utilitario.StringGen(1000, 32).substring(0, 99);

				String sql = "UPDATE usuario SET `DESC_NOVOEMAILVALIDACAO` = ? , `CHAVE_ATIVACAO_NOVOEMAIL` = ? WHERE ID_USUARIO = ?";
				st = conn.prepareStatement(sql);
				st.setString(1, te_email);
				st.setString(2, validacao);
				st.setLong(3, cod_usuario);

				st.executeUpdate();

				String texto = " Clique no link para validar seu novo e-mail: <br> " + sys.getUrl_system() + "mobile?ac=validarEmail&token=" + validacao;

				Utilitario.sendEmail(te_email, texto, "Confirmação de novo e-mail - TragoAqui", conn);
				objRetorno.put("msg", "ok");
			}

		}

		out.print(objRetorno.toJSONString());

	}

	private static void getValprodMax(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		// if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
		// throw new Exception("Usuário inválido.");
		// } else
		{

			PreparedStatement st = conn.prepareStatement(" select max(VAL_PROD) as val_prod from produtos_distribuidora where FLAG_ATIVO = 'S' ");

			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				objRetorno.put("val_prod", Math.ceil(rs.getDouble("VAL_PROD")));

			}

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void saveLocationUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String codbairro = request.getParameter("codbairro") == null ? "" : request.getParameter("codbairro");
		String codcidade = request.getParameter("codcidade") == null ? "" : request.getParameter("codcidade");
		String servico = request.getParameter("servico") == null ? "" : request.getParameter("servico");

		if (codcidade.equalsIgnoreCase("")) {
			throw new Exception("Cidade inválida.");
		}

		if (!servico.equals("T") && !servico.equals("L")) {
			throw new Exception("Você deve escolher um serviço.");
		}
		if (servico.equalsIgnoreCase("T")) {

			if (codbairro.equalsIgnoreCase("")) {
				throw new Exception("Bairro inválido.");
			}
		}
		String sql = "SELECT COD_CIDADE from  cidade where COD_CIDADE  = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, Long.parseLong(codcidade));
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Cidade não encontrada. Você deve escolher uma cidade válida.");
		}
		if (servico.equalsIgnoreCase("T")) {
			st = conn.prepareStatement("SELECT * from bairros where cod_cidade  = ? and cod_bairro = ? ");
			st.setLong(1, Long.parseLong(codcidade));
			st.setLong(2, Long.parseLong(codbairro));
			rs = st.executeQuery();

			if (!rs.next()) {
				throw new Exception("Bairro não encontrado. Você deve escolher um bairro válido.");
			}
		}
		StringBuffer sql2 = new StringBuffer();
		sql2.append("UPDATE usuario ");
		sql2.append("   SET  ");
		if (servico.equalsIgnoreCase("T")) {
			sql2.append("       `COD_BAIRRO` = ?, ");
		}
		sql2.append("       `COD_CIDADE` = ? ");
		sql2.append("WHERE  `ID_USUARIO` = ?;");

		st = conn.prepareStatement(sql2.toString());
		int contparam = 1;

		if (servico.equalsIgnoreCase("T")) {
			st.setLong(contparam, Long.parseLong(codbairro));
			contparam++;
		}
		
		st.setLong(contparam, Long.parseLong(codcidade));
		contparam++;

		st.setLong(contparam, cod_usuario);
		contparam++;
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
			String cod_bairro = request.getParameter("c_bairro") == null ? "" : request.getParameter("c_bairro");
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

			long codcidade = 0;
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

			/*
			 * PreparedStatement st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_email = ? and id_usuario != ?  "); st.setString(1, desc_email); st.setLong(2, cod_usuario); ResultSet rs = st.executeQuery(); if (rs.next()) { throw new Exception("Email já cadastrado!."); }
			 */

			/*
			 * st = conn.prepareStatement("SELECT 1 from  cidade where   COD_CIDADE = ?   "); st.setInt(1, Integer.parseInt(cod_cidade)); rs = st.executeQuery(); if (!rs.next()) { throw new Exception("Cidade inválida!"); }
			 */

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

			if (data_exp_ano.equalsIgnoreCase(""))
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

	private static void carregaLocationUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String sql = "SELECT * from usuario where id_usuario = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();

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

		JSONObject bairro = new JSONObject();
		bairro.put("cod_bairro", "");
		bairro.put("desc_bairro", "Escolha um bairro");

		retorno.add(bairro);

		while (rs.next()) {
			bairro = new JSONObject();
			bairro.put("cod_bairro", rs.getString("cod_bairro"));
			bairro.put("desc_bairro", rs.getString("desc_bairro"));

			retorno.add(bairro);
		}

		out.print(retorno.toJSONString());
	}

	private static void carregaSitucaoes(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		out.print(Utilitario.returnJsonStatusPedidoFlag().toJSONString());
	}

	private static void carregaDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
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

		st = conn.prepareStatement("SELECT * from  distribuidora where cod_cidade  = ? ");
		st.setInt(1, codcidade);
		rs = st.executeQuery();

		JSONObject distr = new JSONObject();
		distr.put("id_distribuidora", "");
		distr.put("desc_nome_abrev", "Escolha uma distribuidora");

		retorno.add(distr);

		while (rs.next()) {
			distr = new JSONObject();
			distr.put("id_distribuidora", rs.getString("ID_DISTRIBUIDORA"));
			distr.put("desc_nome_abrev", rs.getString("DESC_NOME_ABREV"));

			retorno.add(distr);
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

	private static void carregaProdutoUnico(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String idproddistr = request.getParameter("idproddistr") == null ? "" : request.getParameter("idproddistr");

		StringBuffer sql = new StringBuffer();

		sql.append(" ");
		sql.append(" select ");
		sql.append(" ");
		sql.append("  produtos.ID_PROD, ");
		sql.append(" DESC_PROD, ");
		sql.append(" DESC_ABREVIADO, ");
		sql.append(" produtos_distribuidora.VAL_PROD, ");
		sql.append(" distribuidora.ID_DISTRIBUIDORA, ");
		sql.append(" produtos_distribuidora.ID_PROD_DIST, ");
		sql.append(" DESC_NOME_ABREV , ");
		sql.append(" carrinho_item.QTD, ");
		sql.append(" distribuidora.VAL_ENTREGA_MIN ");
		sql.append(" ");
		sql.append(" from produtos ");
		sql.append(" inner join produtos_distribuidora ");
		sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append(" inner join distribuidora ");
		sql.append(" on produtos_distribuidora.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" inner join distribuidora_bairro_entrega ");
		sql.append(" on distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" inner join distribuidora_horario_dia_entre ");
		sql.append(" on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO   = distribuidora_bairro_entrega.ID_DISTR_BAIRRO ");
		sql.append("  ");
		sql.append(" left join carrinho ");
		sql.append(" on carrinho.id_usuario =   " + cod_usuario);
		sql.append("  ");
		sql.append(" ");
		sql.append(" left join carrinho_item ");
		sql.append(" on carrinho_item.ID_PROD_DIST = produtos_distribuidora.ID_PROD_DIST and carrinho_item.id_carrinho = carrinho.id_carrinho ");

		sql.append(" where produtos_distribuidora.ID_PROD_DIST = ? ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(idproddistr));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			retorno.put("ID_PROD", rs.getString("ID_PROD"));
			retorno.put("ID_PROD_DIST", rs.getString("ID_PROD_DIST"));
			retorno.put("QTD", rs.getString("QTD") == null ? "" : rs.getInt("QTD"));
			retorno.put("DESC_PROD", rs.getString("DESC_PROD"));
			retorno.put("DESC_ABREVIADO", rs.getString("DESC_ABREVIADO"));// abreviado do produto
			retorno.put("VAL_PROD", rs.getDouble("VAL_PROD"));//
			retorno.put("ID_DISTRIBUIDORA", rs.getString("ID_DISTRIBUIDORA"));
			retorno.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));/// abreviado da distribuidora
			retorno.put("val_minentrega", rs.getString("VAL_ENTREGA_MIN"));///
			retorno.put("valcar", df2.format(retornaValCarrinho(cod_usuario, conn) - (rs.getInt("QTD") * rs.getDouble("VAL_PROD"))));

		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	private static void carregaProdutos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, boolean listagem) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String distribuidora = request.getParameter("distribuidora") == null ? "" : request.getParameter("distribuidora");
		String desc_pesquisa = request.getParameter("desc_pesquisa") == null ? "" : request.getParameter("desc_pesquisa");
		String valini = request.getParameter("valini") == null ? "" : request.getParameter("valini");
		String valfim = request.getParameter("valfim") == null ? "" : request.getParameter("valfim");
		String idproddistr = request.getParameter("idproddistr") == null ? "" : request.getParameter("idproddistr");
		String fp_flag_entre_ret = request.getParameter("fp_flag_entre_ret") == null ? "" : request.getParameter("fp_flag_entre_ret");
		String fp_ordem = request.getParameter("fp_ordem") == null ? "" : request.getParameter("fp_ordem");

		if (!fp_flag_entre_ret.equals("T") && !fp_flag_entre_ret.equals("L")) {
			throw new Exception("Você deve escolher um tipo de serviço!");
		}

		if (!fp_ordem.equals("N") && !fp_ordem.equals("P")) {
			fp_ordem = "P";
		}
		
		
		if (fp_flag_entre_ret.equalsIgnoreCase("T")) {
			if (cod_bairro.equalsIgnoreCase("")) {
				throw new Exception("Nenhum bairro informado!");
			}
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

		if (fp_flag_entre_ret.equalsIgnoreCase("L")) {

			sql = new StringBuffer();
			sql.append("select * from (SELECT produtos.id_prod, ");
			sql.append("               desc_prod, ");
			sql.append("               desc_abreviado, ");
			sql.append("               produtos_distribuidora.val_prod, ");
			sql.append("               distribuidora.id_distribuidora, ");
			sql.append("               produtos_distribuidora.id_prod_dist, ");
			sql.append("               desc_nome_abrev, ");
			sql.append("               carrinho_item.qtd, ");
			sql.append("               distribuidora.val_entrega_min, ");
			sql.append("               distribuidora.flag_entre_ret ");
			sql.append("        FROM   produtos ");
			sql.append("               INNER JOIN produtos_distribuidora ");
			sql.append("                       ON produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append("               INNER JOIN distribuidora ");
			sql.append("                       ON produtos_distribuidora.id_distribuidora = ");
			sql.append("                          distribuidora.id_distribuidora ");
			sql.append(" ");
			sql.append("               LEFT JOIN carrinho ");
			sql.append("                      ON carrinho.id_usuario =  " + cod_usuario);
			sql.append("               LEFT JOIN carrinho_item ");
			sql.append("                      ON carrinho_item.id_prod_dist = ");
			sql.append("                         produtos_distribuidora.id_prod_dist ");
			sql.append("                         AND carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("        WHERE  produtos_distribuidora.flag_ativo = 'S' ");
			sql.append("               AND distribuidora.flag_ativo_master = 'S' ");
			sql.append("               AND distribuidora.flag_ativo = 'S' ");
			sql.append(" ");
			sql.append(" ");
			sql.append(") as tab");

		} else {

			sql = new StringBuffer();
			sql.append("select * from ( ");
			sql.append(" ");
			sql.append(" select ");
			sql.append(" ");
			sql.append("  produtos.ID_PROD, ");
			sql.append(" DESC_PROD, ");
			sql.append(" DESC_ABREVIADO, ");
			sql.append(" produtos_distribuidora.VAL_PROD, ");
			sql.append(" distribuidora.ID_DISTRIBUIDORA, ");
			sql.append(" produtos_distribuidora.ID_PROD_DIST, ");
			sql.append(" DESC_NOME_ABREV , ");
			sql.append(" carrinho_item.QTD, ");
			sql.append(" distribuidora.VAL_ENTREGA_MIN,distribuidora.flag_entre_ret ");
			sql.append(" ");
			sql.append(" from produtos ");
			sql.append(" inner join produtos_distribuidora ");
			sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append(" inner join distribuidora ");
			sql.append(" on produtos_distribuidora.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
			sql.append(" inner join distribuidora_bairro_entrega ");
			sql.append(" on distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
			sql.append(" inner join distribuidora_horario_dia_entre ");
			sql.append(" on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO   = distribuidora_bairro_entrega.ID_DISTR_BAIRRO ");
			sql.append("  ");
			sql.append(" left join carrinho ");
			sql.append(" on carrinho.id_usuario =   " + cod_usuario);
			sql.append("  ");
			sql.append(" ");
			sql.append(" left join carrinho_item ");
			sql.append(" on carrinho_item.ID_PROD_DIST = produtos_distribuidora.ID_PROD_DIST and carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("  ");
			sql.append("  ");
			sql.append(" ");
			sql.append(" where produtos_distribuidora.FLAG_ATIVO = 'S' and distribuidora.FLAG_ATIVO_MASTER ='S' and distribuidora.FLAG_ATIVO='S' ");
			sql.append(" and distribuidora_bairro_entrega.cod_bairro = ?  and cod_dia = ? and ? between horario_ini and horario_fim and flag_custom = 'N'  ");
			sql.append("  ");
			sql.append("union all ");
			sql.append("  ");
			sql.append(" select ");
			sql.append("  produtos.ID_PROD, ");
			sql.append(" DESC_PROD, ");
			sql.append(" DESC_ABREVIADO, ");
			sql.append(" produtos_distribuidora.VAL_PROD, ");
			sql.append(" distribuidora.ID_DISTRIBUIDORA, ");
			sql.append(" produtos_distribuidora.ID_PROD_DIST, ");
			sql.append(" DESC_NOME_ABREV , ");
			sql.append(" carrinho_item.QTD, ");
			sql.append(" distribuidora.VAL_ENTREGA_MIN,distribuidora.flag_entre_ret ");
			sql.append("  ");
			sql.append(" ");
			sql.append(" from produtos ");
			sql.append(" inner join produtos_distribuidora ");
			sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append(" inner join distribuidora ");
			sql.append(" on produtos_distribuidora.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
			sql.append(" inner join distribuidora_bairro_entrega ");
			sql.append(" on distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
			sql.append(" inner join distribuidora_horario_dia_entre ");
			sql.append(" on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO   = distribuidora_bairro_entrega.ID_DISTR_BAIRRO ");
			sql.append("  ");
			sql.append(" left join carrinho ");
			sql.append(" on carrinho.id_usuario =   " + cod_usuario);
			sql.append("  ");
			sql.append(" ");
			sql.append(" left join carrinho_item ");
			sql.append(" on carrinho_item.ID_PROD_DIST = produtos_distribuidora.ID_PROD_DIST and carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("  ");
			sql.append(" where produtos_distribuidora.FLAG_ATIVO = 'S' and distribuidora.FLAG_ATIVO_MASTER ='S' and distribuidora.FLAG_ATIVO='S' ");
			sql.append(" and distribuidora_bairro_entrega.cod_bairro = ?  and cod_dia = 8 and ? between horario_ini and horario_fim and flag_custom = 'S' ");
			sql.append("  ");
			sql.append(") as  tab ");
			sql.append(" ");
			sql.append(" ");

		}

		sql.append(" where 1=1 ");

		if (listagem) {// se for listagem de todos produtos ou de um especifico, tela de detalhes.
			sql.append(" and tab.desc_prod  like ? ");
		} else {
			// sql.append(" and tab.id_prod = ? ");
			sql.append(" and tab.ID_PROD_DIST  =  ?  ");
		}

		if (!distribuidora.equalsIgnoreCase("")) {
			sql.append(" and tab.id_distribuidora = ?");
		}

		if (!valini.equalsIgnoreCase("")) {
			sql.append("  and  tab.VAL_PROD >= ? ");
		}

		if (!valfim.equalsIgnoreCase("")) {
			sql.append("  and  tab.VAL_PROD <= ? ");
		}

		/*
		 * if (fp_flag_entre_ret.equalsIgnoreCase("A")) {
		 * 
		 * sql.append("  and  tab.flag_entre_ret in ('T','L','A') ");
		 * 
		 * } else
		 */
		if (!fp_flag_entre_ret.equalsIgnoreCase("")) {

			sql.append("  and  (tab.flag_entre_ret = ? or tab.flag_entre_ret='A')");
		}

		if(fp_ordem.equalsIgnoreCase("P")){
			sql.append(" order by tab.val_prod asc limit 20");
		}else if(fp_ordem.equalsIgnoreCase("N")){
			sql.append(" order by tab.DESC_PROD desc limit 20");	
		}
		

		Calendar cal = Calendar.getInstance();

		int contparam = 1;

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		st = conn.prepareStatement(sql.toString());

		if (fp_flag_entre_ret.equalsIgnoreCase("T")) {
			st.setInt(contparam, Integer.parseInt(cod_bairro));
			contparam++;

			st.setInt(contparam, Utilitario.diaSemanaSimple(conn)); //
			contparam++;

			st.setString(contparam, sdf.format(cal.getTime()));
			contparam++;

			st.setInt(contparam, Integer.parseInt(cod_bairro));
			contparam++;

			st.setString(contparam, sdf.format(cal.getTime()));
			contparam++;
		}
		if (listagem) {
			st.setString(contparam, "%" + desc_pesquisa + "%");
			contparam++;
		} else {
			st.setInt(contparam, Integer.parseInt(idproddistr));
			contparam++;
		}

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

		/*
		 * if (fp_flag_entre_ret.equalsIgnoreCase("A")) { // faz nada, fitro ja setado em cima
		 * 
		 * } else
		 */ if (!fp_flag_entre_ret.equalsIgnoreCase("")) {
			st.setString(contparam, (fp_flag_entre_ret));
			contparam++;

		}
		System.out.println(sql.toString());
		rs = st.executeQuery();

		if (listagem) {

			JSONArray prods = new JSONArray();
			while (rs.next()) {
				JSONObject prod = new JSONObject();
				prod.put("ID_PROD", rs.getString("ID_PROD"));
				prod.put("ID_PROD_DIST", rs.getString("ID_PROD_DIST"));
				prod.put("QTD", rs.getString("QTD") == null ? "" : rs.getString("QTD"));
				prod.put("DESC_PROD", rs.getString("DESC_PROD"));
				prod.put("DESC_ABREVIADO", rs.getString("DESC_ABREVIADO"));// abreviado do produto
				prod.put("VAL_PROD", "R$ " + df2.format(rs.getDouble("VAL_PROD")));//
				prod.put("ID_DISTRIBUIDORA", rs.getString("ID_DISTRIBUIDORA"));
				prod.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));/// abreviado da distribuidora
				prods.add(prod);
			}
			double valcar = retornaValCarrinho(cod_usuario, conn);
			if (valcar != 0) {
				retorno.put("temcar", true);
			} else {
				retorno.put("temcar", false);
			}
			retorno.put("fp_distr", distribuidora + "");
			retorno.put("valcar", df2.format(valcar));
			retorno.put("prods", prods);

		} else {

			if (rs.next()) {

				retorno.put("ID_PROD", rs.getString("ID_PROD"));
				retorno.put("ID_PROD_DIST", rs.getString("ID_PROD_DIST"));
				retorno.put("QTD", rs.getString("QTD") == null ? "" : rs.getInt("QTD"));
				retorno.put("DESC_PROD", rs.getString("DESC_PROD"));
				retorno.put("DESC_ABREVIADO", rs.getString("DESC_ABREVIADO"));// abreviado do produto
				retorno.put("VAL_PROD", rs.getDouble("VAL_PROD"));//
				retorno.put("ID_DISTRIBUIDORA", rs.getString("ID_DISTRIBUIDORA"));
				retorno.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));/// abreviado da distribuidora
				retorno.put("val_minentrega", rs.getString("VAL_ENTREGA_MIN"));///
				retorno.put("valcar", df2.format(retornaValCarrinho(cod_usuario, conn) - (rs.getInt("QTD") * rs.getDouble("VAL_PROD"))));
			}

		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	private static double retornaValCarrinho(long cod_usuario, Connection conn) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(val_prod*qtd) as valcar from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho.id_carrinho = carrinho_item.id_carrinho ");
		sql.append("where id_usuario = " + cod_usuario);
		double valcar = 0;
		PreparedStatement st = conn.prepareStatement(sql.toString());
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			valcar = (rs.getDouble("valcar"));
		}

		return valcar;
	}

	private static void carregaValCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();
		retorno.put("valcar", df2.format(retornaValCarrinho(cod_usuario, conn)));
		out.print(retorno.toJSONString());

	}

	private static void limparCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();
		StringBuffer sql = new StringBuffer();

		sql.append(" select * from carrinho  where ID_usuario = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			sql = new StringBuffer();
			sql.append(" delete from carrinho_item where ID_CARRINHO = ? ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, rs.getLong("ID_CARRINHO"));
			st.executeUpdate();

			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho  where ID_CARRINHO = ? ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, rs.getLong("ID_CARRINHO"));
			st.executeUpdate();
		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());

	}

	private static void carregaPedidos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();

		String numero = request.getParameter("numero") == null ? "" : request.getParameter("numero");
		String dataini = request.getParameter("dataini") == null ? "" : request.getParameter("dataini");
		String datafim = request.getParameter("datafim") == null ? "" : request.getParameter("datafim");
		String valini = request.getParameter("val_ini") == null ? "" : request.getParameter("val_ini");
		String valfim = request.getParameter("val_fim") == null ? "" : request.getParameter("val_fim");
		String flagsituacao = request.getParameter("flagsituacao") == null ? "" : request.getParameter("flagsituacao");
		String distribuidora = request.getParameter("distribuidora") == null ? "" : request.getParameter("distribuidora");
		String desc_prod = request.getParameter("desc_prod") == null ? "" : request.getParameter("desc_prod");
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");

		StringBuffer sql = new StringBuffer();
		sql.append(" select pedido.cod_bairro,NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,Coalesce(VAL_ENTREGA,0) as VAL_ENTREGA,DESC_NOME_ABREV,id_pedido,FLAG_PEDIDO_RET_ENTRE from pedido ");
		sql.append(" inner join distribuidora ");
		sql.append(" on distribuidora.ID_DISTRIBUIDORA  = pedido.ID_DISTRIBUIDORA ");

		if (!numero.equalsIgnoreCase("")) {
			sql.append(" and NUM_PED = ? ");
		}

		if (!(dataini.equalsIgnoreCase(""))) {
			sql.append("  and  data_pedido >= ?");
		}

		if (!(datafim.equalsIgnoreCase("")) && datafim != null) {
			sql.append(" and  data_pedido <= ?");
		}

		if (!valini.equalsIgnoreCase("")) {
			sql.append("  and  (VAL_TOTALPROD + VAL_ENTREGA) >= ? ");
		}

		if (!valfim.equalsIgnoreCase("")) {
			sql.append("  and  (VAL_TOTALPROD + VAL_ENTREGA) <= ? ");
		}

		if (!flagsituacao.equalsIgnoreCase("")) {
			sql.append("  and  FLAG_STATUS = ? ");
		}

		if (!distribuidora.equalsIgnoreCase("")) {
			sql.append("  and  pedido.ID_DISTRIBUIDORA = ? ");
		}

		if (!desc_prod.equalsIgnoreCase("")) {
			sql.append(" and exists (select 1 from pedido_item where pedido_item.id_pedido  = 	pedido.id_pedido and id_prod in (select id_prod from produtos where desc_prod like ?) )");
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

		if (!desc_prod.equalsIgnoreCase("")) {

			st.setString(contparam, "%" + desc_prod + "%");
			contparam++;
		}

		if (!cod_bairro.equalsIgnoreCase("")) {
			st.setLong(contparam, Long.parseLong(cod_bairro));
			contparam++;
		}

		st.setLong(contparam, cod_usuario);
		double maxvalue = 0.0;
		ResultSet rs = st.executeQuery();
		JSONObject ret = new JSONObject();
		JSONArray prods = new JSONArray();
		while (rs.next()) {

			maxvalue = maxvalue < rs.getDouble("VAL_TOTALPROD") + rs.getDouble("VAL_ENTREGA") ? rs.getDouble("VAL_TOTALPROD") + rs.getDouble("VAL_ENTREGA") : maxvalue;
			JSONObject prod = new JSONObject();
			prod.put("NUM_PED", rs.getString("NUM_PED"));
			prod.put("DATA_PEDIDO", new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("DATA_PEDIDO")));
			prod.put("VAL_TOTAL", df2.format(rs.getDouble("VAL_TOTALPROD") + rs.getDouble("VAL_ENTREGA")));
			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T")) {
				prod.put("FLAG_STATUS", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
				prod.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));
			} else {
				if (rs.getString("FLAG_STATUS").equalsIgnoreCase("E")) {
					prod.put("FLAG_STATUS", Utilitario.returnStatusPedidoFlag("S"));// se for do tipo local, retorna "em espera"
				} else {
					prod.put("FLAG_STATUS", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
				}
				prod.put("desc_bairro", "Retirar no local");

			}

			prod.put("DESC_NOME_ABREV", rs.getString("DESC_NOME_ABREV"));
			prod.put("id_pedido", rs.getString("id_pedido"));

			prods.add(prod);
		}

		ret.put("pedidos", prods);
		ret.put("maxvalue", Math.ceil(maxvalue));

		out.print(ret.toJSONString());
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
		sql.append("       DESC_NOME_ABREV, FLAG_PEDIDO_RET_ENTRE,TEMPO_ESTIMADO_DESEJADO,FLAG_PEDIDO_RET_ENTRE,");
		sql.append("       flag_status,id_pedido,bairros.desc_bairro ,desc_endereco_num_entrega,desc_endereco_complemento_entrega ");
		sql.append(" FROM   pedido ");
		sql.append("       INNER JOIN distribuidora ");
		sql.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
		sql.append("       INNER JOIN bairros ");
		sql.append("               ON bairros.cod_bairro = pedido.cod_bairro ");
		sql.append(" WHERE  id_pedido = ? ");
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
			if (rs.getTimestamp("DATA_PEDIDO_RESPOSTA") != null) {
				ped.put("data_pedidoresp", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("DATA_PEDIDO_RESPOSTA")));
			} else {
				ped.put("data_pedidoresp", "Não respondido.");
			}
			ped.put("val_totalprod", "R$ " + df2.format(rs.getDouble("VAL_TOTALPROD")));

			if (rs.getString("FLAG_PEDIDO_RET_ENTRE").equalsIgnoreCase("T")) {
				ped.put("flag_status", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
			} else {
				if (rs.getString("FLAG_STATUS").equalsIgnoreCase("E")) {
					ped.put("flag_status", Utilitario.returnStatusPedidoFlag("S"));// se for do tipo local, retorna "em espera"
				} else {
					ped.put("flag_status", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));
				}

			}

			ped.put("flag_status", Utilitario.returnStatusPedidoFlag(rs.getString("FLAG_STATUS")));

			ped.put("tempo_entrega_max", rs.getTimestamp("TEMPO_ESTIMADO_DESEJADO") == null ? "" : new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_DESEJADO")));

			ped.put("desc_serv", Utilitario.returnDistrTiposPedido(rs.getString("FLAG_PEDIDO_RET_ENTRE")));
			ped.put("desc_nome_abrev", rs.getString("DESC_NOME_ABREV"));
			ped.put("id_pedido", rs.getString("id_pedido"));
			ped.put("val_entrega", "R$ " + df2.format(rs.getDouble("val_entrega")));

			ped.put("val_total", "R$ " + df2.format(rs.getDouble("val_entrega") + rs.getDouble("VAL_TOTALPROD")));
			String end = rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega");
			String num = rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega");
			String compl = rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega");

			ped.put("endereco_completo", end + " " + num + " " + compl);
			ped.put("desc_razao_social", rs.getString("desc_razao_social"));
			ped.put("desc_bairro", rs.getString("desc_bairro"));
			ped.put("desc_endereco_entrega", rs.getString("desc_endereco_entrega"));
			ped.put("desc_endereco_num_entrega", rs.getString("desc_endereco_num_entrega"));
			ped.put("desc_endereco_complemento_entrega", rs.getString("desc_endereco_complemento_entrega"));
			ped.put("tempo_entrega", rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA") == null ? "" : new SimpleDateFormat("HH:mm").format(rs.getTimestamp("TEMPO_ESTIMADO_ENTREGA")));

			StringBuffer sql2 = new StringBuffer();
			sql2.append("select DESC_PROD, VAL_UNIT, QTD_PROD, QTD_PROD * VAL_UNIT   as total, DESC_ABREVIADO, produtos.id_prod from pedido_item ");
			sql2.append("inner join produtos ");
			sql2.append("on produtos.ID_PROD  = pedido_item.ID_PROD ");
			sql2.append("where id_pedido = ? order by desc_prod");

			JSONArray produtos = new JSONArray();

			PreparedStatement st2 = conn.prepareStatement(sql2.toString());
			st2.setLong(1, Long.parseLong(id_pedido));
			ResultSet rs2 = st2.executeQuery();

			while (rs2.next()) {
				JSONObject prod = new JSONObject();

				prod.put("desc_prod", rs2.getString("DESC_PROD"));
				prod.put("val_unit", df2.format(rs2.getDouble("VAL_UNIT")));
				prod.put("qtd_prod", rs2.getString("QTD_PROD"));
				prod.put("total", df2.format(rs2.getDouble("total")));
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

	private static void carregaCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, boolean single) throws Exception {
		PrintWriter out = response.getWriter();
		String idprodistr = request.getParameter("idprodistr") == null ? "" : request.getParameter("idprodistr");
		StringBuffer sql = new StringBuffer();
		sql.append("select carrinho_item.ID_PROD_DIST,carrinho_item.id_carrinho, carrinho.cod_bairro ,desc_bairro, seq_item, qtd,carrinho_item.val_prod,produtos_distribuidora.id_distribuidora, DESC_RAZAO_SOCIAL,VAL_ENTREGA_MIN , produtos.DESC_PROD, produtos.DESC_ABREVIADO, ");
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
		sql.append("left join distribuidora_bairro_entrega ");
		sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" ");
		sql.append("where id_usuario = ? ");
		if (single) {
			sql.append("and carrinho_item.ID_PROD_DIST = ? ");
		}

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		if (single) {
			st.setLong(2, Long.parseLong(idprodistr));
		}
		ResultSet rs = st.executeQuery();
		JSONObject carrinho = new JSONObject();

		String id_carrinho = "";
		String cod_bairro = "";
		String desc_bairro = "";
		String id_distribuidora = "";
		String DESC_RAZAO_SOCIAL = "";
		String VAL_ENTREGA_MIN = "";
		double subtotal = 0;
		double frete = 0;
		JSONArray carrinhoitem = new JSONArray();
		if (!single) {
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				// preguiça
				id_carrinho = rs.getString("id_carrinho");
				cod_bairro = rs.getString("cod_bairro");
				desc_bairro = rs.getString("desc_bairro");
				obj.put("seq_item", rs.getString("seq_item"));
				obj.put("id_prod_dist", rs.getString("ID_PROD_DIST"));
				obj.put("qtd", rs.getString("qtd"));
				obj.put("val_prod", rs.getString("val_prod"));
				obj.put("total", rs.getInt("qtd") * rs.getDouble("val_prod"));
				obj.put("desc_prod", rs.getString("DESC_PROD"));
				obj.put("desc_abreviado", rs.getString("DESC_ABREVIADO"));
				id_distribuidora = rs.getString("id_distribuidora");
				DESC_RAZAO_SOCIAL = rs.getString("DESC_RAZAO_SOCIAL");
				VAL_ENTREGA_MIN = rs.getString("VAL_ENTREGA_MIN");
				subtotal = subtotal + (rs.getInt("qtd") * rs.getDouble("val_prod"));
				frete = rs.getDouble("VAL_TELE_ENTREGA");

				carrinhoitem.add(obj);

			}

			carrinho.put("id_carrinho", id_carrinho);
			carrinho.put("cod_bairro", cod_bairro);
			carrinho.put("desc_bairro", desc_bairro);
			carrinho.put("id_distribuidora", id_distribuidora);
			carrinho.put("desc_razao_social", DESC_RAZAO_SOCIAL);
			carrinho.put("VAL_ENTREGA_MIN", VAL_ENTREGA_MIN);
			carrinho.put("val_subtotal", subtotal);
			carrinho.put("val_frete", frete);
			carrinho.put("val_totalcarrinho", subtotal + frete);

			carrinho.put("produtos", carrinhoitem);

			sql = new StringBuffer();
			sql.append(" select * from usuario ");
			sql.append(" where id_usuario = ? ");

			st = conn.prepareStatement(sql.toString());
			st.setLong(1, (cod_usuario));

			rs = st.executeQuery();

			if (rs.next()) {
				JSONObject obj = new JSONObject();
				carrinho.put("desc_mail", rs.getString("DESC_EMAIL"));
				carrinho.put("desc_endereco", rs.getString("DESC_ENDERECO"));
				carrinho.put("desc_endereco_num", rs.getString("DESC_ENDERECO_NUM"));
				carrinho.put("desc_endereco_complemento", rs.getString("DESC_ENDERECO_COMPLEMENTO"));
				carrinho.put("desc_cartao", rs.getString("DESC_CARTAO"));
				carrinho.put("data_exp_mes", rs.getString("DATA_EXP_MES"));
				carrinho.put("data_exp_ano", rs.getString("DATA_EXP_ANO"));
				carrinho.put("desc_cardholdername", rs.getString("DESC_CARDHOLDERNAME"));
				carrinho.put("pay_id", rs.getString("PAY_ID"));
				carrinho.put("desc_cpf", rs.getString("DESC_CPF"));

				carrinho.put("seccode", "");
				carrinho.put("doctype", "CPF");

			}

		} else {

			if (rs.next()) {
				carrinho.put("qtd", rs.getString("qtd"));
			} else {
				carrinho.put("qtd", 0);
			}

		}

		// System.out.println(carrinho.toJSONString());
		out.print(carrinho.toJSONString());
	}

	private static void carregaEnderecos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		String descender = request.getParameter("descender") == null ? "" : request.getParameter("descender"); // precisa receber

		StringBuffer sql = new StringBuffer();
		sql.append(" select DESC_ENDERECO_ENTREGA,DESC_ENDERECO_NUM_ENTREGA,DESC_ENDERECO_COMPLEMENTO_ENTREGA from pedido");
		sql.append(" where id_usuario = ?  and   (CONCAT(Coalesce(DESC_ENDERECO_ENTREGA,''),Coalesce(DESC_ENDERECO_NUM_ENTREGA,''),Coalesce(DESC_ENDERECO_COMPLEMENTO_ENTREGA,''))  like ? )");
		sql.append(" group by DESC_ENDERECO_ENTREGA,DESC_ENDERECO_NUM_ENTREGA,DESC_ENDERECO_COMPLEMENTO_ENTREGA  order by DESC_ENDERECO_ENTREGA ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		st.setString(2, "%" + descender + "%");

		ResultSet rs = st.executeQuery();
		JSONArray enderecos = new JSONArray();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			String edereco = rs.getString("DESC_ENDERECO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_ENTREGA");
			String desc_endereco_num = rs.getString("DESC_ENDERECO_NUM_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_NUM_ENTREGA");
			String desc_endereco_complemento = rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA");

			obj.put("descender", edereco + " " + desc_endereco_num + " " + desc_endereco_complemento);

			obj.put("desc_endereco_entrega", rs.getString("DESC_ENDERECO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_ENTREGA"));
			obj.put("desc_endereco_num_entrega", rs.getString("DESC_ENDERECO_NUM_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_NUM_ENTREGA"));
			obj.put("desc_endereco_complemento_entrega", rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA") == null ? "" : rs.getString("DESC_ENDERECO_COMPLEMENTO_ENTREGA"));

			enderecos.add(obj);

		}

		retorno.put("enderecos", enderecos);

		out.print(retorno.toJSONString());
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
		retorno.put("temcar", jatemcarrinho);
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
				retorno.put("temcar", false);
			}

		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	private static void recalcularCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		PreparedStatement st2 = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id_carrinho from carrinho where id_usuario = ? ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		long idcarrinho = 0;
		if (rs.next()) {// ja tem carrinho

			idcarrinho = rs.getLong("id_carrinho");
		}

		if (idcarrinho == 0) {
			throw new Exception("Seu carrinho está vazio!");
		}

		sql = new StringBuffer();// testa se o produto existe e pega id da distribuidora relacionada ao produto
		sql.append(" select carrinho_item.* , produtos_distribuidora.val_prod as val_correto from carrinho_item inner join produtos_distribuidora on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST ");
		sql.append(" where ID_CARRINHO = " + idcarrinho + " ");
		st = conn.prepareStatement(sql.toString());

		rs = st.executeQuery();
		while (rs.next()) {
			double valprod_car = rs.getDouble("val_prod");
			double valprod_dis = rs.getDouble("val_correto");
			if (valprod_car != valprod_dis) {
				sql = new StringBuffer();
				sql.append("UPDATE carrinho_item ");
				sql.append("   SET val_prod = ? ");
				sql.append("WHERE  id_carrinho = ? and seq_item  = ? ");
				st2 = conn.prepareStatement(sql.toString());
				st2.setDouble(1, (valprod_dis));
				st2.setLong(2, (idcarrinho));
				st2.setInt(3, rs.getInt("seq_item"));
				st2.executeUpdate();
			}

		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());
	}

	private static void removerItemCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		// String id_prod = request.getParameter("id_prod") == null ? "" : request.getParameter("id_prod");
		String id_proddistr = request.getParameter("id_proddistr") == null ? "" : request.getParameter("id_proddistr"); // precisa receber

		boolean jatemcarrinho = false;
		long idcarrinho = 0;

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

		if (jatemcarrinho) {
			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho_item ");
			sql.append(" where ID_PROD_DIST = ? and id_carrinho  = ?  ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, Long.parseLong(id_proddistr));
			st.setLong(2, (idcarrinho));
			st.executeUpdate();
		}

		sql = new StringBuffer();
		sql.append(" select 1 from carrinho_item ");
		sql.append("where id_carrinho = ?  ");
		st = conn.prepareStatement(sql.toString());
		st.setLong(1, (idcarrinho));
		rs = st.executeQuery();
		retorno.put("temcar", true);
		if (!rs.next()) {

			sql = new StringBuffer();//
			sql.append(" delete  from carrinho ");
			sql.append("where id_carrinho = ?  ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, (idcarrinho));
			st.executeUpdate();
			retorno.put("temcar", false);
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

	private static void validacaoTipoServico(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_distribuidora, String tiposerv) throws Exception {

		{
			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual

			sql.append(" select flag_entre_ret,DESC_NOME_ABREV from distribuidora  ");
			sql.append(" where distribuidora.id_distribuidora  = ? ");

			PreparedStatement st = conn.prepareStatement(sql.toString());

			st.setLong(1, (id_distribuidora));
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				String servdistr = rs.getString("flag_entre_ret");
				if (servdistr.equalsIgnoreCase("A")) {
					return;
				} else if (!servdistr.equalsIgnoreCase(tiposerv)) {
					throw new Exception("A distribuidora " + rs.getString("DESC_NOME_ABREV") + " não disponibiliza o tipo de serviço: " + Utilitario.returntipoPedido(tiposerv));
				}

			}
		}

	}

	private static double validacaoFlagsDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, int id_distribuidora_prod) throws Exception {
		{

			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual
			sql.append(" select FLAG_ATIVO,FLAG_ATIVO_MASTER,VAL_ENTREGA_MIN from  distribuidora where id_distribuidora = ? ");
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

				return rs.getDouble("VAL_ENTREGA_MIN");// aproveitei pra trazer o valor junto, pra nao precisar faze otra função e consulta
			}

		}
		return 0;
	}

	private static void validacaoProdutoDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_distribuidora_prod, double valprod_carrinho) throws Exception {

		StringBuffer sql = new StringBuffer();// teste para ver se o valor do carrinho é igual ao valor do produto.
		sql.append("select  * from produtos_distribuidora  where  ID_PROD_DIST = ? and flag_ativo = 'S' ");
		PreparedStatement st3 = conn.prepareStatement(sql.toString());
		st3.setLong(1, id_distribuidora_prod);
		ResultSet rs3 = st3.executeQuery();
		if (rs3.next()) {
			if (rs3.getDouble("VAL_PROD") != valprod_carrinho) {
				throw new Exception("O produto " + Utilitario.getNomeProdIdProdDistr(conn, id_distribuidora_prod, true) + " que está no carrinho tem um valor diferente que se encontra na distribuida. Isto pode acontecer quando a distribuidora modifica o valor de um produto durante sua compra. Por favor clique em recalcular carrinho.");
			}
		} else {
			throw new Exception("O produto " + Utilitario.getNomeProdIdProdDistr(conn, id_distribuidora_prod, true) + " não se encontra disponível. Por favor remova-o de seu carrinho.");
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

	private static void testesMudaBairroCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		String novobairro = request.getParameter("novobairro") == null ? "" : request.getParameter("novobairro");

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE carrinho ");
		sql.append("   SET cod_bairro = ? ");
		sql.append("WHERE  id_usuario = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, Integer.parseInt(novobairro));
		st.setLong(2, cod_usuario);
		st.executeUpdate();

		long idcarrinho = 0;

		sql = new StringBuffer();
		sql.append("select usuario.DESC_EMAIL, usuario. DESC_NOME, carrinho_item.id_carrinho,distribuidora.ID_DISTRIBUIDORA,carrinho.cod_bairro,usuario.DESC_TELEFONE,usuario.DESC_ENDERECO,DESC_ENDERECO_NUM,DESC_ENDERECO_COMPLEMENTO,sum(produtos_distribuidora.val_prod * carrinho_item.qtd ) as val_prod, ");
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
		sql.append("left join distribuidora_bairro_entrega ");
		sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.ID_DISTRIBUIDORA = distribuidora.ID_DISTRIBUIDORA ");
		sql.append(" ");
		sql.append("inner join usuario ");
		sql.append("on usuario.id_usuario = carrinho.id_usuario ");
		sql.append("  ");
		sql.append("where usuario.id_usuario = ? ");
		sql.append(" ");
		sql.append("group by usuario.DESC_EMAIL, usuario. DESC_NOME, carrinho_item.id_carrinho,distribuidora_bairro_entrega.ID_DISTRIBUIDORA,carrinho.cod_bairro,usuario.DESC_TELEFONE,usuario.DESC_ENDERECO,DESC_ENDERECO_NUM,DESC_ENDERECO_COMPLEMENTO, VAL_TELE_ENTREGA");

		st = conn.prepareStatement(sql.toString());
		PreparedStatement st2 = null;

		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		ResultSet rs2 = null;

		if (rs.next()) {
			idcarrinho = rs.getLong("id_carrinho");

			validacaoFlagsDistribuidora(request, response, conn, rs.getInt("ID_DISTRIBUIDORA"));
			validacaoDisBairroHora(request, response, conn, rs.getInt("cod_bairro"), rs.getInt("ID_DISTRIBUIDORA"));
			validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, rs.getInt("ID_DISTRIBUIDORA"));

			sql = new StringBuffer();
			sql.append("select * from carrinho_item inner join produtos_distribuidora on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST  where id_carrinho = ? ");

			st2 = conn.prepareStatement(sql.toString());
			st2.setLong(1, idcarrinho);
			rs2 = st2.executeQuery();
			while (rs2.next()) {

				validacaoProdutoDistribuidora(request, response, conn, rs2.getLong("ID_PROD_DIST"), rs2.getDouble("VAL_PROD"));

			}
		}

		retorno.put("msg", "ok");

		out.print(retorno.toJSONString());

	}

	private static void criarPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		String tipo_pagamento = request.getParameter("tipo_pagamento") == null ? "" : request.getParameter("tipo_pagamento");
		String desc_endereco = request.getParameter("desc_endereco") == null ? "" : request.getParameter("desc_endereco");
		String desc_endereco_num = request.getParameter("desc_endereco_num") == null ? "" : request.getParameter("desc_endereco_num");
		String desc_endereco_complemento = request.getParameter("desc_endereco_complemento") == null ? "" : request.getParameter("desc_endereco_complemento");
		String tempomax = request.getParameter("tempo_estimado_desejado") == null ? "" : request.getParameter("tempo_estimado_desejado");
		String choiceserv = request.getParameter("choiceserv") == null ? "" : request.getParameter("choiceserv");

		if (!choiceserv.equalsIgnoreCase("T") && !choiceserv.equalsIgnoreCase("L")) {
			throw new Exception("Tipo de serviço inválido.");
		}

		if (!tipo_pagamento.equalsIgnoreCase("D") && !tipo_pagamento.equalsIgnoreCase("C")) {
			throw new Exception("Tipo de pagamento inválido.");
		}

		if (choiceserv.equalsIgnoreCase("T")) {
			if (desc_endereco.equalsIgnoreCase("")) {
				throw new Exception("Seu endereço está em branco!");
			}

			if (desc_endereco_num.equalsIgnoreCase("")) {
				throw new Exception("Seu número de endereço está em branco!");
			}

			Utilitario.testeHora("HHmm", tempomax, "Tempo de entrega inválidos!");

		}

		long idcarrinho = 0;

		StringBuffer sql = new StringBuffer();
		sql.append("select usuario.DESC_EMAIL, usuario. DESC_NOME, carrinho_item.id_carrinho,distribuidora.ID_DISTRIBUIDORA,carrinho.cod_bairro,usuario.DESC_TELEFONE,usuario.DESC_ENDERECO,DESC_ENDERECO_NUM,DESC_ENDERECO_COMPLEMENTO,sum(produtos_distribuidora.val_prod * carrinho_item.qtd ) as val_prod, ");
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
		sql.append("left join distribuidora_bairro_entrega ");
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

		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		ResultSet rs2 = null;

		String email = "";
		if (rs.next()) {
			idcarrinho = rs.getLong("id_carrinho");

			double valmin_entrgega = 0;
			boolean offline = false;
			try {
				valmin_entrgega = validacaoFlagsDistribuidora(request, response, conn, rs.getInt("ID_DISTRIBUIDORA"));
			} catch (Exception e) {
				offline = true;
				retorno.put("offline", true);

			}
			if (!offline) {
				validacaoDisBairroHora(request, response, conn, rs.getInt("cod_bairro"), rs.getInt("ID_DISTRIBUIDORA"));
				validacaoTipoServico(request, response, conn, rs.getInt("ID_DISTRIBUIDORA"), choiceserv);
				validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, rs.getInt("ID_DISTRIBUIDORA"));// se um item do carrinho tiver uma distribuidora diferente, ele vai dar um erro

				double subtotal = 0;
				if (rs.getDouble("val_prod") == 0) {
					throw new Exception("Erro, o valor dos produtos de seu pedido é R$ 0,00!.");
				}
				// testa para ver so tipo de serviço é valido

				sql = new StringBuffer();
				sql.append("INSERT INTO pedido ");
				sql.append("  (`ID_PEDIDO`, `ID_DISTRIBUIDORA`, `ID_USUARIO`, `DATA_PEDIDO`, `FLAG_STATUS`, `VAL_TOTALPROD`, `VAL_ENTREGA`,  `NUM_PED`, `COD_BAIRRO`, `NUM_TELEFONECONTATO_CLIENTE`,  `DESC_ENDERECO_ENTREGA`, `DESC_ENDERECO_NUM_ENTREGA`, `DESC_ENDERECO_COMPLEMENTO_ENTREGA`, flag_vizualizado,FLAG_MODOPAGAMENTO,NOME_PESSOA,FLAG_PEDIDO_RET_ENTRE,TEMPO_ESTIMADO_DESEJADO) ");
				sql.append("VALUES ");
				sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?);");

				st = conn.prepareStatement(sql.toString());

				long idped = Utilitario.retornaIdinsertLong("pedido", "ID_PEDIDO", conn);
				st.setLong(1, idped);
				st.setLong(2, rs.getInt("ID_DISTRIBUIDORA"));
				st.setLong(3, cod_usuario);
				st.setTimestamp(4, Utilitario.getTimeStamp(new Date()));
				st.setString(5, "A");
				st.setDouble(6, rs.getDouble("val_prod"));
				if (choiceserv.equalsIgnoreCase("T")) {
					st.setDouble(7, rs.getDouble("VAL_TELE_ENTREGA"));
				} else {
					st.setDouble(7, 0.0);
				}
				st.setLong(8, Utilitario.getNextNumpad(conn, rs.getInt("ID_DISTRIBUIDORA")));
				st.setInt(9, rs.getInt("cod_bairro"));
				st.setString(10, rs.getString("DESC_TELEFONE"));
				if (choiceserv.equalsIgnoreCase("T")) {
					st.setString(11, desc_endereco);
					st.setString(12, desc_endereco_num);
					st.setString(13, desc_endereco_complemento);
				} else {
					st.setString(11, "");
					st.setString(12, "");
					st.setString(13, "");
				}
				st.setString(14, "N");
				st.setString(15, tipo_pagamento);
				st.setString(16, rs.getString("DESC_NOME"));
				st.setString(17, choiceserv);
				if (choiceserv.equalsIgnoreCase("T")) {

					st.setString(18, tempomax.substring(0, 2) + ":" + tempomax.substring(2, 4));
				} else {
					st.setString(18, "00:00");
				}

				email = rs.getString("DESC_EMAIL");

				st.executeUpdate();

				sql = new StringBuffer();
				sql.append("select * from carrinho_item inner join produtos_distribuidora on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST  where id_carrinho = ? ");

				st2 = conn.prepareStatement(sql.toString());
				st2.setLong(1, idcarrinho);
				rs2 = st2.executeQuery();
				while (rs2.next()) {

					validacaoProdutoDistribuidora(request, response, conn, rs2.getLong("ID_PROD_DIST"), rs2.getDouble("VAL_PROD"));

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
					subtotal = subtotal + (rs2.getDouble("VAL_PROD") * rs2.getLong("QTD"));
					st.executeUpdate();

				}

				if (choiceserv.equalsIgnoreCase("T")) {
					if (valmin_entrgega > subtotal) {
						throw new Exception("Sua pedido não atingiu o valor mínimo de entrega da distribuidora. O valor mínimo é de R$ " + df2.format(valmin_entrgega));
					}
				}

				if (tipo_pagamento.equalsIgnoreCase("C")) {// se for do tipo cartao de credito , tem que salvar as infos

					String token = request.getParameter("token") == null ? "" : request.getParameter("token");
					String paymentMethodId = request.getParameter("pay_id") == null ? "" : request.getParameter("pay_id");

					if (paymentMethodId.equalsIgnoreCase("")) {
						throw new Exception("Seu bandeira de cartão não foi escolhida.");
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
					sql.append("WHERE  `ID_PEDIDO` = ? ");

					st = conn.prepareStatement(sql.toString());
					st.setString(1, token);
					st.setString(2, email);
					st.setString(3, paymentMethodId);
					st.setLong(4, idped);

					st.executeUpdate();

					String salvarinfos = request.getParameter("salvarinfos") == null ? "" : request.getParameter("salvarinfos");

					if (salvarinfos.equalsIgnoreCase("true")) {

						String data_exp_ano = request.getParameter("data_exp_ano") == null ? "" : request.getParameter("data_exp_ano");
						String data_exp_mes = request.getParameter("data_exp_mes") == null ? "" : request.getParameter("data_exp_mes");
						String desc_cardholdername = request.getParameter("desc_cardholdername") == null ? "" : request.getParameter("desc_cardholdername");
						String desc_cpf = request.getParameter("desc_cpf") == null ? "" : request.getParameter("desc_cpf");
						String desc_cartao = request.getParameter("desc_cartao") == null ? "" : request.getParameter("desc_cartao");
						String pay_id = request.getParameter("pay_id") == null ? "" : request.getParameter("pay_id");

						sql = new StringBuffer();
						sql.append("UPDATE usuario ");
						sql.append("   SET  ");
						sql.append("       `DESC_CARTAO` = ?, ");
						sql.append("       `DESC_CARDHOLDERNAME` = ?, ");
						sql.append("       `DATA_EXP_MES` = ?, ");
						sql.append("       `DATA_EXP_ANO` = ?, ");
						sql.append("       `PAY_ID` = ?, ");
						sql.append("       `DESC_CPF` = ? ");
						sql.append("WHERE  `ID_USUARIO` = ?;");

						st = conn.prepareStatement(sql.toString());
						st.setString(1, desc_cartao);
						st.setString(2, desc_cardholdername);

						if (data_exp_mes.equalsIgnoreCase(""))
							st.setNull(3, java.sql.Types.INTEGER);
						else
							st.setInt(3, Integer.parseInt(data_exp_mes));

						if (data_exp_ano.equalsIgnoreCase(""))
							st.setNull(4, java.sql.Types.INTEGER);
						else
							st.setInt(4, Integer.parseInt(data_exp_ano));

						st.setString(5, pay_id);
						st.setString(6, desc_cpf);
						st.setLong(7, cod_usuario);

						st.executeUpdate();

					}

				}

				// realizar pagamento, pensar TODO

				// payment(request, response, conn, cod_usuario,email);

				else {
					throw new Exception("Não há itens em seu carrinho!");
				}

				{
					sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
					sql.append(" delete from carrinho_item where ID_CARRINHO = ? ");
					st = conn.prepareStatement(sql.toString());
					st.setLong(1, (idcarrinho));
					st.executeUpdate();

					sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
					sql.append(" delete from carrinho  where ID_CARRINHO = ? ");
					st = conn.prepareStatement(sql.toString());
					st.setLong(1, (idcarrinho));
					st.executeUpdate();
				}
			}
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