package com.ajax;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Parametros_ajax {

	public static void carregaProdutos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray prods = new JSONArray();
		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String val_ini = request.getParameter("val_ini") == null ? "" : request.getParameter("val_ini");
		String val_fim = request.getParameter("val_fim") == null ? "" : request.getParameter("val_fim");
		String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");

		String sql = "select produtos.id_prod, desc_prod,Coalesce(val_prod,0) as val_prod, Coalesce(produtos_distribuidora.flag_ativo,'N') as flag_ativo from produtos  left join produtos_distribuidora on produtos.id_prod = produtos_distribuidora.id_prod	 and ID_DISTRIBUIDORA = ? where (produtos.flag_ativo = 'S') ";

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql = sql + " and  Coalesce(produtos_distribuidora.flag_ativo,'N')  = ? ";
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql = sql + " and  produtos.id_prod = ? ";
		}

		if (!val_ini.equalsIgnoreCase("")) {
			sql = sql + " and  Coalesce(VAL_PROD,0) >= ? ";
		}

		if (!val_fim.equalsIgnoreCase("")) {
			sql = sql + " and   Coalesce(VAL_PROD,0) <= ? ";
		}

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);

		int contparam = 2;
		if (!flag_situacao.equalsIgnoreCase("")) {
			st.setString(contparam, flag_situacao);
			contparam++;
		}

		if (!id_produto.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_produto));
			contparam++;
		}

		if (!val_ini.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_ini));
			contparam++;
		}

		if (!val_fim.equalsIgnoreCase("")) {
			st.setDouble(contparam, Double.parseDouble(val_fim));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			obj.put("id_prod", rs.getString("id_prod"));
			obj.put("desc_prod", rs.getString("desc_prod"));
			obj.put("val_prod", rs.getString("val_prod"));
			obj.put("flag_ativo", rs.getString("flag_ativo"));

			prods.add(obj);

		}

		out.print(prods.toJSONString());
	}

	public static void salvarProd(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		
		JSONObject obj = new JSONObject();
		
		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String val_prod = request.getParameter("val_prod") == null ? "" : request.getParameter("val_prod");
		String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");

		String sql = "select * from produtos  left join produtos_distribuidora on produtos.id_prod = produtos_distribuidora.id_prod	 and ID_DISTRIBUIDORA = ? where (produtos.flag_ativo = 'S') and produtos.id_prod = ? ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_produto));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			sql = "select * from produtos_distribuidora where ID_DISTRIBUIDORA = ?  and id_prod = ?  ";

			PreparedStatement st2 = conn.prepareStatement(sql);
			st2.setInt(1, coddistr);
			st2.setInt(2, Integer.parseInt(id_produto));
			ResultSet rs2 = st2.executeQuery();
			if (!rs2.next()) {

				sql = "insert into produtos_distribuidora  (`ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES   (?, ?, ?, ?);   ";
				PreparedStatement st3 = conn.prepareStatement(sql);
				st3.setInt(1, Integer.parseInt(id_produto));
				st3.setInt(2, coddistr);
				st3.setDouble(3, Double.parseDouble(val_prod));
				st3.setString(4, flag_situacao);
				st3.executeUpdate();
			} else {

				sql = "update  produtos_distribuidora set  VAL_PROD = ? , FLAG_ATIVO = ? where ID_DISTRIBUIDORA = ? and id_prod = ?";
				PreparedStatement st3 = conn.prepareStatement(sql);
				st3.setInt(4, Integer.parseInt(id_produto));
				st3.setInt(3, coddistr);
				st3.setDouble(1, Double.parseDouble(val_prod));
				st3.setString(2, flag_situacao);
				st3.executeUpdate();

			}
			
			obj.put("msg", "Produto salvos!");

		}

		out.print(obj.toJSONString());
		
	}

	public static void loadProduto(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();

		JSONObject obj = new JSONObject();

		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String sql = "select DESC_ABREVIADO,Coalesce(val_prod,0) as val_prod,DESC_PROD,Coalesce( produtos_distribuidora.flag_ativo,'N') as flag_ativo,produtos.id_prod from produtos  left join produtos_distribuidora on produtos.id_prod = produtos_distribuidora.id_prod	 and ID_DISTRIBUIDORA = ? where (produtos.flag_ativo = 'S') and produtos.id_prod = ?  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_produto));

		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			obj.put("nome_abreviado", rs.getString("DESC_ABREVIADO"));
			obj.put("p_id_produto", id_produto);
			obj.put("valor_unit", rs.getString("val_prod"));
			obj.put("nome_completo", rs.getString("DESC_PROD"));
			obj.put("flag_ativo", rs.getString("flag_ativo"));
			obj.put("nome_img", "images/produtos/1.jpg");

		}

		out.print(obj.toJSONString());

	}

}
