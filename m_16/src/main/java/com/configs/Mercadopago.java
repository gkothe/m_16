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
import org.codehaus.jettison.json.JSONObject;

import com.mercadopago.MP;

@WebServlet("/paysrvlt")
public class Mercadopago extends HttpServlet {

	public Mercadopago() {

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getWriter().println("Hello");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String user = "oi";

		try {

			MP mp = new MP("TEST-3928083096731492-072914-2aa78c35c6f210a6322c4acf9abe4d14__LD_LC__-222772872");

			Map map = request.getParameterMap();
			for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
				String type = (String) iterator.next();
				System.out.println(type + " : " + request.getParameter(type));
			}

			String token = request.getParameter("token");
			String email = request.getParameter("email");
			String paymentMethodId = request.getParameter("paymentMethodId");

			JSONObject payment = mp.post("/v1/payments",
					"{" + "'transaction_amount': 100," + "'token': "+token+","
							+ "'description': 'Title of what you are paying for'," + "'installments': 1,"
							+ "'payment_method_id': '"+paymentMethodId+"'," + "'payer': {"
							+ "'email': '"+email+"'" + "}" + "}");

			System.out.println(payment);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			// TODO: handle exception
		}

		System.out.println("oi");
		if (user != null) {
			request.getSession().setAttribute("user", user);
			response.sendRedirect("home");
		} else {
			request.setAttribute("error", "Unknown user, please try again");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}

}
