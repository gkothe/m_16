package com.funcs;

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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.jfree.base.modules.SubSystem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.configs.Conexao;
import com.mercadopago.MP;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import io.jsonwebtoken.*;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

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

	protected void doOptions(HttpServletRequest request, HttpServletResponse response) {

		response.setHeader("Access-Control-Allow-Methods", "GET, POST");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "AUTHORIZATION,X-Auth-Token");
	}

	public void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		try {
			/*
			 * System.out.println("----------entro mob");
			 * 
			 * Map map = request.getParameterMap(); for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) { String type = (String) iterator.next(); System.out.println(type + " : " + request.getParameter(type)); }
			 */

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

			if (cmd.equalsIgnoreCase("getapp")) {
				appplicacao(request, response, conn, sys);
			} else if (cmd.equalsIgnoreCase("login")) {
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

			} else if (cmd.equalsIgnoreCase("testedeversao")) {
				testeversao(request, response, conn);
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
				} else if (cmd.equalsIgnoreCase("confirmaIdadeTermos")) {
					confirmaIdadeTermos(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("carrega_flagentreret")) {
					carregaFlagEntreRet(request, response, conn);
				} else if (cmd.equalsIgnoreCase("carrega_situacaoes")) {
					carregaSitucaoes(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaCarrinho")) {
					carregaCarrinho(request, response, conn, cod_usuario, false);
				} else if (cmd.equalsIgnoreCase("carregaItemCarrinho")) {
					carregaCarrinho(request, response, conn, cod_usuario, true);
				} else if (cmd.equalsIgnoreCase("carregaValCarrinho")) {
					carregaValCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("testesMudaBairroCarrinho")) {
					testesMudaBairroCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("testesMudaServico")) {
					testesMudaServico(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("servicoCarrinho")) {
					servicoCarrinho(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("carregaCategorias")) {
					Parametros_ajax.listaCategorias(request, response, conn, 0,false);
				} else if (cmd.equalsIgnoreCase("carregaMarcas")) {
					Parametros_ajax.listaMarcas(request, response, conn, 0, false);
				} else if (cmd.equalsIgnoreCase("listaLojas")) {
					listaLojas(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("detalheLoja")) {
					detalheLoja(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("listaLojasProds")) {
					listaLojasProds(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("listaHorariosBairro")) {
					listaHorariosBairro(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("LojacarregaBairroServ")) {
					LojacarregaBairroServ(request, response, conn, cod_usuario, sys);
				} else if (cod_usuario == sys.getSys_id_visistante()) {// operações daqui para cima, são validas para visitante.
					objRetorno.put("guest", "true");
					throw new Exception("Você está acessando como visitante. Para poder realizar esta operação você deve logar com o Facebook ou criar uma conta no " + sys.getSys_fromdesc());
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
					carregaPedidoUnico(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("addCarrinho")) {
					addCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("recalcularCarrinho")) {
					recalcularCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("removerItemCarrinho")) {
					removerItemCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("criarPedido")) {
					criarPedido(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("limparCarrinho")) {
					limparCarrinho(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaEnderecos")) {
					carregaEnderecos(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("carregaMotivosCancelamento")) {
					carregaMotivosCancelamento(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("loadInfoCanc")) {
					carregaMotivosCancelamento(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("cancelaPedido")) {
					cancelaPedido(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("infosCancel")) {
					infosCancel(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("txt_obs_hora")) {
					txtObsHora(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("txt_horaatendimento")) {
					txtHoraAtend(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("finalizandoPedidoMobile")) {
					Pedidos_ajax.finalizandoPedidoMobile(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("duplicarPedido")) {
					duplicarPedido(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("pedidonaorecebido")) {
					pedidoNaoRecebido(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("loadFreteBairro")) {
					loadFreteBairro(request, response, conn, cod_usuario);
				} else if (cmd.equalsIgnoreCase("enviarMsgContato")) {
					enviarMsgContato(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("recusouTermos")) {
					recusouTermos(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("teste")) {
					teste(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("LojaProdTestOnline")) {
					LojaProdTestOnline(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("isloja")) {
					isLoja(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("carregaPedidosLoja")) {
					carregaPedidosLoja(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("abrePedidoLoja")) {
					abrePedidosLoja(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("loadMotivos")) {
					Home_ajax.loadMotivos(request, response, conn);
				} else if (cmd.equalsIgnoreCase("finalizandoPedidoLoja")) {
					finalizandoPedido(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("responderPedidoLoja")) {
					responderPedidoLoja(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("checkPedidos")) {
					checkPedidos(request, response, conn, cod_usuario, sys);
				} else if (cmd.equalsIgnoreCase("agendarnot")) {
					agendarNot(request, response, conn, cod_usuario, sys);
				}

				else {
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

	private static void responderPedidoLoja(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			if (rs.getString("flag_role").equalsIgnoreCase("V")) {
				throw new Exception("Você nao tem permissão para realizar esta operação.");
			}

			int distribuidora = rs.getInt("id_distribuidora");
			String id_pedido = request.getParameter("id") == null ? "" : request.getParameter("id"); //

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *  ");
			sql.append(" from   pedido ");
			sql.append(" WHERE  id_distribuidora = ? ");
			sql.append("       AND id_pedido = ? ");

			st = conn.prepareStatement(sql.toString());
			st.setInt(1, distribuidora);
			st.setInt(2, Integer.parseInt(id_pedido));
			rs = st.executeQuery();
			if (rs.next()) {

				if (rs.getString("flag_pedido_ret_entre").equalsIgnoreCase("L")) {
					throw new Exception("Pedidos de retirada em local devem ser respondidos pelo sistema web.");
				} else if (rs.getString("flag_pedido_ret_entre").equalsIgnoreCase("T") && rs.getString("flag_modoentrega").equalsIgnoreCase("T")) {
					throw new Exception("Pedidos de tele-entrega devem ser respondidos pelo sistema web.");
				}

				Pedidos_ajax.responderPedido(request, response, conn, distribuidora);
			}

		} else {
			throw new Exception("Operação não acessivel para seu usuário.");

		}

	}

	private static void checkPedidos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			Home_ajax.checkPedidos(request, response, conn, rs.getInt("id_distribuidora"));
		}
	}

	private static void abrePedidosLoja(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			if (rs.getString("flag_role").equalsIgnoreCase("V")) {
				Pedidos_ajax.carregaPedido_AbertoEnvio(request, response, conn, rs.getInt("id_distribuidora"), false);
			} else {
				Pedidos_ajax.carregaPedido_AbertoEnvio(request, response, conn, rs.getInt("id_distribuidora"), true);
			}

		} else {
			throw new Exception("Pedido não acessivel para seu usuário.");
		}

	}

	private static void agendarNot(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido"); //
		String notlater = request.getParameter("notlater") == null ? "" : request.getParameter("notlater"); //

		if (!Utilitario.isNumeric(notlater)) {
			throw new Exception("Você deve escolher uma quantidade de horas válida!");
		}

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			StringBuffer varname1 = new StringBuffer();
			varname1.append("select * from pedido where id_distribuidora = ? and id_pedido = ? ");
			PreparedStatement st2 = conn.prepareStatement(varname1.toString());
			st2.setInt(1, rs.getInt("id_distribuidora"));
			st2.setInt(2, Integer.parseInt(id_pedido));
			ResultSet rs2 = st2.executeQuery();
			if (rs2.next()) {

				GregorianCalendar dataagend = new GregorianCalendar();
				dataagend.setTime(new Date());
				dataagend.add(Calendar.HOUR, Integer.parseInt(notlater));

				varname1 = new StringBuffer();
				varname1.append(" update pedido set data_proxnot = ?  ");
				varname1.append(" WHERE   id_pedido = ? ");
				st = conn.prepareStatement(varname1.toString());
				st.setTimestamp(1, Utilitario.getTimeStamp(dataagend.getTime()));
				st.setLong(2, Integer.parseInt(id_pedido));
				st.executeUpdate();

				objRetorno.put("datanot", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Utilitario.getTimeStamp(dataagend.getTime())));
			}
			objRetorno.put("msg", "ok");
			out.println(objRetorno.toString());
		} else {
			throw new Exception("Acesso inválido!");
		}

	}

	private static void carregaPedidosLoja(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			Pedidos_ajax.carregaPedidosAbertos(request, response, conn, rs.getInt("id_distribuidora"));

		} else {
			objRetorno.put("msg", "ok");

			out.println(objRetorno.toString());
		}

	}

	private static void finalizandoPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			if (rs.getString("flag_role").equalsIgnoreCase("V")) {
				throw new Exception("Você nao tem permissão para realizar esta operação.");
			}

			Pedidos_ajax.finalizandoPedido(request, response, conn, rs.getInt("id_distribuidora"));

		} else {
			objRetorno.put("msg", "ok");

			out.println(objRetorno.toString());
		}

	}

	private static void isLoja(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();

		PreparedStatement st = conn.prepareStatement(" select * from  distribuidora_mobile where id_usuario = ?  ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			objRetorno.put("loja", true);
		} else {
			objRetorno.put("loja", false);
		}

		objRetorno.put("msg", "ok");

		out.println(objRetorno.toString());

	}

	private static void teste(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		JSONObject objRetorno = new JSONObject();
		PrintWriter out = response.getWriter();
		System.out.println(new Date());
		objRetorno.put("msg", "ok");

		out.println(objRetorno.toString());

	}

	private static void appplicacao(HttpServletRequest request, HttpServletResponse response, Connection conn, Sys_parametros sys) throws Exception {
		JSONObject objRetorno = new JSONObject();
		PrintWriter out = response.getWriter();
		objRetorno.put("app", sys.getApplicacao());

		out.println(objRetorno.toString());

	}

	private static void payment(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_usuario, String email) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		MP mp = new MP("TEST-3928083096731492-072914-2aa78c35c6f210a6322c4acf9abe4d14__LD_LC__-222772872");

		String token = request.getParameter("token");
		String paymentMethodId = request.getParameter("paymentMethodId");

		org.codehaus.jettison.json.JSONObject payment = mp.post("/v1/payments", "{" + "'transaction_amount': 100," + "'token': " + token + "," + "'description': 'Title of what you are paying for'," + "'installments': 1," + "'payment_method_id': '" + paymentMethodId + "'," + "'payer': {" + "'email': '" + email + "'" + "}" + "}");

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

	private static void recusouTermos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		JSONObject objRetorno = new JSONObject();
		PrintWriter out = response.getWriter();
		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" delete from usuario ");
			sql.append(" WHERE   id_usuario = ? ");
			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setLong(1, cod_usuario);
			st.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();

		}

		objRetorno.put("msg", "ok");

		out.println(objRetorno.toString());

	}

	private static void confirmaIdadeTermos(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String termos = request.getParameter("termos") == null ? "" : request.getParameter("termos");
		String idade = request.getParameter("idade") == null ? "" : request.getParameter("idade");

		if (!termos.equalsIgnoreCase("true")) {
			throw new Exception(" Você deve aceitar os termos e condições para usar o " + sys.getSys_fromdesc());
		}

		if (sys.getIgnorar_regramaior18().equalsIgnoreCase("N")) {

			if (!idade.equalsIgnoreCase("true")) {
				throw new Exception(" Você deve ter mais de 18 anos para usar o " + sys.getSys_fromdesc());
			}
		}

		if (sys.getSys_id_visistante() != cod_usuario) {

			if (sys.getIgnorar_regramaior18().equalsIgnoreCase("N")) {
				StringBuffer sql = new StringBuffer();
				sql.append(" update usuario ");
				sql.append("   set flag_maioridade = ? ");

				sql.append(" WHERE   id_usuario = ? ");

				PreparedStatement st = conn.prepareStatement(sql.toString());
				st.setString(1, "S");
				st.setLong(2, cod_usuario);
				st.executeUpdate();
			}

			StringBuffer sql = new StringBuffer();
			sql.append(" update usuario ");
			sql.append("   set flag_leutermos = ? ");

			sql.append(" WHERE   id_usuario = ? ");

			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setString(1, "S");
			st.setLong(2, cod_usuario);
			st.executeUpdate();

		}
		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void txtHoraAtend(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		String datayar = request.getParameter("datayar") == null ? "" : request.getParameter("datayar");
		String codbairro = request.getParameter("codbairro") == null ? "" : request.getParameter("codbairro");

		try {
			new SimpleDateFormat("dd/MM/yyyy").parse(datayar);
		} catch (Exception e) {
			throw new Exception("Data inválida.");
		}

		JSONObject objRetorno = new JSONObject();

		StringBuffer varname1 = new StringBuffer();
		varname1.append("select distribuidora.id_distribuidora, carrinho.cod_bairro from carrinho ");
		varname1.append("  ");
		varname1.append(" inner join carrinho_item ");
		varname1.append(" on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		varname1.append("  ");
		varname1.append(" inner join produtos_distribuidora ");
		varname1.append(" on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
		varname1.append("  ");
		varname1.append(" inner join distribuidora ");
		varname1.append(" on distribuidora.id_distribuidora = produtos_distribuidora.id_distribuidora ");
		varname1.append("  ");
		varname1.append(" where id_usuario  = ?  limit 1;");

		PreparedStatement st = conn.prepareStatement(varname1.toString());
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {

			String text = txtHoraAtendConsulta(request, response, conn, cod_usuario, sys, codbairro, datayar, rs.getInt("id_distribuidora"), "Horários de atendimento no dia escolhido \n", false);

			objRetorno.put("txt_texto", text);
		} else {
			objRetorno.put("txt_texto", "");
		}

		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	public static String txtHoraAtendConsulta(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys, String codbairro, String datayar, int idloja, String msg, boolean tipohtml) throws Exception {

			StringBuffer varname11 = new StringBuffer();
			varname11.append("select date_format(horario_ini, '%H:%i') as horario_ini,date_format(horario_fim, '%H:%i') as horario_fim from distribuidora_bairro_entrega ");
			varname11.append("  ");
			varname11.append("inner join distribuidora_horario_dia_entre ");
			varname11.append("on distribuidora_horario_dia_entre.id_distr_bairro = distribuidora_bairro_entrega.id_distr_bairro ");
			varname11.append(" ");
			varname11.append("where cod_bairro = ? and distribuidora_bairro_entrega.id_distribuidora = ? and cod_dia = ?  order by HORARIO_INI ");
			varname11.append(" ");

			int dayOfWeek = Utilitario.diaDasemanaFromDate(datayar);

			if (codbairro.equalsIgnoreCase("")) {
				throw new Exception("Nenhum bairro selecionado.");
			}

		PreparedStatement st = conn.prepareStatement(varname11.toString());
			st.setLong(1, Integer.parseInt(codbairro));
		st.setLong(2, idloja);
			st.setLong(3, dayOfWeek);
			ResultSet rs2 = st.executeQuery();
		String text = msg;
			boolean temhora = false;
			while (rs2.next()) {
				temhora = true;
			if (tipohtml)
				text = text + "" + rs2.getString("horario_ini") + " - " + rs2.getString("horario_fim") + " <br> ";
			else
				text = text + "" + rs2.getString("horario_ini") + " - " + rs2.getString("horario_fim") + " \n";
			}
			if (!temhora) {
				text = "Sem horários de atendimento para o dia escolhido.";
			}

		return text;
	}

	private static void txtObsHora(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append("select txt_obs_hora,desc_endereco,num_enderec,desc_complemento,distribuidora.cod_bairro from carrinho ");
		sql.append(" ");
		sql.append(" inner join carrinho_item ");
		sql.append(" on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append(" inner join produtos_distribuidora ");
		sql.append(" on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
		sql.append(" ");
		sql.append(" inner join distribuidora ");
		sql.append(" on distribuidora.id_distribuidora = produtos_distribuidora.id_distribuidora");
		sql.append(" ");
		sql.append(" where id_usuario  = ? ");
		sql.append(" ");
		sql.append(" limit 1");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();
		objRetorno.put("txt_obs_hora", "");

		if (rs.next()) {
			String endereco = rs.getString("desc_endereco");
			String num = rs.getString("num_enderec");
			String compl = rs.getString("desc_complemento");
			String bairro = Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0);

			String endfinal = endereco + ", " + num + " " + compl + ", " + bairro + " \n";

			objRetorno.put("txt_obs_hora", endfinal + rs.getString("txt_obs_hora"));
		} else {
			String iddistr = request.getParameter("iddistr") == null ? "" : request.getParameter("iddistr");
			if (!iddistr.equalsIgnoreCase("")) {
				sql = new StringBuffer();
				sql.append("select  txt_obs_hora,desc_endereco,num_enderec,desc_complemento,distribuidora.cod_bairro,flag_ativo from distribuidora ");
				sql.append(" ");
				sql.append(" where id_distribuidora  = ? and flag_ativo_master = 'S' ");
				sql.append(" ");
				sql.append(" limit 1");

				st = conn.prepareStatement(sql.toString());
				st.setLong(1, Long.parseLong(iddistr));
				rs = st.executeQuery();
				if (rs.next()) {
					if (!rs.getString("flag_ativo").equalsIgnoreCase("S")) {
						objRetorno.put("txt_obs_hora", "Esta loja está offline no momento.");
					} else {
						String endereco = rs.getString("desc_endereco");
						String num = rs.getString("num_enderec");
						String compl = rs.getString("desc_complemento");
						String bairro = Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0);

						String endfinal = endereco + ", " + num + " " + compl + ", " + bairro + "\n";

						objRetorno.put("txt_obs_hora", endfinal + rs.getString("txt_obs_hora"));
					}

				}
			}
		}

		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void recSenha(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		Sys_parametros sys = new Sys_parametros(conn);

		String desc_email = request.getParameter("c_email") == null ? "" : request.getParameter("c_email");

		if (desc_email.equalsIgnoreCase("")) {
			throw new Exception("Você deve preencher o campo de email.");
		}

		PreparedStatement st = conn.prepareStatement("select * from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			String texto = " Olá,<br> Conforme solicitado, seguem abaixo seus dados de usuário:<br> Usuário: " + rs.getString("desc_user") + " <br> Senha: " + rs.getString("desc_senha");
			Utilitario.sendEmail(desc_email, texto, sys.getSys_fromdesc() + " - Recuperação de usuario e senha", conn);
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

		PreparedStatement st = conn.prepareStatement("select * from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {

			if (rs.getString("chave_ativacao") != null && !(rs.getString("chave_ativacao").equalsIgnoreCase(""))) {
				String texto = "Olá, <br>  Bem vindo ao " + sys.getSys_fromdesc() + ", para validar sua conta clique <a href='" + sys.getUrl_system() + "mobile?ac=validar&token=" + rs.getString("chave_ativacao") + "'> AQUI </a> e você estará pronto para utilizar nossos serviços. <br> Suas informações de login são: <br> Usuário: " + desc_usuario + " <br> Senha: " + desc_senha;
				Utilitario.sendEmail(desc_email, texto, sys.getSys_fromdesc() + " - Criação de conta!", conn);
				throw new Exception("E-mail já cadastrado! Um novo e-mail de validação foi enviado para o e-mail cadastrado. ");
			} else if (rs.getString("flag_faceuser").equalsIgnoreCase("S")) {
				throw new Exception("Este e-mail já está relacionado a uma conta do Facebook. Se você não possui mais uma conta de Facebook, você pode voltar a tela de login e clicar que recuperar senha.");
			} else {
				throw new Exception("E-mail já cadastrado! Se você se esqueceu de sua senha, volte a tela de login e clique em recuperar senha.");
			}
		}

		st = conn.prepareStatement("SELECT 1 from  usuario where  Binary desc_user = ? ");
		st.setString(1, desc_usuario);
		rs = st.executeQuery();

		if (rs.next()) {
			throw new Exception("Nome de usuário ja existente!");
		}

		String validacao = Utilitario.StringGen(1000, 32).substring(0, 99);

		st = conn.prepareStatement("select * from  usuario where  Binary desc_email =  ? ");
		st.setString(1, desc_email);
		rs = st.executeQuery();

		if (rs.next()) {
			if (rs.getString("flag_faceuser").equalsIgnoreCase("F")) {
				throw new Exception("Este e-mail já está relacionado a uma conta do Facebook. Se você não possui mais uma conta de Facebook, você pode voltar a tela de login e clicar que recuperar senha");
			} else {
				throw new Exception("E-mail já cadastrado! Se você se esqueceu de sua senha, volte a tela de login e clique em recuperar senha.");
			}
		}

		String sql = "insert into usuario ( desc_nome,    desc_user, desc_senha, desc_email, flag_faceuser, flag_ativado, chave_ativacao ) values (?,?,?,?,?,?,?)";
		st = conn.prepareStatement(sql);
		st.setString(1, desc_nome);
		st.setString(2, desc_usuario);
		st.setString(3, desc_senha);
		st.setString(4, desc_email);
		st.setString(5, "N");
		st.setString(6, "N");
		st.setString(7, validacao);

		st.executeUpdate();

		String texto = "Olá, <br>  Bem vindo ao " + sys.getSys_fromdesc() + ", para validar sua conta clique <a href='" + sys.getUrl_system() + "mobile?ac=validar&token=" + validacao + "'> AQUI </a> e você estará pronto para utilizar nossos serviços. <br> Suas informações de login são: <br> Usuário: " + desc_usuario + " <br> Senha: " + desc_senha;

		Utilitario.sendEmail(desc_email, texto, sys.getSys_fromdesc() + " - Criação de conta!", conn);

		objRetorno.put("msg", "ok");

		out.print(objRetorno.toJSONString());

	}

	private static void trocarSenha(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String ts_passatual = request.getParameter("ts_passatual") == null ? "" : request.getParameter("ts_passatual");
		String ts_newpassword = request.getParameter("ts_newpassword") == null ? "" : request.getParameter("ts_newpassword");
		String ts_newpasswordconfirm = request.getParameter("ts_newpasswordconfirm") == null ? "" : request.getParameter("ts_newpasswordconfirm");

		PreparedStatement st = conn.prepareStatement("select * from  usuario where  id_usuario = ? ");
		st.setLong(1, cod_usuario);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			if (!rs.getString("desc_senha").toString().equals(ts_passatual.toString()))
				throw new Exception("Sua senha está incorreta");

			if (!ts_newpassword.toString().equals(ts_newpasswordconfirm.toString())) {
				throw new Exception("Suas senhas novas estão diferentes");
			}

			String sql = "update usuario set desc_senha = ?  where id_usuario = ? ";
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

		PreparedStatement st = conn.prepareStatement("select * from  usuario where  binary desc_senha =  ? and id_usuario = ? ");
		st.setString(1, te_passatual);
		st.setLong(2, cod_usuario);
		ResultSet rs = st.executeQuery();

		if (!rs.next()) {
			throw new Exception("Sua senha está incorreta");
		} else {

			if (rs.getString("flag_faceuser") != null && rs.getString("flag_faceuser").equalsIgnoreCase("S")) {
				throw new Exception("Sua conta está associada a uma conta do Facebook, para mudar seu E-mail, por favor mude seu E-mail no Facebook.");
			} else {

				PreparedStatement st2 = conn.prepareStatement("select * from  usuario where  desc_email =  ? and flag_ativado = 'S'  ");
				st2.setString(1, te_email);
				ResultSet rs2 = st2.executeQuery();
				if (rs2.next()) {
					throw new Exception("Este E-mail já esta sendo usado no " + sys.getSys_fromdesc() + "");
				}

				String validacao = Utilitario.StringGen(1000, 32).substring(0, 99);

				String sql = "update usuario set desc_novoemailvalidacao = ? , chave_ativacao_novoemail = ? where id_usuario = ?";
				st = conn.prepareStatement(sql);
				st.setString(1, te_email);
				st.setString(2, validacao);
				st.setLong(3, cod_usuario);

				st.executeUpdate();

				String texto = " Olá, <br> Foi solicitada uma alteração de email na sua conta " + sys.getSys_fromdesc() + ", clique <a href='" + sys.getUrl_system() + "mobile?ac=validarEmail&token=" + validacao + "' > AQUI </a> para confirmar. ";
				texto = texto + " Caso você não tenha solicitou este e-mail, sugerimos que troque sua senha ou contate-nos. ";

				Utilitario.sendEmail(te_email, texto, sys.getSys_fromdesc() + " - Alteração de Email", conn);
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

			PreparedStatement st = conn.prepareStatement(" select max(val_prod) as val_prod from produtos_distribuidora where flag_ativo = 'S' ");

			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				objRetorno.put("val_prod", Math.ceil(rs.getDouble("val_prod")));

			}

			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void saveLocationUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {// é mais uma validação do que save de fato.

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String codbairro = request.getParameter("codbairro") == null ? "" : request.getParameter("codbairro");
		String codcidade = request.getParameter("codcidade") == null ? "" : request.getParameter("codcidade");
		String servico = request.getParameter("servico") == null ? "" : request.getParameter("servico");

		if (codcidade.equalsIgnoreCase("")) {
			throw new Exception("Cidade inválida.");
		}

		if (!servico.equals("T") && !servico.equals("L") && !servico.equals("A")) {
			throw new Exception("Você deve escolher um serviço.");
		}
		if (servico.equalsIgnoreCase("T") || servico.equalsIgnoreCase("A")) {

			if (codbairro.equalsIgnoreCase("")) {
				throw new Exception("Bairro inválido.");
			}
		}
		String sql = "select cod_cidade from  cidade where cod_cidade  = ? ";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, Long.parseLong(codcidade));
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			throw new Exception("Cidade não encontrada. Você deve escolher uma cidade válida.");
		}
		if (servico.equalsIgnoreCase("T") || servico.equalsIgnoreCase("A")) {
			st = conn.prepareStatement("select * from bairros where cod_cidade  = ? and cod_bairro = ? ");
			st.setLong(1, Long.parseLong(codcidade));
			st.setLong(2, Long.parseLong(codbairro));
			rs = st.executeQuery();

			if (!rs.next()) {
				throw new Exception("Bairro não encontrado. Você deve escolher um bairro válido.");
			}
		}

		StringBuffer sql2 = new StringBuffer();// acho que nao precisaria salvar a cidade, mas agora é indiferente.
		sql2.append("update usuario ");// antigamentente aqui se salvava o bairro, mas agora o bairro no usuario nao muda, a nao ser que ele qeira. Se futuramente mudar o esqema de cidade, vai ter q ser parecido com bioarro
		sql2.append("   set  ");

		sql2.append("       cod_cidade = ? ");
		sql2.append("WHERE  id_usuario = ?;");

		st = conn.prepareStatement(sql2.toString());
		int contparam = 1;

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

			// if (cod_bairro.equalsIgnoreCase("")) {
			// throw new Exception("Você deve preencher o campo de bairro.");
			// }

			long codcidade = 0;
			PreparedStatement st = null;
			ResultSet rs = null;

			String sql2 = "select * from  usuario where id_usuario  = ? ";
			st = conn.prepareStatement(sql2);
			st.setLong(1, cod_usuario);
			rs = st.executeQuery();
			if (rs.next()) {
				codcidade = rs.getLong("cod_cidade");
			}

			if (!cod_bairro.equalsIgnoreCase("")) {
				st = conn.prepareStatement("select * from bairros where cod_cidade  = ? and cod_bairro = ? ");
				st.setLong(1, codcidade);
				st.setLong(2, Long.parseLong(cod_bairro));
				rs = st.executeQuery();

				if (!rs.next()) {
					throw new Exception("Bairro não encontrado. Você deve escolher um bairro válido.");
				}
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
			if (cod_bairro.equalsIgnoreCase("")) {
				st.setNull(6, java.sql.Types.INTEGER);
			} else {
				st.setInt(6, Integer.parseInt(cod_bairro));
			}

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

	private static void infosCancel(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		// if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
		// throw new Exception("Usuário inválido.");
		// } else
		{

			String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido");

			if (id_pedido.equalsIgnoreCase("")) {
				throw new Exception("Pedido inválido");
			}

			String sql2 = "select * from  pedido where id_pedido  = ? and id_usuario = " + cod_usuario;
			PreparedStatement st = conn.prepareStatement(sql2);
			st.setLong(1, Long.parseLong(id_pedido));
			ResultSet rs = st.executeQuery();

			if (!rs.next()) {
				throw new Exception("Pedido inválido.");
			} else {
				objRetorno.put("flag_status", rs.getString("flag_status"));

				sql2 = "select * from  pedido_motivo_cancelamento where id_pedido  = ? ";
				PreparedStatement st2 = conn.prepareStatement(sql2);
				st2.setLong(1, Long.parseLong(id_pedido));
				ResultSet rs2 = st2.executeQuery();
				if (rs2.next()) {

					objRetorno.put("motivo", rs2.getLong("cod_motivo"));
					objRetorno.put("desc_obs", rs2.getString("desc_obs"));
					objRetorno.put("data_cancelamento", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs2.getTimestamp("DATA_CANCELAMENTO")));
				}
			}

			objRetorno.put("motivoexp", sys.getCod_cancelamentosys() + "");
			objRetorno.put("msg", "ok");

		}

		out.print(objRetorno.toJSONString());

	}

	private static void cancelaPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido");
		String descobs = request.getParameter("descobs") == null ? "" : request.getParameter("descobs");
		String motivo = request.getParameter("motivo") == null ? "" : request.getParameter("motivo");

		cancelaPedido(request, response, conn, cod_usuario, sys, id_pedido, descobs, motivo, true);

	}

	public static JSONObject cancelaPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys, String id_pedido, String descobs, String motivo, boolean outprint) throws Exception {

		JSONObject objRetorno = new JSONObject();
		// if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
		// throw new Exception("Usuário inválido.");
		// } else
		{

			if (id_pedido.equalsIgnoreCase("")) {
				throw new Exception("Pedido inválido");
			}

			if (motivo.equalsIgnoreCase("")) {
				throw new Exception("Motivo inválido");
			}

			if (outprint) {// se outprint qer dizer que veio pelo mobile
				if (Integer.parseInt(motivo) == sys.getCod_cancelamentosys()) {
					throw new Exception("Motivo inválido");
				}
			}

			String sql2 = "select * from  motivos_cancelamento where cod_motivo  = ? ";
			PreparedStatement st = conn.prepareStatement(sql2);
			st.setLong(1, Long.parseLong(motivo));
			ResultSet rs = st.executeQuery();
			if (!rs.next()) {
				throw new Exception("Motivo inválido.");
			}

			sql2 = "select now() as agora,addtime(data_pedido, tempo_estimado_desejado) as tempocanc, pedido.* from  pedido where id_pedido  = ? and id_usuario = " + cod_usuario;
			st = conn.prepareStatement(sql2);
			st.setLong(1, Long.parseLong(id_pedido));
			rs = st.executeQuery();

			if (!rs.next()) {
				throw new Exception("Pedido inválido.");
			} else {
				String statuspedido = rs.getString("flag_status");

				if (statuspedido.equalsIgnoreCase("O")) {
					throw new Exception("Este pedido já foi finanalizado.");
				}
				if (statuspedido.equalsIgnoreCase("C")) {
					throw new Exception("Este pedido já foi cancelado.");
				}
				if (statuspedido.equalsIgnoreCase("R")) {
					throw new Exception("Este pedido já foi recusado.");
				}

				/*
				 * if (statuspedido.equalsIgnoreCase("E")) {// TODO se for em envio/em local/agendamento
				 * 
				 * Calendar data6 = Calendar.getInstance(); data6.setTime(rs.getTimestamp("tempocanc")); data6.add(Calendar.MINUTE, sys.getNUM_TEMPOMAXCANC_MINUTO());
				 * 
				 * if (data6.getTime().before(new Date())) { throw new Exception("Tempo para cancelamento esgotado. Você tem " + sys.getNUM_TEMPOMAXCANC_MINUTO() + " minutos após o tempo maximo desejado de entrega para cancelar o pedido. Se você realmente deseja cancelar este pedido, entre em contato com o " + sys.getSys_fromdesc()); }
				 * 
				 * }
				 */

				// datateste =now - (datapedido + tempo desjada de entrega)

				StringBuffer sql = new StringBuffer();
				sql.append(" update pedido ");
				sql.append("   set flag_status = 'C' , ");
				sql.append("   pag_token = null ");
				sql.append(" where  id_pedido = " + id_pedido);

				st = conn.prepareStatement(sql.toString());
				st.executeUpdate();

				if (statuspedido.equalsIgnoreCase("A")) {// em aberto

					sql = new StringBuffer();

					sql.append("INSERT INTO pedido_motivo_cancelamento ");
					sql.append("  (`ID_PEDIDO`, `COD_MOTIVO`, `DESC_OBS`, `DATA_CANCELAMENTO`,FLAG_CONFIRMADO_DISTRIBUIDORA,FLAG_POPUPINICIAL,FLAG_VIZUALIZADO_CANC) ");
					sql.append("VALUES ");
					sql.append("  (?,?,?,now(),'S','S','S')");// seta como sim pq ainda estava em aberto. entao a distribuidora nem vai perceber que foi cancelado.

					st = conn.prepareStatement(sql.toString());
					st.setLong(1, Long.parseLong(id_pedido));
					st.setLong(2, Long.parseLong(motivo));
					st.setString(3, descobs);
					st.executeUpdate();

				}

				if (statuspedido.equalsIgnoreCase("E")) {
					// || statuspedido.equalsIgnoreCase("S")) {//"S" a principio nao esta sendo usado. para espera é "E" + e tipo de serviço "L" local

					if (rs.getString("FLAG_MODOPAGAMENTO").equalsIgnoreCase("D")) {// cancelamento em pagamento por dinhero. só cancelar e avisar a distribuidora.

						sql = new StringBuffer();
						sql.append("INSERT INTO pedido_motivo_cancelamento ");
						sql.append("  (`ID_PEDIDO`, `COD_MOTIVO`, `DESC_OBS`, `DATA_CANCELAMENTO`,FLAG_CONFIRMADO_DISTRIBUIDORA,FLAG_POPUPINICIAL,FLAG_VIZUALIZADO_CANC) ");
						sql.append("VALUES ");
						sql.append("  (?,?,?,now(),'N','N','N')");

						st = conn.prepareStatement(sql.toString());
						st.setLong(1, Long.parseLong(id_pedido));
						st.setLong(2, Long.parseLong(motivo));
						st.setString(3, descobs);
						st.executeUpdate();

					}

					if (rs.getString("FLAG_MODOPAGAMENTO").equalsIgnoreCase("C")) {// cancelamento em pagamento por cartao. poreqnto mesma coisa q o dinheiro. dia q sistema fazer pagamento de cartao , vai ser diferente

						sql = new StringBuffer();
						sql.append(" INSERT INTO pedido_motivo_cancelamento ");
						sql.append("  (`ID_PEDIDO`, `COD_MOTIVO`, `DESC_OBS`, `DATA_CANCELAMENTO`,FLAG_CONFIRMADO_DISTRIBUIDORA,FLAG_POPUPINICIAL,FLAG_VIZUALIZADO_CANC) ");
						sql.append(" VALUES ");
						sql.append("  (?,?,?,now(),'N','N','N')");

						st = conn.prepareStatement(sql.toString());
						st.setLong(1, Long.parseLong(id_pedido));
						st.setLong(2, Long.parseLong(motivo));
						st.setString(3, descobs);
						st.executeUpdate();

					}

					try {

						msgLojasMobile(conn, sys, Long.parseLong(id_pedido), 1);

					} catch (Exception e) {
						System.out.println("Falha no envio de email cancela pedido " + id_pedido + " " + new Date());
					}

					// mandar one sginal e memail

				}

			}

			objRetorno.put("msg", "ok");

		}
		if (outprint) {
			PrintWriter out = response.getWriter();
			out.print(objRetorno.toJSONString());
		}
		return objRetorno;
	}

	public static void msgLojasMobile(Connection conn, Sys_parametros sys, long id_pedido, int codtipomsg) throws Exception {
		String html = "";
		String title = "";
		JSONObject data = new JSONObject();
		data.put("loja", true);
		data.put("id_pedido", id_pedido);

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT distribuidora.desc_mail        AS emaildis, ");
		sql.append("       distribuidora_mobile.desc_mail AS emailmobile, num_ped ");
		sql.append(" FROM   pedido ");
		sql.append("       INNER JOIN distribuidora ");
		sql.append("               ON distribuidora.id_distribuidora = pedido.id_distribuidora ");
		sql.append("       left JOIN distribuidora_mobile ");
		sql.append("               ON distribuidora.id_distribuidora = ");
		sql.append("                  distribuidora_mobile.id_distribuidora ");
		sql.append("                  AND distribuidora_mobile.flag_role = 'A' ");
		sql.append(" WHERE  id_pedido =  ? ");

		PreparedStatement st2 = conn.prepareStatement(sql.toString());
		st2.setLong(1, (id_pedido));
		ResultSet rs2 = st2.executeQuery();

		if (rs2.next()) {
			if (codtipomsg == 1) {// cancelamento
				html = " Olá, o pedido nº " + rs2.getString("num_ped") + " foi cancelado. Para mais informações do pedido clique <a href='" + sys.getUrl_system() + "'> AQUI </a> e acesse o sistema do TragoAqui. ";
				title = sys.getSys_fromdesc() + " - Pedido nº " + rs2.getString("num_ped") + " foi cancelado!";

			} else if (codtipomsg == 2) {// novopedido
				html = "Você recebeu um pedido! O número do pedido é " + rs2.getString("num_ped") + ". <br> Clique <a href='" + sys.getUrl_system() + "'> AQUI </a> para acessar nosso sistema, verificar os produtos requisitados e responder o pedido.";
				title = sys.getSys_fromdesc() + " - Você recebeu um pedido! Nº " + rs2.getString("num_ped");

			} else if (codtipomsg == 3) {// pedido atrasado
				html = "Atenção! O cliente informou que o pedido  Nº " + rs2.getString("num_ped") + " ainda não foi entregue!";
				title = sys.getSys_fromdesc() + " - O pedido  Nº " + rs2.getString("num_ped") + " ainda não foi entregue!";
			} else if (codtipomsg == 4) {// pedido atrasado
				html = "Olá, este é um lembrete que você requisitou em relação ao pedido Nº "+rs2.getString("num_ped")+". Ele ainda está em aberto. <br> Por favor clique <a href='" + sys.getUrl_system() + "'> AQUI </a> para acessar nosso sistema e responde-lo.";
				title = sys.getSys_fromdesc() + " - Lembrete do pedido Nº "+rs2.getString("num_ped")+" !";
			}

			Utilitario.sendEmail(rs2.getString("emaildis"), html, title, conn);
			if (rs2.getString("emailmobile") != null && !(rs2.getString("emailmobile").equalsIgnoreCase(""))) {

				Utilitario.oneSginal(sys, rs2.getString("emailmobile"), title, data);
				if (!rs2.getString("emailmobile").equalsIgnoreCase(rs2.getString("emaildis"))) {
					Utilitario.sendEmail(rs2.getString("emailmobile"), html, title, conn);
				}
			}
		}

	}

	private static void carregaUser(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject objRetorno = new JSONObject();
		// if (cod_usuario.equalsIgnoreCase("") || cod_usuario == null || cod_usuario.equalsIgnoreCase("0")) {
		// throw new Exception("Usuário inválido.");
		// } else
		{

			PreparedStatement st = conn.prepareStatement(" select * from usuario  where id_usuario = ?  ");
			st.setLong(1, cod_usuario);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				objRetorno.put("ID_USUARIO", rs.getString("id_usuario"));
				objRetorno.put("DESC_NOME", rs.getString("desc_nome"));
				objRetorno.put("DESC_TELEFONE", rs.getString("desc_telefone"));
				objRetorno.put("COD_BAIRRO", rs.getString("cod_bairro"));
				objRetorno.put("COD_CIDADE", rs.getString("cod_cidade"));
				objRetorno.put("DESC_ENDERECO", rs.getString("desc_endereco"));
				objRetorno.put("DESC_USER", rs.getString("desc_user"));
				objRetorno.put("DESC_EMAIL", rs.getString("desc_email"));
				objRetorno.put("DESC_CARTAO", rs.getString("desc_cartao"));
				objRetorno.put("DESC_ENDERECO_NUM", rs.getString("desc_endereco_num"));
				objRetorno.put("DESC_ENDERECO_COMPLEMENTO", rs.getString("desc_endereco_complemento"));
				objRetorno.put("DATA_EXP_MES", rs.getString("data_exp_mes"));
				objRetorno.put("DATA_EXP_ANO", rs.getString("data_exp_ano"));
				objRetorno.put("DESC_CARDHOLDERNAME", rs.getString("desc_cardholdername"));
				objRetorno.put("PAY_ID", rs.getString("pay_id"));
				objRetorno.put("DESC_CPF", rs.getString("desc_cpf"));

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

		if (codcidade == 0) {// aqui estamos conisderando que o sistema vai ter um banco por cidade. //TODO
			codcidade = sys.getCod_cidade();
		}

		retorno.put("codbairro", codbairro);
		retorno.put("codcidade", codcidade);

		out.print(retorno.toJSONString());
	}

	private static void carregaBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		JSONArray retorno = new JSONArray();
		PrintWriter out = response.getWriter();

		String firstopt = request.getParameter("firstopt") == null ? "" : request.getParameter("firstopt");

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
		if (firstopt.equalsIgnoreCase("")) {
			bairro.put("desc_bairro", "Escolha um bairro");
		} else {
			bairro.put("desc_bairro", firstopt);
		}

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
		distr.put("desc_nome_abrev", "Escolha uma loja");

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
		sql.append("  produtos.id_prod, ");
		sql.append(" desc_prod, ");
		sql.append(" desc_abreviado, ");
		sql.append(" produtos_distribuidora.val_prod, ");
		sql.append(" distribuidora.id_distribuidora, ");
		sql.append(" produtos_distribuidora.id_prod_dist, desc_marca,");
		sql.append(" desc_nome_abrev , ");
		sql.append(" carrinho_item.qtd, ");
		sql.append(" distribuidora.val_entrega_min,qtd_images ");
		sql.append(" ");
		sql.append(" from produtos ");
		sql.append(" inner join produtos_distribuidora ");
		sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append(" inner join distribuidora ");
		sql.append(" on produtos_distribuidora.id_distribuidora = distribuidora.id_distribuidora ");
		sql.append(" left join distribuidora_bairro_entrega ");
		sql.append(" on distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
		sql.append(" left join distribuidora_horario_dia_entre ");
		sql.append(" on distribuidora_horario_dia_entre.id_distr_bairro   = distribuidora_bairro_entrega.id_distr_bairro ");
		sql.append("       LEFT JOIN marca ");
		sql.append("              ON produtos.id_marca = marca.id_marca ");
		sql.append("  ");
		sql.append(" left join carrinho ");
		sql.append(" on carrinho.id_usuario =   " + cod_usuario);
		sql.append("  ");
		sql.append(" ");
		sql.append(" left join carrinho_item ");
		sql.append(" on carrinho_item.id_prod_dist = produtos_distribuidora.id_prod_dist and carrinho_item.id_carrinho = carrinho.id_carrinho ");

		sql.append(" where produtos_distribuidora.id_prod_dist = ? ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(idproddistr));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			retorno.put("ID_PROD", rs.getString("id_prod"));
			retorno.put("ID_PROD_DIST", rs.getString("id_prod_dist"));
			retorno.put("QTD", rs.getString("qtd") == null ? "" : rs.getInt("qtd"));
			retorno.put("DESC_PROD", rs.getString("desc_prod"));
			retorno.put("DESC_ABREVIADO", rs.getString("desc_abreviado"));// abreviado do produto
			retorno.put("VAL_PROD", rs.getDouble("val_prod"));//
			retorno.put("ID_DISTRIBUIDORA", rs.getString("id_distribuidora"));
			retorno.put("DESC_NOME_ABREV", rs.getString("desc_nome_abrev"));/// abreviado da distribuidora
			retorno.put("val_minentrega", rs.getString("val_entrega_min"));///
			retorno.put("valcar", df2.format(retornaValCarrinho(cod_usuario, conn) - (rs.getInt("qtd") * rs.getDouble("val_prod"))));
			retorno.put("desc_marca", rs.getString("desc_marca") == null ? "Outros" : rs.getString("desc_marca"));

			JSONArray imagens = new JSONArray();
			int qtd_images = rs.getInt("qtd_images");
			for (int i = 1; i <= qtd_images; i++) {
				imagens.add(rs.getString("id_prod") + "_" + i + ".jpg");

			}
			retorno.put("imgs", imagens);
			// retorno.put("img", rs.getString("ID_PROD") + ".jpg");///

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
		String id_categoria = request.getParameter("id_categoria") == null ? "" : request.getParameter("id_categoria");
		String id_marca = request.getParameter("id_marca") == null ? "" : request.getParameter("id_marca");
		String pag = request.getParameter("pag") == null ? "" : request.getParameter("pag");

		String diaagend = request.getParameter("diaagend") == null ? "" : request.getParameter("diaagend");
		String horaagend = request.getParameter("horaagend") == null ? "" : request.getParameter("horaagend");

		if (pag.equalsIgnoreCase("")) {
			pag = "1";
		}

		if (!fp_flag_entre_ret.equals("T") && !fp_flag_entre_ret.equals("L") && !fp_flag_entre_ret.equals("A")) {
			throw new Exception("Você deve escolher um tipo de serviço!");
		}

		if (!fp_ordem.equals("N") && !fp_ordem.equals("P")) {
			fp_ordem = "P";
		}

		if (fp_flag_entre_ret.equalsIgnoreCase("T") || fp_flag_entre_ret.equalsIgnoreCase("A")) {
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
			sql.append("select * from (select produtos.id_prod, ");
			sql.append("               desc_prod, categoria.id_categoria,marca.id_marca,desc_categoria, desc_key_words,desc_marca,");
			sql.append("               desc_abreviado, ");
			sql.append("               produtos_distribuidora.val_prod, ");
			sql.append("               distribuidora.id_distribuidora, ");
			sql.append("               produtos_distribuidora.id_prod_dist, ");
			sql.append("               desc_nome_abrev, ");
			sql.append("               carrinho_item.qtd, ");
			sql.append("               distribuidora.val_entrega_min ");

			sql.append("        from   produtos ");
			sql.append("               inner join produtos_distribuidora ");
			sql.append("                       on produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append("               inner join distribuidora ");
			sql.append("                       on produtos_distribuidora.id_distribuidora = ");
			sql.append("                          distribuidora.id_distribuidora and flag_retirada = 'S' ");
			sql.append("       LEFT JOIN prod_categoria ");
			sql.append("              ON prod_categoria.id_prod = produtos.id_prod ");
			sql.append("       LEFT JOIN categoria ");
			sql.append("              ON categoria.id_categoria = prod_categoria.id_categoria ");
			sql.append("       LEFT JOIN marca ");
			sql.append("              ON produtos.id_marca = marca.id_marca ");
			sql.append(" ");
			sql.append("               left join carrinho ");
			sql.append("                      on carrinho.id_usuario =  " + cod_usuario);
			sql.append("               left join carrinho_item ");
			sql.append("                      on carrinho_item.id_prod_dist = ");
			sql.append("                         produtos_distribuidora.id_prod_dist ");
			sql.append("                         and carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("        where  produtos_distribuidora.flag_ativo = 'S' ");
			sql.append("               and distribuidora.flag_ativo_master = 'S' ");
			sql.append("               and distribuidora.flag_ativo = 'S' ");
			sql.append(" ");
			sql.append(" ");
			sql.append(") as tab");

		} else if (fp_flag_entre_ret.equalsIgnoreCase("T")) {

			sql = new StringBuffer();
			sql.append("select * from ( ");
			sql.append(" ");
			sql.append(" select ");
			sql.append(" ");
			sql.append("  produtos.id_prod, ");
			sql.append(" desc_prod, ");
			sql.append(" desc_abreviado, ");
			sql.append(" produtos_distribuidora.val_prod, ");
			sql.append(" distribuidora.id_distribuidora, ");
			sql.append(" produtos_distribuidora.id_prod_dist, ");
			sql.append(" desc_nome_abrev ,categoria.id_categoria,marca.id_marca,desc_categoria, desc_key_words,desc_marca, ");
			sql.append(" carrinho_item.qtd, ");
			sql.append(" distribuidora.val_entrega_min,qtd_images ");
			sql.append(" ");
			sql.append(" from produtos ");
			sql.append(" inner join produtos_distribuidora ");
			sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append(" inner join distribuidora ");
			sql.append(" on produtos_distribuidora.id_distribuidora = distribuidora.id_distribuidora and flag_tele_entrega = 'S' ");
			sql.append(" inner join distribuidora_bairro_entrega ");
			sql.append(" on distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
			sql.append(" inner join distribuidora_horario_dia_entre ");
			sql.append(" on distribuidora_horario_dia_entre.id_distr_bairro   = distribuidora_bairro_entrega.id_distr_bairro ");
			sql.append("       LEFT JOIN prod_categoria ");
			sql.append("              ON prod_categoria.id_prod = produtos.id_prod ");
			sql.append("       LEFT JOIN categoria ");
			sql.append("              ON categoria.id_categoria = prod_categoria.id_categoria ");
			sql.append("       LEFT JOIN marca ");
			sql.append("              ON produtos.id_marca = marca.id_marca ");
			sql.append("  ");
			sql.append(" left join carrinho ");
			sql.append(" on carrinho.id_usuario =   " + cod_usuario);
			sql.append("  ");
			sql.append(" ");
			sql.append(" left join carrinho_item ");
			sql.append(" on carrinho_item.id_prod_dist = produtos_distribuidora.id_prod_dist and carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("  ");
			sql.append("  ");
			sql.append(" ");
			sql.append(" where produtos_distribuidora.flag_ativo = 'S' and distribuidora.flag_ativo_master ='S' and distribuidora.flag_ativo='S' ");
			sql.append(" and distribuidora_bairro_entrega.cod_bairro = ?  and cod_dia = ? and ? between horario_ini and horario_fim and flag_custom = 'N'  ");
			sql.append("  ");
			sql.append("union all ");
			sql.append("  ");
			sql.append(" select ");
			sql.append("  produtos.id_prod, ");
			sql.append(" desc_prod, ");
			sql.append(" desc_abreviado, ");
			sql.append(" produtos_distribuidora.val_prod, ");
			sql.append(" distribuidora.id_distribuidora, ");
			sql.append(" produtos_distribuidora.id_prod_dist, ");
			sql.append(" desc_nome_abrev , categoria.id_categoria, marca.id_marca,desc_categoria, desc_key_words,desc_marca,");
			sql.append(" carrinho_item.qtd, ");
			sql.append(" distribuidora.val_entrega_min,qtd_images ");
			sql.append("  ");
			sql.append(" ");
			sql.append(" from produtos ");
			sql.append(" inner join produtos_distribuidora ");
			sql.append(" on produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append(" inner join distribuidora ");
			sql.append(" on produtos_distribuidora.id_distribuidora = distribuidora.id_distribuidora and flag_tele_entrega = 'S' ");
			sql.append(" inner join distribuidora_bairro_entrega ");
			sql.append(" on distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
			sql.append(" inner join distribuidora_horario_dia_entre ");
			sql.append(" on distribuidora_horario_dia_entre.id_distr_bairro   = distribuidora_bairro_entrega.id_distr_bairro ");
			sql.append("       LEFT JOIN prod_categoria ");
			sql.append("              ON prod_categoria.id_prod = produtos.id_prod ");
			sql.append("       LEFT JOIN categoria ");
			sql.append("              ON categoria.id_categoria = prod_categoria.id_categoria ");
			sql.append("       LEFT JOIN marca ");
			sql.append("              ON produtos.id_marca = marca.id_marca ");
			sql.append("  ");
			sql.append(" left join carrinho ");
			sql.append(" on carrinho.id_usuario =   " + cod_usuario);
			sql.append("  ");
			sql.append(" ");
			sql.append(" left join carrinho_item ");
			sql.append(" on carrinho_item.id_prod_dist = produtos_distribuidora.id_prod_dist and carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("  ");
			sql.append(" where produtos_distribuidora.flag_ativo = 'S' and distribuidora.flag_ativo_master ='S' and distribuidora.flag_ativo='S' ");
			sql.append(" and distribuidora_bairro_entrega.cod_bairro = ?  and cod_dia = 8 and ? between horario_ini and horario_fim and flag_custom = 'S' ");
			sql.append("  ");
			sql.append(") as  tab ");
			sql.append(" ");
			sql.append(" ");

		} else if (fp_flag_entre_ret.equalsIgnoreCase("A")) {

			try {
				new SimpleDateFormat("dd/MM/yyyy").parse(diaagend).before(new Date());
			} catch (Exception e) {
				throw new Exception("O dia de agendamento é inválido.");
			}

			sql = new StringBuffer();
			sql.append(" select * from (  SELECT     produtos.id_prod, ");
			sql.append("           desc_prod, ");
			sql.append("           desc_abreviado, ");
			sql.append("           produtos_distribuidora.val_prod, ");
			sql.append("           distribuidora.id_distribuidora, ");
			sql.append("           produtos_distribuidora.id_prod_dist, ");
			sql.append("           desc_nome_abrev, ");
			sql.append("           categoria.id_categoria, ");
			sql.append("           marca.id_marca, ");
			sql.append("           desc_categoria, ");
			sql.append("           desc_key_words, ");
			sql.append("           desc_marca, ");
			sql.append("           carrinho_item.qtd, ");
			sql.append("           distribuidora.val_entrega_min, ");
			sql.append("           qtd_images, ");
			sql.append("           GROUP_CONCAT(CONCAT(date_format(horario_ini, '%H:%i'), '-',date_format(horario_fim, '%H:%i')) )   as horario ");

			sql.append("FROM       produtos ");
			sql.append("INNER JOIN produtos_distribuidora ");
			sql.append("ON         produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append("INNER JOIN distribuidora ");
			sql.append("ON         produtos_distribuidora.id_distribuidora = distribuidora.id_distribuidora ");
			sql.append("AND        flag_agendamento = 'S' ");
			sql.append("INNER JOIN distribuidora_bairro_entrega ");
			sql.append("ON         distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
			sql.append("INNER JOIN distribuidora_horario_dia_entre ");
			sql.append("ON         distribuidora_horario_dia_entre.id_distr_bairro = distribuidora_bairro_entrega.id_distr_bairro ");
			sql.append("LEFT JOIN  prod_categoria ");
			sql.append("ON         prod_categoria.id_prod = produtos.id_prod ");
			sql.append("LEFT JOIN  categoria ");
			sql.append("ON         categoria.id_categoria = prod_categoria.id_categoria ");
			sql.append("LEFT JOIN  marca ");
			sql.append("ON         produtos.id_marca = marca.id_marca ");
			sql.append("LEFT JOIN  carrinho ");
			sql.append("ON         carrinho.id_usuario = " + cod_usuario);
			sql.append(" LEFT JOIN  carrinho_item ");
			sql.append("ON         carrinho_item.id_prod_dist = produtos_distribuidora.id_prod_dist ");
			sql.append("AND        carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append("WHERE      produtos_distribuidora.flag_ativo = 'S' ");
			sql.append("AND        distribuidora.flag_ativo_master = 'S' ");
			sql.append("AND        ( ");
			sql.append("                      distribuidora.flag_ativo = 'S' ");
			sql.append("                                 || distribuidora.flag_ativo = 'F') ");
			sql.append("AND        distribuidora_bairro_entrega.cod_bairro = ? ");
			sql.append("and ((cod_dia = ? and distribuidora.flag_custom = 'N') or (cod_dia = 8 and distribuidora.flag_custom = 'S')) ");

			if (!horaagend.equalsIgnoreCase("")) {

				Utilitario.testeHora("HHmm", horaagend, "Horário de entrega inválido!");
				sql.append("and ((? between horario_ini and horario_fim and flag_custom = 'N' ) or ( ? between horario_ini and horario_fim and flag_custom = 'S'  ))");

			}

			sql.append("GROUP BY   produtos.id_prod, ");
			sql.append("           desc_prod, ");
			sql.append("           desc_abreviado, ");
			sql.append("           produtos_distribuidora.val_prod, ");
			sql.append("           distribuidora.id_distribuidora, ");
			sql.append("           produtos_distribuidora.id_prod_dist, ");
			sql.append("           desc_nome_abrev, ");
			sql.append("           categoria.id_categoria, ");
			sql.append("           marca.id_marca, ");
			sql.append("           desc_categoria, ");
			sql.append("           desc_key_words, ");
			sql.append("           desc_marca, ");
			sql.append("           carrinho_item.qtd, ");
			sql.append("           distribuidora.val_entrega_min, ");
			sql.append("           qtd_images ) as tab");
			sql.append(" ");

		}

		sql.append(" where 1=1 ");
		String[] keys = null;
		if (listagem) {// se for listagem de todos produtos ou de um especifico, tela de detalhes.

			if (!desc_pesquisa.equalsIgnoreCase("")) {

				keys = desc_pesquisa.split(" ");
				for (int i = 0; i < keys.length; i++) {
					sql.append(" and (tab.desc_abreviado  like ?  or tab.desc_categoria like ? or tab.desc_key_words like ? or tab.desc_marca like ? )");
				}
			}

		} else {
			sql.append(" and tab.id_prod_dist  =  ?  ");
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

		if (id_categoria.equalsIgnoreCase("O")) {
			sql.append(" and tab.id_categoria is null ");
		} else if (!id_categoria.equalsIgnoreCase("")) {
			sql.append(" and tab.id_categoria = ?  ");
		}

		if (id_marca.equalsIgnoreCase("O")) {
			sql.append(" and tab.id_marca is null ");
		} else if (!id_marca.equalsIgnoreCase("")) {
			sql.append(" and tab.id_marca = ?  ");
		}

		if (fp_ordem.equalsIgnoreCase("P")) {
			sql.append(" order by tab.val_prod asc limit " + 20 + " OFFSET " + (Integer.parseInt(20 + "") * (Integer.parseInt(pag) - 1)));
		} else if (fp_ordem.equalsIgnoreCase("N")) {
			sql.append(" order by tab.desc_abreviado asc limit " + 20 + " OFFSET " + (Integer.parseInt(20 + "") * (Integer.parseInt(pag) - 1)));
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
		} else if (fp_flag_entre_ret.equalsIgnoreCase("A")) {

			st.setInt(contparam, Integer.parseInt(cod_bairro));
			contparam++;
			int dayOfWeek = Utilitario.diaDasemanaFromDate(diaagend);

			st.setInt(contparam, dayOfWeek);
			contparam++;

			if (!horaagend.equalsIgnoreCase("")) {

				st.setString(contparam, horaagend.substring(0, 1) + horaagend.substring(1, 2) + ":" + horaagend.substring(2, 3) + horaagend.substring(3, 4));
				contparam++;

				st.setString(contparam, horaagend.substring(0, 1) + horaagend.substring(1, 2) + ":" + horaagend.substring(2, 3) + horaagend.substring(3, 4));
				contparam++;

			}

		}
		if (listagem) {

			if (!desc_pesquisa.equalsIgnoreCase("")) {

				keys = desc_pesquisa.split(" ");
				for (int i = 0; i < keys.length; i++) {
					st.setString(contparam, "%" + keys[i] + "%");
					contparam++;
					st.setString(contparam, "%" + keys[i] + "%");
					contparam++;
					st.setString(contparam, "%" + keys[i] + "%");
					contparam++;
					st.setString(contparam, "%" + keys[i] + "%");
					contparam++;
				}

			}

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

		if (id_categoria.equalsIgnoreCase("O")) {
		} else if (!id_categoria.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_categoria));
			contparam++;
		}

		if (id_marca.equalsIgnoreCase("O")) {
		} else if (!id_marca.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_marca));
			contparam++;
		}

		rs = st.executeQuery();
		// carrega produto unico foi para outra função
		if (listagem) {

			JSONArray prods = new JSONArray();
			while (rs.next()) {
				JSONObject prod = new JSONObject();
				prod.put("ID_PROD", rs.getString("id_prod"));
				prod.put("ID_PROD_DIST", rs.getString("id_prod_dist"));
				prod.put("QTD", rs.getString("qtd") == null ? "" : rs.getString("qtd"));
				prod.put("DESC_PROD", rs.getString("desc_prod"));
				prod.put("DESC_ABREVIADO", rs.getString("desc_abreviado"));// abreviado do produto
				prod.put("VAL_PROD", "R$ " + df2.format(rs.getDouble("val_prod")));//
				prod.put("ID_DISTRIBUIDORA", rs.getString("id_distribuidora"));
				if (fp_flag_entre_ret.equalsIgnoreCase("A")) {
					prod.put("desc_horario", rs.getString("horario"));
				} else {
					prod.put("desc_horario", "");
				}

				prod.put("DESC_NOME_ABREV", rs.getString("desc_nome_abrev"));/// abreviado da distribuidora
				prod.put("img", rs.getString("id_prod") + "_min.jpg");/// abreviado da distribuidora
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

				retorno.put("ID_PROD", rs.getString("id_prod"));
				retorno.put("ID_PROD_DIST", rs.getString("id_prod_dist"));
				retorno.put("QTD", rs.getString("qtd") == null ? "" : rs.getInt("qtd"));
				retorno.put("DESC_PROD", rs.getString("desc_prod"));
				retorno.put("DESC_ABREVIADO", rs.getString("desc_abreviado"));// abreviado do produto
				retorno.put("VAL_PROD", rs.getDouble("val_prod"));//
				retorno.put("ID_DISTRIBUIDORA", rs.getString("id_distribuidora"));
				retorno.put("DESC_NOME_ABREV", rs.getString("desc_nome_abrev"));/// abreviado da distribuidora
				retorno.put("val_minentrega", rs.getString("val_entrega_min"));///
				retorno.put("valcar", df2.format(retornaValCarrinho(cod_usuario, conn) - (rs.getInt("qtd") * rs.getDouble("val_prod"))));

				JSONArray imagens = new JSONArray();
				int qtd_images = rs.getInt("qtd_images");
				for (int i = 1; i <= qtd_images; i++) {
					imagens.add(rs.getString("id_prod") + "_" + i + ".jpg");

				}
				retorno.put("imgs", imagens);
				// retorno.put("img", rs.getString("ID_PROD") + ".jpg");///
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
			st.setLong(1, rs.getLong("id_carrinho"));
			st.executeUpdate();

			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho  where ID_CARRINHO = ? ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, rs.getLong("id_carrinho"));
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
		String pag = request.getParameter("pag") == null ? "" : request.getParameter("pag");

		StringBuffer sql = new StringBuffer();
		sql.append(" select max( val_totalprod+val_entrega) as  val_totalprod from pedido where id_usuario = ? ");
		PreparedStatement st = conn.prepareStatement(sql.toString());

		st.setLong(1, cod_usuario);
		double maxvalue = 0.0;
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			maxvalue = rs.getDouble("VAL_TOTALPROD");
		}

		sql = new StringBuffer();
		sql.append(" select pedido.cod_bairro,num_ped,data_pedido,val_totalprod,flag_status,coalesce(val_entrega,0) as val_entrega,desc_nome_abrev,id_pedido,flag_pedido_ret_entre from pedido ");
		sql.append(" inner join distribuidora ");
		sql.append(" on distribuidora.ID_DISTRIBUIDORA  = pedido.ID_DISTRIBUIDORA ");

		if (!numero.equalsIgnoreCase("")) {
			sql.append(" and num_ped = ? ");
		}

		if (!(dataini.equalsIgnoreCase(""))) {
			sql.append("  and  data_pedido >= ?");
		}

		if (!(datafim.equalsIgnoreCase("")) && datafim != null) {
			sql.append(" and  data_pedido <= ?");
		}

		if (!valini.equalsIgnoreCase("")) {
			sql.append("  and  (val_totalprod + val_entrega) >= ? ");
		}

		if (!valfim.equalsIgnoreCase("")) {
			sql.append("  and  (val_totalprod + val_entrega) <= ? ");
		}

		if (!flagsituacao.equalsIgnoreCase("")) {
			sql.append("  and  flag_status = ? ");
		}

		if (!distribuidora.equalsIgnoreCase("")) {
			sql.append("  and  pedido.id_distribuidora = ? ");
		}

		if (!desc_prod.equalsIgnoreCase("")) {
			sql.append(" and exists (select 1 from pedido_item where pedido_item.id_pedido  = 	pedido.id_pedido and id_prod in (select id_prod from produtos where desc_prod like ?) )");
		}

		if (!cod_bairro.equalsIgnoreCase("")) {
			sql.append("  and  pedido.cod_bairro = ? ");
		}

		sql.append("  and  id_usuario = ? ");

		sql.append("  order by  data_pedido desc limit " + 20 + " offset " + (Integer.parseInt(20 + "") * (Integer.parseInt(pag) - 1)));

		st = conn.prepareStatement(sql.toString());

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

			Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(datafim + " " + "23:59:59");
			st.setString(contparam, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data));
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

		rs = st.executeQuery();
		JSONObject ret = new JSONObject();
		JSONArray prods = new JSONArray();
		while (rs.next()) {

			JSONObject prod = new JSONObject();
			prod.put("NUM_PED", rs.getString("num_ped"));
			prod.put("DATA_PEDIDO", new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("data_pedido")));
			prod.put("VAL_TOTAL", df2.format(rs.getDouble("val_totalprod") + rs.getDouble("val_entrega")));
			prod.put("FLAG_STATUS", Utilitario.returnStatusPedidoFlag(rs.getString("flag_status"), rs.getString("flag_pedido_ret_entre")));
			if (rs.getString("flag_pedido_ret_entre").equalsIgnoreCase("T")) {
				prod.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));
			} else {
				prod.put("desc_bairro", "Retirar no local");

			}
			prod.put("flag_status2", (rs.getString("flag_status")));
			prod.put("DESC_NOME_ABREV", rs.getString("desc_nome_abrev"));
			prod.put("id_pedido", rs.getString("id_pedido"));

			prods.add(prod);
		}
		ret.put("msg", "ok");
		ret.put("pedidos", prods);
		ret.put("maxvalue", Math.ceil(maxvalue));

		out.print(ret.toJSONString());

	}

	private static void enviarMsgContato(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String title = request.getParameter("title") == null ? "" : request.getParameter("title");
		String msg = request.getParameter("msg") == null ? "" : request.getParameter("msg");

		if (msg.equalsIgnoreCase("")) {
			throw new Exception("Escreva uma mensagem!");
		}

		if (title.equalsIgnoreCase("")) {
			throw new Exception("Título em branco!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" select * from usuario ");
		sql.append(" where id_usuario = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));

		ResultSet rs = st.executeQuery();

		if (rs.next()) {

			msg = msg + " <br> ";
			msg = msg + "Cod. Usuário: " + cod_usuario + " -  " + rs.getString("desc_mail");
			Utilitario.sendEmail(sys.getSys_email(), msg, "Contato: " + title, conn);

		}

		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void pedidoNaoRecebido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido");

		if (!Utilitario.isNumeric(id_pedido)) {
			throw new Exception("Pedido inválido!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select  *,  ");// TODO
		sql.append("       addtime(coalesce(data_agenda_entrega, data_pedido), tempo_estimado_entrega) as tempocanc ");
		sql.append(" FROM   pedido ");
		sql.append(" WHERE  id_pedido = ? and flag_pedido_ret_entre = 'T' ");
		sql.append("       AND id_usuario = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_pedido));
		st.setLong(2, (cod_usuario));
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {

			throw new Exception("Pedido inválido!");
		} else {

			if (rs.getString("flag_status").equalsIgnoreCase("O")) {
				throw new Exception("Este pedido já foi finalizado pela loja.");
			}
			if (rs.getString("flag_status").equalsIgnoreCase("C")) {
				throw new Exception("Este pedido foi cancelado.");
			}
			if (rs.getString("flag_status").equalsIgnoreCase("R")) {
				throw new Exception("Este pedido foi recusado.");
			}

			Calendar data6 = Calendar.getInstance();
			data6.setTime(rs.getTimestamp("tempocanc"));

			if (data6.getTime().after(new Date())) {
				throw new Exception("Você deve esperar o tempo máximo de estimado desejado para informar que não recebeu seu pedido.");
			}

		}

		sql = new StringBuffer();
		sql.append(" update pedido set  flag_not_final_avisa_loja = 'S'  , flag_resposta_usuario = 'N' where id_pedido = ? ");
		st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_pedido));
		st.executeUpdate();

		try {

			msgLojasMobile(conn, sys, Long.parseLong(id_pedido), 3);

		} catch (Exception e) {
			System.out.println("Falha no envio de email nãorecebi pedido " + id_pedido + " " + new Date());
		}

		objRetorno.put("msg", "ok");
		out.print(objRetorno.toJSONString());

	}

	private static void carregaPedidoUnico(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido");

		if (!Utilitario.isNumeric(id_pedido)) {
			throw new Exception("Pedido inválido!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select val_entrega,flag_modoentrega,data_agenda_entrega, ");
		sql.append("       num_ped, ");
		sql.append("       val_totalprod, ");
		sql.append("       desc_razao_social, ");
		sql.append("       desc_bairro, pedido.id_distribuidora,");
		sql.append("       desc_endereco_entrega, ");
		sql.append("       num_telefonecontato_cliente, ");
		sql.append("       tempo_estimado_entrega, ");
		sql.append("       data_pedido, ");
		sql.append("       data_pedido_resposta, ");
		sql.append("       tempo_estimado_entrega, ");
		sql.append("       desc_nome_abrev, flag_pedido_ret_entre,tempo_estimado_desejado,flag_pedido_ret_entre,pedido.flag_modopagamento,");
		sql.append("       flag_status,id_pedido,bairros.desc_bairro ,desc_endereco_num_entrega,desc_endereco_complemento_entrega,  addtime (data_pedido_resposta, tempo_estimado_entrega) as hora_entrega ");
		sql.append(" from   pedido ");
		sql.append("       inner join distribuidora ");
		sql.append("               on distribuidora.id_distribuidora = pedido.id_distribuidora ");
		sql.append("       left join bairros ");
		sql.append("               on bairros.cod_bairro = pedido.cod_bairro ");
		sql.append(" where  id_pedido = ? ");
		sql.append("       and id_usuario = ? order by num_ped desc");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_pedido));
		st.setLong(2, (cod_usuario));

		ResultSet rs = st.executeQuery();
		JSONObject ped = new JSONObject();
		if (rs.next()) {

			// NUM_PED,DATA_PEDIDO,VAL_TOTALPROD,FLAG_STATUS,DESC_NOME_ABREV,id_pedido
			ped.put("num_ped", rs.getString("NUM_PED"));
			ped.put("data_pedido", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("data_pedido")));
			if (rs.getTimestamp("DATA_PEDIDO_RESPOSTA") != null) {
				ped.put("data_pedidoresp", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("data_pedido_resposta")));
			} else {
				ped.put("data_pedidoresp", "Não respondido.");
			}
			ped.put("val_totalprod", "R$ " + df2.format(rs.getDouble("val_totalprod")));
			ped.put("flag_status", Utilitario.returnStatusPedidoFlag(rs.getString("flag_status"), rs.getString("flag_pedido_ret_entre")));

			ped.put("flag_status2", (rs.getString("flag_status")));
			ped.put("flag_serv", (rs.getString("flag_pedido_ret_entre")));
			ped.put("flag_modoentrega", (rs.getString("flag_modoentrega")));
			

			ped.put("tempo_entrega_max", rs.getTimestamp("tempo_estimado_desejado") == null ? "" : new SimpleDateFormat("HH:mm").format(rs.getTimestamp("tempo_estimado_desejado")));
			ped.put("desc_serv", Utilitario.returnDistrTiposPedido(rs.getString("flag_pedido_ret_entre"), rs.getString("flag_modoentrega")));

			ped.put("desc_nome_abrev", rs.getString("desc_nome_abrev"));
			ped.put("id_pedido", rs.getString("id_pedido"));
			ped.put("val_entrega", "R$ " + df2.format(rs.getDouble("val_entrega")));

			ped.put("val_total", "R$ " + df2.format(rs.getDouble("val_entrega") + rs.getDouble("val_totalprod")));
			String end = rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega");
			String num = rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega");
			String compl = rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega");

			ped.put("endereco_completo", end + " " + num + " " + compl);
			ped.put("endereco_completo2", end + " " + num + " " + compl + " " + rs.getString("desc_bairro"));
			ped.put("desc_razao_social", rs.getString("desc_razao_social"));
			ped.put("desc_bairro", rs.getString("desc_bairro"));
			ped.put("desc_endereco_entrega", rs.getString("desc_endereco_entrega"));
			ped.put("desc_endereco_num_entrega", rs.getString("desc_endereco_num_entrega"));
			ped.put("desc_endereco_complemento_entrega", rs.getString("desc_endereco_complemento_entrega"));
			ped.put("tempo_entrega2", rs.getTimestamp("tempo_estimado_entrega") == null ? "" : new SimpleDateFormat("HH:mm").format(rs.getTimestamp("tempo_estimado_entrega")));
			ped.put("tempo_entrega", rs.getTimestamp("hora_entrega") == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("hora_entrega")));

			if (rs.getTimestamp("data_agenda_entrega") != null) {
				ped.put("data_agenda_entrega", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("data_agenda_entrega")));
			}else{
				ped.put("data_agenda_entrega", "");
			}
			
			StringBuffer sql2 = new StringBuffer();
			sql2.append("select id_prod_dist, recusado_disponivel,flag_recusado,  desc_prod, val_unit, qtd_prod, qtd_prod * val_unit   as total, desc_abreviado, produtos.id_prod from pedido_item ");
			sql2.append("inner join produtos ");
			sql2.append("on produtos.id_prod  = pedido_item.id_prod ");

			sql2.append(" inner join produtos_distribuidora ");
			sql2.append(" on produtos_distribuidora.id_prod = pedido_item.id_prod and id_distribuidora = " + rs.getInt("id_distribuidora"));

			sql2.append(" where id_pedido = ? order by desc_prod");

			JSONArray produtos = new JSONArray();
			JSONArray produtos_semestq = new JSONArray();

			PreparedStatement st2 = conn.prepareStatement(sql2.toString());
			st2.setLong(1, Long.parseLong(id_pedido));
			ResultSet rs2 = st2.executeQuery();

			while (rs2.next()) {
				JSONObject prod = new JSONObject();
				prod.put("ID_PROD_DIST", rs2.getString("id_prod_dist"));
				prod.put("desc_prod", rs2.getString("desc_prod"));
				prod.put("val_unit", df2.format(rs2.getDouble("val_unit")));
				prod.put("qtd_prod", rs2.getString("qtd_prod"));
				prod.put("total", df2.format(rs2.getDouble("total")));
				prod.put("id_prod", rs2.getString("id_prod"));
				prod.put("desc_abreviado", rs2.getString("desc_abreviado"));

				if (rs2.getString("flag_recusado").equalsIgnoreCase("S")) {
					prod.put("recusado_disponivel", rs2.getString("recusado_disponivel"));
					produtos_semestq.add(prod);
				}

				produtos.add(prod);
			}
			ped.put("motrecusa", "");
			if (rs.getString("flag_status").equalsIgnoreCase("R")) {
				ped.put("motrecusa", Pedidos_ajax.retornaTextRecusado(conn, id_pedido, sys, produtos_semestq));
			}

			ped.put("produtos", produtos);

		} else {
			throw new Exception("Pedido não encontrado!");
		}

		out.print(ped.toJSONString());
	}

	private static void duplicarPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		JSONObject retorno = new JSONObject();
		PrintWriter out = response.getWriter();

		String id_pedido = request.getParameter("id_pedido") == null ? "" : request.getParameter("id_pedido");

		if (!Utilitario.isNumeric(id_pedido)) {
			throw new Exception("Pedido inválido!");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select * ");
		sql.append(" from   pedido ");
		sql.append("       inner join distribuidora ");
		sql.append("               on distribuidora.id_distribuidora = pedido.id_distribuidora ");
		sql.append("       left join bairros ");
		sql.append("               on bairros.cod_bairro = pedido.cod_bairro ");
		sql.append(" where  id_pedido = ? ");
		sql.append("       and id_usuario = ? order by num_ped desc");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_pedido));
		st.setLong(2, (cod_usuario));
		boolean prods_notadded = false;
		ResultSet rs = st.executeQuery();
		String prods = "";
		if (rs.next()) {

			StringBuffer sql2 = new StringBuffer();
			sql2.append("select * from carrinho ");
			sql2.append("where id_usuario = ? ");

			PreparedStatement st2 = conn.prepareStatement(sql2.toString());
			st2.setLong(1, cod_usuario);
			ResultSet rs2 = st2.executeQuery();
			if (rs2.next()) {

				sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
				sql.append(" delete from carrinho_item where id_carrinho = ? ");
				st = conn.prepareStatement(sql.toString());
				st.setLong(1, (rs2.getInt("id_carrinho")));
				st.executeUpdate();

				sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
				sql.append(" delete from carrinho  where id_carrinho = ? ");
				st = conn.prepareStatement(sql.toString());
				st.setLong(1, (rs2.getInt("id_carrinho")));
				st.executeUpdate();

			}

			sql2 = new StringBuffer();
			sql2.append(" select  *, produtos.flag_ativo as ativo1,  produtos_distribuidora.flag_ativo as ativo2 from pedido_item ");
			sql2.append(" inner join produtos ");
			sql2.append(" on produtos.id_prod  = pedido_item.id_prod ");
			sql2.append(" inner join produtos_distribuidora ");
			sql2.append(" on produtos_distribuidora.id_prod  = produtos.id_prod and produtos_distribuidora.id_distribuidora = " + rs.getString("id_distribuidora"));
			sql2.append(" where id_pedido = ?  order by desc_prod ");

			st2 = conn.prepareStatement(sql2.toString());
			st2.setLong(1, Long.parseLong(id_pedido));
			rs2 = st2.executeQuery();
			
			String choiceserv = "";
			if(rs.getString("flag_pedido_ret_entre").equalsIgnoreCase("L")){
				choiceserv = "L";
			}else if(rs.getString("flag_pedido_ret_entre").equalsIgnoreCase("T") && rs.getString("flag_modoentrega").equalsIgnoreCase("T")){
				choiceserv = "T";
			}else if(rs.getString("flag_pedido_ret_entre").equalsIgnoreCase("T") && rs.getString("flag_modoentrega").equalsIgnoreCase("A")){
				choiceserv = "A";
			}

			while (rs2.next()) {
				if (rs2.getString("ativo1").equalsIgnoreCase("S") && rs2.getString("ativo2").equalsIgnoreCase("S")) {

					if (rs2.getString("flag_recusado").equalsIgnoreCase("S")) {
						if (rs2.getInt("recusado_disponivel") > 0) {
							addCarrinho(request, response, conn, cod_usuario, rs2.getString("id_prod_dist"), rs2.getString("recusado_disponivel"), rs.getString("cod_bairro"), choiceserv, false);
						} else {
							prods = prods + rs2.getString("desc_abreviado") + " \n ";
							prods_notadded = true;
						}
					} else {
						addCarrinho(request, response, conn, cod_usuario, rs2.getString("id_prod_dist"), rs2.getString("qtd_prod"), rs.getString("cod_bairro"),choiceserv, false);
					}
				} else {
					prods = prods + rs2.getString("DESC_ABREVIADO") + " \n";
					prods_notadded = true;
				}
			}
		} else {
			throw new Exception("Pedido não encontrado!");
		}
		retorno.put("prods", prods);
		retorno.put("prods_notadded", prods_notadded);
		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());
	}

	private static void carregaCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, boolean single) throws Exception {
		PrintWriter out = response.getWriter();
		String idprodistr = request.getParameter("idprodistr") == null ? "" : request.getParameter("idprodistr");
		StringBuffer sql = new StringBuffer();
		sql.append("select carrinho_item.id_prod_dist,carrinho_item.id_carrinho, carrinho.cod_bairro ,desc_bairro, seq_item, qtd,carrinho_item.val_prod,produtos_distribuidora.id_distribuidora, desc_razao_social,val_entrega_min , produtos.desc_prod, produtos.desc_abreviado, ");
		sql.append(" ");
		sql.append("case when flag_telebairro = 'S' then distribuidora_bairro_entrega.val_tele_entrega else distribuidora.val_tele_entrega end as val_tele_entrega ");
		sql.append(" ");
		sql.append("from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append("inner join produtos_distribuidora ");
		sql.append("on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
		sql.append(" ");
		sql.append("inner join produtos ");
		sql.append("on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append(" ");
		sql.append("inner join distribuidora ");
		sql.append("on distribuidora.id_distribuidora = produtos_distribuidora.id_distribuidora ");
		sql.append(" ");
		sql.append("left join bairros ");
		sql.append("on bairros.cod_bairro = carrinho.cod_bairro ");
		sql.append(" ");
		sql.append("left join distribuidora_bairro_entrega ");
		sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
		sql.append(" ");
		sql.append("where id_usuario = ? ");
		if (single) {
			sql.append("and carrinho_item.id_prod_dist = ? ");
		}

		sql.append("order by produtos.desc_abreviado  ");

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
		double VAL_ENTREGA_MIN = 0;
		double subtotal = 0;
		double frete = 0;
		JSONArray carrinhoitem = new JSONArray();
		if (!single) {
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				// preguiça
				id_carrinho = rs.getString("id_carrinho");
				cod_bairro = rs.getString("cod_bairro") == null ? "" : rs.getString("cod_bairro");
				desc_bairro = rs.getString("desc_bairro");
				obj.put("seq_item", rs.getString("seq_item"));
				obj.put("id_prod_dist", rs.getString("ID_PROD_DIST"));
				obj.put("qtd", rs.getString("qtd"));

				obj.put("val_prod", "" + df2.format(rs.getDouble("val_prod")));//

				obj.put("total", df2.format(rs.getInt("qtd") * rs.getDouble("val_prod")));
				obj.put("desc_prod", rs.getString("desc_prod"));
				obj.put("desc_abreviado", rs.getString("desc_abreviado"));
				id_distribuidora = rs.getString("id_distribuidora");
				DESC_RAZAO_SOCIAL = rs.getString("desc_razao_social");
				VAL_ENTREGA_MIN = rs.getDouble("val_entrega_min");
				subtotal = subtotal + (rs.getInt("qtd") * rs.getDouble("val_prod"));
				frete = rs.getDouble("val_tele_entrega");

				carrinhoitem.add(obj);

			}
			if (!cod_bairro.equalsIgnoreCase("")) {
				if (subtotal < VAL_ENTREGA_MIN) {
					carrinho.put("valorbaixo", true);
				} else {
					carrinho.put("valorbaixo", false);
				}
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

			try {

				sql = new StringBuffer();
				sql.append(" select * from usuario ");
				sql.append(" where id_usuario = ? ");

				st = conn.prepareStatement(sql.toString());
				st.setLong(1, (cod_usuario));

				rs = st.executeQuery();

				if (rs.next()) {
					JSONObject obj = new JSONObject();
					if (Integer.parseInt(cod_bairro) == rs.getInt("cod_bairro")) {
						// carrinho.put("desc_mail", rs.getString("DESC_EMAIL"));
						carrinho.put("desc_endereco", rs.getString("desc_endereco"));
						carrinho.put("desc_endereco_num", rs.getString("desc_endereco_num"));
						carrinho.put("desc_endereco_complemento", rs.getString("desc_endereco_complemento"));
						// carrinho.put("desc_cartao", rs.getString("DESC_CARTAO"));
						// carrinho.put("data_exp_mes", rs.getString("DATA_EXP_MES"));
						// carrinho.put("data_exp_ano", rs.getString("DATA_EXP_ANO"));
						// carrinho.put("desc_cardholdername", rs.getString("DESC_CARDHOLDERNAME"));
						// carrinho.put("pay_id", rs.getString("PAY_ID"));
						// carrinho.put("desc_cpf", rs.getString("DESC_CPF"));

						carrinho.put("seccode", "");
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
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
		sql.append("select desc_endereco_entrega,desc_endereco_num_entrega,desc_endereco_complemento_entrega,pedido.cod_bairro,  desc_bairro  from pedido ");
		sql.append("  ");
		sql.append(" inner join bairros ");
		sql.append(" on bairros.cod_bairro = pedido.cod_bairro ");
		sql.append(" ");
		sql.append(" where id_usuario = ?  and   (concat(coalesce(desc_endereco_entrega,''),coalesce(desc_endereco_num_entrega,''),coalesce(desc_endereco_complemento_entrega,''))  like ? ) ");
		sql.append(" group by desc_endereco_entrega,desc_endereco_num_entrega,desc_endereco_complemento_entrega,pedido.cod_bairro ");
		sql.append(" union ");
		sql.append(" select desc_endereco,desc_endereco_num,desc_endereco_complemento,usuario.cod_bairro, desc_bairro from usuario ");
		sql.append(" ");
		sql.append(" inner join bairros ");
		sql.append(" on bairros.cod_bairro = usuario.cod_bairro ");
		sql.append(" ");
		sql.append(" where  id_usuario = ?  and   (concat(coalesce(desc_endereco,''),coalesce(desc_endereco_num,''),coalesce(desc_endereco_complemento,''),coalesce(desc_bairro,''))  like ? ) ");
		sql.append(" ");
		sql.append(" ");
		sql.append(" order by desc_bairro, desc_endereco_entrega");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		st.setString(2, "%" + descender + "%");
		st.setLong(3, (cod_usuario));
		st.setString(4, "%" + descender + "%");

		ResultSet rs = st.executeQuery();
		JSONArray enderecos = new JSONArray();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			String edereco = rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega");
			String desc_endereco_num = rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega");
			String desc_endereco_complemento = rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega");
			if (!edereco.equalsIgnoreCase("")) {

				obj.put("descender", edereco + " " + desc_endereco_num + " " + desc_endereco_complemento);

				obj.put("desc_endereco_entrega", rs.getString("desc_endereco_entrega") == null ? "" : rs.getString("desc_endereco_entrega"));
				obj.put("desc_endereco_num_entrega", rs.getString("desc_endereco_num_entrega") == null ? "" : rs.getString("desc_endereco_num_entrega"));
				obj.put("desc_endereco_complemento_entrega", rs.getString("desc_endereco_complemento_entrega") == null ? "" : rs.getString("desc_endereco_complemento_entrega"));
				obj.put("cod_bairro", rs.getString("cod_bairro"));
				obj.put("desc_bairro", Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0));

				enderecos.add(obj);
			}
		}

		retorno.put("enderecos", enderecos);

		out.print(retorno.toJSONString());
	}

	private static void carregaMotivosCancelamento(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append(" select * from motivos_cancelamento order by desc_motivo");

		PreparedStatement st = conn.prepareStatement(sql.toString());

		ResultSet rs = st.executeQuery();
		JSONArray enderecos = new JSONArray();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			obj.put("cod_motivo", rs.getString("cod_motivo"));
			obj.put("desc_motivo", rs.getString("desc_motivo"));

			enderecos.add(obj);

		}

		retorno.put("motivos", enderecos);

		out.print(retorno.toJSONString());
	}

	private static void addCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		String id_proddistr = request.getParameter("id_proddistr") == null ? "" : request.getParameter("id_proddistr"); // precisa receber
		// String id_ditribuidora = request.getParameter("id_ditribuidora") == null ? "" : request.getParameter("id_ditribuidora");
		String qtd = request.getParameter("qtd") == null ? "" : request.getParameter("qtd"); // precisa receber
		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro"); // precisa receber
		String choiceserv = request.getParameter("choiceserv") == null ? "" : request.getParameter("choiceserv");

		addCarrinho(request, response, conn, cod_usuario, id_proddistr, qtd, cod_bairro, choiceserv, true);

	}

	public static void addCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, String id_proddistr, String qtd, String cod_bairro, String choiceserv, boolean sendoutprint) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		boolean jatemcarrinho = false;
		long idcarrinho = 0;
		int id_distribuidora_prod = 0;
		double valunit = 0;

		if (!choiceserv.equals("T") && !choiceserv.equals("L") && !choiceserv.equals("A")) {
			throw new Exception("Tipo de serviço inválido.");
		}

		if (choiceserv.equalsIgnoreCase("A") || choiceserv.equals("T")) {

			if (!Utilitario.isNumeric(cod_bairro)) {
				throw new Exception("Bairro inválido!");
			}

			if (cod_bairro.equalsIgnoreCase("0")) {
				throw new Exception("Bairro inválido!");
			}

		}

		if (!Utilitario.isNumeric(id_proddistr)) {
			throw new Exception("Produto inválido!");
		}

		if (!Utilitario.isNumeric(qtd)) {
			throw new Exception("Quantidade inválida!");
		}

		if (id_proddistr.equalsIgnoreCase("0")) {
			throw new Exception("Produto inválido!");
		}

		if (Integer.parseInt(qtd) > 10000) {
			throw new Exception("Você não pode adicionar mais de 10.000 unidades de um produto no seu carrinho!");
		}

		if (Integer.parseInt(qtd) < 0) {
			throw new Exception("Você não pode adicionar uma quantidade negativa do produto!");
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
		sql.append(" where id_prod_dist = ?  ");
		st = conn.prepareStatement(sql.toString());
		st.setLong(1, Long.parseLong(id_proddistr));
		rs = st.executeQuery();
		if (rs.next()) {

			if (rs.getString("flag_ativo").equalsIgnoreCase("N")) {
				throw new Exception("Produto está inativado!");
			}

			id_distribuidora_prod = rs.getInt("id_distribuidora");
			valunit = rs.getDouble("val_prod");
		} else {
			throw new Exception("Produto inválido!");
		}

		validacaoFlagsDistribuidora(request, response, conn, id_distribuidora_prod, choiceserv);

		if (jatemcarrinho && id_distribuidora_prod != 0) {

			validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, id_distribuidora_prod);

		}
		if (choiceserv.equalsIgnoreCase("T") || choiceserv.equalsIgnoreCase("A")) {
			validacaoDisBairroHora(request, response, conn, Long.parseLong(cod_bairro), id_distribuidora_prod, choiceserv);
		}

		if (jatemcarrinho) {
			sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
			sql.append(" delete from carrinho_item ");
			sql.append(" where id_prod_dist = ? and id_carrinho  = ?  ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, Long.parseLong(id_proddistr));
			st.setLong(2, (idcarrinho));
			st.executeUpdate();
		}

		if (!jatemcarrinho && Integer.parseInt(qtd) != 0) {// criar carrinho

			idcarrinho = Utilitario.retornaIdinsert("carrinho", "id_carrinho", conn); // add carrinho
			sql = new StringBuffer();// insere no carrinho
			sql.append(" INSERT INTO carrinho (`ID_CARRINHO`, `ID_USUARIO`, `COD_BAIRRO`, `DATA_CRIACAO`, flag_servico) VALUES (?, ?, ?, now(),?); ");
			st = conn.prepareStatement(sql.toString());
			st.setLong(1, idcarrinho);
			st.setLong(2, (cod_usuario));
			if (choiceserv.equalsIgnoreCase("L"))
				st.setNull(3, java.sql.Types.ARRAY);
			else if (choiceserv.equalsIgnoreCase("T") || choiceserv.equalsIgnoreCase("A"))
				st.setLong(3, Long.parseLong(cod_bairro));

			st.setString(4, choiceserv);

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
		if (sendoutprint)
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
		sql.append(" select carrinho_item.* , produtos_distribuidora.val_prod as val_correto from carrinho_item inner join produtos_distribuidora on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
		sql.append(" where id_carrinho = " + idcarrinho + " ");
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
			sql.append(" where id_prod_dist = ? and id_carrinho  = ?  ");
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

	private static void validacaoDisBairroHora(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_bairro, int id_distribuidora_prod, String servico) throws Exception {

		{
			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual

			sql.append(" select 1 from distribuidora_bairro_entrega ");
			sql.append(" inner join distribuidora_horario_dia_entre ");
			sql.append(" on distribuidora_horario_dia_entre.id_distr_bairro   =   distribuidora_bairro_entrega.id_distr_bairro ");
			sql.append(" where cod_bairro = ? and distribuidora_bairro_entrega.id_distribuidora  = ?   ");
			if (servico.equalsIgnoreCase("T")) {
				sql.append(" and (now() between horario_ini and horario_fim) and cod_dia = ?  ");
			}
			PreparedStatement st = conn.prepareStatement(sql.toString());

			if (cod_bairro == 0) {
				throw new Exception("Bairro inválido.");
			}

			st.setLong(1, (cod_bairro));
			st.setLong(2, (id_distribuidora_prod));
			if (servico.equalsIgnoreCase("T")) {
				st.setLong(3, Utilitario.diaSemana(conn, id_distribuidora_prod));
			}

			ResultSet rs = st.executeQuery();
			if (!rs.next()) {
				throw new Exception("A loja '" + Utilitario.getNomeDistr(conn, id_distribuidora_prod, true) + "', que se encontra no seu carrinho, não se encontra disponível para o bairro escolhido. Limpe seu carrinho e escolha outra loja ou escolha outro bairro.");
			}
		}

	}

	private static void validacaoTipoServico(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_distribuidora, String tiposerv) throws Exception {

		StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual

		sql.append(" select flag_ativo,flag_agendamento,flag_tele_entrega,flag_retirada,desc_nome_abrev from distribuidora  ");
		sql.append(" where distribuidora.id_distribuidora  = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());

		st.setLong(1, (id_distribuidora));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			if (tiposerv.equalsIgnoreCase("A")) {
				if (rs.getString("flag_agendamento").equalsIgnoreCase("N")) {

					throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "'  contém produtos no seu carrinho e não trabalha com agendamento. Para mudar de serviço acesse o Carrinho pelo menu e exclua os produtos.");

				}
			} else if (tiposerv.equalsIgnoreCase("T")) {
				if (rs.getString("flag_tele_entrega").equalsIgnoreCase("N")) {
					throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' contém produtos no seu carrinho e não  trabalha com tele-entrega. Para mudar de serviço acesse o Carrinho pelo menu e exclua os produtos.");
				}

				if (rs.getString("flag_ativo").equalsIgnoreCase("N") || rs.getString("flag_ativo").equalsIgnoreCase("F")) {
					throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' contém produtos no seu carrinho e  está offline. Para mudar de serviço acesse o Carrinho pelo menu e exclua os produtos.");
				}

			} else if (tiposerv.equalsIgnoreCase("L")) {

				if (rs.getString("flag_retirada").equalsIgnoreCase("N")) {
					throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' não trabalha com retirada em local. Para mudar de serviço acesse o Carrinho pelo menu e exclua os produtos.");
				}

				if (rs.getString("flag_ativo").equalsIgnoreCase("N") || rs.getString("flag_ativo").equalsIgnoreCase("F")) {
					throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' contém produtos no seu carrinho e  está offline. Para mudar de serviço acesse o Carrinho pelo menu e exclua os produtos.");
				}

			}

		}

	}

	private static double validacaoFlagsDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, int id_distribuidora_prod, String serv) throws Exception {
		{

			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é compativel com o bairro no horario atual
			sql.append(" select flag_ativo,flag_ativo_master,val_entrega_min from  distribuidora where id_distribuidora = ? ");
			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setLong(1, (id_distribuidora_prod));
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				if (!serv.equalsIgnoreCase("A")) {
					if (!rs.getString("flag_ativo").equalsIgnoreCase("S")) {
						throw new Exception("Loja que se encontra no seu carrinho está offline no momento. Limpe seu carrinho primeiramente para proceder.");
					}
				}

				if (!rs.getString("flag_ativo_master").equalsIgnoreCase("S")) {
					throw new Exception("Loja que se encontra no seu carrinho está desativada.");
				}

				return rs.getDouble("val_entrega_min");// aproveitei pra trazer o valor junto, pra nao precisar faze otra função e consulta
			}

		}
		return 0;
	}

	private static boolean validacaoProdutoDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_distribuidora_prod, double valprod_carrinho, boolean telapag) throws Exception {

		StringBuffer sql = new StringBuffer();// teste para ver se o valor do carrinho é igual ao valor do produto.
		sql.append("select  * from produtos_distribuidora  where  id_prod_dist = ? and flag_ativo = 'S' ");
		PreparedStatement st3 = conn.prepareStatement(sql.toString());
		st3.setLong(1, id_distribuidora_prod);
		ResultSet rs3 = st3.executeQuery();
		if (rs3.next()) {
			if (rs3.getDouble("val_prod") != valprod_carrinho) {
				if (telapag) {
					return false;
				} else {
					throw new Exception("O produto " + Utilitario.getNomeProdIdProdDistr(conn, id_distribuidora_prod, true) + " que está no carrinho tem um valor diferente que se encontra na loja. Isto pode acontecer quando a loja modifica o valor de um produto durante sua compra. Por favor clique em recalcular carrinho.");
				}
			}
		} else {
			throw new Exception("O produto " + Utilitario.getNomeProdIdProdDistr(conn, id_distribuidora_prod, true) + " não se encontra disponível. Por favor remova-o de seu carrinho.");
		}
		return true;
	}

	private static void validacaoTesteCarrinhoDistribuidora(HttpServletRequest request, HttpServletResponse response, Connection conn, long id_carrinho, int id_distribuidora_prod) throws Exception {
		{

			StringBuffer sql = new StringBuffer();// testa para ver se a distribuidora é a mesma do carrinho

			sql.append(" select id_distribuidora from carrinho_item ");
			sql.append("inner join produtos_distribuidora ");
			sql.append("on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
			sql.append("where id_carrinho = ?  ");
			PreparedStatement st = conn.prepareStatement(sql.toString());
			st.setLong(1, (id_carrinho));
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (id_distribuidora_prod != rs.getInt("ID_DISTRIBUIDORA")) {
					throw new Exception("Produto selecionado é de uma loja diferente da que se encontra no carrinho!");
				}
			}

		}

	}

	private static void servicoCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();
		if (sys.getApplicacao().equalsIgnoreCase("1")) {
			retorno.put("serv", "T");// padrao
		} else {
			retorno.put("serv", "A");
		}

		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql.append("select *  ");
		sql.append("  ");
		sql.append("from carrinho ");
		sql.append(" ");
		sql.append("where carrinho.id_usuario = ? ");
		sql.append(" ");

		retorno.put("cod_bairro", 0);// dando erro no mobile

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			retorno.put("serv", rs.getString("flag_servico"));
			retorno.put("cod_bairro", rs.getString("cod_bairro") == null ? "" : rs.getInt("cod_bairro"));

		}
		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());

	}

	private static void testesMudaServico(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();
		String choiceserv = request.getParameter("choiceserv") == null ? "" : request.getParameter("choiceserv");

		if (!choiceserv.equals("T") && !choiceserv.equals("L") && !choiceserv.equals("A")) {
			throw new Exception("Tipo de serviço inválido.");
		}
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql.append("select distribuidora.flag_ativo, flag_agendamento,flag_tele_entrega,flag_retirada,distribuidora.desc_nome_abrev,distribuidora.id_distribuidora  ");
		sql.append("  ");
		sql.append("from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append("inner join produtos_distribuidora ");
		sql.append("on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
		sql.append(" ");
		sql.append("inner join produtos ");
		sql.append("on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append("  ");
		sql.append("inner join distribuidora ");
		sql.append("on distribuidora.id_distribuidora = produtos_distribuidora.id_distribuidora ");
		sql.append("  ");
		sql.append("  ");
		sql.append("where carrinho.id_usuario = ? ");
		sql.append(" ");
		sql.append("group by flag_agendamento,flag_tele_entrega,flag_retirada,distribuidora.desc_nome_abrev");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			validacaoTipoServico(request, response, conn, rs.getInt("id_distribuidora"), choiceserv);

			sql = new StringBuffer();
			sql.append("UPDATE carrinho ");
			sql.append("   SET flag_servico = ? ");
			sql.append("WHERE  id_usuario = ? ");

			st = conn.prepareStatement(sql.toString());
			st.setString(1, choiceserv);
			st.setLong(2, cod_usuario);
			st.executeUpdate();

			if (choiceserv.equals("L")) {

				sql = new StringBuffer();
				sql.append("UPDATE carrinho ");
				sql.append("   SET cod_bairro = ? ");
				sql.append("WHERE  id_usuario = ? ");

				st = conn.prepareStatement(sql.toString());
				st.setNull(1, java.sql.Types.INTEGER);
				st.setLong(2, cod_usuario);
				st.executeUpdate();

			}

		}
		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());

	}

	private static void testesMudaBairroCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		String novobairro = request.getParameter("novobairro") == null ? "" : request.getParameter("novobairro");
		String choiceserv = request.getParameter("choiceserv") == null ? "" : request.getParameter("choiceserv");

		testesMudaBairroCarrinho(request, response, conn, cod_usuario, novobairro, choiceserv, true);

	}

	private static void testesMudaBairroCarrinho(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, String novobairro, String choiceserv, boolean outprint) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		if (choiceserv.equals("L")) {

			throw new Exception("Tipo de serviço inválido.");
		}

		if (!choiceserv.equals("T") && !choiceserv.equals("A")) {
			throw new Exception("Tipo de serviço inválido.");
		}

		if (novobairro.equalsIgnoreCase("") || !Utilitario.isNumeric(novobairro)) {
			throw new Exception("Bairro inválido.");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select * from carrinho ");
		sql.append("WHERE  id_usuario = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, cod_usuario);

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			if (choiceserv.equals("T") || choiceserv.equals("A")) {
				sql = new StringBuffer();
				sql.append("UPDATE carrinho ");
				sql.append("   SET cod_bairro = ? , flag_servico = ? ");
				sql.append("WHERE  id_usuario = ? ");

				st = conn.prepareStatement(sql.toString());
				st.setInt(1, Integer.parseInt(novobairro));
				st.setString(2, choiceserv);
				st.setLong(3, cod_usuario);
				st.executeUpdate();
			}
			long idcarrinho = 0;

			sql = new StringBuffer();
			sql.append("select distribuidora.desc_nome_abrev,flag_agendamento,flag_tele_entrega,flag_retirada,usuario.desc_email, usuario. desc_nome, carrinho_item.id_carrinho,distribuidora.id_distribuidora,carrinho.cod_bairro,usuario.desc_telefone,usuario.desc_endereco,desc_endereco_num,desc_endereco_complemento,sum(produtos_distribuidora.val_prod * carrinho_item.qtd ) as val_prod, ");
			sql.append("  ");
			sql.append("case when flag_telebairro = 'S' then distribuidora_bairro_entrega.val_tele_entrega else distribuidora.val_tele_entrega end as val_tele_entrega ");
			sql.append("  ");
			sql.append("from carrinho ");
			sql.append(" ");
			sql.append("inner join carrinho_item ");
			sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
			sql.append(" ");
			sql.append("inner join produtos_distribuidora ");
			sql.append("on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
			sql.append(" ");
			sql.append("inner join produtos ");
			sql.append("on produtos_distribuidora.id_prod = produtos.id_prod ");
			sql.append("  ");
			sql.append("inner join distribuidora ");
			sql.append("on distribuidora.id_distribuidora = produtos_distribuidora.id_distribuidora ");
			sql.append("  ");
			sql.append("left join bairros ");
			sql.append("on bairros.cod_bairro = carrinho.cod_bairro ");
			sql.append("");
			sql.append("left join distribuidora_bairro_entrega ");
			sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
			sql.append(" ");
			sql.append("inner join usuario ");
			sql.append("on usuario.id_usuario = carrinho.id_usuario ");
			sql.append("  ");
			sql.append("where usuario.id_usuario = ? ");
			sql.append(" ");
			sql.append("group by distribuidora.desc_nome_abrev,flag_agendamento,flag_tele_entrega,flag_retirada,usuario.desc_email, usuario. desc_nome, carrinho_item.id_carrinho,distribuidora_bairro_entrega.id_distribuidora,carrinho.cod_bairro,usuario.desc_telefone,usuario.desc_endereco,desc_endereco_num,desc_endereco_complemento, val_tele_entrega");

			st = conn.prepareStatement(sql.toString());

			st.setLong(1, (cod_usuario));
			rs = st.executeQuery();

			if (rs.next()) {

				validacaoTipoServico(request, response, conn, rs.getInt("id_distribuidora"), choiceserv);
				/*
				 * if (choiceserv.equalsIgnoreCase("A")) { if (rs.getString("flag_agendamento").equalsIgnoreCase("N")) {
				 * 
				 * throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' não trabalha com agendamento. Para mudar de serviço, por favor vá no seu carrinho e exclua os produtos.");
				 * 
				 * } } else if (choiceserv.equalsIgnoreCase("T")) { if (rs.getString("flag_tele_entrega").equalsIgnoreCase("N")) {
				 * 
				 * throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' não trabalha com tele-entrega. Para mudar de serviço, por favor vá no seu carrinho e exclua os produtos.");
				 * 
				 * } } else if (choiceserv.equalsIgnoreCase("L")) {
				 * 
				 * if (rs.getString("flag_retirada").equalsIgnoreCase("N")) { throw new Exception("A loja '" + rs.getString("desc_nome_abrev") + "' não trabalha com retirada em local. Para mudar de serviço, por favor vá no seu carrinho e exclua os produtos."); } }
				 */

				idcarrinho = rs.getLong("id_carrinho");

				validacaoFlagsDistribuidora(request, response, conn, rs.getInt("id_distribuidora"), choiceserv);
				if (choiceserv.equalsIgnoreCase("T") || choiceserv.equalsIgnoreCase("A")) {
					validacaoDisBairroHora(request, response, conn, rs.getInt("cod_bairro"), rs.getInt("id_distribuidora"), choiceserv);
				}

				validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, rs.getInt("id_distribuidora"));

			}
		}
		retorno.put("msg", "ok");
		if (outprint)
			out.print(retorno.toJSONString());

	}

	private static void loadFreteBairro(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();
		String bairro = request.getParameter("codbairro") == null ? "" : request.getParameter("codbairro");
		retorno.put("val_entrega", 0);
		if (!bairro.equalsIgnoreCase("")) {

			StringBuffer varname1 = new StringBuffer();
			varname1.append("SELECT distribuidora.id_distribuidora ");
			varname1.append("FROM   carrinho ");
			varname1.append("       INNER JOIN carrinho_item ");
			varname1.append("               ON carrinho_item.id_carrinho = carrinho.id_carrinho ");
			varname1.append("       INNER JOIN produtos_distribuidora ");
			varname1.append("               ON produtos_distribuidora.id_prod_dist = ");
			varname1.append("                  carrinho_item.id_prod_dist ");
			varname1.append("       INNER JOIN produtos ");
			varname1.append("               ON produtos_distribuidora.id_prod = produtos.id_prod ");
			varname1.append("       INNER JOIN distribuidora ");
			varname1.append("               ON distribuidora.id_distribuidora = ");
			varname1.append("                  produtos_distribuidora.id_distribuidora");
			varname1.append(" where carrinho.id_usuario = ? ");

			PreparedStatement st = conn.prepareStatement(varname1.toString());

			st.setLong(1, (cod_usuario));
			ResultSet rs = st.executeQuery();
			int dits = 0;
			if (rs.next()) {
				dits = rs.getInt("id_distribuidora");
			}

			varname1 = new StringBuffer();
			varname1.append("select case ");
			varname1.append("         when flag_telebairro = 'S' then ");
			varname1.append("         distribuidora_bairro_entrega.val_tele_entrega ");
			varname1.append("         else distribuidora.val_tele_entrega ");
			varname1.append("       end as val_tele_entrega ");
			varname1.append("from   distribuidora ");
			varname1.append("       left join distribuidora_bairro_entrega ");
			varname1.append("              on distribuidora_bairro_entrega.id_distribuidora = ");
			varname1.append("                 distribuidora.id_distribuidora ");
			varname1.append("where  distribuidora_bairro_entrega.cod_bairro = ? ");
			varname1.append("       and distribuidora.id_distribuidora = ? ");
			varname1.append("group  by val_tele_entrega");

			st = conn.prepareStatement(varname1.toString());
			st.setLong(1, Integer.parseInt((bairro)));
			st.setLong(2, (dits));

			rs = st.executeQuery();
			if (rs.next()) {
				retorno.put("val_entrega", rs.getDouble("VAL_TELE_ENTREGA"));
			}
		}
		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());
	}

	private static void criarPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario) throws Exception {

		String tipo_pagamento = request.getParameter("tipo_pagamento") == null ? "" : request.getParameter("tipo_pagamento");
		String desc_endereco = request.getParameter("desc_endereco") == null ? "" : request.getParameter("desc_endereco");
		String desc_endereco_num = request.getParameter("desc_endereco_num") == null ? "" : request.getParameter("desc_endereco_num");
		String desc_endereco_complemento = request.getParameter("desc_endereco_complemento") == null ? "" : request.getParameter("desc_endereco_complemento");
		String tempomax = request.getParameter("tempo_estimado_desejado") == null ? "" : request.getParameter("tempo_estimado_desejado");
		String choiceserv = request.getParameter("choiceserv") == null ? "" : request.getParameter("choiceserv");
		String trocopara = request.getParameter("troco_para") == null ? "" : request.getParameter("troco_para");
		String dataagendamento = request.getParameter("dataagendamento") == null ? "" : request.getParameter("dataagendamento");
		String dataagendamentohora = request.getParameter("dataagendamentohora") == null ? "" : request.getParameter("dataagendamentohora");
		String bairro = request.getParameter("bairro") == null ? "" : request.getParameter("bairro");
		String obsinfo = request.getParameter("obsinfo") == null ? "" : request.getParameter("obsinfo");
		String telefone = request.getParameter("c_telefone") == null ? "" : request.getParameter("c_telefone");

		JSONObject param = new JSONObject();

		param.put("tipo_pagamento", tipo_pagamento);
		param.put("desc_endereco", desc_endereco);
		param.put("desc_endereco_num", desc_endereco_num);
		param.put("desc_endereco_complemento", desc_endereco_complemento);
		param.put("tempomax", tempomax);
		param.put("choiceserv", choiceserv);
		param.put("trocopara", trocopara);
		param.put("dataagendamento", dataagendamento);
		param.put("dataagendamentohora", dataagendamentohora);
		param.put("bairro", bairro);
		param.put("obsinfo", obsinfo);
		param.put("c_telefone", telefone);

		criarPedido(request, response, conn, cod_usuario, param, true);

	}

	public static JSONObject criarPedido(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, JSONObject param, boolean outprint) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		Sys_parametros sys = new Sys_parametros(conn);

		String tipo_pagamento = param.get("tipo_pagamento").toString();
		String desc_endereco = param.get("desc_endereco").toString();
		String desc_endereco_num = param.get("desc_endereco_num").toString();
		String desc_endereco_complemento = param.get("desc_endereco_complemento").toString();
		String tempomax = param.get("tempomax").toString();
		String choiceserv = param.get("choiceserv").toString();
		String trocopara = param.get("trocopara").toString();
		String dataagendamento = param.get("dataagendamento").toString();
		String dataagendamentohora = param.get("dataagendamentohora").toString();
		String bairro = param.get("bairro").toString();
		String obsinfo = param.get("obsinfo") == null ? "" : param.get("obsinfo").toString();
		String c_telefone = param.get("c_telefone") == null ? "" : param.get("c_telefone").toString();

		if (!choiceserv.equals("T") && !choiceserv.equals("L") && !choiceserv.equals("A")) {
			throw new Exception("Tipo de serviço inválido.");
		}

		if (!tipo_pagamento.equalsIgnoreCase("D") && !tipo_pagamento.equalsIgnoreCase("C")) {
			throw new Exception("Tipo de pagamento inválido.");
		}

		if (choiceserv.equalsIgnoreCase("T") || choiceserv.equalsIgnoreCase("A")) {
			if (desc_endereco.equalsIgnoreCase("")) {
				throw new Exception("Seu endereço está em branco!");
			}

			if (desc_endereco_num.equalsIgnoreCase("")) {
				throw new Exception("Seu número de endereço está em branco!");
			}

			if (c_telefone.equalsIgnoreCase("")) {
				throw new Exception("Por favor informe seu telefone, caso a loja precise entrar em contato com você.");
			}

			Utilitario.testeHora("HHmm", tempomax, "Tempo maximo de entrega inválido!");

		}

		StringBuffer sql = new StringBuffer();
		sql.append("select * from pedido where now() between (select  max(data_pedido) from pedido where id_usuario = ?) and DATE_ADD(data_pedido, INTERVAL 30 second)");

		PreparedStatement st = conn.prepareStatement(sql.toString());

		st.setLong(1, (cod_usuario));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			throw new Exception("Você deve esperar 30 segs após a realização de um pedido para fazer outro pedido!");
		}

		if (choiceserv.equalsIgnoreCase("T") || choiceserv.equalsIgnoreCase("A"))
			testesMudaBairroCarrinho(request, response, conn, cod_usuario, bairro, choiceserv, false);

		long idcarrinho = 0;

		sql = new StringBuffer();
		sql.append("select distribuidora.desc_mail as mailoja, usuario.flag_maioridade,usuario.flag_leutermos,usuario.desc_email, usuario. desc_nome, carrinho_item.id_carrinho,distribuidora.id_distribuidora,carrinho.cod_bairro,usuario.desc_telefone,usuario.desc_endereco,desc_endereco_num,desc_endereco_complemento,sum(produtos_distribuidora.val_prod * carrinho_item.qtd ) as val_prod, date_format(tempo_minimo_entrega, '%H:%i') as tempo_minimo_entrega , ");
		sql.append("  ");
		sql.append("case when flag_telebairro = 'S' then distribuidora_bairro_entrega.val_tele_entrega else distribuidora.val_tele_entrega end as val_tele_entrega ");
		sql.append("  ");
		sql.append("from carrinho ");
		sql.append(" ");
		sql.append("inner join carrinho_item ");
		sql.append("on carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append(" ");
		sql.append("inner join produtos_distribuidora ");
		sql.append("on produtos_distribuidora.id_prod_dist = carrinho_item.id_prod_dist ");
		sql.append(" ");
		sql.append("inner join produtos ");
		sql.append("on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append("  ");
		sql.append("inner join distribuidora ");
		sql.append("on distribuidora.id_distribuidora = produtos_distribuidora.id_distribuidora ");
		sql.append("  ");
		sql.append("left join bairros ");
		sql.append("on bairros.cod_bairro = carrinho.cod_bairro ");
		sql.append("");
		sql.append("left join distribuidora_bairro_entrega ");
		sql.append("on distribuidora_bairro_entrega.cod_bairro = bairros.cod_bairro and distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");
		sql.append(" ");
		sql.append("inner join usuario ");
		sql.append("on usuario.id_usuario = carrinho.id_usuario ");
		sql.append("  ");
		sql.append("where usuario.id_usuario = ? ");
		sql.append(" ");
		sql.append("group by distribuidora.desc_mail, usuario.flag_maioridade,usuario.flag_leutermos , usuario.desc_email, usuario. desc_nome, carrinho_item.id_carrinho,distribuidora_bairro_entrega.id_distribuidora,carrinho.cod_bairro,usuario.desc_telefone,usuario.desc_endereco,desc_endereco_num,desc_endereco_complemento, val_tele_entrega");

		st = conn.prepareStatement(sql.toString());
		PreparedStatement st2 = null;

		st.setLong(1, (cod_usuario));
		rs = st.executeQuery();
		ResultSet rs2 = null;
		long idped = 0;
		String email = "";
		if (rs.next()) {
			idcarrinho = rs.getLong("id_carrinho");
			String emailoja = rs.getString("mailoja");
			double valmin_entrgega = 0;
			boolean offline = false;
			try {
				valmin_entrgega = validacaoFlagsDistribuidora(request, response, conn, rs.getInt("id_distribuidora"), choiceserv);
			} catch (Exception e) {
				offline = true;
				retorno.put("offline", true);

			}

			if (sys.getIgnorar_regramaior18().equalsIgnoreCase("N")) {
				if (rs.getString("flag_maioridade") == null || rs.getString("flag_maioridade").equalsIgnoreCase("N")) {
					throw new Exception("Você deve ser maior de 18 anos para realizar um pedido");
				}
			}

			if (rs.getString("flag_leutermos") == null || rs.getString("flag_leutermos").equalsIgnoreCase("N")) {
				throw new Exception("Você deve concordar com os termos e condições para realizar um pedido. Seu cadastrado será removido. Para utilizar do nossos serviços cadastre-se novamente e aceite os termos e condições.");
			}

			if (!offline) {

				validacaoTipoServico(request, response, conn, rs.getInt("id_distribuidora"), choiceserv);
				validacaoTesteCarrinhoDistribuidora(request, response, conn, idcarrinho, rs.getInt("id_distribuidora"));// se um item do carrinho tiver uma distribuidora diferente, ele vai dar um erro

				double subtotal = 0;
				if (rs.getDouble("val_prod") == 0) {
					throw new Exception("Erro, o valor dos produtos de seu pedido é R$ 0,00!.");
				}
				// testa para ver so tipo de serviço é valido

				sql = new StringBuffer();
				sql.append("INSERT INTO pedido ");
				sql.append("  (`ID_PEDIDO`, `ID_DISTRIBUIDORA`, `ID_USUARIO`, `DATA_PEDIDO`, `FLAG_STATUS`, `VAL_TOTALPROD`, `VAL_ENTREGA`,  `NUM_PED`, `COD_BAIRRO`, `NUM_TELEFONECONTATO_CLIENTE`,  `DESC_ENDERECO_ENTREGA`, `DESC_ENDERECO_NUM_ENTREGA`, `DESC_ENDERECO_COMPLEMENTO_ENTREGA`, flag_vizualizado,FLAG_MODOPAGAMENTO,NOME_PESSOA,FLAG_PEDIDO_RET_ENTRE,TEMPO_ESTIMADO_DESEJADO,flag_modoentrega,DATA_AGENDA_ENTREGA,DESC_OBSERVACAO) ");
				sql.append("VALUES ");
				sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?);");

				st = conn.prepareStatement(sql.toString());

				idped = Utilitario.retornaIdinsertLong("pedido", "id_pedido", conn);
				st.setLong(1, idped);
				st.setLong(2, rs.getInt("id_distribuidora"));
				st.setLong(3, cod_usuario);
				st.setTimestamp(4, Utilitario.getTimeStamp(new Date()));
				st.setString(5, "A");
				st.setDouble(6, rs.getDouble("val_prod"));
				if (choiceserv.equalsIgnoreCase("T")) {
					st.setDouble(7, rs.getDouble("val_tele_entrega"));
				} else {
					st.setDouble(7, 0.0);
				}

				long numpad = Utilitario.getNextNumpad(conn, rs.getInt("id_distribuidora"));
				st.setLong(8, numpad);

				st.setString(10, rs.getString("desc_telefone"));
				if (choiceserv.equalsIgnoreCase("T") || choiceserv.equalsIgnoreCase("A")) {
					validacaoDisBairroHora(request, response, conn, rs.getInt("cod_bairro"), rs.getInt("id_distribuidora"), choiceserv);
					st.setInt(9, rs.getInt("cod_bairro"));
					st.setString(11, desc_endereco);
					st.setString(12, desc_endereco_num);
					st.setString(13, desc_endereco_complemento);
				} else {
					st.setNull(9, java.sql.Types.INTEGER);
					st.setString(11, "");
					st.setString(12, "");
					st.setString(13, "");
				}
				st.setString(14, "N");
				st.setString(15, tipo_pagamento);
				st.setString(16, rs.getString("DESC_NOME"));
				st.setString(17, choiceserv.equalsIgnoreCase("A") ? "T" : choiceserv);// agendamentos no pedido são modelados asism: FLAG_PEDIDO_RET_ENTRE = 'T' e flag_modoentrega ='A'
				if (choiceserv.equalsIgnoreCase("A")) {
					st.setString(18, "00:00");
					// st.setString(18, "00:30");
				} else if (choiceserv.equalsIgnoreCase("T")) {

					st.setString(18, tempomax.substring(0, 2) + ":" + tempomax.substring(2, 4));
				} else {
					st.setString(18, "00:00");
				}

				if (choiceserv.equalsIgnoreCase("T")) {
					st.setString(19, "T");
				} else if (choiceserv.equalsIgnoreCase("A")) {
					st.setString(19, "A");
				} else {
					st.setNull(19, java.sql.Types.VARCHAR);
				}

				if (choiceserv.equalsIgnoreCase("A")) {// agendamento
					if (dataagendamento.equalsIgnoreCase("")) {
						throw new Exception("Data de agendamento inválida.");
					}
					if (dataagendamentohora.equalsIgnoreCase("")) {
						throw new Exception("Horário de entrega inválido.");
					}

					if (new SimpleDateFormat("dd/MM/yyyyHHmm").parse(dataagendamento + dataagendamentohora).before(new Date())) {
						throw new Exception("O horario de agendamento é anterior ao horário atual.");
					}

					StringBuffer varname11 = new StringBuffer();
					varname11.append("select date_format(horario_ini, '%H:%i') as horario_ini,date_format(horario_fim, '%H:%i') as horario_fim from distribuidora_bairro_entrega ");
					varname11.append("  ");
					varname11.append("inner join distribuidora_horario_dia_entre ");
					varname11.append("on distribuidora_horario_dia_entre.id_distr_bairro = distribuidora_bairro_entrega.id_distr_bairro ");
					varname11.append(" ");
					varname11.append("where cod_bairro = ? and distribuidora_bairro_entrega.id_distribuidora = ? and cod_dia = ? and horario_ini <= ? and horario_fim >= ? order by horario_ini ");
					varname11.append(" ");

					int dayOfWeek = Utilitario.diaDasemanaFromDate(dataagendamento);

					PreparedStatement st3 = conn.prepareStatement(varname11.toString());
					st3.setLong(1, rs.getInt("cod_bairro"));
					st3.setLong(2, rs.getInt("id_distribuidora"));
					st3.setLong(3, dayOfWeek);
					st3.setString(4, dataagendamentohora.substring(0, 1) + dataagendamentohora.substring(1, 2) + ":" + dataagendamentohora.substring(2, 3) + dataagendamentohora.substring(3, 4));
					st3.setString(5, dataagendamentohora.substring(0, 1) + dataagendamentohora.substring(1, 2) + ":" + dataagendamentohora.substring(2, 3) + dataagendamentohora.substring(3, 4));
					ResultSet rs3 = st3.executeQuery();
					if (!rs3.next()) {
						throw new Exception("A loja não atende neste horário.");
					}

					st.setTimestamp(20, Utilitario.getTimeStamp(new SimpleDateFormat("dd/MM/yyyyHHmm").parse(dataagendamento + dataagendamentohora)));

				} else {
					st.setNull(20, java.sql.Types.TIMESTAMP);
				}

				st.setString(21, obsinfo);

				email = rs.getString("DESC_EMAIL");

				st.executeUpdate();

				sql = new StringBuffer();
				sql.append("select * from carrinho_item inner join produtos_distribuidora on produtos_distribuidora.ID_PROD_DIST = carrinho_item.ID_PROD_DIST  where id_carrinho = ? ");

				st2 = conn.prepareStatement(sql.toString());
				st2.setLong(1, idcarrinho);
				rs2 = st2.executeQuery();
				boolean valores_carrinho_ok = true;
				while (rs2.next()) {

					valores_carrinho_ok = validacaoProdutoDistribuidora(request, response, conn, rs2.getLong("id_prod_dist"), rs2.getDouble("val_prod"), true);

					sql = new StringBuffer();
					sql.append(" INSERT INTO pedido_item ");
					sql.append("   (`ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`) ");
					sql.append(" VALUES ");
					sql.append("  (?, ?, ?, ?, ?);");

					st = conn.prepareStatement(sql.toString());
					st.setLong(1, idped);
					st.setLong(2, Utilitario.retornaIdinsertChaveSecundaria("pedido_item", "id_pedido", idped + "", "seq_item", conn));
					st.setDouble(3, rs2.getDouble("val_prod"));
					st.setLong(4, rs2.getLong("id_prod"));
					st.setLong(5, rs2.getLong("qtd"));
					subtotal = subtotal + (rs2.getDouble("val_prod") * rs2.getLong("qtd"));
					st.executeUpdate();

				}

				if (!valores_carrinho_ok) {
					retorno.put("relcaccarrinho", true);
					conn.rollback();
				} else {
					retorno.put("relcaccarrinho", false);
					if (choiceserv.equalsIgnoreCase("T")) {
						if (valmin_entrgega > subtotal) {
							throw new Exception("Sua pedido não atingiu o valor mínimo de entrega da distribuidora. O valor mínimo é de R$ " + df2.format(valmin_entrgega));
						}
					}

					if (choiceserv.equalsIgnoreCase("A")) {

						GregorianCalendar datadesejada = new GregorianCalendar();
						datadesejada.setTime(new SimpleDateFormat("dd/MM/yyyyHHmm").parse(dataagendamento + dataagendamentohora));

						GregorianCalendar dataminimo = new GregorianCalendar();
						dataminimo.setTime(new Date());
						dataminimo.add(Calendar.HOUR, Integer.parseInt(rs.getString("tempo_minimo_entrega").substring(0, 2)));
						dataminimo.add(Calendar.MINUTE, Integer.parseInt(rs.getString("tempo_minimo_entrega").substring(3, 5)));

						if (datadesejada.getTime().before(dataminimo.getTime())) {
							throw new Exception("O tempo mínimo para entrega nesta loja é " + rs.getString("tempo_minimo_entrega"));
						}

					} else if (choiceserv.equalsIgnoreCase("T")) {

						if (rs.getString("tempo_minimo_entrega") != null) {
							GregorianCalendar datadesejada = new GregorianCalendar();
							datadesejada.setTime(new Date());
							datadesejada.add(Calendar.HOUR, Integer.parseInt(tempomax.substring(0, 2)));
							datadesejada.add(Calendar.MINUTE, Integer.parseInt(tempomax.substring(2, 4)));

							GregorianCalendar dataminimo = new GregorianCalendar();
							dataminimo.setTime(new Date());
							dataminimo.add(Calendar.HOUR, Integer.parseInt(rs.getString("tempo_minimo_entrega").substring(0, 2)));
							dataminimo.add(Calendar.MINUTE, Integer.parseInt(rs.getString("tempo_minimo_entrega").substring(3, 5)));

							if (datadesejada.getTime().before(dataminimo.getTime())) {
								throw new Exception("O tempo mínimo para entrega nesta loja é " + rs.getString("tempo_minimo_entrega"));
							}
						}
					}

					if (tipo_pagamento.equalsIgnoreCase("D")) {

						if (!trocopara.equalsIgnoreCase("")) {

							try {
								Double.parseDouble(trocopara);
							} catch (Exception e) {
								throw new Exception("Troco inválido!");
							}

							if (rs.getDouble("val_prod") > Double.parseDouble(trocopara) && Double.parseDouble(trocopara) != 0) {
								throw new Exception("O valor de 'troco para' deve ser maior que o valor do pedido.");
							}

							sql = new StringBuffer();
							sql.append("UPDATE pedido ");
							sql.append("   SET `NUM_TROCOPARA` = ?  ");
							sql.append("WHERE  `ID_PEDIDO` = ? ");

							st = conn.prepareStatement(sql.toString());
							st.setDouble(1, Double.parseDouble(trocopara));
							st.setLong(2, idped);

							st.executeUpdate();
						}
					}

					if (!c_telefone.equalsIgnoreCase("")) {

						sql = new StringBuffer();
						sql.append("UPDATE usuario ");
						sql.append("   SET  ");
						sql.append("       `DESC_TELEFONE` = ? ");
						sql.append("WHERE  `ID_USUARIO` = ? ");

						st = conn.prepareStatement(sql.toString());
						st.setString(1, c_telefone);
						st.setLong(2, cod_usuario);

						st.executeUpdate();

					}

					// payment(request, response, conn, cod_usuario,email);

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

					if (choiceserv.equalsIgnoreCase("A")) {

						try {
							msgLojasMobile(conn, sys, idped, 2);
						} catch (Exception e) {
							System.out.println("Falha no envio de email cria pedido " + idped + " " + new Date());
						}

					}

				}
			}
		} else

		{
			throw new Exception("Não há itens em seu carrinho!");
		}
		retorno.put("msg", "ok");
		retorno.put("id_pedido", idped);

		if (outprint)
			out.print(retorno.toJSONString());
		else {

		}

		return retorno;
	}

	private static void listaLojasProds(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		// TODO cidade
		String idloja = request.getParameter("idloja") == null ? "" : request.getParameter("idloja");
		String desc_pesquisa = request.getParameter("desc_pesquisa") == null ? "" : request.getParameter("desc_pesquisa");
		String pag = request.getParameter("pag") == null ? "" : request.getParameter("pag");
		String fp_ordem = request.getParameter("fp_ordem") == null ? "" : request.getParameter("fp_ordem");
		String id_categoria = request.getParameter("id_categoria") == null ? "" : request.getParameter("id_categoria");
		String id_marca = request.getParameter("id_marca") == null ? "" : request.getParameter("id_marca");

		if (pag.equalsIgnoreCase("")) {
			pag = "1";
		}

		PrintWriter out = response.getWriter();
		JSONObject ret = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append(" select produtos.id_prod, ");
		sql.append("               desc_prod, ");
		sql.append("               desc_abreviado, ");
		sql.append("               produtos_distribuidora.val_prod, ");
		sql.append("               distribuidora.id_distribuidora, ");
		sql.append("               produtos_distribuidora.id_prod_dist, ");
		sql.append("               desc_nome_abrev, desc_categoria, desc_key_words,desc_marca, ");
		sql.append("               carrinho_item.qtd, ");
		sql.append("               distribuidora.val_entrega_min ");
		sql.append("                ");
		sql.append("        from   produtos ");
		sql.append("               inner join produtos_distribuidora ");
		sql.append("                       on produtos_distribuidora.id_prod = produtos.id_prod ");
		sql.append("               inner join distribuidora ");
		sql.append("                       on produtos_distribuidora.id_distribuidora = ");
		sql.append("                          distribuidora.id_distribuidora ");
		sql.append("       LEFT JOIN prod_categoria ");
		sql.append("              ON prod_categoria.id_prod = produtos.id_prod ");
		sql.append("       LEFT JOIN categoria ");
		sql.append("              ON categoria.id_categoria = prod_categoria.id_categoria ");
		sql.append("       LEFT JOIN marca ");
		sql.append("              ON produtos.id_marca = marca.id_marca ");
		sql.append("  ");
		sql.append("               left join carrinho ");
		sql.append("                      on carrinho.id_usuario =   " + cod_usuario);
		sql.append("               left join carrinho_item ");
		sql.append("                      on carrinho_item.id_prod_dist = ");
		sql.append("                         produtos_distribuidora.id_prod_dist ");
		sql.append("                         and carrinho_item.id_carrinho = carrinho.id_carrinho ");
		sql.append("        where  produtos_distribuidora.flag_ativo = 'S' ");
		sql.append("               and distribuidora.flag_ativo_master = 'S' ");
		sql.append("               and distribuidora.id_distribuidora =  ? ");

		String[] keys = null;

		if (!desc_pesquisa.equalsIgnoreCase("")) {

			keys = desc_pesquisa.split(" ");
			for (int i = 0; i < keys.length; i++) {
				sql.append(" and (desc_abreviado  like ?  or desc_categoria like ? or desc_key_words like ? or desc_marca like ? )");
			}

		}

		if (id_categoria.equalsIgnoreCase("O")) {
			sql.append(" and categoria.id_categoria is null ");
		} else if (!id_categoria.equalsIgnoreCase("")) {
			sql.append(" and categoria.id_categoria = ?  ");
		}

		if (id_marca.equalsIgnoreCase("O")) {
			sql.append(" and marca.id_marca is null ");
		} else if (!id_marca.equalsIgnoreCase("")) {
			sql.append(" and marca.id_marca = ?  ");
		}

		if (fp_ordem.equalsIgnoreCase("P")) {
			sql.append(" order by produtos_distribuidora.val_prod asc limit " + 20 + " OFFSET " + (Integer.parseInt(20 + "") * (Integer.parseInt(pag) - 1)));
		} else if (fp_ordem.equalsIgnoreCase("N")) {
			sql.append(" order by produtos.desc_abreviado asc limit " + 20 + " OFFSET " + (Integer.parseInt(20 + "") * (Integer.parseInt(pag) - 1)));
		}

		PreparedStatement st = conn.prepareStatement(sql.toString());

		int contparam = 1;
		st.setInt(contparam, Integer.parseInt(idloja));
		contparam++;

		if (!desc_pesquisa.equalsIgnoreCase("")) {

			keys = desc_pesquisa.split(" ");
			for (int i = 0; i < keys.length; i++) {
				st.setString(contparam, "%" + keys[i] + "%");
				contparam++;
				st.setString(contparam, "%" + keys[i] + "%");
				contparam++;
				st.setString(contparam, "%" + keys[i] + "%");
				contparam++;
				st.setString(contparam, "%" + keys[i] + "%");
				contparam++;
			}

		}

		if (id_categoria.equalsIgnoreCase("O")) {
		} else if (!id_categoria.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_categoria));
			contparam++;
		}

		if (id_marca.equalsIgnoreCase("O")) {
		} else if (!id_marca.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_marca));
			contparam++;
		}

		JSONArray prods = new JSONArray();
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject prod = new JSONObject();
			prod.put("ID_PROD", rs.getString("id_prod"));
			prod.put("ID_PROD_DIST", rs.getString("id_prod_dist"));
			prod.put("QTD", rs.getString("qtd") == null ? "" : rs.getString("qtd"));
			prod.put("DESC_PROD", rs.getString("desc_prod"));
			prod.put("DESC_ABREVIADO", rs.getString("desc_abreviado"));// abreviado do produto
			prod.put("VAL_PROD", "R$ " + df2.format(rs.getDouble("val_prod")));//
			prod.put("ID_DISTRIBUIDORA", rs.getString("id_distribuidora"));
			prod.put("DESC_NOME_ABREV", rs.getString("desc_nome_abrev"));/// abreviado da distribuidora
			prod.put("img", rs.getString("id_prod") + "_min.jpg");/// abreviado da distribuidora
			prods.add(prod);
		}

		double valcar = retornaValCarrinho(cod_usuario, conn);
		if (valcar != 0) {
			ret.put("temcar", true);
		} else {
			ret.put("temcar", false);
		}

		ret.put("valcar", df2.format(valcar));
		ret.put("prods", prods);
		ret.put("msg", "ok");

		out.print(ret.toJSONString());

	}

	private static void detalheLoja(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {
		// TODO cidade
		String idloja = request.getParameter("idloja") == null ? "" : request.getParameter("idloja");
		PrintWriter out = response.getWriter();
		JSONObject ret = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT cod_bairro, ");
		sql.append("       txt_obs_hora, ");
		sql.append("       id_distribuidora, ");
		sql.append("       desc_cidade, ");
		sql.append("       desc_razao_social, ");
		sql.append("       val_entrega_min, ");
		sql.append("       desc_telefone, ");
		sql.append("       desc_endereco, ");
		sql.append("       num_enderec, ");
		sql.append("       desc_loja, ");
		sql.append("       date_format(tempo_minimo_entrega, '%H:%i') as tempo_minimo_entrega, ");
		sql.append("       desc_complemento, ");
		sql.append("       val_tele_entrega, ");
		sql.append("       flag_custom, ");
		sql.append("       desc_mail, ");
		sql.append("       COALESCE(flag_ativo, 'N') AS flag_ativo, ");
		sql.append("       flag_modopagamento, ");
		sql.append("       flag_agendamento,flag_tele_entrega,flag_retirada ");
		sql.append(" FROM   distribuidora ");
		sql.append(" inner join cidade ");
		sql.append(" on cidade.cod_cidade = distribuidora.cod_cidade  ");

		sql.append(" WHERE  id_distribuidora = ? ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, Integer.parseInt(idloja));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			ret.put("nome_img", "images/logos/logo_" + idloja + ".jpg");
			ret.put("id_distribuidora", rs.getString("id_distribuidora"));
			ret.put("desc_cidade", rs.getString("desc_cidade"));

			String endereco = rs.getString("desc_endereco");
			String num = rs.getString("num_enderec");
			String compl = rs.getString("desc_complemento");
			String bairro = Utilitario.getNomeBairro(conn, rs.getInt("cod_bairro"), 0);

			String endfinal = endereco + ", " + num + " " + compl + ", " + bairro + " \n";
			ret.put("endfinal", endfinal);
			ret.put("desc_razao_social", rs.getString("desc_razao_social"));
			ret.put("val_entrega_min", rs.getString("val_entrega_min"));
			ret.put("desc_telefone", rs.getString("desc_telefone"));
			ret.put("desc_endereco", rs.getString("desc_endereco"));
			ret.put("num_enderec", rs.getString("num_enderec"));
			ret.put("desc_complemento", rs.getString("desc_complemento"));
			ret.put("cod_bairro", rs.getString("cod_bairro"));
			ret.put("desc_mail", rs.getString("desc_mail"));

			String servico = "";

			if (rs.getString("flag_agendamento").equalsIgnoreCase("S")) {
				servico = servico + ", Agendamento";
			}

			if (rs.getString("flag_tele_entrega").equalsIgnoreCase("S")) {
				servico = servico + ", Tele-entrega";
			}

			if (rs.getString("flag_retirada").equalsIgnoreCase("S")) {
				servico = servico + ", Retirada em Local";
			}

			servico = servico.replaceFirst(",", "");

			ret.put("desc_servico", servico);
			ret.put("flag_agendamento", rs.getString("flag_agendamento"));
			ret.put("flag_tele_entrega", rs.getString("flag_tele_entrega"));
			ret.put("flag_retirada", rs.getString("flag_retirada"));

			ret.put("tempo_minimo_entrega", (rs.getString("tempo_minimo_entrega")));
			ret.put("desc_loja", (rs.getString("desc_loja")));
			ret.put("flag_custom", (rs.getString("flag_custom")));
			ret.put("text_perso", "Esta loja está temporariamente com o 'Modo de período personalizado' ativado.\nO horário abaixo é válido para todos os dias da semana. ");
			ret.put("txt_obs_hora", rs.getString("txt_obs_hora"));
			ret.put("flag_modopagamento", Utilitario.returnModoPagamento(rs.getString("flag_modopagamento").equalsIgnoreCase("A") ? "DC" : rs.getString("flag_modopagamento")));// TODO se mudar os modos de pagamento

		}

		out.print(ret.toJSONString());

	}

	private static void listaLojas(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		String fp_pickbairro = request.getParameter("fp_pickbairro") == null ? "" : request.getParameter("fp_pickbairro");// bairro da loja
		String hp_sit = request.getParameter("hp_sit") == null ? "" : request.getParameter("hp_sit");
		String data_agend = request.getParameter("data_agend") == null ? "" : request.getParameter("data_agend");
		String bairroatendimento = request.getParameter("bairroatendimento") == null ? "" : request.getParameter("bairroatendimento"); // bairro onde atende
		String fp_flag_entre_ret = request.getParameter("fp_flag_entre_ret") == null ? "" : request.getParameter("fp_flag_entre_ret");

		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql.append("select * , case when flag_ativo = 'S' then 0 else 1 end as ordem ");
		sql.append("  ");
		sql.append(" from distribuidora ");
		sql.append(" left join bairros ");
		sql.append(" on bairros.cod_bairro = distribuidora.cod_bairro ");
		sql.append(" ");
		sql.append(" where distribuidora. cod_cidade = ? ");
		sql.append(" ");

		if (!fp_pickbairro.equalsIgnoreCase("")) {
			sql.append("  and  bairros.cod_bairro = ? ");
		}

		if (hp_sit.equalsIgnoreCase("N")) {
			sql.append("  and  (distribuidora.flag_ativo = 'N' or  distribuidora.flag_ativo = 'F') ");
		} else if (hp_sit.equalsIgnoreCase("S")) {
			sql.append("  and  distribuidora.flag_ativo = 'S' ");
		}

		if (fp_flag_entre_ret.equalsIgnoreCase("A")) {
			sql.append("  and  distribuidora.flag_agendamento = 'S' ");
		} else if (fp_flag_entre_ret.equalsIgnoreCase("T")) {
			sql.append("  and  distribuidora.flag_tele_entrega = 'S' ");
		} else if (fp_flag_entre_ret.equalsIgnoreCase("L")) {
			sql.append("  and  distribuidora.flag_retirada = 'S' ");
		}

		sql.append("  order by ordem, desc_nome_abrev  ");

		PreparedStatement st = conn.prepareStatement(sql.toString());

		int contparam = 1;

		st.setLong(contparam, 1);// TODO
		contparam++;

		if (!fp_pickbairro.equalsIgnoreCase("")) {
			st.setLong(contparam, Long.parseLong(fp_pickbairro));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		JSONArray lojas = new JSONArray();
		while (rs.next()) {
			JSONObject loja = new JSONObject();

			loja.put("id_distribuidora", rs.getLong("id_distribuidora"));
			loja.put("desc_nome_abrev", rs.getString("desc_nome_abrev"));

			if (rs.getString("flag_ativo").equalsIgnoreCase("F") || rs.getString("flag_ativo").equalsIgnoreCase("N")) {
				loja.put("desc_status", "Offline");
			} else {
				loja.put("desc_status", "Online");
			}

			if (rs.getString("flag_ativo").equalsIgnoreCase("F")) {
				loja.put("flag_ativo", "N");
			} else {
				loja.put("flag_ativo", rs.getString("flag_ativo"));
			}

			String servico = "";

			if (rs.getString("flag_agendamento").equalsIgnoreCase("S")) {
				servico = servico + ", Agendamento";
			}

			if (rs.getString("flag_tele_entrega").equalsIgnoreCase("S")) {
				servico = servico + ", Tele-entrega";
			}

			if (rs.getString("flag_retirada").equalsIgnoreCase("S")) {
				servico = servico + ", Retirada em Local";
			}

			servico = servico.replaceFirst(",", "");

			loja.put("desc_servico", servico);
			loja.put("desc_bairro", rs.getString("desc_bairro") == null ? "" : rs.getString("desc_bairro"));
			loja.put("logo", "images/logos/logo_" + rs.getLong("id_distribuidora") + ".jpg");

			if (!data_agend.equalsIgnoreCase("")) {
				loja.put("desc_horario", txtHoraAtendConsulta(request, response, conn, cod_usuario, sys, bairroatendimento, data_agend, rs.getInt("id_distribuidora"), "", true));
			}

			String endereco = rs.getString("desc_endereco");
			String num = rs.getString("num_enderec");
			String compl = rs.getString("desc_complemento");
			String endfinal = endereco + ", " + num + " " + compl;

			loja.put("endfinal", endfinal);

			lojas.add(loja);

		}
		retorno.put("lojas", lojas);
		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());

	}

	private static void listaHorariosBairro(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		String cod_bairro = request.getParameter("cod_bairro") == null ? "" : request.getParameter("cod_bairro");
		String id_loja = request.getParameter("idloja") == null ? "" : request.getParameter("idloja");

		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		PreparedStatement st3 = null;
		ResultSet rs3 = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;

		String sql = " select * from dias_semana ";
		st2 = conn.prepareStatement(sql);
		rs2 = st2.executeQuery();
		while (rs2.next()) {

			String horario = "";

			StringBuffer sql2 = new StringBuffer();
			sql2.append(" select *, case when flag_telebairro = 'S' then distribuidora_bairro_entrega.val_tele_entrega else distribuidora.val_tele_entrega end as frete   from distribuidora_bairro_entrega ");
			sql2.append(" ");
			sql2.append(" left join distribuidora_horario_dia_entre ");
			sql2.append(" on distribuidora_horario_dia_entre.ID_DISTR_BAIRRO = distribuidora_bairro_entrega.id_distr_bairro ");
			sql2.append(" inner join distribuidora ");
			sql2.append(" on distribuidora.id_distribuidora = distribuidora_horario_dia_entre.id_distribuidora ");
			sql2.append(" ");
			sql2.append(" where Coalesce(distribuidora_bairro_entrega.id_distribuidora,? ) = ? and distribuidora_bairro_entrega.cod_bairro = ? and cod_dia = " + rs2.getString("cod_dia") + " ORDER BY HORARIO_INI desc");

			st3 = conn.prepareStatement(sql2.toString());
			st3.setInt(1, Integer.parseInt(id_loja));
			st3.setInt(2, Integer.parseInt(id_loja));
			st3.setInt(3, Integer.parseInt(cod_bairro));

			rs3 = st3.executeQuery();
			while (rs3.next()) {
				String horaini = new SimpleDateFormat("HH:mm").format(rs3.getTimestamp("HORARIO_INI"));
				String horafim = new SimpleDateFormat("HH:mm").format(rs3.getTimestamp("HORARIO_FIM"));

				horario = ", " + horaini + " - " + horafim + horario;

				retorno.put("frete", rs3.getString("frete"));

			}

			if (horario.equalsIgnoreCase("")) {

				if (rs2.getInt("cod_dia") == 6) {
					retorno.put("sab_horas", "Nenhum atendimento.");
				} else {
					retorno.put(rs2.getString("desc_dia").substring(0, 3).toLowerCase() + "_horas", "Nenhum atendimento.");
				}

			} else {

				horario = horario.replaceFirst(", ", "");
				if (rs2.getInt("cod_dia") == 6) {
					retorno.put("sab_horas", horario);
				} else {
					retorno.put(rs2.getString("desc_dia").substring(0, 3).toLowerCase() + "_horas", horario);
				}
			}

		}

		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());

	}

	private static void LojacarregaBairroServ(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		String id_loja = request.getParameter("idloja") == null ? "" : request.getParameter("idloja");

		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		PreparedStatement st2 = null;
		ResultSet rs2 = null;

		StringBuffer sql2 = new StringBuffer();
		sql2.append(" select bairros.cod_bairro,desc_bairro from bairros ");

		sql2.append(" inner join distribuidora_bairro_entrega ");
		sql2.append(" on bairros.cod_bairro = distribuidora_bairro_entrega.cod_bairro ");

		sql2.append(" inner join distribuidora_horario_dia_entre  ");
		sql2.append(" on distribuidora_horario_dia_entre.id_distr_bairro   =   distribuidora_bairro_entrega.id_distr_bairro  ");

		sql2.append(" inner join distribuidora ");
		sql2.append(" on distribuidora_bairro_entrega.id_distribuidora = distribuidora.id_distribuidora ");

		sql2.append("  where distribuidora. id_distribuidora = ? and distribuidora.cod_cidade = ? and  cod_dia = ?  and (now() between horario_ini and horario_fim)  ");

		JSONArray bairros = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("cod_bairro", "");
		obj.put("desc_bairro", "Escolha um bairro");
		bairros.add(obj);

		st2 = conn.prepareStatement(sql2.toString());

		st2.setInt(1, Integer.parseInt(id_loja));
		st2.setInt(2, 1);// TODO
		st2.setInt(3, Utilitario.diaSemana(conn, Integer.parseInt(id_loja)));

		rs2 = st2.executeQuery();
		boolean tembairro = false;
		while (rs2.next()) {
			obj = new JSONObject();
			obj.put("cod_bairro", rs2.getString("cod_bairro"));
			obj.put("desc_bairro", rs2.getString("desc_bairro"));
			tembairro = true;
			bairros.add(obj);
		}

		if (!tembairro) {
			bairros = new JSONArray();
			obj = new JSONObject();
			obj.put("cod_bairro", "");
			obj.put("desc_bairro", "Nenhum bairro disponivel");
			bairros.add(obj);

		}

		retorno.put("bairros", bairros);

		sql2 = new StringBuffer();
		sql2.append(" select * from distribuidora ");
		sql2.append("  where distribuidora. ID_DISTRIBUIDORA = ? and distribuidora.cod_cidade = ? ");

		st2 = conn.prepareStatement(sql2.toString());

		st2.setInt(1, Integer.parseInt(id_loja));
		st2.setInt(2, 1);// TODO

		rs2 = st2.executeQuery();

		JSONArray servicos = new JSONArray();

		obj = new JSONObject();
		obj.put("flag_servico", "");
		obj.put("desc_servico", "Escolha um serviço");
		servicos.add(obj);

		if (rs2.next()) {

			if (rs2.getString("flag_agendamento").equalsIgnoreCase("S")) {
				obj = new JSONObject();
				obj.put("flag_servico", "A");
				obj.put("desc_servico", Utilitario.returnDistrTiposServicoMob("A"));
				servicos.add(obj);
			}

			if (rs2.getString("flag_tele_entrega").equalsIgnoreCase("S")) {
				obj = new JSONObject();
				obj.put("flag_servico", "T");
				obj.put("desc_servico", Utilitario.returnDistrTiposServicoMob("T"));
				servicos.add(obj);
			}

			if (rs2.getString("flag_retirada").equalsIgnoreCase("S")) {
				obj = new JSONObject();
				obj.put("flag_servico", "L");
				obj.put("desc_servico", Utilitario.returnDistrTiposServicoMob("L"));
				servicos.add(obj);
			}

		}

		retorno.put("servicos", servicos);
		retorno.put("bairros", bairros);

		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());
	}

	private static void LojaProdTestOnline(HttpServletRequest request, HttpServletResponse response, Connection conn, long cod_usuario, Sys_parametros sys) throws Exception {

		String id_loja = request.getParameter("idloja") == null ? "" : request.getParameter("idloja");
		String choiveserv = request.getParameter("choiceserv") == null ? "" : request.getParameter("choiceserv");

		// TODO cidade?
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append(" select flag_ativo,flag_ativo_master from  distribuidora where id_distribuidora = ? ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, Integer.parseInt(id_loja));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			if (!choiveserv.equalsIgnoreCase("A")) {
				if (!rs.getString("flag_ativo").equalsIgnoreCase("S")) {
					throw new Exception("A loja está offline no momento.");
				}
			}

			if (!rs.getString("flag_ativo_master").equalsIgnoreCase("S")) {
				throw new Exception("A loja está desativada.");
			}

		}

		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());
	}

	private static void testeversao(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		String versao = request.getParameter("versao") == null ? "" : request.getParameter("versao");
		PrintWriter out = response.getWriter();
		JSONObject retorno = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append(" select app_versao from sys_parametros ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			if (!rs.getString("app_versao").equalsIgnoreCase(versao)) {
				throw new Exception("Atenção! A versão do aplicativo está desatualizada. Por favor atualize na Store! Se você optar por não atualizar, pode encontrar problemas e instabilidade durante o uso.");
			}

		}

		retorno.put("msg", "ok");
		out.print(retorno.toJSONString());
	}

	public static void main(String[] args) {

		Connection conn = null;
		try {
			conn = Conexao.getConexao();

			// Utilitario.retornaIdinsertChaveSecundaria("pedido_item", "id_pedido", "5", "seq_item", conn);
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