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

import com.funcs.Home_ajax;
import com.funcs.Parametros_ajax;
import com.funcs.Pedidos_ajax;
import com.funcs.Utilitario;

@SuppressWarnings("unchecked")
@WebServlet(urlPatterns = { "/home" })
public class HomeController extends javax.servlet.http.HttpServlet {
	;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	public void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

	/*	System.out.println("--------entro home");
		Map map = request.getParameterMap();
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			System.out.println(type + " : " + request.getParameter(type));
		}*/

		try {

			String strTipo = request.getParameter("ac");
			if (strTipo == null) {
				strTipo = "home";
			}
			request.setCharacterEncoding("UTF-8");

			if (request.getSession().getAttribute("username") == null) {
				// request.getRequestDispatcher("").forward(request, response);
				if (strTipo.equalsIgnoreCase("ajax")) {
					ajaxErro(request, response);
				} else {
					request.getSession().invalidate();
					response.sendRedirect(request.getContextPath() + "/");
				}
			} else {

				if (strTipo.equalsIgnoreCase("listaped")) {
					listaped(request, response);
				} else if (strTipo.equalsIgnoreCase("listapedfechado")) {
					listapedfechado(request, response);
				} else if (strTipo.equalsIgnoreCase("listaprod")) {
					listaprod(request, response);
				} else if (strTipo.equalsIgnoreCase("listaconfigemp")) {
					listaconfigemp(request, response);
				} else if (strTipo.equalsIgnoreCase("wizardhorarios")) {
					wizardhorarios(request, response);
				} else if (strTipo.equalsIgnoreCase("home")) {
					home(request, response);
				} else if (strTipo.equalsIgnoreCase("logout")) {
					request.getSession().invalidate();
					response.sendRedirect(request.getContextPath() + "/");
					// request.getRequestDispatcher("").forward(request,
					// response);
					return;
				} else if (strTipo.equalsIgnoreCase("ajax")) {
					ajax(request, response);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				// exibeErro(request, response, ex);
			} catch (Exception e) {
			}
		}
	}

	private void ajaxErro(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();

		response.setContentType("text/x-json; charset=UTF-8");
		response.setDateHeader("Expires", 0);
		response.setDateHeader("Last-Modified", new java.util.Date().getTime());
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		request.setCharacterEncoding("UTF-8");

		JSONObject objRetorno = new JSONObject();

		try {
			objRetorno.put("errologin", "Por favor realize o login novamente!");
			out.print(objRetorno.toJSONString());
		} catch (Exception ex) {

			ex.printStackTrace();
			out.print(objRetorno.toJSONString());

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

		int coddistr = Integer.parseInt(request.getSession(false).getAttribute("coddis").toString());
		Connection conn = null;
		JSONObject objRetorno = new JSONObject();

		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			String cmd = request.getParameter("cmd");

			atualizaLastAjax(coddistr, conn);

			if (cmd.equalsIgnoreCase("checkPedidos")) {
				Home_ajax.checkPedidos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("getLogo")) {
				Home_ajax.getLogo(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadMotivos")) {
				Home_ajax.loadMotivos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("autocomplete")) {
				Home_ajax.autoComplete(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaPedidosAbertos")) {
				Pedidos_ajax.carregaPedidosAbertos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaPedidoshistoricos")) {
				Pedidos_ajax.carregaPedidoshistoricos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaBairros")) {
				Pedidos_ajax.carregaBairros(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaPedido_AbertoEnvio")) {
				Pedidos_ajax.carregaPedido_AbertoEnvio(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaPedido_fechado")) {
				Pedidos_ajax.carregaPedido_fechado(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("finalizandoPedido")) {
				Pedidos_ajax.finalizandoPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("responderPedido")) {
				Pedidos_ajax.responderPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaProdutos")) {
				Parametros_ajax.carregaProdutos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("salvar_prod")) {
				Parametros_ajax.salvarProd(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadProduto")) {
				Parametros_ajax.loadProduto(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadCidade")) {
				Parametros_ajax.loadCidade(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadBairrosParam")) {
				Parametros_ajax.loadBairrosParam(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadDiasSemana")) {
				Parametros_ajax.loadDiasSemana(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadDadosEmp")) {
				Parametros_ajax.loadDadosEmp(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("salvarConfigsEmp")) {
				Parametros_ajax.salvarConfigsEmp(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadBairrosWizard")) {
				Parametros_ajax.loadBairrosWizard(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("salvarConfigsHorariosBairros")) {
				Parametros_ajax.salvarConfigsHorariosBairros(request, response, conn, coddistr);
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

	private void atualizaLastAjax(int coddistr, Connection conn) throws Exception {

		String sql = " Update distribuidora set DATE_LASTAJAX = now() where ID_DISTRIBUIDORA = ? ";
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (coddistr));
		st.executeUpdate();

	}

	private void listapedfechado(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/lista_pedidos_historico.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void listaprod(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/produtos_distribuidora.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void listaconfigemp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/config_empresa.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void listaped(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/lista_pedidos_aberto.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void wizardhorarios(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/wizard_bairros_horarios.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void home(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/home.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
}
