<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0" name="viewport" />

<title>Nameless! |</title>
<!-- Bootstrap -->
<link href="gentelella-master/vendors/bootstrap/dist/css/bootstrap.css"
	rel="stylesheet">
<!-- Font Awesome -->
<link
	href="gentelella-master/vendors/font-awesome/css/font-awesome.min.css"
	rel="stylesheet">
<!-- iCheck -->
<link href="gentelella-master/vendors/iCheck/skins/flat/green.css"
	rel="stylesheet">
<link href="gentelella-master/vendors/select2/dist/css/select2.min.css"
	rel="stylesheet">
<!-- bootstrap-progressbar -->
<link
	href="gentelella-master/vendors/bootstrap-progressbar/css/bootstrap-progressbar-3.3.4.min.css"
	rel="stylesheet">
<link rel="stylesheet" type="text/css"
	href="css/ui-lightness/jquery-ui-1.10.4.custom.css" />
<link rel="stylesheet" type="text/css"
	href="JavaScript/bootstrap-3.3.6-dist/css/bootstrap-table.css">
<link rel="stylesheet" type="text/css"
	href="JavaScript/bootstrap-3.3.6-dist/css/bootstrap-timepicker.css">
<link rel="stylesheet" type="text/css" href="css/autocomplete.css" />
<link rel="stylesheet" type="text/css"
	href="JavaScript/bootstrap_date1.4.0/css/bootstrap-datepicker.css" />
<link rel="stylesheet" type="text/css"
	href="JavaScript/bootstrap-calendar-master/css/calendar.css" />
<!-- Custom Theme Style -->
<link href="gentelella-master/production/css/custom.css"
	rel="stylesheet">
<style type="text/css">
.filtros {
	padding-top: 6px;
	padding-right: 0px;
	padding-left: 0px;
}

.rowpad {
	padding-top: 5px;
}

p {
	max-width: 600px;
}

.btn_tabela {
	margin-bottom: 0px;
	height: 20px;
	padding: 2px 6px 20px;
	width: 35px
}

input:focus {
	outline: none !important;
	border-color: #9ecaed !important;
	box-shadow: 0 0 10px #9ecaed !important;
}
</style>


</head>

<body class="nav-md">
	<div class="container body">
		<div class="main_container" id="main_cont" style="">
			<div class="col-md-3 left_col">
				<div class="left_col scroll-view">
					<div class="navbar nav_title" style="border: 0;">
						<a class="site_title"><image style="width: 25px"
								src="images/beer_PNG2388.png"></image> <span>ChamaTrago!</span></a>
					</div>

					<div class="clearfix"></div>

					<!-- menu profile quick info -->
					<div class="profile">
						<div class="profile_pic"
							style="padding-left: 15px; padding-top: 15px;">
							<img id="lbl_logomenu" src="" style="width: 65px" alt="...">
						</div>
						<div class="profile_info">
							<span>Distribuidora,</span>
							<h2>
								<label id="lbl_descfanta"></label>
							</h2>
						</div>
					</div>
					<!-- /menu profile quick info -->

					<br /> <br> <br>
					<!-- sidebar menu -->
					<div id="sidebar-menu"
						class="main_menu_side hidden-print main_menu">
						<div class="menu_section">
							<h3>Menu</h3>

							<ul class="nav side-menu">
								<li><a class="clickmenu2"><i
										class="fa fa-home clickmenu2"></i> Pedidos <span
										class="fa fa-chevron-down clickmenu"></span></a>
									<ul class="nav child_menu">
										<li><a linkmenu="listaped"
											onclick="trocaPag(this,'N');" class="clickmenu">Abertos</a></li>
										<li><a linkmenu="listapedfechado"
											onclick="trocaPag(this,'N');" class="clickmenu">Histórico</a></li>
									</ul></li>
								<li><a class="clickmenu2"><i
										class="fa fa-gear clickmenu2"></i> Parâmetros <span
										class="fa fa-chevron-down clickmenu"></span></a>
									<ul class="nav child_menu">
										<li class=''><a linkmenu="listaprod"
											onclick="trocaPag(this,'N');" class="clickmenu">Produtos</a></li>
										<li><a linkmenu="listaconfigemp"
											onclick="trocaPag(this,'N');" class="clickmenu">Configurações
												da empresa</a></li>
									</ul></li>
								<li><a class="clickmenu2"><i
										class="fa fa-edit clickmenu2"></i> Relatórios <span
										class="fa fa-chevron-down clickmenu"></span></a>
									<ul class="nav child_menu">
										<li><a href="form.html">The void is here</a></li>
									</ul></li>

							</ul>
						</div>



					</div>
					<!-- /sidebar menu -->


				</div>
			</div>

			<!-- top navigation -->
			<div class="top_nav">

				<div class="nav_menu">

					<nav class="" role="navigation">
						<div class="nav toggle">
							<a id="menu_toggle""><i class="fa fa-bars"></i></a>

						</div>

						<ul class="nav navbar-nav navbar-right">
							<li class=""><a href="javascript:;"
								class="user-profile dropdown-toggle" data-toggle="dropdown"
								aria-expanded="false"> Opções <span
									class=" fa fa-angle-down"></span>
							</a>
								<ul class="dropdown-menu dropdown-usermenu pull-right">
									<li><a href="javascript:showHelp();"><i
											class="fa fa-question pull-right"></i>Ajuda/Informações
											gerais</a></li>
									<li><a href="home?ac=logout"><i
											class="fa fa-sign-out pull-right"></i> Sair</a></li>
								</ul></li>

							<li role="presentation" class="dropdown"><a
								href="javascript:;" class="dropdown-toggle info-number"
								data-toggle="dropdown" aria-expanded="false"> <i
									class="fa fa-envelope-o"></i> <span class="badge bg-red"
									id="h_qtd_pedz"></span>
							</a>
								<ul id="menu_notification"
									class="dropdown-menu list-unstyled msg_list" role="menu">


								</ul></li>

							<li style="margin-top: 15px;">

								<div style="cursor: pointer; display: none;" id="msg_holder">
									<a onclick="trocaPag(this,'N');"
										linkmenu="listaped"><span
										class="label label-danger aviso_pedido" id="msg_nao_vizu"
										style="font-size: 195%;">Você tem pedidos não
											respondidos! </span></a>

								</div>

							</li>

						</ul>
					</nav>
				</div>

			</div>
			<!-- /top navigation -->


			<!-- page content -->
			<div class="right_col"
				style="max-height: 100% !important; padding-left: 10px; padding-right: 10px;"
				role="main">

				<div class="x_panel" id="mainpage"
					style="overflow: auto; padding-left: 5px; padding-right: 5px;"></div>

			</div>
			<!-- /page content -->

			<!-- footer content -->
			<footer style="padding-bottom: 25px;">
				<br>
			</footer>
			<!-- /footer content -->



			<div class="modal fade bs-example-modal-lg" tabindex="-5"
				role="dialog" id="modal_ajuda" aria-hidden="true">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">

						<div class="modal-header" align="center">
							<button type="button" class="close" data-dismiss="modal">
								<span aria-hidden="true">×</span>
							</button>
							<h4 class="modal-title" id="">
								<label style="font-size: 20px; color: black" id="">Ajuda/Informações
									do ChamaTrago!</label>
							</h4>
						</div>
						<div class="modal-body" style="max-height: 800px; overflow: auto;">

							<div class="row">

								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 ">
									<strong>Bem-vindo ao ChamaTrago!</strong><br> Para
									visualizar pedidos abertos vá no menu no lado esquerdo da tela,
									clique em "Pedidos" e em seguida "Abertos". <br> Para
									visualizar o histórico de pedidos vá no menu no lado esquerdo
									da tela, clique em "Pedidos" e em seguida "Histórico". <br>
									Para ativar,desativar e mudar o preço de produtos, vá no menu
									no lado esquerdo da tela, clique em "Parâmetros" e em seguida
									"Produtos". <br> Para modificar dados de cadastro,
									funcionamento, bairros e horarios da empresa, vá no menu no
									lado esquerdo da tela, clique em "Parâmetros" e em seguida
									"Configurações da empresa". <br>
								</div>


							</div>
							<br>
							<div class="row">

								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 ">
									Para maiores informações entre em contato consoco pelo email
									######@$$$$.com ou pelo telefone (051)0000-11111.</div>


							</div>


						</div>
						<div class="modal-footer">

							<div class="row">

								<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10 "></div>


								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 " align="right">
									<button type="button" class="btn btn-primary"
										data-dismiss="modal">Fechar</button>
								</div>

							</div>
						</div>
					</div>
				</div>
			</div>


			<div class="modal fade bs-example-modal-lg" tabindex="-5"
				role="dialog" id="modal_pedido" aria-hidden="true">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">

						<div class="modal-header" align="center">
							<button type="button" class="close" data-dismiss="modal">
								<span aria-hidden="true">×</span>
							</button>
							<h4 class="modal-title" id="">
								<label style="font-size: 40px; color: green" id="m_lbl_titulo"></label>

							</h4>
						</div>
						<div class="modal-body" style="max-height: 800px">



							<div class="row">

								<div class="col-xs-7 col-sm-7 col-md-7 col-lg-7" align="left">

									<table style="font-size: 120%">

										<tr>
											<td style="padding-right: 5px;"><label for="">Bairro:
											</label></td>
											<td><label id="m_desc_bairro"> </label></td>

										</tr>

									</table>

								</div>



								<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5" align="right">

									<table style="font-size: 120%">
										<tr>
											<td style="padding-right: 5px;"><label for="">Data
													do pedido: </label></td>
											<td><label id="m_data_pedido"></label></td>

										</tr>

									</table>

								</div>

							</div>

							<div class="row">

								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" align="">


									<table id="m_table_produtos" data-toggle="table"
										class="table_boots" data-height="250" data-locale="pt-BR"
										data-pagination="true" data-page-list="[4,10,25,50,100]"
										data-page-size="4" data-locale="pt-BR"
										data-pagination-first-text="Primeiro"
										data-pagination-pre-text="Anterior"
										data-pagination-next-text="Próximo"
										data-pagination-last-text="Último">
										<thead class="header">
											<tr>
												<th data-field="ID_PROD" data-width="7%" data-align="center">Cod.</th>
												<th data-field="DESC_PROD" data-width="30%"
													data-align="center">Produto</th>
												<th data-field="QTD_PROD" data-width="20%"
													data-align="center">Quantidade</th>
												<th data-formatter="valorFormater" data-field="VAL_UNIT"
													data-width="15%" data-align="right">Valor Unit.</th>
												<th data-formatter="valorFormater" data-field="VAL_TOTAL"
													data-width="15%" data-align="right">Valor Total</th>

											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>


								</div>

							</div>
							<br>


							<div class="row">

								<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">


									<table>
										<tr>
											<td style="padding-right: 5px;"><label for="">Valor
													total do produtos: </label></td>
											<td><label id=""> </label><label id="m_total_produtos"></label></td>

										</tr>
									</table>


								</div>

								<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 m_enviado"
									align="left">

									<table>
										<tr>
											<td style="padding-right: 5px;"><label for="">Data
													de resposta: </label></td>
											<td><label id="m_data_resposta"></label></td>

										</tr>

									</table>

								</div>

							</div>

							<div class="row">

								<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" align="">

									<table>
										<tr>
											<td style="padding-right: 5px;"><label for="">Valor
													de tele-entrega: </label></td>
											<td><label id=""> </label><label id="m_total_tele"></label></td>

										</tr>

									</table>

								</div>

								<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 m_enviado"
									align="left">

									<table>
										<tr>
											<td style="padding-right: 5px;"><label for="">Tempo
													estimado para entrega: </label></td>
											<td><label id="m_tempo_entrega"></label></td>

										</tr>

									</table>

								</div>

							</div>



							<div class="row">
								<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8" align="">
									<table>
										<tr>
											<td style="padding-right: 5px;"><label for="">Total
													pedido: </label></td>
											<td><label id=""> </label><label id="m_total_pedido"></label></td>


										</tr>

									</table>

								</div>
							</div>


							<DIV class="m_enviado">

								<br>
								<div class="row ">
									<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 " align="">
										<table style="font-size: 125%; width: 100%" class="well">
											<tr>
												<td style="padding-right: 5px; padding-left: 5px;"
													colspan="100%"><label> Informações do
														comprador. </label></td>

											</tr>
											<tr>
												<td style="padding-right: 5px; padding-left: 5px;"><label>
														Nome: </label> &nbsp; <label id="envio_desc_nome"> </label></td>
												<td style="padding-right: 5px; padding-left: 5px;"><label>
														Telefone: </label> &nbsp; <label id="envio_desc_telefone">
												</label></td>
											</tr>

											<tr>
												<td style="padding-right: 5px; padding-left: 5px;"><label>Endereço:</label>
													&nbsp; <label id="envio_desc_endereco"></label></td>
												<td style="padding-right: 5px; padding-left: 5px;"><label>Bairro:</label>
													&nbsp;<label id="envio_desc_bairro"></label></td>
											</tr>

										</table>

									</div>
								</div>

							</div>


							<DIV ID="m_aberto">


								<div class="row">

									<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3" align="">
										<input type="hidden" id="m_id_pedido">
									</div>
									<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1" align=""></div>
									<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2" align="">

										<input type="radio" name="flag_aceita_recusa" value="A">
										<LABEL id="lbl_aceitar" style="font-size: 150%; color: green">Confirmar</LABEL>


									</div>

									<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2" align="">

										<input type="radio" name="flag_aceita_recusa" value="R">
										<LABEL id="lbl_recusar" style="font-size: 150%; color: red">Recusar</LABEL>

									</div>
								</div>
								<div class="row" id="m_tempo_entrega_box"
									style="margin-left: 0px; margin-right: 0px;">

									<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 well"
										align="">

										<table>
											<tr>
												<td style="padding-right: 5px;"><label for="">Tempo
														estimado para entrega: &nbsp;</label></td>
												<td><input id="m_hora_entrega" style="width: 50px"
													class="form-control" type="text" maxlength="2" value=""
													name="m_hora_entrega"></td>
												<td style="padding-right: 5px;">&nbsp;<label for="">Hora(s)</label></td>
												<td><input id="m_minutos_entrega" style="width: 50px"
													class="form-control" type="text" maxlength="2" value=""
													name="m_minutos_entrega"></td>
												<td style="padding-right: 5px;">&nbsp;<label for="">Minutos(s)</label></td>
											</tr>

										</table>


									</div>
								</div>

								<div class="row" id="m_motivos_recusa_box"
									style="margin-left: 0px; margin-right: 0px;">

									<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 well"
										align="">

										<label>Motivo(s) de recusa do pedido</label>
										<!-- Aqui carrega os motivo com checkbxo qdo carrega a tela -->
										<table id="desc_motivos">


										</table>

									</div>
								</div>
							</div>


							<div class="row" id="m_resposta_motivos"
								style="margin-left: 0px; margin-right: 0px;">

								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 well"
									align="">
									<!-- Aqui carrega descrição dos motivo quando abre o pedido recusado-->
									<label>Motivo(s) de recusa do pedido</label>

									<table id="desc_motivos2" style="">


									</table>

								</div>
							</div>




						</div>
						<div class="modal-footer">

							<div class="row"">
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 " align="left">
									<button type="button" class="btn btn-primary"
										data-dismiss="modal">Fechar</button>
								</div>

								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 "></div>


								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 " align="right">
									<div id="div_responder">
										<button type="button" id="m_responder" class="btn btn-primary">Responder</button>
									</div>
									<div id="div_finalizar">
										<button type="button" id="m_finalizar" class="btn btn-primary">Finalizar(Ok)</button>
									</div>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>

	<!-- jQuery jQuery v2.2.3  -->
	<script src="gentelella-master/vendors/jquery/dist/jquery.min.js"></script>
	<!-- Bootstrap -->
	<script
		src="gentelella-master/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
	<!-- Custom Theme Scripts  tem que ter-->
	<script src="gentelella-master/production/js/custom.js"></script>

	<script type="text/javascript"
		src="JavaScript/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script>
	<script type="text/javascript"
		src="JavaScript/jquery-ui-1.10.4.custom/js/jquery.mask.min.js"></script>
	<script type="text/javascript"
		src="JavaScript/jquery-ui-1.10.4.custom/js/autoNumeric.1.9.22.js"
		charset="utf-8"></script>
	<script type="text/javascript"
		src="JavaScript/jquery-ui-1.10.4.custom/js/jquery.blockUI.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-3.3.6-dist/js/jquery.bootstrap-growl.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-3.3.6-dist/js/bootstrap-timepicker.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap_date1.4.0/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap_date1.4.0/locales/bootstrap-datepicker.pt-BR.min.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-calendar-master/components/underscore/underscore-min.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-calendar-master/js/calendar.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-calendar-master/js/language/pt-BR.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/dist/bootstrap-table.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/dist/locale/bootstrap-table-pt-BR.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/dist/extensions/cookie/bootstrap-table-cookie.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/export/tableExport.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/export/libs/FileSaver/FileSaver.min.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/export/libs/html2canvas/html2canvas.min.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/export/libs/jsPDF/jspdf.min.js"></script>
	<script type="text/javascript"
		src="JavaScript/bootstrap-table-master/export/libs/jsPDF-AutoTable/jspdf.plugin.autotable.js"></script>


</body>


<script>
	var url = "";
	var php = "";
	var menu = "";

	$(document).ready(function() {
<%boolean link = false;
			if (request.getParameter("link") != null && !(request.getParameter("link").equalsIgnoreCase(""))) {%>
	url = '<%=request.getParameter("link")%>';
	jsp = '<%=request.getParameter("jsp")%>';
	menu ='<%=request.getParameter("m")%>';
	
<%} else {%>
		url = "listaped"
		jsp = "N"
		menu = "m"
<%}%>
//	$('#mainpage').load('home?ac=listaped');

	});
</script>


<script type="text/javascript" src="JavaScript/menu_index.js"></script>

</html>