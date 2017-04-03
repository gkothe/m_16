//@ sourceURL=modo_pagamento.js
$(document).ready(function() {

	loadModosPagamentos();

	$("#marca_modos").click(function() {

		$('.modopag_entrega').each(function() {
			this.checked = true;
		});
		
		$('.modopag_retirada').each(function() {
			this.checked = true;
		});

	});

	$("#desmarca_modos").click(function() {


		$('.modopag_entrega').each(function() {
			this.checked = false;
		});
		
		$('.modopag_retirada').each(function() {
			this.checked = false;
		});
		
		
	});

	$("#salvar_modos").click(function() {

		salvarPagamentos();
	});

});

function salvarPagamentos() {
	$.blockUI({
		message : 'Salvando...'
	});
	var modopag = [];

	$('.modopag_entrega').each(function() {

		var id = $(this).val();
		var entrega = this.checked ? true : false;
		var retirada = 	$("#check_retirada_"+id).is(":checked") ? true : false;
		if(entrega || retirada){
			modopag.push({id:id, entrega:entrega, retirada:retirada})
		}

	});


	var modopagjson = JSON.stringify(modopag);
	
	
	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "salvarModosPagamento",
			modopagjson:modopagjson

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			if (data.msg == 'ok') {

				sysMsg("Pagamentos salvos!", 'M')
				setTimeout(function() {
					location.reload(true);
				}, 1500);

			} else {
				sysMsg(data.erro, 'E')
			}
			$.unblockUI();
		},
		error : function(data) {
			$.unblockUI();
		}
	});

}

function loadModosPagamentos() {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "listaModosPagamento",

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			var html = [];

			for (t = 0; t < data.length; t++) {
				html.push("<tr><td> <label> " + data[t].desc_modo + "</label>  	</td>");
				html.push("<td   style='text-align:center' > <input id='' data-toggle=\"tooltip\" title=\"" + data[t].desc_modo + "\" type=\"checkbox\" style=\"width: 50px\" " + (data[t].flag_entrega == true ? "checked=checked" : "") + "    class=\"modopag_entrega\" value=\"" + data[t].id_modo_pagamento + "\">  	</td>");
				html.push("<td  style='text-align:center' >  <input id='check_retirada_"+data[t].id_modo_pagamento+"' data-toggle=\"tooltip\" title=\"" + data[t].desc_modo + "\" type=\"checkbox\" type=\"checkbox\" style=\"width: 75px\"  " + (data[t].flag_retiradalocal == true ? "checked=checked" : "") + "    class=\"modopag_retirada\" value=\"" + data[t].id_modo_pagamento + "\">  	</td>");
				html.push(" </tr>");
			}

			$('[data-toggle="tooltip"]').tooltip();
			$("#tab_modo_pagamento").html(html.toString());
		},
		error : function(data) {

		}
	});

}