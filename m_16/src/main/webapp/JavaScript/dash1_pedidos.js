//@ sourceURL=dash1_pedidos.js
var theme = {
	color : [ '#26B99A', '#34495E', '#BDC3C7', '#3498DB', '#9B59B6', '#8abb6f', '#759c6a', '#bfd3b7' ],

	title : {
		itemGap : 8,
		textStyle : {
			fontWeight : 'normal',
			color : '#408829'
		}
	},

	dataRange : {
		color : [ '#1f610a', '#97b58d' ]
	},

	toolbox : {
		color : [ '#408829', '#408829', '#408829', '#408829' ]
	},

	tooltip : {
		backgroundColor : 'rgba(0,0,0,0.5)',
		axisPointer : {
			type : 'line',
			lineStyle : {
				color : '#408829',
				type : 'dashed'
			},
			crossStyle : {
				color : '#408829'
			},
			shadowStyle : {
				color : 'rgba(200,200,200,0.3)'
			}
		}
	},

	dataZoom : {
		dataBackgroundColor : '#eee',
		fillerColor : 'rgba(64,136,41,0.2)',
		handleColor : '#408829'
	},
	grid : {
		borderWidth : 0
	},

	categoryAxis : {
		axisLine : {
			lineStyle : {
				color : '#408829'
			}
		},
		splitLine : {
			lineStyle : {
				color : [ '#eee' ]
			}
		}
	},

	valueAxis : {
		axisLine : {
			lineStyle : {
				color : '#408829'
			}
		},
		splitArea : {
			show : true,
			areaStyle : {
				color : [ 'rgba(250,250,250,0.1)', 'rgba(200,200,200,0.1)' ]
			}
		},
		splitLine : {
			lineStyle : {
				color : [ '#eee' ]
			}
		}
	},
	timeline : {
		lineStyle : {
			color : '#408829'
		},
		controlStyle : {
			normal : {
				color : '#408829'
			},
			emphasis : {
				color : '#408829'
			}
		}
	},

	k : {
		itemStyle : {
			normal : {
				color : '#68a54a',
				color0 : '#a9cba2',
				lineStyle : {
					width : 1,
					color : '#408829',
					color0 : '#86b379'
				}
			}
		}
	},
	map : {
		itemStyle : {
			normal : {
				areaStyle : {
					color : '#ddd'
				},
				label : {
					textStyle : {
						color : '#c12e34'
					}
				}
			},
			emphasis : {
				areaStyle : {
					color : '#99d2dd'
				},
				label : {
					textStyle : {
						color : '#c12e34'
					}
				}
			}
		}
	},
	force : {
		itemStyle : {
			normal : {
				linkStyle : {
					strokeColor : '#408829'
				}
			}
		}
	},
	chord : {
		padding : 4,
		itemStyle : {
			normal : {
				lineStyle : {
					width : 1,
					color : 'rgba(128, 128, 128, 0.5)'
				},
				chordStyle : {
					lineStyle : {
						width : 1,
						color : 'rgba(128, 128, 128, 0.5)'
					}
				}
			},
			emphasis : {
				lineStyle : {
					width : 1,
					color : 'rgba(128, 128, 128, 0.5)'
				},
				chordStyle : {
					lineStyle : {
						width : 1,
						color : 'rgba(128, 128, 128, 0.5)'
					}
				}
			}
		}
	},
	gauge : {
		startAngle : 225,
		endAngle : -45,
		axisLine : {
			show : true,
			lineStyle : {
				color : [ [ 0.2, '#86b379' ], [ 0.8, '#68a54a' ], [ 1, '#408829' ] ],
				width : 8
			}
		},
		axisTick : {
			splitNumber : 10,
			length : 12,
			lineStyle : {
				color : 'auto'
			}
		},
		axisLabel : {
			textStyle : {
				color : 'auto'
			}
		},
		splitLine : {
			length : 18,
			lineStyle : {
				color : 'auto'
			}
		},
		pointer : {
			length : '90%',
			color : 'auto'
		},
		title : {
			textStyle : {
				color : '#333'
			}
		},
		detail : {
			textStyle : {
				color : 'auto'
			}
		}
	},
	textStyle : {
		fontFamily : 'Arial, Verdana, sans-serif'
	}
};

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

	$('a[data-toggle="tab"]').on('shown.bs.tab', function(evt) {
		$(window).trigger('resize');
		filtrar(false)
	});
});

function filtrar(troca) {

	$(".linha_add_dash").remove();

	dashServico();
	dashModo();
	dashVendaProdsQtd();
	dashVendaProdsVal();
	dashInfosBasicas();
	dashPedidoHora();
	dashPedidoDia();
	dashBairrosLista();
	if (troca) {
		$('#tabs_dash a[href="#dashboard1"]').tab('show')
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

function dashVendaProdsQtd() {// Produtos mais vendidos

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
			dias_semana : dias_semana

		},
		success : function(data) {

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
					barMaxWidth:30,
					barGap:"20%"
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

function dashVendaProdsVal() {// Produtos com mais faturamento

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
			dias_semana : dias_semana

		},
		success : function(data) {

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

			var echartBar = echarts.init(document.getElementById('dash_vendasprodval'), theme);

			echartBar.setOption({
				title : {
					text : 'Produtos ',
					subtext : 'Produtos mais faturados'
				},
				tooltip : {
					trigger : 'axis'
				},
				legend : {
					x : 100,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					boundaryGap : [ 0, 0.01 ]
				} ],
				yAxis : [ {
					type : 'category',
					data : desc
				} ],
				series : [ {
					name : 'Valor R$',
					type : 'bar',
					data : qtd
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

function dashBairrosLista() {// Produtos com mais faturamento

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
			dias_semana : dias_semana

		},
		success : function(data) {

			var dataset = [];
			var desc = [];
			var qtd = [];
			var val = [];

			for (t = 0; t < data.length; t++) {

				desc[desc.length] = data[t].desc;
				qtd[qtd.length] = data[t].qtd;
				val[val.length] = data[t].valtotal;

			}

			desc.reverse();
			qtd.reverse();

			var echartBar = echarts.init(document.getElementById('dash_vendasbairro'), theme);

			echartBar.setOption({
				title : {
					text : 'Produtos ',
					subtext : 'Produtos mais faturados'
				},
				tooltip : {
					trigger : 'axis'
				},
				legend : {
					x : 100,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					boundaryGap : [ 0, 0.01 ]
				} ],
				yAxis : [ {
					type : 'category',
					data : desc
				} ],
				series : [ {
					name : 'Quantidade:',
					type : 'bar',
					data : qtd
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