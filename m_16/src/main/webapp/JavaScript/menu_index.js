//@ sourceURL=menu_index.js
var inteiro = {
	aSep : '',
	aDec : ',',
	mDec : 0,
	vMin : 0
};

var inteiro2 = {
	aSep : '',
	aDec : ',',
	mDec : 0,
	vMax : 60,
	vMin : 0
};

var numerico = {
	aSep : '.',
	aDec : ',',
	mDec : 2,
	vMin : 0,
	aSign : 'R$ '
};

var delay = (function() {
	var timer = 0;
	return function(callback, ms) {
		clearTimeout(timer);
		timer = setTimeout(callback, ms);
	};
})();

function resizedivs() {
	$("#main_cont").css("height", $(window).height() - 60)
	$("#mainpage").css("height", $(window).height() - 130)
}
$(window).resize(function() {
	resizedivs();

});

function showHelp() {

	$("#modal_ajuda").modal("show");

}

function trocaPag(pag, jsp) {

	var link = $(pag).attr('linkmenu');
	var men = "";
	if ($BODY.hasClass('nav-md')) {
		men = "m"
	} else {
		men = "s"
	}

	document.location.href = "home?link=" + link + "&jsp=" + jsp + "&m=" + men;
}

var active_menu;
$(document).ready(function() {

	$('#mainpage').load('home?ac=' + url);

	$("[linkmenu=" + url + "]").parent().addClass("current-page");
	$("[linkmenu=" + url + "]").parent().parent().css("display", "block");
	$("[linkmenu=" + url + "]").parent().parent().parent().addClass("active");

	if (menu == "s") {
		$('#menu_toggle').click();
	}

	checarPedidos();
	loadLogoEmpresa();

	$(".clickmenu").click(function() {
		if (active_menu != undefined) {
			active_menu.removeClass("active").removeClass("active-sm");

		}
		if ($BODY.hasClass('nav-sm')) {
			$('#sidebar-menu').find('ul.child_menu').hide();
			$('#sidebar-menu').find('ul.child_menu').parent().removeClass("active")
		}

		$("#mainpage").load($(this).attr('link'));
		active_menu = $(this).parent();

		setTimeout(function() {
			$('.table_boots').bootstrapTable('resetView');
		}, 500);

	});

	$(".clickmenu2").click(function() {

		setTimeout(function() {
			$('.table_boots').bootstrapTable('resetView');
		}, 500);

	});

	$('.keep-open', $('.fixed-table-toolbar')).prependTo($('#colunas'));
	$('.fixed-table-toolbar').remove();
	$('#colunas').addClass("fixed-table-toolbar");
	$('label', $('#colunas')).css('padding', '3px 20px').css('font-weight', 'normal');
	$('.dropdown-menu', $('#colunas')).css('min-width', '180px');
	$('th', $('#m_table_produtos')).css('background-color', 'rgb(248, 248, 248)');

	var tabela = $('#m_table_produtos');

	$(tabela).bootstrapTable();

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_pedidos_abertos')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_pedidos_abertos').bootstrapTable('updateFooter');
	});

	$('#m_tempo_entrega_box').hide();
	$('#m_motivos_recusa_box').hide();

	$('.hora').timepicker({
		minuteStep : 1,
		showSeconds : false,
		showMeridian : false,
		defaultTime : false
	});

	$("#m_hora_entrega").autoNumeric('init', inteiro);
	$("#m_minutos_entrega").autoNumeric('init', inteiro2);

	$('input[type=radio][name=flag_aceita_recusa]').change(function() {
		testaAceitaRecusa();
	});

	$("#m_responder").click(function() {
		responderPedido();

	});

	$("#m_finalizar").click(function() {
		finalizarPedido();

	});

	$("#lbl_recusar").click(function() {
		$("input[name=flag_aceita_recusa][value='R']").prop('checked', true);
		testaAceitaRecusa();

	});
	$("#lbl_aceitar").click(function() {
		$("input[name=flag_aceita_recusa][value='A']").prop('checked', true);
		testaAceitaRecusa();
	});

	$('#modal_pedido').on('hidden.bs.modal', function() {
		limpaModal();
	})

	$('input[type=radio][name=flag_aceita_recusa]').prop('checked', false);

	$("#m_total_produtos").autoNumeric('init', numerico);
	$("#m_total_tele").autoNumeric('init', numerico);
	$("#m_total_pedido").autoNumeric('init', numerico);

	$('#modal_pedido').on('shown.bs.modal', function() {
		var $table = $('#m_table_produtos');
		$table.bootstrapTable('resetView');
	});

	loadMotivos();

	$('#msg_nao_vizu').blink({
		delay : 400
	});

	$('#msg_offline').blink({
		delay : 400
	})

	window.setInterval(function() {
		checarPedidos();
	}, 5000);

	/*
	 * window.setInterval(function() { playAudioPedido(); }, 5000);
	 */
	resizedivs();

});

var sommute = false;
var audio = new Audio('audiopedido.mp3');
function mutarsom() {
	audio.pause();
	audio.currentTime = 0;
	sommute = true;
	 setTimeout(function () {
		 sommute = false;
	    }, 120000);
	
}
function playAudioPedido() {
	if (!sommute)
		audio.play();
}

function limpaModal() {

	$("#m_desc_bairro").html("");
	$("#m_total_pedido").autoNumeric('set', 0);
	$("#m_total_tele").autoNumeric('set', 0);
	$("#m_total_produtos").autoNumeric('set', 0);
	$("#m_data_pedido").html("");
	$("#m_id_pedido").val("");
	$('#m_table_produtos').bootstrapTable('removeAll');
	$('input[type=radio][name=flag_aceita_recusa]').prop('checked', false);
	$('.motivo').prop('checked', false);
	$("#m_hora_entrega").autoNumeric('set', 0);
	$("#m_minutos_entrega").autoNumeric('set', 0);
	$("#desc_motivos2").html("");

	testaAceitaRecusa();

}

function loadLogoEmpresa() {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "getLogo",

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			$("#lbl_descfanta").html(data.desc_nome);
			$("#lbl_logomenu").attr("src", data.nome_img);

		},
		error : function(data) {
			alert(data.responseText);
		}
	});

}

function checarPedidos() {
	$.ajax({
		type : 'POST',
		url : 'home?ac=ajax',
		data : {
			cmd : "checkPedidos",
		},
		async : true,
		dataType : 'json',
		success : function(data) {
			$("#msg_holder2").hide();
			$("#menu_notification").html("");
			$("#h_qtd_pedz").html("");

			if (data.errologin != undefined) {
				window.location.href = "";
			} else if (data.tem == "true") {

				$("#msg_holder").show();

				var html = "";
				var vizualizado = "";
				$("#h_qtd_pedz").html(data.qtd);
				for (t = 0; t < data.pedidos.length; t++) {
					var html = "";
					html = html + (" <li> <a onClick=\"visualizarPedido(" + data.pedidos[t].id_pedido + ")\" >     ");
					html = html + ("	<span class=\"message\">Número do pedido:  " + data.pedidos[t].num_ped + " <span class=\"time\"> " + data.pedidos[t].texto_minutos + " </span> </span>    ");
					html = html + ("	<span class=\"message\">Bairro: " + data.pedidos[t].desc_bairro + "  </span>    ");
					html = html + ("	<span class=\"message\">Valor: R$ <label style='font-weight:normal !important;' id='lbl_notval_" + t + "'>   </span>  	</a> </li> ");

					if (data.pedidos[t].flag_vizualizado == 'N') {
						vizualizado = 'N';
					}
					$("#menu_notification").html($("#menu_notification").html() + html);
					$("#lbl_notval_" + t).autoNumeric('init', numerico);
					$("#lbl_notval_" + t).autoNumeric('set', data.pedidos[t].valor);

				}

				html = ("<li> <div class=\"text-center\"> <a  href=\"home?link=listaped&jsp=N&m=m\"  > <strong>Ver todos pedidos</strong> <i class=\"fa fa-angle-right\"></i> </a> </div> </li>");

				$("#menu_notification").html($("#menu_notification").html() + html);

				$(".not_numerico").autoNumeric('init', numerico);

				if (vizualizado == "N") {
					playAudioPedido();
				}

			} else if (data.tem == "false") {
				$("#msg_holder").hide();

			}

		},
		error : function(data) {
			if (data.status == 0) {

				$("#msg_holder").hide();
				$("#msg_holder2").show();
				$("#h_qtd_pedz").html("");

			} else {
				alert(data.statusText);
			}
		}
	});

}

function testaAceitaRecusa() {
	if ($('input[name=flag_aceita_recusa]:checked').val() == 'A') {
		$('#m_tempo_entrega_box').show();
		$('#m_motivos_recusa_box').hide();
		$("#m_hora_entrega").focus();

	} else if ($('input[name=flag_aceita_recusa]:checked').val() == 'R') {
		$('#m_tempo_entrega_box').hide();
		$('#m_motivos_recusa_box').show();
	} else {

		$('#m_tempo_entrega_box').hide();
		$('#m_motivos_recusa_box').hide();

	}
}

function valorFormater(value, row, index) {
	if (!$("#sys_formatador").hasClass("autonumeric")) {
		$("#sys_formatador").autoNumeric('init', numerico);
		$("#sys_formatador").addClass("autonumeric");
	}

	$("#sys_formatador").autoNumeric('set', value);
	return $("#sys_formatador").val();
}

function loadMotivos() {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "loadMotivos",

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			var html = [];

			for (t = 0; t < data.length; t++) {

				html.push(" <tr> <td style=\"padding-right: 10px;\"> ");
				html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
				html.push("<label> <input type=\"checkbox\" class=\"motivo\" value=\"" + data[t].COD_MOTIVO + "\"> " + data[t].DESC_MOTIVO + "</label> </div> 	</td>");

				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px;\"> ");
					html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
					html.push("<label> <input type=\"checkbox\" class=\"motivo\" value=\"" + data[t + 1].COD_MOTIVO + "\"> " + data[t + 1].DESC_MOTIVO + "</label> </div> 	</td>");
					t++;
				}

				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px;\"> ");
					html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
					html.push("<label> <input type=\"checkbox\"  class=\"motivo\" value=\"" + data[t + 1].COD_MOTIVO + "\"> " + data[t + 1].DESC_MOTIVO + "</label> </div> 	</td>");
					t++;
				}

				html.push(" </tr>");

			}

			$("#desc_motivos").html(html);

		},
		error : function(data) {
			alert(data.responseText);
		}
	});

}

function visualizarPedido(id) {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "carregaPedido_AbertoEnvio",
			id : id

		},
		async : false,
		dataType : 'json',
		success : function(data) {

			audio.pause();
			audio.currentTime = 0;
			
			$("#m_desc_bairro").html(data.desc_bairro);
			$("#m_total_pedido").autoNumeric('set', parseFloat(data.VAL_ENTREGA) + parseFloat(data.VAL_TOTALPROD));
			$("#m_total_tele").autoNumeric('set', data.VAL_ENTREGA);
			$("#m_total_produtos").autoNumeric('set', data.VAL_TOTALPROD);
			$("#m_data_pedido").html(data.data_pedido);
			$("#m_id_pedido").val(data.ID_PEDIDO);

			if (data.flag_status == "A") {
				$("#m_lbl_titulo").html("Pedido em aberto!");
				$("#m_lbl_titulo").css("color", "green");

				$(".m_enviado").hide();
				$("#m_aberto").show();
				$("#m_resposta_motivos").hide();
				$("#m_responder").show();
				$("#m_finalizar").hide();

			} else if (data.flag_status == "E") {
				$("#m_lbl_titulo").css("color", "green");
				$("#m_lbl_titulo").html("Em envio");

				$("#m_responder").hide();
				// setar os dados se estiver em envio

				$("#envio_desc_nome").html(data.DESC_NOME);
				$("#envio_desc_telefone").html(data.DESC_TELEFONE);
				$("#envio_desc_bairro").html(data.desc_bairro);
				$("#envio_desc_endereco").html(data.DESC_ENDERECO);
				$("#m_resposta_motivos").hide();
				$("#m_data_resposta").html(data.m_data_resposta);
				$("#m_tempo_entrega").html(data.m_tempo_entrega);
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
			alert(data.responseText);
		}
	});

}

function finalizarPedido() {

	$.blockUI({
		message : 'Finalizando...'
	});

	if (confirm("Tem certeza que deseja finalizar este pedido? (Mudar a situação para Ok.)")) {

		var id_pedido = $("#m_id_pedido").val();

		$.ajax({
			type : "POST",
			url : "home?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'finalizandoPedido',
				id_pedido : id_pedido

			},
			success : function(data) {

				if (data.msg == 'ok') {
					alert("Pedido finalizado!");

					checarPedidos();

					try {
						loadAbertos(true);
					} catch (err) {

					}

					$('#modal_pedido').modal('hide');
					limpaModal();

				} else if (data.erro != undefined) {
					alert(data.erro);
				}

				$.unblockUI();

			},
			error : function(msg) {
				$.unblockUI();
				alert("Erro: " + msg.msg);
			}
		});

	}

}

function responderPedido() {

	if ($('input[name=flag_aceita_recusa]:checked').val() != undefined) {

		var resposta = $('input[name=flag_aceita_recusa]:checked').val();
		var msg = (resposta == "A" ? "Tem certeza que deseja ACEITAR este pedido?" : "Tem certeza que deseja RECUSAR este pedido?");

		if (confirm(msg)) {

			$.blockUI({
				message : 'Respondendo...'
			});

			var hora_entrega = $("#m_hora_entrega").val();
			var min_entrega = $("#m_minutos_entrega").val();

			var motivos = [];
			var i = 0;

			$('.motivo:checked').each(function() {
				motivos[i++] = $(this).val();
			});

			var id = $("#m_id_pedido").val();

			var motivos_json = JSON.stringify(motivos);
			$.ajax({
				type : "POST",
				url : "home?ac=ajax",
				dataType : "json",
				async : true,
				data : {
					cmd : 'responderPedido',
					motivos_json : motivos_json,
					hora_entrega : hora_entrega,
					min_entrega : min_entrega,
					id : id,
					resposta : resposta

				},
				success : function(data) {

					if (data.msg == 'ok') {
						if (resposta == "A") {
							alert("Pedido Respondido");

							// TRAZER MODAL COM ENDEREÇO OU PDF OU ALGO COM OS 2
							// EM CONJUNTO

						}

						if (resposta == "R") {
							alert("Pedido Recusado!");
						}

						checarPedidos();

						try {
							loadAbertos(true);
						} catch (err) {

						}

						$('#modal_pedido').modal('hide');
						limpaModal()
						if (resposta == "A") {

							setTimeout(function() {
								visualizarPedido(id);
							}, 700)

						}

					} else if (data.erro != undefined) {
						alert(data.erro);
					}

					$.unblockUI();

				},
				error : function(msg) {
					$.unblockUI();
					alert("Erro: " + msg.msg);
				}
			});
		}

	} else {
		alert("Você deve aceitar ou recusar o pedido.");
	}
}

function setAutocomplete(cod, descr) {

	$('#lblerro_' + cod).hide();

	$('#' + descr).autocomplete({
		source : function(request, response) {
			$.ajax({
				url : "home?ac=ajax",
				dataType : "json",
				type : 'POST',
				data : {
					cmd : 'autocomplete',
					q : request.term,
					campo : descr

				},
				success : function(data) {

					if (data.length > 0) {
						$("#" + descr).attr("idcd", data[0].id);
						$("#" + descr).attr("descrcd", data[0].descr);
					}
					if (data.length == 0) {
						$("#" + descr).attr("idcd", "");
						$("#" + descr).attr("descrcd", "");
					}

					response($.map(data, function(value, key) {
						return {
							label : value.descr,
							value : value.id

						};
					}));
				}
			});
		},
		minLength : 0,
		focus : function() {
			// prevent value inserted on focus
			return false;
		},
		select : function(event, ui) {

			$("#" + cod).val(ui.item.value);
			$("#" + cod).attr("descr", ui.item.label);
			$("#" + descr).val(ui.item.label);

			ui.item.value = ui.item.label;
		},
		open : function() {
			$(this).removeClass("ui-corner-all").addClass("ui-corner-top");
		},
		close : function() {
			$(this).removeClass("ui-corner-top").addClass("ui-corner-all");
		}
	});

	$('#' + descr).keypress(function(event) {
		if (event.keyCode == 13) {
			// if($(this).val()==""){
			$(this).autocomplete("search", $("#" + descr).val())
			$('#' + cod).val($(this).attr("idcd"));
			$(this).val($(this).attr("descrcd"));
			// }
		}
	});

	$('#' + cod).blur(function() {
		var param = [ {
			name : 'cmd',
			value : 'autocomplete'
		}, {
			name : 'campo',
			value : cod
		}, {
			name : 'q',
			value : $("#" + cod).val()
		} ];

		if ($("#" + cod).val()) {
			$.ajax({
				type : 'POST',
				url : "home?ac=ajax",
				data : param,
				async : true,
				dataType : 'json',
				success : function(data) {
					if (data.descr) {
						$("#" + descr).val(data.descr);
					} else {
						$("#" + descr).val("");
					}

				},
				error : function(data) {
					alert(data.responseText);
				}
			});
		}
	});

	$('#' + descr).blur(function(event) {

		$("#" + descr).removeClass("erro");
		$("#" + descr).removeClass("sucesso");

		var param = [ {
			name : 'cmd',
			value : 'autocomplete'
		}, {
			name : 'campo',
			value : descr
		}, {
			name : 'q',
			value : $("#" + descr).val()
		},

		];

		$.ajax({
			url : "home?ac=ajax",
			type : 'POST',
			dataType : "json",
			data : param,
			async : false,
			success : function(data) {

				if (data.length == 0 || $("#" + descr).val() == "") {
					$('#' + cod).val("");
					$("#" + descr).attr("idcd", "");
					$("#" + descr).attr("descrcd", "");
					$('#lblerro_' + cod).hide();

				}
				var t = 0;
				if (data.length == 0 && $("#" + descr).val() != "") {
					$("#" + descr).addClass("erro");
					$("#" + cod).val("");
					$('#lblerro_' + cod).show();
				} else if (data.length > 0 && $("#" + descr).val() != "" && $("#" + descr).val() != data[0].descr) {
					var achou = false;
					for (t = 1; t < data.length; t++) {
						if ($("#" + descr).val() == data[t].descr) {
							achou = true;
							break;
						}
					}
					if (!achou) {
						$("#" + descr).addClass("erro");
						$("#" + cod).val("");
						$('#lblerro_' + cod).hide().show();
					}
				}

				if (data.length > 0 && $("#" + descr).val() != "" && $("#" + descr).val() == data[t].descr) {

					$('#lblerro_' + cod).hide();
					$("#" + descr).addClass("sucesso");
					$("#" + cod).val(data[t].id);
					$("#" + descr).attr("idcd", data[t].id);
					$("#" + descr).attr("descrcd", data[t].descr);

				}

			}
		});
	});
}

(function($) {
	$.fn.blink = function(options) {
		var defaults = {
			delay : 500
		};
		var options = $.extend(defaults, options);
		return $(this).each(function(idx, itm) {
			setInterval(function() {
				if ($(itm).css("visibility") === "visible") {
					$(itm).css('visibility', 'hidden');
				} else {
					$(itm).css('visibility', 'visible');
				}
			}, options.delay);
		});
	}
}(jQuery))