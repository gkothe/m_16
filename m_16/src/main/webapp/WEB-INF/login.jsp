<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="utf-8" />
<title>Login</title>
<meta content="width=device-width, initial-scale=1.0" name="viewport" />
<title>ChamaTrago!</title>

<script src="gentelella-master/vendors/jquery/dist/jquery.min.js"></script>
<script
	src="gentelella-master/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="JavaScript/index.js"></script>
<script type="text/javascript"
	src="JavaScript/jquery-ui-1.10.4.custom/js/jquery.blockUI.js"></script>
<link href="gentelella-master/vendors/bootstrap/dist/css/bootstrap.css"
	rel="stylesheet">





<style type="text/css">
body {
	background: url(images/background.jpg) no-repeat center center fixed;
	-webkit-background-size: cover;
	-moz-background-size: cover;
	-o-background-size: cover;
	background-size: cover;
}
</style>
</head>
<%
String error ="";
if(request.getAttribute("errologin")!=null){
	error = request.getAttribute("errologin").toString();
}
	
%>
<body>
	<div align="center">
		<div class="loginBox">
			<form action="" method="post">
				<br> <br> <br>
				<div class="container">
					<input type="hidden" name="acao" id="acao" value="log">
					<div class="row">
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12"
							align="center">
							<div class="panel panel-default" style="width: 400px">

								<div class="panel-heading">
									<h2 class="form-signin-heading">ChamaTrago!</h2>
								</div>
								<div class="panel-body">

									<label for="" class="">Usuário</label> <input id="username"
										class="form-control" placeholder="Usuário" name="username"
										required autofocus> <br> <label for="" class="">Senha</label>
									<label for="inputPassword" class="sr-only">Password</label> <input
										type="password" name="password" id="password"
										class="form-control" placeholder="Senha" required> <br>
									<button class="btn btn-lg btn-primary btn-block" type="submit"
										name="submit">Entrar</button>
									<br>
									<%
										if (error != null && error != "") {
									%>
									<div class="alert alert-danger alert-dismissible fade in"
										role="alert">
										<button type="button" class="close" data-dismiss="alert"
											aria-label="Close">
											<span aria-hidden="true">×</span>
										</button>
										<%=error%>
									</div>
									<%
										}
									%>

									<br> <label for="" class="">Esqueceu sua
										senha\usuário? Clique abaixo.</label>
									<button class="btn btn-lg btn-primary btn-block" type="button"
										onclick="showTrocaEmail();">Recuperar senha\usuário.</button>

								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- /container -->


			</form>
		</div>

	</div>


	<div class="modal fade bs-example-modal-lg" tabindex="-50"
		role="dialog" id="modal_ajuda" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header" align="center">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="">
						<label style="font-size: 20px; color: black">Recuperar
							senha\usuário. </label>
					</h4>
				</div>
				<div class="modal-body" style="max-height: 800px; overflow: auto;">
					<div class="row">
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 ">

							<div class="form-group">
								<label for="desc_mail">Preencha o seu e-mail cadastrado
									abaixo:</label> <input type="text" 
									class="form-control warn-change" id="r_desc_mail" />
							</div>

						</div>
					</div>
					<div class="modal-footer">
						<div class="row">
							<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 " align="left">
								<button type="button" class="btn btn-primary"
									data-dismiss="modal">Fechar</button>
							</div>

							<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8 "></div>
							<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 " align="right">
								<button type="button" id="btn_envia_email"
									class="btn btn-primary">Enviar</button>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</body>
</html>