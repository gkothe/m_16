<%@page import="java.sql.ResultSet"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="com.configs.Conexao"%>
<%@page import="com.funcs.Sys_parametros"%>
<%@page import="java.sql.Connection"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0" name="viewport" />

<title>TragoAqui</title>
<!-- jQuery jQuery v2.2.3  -->
<script src="gentelella-master/vendors/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap -->
<script
	src="gentelella-master/vendors/bootstrap/dist/js/bootstrap.min.js"></script>


<script type="text/javascript"
	src="JavaScript/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript"
	src="JavaScript/jquery-ui-1.10.4.custom/js/jquery.mask.min.js"></script>
<script type="text/javascript"
	src="JavaScript/jquery-ui-1.10.4.custom/js/jquery.blockUI.js"></script>
<script type="text/javascript"
	src="JavaScript/jquery-ui-1.10.4.custom/js/autoNumeric.1.9.22.js"
	charset="utf-8"></script>
<link href="gentelella-master/vendors/bootstrap/dist/css/bootstrap.css"
	rel="stylesheet">
<body class="">
	<%
		Connection conn = null;
		Sys_parametros sys = null;
		try {
			conn = Conexao.getConexao();
			sys = new Sys_parametros(conn);
	%>
	<br>
	<br>
	<br>
	<br>

	<div class="x_content">

		<div style="width: 95%; height: 100%"
			class="container bs-docs-container">

			<div class="row rowpad">
				<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 filtros"
					align="right">
					<label for=""> Senha</label>
				</div>
				<div class="col-xs-3 col-sm-3 col-md-4 col-lg-4">

					<input id="desc_senha" type="password" autocomplete="off">
				</div>
			</div>
			<Br>

			<div class="row rowpad">


				<%
					StringBuffer sql = new StringBuffer();
						sql.append("select max(id_prod)+1 as id from produtos");

						PreparedStatement st = conn.prepareStatement(sql.toString());
						ResultSet rs = st.executeQuery();
						if (rs.next()) {
						}
				%>

				<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 filtros"
					align="right">
					<label for=""> Produto</label>
				</div>
				<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">

					<div class="input-group" style="width: 100%">

						<input id="id_produto" placeholder="Cód." style="width: 25%"
							class="form-control" type="text" value="<%=rs.getInt("id")%>"
							name="id_produto"> <input id="desc_abreviado"
							style="width: 75%" class="form-control" placeholder="Nome"
							type="text" maxlength="" value="" name="desc_abreviado">
					</div>
				</div>


			</div>

			<br>


			<div class="row rowpad">

				<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 ">
					<label for=""> QTD Imagem:</label>
				</div>
				<div class="col-xs-3 col-sm-3 col-md-4 col-lg-4">

					<div class="input-group" style="width: 100%">

						<input id="qtd_image">
					</div>
				</div>
			</div>

			<div class="row rowpad">
				<div class="col-xs-2 col-sm-2 col-md-3 col-lg-3" align="left">
					<label for="">Descrição</label>
				</div>
				<div class="col-xs-10 col-sm-10 col-md-10 col-lg-12 ">

					<textarea style="width: 100%" rows="10" cols="" id="desc_prod">  </textarea>

				</div>


			</div>
			<div class="row rowpad">
				<div class="col-xs-2 col-sm-2 col-md-3 col-lg-3" align="left">
					<label for="">Keywords</label>
				</div>
				<div class="col-xs-10 col-sm-10 col-md-10 col-lg-12 ">

					<textarea style="width: 100%" rows="2" cols="" id="key_words">  </textarea>

				</div>



			</div>

			<div class="row rowpad">
				<%
					sql = new StringBuffer();
						sql.append("SELECT * ");
						sql.append("FROM   categoria ");
						sql.append("  order by desc_categoria ");
						st = conn.prepareStatement(sql.toString());
						JSONObject obj = new JSONObject();
						rs = st.executeQuery();
				%>
				<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 filtros"
					align="right">
					<label for=""> Categoria</label>
				</div>
				<div class="col-xs-4 col-sm-4 col-md-4	 col-lg-4">
					<select id="id_categoria" class="form-control" style="width: 100%"
						name="id_categoria">
						<option value="">Outras</option>
						<%
							while (rs.next()) {
						%>
						<option value=<%=rs.getInt("id_categoria")%>><%=rs.getString("desc_categoria")%></option>
						<%
							}
						%>
					</select>

				</div>


				<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 filtros"
					align="right">
					<label for=""> Marca</label>
				</div>

				<%
					sql = new StringBuffer();
						sql.append("SELECT * ");
						sql.append("FROM   marca ");
						sql.append("  order by desc_marca ");
						st = conn.prepareStatement(sql.toString());
						obj = new JSONObject();
						rs = st.executeQuery();
				%>
				<div class="col-xs-5 col-sm-5 col-md-5	 col-lg-4">
					<select id="id_marca" class="form-control" style="width: 100%">
						<option value="">Outras</option>
						<%
							while (rs.next()) {
						%>
						<option value=<%=rs.getInt("id_marca")%>><%=rs.getString("desc_marca")%></option>
						<%
							}
						%>

					</select>

				</div>

				<br> <br> <br>
				<div class="row rowpad">
					<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 filtros"
						align="right">
						<button class="btn btn-primary  " type="button" id="btn_produto">Adicionar
							Produto</button>
					</div>

					<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 filtros"
						align="right"></div>
					<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 filtros"
						align="right">
						<button class="btn btn-primary  " type="button"
							id="btn_atuproduto">Atuailzar Produto</button>
					</div>
				</div>
				<br> <br>

				-----------------------------------------------------------------------------------------------------------------------------------------


				<div class="row rowpad">
					<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 filtros"
						align="right">
						<label for=""> Inserir Categoria</label>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">

						<input id="desc_categoria" style="width: 75%" class="form-control"
							placeholder="" type="text" maxlength="" value="">
					</div>
					<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 filtros"
						align="right">
						<button class="btn btn-primary  " type="button" id="btn_categ">Adicionar
							Categoria</button>
					</div>



				</div>


				<br> <br>

				-----------------------------------------------------------------------------------------------------------------------------------------


				<div class="row rowpad">
					<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 filtros"
						align="right">
						<label for=""> Inserir Marca</label>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">

						<input id="desc_marca" style="width: 75%" class="form-control"
							placeholder="" type="text" maxlength="" value="" name="">
					</div>

					<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 filtros"
						align="right">
						<button class="btn btn-primary  " type="button" id="btn_marca">Adicionar
							Marca</button>
					</div>
				</div>
				 <Br>
			</div>


		</div>
</body>

<%
	conn.close();
	} catch (Exception e) {

		try {
			conn.close();
		} catch (Exception e2) {
		}

	}
%>

<script>
	function inserirProduto() {

		var desc_senha = $("#desc_senha").val();
		var id_produto = $("#id_produto").val();
		var desc_abreviado = $("#desc_abreviado").val();
		var desc_prod = $("#desc_prod").val();
		var key_words = $("#key_words").val();
		var id_categoria = $("#id_categoria").val();
		var id_marca = $("#id_marca").val();
		var qtd_image = $("#qtd_image").val();

		$.blockUI({
			message : 'salvando...'
		});

		$.ajax({
			type : "POST",
			url : "admin?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'inserirProduto',
				desc_senha : desc_senha,
				id_produto : id_produto,
				desc_abreviado : desc_abreviado,
				desc_prod : desc_prod,
				key_words : key_words,
				id_categoria : id_categoria,
				id_marca : id_marca,
				qtd_image : qtd_image

			},
			success : function(data) {

				if (data.erro) {

					alert(data.erro);
				} else if (data.msg == "ok") {
					alert("produto inserido");

					$("#id_produto").val("");
					$("#desc_abreviado").val("");
					$("#desc_prod").val("");
					$("#key_words").val("");
					$("#id_categoria").val("");
					$("#id_marca").val("");
					$("#qtd_image").val("");

				}

				$.unblockUI();
			},
			error : function(msg) {
				$.unblockUI();
				alert(msg);

			}
		});

	}

	function atualizarProd() {

		var desc_senha = $("#desc_senha").val();
		var id_produto = $("#id_produto").val();
		var desc_abreviado = $("#desc_abreviado").val();
		var desc_prod = $("#desc_prod").val();
		var key_words = $("#key_words").val();
		var id_categoria = $("#id_categoria").val();
		var id_marca = $("#id_marca").val();
		var qtd_image = $("#qtd_image").val();

		$.blockUI({
			message : 'salvando...'
		});

		$.ajax({
			type : "POST",
			url : "admin?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'atualizarProd',
				desc_senha : desc_senha,
				id_produto : id_produto,
				desc_abreviado : desc_abreviado,
				desc_prod : desc_prod,
				key_words : key_words,
				id_categoria : id_categoria,
				id_marca : id_marca,
				qtd_image : qtd_image

			},
			success : function(data) {

				if (data.erro) {

					alert(data.erro);
				} else if (data.msg == "ok") {
					alert("produto inserido");

					$("#id_produto").val("");
					$("#desc_abreviado").val("");
					$("#desc_prod").val("");
					$("#key_words").val("");
					$("#id_categoria").val("");
					$("#id_marca").val("");
					$("#qtd_image").val("");

				}

				$.unblockUI();
			},
			error : function(msg) {
				$.unblockUI();
				alert(msg);

			}
		});

	}

	function getProd() {

		var id_produto = $("#id_produto").val();
		var desc_senha = $("#desc_senha").val();

		$.ajax({
			type : "POST",
			url : "admin?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'getProd',
				desc_senha : desc_senha,
				id_produto : id_produto

			},
			success : function(data) {

				$("#id_produto").val(data.id_prod);
				$("#desc_abreviado").val(data.desc_abreviado);
				$("#desc_prod").val(data.desc_prod);
				$("#key_words").val(data.desc_key_words);
				$("#id_categoria").val(data.id_categoria);
				$("#id_marca").val(data.id_marca);
				$("#qtd_image").val(data.qtd_images);

				
			},
			error : function(msg) {
				$.unblockUI();
				alert(msg);

			}
		});

	}

	function inserirCateg() {

		var desc_senha = $("#desc_senha").val();
		var desc_categoria = $("#desc_categoria").val();

		$.blockUI({
			message : 'salvando...'
		});

		$.ajax({
			type : "POST",
			url : "admin?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'inserirCategoria',
				desc_senha : desc_senha,
				desc_categoria : desc_categoria

			},
			success : function(data) {

				if (data.erro) {

					alert(data.erro);
				} else if (data.msg == "ok") {
					alert("CATEGORIA inseridA");
					$("#desc_categoria").val("");
					location.reload()

				}

				$.unblockUI();
			},
			error : function(msg) {
				$.unblockUI();
				alert(msg);

			}
		});

	}

	function inserirMarca() {

		var desc_senha = $("#desc_senha").val();
		var desc_marca = $("#desc_marca").val();

		$.blockUI({
			message : 'salvando...'
		});

		$.ajax({
			type : "POST",
			url : "admin?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'inserirMarca',
				desc_senha : desc_senha,
				desc_marca : desc_marca

			},
			success : function(data) {
				console.log(data);

				if (data.erro) {

					alert(data.erro);
				} else if (data.msg == "ok") {
					alert("Marca inserida");
					$("#desc_marca").val("");

					location.reload()

				}

				$.unblockUI();
			},
			error : function(msg) {
				$.unblockUI();
				alert(msg);

			}
		});

	}

	$(document).ready(function() {

		
		$("#id_produto").blur(function() {
			getProd();
		});
		
		
		$("#btn_produto").click(function() {
			inserirProduto();
		});

		$("#btn_atuproduto").click(function() {
			atualizarProd();
		});

		$("#btn_categ").click(function() {
			inserirCateg();
		});

		$("#btn_marca").click(function() {
			inserirMarca();
		});

	});
</script>




</html>