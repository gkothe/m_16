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

import com.ajax.Home_ajax;
import com.ajax.Pedidos_ajax;

/**
 * @author Virtuallis MÃ³dulo responsÃ¡vel pelas tarefas
 */
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

		try {

			if (request.getSession().getAttribute("username") == null) {
				request.getSession().invalidate();
				request.getRequestDispatcher("/sys").forward(request, response);
			}
		} catch (Exception e) {
		}

		String strTipo = request.getParameter("ac");
		if (strTipo == null) {
			strTipo = "home";
		}

		try {
			request.setCharacterEncoding("UTF-8");

			if (strTipo.equalsIgnoreCase("listaped")) {
				listaped(request, response);
			} else if (strTipo.equalsIgnoreCase("listapedfechado")) {
				listapedfechado(request, response);
			} else if (strTipo.equalsIgnoreCase("listaprod")) {
				listaprod(request, response);
			} else if (strTipo.equalsIgnoreCase("listaconfigemp")) {
				listaconfigemp(request, response);
			} else if (strTipo.equalsIgnoreCase("home")) {
				home(request, response);
			} else if (strTipo.equalsIgnoreCase("ajax")) {
				ajax(request, response);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				exibeErro(request, response, ex);
			} catch (Exception e) {
			}
		}
	}

	private void exibeErro(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
		try {

			request.setAttribute("msg_erro", ex.getMessage());
			request.setAttribute("link", "/WEB-INF/jspnp/erro.jsp");
			request.getRequestDispatcher("/WEB-INF/jspnp/home.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
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

			if (cmd.equalsIgnoreCase("checkPedidos")) {
				Home_ajax.checkPedidos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("getLogo")) {
				Home_ajax.getLogo(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadMotivos")) {
				Home_ajax.loadMotivos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaPedidosAbertos")) {
				Pedidos_ajax.carregaPedidosAbertos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaBairros")) {
				Pedidos_ajax.carregaBairros(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaPedido")) {
				Pedidos_ajax.carregaPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("finalizandoPedido")) {
				Pedidos_ajax.finalizandoPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("responderPedido")) {
				Pedidos_ajax.responderPedido(request, response, conn, coddistr);
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
