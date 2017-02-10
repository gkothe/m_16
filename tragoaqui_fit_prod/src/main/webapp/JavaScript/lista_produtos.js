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
	
	loadCategorias();
	
	$("#btn_filtros_limpar").click(function() {

		$("#id_produto_listagem").val("");
		$("#desc_produto_listagem").val("");
		$("#val_ini").autoNumeric('set', "");
		$("#val_fim").autoNumeric('set', "");
		$("#flag_situacao").val("");
		$("#descricaoprod").val("");
		$("#id_categoria").val("");
		$("#id_marca").val("");
		
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
		editarProduto($element.id_prod);

	});
	
	
	$(tabela).on('click-cell.bs.table', function(field, value, row, $element) {
		editarProduto($element.id_prod);
	});
	

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_produtos')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_produtos').bootstrapTable('updateFooter');
	});

	
	loadProdutos();

	$("#row_da_tabelaprod").find(".fixed-table-toolbar").append("<div class=\"pull-left \" style=\"padding-top: 10px;\"> <label>Listagem de produtos</label> </div>");
	
	$('#descricaoprod').keyup(function(e){
	    if(e.keyCode == 13)
	    {
	    	loadProdutos();
	    }
	});
	
	
	if(applicacao==1){
		$(tabela).bootstrapTable('hideColumn', 'desc_marca');
		$('.app2').hide();
	}else if(applicacao==2){
		loadMarcas();
		$(tabela).bootstrapTable('showColumn', 'desc_marca');
		$('.app2').show();
	}
	
	
	

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
var slideIndex = 1;



function plusDivs(n) {
  showDivs(slideIndex += n);
}

function showDivs(n) {
  var i;
  var x = document.getElementsByClassName("mySlides");
  if (n > x.length) {slideIndex = 1}    
  if (n < 1) {slideIndex = x.length}
  for (i = 0; i < x.length; i++) {
     x[i].style.display = "none";  
  }
  x[slideIndex-1].style.display = "block";  
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
			$("#p_nome_completo").val(data.nome_completo);
			$("#m_val_prod").autoNumeric('set', data.valor_unit);
			$("#p_flag_status").val(data.flag_ativo);
			$("#p_id_produto").val(data.p_id_produto);
			$("#desc_categoria").val(data.desc_categoria);
			$("#desc_marca").val(data.desc_marca);
			
			
			
			
			$("#prod_slide").html("");
			
			var html = "";
			
			slideIndex = 1;
			html = html  +"";
			
			
			//$("#p_img_prod").attr("src", data.nome_img);
			
			
			for (t = 0; t < data.imgs.length; t++) {
				
				html = html  +" <img class='mySlides' style=\"height: 250px\" src='"+data.imgs[t]+"' >";
			}
			
			
			html = html  +'  <a class="w3-btn-floating w3-display-left" onclick="plusDivs(-1)">&#10094;</a> ';
			html = html  +' <a class="w3-btn-floating w3-display-right" onclick="plusDivs(1)">&#10095;</a> ';
			
	
			
			
			$("#prod_slide").html(html);
			  
			showDivs(slideIndex);

			

			testComboProd();

			$('#modal_produto').modal('show');
			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			
		}
	});

}

function cleanModalprod() {

	$("#m_lbl_produto_nome").html("");
	$("#p_nome_reduzido").html("");
	$("#p_nome_completo").val("");
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
				 sysMsg(data.msg,'M')
				
				cleanModalprod();
				$('#modal_produto').modal('hide');
				loadProdutos();
				$.unblockUI();
			}else{
				 sysMsg(data.erro,'E')
			}

		},
		error : function(msg) {
			$.unblockUI();
		}
	});

}





function loadCategorias() {
	
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'listaCategorias',
		},
		success : function(data) {
			var html = "";

			for (t = 0; t < data.length; t++) {
				html = html + "<option value='" + data[t].id_categoria + "'  > " + data[t].desc_categoria + "  </option>  ";
			}

			$("#id_categoria").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
		}
	});

}


function loadMarcas() {
	
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'listaMarcas',
		},
		success : function(data) {
			var html = "";

			for (t = 0; t < data.length; t++) {
				html = html + "<option value='" + data[t].id_marca + "'  > " + data[t].desc_marca + "  </option>  ";
			}

			$("#id_marca").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
		}
	});

}

function loadProdutos() {

	var id_produto = $("#id_produto_listagem").val();
	var val_ini = $("#val_ini").autoNumeric('get');
	var val_fim = $("#val_fim").autoNumeric('get');
	var flag_situacao = $("#flag_situacao").val();
	var descricaoprod = $("#descricaoprod").val();
	var id_categoria = $("#id_categoria").val();
	var id_marca = $("#id_marca").val();
	
	
	
	
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
			flag_situacao : flag_situacao,
			descricaoprod:descricaoprod,
			id_categoria:id_categoria,
			id_marca:id_marca

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