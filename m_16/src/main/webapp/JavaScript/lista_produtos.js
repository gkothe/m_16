//@ sourceURL=lista_produtos.js
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

	$("#btn_filtrar").click(function() {
		loadProdutos();
	});

	$("#btn_filtros_limpar").click(function() {

		$("#id_produto_listagem").val("");
		$("#desc_produto_listagem").val("");
		$("#val_ini").autoNumeric('set', "");
		$("#val_fim").autoNumeric('set', "");
		$("#flag_situacao").val("");

		loadProdutos();
	});

	$("#p_flag_status").change(function() {
		testComboProd();
	});

	$("#p_salvar").click(function() {
		var produto = $("#p_id_produto").val();
		salvarProduto(produto);
	});

	$("#val_ini").autoNumeric('init', numerico);
	$("#val_fim").autoNumeric('init', numerico);
	$("#m_val_prod").autoNumeric('init', numerico);
	$("#id_produto_listagem").autoNumeric('init', inteiro);

	setAutocomplete("id_produto_listagem", "desc_produto_listagem");

	$('.keep-open', $('.fixed-table-toolbar')).prependTo($('#colunas'));
	$('.fixed-table-toolbar').remove();
	$('#colunas').addClass("fixed-table-toolbar");
	$('label', $('#colunas')).css('padding', '3px 20px').css('font-weight', 'normal');
	$('.dropdown-menu', $('#colunas')).css('min-width', '180px');
	$('th', $('#table_notas')).css('background-color', 'rgb(248, 248, 248)');

	var tabela = $('#table_produtos');

	$(tabela).bootstrapTable().on('dbl-click-cell.bs.table', function(field, value, row, element) {
		var id = element.seq_movimento;
		var form = document.getElementById('form');
		Enviar(form, $("#app_root").val() + "?ac=nfse&id=" + id);

	});

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_produtos')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_produtos').bootstrapTable('updateFooter');
	});

	loadProdutos();

	$("#row_da_tabelaprod").find(".fixed-table-toolbar").append("<div class=\"pull-left \" style=\"padding-top: 10px;\"> <label>Listagem de produtos</label> </div>");
	;

});

function btnFormater(value, row, index) {
	var html = "";
	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Editar produto\" data-trigger=\"focus\"  onclick='editarProduto(" + row.id_prod + ")'  class=\"btn btn-default  btn_tabela \" data-valor=\"" + row.id_prod + "\"     data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\"fa fa-pencil-square-o\"></i>";
	html = html + "</a>";

	return html;
}

function statusFormater(value, row, index) {

	var html = "";
	if (value == "N") {

		html = html + "<label style='color:red'>Desativado<label>";

	} else if (value == "S") {
		html = html + "<label style='color:green'>Ativado<label>";
	}

	return html;
}

function testComboProd() {

	if ($("#p_flag_status").val() == 'S') {

		$("#p_flag_status").css("background-color", 'rgb(0, 255, 0)');
	} else if ($("#p_flag_status").val() == 'N') {

		$("#p_flag_status").css("background-color", 'red');
	}

}

function editarProduto(id_produto) {

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'loadProduto',
			id_produto : id_produto

		},
		success : function(data) {

			/*
			 * $encode["nome_abreviado"] = $row["DESC_ABREVIADO"];
			 * $encode["valor_unit"] = $row["val_prod"];
			 * $encode["nome_completo"] = $row["DESC_PROD"];
			 * $encode["flag_ativo"] = $row["flag_ativo"];
			 */

			$("#m_lbl_produto_nome").html("Cód " + data.p_id_produto +" - " +data.nome_abreviado);
			$("#p_nome_reduzido").html(data.nome_abreviado);
			$("#p_nome_completo").html(data.nome_completo);
			$("#m_val_prod").autoNumeric('set', data.valor_unit);
			$("#p_flag_status").val(data.flag_ativo);
			$("#p_id_produto").val(data.p_id_produto);

			$("#p_img_prod").attr("src", data.nome_img);

			testComboProd();

			$('#modal_produto').modal('show');
			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function cleanModalprod() {

	$("#m_lbl_produto_nome").html("");
	$("#p_nome_reduzido").html("");
	$("#p_nome_completo").html("");
	$("#m_val_prod").autoNumeric('set', 0);
	$("#p_flag_status").val("");
	$("#p_id_produto").val("");

	$("#p_img_prod").attr("src", "");

}

function salvarProduto(id_produto) {

	var val_prod = $("#m_val_prod").autoNumeric('get');
	var flag_situacao = $("#p_flag_status").val();
	$.blockUI({
		message : 'Salvando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'salvar_prod',
			id_produto : id_produto,
			val_prod : val_prod,
			flag_situacao : flag_situacao

		},
		success : function(data) {
			if (data.msg) {
				alert(data.msg);
				cleanModalprod();
				$('#modal_produto').modal('hide');
				loadProdutos();
				$.unblockUI();
			}else{
				alert(data.erro);
			}

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function loadProdutos() {

	var id_produto = $("#id_produto_listagem").val();
	var val_ini = $("#val_ini").autoNumeric('get');
	var val_fim = $("#val_fim").autoNumeric('get');
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
			cmd : 'carregaProdutos',
			id_produto : id_produto,
			val_ini : val_ini,
			val_fim : val_fim,
			flag_situacao : flag_situacao

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
			alert("Erro: " + msg.msg);
		}
	});

}