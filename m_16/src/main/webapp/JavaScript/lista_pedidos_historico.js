//@ sourceURL=lista_pedidos_historico.js
var rstimer = false;
$(window).resize(function() {
	if (rstimer) {
		clearTimeout(rstimer);
	}

	rstimer = setTimeout(function() {
		$('#table_pedidos_historico').bootstrapTable('resetView');
	}, 500);
});

$(document).ready(function() {

	$("#btn_filtrar_historico").click(function() {
		loadhistoricos();
	});

	$("#btn_filtros_limpar").click(function() {

		$("#num_pedido_historico").val("");
		$("#id_produto").val("");
		$("#desc_produto").val("");

		$("#data_pedido_ini_historico").val("");
		$("#data_pedido_fim_historico").val("");

		$("#data_pedido_fim_historico_resposta").val("");
		$("#data_pedido_ini_historico_resposta").val("");

		$("#cod_bairro_historico").val("");
		$("#val_ini_historico").autoNumeric('set', "");
		$("#val_fim_historico").autoNumeric('set', "");
		$("#flag_situacao").val("");

		loadhistoricos();
	});

	$("#val_ini_historico").autoNumeric('init', numerico);
	$("#val_fim_historico").autoNumeric('init', numerico);
	$("#id_produto").autoNumeric('init', inteiro);

	setAutocomplete("id_produto", "desc_produto");

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

	$('.keep-open', $('.fixed-table-toolbar')).prependTo($('#colunas'));
	$('.fixed-table-toolbar').remove();
	$('#colunas').addClass("fixed-table-toolbar");
	$('label', $('#colunas')).css('padding', '3px 20px').css('font-weight', 'normal');
	$('.dropdown-menu', $('#colunas')).css('min-width', '180px');
	$('th', $('#table_notas')).css('background-color', 'rgb(248, 248, 248)');

	var tabela = $('#table_pedidos_historico');

	$(tabela).bootstrapTable().on('dbl-click-cell.bs.table', function(field, value, row, element) {
		var id = element.seq_movimento;
		var form = document.getElementById('form');
		Enviar(form, $("#app_root").val() + "?ac=nfse&id=" + id);

	});

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_pedidos_historico')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_pedidos_historico').bootstrapTable('updateFooter');
	});
	
	
	$(tabela).on('click-cell.bs.table', function(field, value, row, $element) {
		visualizarPedido($element.ID_PEDIDO);
	});

	
	$(tabela).on('page-change.bs.table', function() {
		$(".openpedido").click(function() {
			visualizarPedido($(this).attr("data-valor"));
		});
	});
	

	loadhistoricos();
	carregaBairros();

});

function btnFormater(value, row, index) {
	var html = "";
	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Visualizar e responder\" data-trigger=\"focus\"  class=\" btn btn-default openpedido btn_tabela\" data-valor=\"" + row.ID_PEDIDO + "\"   data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\"fa fa-pencil-square-o\"></i>";
	html = html + "</a>";

	return html;
}







function statusFormater(value, row, index) {

	var html = "";
	if (value == "R") {

		html = html + "<label style='color:red'>Recusado<label>";

	} else if (value == "O") {
		html = html + "<label style='color:green'>Finalizado<label>";
	}

	return html;
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

			$("#cod_bairro_historico").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function visualizarPedidoHistorico(id) {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "carregaPedido_fechado",
			id : id

		},
		async : false,
		dataType : 'json',
		success : function(data) {

			
			if(data.tipo_servico == "T"){
				$("#m_lbl_bairro").html("Bairro:");
				$("#m_tempo_max").html(data.m_tempo_max);
				$("#m_tempomax_div").show();
			}else{
				$("#m_tempomax_div").hide();
				$("#m_lbl_bairro").html("");
			}
			
			$("#m_desc_bairro").html(data.desc_bairro);
			$("#m_total_pedido").autoNumeric('set', parseFloat(data.VAL_ENTREGA) + parseFloat(data.VAL_TOTALPROD));
			$("#m_total_tele").autoNumeric('set', data.VAL_ENTREGA);
			$("#m_total_produtos").autoNumeric('set', data.VAL_TOTALPROD);
			$("#m_data_pedido").html(data.data_pedido);
			$("#m_id_pedido").val(data.ID_PEDIDO);

			if (data.flag_status == "O") {
				$("#m_lbl_titulo").html("Pedido Finalizado");
				$("#m_lbl_titulo").css("color", "green");


				$("#envio_desc_nome").html(data.DESC_NOME);
				$("#envio_desc_telefone").html(data.DESC_TELEFONE);
				$("#envio_desc_bairro").html(data.desc_bairro);
				$("#envio_desc_endereco").html(data.DESC_ENDERECO);

				$("#m_data_resposta").html(data.m_data_resposta);
				$("#m_tempo_entrega").html(data.m_tempo_entrega);
				
				$("#m_resposta_motivos").hide();
				$(".m_enviado").show();
				$("#m_aberto").hide();
				$("#m_responder").hide();
				$("#m_finalizar").hide();
				
				

			} else if (data.flag_status == "R") {
				$("#m_lbl_titulo").css("color", "red");
				$("#m_lbl_titulo").html("Pedido Recusado");

				$("#m_responder").hide();
				$("#m_finalizar").hide();
				//setar os valores 
				
				
				var html = "";
				var html = [];

				for (t = 0; t < data.motivos.length; t++) {

					html.push(" <tr> <td style=\"padding-right: 10px; width 40% \"> ");
					html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
					html.push("<label>  "+(t+1)+" - " + data.motivos[t] + "</label> </div> 	</td>");

					if (data.motivos[t + 1] != undefined) {

						html.push("<td style=\"padding-right: 10px; width 40%\"> ");
						html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
						html.push("<label> "+(t+2)+" - " + data.motivos[t + 1] + "</label> </div> 	</td>");
						t++;
					}

					
					html.push(" </tr>");

				}

				$("#desc_motivos2").html(html);
				
				
				$("#m_resposta_motivos").show();
				
				$('#m_tempo_entrega_box').hide();
				$('#m_motivos_recusa_box').show();

				$(".m_enviado").hide();
				$("#m_aberto").hide();
			}

			$('#m_table_produtos').bootstrapTable('load', data.prods);
			$('#m_table_produtos').bootstrapTable('resetView');
			$(".th-inner").css("text-align", "center");

			$('input[type=radio][name=flag_aceita_recusa]').prop('checked', false);

			$('#modal_pedido').modal('show');

		},
		error : function(data) {
			alert(data.responseText);
		}
	});

}

function loadhistoricos() {

	var num_pedido_historico = $("#num_pedido_historico").val();
	var id_produto = $("#id_produto").val();
	var cod_bairro_historico = $("#cod_bairro_historico").val();
	var data_pedido_ini = $("#data_pedido_ini_historico").val();
	var data_pedido_fim = $("#data_pedido_fim_historico").val();
	var data_reposta_ini = $("#data_pedido_ini_historico_resposta").val();
	var data_reposta_fim = $("#data_pedido_fim_historico_resposta").val();

	var val_ini_historico = $("#val_ini_historico").autoNumeric('get');
	var val_fim_historico = $("#val_fim_historico").autoNumeric('get');
	var flag_situacao = $("#flag_situacao").val();

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'carregaPedidoshistoricos',
			num_pedido_historico : num_pedido_historico,
			id_produto : id_produto,
			cod_bairro_historico : cod_bairro_historico,
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			data_reposta_ini : data_reposta_ini,
			data_reposta_fim : data_reposta_fim,
			val_ini_historico : val_ini_historico,
			val_fim_historico : val_fim_historico,
			flag_situacao : flag_situacao

		},
		success : function(data) {

			$('#table_pedidos_historico').bootstrapTable('load', data);
			$('#table_pedidos_historico').bootstrapTable('resetView');
			$('[data-toggle="tooltip"]').tooltip();

			$(".openpedido").click(function() {
				visualizarPedidoHistorico($(this).attr("data-valor"));
			});
			$(".th-inner").css("text-align", "center");

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}