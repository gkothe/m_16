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
		loadhistoricos(1, 10);
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
		$("#flag_pedido_ret_entre").val("");

		loadhistoricos(1, 10);
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
		visualizarPedidoHistorico($element.ID_PEDIDO);
	});

	$(tabela).on('page-change.bs.table', function(e, pag, size) {
		$(".openpedido").click(function() {
			visualizarPedidoHistorico($(this).attr("data-valor"));
		});

		loadhistoricos(pag, size);
	});

	$(tabela).on('sort.bs.table', function(e, name, order) {
		$("#sys_order").val(order);
		$("#sys_name").val(name);

		loadhistoricos(1, 10);

	});

	$("#sys_name").val("NUM_PED");
	$("#sys_order").val("asc");

	loadhistoricos(1, 10);
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
	} else if (value == "C") {
		html = html + "<label style='color:red'>Cancelado<label>";
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
			$(".cancelamento").hide();

			if (data.tipo_servico == "T") {
				$("#m_lbl_bairro").html("Bairro:");

				if (data.flag_modoentrega == 'A') {
					$("#m_tempomax_div").hide();
					$("#m_agendamento").html(data.data_agenda_entrega);
					$("#m_agendamento_div").show();
				} else {
					$("#m_agendamento_div").hide();
					$("#m_tempomax_div").hide();
					$("#m_tempo_max").html(data.m_tempo_max);
					$("#m_tempomax_div").show();
				}

			} else {
				$("#m_agendamento_div").hide();
				$("#m_tempomax_div").hide();
				$("#m_lbl_bairro").html("");
			}

			if (data.num_trocopara != undefined) {
				$("#m_troco_para_div").show();
				$("#m_troco_para").html(data.num_trocopara);

			} else {
				$("#m_troco_para_div").hide();
				$("#m_troco_para").html("");
			}

			$("#m_desc_bairro").html(data.desc_bairro);
			$("#m_total_pedido").autoNumeric('set', parseFloat(data.VAL_ENTREGA) + parseFloat(data.VAL_TOTALPROD));
			$("#m_total_tele").autoNumeric('set', data.VAL_ENTREGA);
			$("#m_total_produtos").autoNumeric('set', data.VAL_TOTALPROD);
			$("#m_data_pedido").html(data.data_pedido);
			$("#m_id_pedido").val(data.ID_PEDIDO);
			var num_ped = data.num_ped;
			if (data.flag_status == "O") {
				$("#m_lbl_titulo").html("Pedido Finalizado - Número: " + num_ped);
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
				$("#m_lbl_titulo").html("Pedido Recusado Número: " + num_ped);

				$("#m_responder").hide();
				$("#m_finalizar").hide();
				// setar os valores

				var html = "";
				var html = [];

				for (t = 0; t < data.motivos.length; t++) {
					if ((motivo_estoque + "") == (data.motivos[t].cod_motivo + "")) {
						html.push(" <tr> <td colspan='100%' style=\"padding-right: 10px;\"> ");
						html.push("<div   class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
						html.push("<label>  " + (t + 1) + " - " + data.motivos[t].desc_motivo + "</label> </div> &nbsp; ");
						html.push('<button data-toggle=\"tooltip\" style="margin-bottom: 0px;padding-bottom: 2px;padding-top: 1px;height: 21px;"  id=\"modal_prodsrecusa_btn_prods2\" type="button" class="btn btn-primary" title="Listar produtos"> <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span></button>');
						html.push(" </tr>");
					}

				}

				for (t = 0; t < data.motivos.length; t++) {

					if ((motivo_estoque + "") == (data.motivos[t].cod_motivo + "")) {
					} else {

						html.push(" <tr> <td style=\"padding-right: 10px; width 40% \"> ");
						html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
						html.push("<label>  " + (t + 1) + " - " + data.motivos[t].desc_motivo + "</label> </div> 	</td>");

						if (data.motivos[t + 1] != undefined) {

							if ((motivo_estoque + "") == (data.motivos[t + 1].cod_motivo + "")) {
								t++;
							}
							if (data.motivos[t + 1] != undefined) {
								html.push("<td style=\"padding-right: 10px; width 40%\"> ");
								html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
								html.push("<label> " + (t + 2) + " - " + data.motivos[t + 1].desc_motivo + "</label> </div> 	</td>");
								t++;
							}
						}

						html.push(" </tr>");
					}
				}

				setTimeout(function() {

					$('[data-toggle="tooltip"]').tooltip();

					$("#modal_prodsrecusa_btn_prods2").click(function() {

						showProdsRecusaHist(id);

					});

				}, 700)

				$("#desc_motivos2").html(html);

				$("#m_resposta_motivos").show();

				$('#m_tempo_entrega_box').hide();
				$('#m_motivos_recusa_box').show();

				$(".m_enviado").hide();
				$("#m_aberto").hide();
			} else if (data.flag_status == "C") {

				$("#m_lbl_titulo").css("color", "red");

				$("#m_lbl_titulo").html("Cancelado -  Número: " + num_ped);
				$(".cancelamento").show();
				$("#m_responder").hide();

				// setar os dados se estiver em envio

				$("#envio_desc_nome").html(data.DESC_NOME);
				$("#envio_desc_telefone").html(data.DESC_TELEFONE);
				$("#envio_desc_bairro").html(data.desc_bairro);
				$("#envio_desc_endereco").html(data.DESC_ENDERECO);
				$("#m_resposta_motivos").hide();
				$("#m_data_resposta").html(data.m_data_resposta);
				$("#m_tempo_entrega").html(data.m_tempo_entrega);

				$("#m_data_cancelamento").html(data.DATA_CANCELAMENTO);
				$("#m_motivo").html(data.DESC_MOTIVO);
				$("#m_observ").html(data.DESC_OBS);

				if (data.darok == true) {
					$("#m_finalizar").show();
				} else {
					$("#m_finalizar").hide();
				}

				$(".m_enviado").show();
				$("#m_aberto").hide();

			}

			$('#m_table_produtos').bootstrapTable('load', data.prods);
			$('#m_table_produtos').bootstrapTable('resetView');
			$(".th-inner").css("text-align", "center");

			$('input[type=radio][name=flag_aceita_recusa]').prop('checked', false);

			$('#modal_pedido').modal('show');

		},
		error : function(data) {

		}
	});

}

function showProdsRecusaHist(id) {

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
			$("#msg_erro_aviso_sub").html("Produtos que foram marcados como 'em falta no estoque':");
			preventclean = true;
			$("#modal_prodsrecusa").modal('show');
			$("#modal_pedido").modal('hide');
			$("#modal_prodsrecusa_btn_voltar").hide();
			
			

			var html = [];

			html.push("<table>");

			for (t = 0; t < data.prods.length; t++) {
				if (data.prods[t].FLAG_RECUSADO=='S') {
					html.push(" <tr> <td style=\"padding-right: 10px;\"> ");
					html.push("<label> " + data.prods[t].DESC_PROD + "</label> </div> 	</td> <tr>");
				}

			}

			html.push("</table>");
			$("#modal_prodsrecusa_div").html(html);
		},
		error : function(data) {
			if (data.statusText != undefined)
				sysMsg(data.statusText, 'E');
		}
	});

}

function loadhistoricos(pag, size) {

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
	var flag_pedido_ret_entre = $("#flag_pedido_ret_entre").val();

	var sort = $("#sys_name").val();
	var order = $("#sys_order").val();

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
			flag_situacao : flag_situacao,
			flag_pedido_ret_entre : flag_pedido_ret_entre,
			pag : pag,
			size : size,
			sort : sort,
			order : order

		},
		success : function(data) {

			$('#table_pedidos_historico').bootstrapTable('load', data);
			$('#table_pedidos_historico').bootstrapTable('resetView');
			$('[data-toggle="tooltip"]').tooltip();

			/*
			 * $(".openpedido").click(function() {
			 * visualizarPedidoHistorico($(this).attr("data-valor")); });
			 */
			$(".th-inner").css("text-align", "center");

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

}