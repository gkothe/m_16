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
@WebServlet(urlPatterns = { "/admin" })
public class AdminController extends javax.servlet.http.HttpServlet {
	;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processaRequisicoes(request, response);
	}

	public void processaRequisicoes(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("--------entro admin");
		Map map = request.getParameterMap();
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			System.out.println(type + " : " + request.getParameter(type));
		}

		try {

			String strTipo = request.getParameter("ac");
			if (strTipo == null) {
				strTipo = "prod";
			}
			request.setCharacterEncoding("UTF-8");

			if (strTipo.equalsIgnoreCase("prod")) {
				prod_cadastro(request, response);
			} else if (strTipo.equalsIgnoreCase("logout")) {
				request.getSession().invalidate();
				response.sendRedirect(request.getContextPath() + "/");
				// request.getRequestDispatcher("").forward(request,
				// response);
				return;
			} else if (strTipo.equalsIgnoreCase("ajax")) {
				ajax(request, response);
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

		Connection conn = null;
		JSONObject objRetorno = new JSONObject();

		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			Sys_parametros sys = new Sys_parametros(conn);
			String cmd = request.getParameter("cmd");

			String pass = request.getParameter("desc_senha") == null ? "" : request.getParameter("desc_senha");
			if (!pass.equals(sys.getSys_senha())) {
				throw new Exception("Senha incorreta");
			}

			if (cmd.equalsIgnoreCase("inserirProduto")) {
				inserirProd(request, response, conn);
			} else if (cmd.equalsIgnoreCase("inserirCategoria")) {
				inserirCateg(request, response, conn);
			} else if (cmd.equalsIgnoreCase("inserirMarca")) {
				inserirMarca(request, response, conn);
			} else if (cmd.equalsIgnoreCase("atualizarProd")) {
				atualizarProd(request, response, conn);
			} else if (cmd.equalsIgnoreCase("getprod")) {
				getProd(request, response, conn);
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

	
	
	public static void getProd(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objret = new JSONObject();

		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String sql = " select * from  produtos left join prod_categoria on produtos.id_prod = prod_categoria.id_prod where produtos. id_prod = ? ";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_produto));
		ResultSet rs =	st.executeQuery();

		if(rs.next()){
			
			objret.put("id_prod", rs.getInt("id_prod"));
			objret.put("desc_prod", rs.getString("desc_prod"));
			objret.put("desc_abreviado", rs.getString("desc_abreviado"));
			objret.put("qtd_images", rs.getInt("qtd_images"));
			objret.put("id_marca", rs.getInt("id_marca"));
			objret.put("desc_key_words", rs.getString("desc_key_words"));
			objret.put("id_categoria", rs.getInt("id_categoria"));
			
		}
		

		objret.put("msg", "ok");
		out.print(objret.toJSONString());

	}
	
	
	
	public static void inserirProd(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objret = new JSONObject();

		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String desc_abreviado = request.getParameter("desc_abreviado") == null ? "" : request.getParameter("desc_abreviado");
		String desc_prod = request.getParameter("desc_prod") == null ? "" : request.getParameter("desc_prod");
		String key_words = request.getParameter("key_words") == null ? "" : request.getParameter("key_words");
		String id_categoria = request.getParameter("id_categoria") == null ? "" : request.getParameter("id_categoria");
		String id_marca = request.getParameter("id_marca") == null ? "" : request.getParameter("id_marca");
		String qtd_image = request.getParameter("qtd_image") == null ? "" : request.getParameter("qtd_image");

		String sql = "INSERT INTO produtos (id_prod, desc_prod, desc_abreviado, flag_ativo,qtd_images, desc_key_words";
		if (!id_marca.equalsIgnoreCase("")) {
			sql = sql + " , id_marca";
		}
		sql = sql + " ) VALUES (?, ?, ?, ?,?,? ";

		if (!id_marca.equalsIgnoreCase("")) {
			sql = sql + ",?";
		}
		sql = sql + ");";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_produto));
		st.setString(2, desc_prod.trim());
		st.setString(3, desc_abreviado.trim());
		st.setString(4, "S");
		st.setInt(5, Integer.parseInt(qtd_image));
		st.setString(6, key_words.trim());
		if (!id_marca.equalsIgnoreCase("")) {
			st.setInt(7, Integer.parseInt(id_marca));
		}
		st.executeUpdate();

		if (!id_categoria.equalsIgnoreCase("")) {
			sql = "INSERT INTO prod_categoria (id_categoria, id_prod ) VALUES (?, ?);";
			st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(id_categoria));
			st.setInt(2, Integer.parseInt(id_produto));
			st.executeUpdate();
		}

		objret.put("msg", "ok");
		out.print(objret.toJSONString());

	}
	
	
	public static void atualizarProd(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objret = new JSONObject();

		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String desc_abreviado = request.getParameter("desc_abreviado") == null ? "" : request.getParameter("desc_abreviado");
		String desc_prod = request.getParameter("desc_prod") == null ? "" : request.getParameter("desc_prod");
		String key_words = request.getParameter("key_words") == null ? "" : request.getParameter("key_words");
		String id_categoria = request.getParameter("id_categoria") == null ? "" : request.getParameter("id_categoria");
		String id_marca = request.getParameter("id_marca") == null ? "" : request.getParameter("id_marca");
		String qtd_image = request.getParameter("qtd_image") == null ? "" : request.getParameter("qtd_image");
		
		String sql = " update produtos set desc_prod = ?, desc_abreviado = ?,  qtd_images = ? , id_marca = ? , desc_key_words = ? where id_prod = ?; ";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, desc_prod);
		st.setString(2, desc_abreviado);
		st.setInt(3, Integer.parseInt(qtd_image));
		
		if(id_marca.equalsIgnoreCase("")){
			st.setNull(4, java.sql.Types.INTEGER);
		}else{
			st.setInt(4, Integer.parseInt(id_marca));
		}
		
		st.setString(5, key_words);
		st.setInt(6, Integer.parseInt(id_produto));
		
		
		st.executeUpdate();

		sql = "delete from prod_categoria where id_prod = ?;";
		st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(id_produto));
		st.executeUpdate();
		
		if (!id_categoria.equalsIgnoreCase("")) {
			
			sql = "INSERT INTO prod_categoria (id_categoria, id_prod ) VALUES (?, ?);";
			st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(id_categoria));
			st.setInt(2, Integer.parseInt(id_produto));
			st.executeUpdate();
		}

		objret.put("msg", "ok");
		out.print(objret.toJSONString());

	}
	
	
	
	

	public static void inserirMarca(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objret = new JSONObject();

		String desc_marca = request.getParameter("desc_marca") == null ? "" : request.getParameter("desc_marca"); //

		String sql = "INSERT INTO marca (ID_MARCA, DESC_MARCA) VALUES (?, ?);";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Utilitario.retornaIdinsert("marca", "ID_MARCA", conn));
		st.setString(2, desc_marca);
		st.executeUpdate();
		objret.put("msg", "ok");
		out.print(objret.toJSONString());

		


		
	}

	public static void inserirCateg(HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objret = new JSONObject();

		String desc_categoria = request.getParameter("desc_categoria") == null ? "" : request.getParameter("desc_categoria"); //

		String sql = "INSERT INTO categoria   (ID_CATEGORIA, DESC_CATEGORIA) VALUES  (?, ?);";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Utilitario.retornaIdinsert("categoria", "ID_CATEGORIA", conn));
		st.setString(2, desc_categoria);
		st.executeUpdate();
		objret.put("msg", "ok");
		out.print(objret.toJSONString());

	}

	private void prod_cadastro(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getRequestDispatcher("/WEB-INF/prod_cadastro.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

}
