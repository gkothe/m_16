//@ sourceURL=inserir_pedido.js
var rstimer = false;
$(window).resize(function() {
	if (rstimer) {
		clearTimeout(rstimer);
	}

	rstimer = setTimeout(function() {
		$('#table_produtos').bootstrapTable('resetView');
	}, 500);
});

$(document).ready(function() {

	$("#btn_filtrarprodped").click(function() {
		loadProdutos();
	});

	$("#p_salvar").click(function() {
		salvarpedido();
	});

	loadModosPagamentos();

	$("#btn_addprod_filt").click(function() {
		if ($("#id_produto").val() != "" && $("#desc_produto").val() != "") {
			addprod($("#id_produto").val(), $("#desc_produto").val())

			$("#id_produto").val("");
			$("#desc_produto").val("");
		}
	});

	$('#descricaoprod').keyup(function(e) {
		if (e.keyCode == 13) {
			loadProdutos();
		}
	});

	setAutocomplete("id_produto", "desc_produto");

	$('.keep-open', $('.fixed-table-toolbar')).prependTo($('#colunas'));
	$('.fixed-table-toolbar').remove();
	$('#colunas').addClass("fixed-table-toolbar");
	$('label', $('#colunas')).css('padding', '3px 20px').css('font-weight', 'normal');
	$('.dropdown-menu', $('#colunas')).css('min-width', '180px');
	$('th', $('#table_notas')).css('background-color', 'rgb(248, 248, 248)');

	var tabela = $('#table_produtos');

	$(tabela).bootstrapTable().on('dbl-click-cell.bs.table', function(field, value, row, element) {
		addprod($element.id_prod, $element.desc_abreviado);

	});

	$(tabela).on('click-cell.bs.table', function(field, value, row, $element) {
		addprod($element.id_prod, $element.desc_abreviado);
	});

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_produtos')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_produtos').bootstrapTable('updateFooter');
	});

	loadProdutos();

	$('.fixed-table-toolbar').remove();

});

function loadModosPagamentos() {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "listaModosPagamentoLoja",
			choiceserv: 'L'
		},
		async : true,
		dataType : 'json',
		success : function(data) {

			var html = "";

			for (t = 0; t < data.length; t++) {
				html = html + "<option value='" + data[t].id_modo_pagamento + "'  > " + data[t].desc_modo + "  </option>  ";
			}

			$("#id_modopagamento").html(html);

		},
		error : function(data) {

		}
	});

}

function btnFormater(value, row, index) {

	var html = "";
	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Adicionar produto\" data-trigger=\"focus\"  class=\"btn btn-default  btn_tabela \" data-valor=\"" + row.id_prod + "\"     data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\" glyphicon glyphicon-plus\"></i>";
	html = html + "</a>";

	return html;
}

function addprod(id, desc) {
	if ($("#qtd_" + id).length == 0) {

		var html = "";
		html = html + ("<tr id='linhaprod_" + id + "'> <td style=\"padding-right: 10px;\"> ");
		html = html + ("<div class=\"\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
		html = html + ("<label style='padding-top: 15px'> " + desc + "   -   </label> </div> 	</td>");
		html = html + ("<td class='prodrec_qtds' > <div style='margin-bottom: 0px;' class=\"form-group\">Qtd :<input data-toggle='tooltip' title='' min='0' type=\"number\" value='0' idprod='" + id + "'  class=\"form-control qtdinsertitem \" id=\"qtd_" + id + "\" /></div></td>");
		html = html + ("<td style='padding-left:60px;'><br><a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Remover este produto\" data-trigger=\"focus\"  onclick='removerProd(" + id + ")'  class=\"btn btn-default  btn_tabela \" data-valor=\"" + id + "\"     data-container=\"body\" data-placement=\"left\" > <i class='fa fa-trash-o'></i> </a> </td></tr>");
		html = html + ("</tr>");

		$("#add_items").append(html);

		$("#div_scroll").animate({
			scrollTop : $('#div_scroll').prop("scrollHeight")
		}, 100);
		$("#lbl_nenhumprod").hide();
		
		$('[data-toggle="tooltip"]').tooltip();
		
	}

}

function removerProd(id) {
	$("#linhaprod_" + id).remove();
}

function salvarpedido() {

	BootstrapDialog.show({
		message : "Tem certeza que deseja inserir este pedido?",
		title : "Aviso!",
		buttons : [ {
			label : 'Não',
			// no title as it is optional
			cssClass : 'btn-primary first_btn_confirm',
			action : function(dialogItself) {
				dialogItself.close();
			}
		}, {
			label : 'Sim',
			// no title as it is optional
			cssClass : 'btn-primary',
			action : function(dialogItself) {
				dialogItself.close();
				$.blockUI({
					message : 'Respondendo...',
					baseZ : 9000
				});

				var id_modopagamento = $("#id_modopagamento").val();

				var prods = [];
				var i = 0;
				$('.qtdinsertitem').each(function() {
					var item = {
						'id' : $(this).attr("idprod"),
						'qtd' : $(this).val()
					};
					prods[i++] = item;

				});

				var prodsjson = JSON.stringify(prods);

				$.ajax({
					type : "POST",
					url : "home?ac=ajax",
					dataType : "json",
					async : true,
					data : {
						cmd : 'insertpedidodistri',
						id_modopagamento : id_modopagamento,
						prodsjson : prodsjson

					},
					success : function(data) {

						if (data.msg == 'ok') {

							sysMsg("Pedido Inserido!", 'M')
							setTimeout(function() {
								$.unblockUI();
								location.reload(true);
							}, 1500);

						} else if (data.erro != undefined) {
							sysMsg(data.erro, 'E')
						}

						$.unblockUI();

					},
					error : function(msg) {
						$.unblockUI();
					}
				});

			}
		} ]
	});

}

function loadProdutos() {

	var flag_situacao = $("#flag_situacao").val();
	var descricaoprod = $("#descricaoprod").val();
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'carregaProdutos',
			descricaoprod : descricaoprod,
			flag_situacao : "S"

		},
		success : function(data) {

			$('#table_produtos').bootstrapTable('load', data);
			$('#table_produtos').bootstrapTable('resetView');
			$('[data-toggle="tooltip"]').tooltip();

			$(".openpedido").click(function() {
				visualizarPedidoHistorico($(this).attr("data-valor"));
			});
			$(".th-inner").css("text-align", "center");

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
		}
	});

}