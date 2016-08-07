package com.configs;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.ajax.Home_ajax;
import com.ajax.Parametros_ajax;
import com.ajax.Pedidos_ajax;
import com.ajax.Utilitario;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "" })
public class MainController extends javax.servlet.http.HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	private void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		try {

		/*	System.out.println("----------entro main");
			
			Map map = request.getParameterMap();
			for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
				      String type = (String) iterator.next();
				      System.out.println(type +" : " +request.getParameter(type));
		     }
			*/
			
			
			
			SysController controller = null;

			String identAcao = request.getParameter("acao") == null ? "" : request.getParameter("acao").toString().trim();

			if (identAcao.toLowerCase().equalsIgnoreCase("log")) {
				controller = new AcessoController();
				if (controller != null) {
					controller.processaRequisicoes(request, response);
				}
			} else if (identAcao.toLowerCase().equalsIgnoreCase("senha_email")) {
				recuperaSenha(request, response);
			} else if (request.getSession().getAttribute("username") == null) {
				request.getSession().invalidate();
				request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
			} else {
				request.getRequestDispatcher("/home").forward(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void recuperaSenha(HttpServletRequest request, HttpServletResponse response) throws Exception {

		PrintWriter out = response.getWriter();
		response.setContentType("text/x-json; charset=UTF-8");
		response.setDateHeader("Expires", 0);
		response.setDateHeader("Last-Modified", new java.util.Date().getTime());
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		JSONObject objRetorno = new JSONObject();

		try {
			conn = Conexao.getConexao();
			String email = request.getParameter("email") == null ? "" : request.getParameter("email").toString().trim();

			String sql = "select desc_login,desc_senha,desc_mail from distribuidora  where desc_mail  = ?";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, email);
			ResultSet rs = st.executeQuery();
			if (!rs.next()) {
				throw new Exception("Não foi encontrado nenhuma empresa com o  email informado.");
			} else {
				Utilitario.sendEmail(rs.getString("desc_mail"), "Suas informações de login são <br> \n Usuário: " + rs.getString("desc_login") + "<br>Senha: " + rs.getString("desc_senha"), "Recuperação de senha");
			}
			objRetorno.put("msg", "ok");

			out.print(objRetorno.toJSONString());
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

}
