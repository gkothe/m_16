<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.mercadopago.MP"%>
<%@page import="org.codehaus.jettison.json.JSONObject"%>

<%
	// hot MP mp = new MP("3928083096731492", "GQiU4JBINN66ZU7m3IL731WUSdIFALwz");

MP mp = new MP("3928083096731492", "GQiU4JBINN66ZU7m3IL731WUSdIFALwz");

	String preferenceData = "{'items':"+
		"[{"+
			"'title':'Multicolor kite',"+
			"'quantity':1,"+
			"'currency_id':'BRL',"+ // Available currencies at: https://api.mercadopago.com/currencies
			"'unit_price':1.0"+
		"}]"+
	"}";

	JSONObject preference = mp.createPreference(preferenceData);
	

	String sandboxInitPoint = preference.getJSONObject("response").getString("sandbox_init_point");
	

	//String initPoint = preference.getJSONObject("response").getString("init_point");
%>

<!DOCTYPE html>
<html>
	<head>
		<title>Pay</title>
	</head>
	<body>
<%-- 		<a href="<%= initPoint %>">Pay</a> --%>
		<a href="<%= sandboxInitPoint %>">Paysand</a>
	</body>
</html>