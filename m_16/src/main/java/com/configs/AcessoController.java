package com.configs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AcessoController implements SysController {
	Connection connCliente = null;
	Connection connMaster = null;
	HttpSession session = null;

	public void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("----------entro acesso");
		session = request.getSession(false);

		String erroLogin = null;

		if (request.getAttribute("errologin") != null)
			erroLogin = request.getAttribute("errologin").toString();

		if (session != null) {
			if (erroLogin == null)
				if (session.getAttribute("errologin") != null)
					erroLogin = session.getAttribute("errologin").toString();

			session.invalidate();
		}

		if (erroLogin != null) {
			request.setAttribute("errologin", erroLogin);
		}

		String acao = request.getParameter("ac");
		acao = acao == null ? "" : acao;
		
			try {
				processaLogin(request, response);
			} catch (Exception ex) {
				ex.printStackTrace();
				// Fazer foward ara ágina de erro
			}
	}

	
	
	private void processaLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long idUsuario = null;
		Connection conn = null;
		try {

			conn = Conexao.getConexao();

			String user = "-1";
			String password = "-1";

			if (request.getParameter("username") != null) {
				if (request.getParameter("username").compareToIgnoreCase("") != 0) {
					user = request.getParameter("username").trim();
				}
			}

			if (request.getParameter("password") != null) {
				if (request.getParameter("password").compareToIgnoreCase("") != 0) {
					password = request.getParameter("password").trim();
				}
			}
			if (!(password.equals("-1")) && !(user.equals("-1"))) {

				PreparedStatement stmt = conn.prepareStatement(
						"SELECT ID_DISTRIBUIDORA,desc_login FROM distribuidora WHERE  Binary desc_login = ? and Binary desc_senha = ? and flag_ativo_master = 'S' ");
				stmt.setString(1, user);
				stmt.setString(2, password);
				ResultSet rs = stmt.executeQuery();

				if (rs.next()) {
					session = request.getSession(true);
					session.setAttribute("username", rs.getString("desc_login"));
					session.setAttribute("coddis", rs.getInt("ID_DISTRIBUIDORA"));
					


					request.getRequestDispatcher("home?ac=home").forward(request, response);
					
					
				} else {
					throw new Exception("Usuário e/ou senha inválidos.");
				}

			} else {
				throw new Exception("Usuário e/ou senha inválidos.");
			}

		} catch (Exception e) {
			e.printStackTrace();

			request.setAttribute("errologin", e.getMessage());
			//response.sendRedirect(request.getContextPath() + "/");
			request.getSession().invalidate();
			request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);

			if (request.getSession(false) != null) {
				request.getSession(false).invalidate();
			}
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
			}

		}
	}


}
