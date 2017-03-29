//@ sourceURL=modo_pagamento.js
$(document).ready(function() {

	loadModosPagamentos();

	$("#marca_modos").click(function() {

		$('.modopag').each(function() {
			this.checked = true;
		});

	});

	$("#desmarca_modos").click(function() {

		$('.modopag').each(function() {
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

	$('.modopag').each(function() {
		var sThisVal = (this.checked ? $(this).val() : "");
		if (sThisVal != "")
			modopag.push(sThisVal);
	});

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "salvarModosPagamento",
			modopag : modopag.toString(),

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
				html.push("<tr> <td style=\"padding-right: 10px;   min-width: 200px;\" > <label> <input type=\"checkbox\"  " + (data[t].checked == true ? "checked=checked" : "") + "    class=\"modopag\" value=\"" + data[t].id_modo_pagamento + "\"> " + data[t].desc_modo + "</label>  	</td>");
				if (data[t + 1] != undefined) {
					html.push("<td style=\"padding-right: 10px; min-width: 200px;\" > <label> <input type=\"checkbox\"  " + (data[t].checked == true ? "checked=checked" : "") + " class=\"modopag\" value=\"" + data[t + 1].id_modo_pagamento + "\"> " + data[t + 1].desc_modo + "</label>  	</td>");
					t++;
				}
				if (data[t + 1] != undefined) {
					html.push("<td style=\"padding-right: 10px; min-width: 200px; \"> <label> <input type=\"checkbox\" " + (data[t].checked == true ? "checked=checked" : "") + " class=\"modopag\" value=\"" + data[t + 1].id_modo_pagamento + "\"> " + data[t + 1].desc_modo + "</label> 	</td> ");
					t++;
				}
				html.push(" </tr>");
			}
			$("#tab_modo_pagamento").html(html.toString());
		},
		error : function(data) {

		}
	});

}