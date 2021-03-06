package com.funcs;

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

import com.configs.Conexao;

@SuppressWarnings("unchecked")
@WebServlet(urlPatterns = { "/home/*" })
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

//		System.out.println("--------entro home");
//		Map map = request.getParameterMap();
//		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
//			String type = (String) iterator.next();
//			System.out.println(type + " : " + request.getParameter(type));
//		}
		// System.out.println( request.getParameter("cmd")+" - " + new Date());

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
				} else if (strTipo.equalsIgnoreCase("listapagmods")) {
					listapagmods(request, response);
				}
				else if (strTipo.equalsIgnoreCase("wizardhorarios")) {
					wizardhorarios(request, response);
				} else if (strTipo.equalsIgnoreCase("mobileusers")) {
					mobileusers(request, response);
				} else if (strTipo.equalsIgnoreCase("home")) {
					home(request, response);
				} else if (strTipo.equalsIgnoreCase("dashpedidos")) {
					dashpedidos(request, response);
				} else if (strTipo.equalsIgnoreCase("inserirpedido")) {
					inserirPedido(request, response);
				} else if (strTipo.equalsIgnoreCase("rel_pedidos")) {
					relPedidos(request, response);
				} else if (strTipo.equalsIgnoreCase("rel_produtos")) {
					relProdutos(request, response);
				} else if (strTipo.equalsIgnoreCase("rel_pedidospdf")) {
					Relatorios.relPedidos(request, response, Integer.parseInt(request.getSession(false).getAttribute("coddis").toString()));
				} else if (strTipo.equalsIgnoreCase("rel_produtospdf")) {
					Relatorios.relProdutos(request, response, Integer.parseInt(request.getSession(false).getAttribute("coddis").toString()));
				} else if (strTipo.equalsIgnoreCase("rel_gradehorarios")) {
					rel_gradehorarios(request, response);
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
			// System.out.println( request.getParameter("cmd")+ "-fim "+" - " + new Date());
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
			// System.out.println(cmd);
			atualizaLastAjax(coddistr, conn);

			if (cmd.equalsIgnoreCase("checkPedidos")) {
				Home_ajax.checkPedidos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("getLogo")) {
				Home_ajax.getLogo(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadMotivos")) {
				Home_ajax.loadMotivos(request, response, conn);
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
			} else if (cmd.equalsIgnoreCase("marcarPedido")) {
				Pedidos_ajax.marcarPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("finalizandoPedido")) {
				Pedidos_ajax.finalizandoPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("responderPedido")) {
				Pedidos_ajax.responderPedido(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("insertpedidodistri")) {
				Pedidos_ajax.InsertPedidoDistri(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("carregaProdutos")) {
				Parametros_ajax.carregaProdutos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("cleanBairrosHorarios")) {
				Parametros_ajax.limparBairros(request, response, conn, coddistr);
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
				Parametros_ajax.salvarConfigsHorariosBairrosNovo(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("listaModosPagamento")) {
				Parametros_ajax.listaModosPagamento(request, response, conn, coddistr,false);
			} else if (cmd.equalsIgnoreCase("listaModosPagamentoLoja")) {
				Parametros_ajax.listaModosPagamento(request, response, conn, coddistr,true);
			}  else if (cmd.equalsIgnoreCase("salvarModosPagamento")) {
				Parametros_ajax.salvarModosPagamento(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashServico")) {
				Relatorios.dashServico(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashPagamento")) {
				Relatorios.dashPagamento(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashProdutosQtd")) {
				Relatorios.dashProdutos(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashInfosBasicas")) {
				Relatorios.dashInfosBasicas(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashProdutosVal")) {
				Relatorios.dashProdutosVal(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashHoraPed")) {
				Relatorios.dashHoraPed(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashDayPed")) {
				Relatorios.dashDayPed(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashListaBairros")) {
				Relatorios.dashListaBairros(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashProdBairroSingle")) {
				Relatorios.dashProdBairro_single(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashProdHoraSingle")) {
				Relatorios.dashProdHora_single(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashProdDiaSingle")) {
				Relatorios.dashProdDia_single(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashProdInfosgerais_single")) {
				Relatorios.dashProdInfosgerais_single(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashMeses")) {
				Relatorios.dashMeses(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("dashDiasDoMes")) {
				Relatorios.dashDiasDoMes(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("getfullprodname")) {
				Utilitario.getFullProdname(request, response, conn);
			} else if (cmd.equalsIgnoreCase("listaCategorias")) {
				Parametros_ajax.listaCategorias(request, response, conn,coddistr,true);
			} else if (cmd.equalsIgnoreCase("listaMarcas")) {
				Parametros_ajax.listaMarcas(request, response, conn, coddistr,true);
			} else if (cmd.equalsIgnoreCase("savelojamobileusers")) {
				Parametros_ajax.saveLojaMobileUsers(request, response, conn, coddistr);
			} else if (cmd.equalsIgnoreCase("loadlojamobileusers")) {
				Parametros_ajax.loadLojaMobileUsers(request, response, conn, coddistr);
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

		String sql = " update distribuidora set date_lastajax = now() where id_distribuidora = ? ";
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setLong(1, (coddistr));
		st.executeUpdate();

	}

	private void rel_gradehorarios(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			int coddistr = Integer.parseInt(request.getSession(false).getAttribute("coddis").toString());
			Relatorios.relGradeHorarios(request, response, coddistr);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void dashpedidos(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/dash1_pedidos.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void inserirPedido(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/inserir_pedido.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
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

	private void relPedidos(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/rel_pedidos.html").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	private void relProdutos(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/rel_produtos.html").forward(request, response);
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

	private void listapagmods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/modo_pagamentos.html").forward(request, response);
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

	private void mobileusers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/mobileusers.html").forward(request, response);
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
