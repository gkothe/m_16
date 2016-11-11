//@ sourceURL=rel_pedido.js
$(document).ready(function() {

	$("#btn_filtrar_historico").click(function() {
		gerarRel() 
	});

	$("#btn_filtros_limpar").click(function() {

		$("#rel_data_ini").val("");
		$("#rel_data_fim").val("");
		$("#cod_bairro").val("");
		$("#flag_pagamento").val("");
		$("#flag_situacao").val("");
		$("#flag_servico").val("");
		$("#chk_infocliente").val("");
		$("#chk_prods").val("");

	});

	$('.data').datepicker({
		format : 'dd/mm/yyyy',
		language : 'pt-BR',
		todayHighlight : true,
		daysOfWeekHighlighted : "0,6",
	});

	$('.hora').timepicker({
		minuteStep : 1,
		showSeconds : false,
		showMeridian : false,
		defaultTime : false
	});

	trazData();
	carregaBairros();

});

function carregaBairros() {

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'carregaBairros'

		},
		success : function(data) {

			var html = "<option value=''  > Todos  </option>  ";

			for (t = 0; t < data.length; t++) {

				html = html + "<option value='" + data[t].cod_bairro + "'  > " + data[t].desc_bairro + "  </option>  ";

			}

			$("#cod_bairro").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function gerarRel() {

	var dataini = $("#rel_data_ini").val();
	var datafim = $("#rel_data_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var flag_situacao = $("#flag_situacao").val();
	var flag_pagamento = $("#flag_pagamento").val();
	var flag_opc = $("#flag_opc").val();
	var flag_servico = $("#flag_servico").val();

	var erro = false;
//	if (dataini == "" || datafim == "") {
//		erro = true;
//	}
	
	var filtros  = "";
	if(dataini !=""){
		filtros = filtros+ "&dataini=" + dataini;
	}
	
	if(datafim !=""){
		filtros = filtros+ "&datafim=" + datafim;
	}
	
	if(cod_bairro !=""){
		filtros = filtros+ "&cod_bairro=" + cod_bairro;
	}
	
	if(flag_situacao !=""){
		filtros = filtros+ "&flag_situacao=" + flag_situacao;
	}
	
	if(flag_pagamento !=""){
		filtros = filtros+ "&flag_pagamento=" + flag_pagamento;
	}
	
	if(flag_servico !=""){
		filtros = filtros+ "&flag_servico=" + flag_servico;
	}
	
	if(flag_opc !=""){
		filtros = filtros+ "&flag_opc=" + flag_opc;
	}
	
	
	if (!erro) {
		var nome = "Relátorio de pedidos"
		var w = window.open("home/" + nome + ".pdf?ac=rel_pedidospdf"+filtros, '_blank');
	}
}

function trazData() {

	var date = new Date(), y = date.getFullYear(), m = date.getMonth();
	var firstDay = new Date(y, m, 1);
	var lastDay = new Date(y, m + 1, 0);

	$("#rel_data_ini").val(firstDay.getDate() + '/' + (firstDay.getMonth() + 1) + '/' + firstDay.getFullYear());
	$("#rel_data_fim").val(lastDay.getDate() + '/' + (lastDay.getMonth() + 1) + '/' + lastDay.getFullYear());

}
