package com.funcs;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class Parametros_ajax {

	// Dias são fixos, 1 segunda feira ate 7 domingo, 8=custom

	public static void carregaProdutos(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray prods = new JSONArray();
		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String val_ini = request.getParameter("val_ini") == null ? "" : request.getParameter("val_ini");
		String val_fim = request.getParameter("val_fim") == null ? "" : request.getParameter("val_fim");
		String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");
		String descricaoprod = request.getParameter("descricaoprod") == null ? "" : request.getParameter("descricaoprod");
		String id_categoria = request.getParameter("id_categoria") == null ? "" : request.getParameter("id_categoria");
		String id_marca = request.getParameter("id_marca") == null ? "" : request.getParameter("id_marca");

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT produtos.id_prod, ");
		sql.append("       desc_prod,desc_categoria,desc_marca, ");
		sql.append("       desc_abreviado, ");
		sql.append("       COALESCE(val_prod, 0)                            AS val_prod, ");
		sql.append("       COALESCE(produtos_distribuidora.flag_ativo, 'N') AS flag_ativo ");
		sql.append("FROM   produtos ");
		sql.append("       LEFT JOIN prod_categoria ");
		sql.append("              ON prod_categoria.id_prod = produtos.id_prod ");
		sql.append("       LEFT JOIN categoria ");
		sql.append("              ON categoria.id_categoria = prod_categoria.id_categoria ");
		sql.append("       LEFT JOIN produtos_distribuidora ");
		sql.append("              ON produtos.id_prod = produtos_distribuidora.id_prod ");
		sql.append("       LEFT JOIN marca ");
		sql.append("              ON produtos.id_marca = marca.id_marca ");
		sql.append("                 AND id_distribuidora = ? ");
		sql.append("WHERE  ( produtos.flag_ativo = 'S' )");

		if (!flag_situacao.equalsIgnoreCase("")) {
			sql.append("  and  Coalesce(produtos_distribuidora.flag_ativo,'N')  = ? ");
		}

		if (!id_produto.equalsIgnoreCase("")) {
			sql.append("  and  produtos.id_prod = ? ");
		}

		if (!val_ini.equalsIgnoreCase("")) {
			sql.append("  and  Coalesce(val_prod,0) >= ? ");
		}

		if (!val_fim.equalsIgnoreCase("")) {
			sql.append("  and   Coalesce(val_prod,0) <= ? ");
		}

		String[] keys = null;
		if (!descricaoprod.equalsIgnoreCase("")) {

			keys = descricaoprod.split(" ");
			for (int i = 0; i < keys.length; i++) {
				sql.append("  and   desc_prod like  ? ");
			}

		}

		if (id_categoria.equalsIgnoreCase("O")) {
			sql.append(" and categoria.id_categoria is null ");
		} else if (!id_categoria.equalsIgnoreCase("")) {
			sql.append(" and categoria.id_categoria = ?  ");
		}

		if (id_marca.equalsIgnoreCase("O")) {
			sql.append(" and marca.id_marca is null ");
		} else if (!id_marca.equalsIgnoreCase("")) {
			sql.append(" and marca.id_marca = ?  ");
		}

		sql.append("  order by desc_abreviado ");

		PreparedStatement st = conn.prepareStatement(sql.toString());
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
		if (!descricaoprod.equalsIgnoreCase("")) {

			keys = descricaoprod.split(" ");
			for (int i = 0; i < keys.length; i++) {
				st.setString(contparam, "%" + keys[i] + "%");
				contparam++;
			}

		}

		if (id_categoria.equalsIgnoreCase("O")) {
		} else if (!id_categoria.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_categoria));
			contparam++;
		}

		if (id_marca.equalsIgnoreCase("O")) {
		} else if (!id_marca.equalsIgnoreCase("")) {
			st.setInt(contparam, Integer.parseInt(id_marca));
			contparam++;
		}

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			obj.put("id_prod", rs.getString("id_prod"));
			obj.put("desc_prod", rs.getString("desc_prod"));
			obj.put("desc_abreviado", rs.getString("desc_abreviado"));
			obj.put("val_prod", rs.getString("val_prod"));
			obj.put("flag_ativo", rs.getString("flag_ativo"));
			obj.put("desc_categoria", rs.getString("desc_categoria") == null ? "Outros" : rs.getString("desc_categoria"));
			obj.put("desc_marca", rs.getString("desc_marca") == null ? "Outros" : rs.getString("desc_marca"));

			prods.add(obj);

		}

		out.print(prods.toJSONString());
	}

	public static void loadProduto(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();

		JSONObject obj = new JSONObject();

		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT qtd_images, ");
		sql.append("       desc_abreviado,desc_categoria,desc_marca, ");
		sql.append("       COALESCE(val_prod, 0)                            AS val_prod, ");
		sql.append("       desc_prod, ");
		sql.append("       COALESCE(produtos_distribuidora.flag_ativo, 'N') AS flag_ativo, ");
		sql.append("       produtos.id_prod ");
		sql.append("FROM   produtos ");
		sql.append("       LEFT JOIN prod_categoria ");
		sql.append("              ON prod_categoria.id_prod = produtos.id_prod ");
		sql.append("       LEFT JOIN categoria ");
		sql.append("              ON categoria.id_categoria = prod_categoria.id_categoria ");
		sql.append("       LEFT JOIN marca ");
		sql.append("              ON produtos.id_marca = marca.id_marca ");
		sql.append("       LEFT JOIN produtos_distribuidora ");
		sql.append("              ON produtos.id_prod = produtos_distribuidora.id_prod ");
		sql.append("                 AND id_distribuidora = ? ");
		sql.append("WHERE  ( produtos.flag_ativo = 'S' ) ");
		sql.append("       AND produtos.id_prod = ?");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_produto));

		ResultSet rs = st.executeQuery();
		if (rs.next()) {

			obj.put("nome_abreviado", rs.getString("desc_abreviado"));
			obj.put("p_id_produto", id_produto);
			obj.put("valor_unit", rs.getString("val_prod"));
			obj.put("nome_completo", rs.getString("desc_prod").replace("<br><br>", ""));
			obj.put("flag_ativo", rs.getString("flag_ativo"));
			obj.put("desc_categoria", rs.getString("desc_categoria") == null ? "Outros" : rs.getString("desc_categoria"));
			obj.put("desc_marca", rs.getString("desc_marca") == null ? "Outros" : rs.getString("desc_marca"));

			JSONArray imagens = new JSONArray();
			int qtd_images = rs.getInt("qtd_images");
			for (int i = 1; i <= qtd_images; i++) {
				imagens.add("images/produtos/" + rs.getString("ID_PROD") + "_" + i + ".jpg");

			}
			obj.put("imgs", imagens);

		}

		out.print(obj.toJSONString());

	}

	public static void listaCategorias(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray cate = new JSONArray();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * ");
		sql.append("FROM   categoria ");

		sql.append("  order by desc_categoria ");

		PreparedStatement st = conn.prepareStatement(sql.toString());

		JSONObject obj = new JSONObject();
		obj.put("id_categoria", "");
		obj.put("desc_categoria", "Todas");

		cate.add(obj);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			obj = new JSONObject();

			obj.put("id_categoria", rs.getString("id_categoria"));
			obj.put("desc_categoria", rs.getString("desc_categoria"));

			cate.add(obj);

		}

		obj = new JSONObject();

		obj.put("id_categoria", "O");
		obj.put("desc_categoria", "Outros");

		cate.add(obj);

		out.print(cate.toJSONString());
	}

	public static void listaMarcas(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray cate = new JSONArray();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * ");
		sql.append("FROM   marca ");

		sql.append("  order by desc_marca ");

		PreparedStatement st = conn.prepareStatement(sql.toString());

		JSONObject obj = new JSONObject();
		obj.put("id_marca", "");
		obj.put("desc_marca", "Todas");

		cate.add(obj);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			obj = new JSONObject();

			obj.put("id_marca", rs.getString("id_marca"));
			obj.put("desc_marca", rs.getString("desc_marca"));

			cate.add(obj);

		}

		obj = new JSONObject();

		obj.put("id_marca", "O");
		obj.put("desc_marca", "Outros");

		cate.add(obj);

		out.print(cate.toJSONString());
	}

	public static void salvarProd(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();

		JSONObject obj = new JSONObject();

		String id_produto = request.getParameter("id_produto") == null ? "" : request.getParameter("id_produto"); //
		String val_prod = request.getParameter("val_prod") == null ? "" : request.getParameter("val_prod");
		String flag_situacao = request.getParameter("flag_situacao") == null ? "" : request.getParameter("flag_situacao");

		String sql = "select * from produtos  left join produtos_distribuidora on produtos.id_prod = produtos_distribuidora.id_prod	 and id_distribuidora = ? where (produtos.flag_ativo = 's') and produtos.id_prod = ? ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.setInt(2, Integer.parseInt(id_produto));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			sql = "select * from produtos_distribuidora where id_distribuidora = ?  and id_prod = ?  ";

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

			obj.put("msg", "Produto salvo!");

		}

		out.print(obj.toJSONString());

	}

	public static void loadCidade(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray prods = new JSONArray();

		String sql = "select * from cidade order by desc_cidade ";

		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			obj.put("COD_CIDADE", rs.getString("cod_cidade"));
			obj.put("DESC_CIDADE", rs.getString("desc_cidade"));

			prods.add(obj);

		}

		out.print(prods.toJSONString());
	}

	public static void loadBairrosParam(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray ret = new JSONArray();

		PreparedStatement st3;
		ResultSet rs3;
		PreparedStatement st2;
		ResultSet rs2;
		JSONObject obj;
		String sql = "select distribuidora_bairro_entrega.cod_bairro, bairros.desc_bairro, coalesce(val_tele_entrega, 0) as val_tele , coalesce(flag_telebairro, 'N') as flag_telebairro,id_distr_bairro from distribuidora_bairro_entrega  inner join bairros on bairros.cod_bairro = distribuidora_bairro_entrega.cod_bairro where id_distribuidora = ? and bairros.cod_cidade  = " + request.getSession(false).getAttribute("cod_cidade").toString() + " order by desc_bairro  ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();

		while (rs.next()) {

			sql = "select * from dias_semana order by cod_dia asc";
			JSONArray dias = new JSONArray();
			st2 = conn.prepareStatement(sql);
			rs2 = st2.executeQuery();
			while (rs2.next()) {
				JSONArray horarios = new JSONArray();
				sql = " select id_horario as id_horario, date_format(horario_ini, '%H:%i')  as horario_ini ,date_format(horario_fim, '%H:%i') as horario_fim from distribuidora_horario_dia_entre where id_distribuidora = ? and id_distr_bairro = ?  and cod_dia =  ? order by  horario_ini asc ";

				st3 = conn.prepareStatement(sql);
				st3.setInt(1, coddistr);
				st3.setInt(2, rs.getInt("id_distr_bairro"));
				st3.setInt(3, rs2.getInt("cod_dia"));
				rs3 = st3.executeQuery();
				while (rs3.next()) {
					obj = new JSONObject();

					obj.put("id_horario", rs3.getLong("id_horario"));
					obj.put("HORARIO_INI", rs3.getString("horario_ini"));
					obj.put("HORARIO_FIM", rs3.getString("horario_fim"));

					horarios.add(obj);
				}

				obj = new JSONObject();
				obj.put("cod_dia", rs2.getString("cod_dia"));
				obj.put("horarios", horarios);
				dias.add(obj);

			}

			JSONObject dados = new JSONObject();
			dados.put("cod_bairro", rs.getString("cod_bairro"));
			dados.put("desc_bairro", rs.getString("desc_bairro"));
			dados.put("dias", dias);
			dados.put("val_tele", rs.getDouble("val_tele"));
			dados.put("flag_telebairro", rs.getString("flag_telebairro"));
			ret.add(dados);
		}

		out.print(ret.toJSONString());
	}

	public static void loadBairrosParam2TEntativaFailed(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray ret = new JSONArray();
		// problema com dias em branco

		JSONObject obj;

		StringBuffer sql = new StringBuffer();
		sql.append("select distribuidora_bairro_entrega.cod_bairro, bairros.desc_bairro, coalesce(val_tele_entrega, 0) as val_tele , coalesce(flag_telebairro, 'N') as flag_telebairro,distribuidora_bairro_entrega.id_distr_bairro, id_horario as id_horario, date_format(horario_ini, '%H:%i')  as horario_ini ,date_format(horario_fim, '%H:%i') as horario_fim, dias_semana.cod_dia from distribuidora_bairro_entrega ");
		sql.append("inner join bairros ");
		sql.append("on bairros.cod_bairro = distribuidora_bairro_entrega.cod_bairro ");
		sql.append("inner join distribuidora_horario_dia_entre ");
		sql.append("on distribuidora_horario_dia_entre.id_distr_bairro = distribuidora_bairro_entrega.id_distr_bairro ");
		sql.append("inner join dias_semana ");
		sql.append("on dias_semana.cod_dia = distribuidora_horario_dia_entre.cod_dia ");
		sql.append(" ");
		sql.append("where distribuidora_bairro_entrega.id_distribuidora = ? order by desc_bairro,cod_dia");

		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();

		int bairroatual = 0;
		int cod_diaatual = 0;
		JSONArray dias = new JSONArray();
		JSONArray horarios = new JSONArray();
		while (rs.next()) {

			if (bairroatual != rs.getInt("cod_bairro")) {
				dias = new JSONArray();
				JSONObject dados = new JSONObject();
				dados.put("cod_bairro", rs.getString("cod_bairro"));
				dados.put("desc_bairro", rs.getString("desc_bairro"));
				dados.put("dias", dias);
				dados.put("val_tele", rs.getDouble("val_tele"));
				dados.put("flag_telebairro", rs.getString("flag_telebairro"));
				ret.add(dados);

				bairroatual = rs.getInt("cod_bairro");
			}

			if (cod_diaatual != rs.getInt("cod_dia")) {
				horarios = new JSONArray();
				obj = new JSONObject();
				obj.put("cod_dia", rs.getString("cod_dia"));
				obj.put("horarios", horarios);
				dias.add(obj);

				cod_diaatual = rs.getInt("cod_dia");
			}

			obj = new JSONObject();

			obj.put("id_horario", rs.getLong("id_horario"));
			obj.put("HORARIO_INI", rs.getString("horario_ini"));
			obj.put("HORARIO_FIM", rs.getString("horario_fim"));

			horarios.add(obj);

		}

		System.out.println(ret.toJSONString());
		out.print(ret.toJSONString());
	}

	public static void loadDiasSemana(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray prods = new JSONArray();

		String sql = "select * from dias_semana ";

		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			JSONObject obj = new JSONObject();

			obj.put("COD_DIA", rs.getString("COD_DIA"));
			obj.put("DESC_DIA", rs.getString("DESC_DIA"));

			prods.add(obj);

		}

		out.print(prods.toJSONString());
	}

	public static void loadDadosEmp(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray ret = new JSONArray();

		String sql = " select cod_bairro,desc_loja, txt_obs_hora, date_format(tempo_minimo_entrega, '%H:%i') as tempo_minimo_entrega, id_distribuidora, cod_cidade,desc_razao_social,desc_nome_abrev,val_entrega_min,desc_telefone,desc_endereco,num_enderec,desc_complemento,val_tele_entrega,flag_custom, desc_mail, coalesce(flag_ativo,'N') as flag_ativo,flag_modopagamento,flag_agendamento,flag_tele_entrega,flag_retirada from distribuidora  where	 id_distribuidora = ? ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			JSONObject obj = new JSONObject();

			sql = "  select max( id_horario) + 1000 as id_horariomax from distribuidora_horario_dia_entre  ";
			st = conn.prepareStatement(sql);
			String id_horariomax = "";
			ResultSet rs2 = st.executeQuery();
			if (rs2.next()) {
				id_horariomax = rs2.getString("id_horariomax");
			}

			obj.put("nome_img", "images/logos/logo_" + coddistr + ".jpg");
			obj.put("id_horariomax", id_horariomax);
			obj.put("ID_DISTRIBUIDORA", rs.getString("id_distribuidora"));
			obj.put("COD_CIDADE", rs.getString("cod_cidade"));
			obj.put("DESC_RAZAO_SOCIAL", rs.getString("desc_razao_social"));
			obj.put("DESC_NOME_ABREV", rs.getString("desc_nome_abrev"));
			obj.put("VAL_ENTREGA_MIN", rs.getString("val_entrega_min"));
			obj.put("DESC_TELEFONE", rs.getString("desc_telefone"));
			obj.put("DESC_ENDERECO", rs.getString("desc_endereco"));
			obj.put("NUM_ENDEREC", rs.getString("num_enderec"));
			obj.put("DESC_COMPLEMENTO", rs.getString("desc_complemento"));
			obj.put("VAL_TELE_ENTREGA", rs.getString("val_tele_entrega"));
			obj.put("flag_custom", rs.getString("flag_custom"));
			obj.put("cod_bairro", rs.getString("cod_bairro"));
			obj.put("desc_loja", rs.getString("desc_loja"));
			obj.put("tempo_minimo_entrega", rs.getString("tempo_minimo_entrega"));
			obj.put("desc_mail", rs.getString("desc_mail"));
			obj.put("flag_ativo", rs.getString("flag_ativo").equalsIgnoreCase("F") ? "S" : rs.getString("flag_ativo"));
			obj.put("flag_modopag", rs.getString("flag_modopagamento"));
			obj.put("txt_obs_hora", rs.getString("txt_obs_hora"));

			obj.put("flag_agendamento", rs.getString("flag_agendamento"));
			obj.put("flag_tele_entrega", rs.getString("flag_tele_entrega"));
			obj.put("flag_retirada", rs.getString("flag_retirada"));
			
			
			ret.add(obj);

		}

		out.print(ret.toJSONString());
	}

	public static void salvarConfigsEmp(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject ret = new JSONObject();

		String desc_razaosocial = request.getParameter("desc_razaosocial") == null ? "" : request.getParameter("desc_razaosocial"); //
		String desc_fantasia = request.getParameter("desc_fantasia") == null ? "" : request.getParameter("desc_fantasia"); //
		String cod_cidade = request.getParameter("cod_cidade") == null ? "" : request.getParameter("cod_cidade"); //
		String num_telefone = request.getParameter("num_telefone") == null ? "" : request.getParameter("num_telefone"); //
		String desc_endereco = request.getParameter("desc_endereco") == null ? "" : request.getParameter("desc_endereco"); //
		String desc_complemento = request.getParameter("desc_complemento") == null ? "" : request.getParameter("desc_complemento"); //
		String desc_num = request.getParameter("desc_num") == null ? "" : request.getParameter("desc_num"); //
		String val_min_entrega = request.getParameter("val_min_entrega") == null ? "" : request.getParameter("val_min_entrega"); //
		String val_padrao_tele = request.getParameter("val_padrao_tele") == null ? "" : request.getParameter("val_padrao_tele"); //
		String flag_custom = request.getParameter("flag_custom") == null ? "" : request.getParameter("flag_custom"); //
		String flag_online = request.getParameter("flag_online") == null ? "" : request.getParameter("flag_online"); //
		String bairrosjson = request.getParameter("bairros") == null ? "" : request.getParameter("bairros"); //
		String desc_mail = request.getParameter("desc_mail") == null ? "" : request.getParameter("desc_mail"); //
		String flag_modopag = request.getParameter("flag_modopag") == null ? "" : request.getParameter("flag_modopag"); //
		String flag_entre_ret = request.getParameter("flag_entre_ret") == null ? "" : request.getParameter("flag_entre_ret"); //
		String txt_obs_hora = request.getParameter("txt_obs_hora") == null ? "" : request.getParameter("txt_obs_hora"); //
		String cod_bairro_distr = request.getParameter("cod_bairro_distr") == null ? "" : request.getParameter("cod_bairro_distr"); //
		String desc_loja = request.getParameter("desc_loja") == null ? "" : request.getParameter("desc_loja"); //
		String tempo_minimo_entrega = request.getParameter("tempo_minimo_entrega") == null ? "00:00" : request.getParameter("tempo_minimo_entrega"); //

		String flag_agendamento = request.getParameter("flag_agendamento") == null ? "" : request.getParameter("flag_agendamento"); //
		String flag_tele_entrega = request.getParameter("flag_tele_entrega") == null ? "" : request.getParameter("flag_tele_entrega"); //
		String flag_retirada = request.getParameter("flag_retirada") == null ? "" : request.getParameter("flag_retirada"); //
		
		
		
		
		if (!(flag_agendamento.equalsIgnoreCase("S")) && !(flag_agendamento.equalsIgnoreCase("N"))) {
			throw new Exception("Dados inválidos, entre em contato com o suporte.");
		}

		
		if (!(flag_tele_entrega.equalsIgnoreCase("S")) && !(flag_tele_entrega.equalsIgnoreCase("N"))) {
			throw new Exception("Dados inválidos, entre em contato com o suporte.");
		}

		
		if (!(flag_retirada.equalsIgnoreCase("S")) && !(flag_retirada.equalsIgnoreCase("N"))) {
			throw new Exception("Dados inválidos, entre em contato com o suporte.");
		}

		if (!(flag_custom.equalsIgnoreCase("S")) && !(flag_custom.equalsIgnoreCase("N"))) {
			throw new Exception("Dados inválidos, entre em contato com o suporte.");
		}

		if (!(flag_online.equalsIgnoreCase("S")) && !(flag_online.equalsIgnoreCase("N"))) {
			throw new Exception("Dados inválidos, entre em contato com o suporte.");
		}

		if (desc_endereco.equalsIgnoreCase("") || desc_num.equalsIgnoreCase("") || cod_bairro_distr.equalsIgnoreCase("")) {
			throw new Exception("Por favor preencha os campos de endereço, número e bairro.");
		}

		JSONArray bairros = (JSONArray) new JSONParser().parse(bairrosjson);

		String sql = " UPDATE distribuidora SET `COD_CIDADE` = ?, `DESC_RAZAO_SOCIAL` = ?, `DESC_NOME_ABREV` = ?, `VAL_ENTREGA_MIN` = ?, `DESC_TELEFONE` = ?, `DESC_ENDERECO` = ?, `NUM_ENDEREC` = ?, `DESC_COMPLEMENTO` = ?, `VAL_TELE_ENTREGA` = ? , flag_custom = ? , flag_ativo = ?, desc_mail =?, FLAG_MODOPAGAMENTO = ? ,  txt_obs_hora = ?, COD_BAIRRO = ?, desc_loja = ? , tempo_minimo_entrega = ?, flag_agendamento = ? , flag_tele_entrega = ? , flag_retirada = ?  WHERE `ID_DISTRIBUIDORA` = ? ";

		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, Integer.parseInt(cod_cidade));
		st.setString(2, desc_razaosocial);
		st.setString(3, desc_fantasia);
		st.setDouble(4, Double.parseDouble(val_min_entrega));
		st.setString(5, num_telefone);
		st.setString(6, desc_endereco);
		st.setString(7, desc_num);
		st.setString(8, desc_complemento);
		st.setDouble(9, Double.parseDouble(val_padrao_tele));
		st.setString(10, flag_custom);
		st.setString(11, flag_online);
		st.setString(12, desc_mail);
		st.setString(13, flag_modopag);
		st.setString(14, txt_obs_hora);
		st.setString(15, cod_bairro_distr);
		st.setString(16, desc_loja);
		st.setString(17, tempo_minimo_entrega);
		st.setString(18, flag_agendamento);
		st.setString(19, flag_tele_entrega);
		st.setString(20, flag_retirada);
		st.setInt(21, coddistr);

		st.executeUpdate();

		sql = " delete from distribuidora_horario_dia_entre where id_distribuidora = ? ";

		st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.executeUpdate();

		sql = " delete from distribuidora_bairro_entrega where id_distribuidora = ?  ";

		st = conn.prepareStatement(sql);
		st.setInt(1, coddistr);
		st.executeUpdate();

		for (int i = 0; i < bairros.size(); i++) {

			JSONObject bairro = new JSONObject();

			bairro = (JSONObject) bairros.get(i);

			if (bairro.get("cod_bairro") == null || bairro.get("cod_bairro") == "") {
				throw new Exception("Dados inválidos, entre em contato com o suporte.");
			}

			if (bairro.get("val_tele") == null || bairro.get("val_tele") == "") {
				throw new Exception("Dados inválidos, entre em contato com o suporte.");
			}

			if (bairro.get("flag_telebairro") == null || bairro.get("flag_telebairro") == "") {
				throw new Exception("Dados inválidos, entre em contato com o suporte.");
			}

			sql = "select * from distribuidora_bairro_entrega where id_distribuidora = ? and cod_bairro = ?";

			st = conn.prepareStatement(sql);
			st.setInt(1, coddistr);
			st.setInt(2, Integer.parseInt(bairro.get("cod_bairro").toString()));
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				throw new Exception("Erro! Mesmo bairro configurado mais de uma vez. Por favor remova um da relação de bairros/horários.");
			}

			sql = "INSERT INTO distribuidora_bairro_entrega (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (?, ?, ?, ?, ?)";
			int iddistrbair = Utilitario.retornaIdinsert("distribuidora_bairro_entrega", "ID_DISTR_BAIRRO", conn);
			st = conn.prepareStatement(sql);
			st.setInt(1, iddistrbair);
			st.setInt(2, Integer.parseInt(bairro.get("cod_bairro").toString()));
			st.setInt(3, coddistr);
			st.setDouble(4, Double.parseDouble(bairro.get("val_tele").toString()));
			st.setString(5, bairro.get("flag_telebairro").toString());
			st.executeUpdate();

			JSONArray dias = new JSONArray();
			dias = (JSONArray) bairro.get("dias");

			for (int j = 0; j < dias.size(); j++) {
				JSONObject dia = new JSONObject();
				dia = (JSONObject) dias.get(j);

				JSONArray horarios = new JSONArray();
				horarios = (JSONArray) dia.get("horarios");

				for (int k = 0; k < horarios.size(); k++) {

					JSONObject horario = new JSONObject();
					horario = (JSONObject) horarios.get(k);

					if (dia.get("cod_dia") == null || dia.get("cod_dia") == "") {
						throw new Exception("Dados inválidos, entre em contato com o suporte.");
					}

					if (horario.get("HORARIO_INI") == null || horario.get("HORARIO_INI") == "") {
						throw new Exception("Dados inválidos, entre em contato com o suporte.");
					}

					if (horario.get("HORARIO_FIM") == null || horario.get("HORARIO_FIM") == "") {
						throw new Exception("Dados inválidos, entre em contato com o suporte.");
					}

					String idteste = horario.get("id_horario").toString();
					if (horario.get("id_horario") == null || horario.get("id_horario") == "") {
						throw new Exception("Dados inválidos, entre em contato com o suporte.");
					}

					if (horario.get("HORARIO_INI").toString().length() != 5) {
						throw new Exception("O horário " + horario.get("HORARIO_INI") + " é invalido");
					}
					if (horario.get("HORARIO_FIM").toString().length() != 5) {
						throw new Exception("O horário " + horario.get("HORARIO_FIM") + " é invalido");
					}

					Date dataini = Utilitario.testeHora("HH:mm", horario.get("HORARIO_INI").toString(), "");// tempo de entrega da distri
					Date datafim = Utilitario.testeHora("HH:mm", horario.get("HORARIO_FIM").toString(), "");
					;// tempo de entrega do usuario

					for (int l = 0; l < horarios.size(); l++) {

						JSONObject horario2 = new JSONObject();
						horario2 = (JSONObject) horarios.get(l);

						if (!(idteste.equalsIgnoreCase(horario2.get("id_horario").toString()))) {

							Date dataini2 = Utilitario.testeHora("HH:mm", horario.get("HORARIO_INI").toString(), "");// tempo de entrega da distri
							Date datafim2 = Utilitario.testeHora("HH:mm", horario.get("HORARIO_FIM").toString(), "");
							;// tempo de entrega do usuario

							if (dataini.after(dataini2) && dataini.before(datafim2)) {
								throw new Exception("Erro ao salvar. Existem horarios conflitantes.");

							}

							if (datafim.after(dataini2) && datafim.before(datafim2)) {
								throw new Exception("Erro ao salvar. Existem horarios conflitantes.");

							}

						}

					}

					sql = "INSERT INTO distribuidora_horario_dia_entre (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (?, ?, ?, ?, ?, ?);";
					st = conn.prepareStatement(sql);
					st.setLong(1, Utilitario.retornaIdinsertLong("distribuidora_horario_dia_entre", "id_horario", conn));
					st.setInt(2, coddistr);
					st.setInt(3, Integer.parseInt(dia.get("cod_dia").toString()));
					st.setInt(4, iddistrbair);
					st.setString(5, horario.get("HORARIO_INI").toString() + ":00");
					st.setString(6, horario.get("HORARIO_FIM").toString() + ":59");
					st.executeUpdate();

				}
			}

		}

		ret.put("msg", "ok");
		out.print(ret.toJSONString());
	}

	public static void loadBairrosWizard(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONArray ret = new JSONArray();

		String sql = "select * from bairros where cod_cidade =  " + request.getSession(false).getAttribute("cod_cidade").toString() + " order by desc_bairro";

		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			JSONObject dados = new JSONObject();
			dados.put("cod_bairro", rs.getString("cod_bairro"));
			dados.put("desc_bairro", rs.getString("desc_bairro"));
			ret.add(dados);
		}

		out.print(ret.toJSONString());
	}

	public static void salvarConfigsHorariosBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject ret = new JSONObject();

		String bairrosbox = request.getParameter("bairrosbox") == null ? "" : request.getParameter("bairrosbox"); //
		String diassemana = request.getParameter("diassemana") == null ? "" : request.getParameter("diassemana"); //
		String horariosjson = request.getParameter("horariosjson") == null ? "" : request.getParameter("horariosjson"); //
		String tipoopc = request.getParameter("tipoopc") == null ? "" : request.getParameter("tipoopc"); //

		if (!(tipoopc.equalsIgnoreCase("1")) && !(tipoopc.equalsIgnoreCase("3")) && !(tipoopc.equalsIgnoreCase("2"))) {
			throw new Exception("Tipo de operação inválida .");
		}

		String[] bairros = bairrosbox.split(",");

		for (int i = 0; i < bairros.length; i++) {
			if (!Utilitario.isNumeric(bairros[i])) {
				throw new Exception("Bairro inválido.");
			}
		}

		String[] dias = diassemana.split(",");
		for (int i = 0; i < dias.length; i++) {
			if (!Utilitario.isNumeric(dias[i])) {
				throw new Exception("Dia inválido.");
			}
		}

		JSONArray horarios = (JSONArray) new JSONParser().parse(horariosjson);

		PreparedStatement st = null;
		PreparedStatement st2 = null;
		PreparedStatement st3 = null;

		if (tipoopc.equalsIgnoreCase("3")) {

			st = conn.prepareStatement("delete from distribuidora_horario_dia_entre where id_distribuidora = ? ");
			st.setInt(1, coddistr);
			st.executeUpdate();

			st = conn.prepareStatement("delete from distribuidora_bairro_entrega where id_distribuidora = ? ");
			st.setInt(1, coddistr);
			st.executeUpdate();

		} else if (tipoopc.equalsIgnoreCase("2")) {

			String sql = " select * from distribuidora_bairro_entrega where cod_bairro in (" + bairrosbox + ")  and id_distribuidora = " + coddistr;
			st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			String id_distr_bairro_str = "";
			while (rs.next()) {
				id_distr_bairro_str = id_distr_bairro_str + "," + rs.getInt("ID_DISTR_BAIRRO");
			}
			id_distr_bairro_str = id_distr_bairro_str.replaceFirst(",", "");
			if (!id_distr_bairro_str.equalsIgnoreCase("")) {
				st = conn.prepareStatement("delete from distribuidora_horario_dia_entre where ID_DISTR_BAIRRO in (" + id_distr_bairro_str + ")  ");
				st.executeUpdate();

				st = conn.prepareStatement("delete from distribuidora_bairro_entrega where ID_DISTR_BAIRRO in (" + id_distr_bairro_str + ")  ");
				st.executeUpdate();
			}

		}

		// insert
		String sql = "select * from  bairros where cod_bairro in (" + bairrosbox + ") and cod_cidade =  " + request.getSession(false).getAttribute("cod_cidade").toString() + " ";

		st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		ResultSet rs2 = null;
		String msg = "";
		while (rs.next()) {
			int iddistrbair = 0;
			boolean inserirbairro = true;
			if (tipoopc.equalsIgnoreCase("1")) {
				st2 = conn.prepareStatement("    select * from distribuidora_bairro_entrega where id_distribuidora = " + coddistr + " and COD_BAIRRO = " + rs.getString("cod_bairro") + " ;");
				rs2 = st2.executeQuery();
				if (rs2.next()) {
					iddistrbair = rs2.getInt("ID_DISTR_BAIRRO");
					inserirbairro = false;
				}
			}

			if (inserirbairro) {
				iddistrbair = Utilitario.retornaIdinsert("distribuidora_bairro_entrega", "id_distr_bairro", conn);
				st2 = conn.prepareStatement("INSERT INTO distribuidora_bairro_entrega (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (?, ?, ?, ?, ?);");
				st2.setInt(1, iddistrbair);
				st2.setInt(2, rs.getInt("cod_bairro"));
				st2.setInt(3, coddistr);
				st2.setDouble(4, 0.0);
				st2.setString(5, "N");
				st2.executeUpdate();
			}

			for (int i = 0; i < dias.length; i++) {
				int coddia = Integer.parseInt(dias[i]);
				for (int j = 0; j < horarios.size(); j++) {
					JSONObject horario = (JSONObject) horarios.get(j);

					if (horario.get("HORARIO_INI") == null || horario.get("HORARIO_INI") == "") {
						throw new Exception("Dados inválidos, entre em contato com o suporte.");
					}

					if (horario.get("HORARIO_FIM") == null || horario.get("HORARIO_FIM") == "") {
						throw new Exception("Dados inválidos, entre em contato com o suporte.");
					}

					if (horario.get("HORARIO_INI").toString().length() != 5) {
						throw new Exception("O horário " + horario.get("HORARIO_INI") + " é invalido");
					}
					if (horario.get("HORARIO_FIM").toString().length() != 5) {
						throw new Exception("O horário " + horario.get("HORARIO_FIM") + " é invalido");
					}

					Date dataini = Utilitario.testeHora("HH:mm", horario.get("HORARIO_INI").toString(), "");// tempo de entrega da distri
					Date datafim = Utilitario.testeHora("HH:mm", horario.get("HORARIO_FIM").toString(), "");
					;// tempo de entrega do usuario

					// teste de conflito de horario

					StringBuffer varname1 = new StringBuffer();
					varname1.append("select * ");
					varname1.append("from   distribuidora_horario_dia_entre ");
					varname1.append("where  id_distribuidora = " + coddistr + " ");
					varname1.append("       and cod_dia = " + coddia + " ");
					varname1.append("       and id_distr_bairro =  " + iddistrbair + " ");
					varname1.append("       and ( ( horario_ini between ? and ? ");
					varname1.append("                or horario_fim between ? and ? ) ");
					varname1.append("              or ( ? between horario_ini and horario_fim ");
					varname1.append("                    or ? between horario_ini and horario_fim ) );");

					st2 = conn.prepareStatement(varname1.toString());
					st2.setString(1, horario.get("HORARIO_INI").toString() + ":00");
					st2.setString(2, horario.get("HORARIO_FIM").toString() + ":59");
					st2.setString(3, horario.get("HORARIO_INI").toString() + ":00");
					st2.setString(4, horario.get("HORARIO_FIM").toString() + ":59");
					st2.setString(5, horario.get("HORARIO_INI").toString() + ":00");
					st2.setString(6, horario.get("HORARIO_FIM").toString() + ":59");
					rs2 = st2.executeQuery();

					if (rs2.next()) {
						msg = msg + "O horário " + horario.get("HORARIO_INI").toString() + " até as " + horario.get("HORARIO_FIM").toString() + " conflita com horários no Bairro: " + Utilitario.getNomeBairro(conn, 0, iddistrbair) + "   -  Dia: " + Utilitario.getDescDiaSemana(conn, (coddia), false) + " - das " + rs2.getString("HORARIO_INI") + " até " + rs2.getString("HORARIO_FIM") + " \n";
						// throw new Exception("O horário " + horario.get("HORARIO_INI").toString() + " até as " + horario.get("HORARIO_FIM").toString() + " conflita com horários no Bairro: " + Utilitario.getNomeBairro(conn, 0, iddistrbair) + " - Dia: " + Utilitario.getDescDiaSemana(conn, (coddia)) + " - das " + rs2.getString("HORARIO_INI") + " até " + rs2.getString("HORARIO_FIM") );
					}

					sql = "INSERT INTO distribuidora_horario_dia_entre (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (?, ?, ?, ?, ?, ?);";
					st3 = conn.prepareStatement(sql);
					st3.setInt(1, Utilitario.retornaIdinsert("distribuidora_horario_dia_entre", "id_horario", conn));
					st3.setInt(2, coddistr);
					st3.setInt(3, coddia);
					st3.setInt(4, iddistrbair);
					st3.setString(5, horario.get("HORARIO_INI").toString() + ":00");
					st3.setString(6, horario.get("HORARIO_FIM").toString() + ":59");
					st3.executeUpdate();
				}
			}

		}

		if (!msg.equalsIgnoreCase("")) {
			throw new Exception(msg);
		}
		ret.put("msg", "ok");
		out.print(ret.toJSONString());
	}

	public static void limparBairros(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject ret = new JSONObject();

		PreparedStatement st = null;

		st = conn.prepareStatement("delete from distribuidora_horario_dia_entre where id_distribuidora = ? ");
		st.setInt(1, coddistr);
		st.executeUpdate();

		st = conn.prepareStatement("delete from distribuidora_bairro_entrega where id_distribuidora = ? ");
		st.setInt(1, coddistr);
		st.executeUpdate();

		ret.put("msg", "ok");
		out.print(ret.toJSONString());
	}

	public static void salvarConfigsHorariosBairrosNovo(HttpServletRequest request, HttpServletResponse response, Connection conn, int coddistr) throws Exception {
		PrintWriter out = response.getWriter();
		JSONObject ret = new JSONObject();

		String bairrosbox = request.getParameter("bairrosbox") == null ? "" : request.getParameter("bairrosbox"); //
		String horariosjson = request.getParameter("horariosjson") == null ? "" : request.getParameter("horariosjson"); //
		String tipoopc = request.getParameter("tipoopc") == null ? "" : request.getParameter("tipoopc"); //

		if (!(tipoopc.equalsIgnoreCase("1")) && !(tipoopc.equalsIgnoreCase("3")) && !(tipoopc.equalsIgnoreCase("2"))) {
			throw new Exception("Tipo de operação inválida .");
		}
		int[] dias = { 1, 2, 3, 4, 5, 6, 7, 8 };
		String[] bairros = bairrosbox.split(",");

		for (int i = 0; i < bairros.length; i++) {
			if (!Utilitario.isNumeric(bairros[i])) {
				throw new Exception("Bairro inválido.");
			}
		}

		JSONArray horarios = (JSONArray) new JSONParser().parse(horariosjson);

		/*
		 * for (int i = 0; i < horarios.size(); i++) { JSONObject faixa = (JSONObject) horarios.get(i); for (int t = 0; t < dias.length; t++) { System.out.println(horarios.get(i)); if (!Utilitario.isNumeric(faixa.get(i).toString().substring(horarios.get(i).toString().length() - 1, horarios.get(i).toString().length()))) {// pega o ultimo char de horarios_1 throw new Exception("Dia inválido."); } }
		 * 
		 * }
		 */

		PreparedStatement st = null;
		PreparedStatement st2 = null;
		PreparedStatement st3 = null;

		if (tipoopc.equalsIgnoreCase("3")) {

			st = conn.prepareStatement("delete from distribuidora_horario_dia_entre where id_distribuidora = ? ");
			st.setInt(1, coddistr);
			st.executeUpdate();

			st = conn.prepareStatement("delete from distribuidora_bairro_entrega where id_distribuidora = ? ");
			st.setInt(1, coddistr);
			st.executeUpdate();

		} else if (tipoopc.equalsIgnoreCase("2")) {

			String sql = " select * from distribuidora_bairro_entrega where cod_bairro in (" + bairrosbox + ")  and id_distribuidora = " + coddistr;
			st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			String id_distr_bairro_str = "";
			while (rs.next()) {
				id_distr_bairro_str = id_distr_bairro_str + "," + rs.getInt("ID_DISTR_BAIRRO");
			}
			id_distr_bairro_str = id_distr_bairro_str.replaceFirst(",", "");
			if (!id_distr_bairro_str.equalsIgnoreCase("")) {
				st = conn.prepareStatement("delete from distribuidora_horario_dia_entre where ID_DISTR_BAIRRO in (" + id_distr_bairro_str + ")  ");
				st.executeUpdate();

				st = conn.prepareStatement("delete from distribuidora_bairro_entrega where ID_DISTR_BAIRRO in (" + id_distr_bairro_str + ")  ");
				st.executeUpdate();
			}

		}

		// insert
		String sql = "select * from  bairros where cod_bairro in (" + bairrosbox + ") and cod_cidade =  " + request.getSession(false).getAttribute("cod_cidade").toString() + " ";

		st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		ResultSet rs2 = null;
		String msg = "";
		while (rs.next()) {
			int iddistrbair = 0;
			boolean inserirbairro = true;
			if (tipoopc.equalsIgnoreCase("1")) {
				st2 = conn.prepareStatement("    select * from distribuidora_bairro_entrega where id_distribuidora = " + coddistr + " and COD_BAIRRO = " + rs.getString("cod_bairro") + " ;");
				rs2 = st2.executeQuery();
				if (rs2.next()) {
					iddistrbair = rs2.getInt("ID_DISTR_BAIRRO");
					inserirbairro = false;
				}
			}

			if (inserirbairro) {
				iddistrbair = Utilitario.retornaIdinsert("distribuidora_bairro_entrega", "ID_DISTR_BAIRRO", conn);
				st2 = conn.prepareStatement("INSERT INTO distribuidora_bairro_entrega (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (?, ?, ?, ?, ?);");
				st2.setInt(1, iddistrbair);
				st2.setInt(2, rs.getInt("cod_bairro"));
				st2.setInt(3, coddistr);
				st2.setDouble(4, 0.0);
				st2.setString(5, "N");
				st2.executeUpdate();
			}

			for (int i = 0; i < horarios.size(); i++) {
				JSONObject faixa = (JSONObject) horarios.get(i);
				for (int t = 0; t < dias.length; t++) {
					if (faixa.get("HORARIO_" + dias[t]) != null) {
						String horariocompleto = faixa.get("HORARIO_" + dias[t]).toString();
						String[] horariosstr = horariocompleto.split(" - ");

						Date dataini = Utilitario.testeHora("HH:mm", horariosstr[0], "");// tempo de entrega da distri
						Date datafim = Utilitario.testeHora("HH:mm", horariosstr[1], "");
						;// tempo de entrega do usuario

						StringBuffer varname1 = new StringBuffer();
						varname1.append("SELECT * ");
						varname1.append("FROM   distribuidora_horario_dia_entre ");
						varname1.append("WHERE  id_distribuidora = " + coddistr + " ");
						varname1.append("       AND cod_dia = " + dias[t] + " ");
						varname1.append("       AND id_distr_bairro =  " + iddistrbair + " ");
						varname1.append("       AND ( ( horario_ini BETWEEN ? AND ? ");
						varname1.append("                OR horario_fim BETWEEN ? AND ? ) ");
						varname1.append("              OR ( ? BETWEEN horario_ini AND horario_fim ");
						varname1.append("                    OR ? BETWEEN horario_ini AND horario_fim ) );");

						st2 = conn.prepareStatement(varname1.toString());
						st2.setString(1, horariosstr[0] + ":00");
						st2.setString(2, horariosstr[1] + ":59");
						st2.setString(3, horariosstr[0] + ":00");
						st2.setString(4, horariosstr[1] + ":59");
						st2.setString(5, horariosstr[0] + ":00");
						st2.setString(6, horariosstr[1] + ":59");
						rs2 = st2.executeQuery();

						if (rs2.next()) {
							msg = msg + "O horário " + horariosstr[0] + " até as " + horariosstr[1] + " conflita com horários no Bairro: " + Utilitario.getNomeBairro(conn, 0, iddistrbair) + "   -  Dia: " + Utilitario.getDescDiaSemana(conn, (dias[t]), false) + " - das " + rs2.getString("HORARIO_INI") + " até " + rs2.getString("HORARIO_FIM") + " \n";
							// throw new Exception("O horário " + horario.get("HORARIO_INI").toString() + " até as " + horario.get("HORARIO_FIM").toString() + " conflita com horários no Bairro: " + Utilitario.getNomeBairro(conn, 0, iddistrbair) + " - Dia: " + Utilitario.getDescDiaSemana(conn, (coddia)) + " - das " + rs2.getString("HORARIO_INI") + " até " + rs2.getString("HORARIO_FIM") );
						}

						sql = "INSERT INTO distribuidora_horario_dia_entre (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (?, ?, ?, ?, ?, ?);";
						st3 = conn.prepareStatement(sql);
						st3.setInt(1, Utilitario.retornaIdinsert("distribuidora_horario_dia_entre", "id_horario", conn));
						st3.setInt(2, coddistr);
						st3.setInt(3, dias[t]);
						st3.setInt(4, iddistrbair);
						st3.setString(5, horariosstr[0] + ":00");
						st3.setString(6, horariosstr[1] + ":59");
						st3.executeUpdate();

					}
				}

			}

		}

		if (!msg.equalsIgnoreCase("")) {
			throw new Exception(msg);
		}
		ret.put("msg", "ok");
		out.print(ret.toJSONString());
	}

}
