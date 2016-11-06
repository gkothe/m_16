//@ sourceURL=dash1_pedidos.js

var x_bar = 200;
var y_bar = 50;
var x2_bar = 80;
var y2_bar = 20;
var widht_bar = "75%"
$(document).ready(function() {

	$('#tabs_dash a[href="#dashboard1"]').tab('show')
	$('#tabs_dash a[href="#filtros"]').tab('show')

	$('.collapse-link').on('click', function() {
		var $BOX_PANEL = $(this).closest('.x_panel'), $ICON = $(this).find('i'), $BOX_CONTENT = $BOX_PANEL.find('.x_content');

		// fix for some div with hardcoded fix class
		if ($BOX_PANEL.attr('style')) {
			$BOX_CONTENT.slideToggle(200, function() {
				$BOX_PANEL.removeAttr('style');
			});
		} else {
			$BOX_CONTENT.slideToggle(200);
			$BOX_PANEL.css('height', 'auto');
		}

		$ICON.toggleClass('fa-chevron-up fa-chevron-down');
	});
	setAutocomplete("id_produto_listagem", "desc_produto_listagem");
	carregaBairros();
	$('#dias_semana').multiselect();

	$(".hora").inputmask("h:s", {
		"placeholder" : "00:00"
	});

	$('.data').datepicker({
		format : 'dd/mm/yyyy',
		language : 'pt-BR',
		todayHighlight : true,
		daysOfWeekHighlighted : "0,6",
	});
	filtrar();

	$("#btn_filtrar").click(function() {

		// filtrar(true);
		$('#tabs_dash a[href="#dashboard1"]').tab('show')
	});

	$("#btn_filtrar2").click(function() {
		filtrarProds();
	});

	$('a[data-toggle="tab"]').on('shown.bs.tab', function(evt) {
		$(window).trigger('resize');
		filtrar(false)
	});
});

function filtrar(troca) {

	$(".linha_add_dash").remove();

	dashServico();
	dashModo();
	dashVendaProdsQtd('+');
	dashVendaProdsVal('+');
	dashInfosBasicas();
	dashPedidoHora();
	dashPedidoDia();
	dashBairrosLista();
	filtrarProds();
	if (troca) {
		$('#tabs_dash a[href="#dashboard1"]').tab('show')
	}

}

function filtrarProds() {

	dashProdHora_single();
	dashProdDiaSingle();
	dashProdBairroSingle();
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

			$("#cod_bairro").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

var cont = 0;
function valorFormater(value) {
	if (!$("#sys_formatador").hasClass("autonumeric")) {
		$("#sys_formatador").autoNumeric('init', inteiro3);
		$("#sys_formatador").addClass("autonumeric");
	}

	$("#sys_formatador").autoNumeric('set', value);
	return $("#sys_formatador").val();
}

function valorFormater2(value) {
	if (!$("#sys_formatador2").hasClass("autonumeric")) {
		$("#sys_formatador2").autoNumeric('init', numerico2);
		$("#sys_formatador2").addClass("autonumeric");
	}

	$("#sys_formatador2").autoNumeric('set', value);
	return $("#sys_formatador2").val();
}

function dashVendaProdsQtd(consulta) {// Produtos mais vendidos

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$("#dash_vendasprod").html("");

	$(window).trigger('resize');

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashProdutosQtd',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana,
			consulta : consulta
		},
		success : function(data) {

			if (consulta == "+")
				$("#dashVendaProdsQtd_title").html("Produtos mais vendidos");
			else
				$("#dashVendaProdsQtd_title").html("Produtos menos vendidos");

			var dataset = [];
			var desc = [];
			var qtd = [];

			for (t = 0; t < data.length; t++) {

				dataset[dataset.length] = {
					device : data[t].desc,
					geekbench : data[t].qtd
				};

				desc[desc.length] = data[t].desc;
				qtd[qtd.length] = data[t].qtd;

			}

			desc.reverse();
			qtd.reverse();

			var echartBar = echarts.init(document.getElementById('dash_vendasprod'));

			echartBar.setOption({
				color : [ '#26B99A' ],
				title : {
					text : 'Produtos ',
					show : true

				},
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {

						return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
					}
				},
				legend : {
					x : 500,
					show : true,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					boundaryGap : [ 0, 0.01 ],
					axisLabel : {
						formatter : function(val) {
							return valorFormater(val)
						},

					}
				} ],
				grid : {
					x : x_bar,
					y : y_bar,
					x2 : x2_bar,
					y2 : y2_bar,
					width : widht_bar
				},
				yAxis : [ {
					type : 'category',
					data : desc,
					axisLabel : {
						textStyle : {
							fontSize : 10
						},
						splitAreaType : {
							show : true
						},
						margin : 3

					}
				} ],
				series : [ {
					name : 'Quantidade',
					type : 'bar',
					data : qtd,
					barGap : "20%"
				} ]
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

	$(window).trigger('resize');
}

function dashVendaProdsVal(consulta) {// Produtos com mais faturamento

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$("#dash_vendasprodval").html("");

	$(window).trigger('resize');

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashProdutosVal',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana,
			consulta : consulta

		},
		success : function(data) {

			if (consulta == "+")
				$("#dashProdutosVal_title").html("Produtos mais faturados");
			else
				$("#dashProdutosVal_title").html("Produtos menos faturados");

			var dataset = [];
			var desc = [];
			var qtd = [];

			for (t = 0; t < data.length; t++) {

				dataset[dataset.length] = {
					device : data[t].desc,
					geekbench : data[t].valsold
				};

				desc[desc.length] = data[t].desc;
				qtd[qtd.length] = data[t].valsold;

			}

			desc.reverse();
			qtd.reverse();

			var echartBar = echarts.init(document.getElementById('dash_vendasprodval'));

			echartBar.setOption({
				color : [ '#26B99A' ],
				title : {
					text : 'Produtos ',
					show : true

				},
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {

						return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
					}
				},
				legend : {
					x : 500,
					show : true,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					boundaryGap : [ 0, 0.01 ],
					axisLabel : {
						formatter : function(val) {
							return valorFormater2(val)
						},

					}
				} ],
				grid : {
					x : x_bar,
					y : y_bar,
					x2 : x2_bar,
					y2 : y2_bar,
					width : widht_bar
				},
				yAxis : [ {
					type : 'category',
					data : desc,
					axisLabel : {
						textStyle : {
							fontSize : 10
						},
						splitAreaType : {
							show : true
						},
						margin : 3

					}
				} ],
				series : [ {
					name : 'Valor R$',
					type : 'bar',
					data : qtd,
					barGap : "20%"
				} ]
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

	$(window).trigger('resize');
}

function dashBairrosLista(consulta) {// Produtos com mais faturamento

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$("#dash_vendasbairro").html("");

	$(window).trigger('resize');

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashListaBairros',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana,
			consulta : consulta

		},
		success : function(data) {

			var dataset = [];
			var desc = [];
			var qtd = [];
			var val = [];
var title;
			var serieobj;

			for (t = 0; t < data.length; t++) {

				desc[desc.length] = data[t].desc;
				qtd[qtd.length] = data[t].qtd;
				val[val.length] = data[t].valtotal;

			}

			desc.reverse();
			qtd.reverse();
			val.reverse();

			if (consulta == "valor") {
				title = "Pedidos por bairro - Faturamento"
				serieobj = {
					name : 'Valor R$',
					type : 'bar',
					data : val,
					barGap : "20%"
				}

			} else {
				title = "Pedidos por bairro - Quantidade"
				serieobj = {
					name : 'Quantidade',
					type : 'bar',
					data : qtd,
					barGap : "20%"
				}

			}

			var echartBar = echarts.init(document.getElementById('dash_vendasbairro'));

			echartBar.setOption({
				color : [ '#26B99A' ],
				title : {
					text : title,
					show : true

				},
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {
						if (consulta == "valor") {
							return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
						} else {
							return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
						}
					}
				},
				legend : {
					x : 500,
					show : true,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					boundaryGap : [ 0, 0.01 ],
					axisLabel : {
						formatter : function(val) {
							
							if (consulta == "valor") {
								return valorFormater2(val)
							} else {
								return valorFormater(val)
							}
							
							
						},

					}
				} ],
				grid : {
					x : x_bar,
					y : y_bar,
					x2 : x2_bar,
					y2 : y2_bar,
					width : widht_bar
				},
				yAxis : [ {
					type : 'category',
					data : desc,
					axisLabel : {
						textStyle : {
							fontSize : 10
						},
						splitAreaType : {
							show : true
						},
						margin : 3

					}
				} ],
				series : [ serieobj ]
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

	$(window).trigger('resize');
}

function dashInfosBasicas() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashInfosBasicas',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana,

		},
		success : function(data) {

			$("#lbldash_qtdped").val(data.qtdped);
			$("#lbldash_valtotal").val(data.val_total);
			$("#lbldash_media").val(data.media);
			$("#lbldash_temp1").val(data.tempo_estimado_desejado_medio);
			$("#lbldash_temp2").val(data.tempo_estimado_entrega_medio);
			$("#lbldash_temp3").val(data.tempo_resposta_ped);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function dashPedidoHora() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashHoraPed',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana

		},
		success : function(data) {

			var labels = [];
			var datadash = [];

			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].horaformated;
				datadash[datadash.length] = data[t].qtd;

			}

			$("#dash_pedhora_holder").html("");
			$("#dash_pedhora_holder").html("<canvas id=\"dash_pedhora\"></canvas>");

			var ctx = document.getElementById("dash_pedhora");

			var lineChart = new Chart(ctx, {
				type : 'line',
				data : {
					labels : labels,
					datasets : [ {
						label : "Pedidos",
						backgroundColor : "rgba(38, 185, 154, 0.31)",
						borderColor : "rgba(38, 185, 154, 0.7)",
						pointBorderColor : "rgba(38, 185, 154, 0.7)",
						pointBackgroundColor : "rgba(38, 185, 154, 0.7)",
						pointHoverBackgroundColor : "#fff",
						pointHoverBorderColor : "rgba(220,220,220,1)",
						pointBorderWidth : 1,
						data : datadash
					} ]
				},
			});

			var options = {
				legend : false,
				responsive : false
			};

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function dashPedidoDia() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashDayPed',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana

		},
		success : function(data) {

			var labels = [];
			var datadash = [];

			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].dia;
				datadash[datadash.length] = data[t].qtd;

			}

			$("#dash_peddia_holder").html("");
			$("#dash_peddia_holder").html("<canvas id=\"dash_peddia\"></canvas>");

			var ctx = document.getElementById("dash_peddia");

			var lineChart = new Chart(ctx, {
				type : 'line',
				data : {
					labels : labels,
					datasets : [ {
						label : "Pedidos",
						backgroundColor : "rgba(38, 185, 154, 0.31)",
						borderColor : "rgba(38, 185, 154, 0.7)",
						pointBorderColor : "rgba(38, 185, 154, 0.7)",
						pointBackgroundColor : "rgba(38, 185, 154, 0.7)",
						pointHoverBackgroundColor : "#fff",
						pointHoverBorderColor : "rgba(220,220,220,1)",
						pointBorderWidth : 1,
						data : datadash
					} ]
				},
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function dashModo() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashPagamento',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana

		},
		success : function(data) {

			var labels = [];
			var datadash = [];
			var backgroundColor = [];
			var hoverBackgroundColor = [];

			var html = "";
			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].desc;
				datadash[datadash.length] = data[t].qtd;
				html = html + "<tr class='linha_add_dash'>";
				html = html + "	<td style=\"height: 30px; !important \">";
				html = html + "		<p>";

				if (t == 0) {
					html = html + "			<i class=\"fa fa-square blue\"></i>&nbsp;&nbsp;" + data[t].desc;
					backgroundColor[backgroundColor.length] = "#3498DB";
					hoverBackgroundColor[hoverBackgroundColor.length] = "#49A9EA";
				} else {
					html = html + "			<i class=\"fa fa-square green\"></i>&nbsp;&nbsp;" + data[t].desc;
					backgroundColor[backgroundColor.length] = "#26B99A";
					hoverBackgroundColor[hoverBackgroundColor.length] = "#36CAAB";

				}

				html = html + "		</p>";
				html = html + "	</td>";
				html = html + "	<td style=\"text-align: center\">" + data[t].qtddf + "</td>";
				html = html + "	<td style=\"text-align: center\">" + data[t].perc + "%</td>";
				html = html + "</tr>";

			}

			html = html + "<tr class='linha_add_dash' ><td>&nbsp<td></tr>";
			$("#dash_pagtable").append(html);

			var options = {
				legend : false,
				responsive : false
			};

			new Chart(document.getElementById("pie_pagamento"), {
				type : 'doughnut',
				tooltipFillColor : "rgba(51, 51, 51, 0.55)",
				data : {
					labels : labels,
					datasets : [ {
						data : datadash,
						backgroundColor : backgroundColor,
						hoverBackgroundColor : hoverBackgroundColor
					} ]
				},
				options : options
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function dashServico() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashServico',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana

		},
		success : function(data) {

			var labels = [];
			var datadash = [];

			var backgroundColor = [];
			var hoverBackgroundColor = [];

			var html = "";
			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].desc;
				datadash[datadash.length] = data[t].qtd;
				html = html + "<tr class='linha_add_dash'>";
				html = html + "	<td style=\"height: 30px; !important \">";
				html = html + "		<p>";

				if (t == 0) {
					html = html + "			<i class=\"fa fa-square blue\"></i>&nbsp;&nbsp;" + data[t].desc;
					backgroundColor[backgroundColor.length] = "#3498DB";
					hoverBackgroundColor[hoverBackgroundColor.lenght] = "#49A9EA";
				} else {
					html = html + "			<i class=\"fa fa-square green\"></i>&nbsp;&nbsp;" + data[t].desc;
					backgroundColor[backgroundColor.length] = "#26B99A";
					hoverBackgroundColor[hoverBackgroundColor.lenght] = "#36CAAB";

				}
				html = html + "		</p>";
				html = html + "	</td>";
				html = html + "	<td style=\"text-align: center\">" + data[t].qtddf + "</td>";
				html = html + "	<td style=\"text-align: center\">" + data[t].perc + "%</td>";
				html = html + "</tr>";

			}

			html = html + "<tr class='linha_add_dash' ><td>&nbsp<td></tr>";
			$("#dash_servtable").append(html);

			var options = {
				legend : false,
				responsive : false
			};

			new Chart(document.getElementById("pie_servico"), {
				type : 'doughnut',
				tooltipFillColor : "rgba(51, 51, 51, 0.55)",
				data : {
					labels : labels,
					datasets : [ {
						data : datadash,
						backgroundColor : backgroundColor,
						hoverBackgroundColor : hoverBackgroundColor
					} ]
				},
				options : options
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function dashProdDiaSingle() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	var id_prod = $("#id_produto_listagem").val();

	if (id_prod != "") {

		$.blockUI({
			message : 'Carregando...'
		});

		$.ajax({
			type : "POST",
			url : "home?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'dashProdDiaSingle',
				data_pedido_ini : data_pedido_ini,
				data_pedido_fim : data_pedido_fim,
				cod_bairro : cod_bairro,
				hora_final : hora_final,
				hora_inicial : hora_inicial,
				flag_servico : flag_servico,
				dias_semana : dias_semana,
				id_prod : id_prod

			},
			success : function(data) {

				var labels = [];
				var datadash = [];

				for (t = 0; t < data.length; t++) {

					labels[labels.length] = data[t].dia;
					datadash[datadash.length] = data[t].qtd;

				}

				$("#dash_proddia_holder").html("");
				$("#dash_proddia_holder").html("<canvas id=\"dash_proddia\"></canvas>");

				var ctx = document.getElementById("dash_proddia");

				var lineChart = new Chart(ctx, {
					type : 'line',
					data : {
						labels : labels,
						datasets : [ {
							label : "Unidades",
							backgroundColor : "rgba(38, 185, 154, 0.31)",
							borderColor : "rgba(38, 185, 154, 0.7)",
							pointBorderColor : "rgba(38, 185, 154, 0.7)",
							pointBackgroundColor : "rgba(38, 185, 154, 0.7)",
							pointHoverBackgroundColor : "#fff",
							pointHoverBorderColor : "rgba(220,220,220,1)",
							pointBorderWidth : 1,
							data : datadash
						} ]
					},
				});

				$.unblockUI();

			},
			error : function(msg) {
				$.unblockUI();
				alert("Erro: " + msg.msg);
			}
		});
	}
}

function dashProdHora_single() {

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	var id_prod = $("#id_produto_listagem").val();

	if (id_prod != "") {

		$.blockUI({
			message : 'Carregando...'
		});

		$.ajax({
			type : "POST",
			url : "home?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'dashProdHoraSingle',
				data_pedido_ini : data_pedido_ini,
				data_pedido_fim : data_pedido_fim,
				cod_bairro : cod_bairro,
				hora_final : hora_final,
				hora_inicial : hora_inicial,
				flag_servico : flag_servico,
				dias_semana : dias_semana,
				id_prod : id_prod

			},
			success : function(data) {

				var labels = [];
				var datadash = [];

				for (t = 0; t < data.length; t++) {

					labels[labels.length] = data[t].horaformated;
					datadash[datadash.length] = data[t].qtd;

				}

				// dash_proddia_holder,dash_prodhora_holder

				$("#dash_prodhora_holder").html("");
				$("#dash_prodhora_holder").html("<canvas id=\"dash_prodhora\"></canvas>");

				var ctx = document.getElementById("dash_prodhora");

				var lineChart = new Chart(ctx, {
					type : 'line',
					data : {
						labels : labels,
						datasets : [ {
							label : "Unidades",
							backgroundColor : "rgba(38, 185, 154, 0.31)",
							borderColor : "rgba(38, 185, 154, 0.7)",
							pointBorderColor : "rgba(38, 185, 154, 0.7)",
							pointBackgroundColor : "rgba(38, 185, 154, 0.7)",
							pointHoverBackgroundColor : "#fff",
							pointHoverBorderColor : "rgba(220,220,220,1)",
							pointBorderWidth : 1,
							data : datadash
						} ]
					},
				});

				var options = {
					legend : false,
					responsive : false
				};

				$.unblockUI();

			},
			error : function(msg) {
				$.unblockUI();
				alert("Erro: " + msg.msg);
			}
		});
	}
}

function dashProdBairroSingle() {// Produtos com mais faturamento

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$("#dash_prodbairro").html("");

	var id_prod = $("#id_produto_listagem").val();

	if (id_prod != "") {

		$(window).trigger('resize');

		$.ajax({
			type : "POST",
			url : "home?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'dashProdBairroSingle',
				data_pedido_ini : data_pedido_ini,
				data_pedido_fim : data_pedido_fim,
				cod_bairro : cod_bairro,
				hora_final : hora_final,
				hora_inicial : hora_inicial,
				flag_servico : flag_servico,
				dias_semana : dias_semana,
				id_prod : id_prod

			},
			success : function(data) {

				var dataset = [];
				var desc = [];
				var qtd = [];

				console.log(data);

				for (t = 0; t < data.length; t++) {

					desc[desc.length] = data[t].desc;
					qtd[qtd.length] = data[t].qtd;

				}

				desc.reverse();
				qtd.reverse();

				var echartBar = echarts.init(document.getElementById('dash_prodbairro'));

				echartBar.setOption({
					color : [ '#26B99A' ],
					title : {
						text : 'Local',
						show : true

					},
					tooltip : {
						trigger : 'axis',
						formatter : function(val) {

							return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
						}
					},
					legend : {
						x : 500,
						show : true,
						data : [ 'Produto' ]

					},

					calculable : true,
					xAxis : [ {
						type : 'value',
						boundaryGap : [ 0, 0.01 ],
						axisLabel : {
							formatter : function(val) {
								return valorFormater(val)
							},

						}
					} ],
					grid : {
						x : x_bar,
						y : y_bar,
						x2 : x2_bar,
						y2 : y2_bar,
						width : widht_bar
					},
					yAxis : [ {
						type : 'category',
						data : desc,
						axisLabel : {
							textStyle : {
								fontSize : 10
							},
							splitAreaType : {
								show : true
							},
							margin : 3

						}
					} ],
					series : [ {
						name : 'Unidades',
						type : 'bar',
						data : qtd,
						barGap : "20%"
					} ]
				});

				$.unblockUI();

			},
			error : function(msg) {
				$.unblockUI();
				alert("Erro: " + msg.msg);
			}
		});

		$(window).trigger('resize');
	}
}
