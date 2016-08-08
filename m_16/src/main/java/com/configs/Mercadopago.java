/*
 * Created on 21/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.configs;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mercadopago.MP;

@WebServlet("/paysrvlt")
public class Mercadopago extends HttpServlet {

	public Mercadopago() {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	private void pagamentoMP(HttpServletRequest request, HttpServletResponse response) throws JSONException, Exception {

		MP mp = new MP("TEST-3928083096731492-072914-2aa78c35c6f210a6322c4acf9abe4d14__LD_LC__-222772872");

		String token = request.getParameter("token");
		String email = request.getParameter("email");
		String paymentMethodId = request.getParameter("paymentMethodId");

		JSONObject payment = mp.post("/v1/payments", "{" + "'transaction_amount': 100," + "'token': " + token + "," + "'description': 'Title of what you are paying for'," + "'installments': 1," + "'payment_method_id': '" + paymentMethodId + "'," + "'payer': {" + "'email': '" + email + "'" + "}" + "}");

		System.out.println(payment);

	}

	private void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		Map map = request.getParameterMap();
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			System.out.println(type + " : " + request.getParameter(type));
		}

		String ac = request.getParameter("ac") == null ? "" : request.getParameter("ac");
		try {
			if (ac.equalsIgnoreCase("pag")) {

				// pagamentoMP(request, response);
				String token = request.getParameter("token");

			} else {

				request.getRequestDispatcher("/WEB-INF/pag.html").forward(request, response);

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			// TODO: handle exception
		}

	}
	
	public static void main(String[] args) {
		
		try {
			
			MP mp = new MP("TEST-3928083096731492-072914-2aa78c35c6f210a6322c4acf9abe4d14__LD_LC__-222772872");
			String token ="";//50526f1b791fc9b0cefc0c4a6c7908cc
			String email = "test_user_19653727@testuser.com";
			String paymentMethodId = "visa";

			JSONObject payment = mp.post("/v1/payments", "{" + "'transaction_amount': 100," + "'token': " + token + "," + "'description': 'Title of what you are paying for'," + "'installments': 1," + "'payment_method_id': '" + paymentMethodId + "'," + "'payer': {" + "'email': '" + email + "'" + "}" + "}");
			
			System.out.println(payment);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
}
