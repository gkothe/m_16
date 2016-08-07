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
//@WebServlet(urlPatterns = {""})
public class EmptyController extends javax.servlet.http.HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	private void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		try {
				System.out.println("aqui");
				//response.sendRedirect("");
			//	request.sendRedirect   ("/sys").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	 

}

