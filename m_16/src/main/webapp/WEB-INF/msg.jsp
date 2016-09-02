<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="utf-8" />
<title>Msg</title>
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

</head>
<%
	String msg = "";
	if (request.getAttribute("msg") != null) {
		msg = request.getAttribute("msg").toString();
	}
	boolean erro = false;
	if (request.getAttribute("erro") != null) {
		erro = Boolean.parseBoolean(request.getAttribute("erro").toString());
	}
%>
<body>

	<div align="center">
		<br> <br> <br>

		<div class="loginBox">
			<div class="container">
				<%
					if (!erro) {
				%>

				<div class="alert alert-success  fade in" role="alert">

					<%=msg%>
				</div>
				<%
					} else {
				%>
				<div class="alert alert-danger  fade in" role="alert">

					<%=msg%>
				</div>
				<%
					}
				%>
			</div>
		</div>
	</div>
</body>
</html>