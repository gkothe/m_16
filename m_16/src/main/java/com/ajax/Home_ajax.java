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

public class Home_ajax {

	public static void checkPedidos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		JSONArray pedidos_todos = new JSONArray();

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String sql = "select count(id_pedido) as qtd from pedido where ID_DISTRIBUIDORA = ? and flag_status = 'A'";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();

		objRetorno.put("tem", "false");

		if (rs.next()) {
			if (rs.getInt("qtd") != 0) {

				objRetorno.put("qtd", rs.getInt("qtd"));
				objRetorno.put("tem", "true");

			}

		}

		if (objRetorno.get("tem").toString().equalsIgnoreCase("true")) {

			sql = "select id_pedido,DESC_BAIRRO,NUM_PED,VAL_TOTALPROD,DATA_PEDIDO, NOW() as agora from pedido inner join bairros on bairros.cod_bairro = pedido.cod_bairro where ID_DISTRIBUIDORA = ? and flag_status = \'A\' order by data_pedido asc";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);
			rs = st.executeQuery();

			while (rs.next()) {

				JSONObject pedidos = new JSONObject();

				pedidos.put("num_ped", rs.getString("NUM_PED"));
				pedidos.put("desc_bairro", rs.getString("DESC_BAIRRO"));
				pedidos.put("valor", rs.getString("VAL_TOTALPROD"));
				pedidos.put("id_pedido", rs.getString("id_pedido"));

				Date date_pedido = rs.getTimestamp("DATA_PEDIDO");
				Date agora = rs.getTimestamp("agora");
				String texto_minutos = "";
				// double time =

				Calendar date_pedidocal = Calendar.getInstance();
				date_pedidocal.setTime(date_pedido);

				Calendar agoracal = Calendar.getInstance();
				agoracal.setTime(agora);

				long diferenca = agoracal.getTimeInMillis() - date_pedidocal.getTimeInMillis();
				long diferencaMin = diferenca / (60 * 1000); // DIFERENCA EM
																// MINUTOS

				if (diferencaMin <= 60) {
					texto_minutos = "Há " + diferencaMin + " minuto(s).";
				}

				if (diferencaMin >= 60 && diferencaMin <= 1440) {
					diferencaMin = diferencaMin / 60;
					texto_minutos = "Há " + diferencaMin + " horas(s).";

				}

				if (diferencaMin > 1440) {
					diferencaMin = diferencaMin / 1440;
					texto_minutos = "Há " + diferencaMin + " dia(s).";

				}

				pedidos.put("texto_minutos", texto_minutos);
				pedidos_todos.add(pedidos);
			}
		}

		objRetorno.put("pedidos", pedidos_todos);

		out.print(objRetorno.toJSONString());

	}

	public static void getLogo(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();

		JSONObject objRetorno = new JSONObject();

		String sql = "select * from distribuidora  where	 ID_DISTRIBUIDORA = ?";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			objRetorno.put("desc_nome", rs.getString("DESC_NOME_ABREV"));
		}

		
		objRetorno.put("nome_img", "images/logos/logo_" + coddistr + ".jpg");
		out.print(objRetorno.toJSONString());

	}
	
	
	
	
	public static void loadMotivos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {

		PrintWriter out = response.getWriter();

		JSONArray objRetorno = new JSONArray();

		String sql = "select * from  motivos_recusa order by DESC_MOTIVO";

		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			
			JSONObject obj = new JSONObject();
			obj.put("COD_MOTIVO", rs.getString("COD_MOTIVO"));
			obj.put("DESC_MOTIVO", rs.getString("DESC_MOTIVO"));
			
			objRetorno.add(obj);
		}

	
		out.print(objRetorno.toJSONString());

	}
	
	
	
	
	
	

	public static void main(String[] args) {

		try {
			Calendar date_pedidocal = Calendar.getInstance();
			date_pedidocal.setTime(new SimpleDateFormat("dd/MM/yyyy hh:mm").parse("02/08/2016 10:10"));

			Calendar agoracal = Calendar.getInstance();
			agoracal.setTime(new Date());

			long diferenca = agoracal.getTimeInMillis() - date_pedidocal.getTimeInMillis();
			long diferencaMin = diferenca / (60 * 1000); // DIFERENCA EM MINUTOS

			System.out.println(diferencaMin);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
		}

	}

}
