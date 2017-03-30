//@ sourceURL=lista_pedidos_aberto.js
var rstimer = false;
$(window).resize(function() {
	if (rstimer) {
		clearTimeout(rstimer);
	}

	rstimer = setTimeout(function() {
		$('#table_pedidos_abertos').bootstrapTable('resetView');
	}, 500);
});

$(document).ready(function() {

	$(".btn_filtrar_aberto").click(function() {
		$("#modal_filtros_aberto").modal('hide');

		loadAbertos(true);

		$('#table_pedidos_abertos').bootstrapTable('selectPage', 1);
	});

	$('#msg_filtros').blink({
		delay : 400
	});

	$("#btn_maisfiltras_aberto").click(function() {
		$("#modal_filtros_aberto").modal("show");
	});

	$(".btn_filtros_limpar").click(function() {
		$("#modal_filtros_aberto").modal('hide');

		$("#num_pedido_aberto").val("");
		$("#id_produto").val("");
		$("#desc_produto").val("");
		$("#cod_bairro_aberto").val("");
		$("#data_pedido_ini").val("");
		$("#data_pedido_ini_hora").val("");
		$("#data_pedido_fim").val("");
		$("#data_pedido_fim_hora").val("");
		$("#val_ini_aberto").autoNumeric('set', "");
		$("#val_fim_aberto").autoNumeric('set', "");
		$("#flag_situacao").val("");
		$("#flag_visu").val("");
		$("#flag_pedido_ret_entre").val("")
		$("#flag_marcado_filtro").val("")

		loadAbertos(true);
		$('#table_pedidos_abertos').bootstrapTable('selectPage', 1);
	});

	$("#val_ini_aberto").autoNumeric('init', numerico);
	$("#val_fim_aberto").autoNumeric('init', numerico);
	$("#id_produto").autoNumeric('init', inteiro);

	setAutocomplete("id_produto", "desc_produto");

	$('.data').datepicker({
		format : 'dd/mm/yyyy',
		language : 'pt-BR',
		todayHighlight : true,
		daysOfWeekHighlighted : "0,6",
	});

	/*
	 * $('.hora').timepicker({ minuteStep : 1, showSeconds : false, showMeridian :
	 * false, defaultTime : false });
	 */
	$(".hora").inputmask("h:s", {
		"placeholder" : "00:00"
	});

	$('.keep-open', $('.fixed-table-toolbar')).prependTo($('#colunas'));
	$('.fixed-table-toolbar').remove();
	$('#colunas').addClass("fixed-table-toolbar");
	$('label', $('#colunas')).css('padding', '3px 20px').css('font-weight', 'normal');
	$('.dropdown-menu', $('#colunas')).css('min-width', '180px');
	$('th', $('#table_pedidos_abertos')).css('background-color', 'rgb(248, 248, 248)');

	var tabela = $('#table_pedidos_abertos');

	$(tabela).bootstrapTable();

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_pedidos_abertos')).css('background-color', 'rgb(248, 248, 248)');
		rowstuffs();
		$('[data-toggle="tooltip"]').tooltip();
	});

	$(tabela).on('click-cell.bs.table', function(event, field, value, $element) {

		if (field == 'flag_marcado') {

			if (value == 'S') {
				$("#flag_marcado_" + $element.ID_PEDIDO).prop('checked', false);
			} else {
				$("#flag_marcado_" + $element.ID_PEDIDO).prop('checked', true);
			}

			marcarPedido($element.ID_PEDIDO);
		} else {

			visualizarPedido($element.ID_PEDIDO);
		}
	});

	$(tabela).on('page-change.bs.table', function() {
		$(".openpedido").click(function() {
			visualizarPedido($(this).attr("data-valor"));
		});

		$('[data-toggle="tooltip"]').tooltip();

		rowstuffs();

	});

	carregaBairros();
	loadAbertos(true);

	window.setInterval(function() {
		loadAbertos(false);
	}, 5000);

	loadAbertos();

});

function rowstuffs() {

	$(".ped_atrasado").each(function(index) {
		var td = $(this).closest("td");
		$(td).addClass(" ped_atrasadotd ");
	})

	$(".ped_marcado").each(function(index) {
		var td = $(this).closest("td");
		$(td).addClass(" ped_marcadotd ");
	})

}

// function btnFormater(value, row, index) {
// var html = "";
// html = html + "<a data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\"
// title=\"Visualizar e responder\" data-trigger=\"focus\" class=\" btn
// btn-default openpedido btn_tabela\" data-valor=\"" + row.ID_PEDIDO + "\"
// data-container=\"body\" data-placement=\"left\" >";
// html = html + "<i class=\"fa fa-pencil-square-o\"></i>";
// html = html + "</a>";
//	
// return html;
// }

function btnFormater(value, row, index) {

	var html = "";
	var html = atrasadoBuid(row, true);
	html = html + " <div  class='checkbox ' data-toggle='tooltip' title='' style='margin-top: 0px; margin-bottom: 0px; text-align:center;padding-right: 10px'> ";
	var checked = "";
	if (value == 'S') {
		checked = "checked='true'";
	} else {
		checked = "";
	}

	html = html + "	<label style='padding-left: 0px !important;  min-height: 8px !important' > <input type='checkbox' " + checked + " id='flag_marcado_" + row.ID_PEDIDO + "' style='color: black !important'    >	</label>  ";
	html = html + " </div> ";
	html = html + atrasadoBuid(row, false);
	return html;
}

function valorFormater_aberto(value, row, index) {
	if (!$("#sys_formatador").hasClass("autonumeric")) {
		$("#sys_formatador").autoNumeric('init', numerico);
		$("#sys_formatador").addClass("autonumeric");
	}

	$("#sys_formatador").autoNumeric('set', value);
	var html = atrasadoBuid(row, true);
	html = html + $("#sys_formatador").val();
	html = html + atrasadoBuid(row, false);

	return html;
}

function qtdProdFormatter(value, row, index) {
	var html = atrasadoBuid(row, true);
	html = html + "<label style=\"font-weight: normal;cursor: pointer;\" data-toggle=\"tooltip\" title='" + value + "'  > " + row.qtdprod + " tipo(s) de produto(s). </label>";
	html = html + atrasadoBuid(row, false);
	return html;
}

function statusFormater(value, row, index) {

	var html = atrasadoBuid(row, true);
	if (value == "A") {

		html = html + "<label style='color:red'>Aberto<label>";

	} else if (value == "E") {
		html = html + "<label style='color:green'>Aceito<label>";
	} else if (value == "S") {
		html = html + "<label style='color:green'>Aceito<label>";
	} else if (value == "C") {
		html = html + "<label style='color:red'>Cancelado<label>";
	}
	html = html + atrasadoBuid(row, false);
	return html;
}

function visuFormater(value, row, index) {

	var html = atrasadoBuid(row, true);
	if (value == "N") {

		html = html + "<label style='color:red'>Não<label>";

	} else if (value == "S") {
		html = html + "<label >Sim<label>";
	}
	html = html + atrasadoBuid(row, false);
	return html;
}

function atrasadoFormater(value, row, index) {

	var html = atrasadoBuid(row, true);
	html = html + value;
	html = html + atrasadoBuid(row, false);

	return html;

}

function atrasadoBuid(row, ini) {

	if (row.atrasado == 'true' || row.atrasado == true) {
		if (ini == true) {
			return "<div data-toggle=\"tooltip\" title='O cliente informou que ainda não recebeu este pedido. O tempo de entrega passou da previsão' class='ped_atrasado'>";
		} else {
			return "</div>";
		}

	} else if (row.flag_marcado == 'S') {
		if (ini == true) {
			return "<div class='ped_marcado'>";
		} else {
			return "</div>";
		}
	} else {
		return "";
	}

}

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

			$("#cod_bairro_aberto").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

}

function marcarPedido(id_pedido) {

	var flag_marcado = "";

	if ($("#flag_marcado_" + id_pedido).is(":checked")) {
		flag_marcado = "S"
	} else {
		flag_marcado = "N"
	}

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'marcarPedido',
			flag_marcado : flag_marcado,
			id_pedido : id_pedido

		},
		success : function(data) {

			loadAbertos(false);
		},
		error : function(msg) {

			if (msg.status == 0) {

			} else {
				sysMsg(msg.msg, 'E')
			}

		}
	});

}

function loadAbertos(blockui) {

	if ($("#extra_paramfield").val() == 'canc') {
		$("#flag_situacao").val("C")
		$("#extra_paramfield").val("");
	}

	var num_pedido_aberto = $("#num_pedido_aberto").val();
	var id_produto = $("#id_produto").val();
	var cod_bairro_aberto = $("#cod_bairro_aberto").val();
	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_ini_hora = $("#data_pedido_ini_hora").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var data_pedido_fim_hora = $("#data_pedido_fim_hora").val();
	var val_ini_aberto = $("#val_ini_aberto").autoNumeric('get');
	var val_fim_aberto = $("#val_fim_aberto").autoNumeric('get');
	var flag_situacao = $("#flag_situacao").val();
	var flag_visu = $("#flag_visu").val();
	var flag_pedido_ret_entre = $("#flag_pedido_ret_entre").val();
	var flag_marcado_filtro = $("#flag_marcado_filtro").val();
	

	if (blockui) {
		$.blockUI({
			message : 'Carregando...'
		});
	}

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'carregaPedidosAbertos',
			num_pedido_aberto : num_pedido_aberto,
			id_produto : id_produto,
			cod_bairro_aberto : cod_bairro_aberto,
			data_pedido_ini : data_pedido_ini,
			data_pedido_ini_hora : data_pedido_ini_hora,
			data_pedido_fim : data_pedido_fim,
			data_pedido_fim_hora : data_pedido_fim_hora,
			val_ini_aberto : val_ini_aberto,
			val_fim_aberto : val_fim_aberto,
			flag_situacao : flag_situacao,
			flag_visu : flag_visu,
			flag_pedido_ret_entre : flag_pedido_ret_entre,
			flag_marcado_filtro:flag_marcado_filtro

		},
		success : function(data) {

			if (data.errologin != undefined) {
				window.location.href = "";
			}

			if (data.temfiltro == 'S') {
				$("#msg_filtros").show();
			} else {
				$("#msg_filtros").hide();
			}

			$('#table_pedidos_abertos').bootstrapTable('load', data.pedidos);
			$('#table_pedidos_abertos').bootstrapTable('resetView');
			$('[data-toggle="tooltip"]').tooltip();

			// $(".openpedido").click(function() { // EVENTO RESOLVIDO PELO
			// CLICK
			// ON CELL

			// visualizarPedido($(this).attr("data-valor"));
			// });
			$(".th-inner").css("text-align", "center");

			rowstuffs();

			if (blockui) {
				$.unblockUI();
			}
		},
		error : function(msg) {

			if (blockui) {
				$.unblockUI();
			}
			if (msg.status == 0) {

			} else {
				sysMsg(msg.msg, 'E')
			}

		}
	});

};
