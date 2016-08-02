package com.configs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/sys" })
public class MainController extends javax.servlet.http.HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	private void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		try {
			
			SysController controller = null;

			String identAcao = request.getParameter("acao") == null ? ""
					:  request.getParameter("acao").toString().trim();
			
			if (identAcao.toLowerCase().equalsIgnoreCase("log")) {
				controller = new AcessoController();
				if (controller != null) {
					controller.processaRequisicoes(request, response);
				}
			}else if (request.getSession().getAttribute("username") == null) {
				request.getSession().invalidate();
				request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
			}else{
				request.getRequestDispatcher("/home").forward(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
