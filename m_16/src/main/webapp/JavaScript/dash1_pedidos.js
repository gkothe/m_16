//@ sourceURL=dash1_pedidos.js

var x_bar = 200; // lateral esqerda
var y_bar = 50;
var x2_bar = 80;
var y2_bar = 50;
var widht_bar = "75%";

// para os grafico de dia hora
var x_bar2 = 80;
var y_bar2 = 40;
var x2_bar2 = 40;
var y2_bar2 = 70;
var widht_bar2 = "85%";

$(document).ready(function() {

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

	$("#btn_filtrar").click(function() {

		//$('#tabs_dash a[href="#dashboard1"]').tab('show')
		 filtrar(false);
	});

	$("#btn_filtros_limpar").click(function() {

		limparfiltros();
	});

	$("#btn_filtrar2").click(function() {
		divManage(false);
		filtrarProds(true, false);
	});

	$("#btn_showlist").click(function() {
		divManage(true);
		filtrarProds(false, true);
	});

	$('a[data-toggle="pill"]').on('shown.bs.tab', function(evt) {
		$(window).trigger('resize');
		var target = $(evt.target).attr("href") // activated tab
		if (target == '#dashboard1') {
			filtrar(false);
		} else if (target == '#dashboard2') {
			filtrarProds(true, true);
		} else if (target == '#dashboard3') {
			filtrar(false);
		}
		$(window).trigger('resize');
	});

	$('#tabs_dash a[href="#filtros"]').tab('show')

	dashInfosBasicas();
	divManage(true);
});

function divManage(lista) {

	if (lista == false) {
		$("#div_produnico").show();
		$("#div_prodlista").hide();
		$("#btn_showlist").show();

	} else {
		$("#div_produnico").hide();
		$("#div_prodlista").show();
		$("#btn_showlist").hide();
	}

}

function filtrarProds(single, lista) {

	if (lista) {
		dashVendaProdsQtd('+');
		dashVendaProdsVal('+');
	}
	if (single) {
		$('#div_produnico').animate({
			scrollTop : (0)
		}, 'slow');

		dashProdInfosgerais_single();
		dashProdHora_single('qtd');
		dashProdDiaSingle('qtd');
		dashProdBairroSingle('qtd');
	}
}

function filtrar(troca) {
	$(".linha_add_dash").remove();
	dashMeses('qtd');

	dashServico();
	dashModo();

	dashInfosBasicas();
	dashPedidoHora('qtd');
	dashPedidoDia('qtd');
	dashBairrosLista('qtd');
	filtrarProds(false, true);
	if (troca) {
		$('#tabs_dash a[href="#dashboard1"]').tab('show')
	}

}

function limparfiltros() {

	$("#data_pedido_ini").val("");
	$("#data_pedido_fim").val("");
	$("#cod_bairro").val("");
	$("#hora_final").val("");
	$("#hora_inicial").val("");
	$("#flag_servico").val("");
	// $("#dias_semana").val("");
	$("#dias_semana").multiselect('deselect', [ "1", "2", "3", "4", "5", "6", "7" ]);

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

			var desc = [];
			var qtd = [];

			for (t = 0; t < data.length; t++) {

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
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'
							},
							type : [ 'line', 'bar' ],
							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
				},
				legend : {
					x : 500,
					show : false,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					// scale:true,
					boundaryGap : [ 0, 0.01 ],
					axisLabel : {
						formatter : function(val) {
							return valorFormater(val)
						},

					},
					scale : true,
					min : 0,
					max : data[data.length - 1].qtdmax,
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

			echartBar.on(echarts.config.EVENT.CLICK, eConsole);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

	$(window).trigger('resize');
}

function eConsole(param) {
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'getfullprodname',
			param : param.name
		},
		success : function(data) {

			$("#desc_produto_listagem").val(data.name);
			$("#desc_produto_listagem").blur();

			setTimeout(function() {
				divManage(false);
				filtrarProds(true, false);

				// $('#tabs_dash a[href="#dashboard2"]').tab('show');
				$.unblockUI();
			}, 1500);

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

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
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'
							},
							type : [ 'line', 'bar' ],

							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
				},
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {

						return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
					}
				},
				legend : {
					x : 500,
					show : false,
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

					},
					scale : true,
					min : 0,
					max : data[data.length - 1].qtdmax,
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
			echartBar.on(echarts.config.EVENT.CLICK, eConsole);
			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

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
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'

							},
							type : [ 'line', 'bar' ],
							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
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
					show : false,
					data : [ 'Produto' ]

				},

				calculable : true,
				xAxis : [ {
					type : 'value',
					boundaryGap : [ 0, 0.01 ],
					scale : true,
					min : 0,
					max : (consulta == "valor" ? data[data.length - 1].fatmax : data[data.length - 1].qtdmax),
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

		}
	});

}

function dashPedidoHora(consulta) {

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
			var label;
			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].horaformated;
				if (consulta == "qtd") {
					datadash[datadash.length] = data[t].qtd;
					label = "Pedido - Quantidade";
				} else {
					datadash[datadash.length] = data[t].val_totalprod;
					label = "Pedido - R$";
				}

			}

			$("#dash_pedhora_holder").html("");

			$("#dash_pedhora_holder").html("<div style='height: 350px' id=\"dash_pedhora\"></div>");

			var echartLine = echarts.init(document.getElementById('dash_pedhora'));

			echartLine.setOption({
				title : {
					text : consulta == "qtd" ? 'Quantidade' : "Faturamento",
				},
				color : [ '#26B99A' ],
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {
						if (consulta == "qtd") {
							return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
						} else {
							return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
						}

					}
				},

				legend : {
					x : 220,
					y : 40,
					data : [ 'Intent' ],
					show : false

				},
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'

							},
							type : [ 'line', 'bar' ],
							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
				},
				calculable : true,
				grid : {
					x : x_bar2,
					y : y_bar2,
					x2 : x2_bar2,
					y2 : y2_bar2,
					width : widht_bar2
				},
				xAxis : [ {
					type : 'category',
					boundaryGap : false,
					data : labels,
					axisLabel : {
						rotate : 45,
						interval : 0

					}
				} ],
				yAxis : [ {
					type : 'value',
					axisLabel : {
						formatter : function(val) {
							if (consulta == "qtd") {
								return valorFormater(val)
							} else {
								return valorFormater2(val)
							}

						},
					}
				} ],
				series : [ {
					name : consulta == "qtd" ? 'Quantidade' : "Valor R$",
					type : 'line',
					smooth : true,
					itemStyle : {
						normal : {
							areaStyle : {
								type : 'default'
							}
						}
					},
					data : datadash
				} ]
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

}

function dashPedidoDia(consulta) {

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

				if (consulta == "qtd") {
					datadash[datadash.length] = data[t].qtd;
					label = "Pedido - Quantidade";
				} else {
					datadash[datadash.length] = data[t].val_totalprod;
					label = "Pedido - R$";
				}

			}

			$("#dash_peddia_holder").html("");
			// $("#dash_peddia_holder").html("<canvas
			// id=\"dash_peddia\"></canvas>");
			$("#dash_peddia_holder").html("<div  style='height: 350px' id=\"dash_peddia\"></div>");

			var echartLine = echarts.init(document.getElementById('dash_peddia'));

			echartLine.setOption({
				title : {
					text : consulta == "qtd" ? 'Quantidade' : "Faturamento",
				},
				color : [ '#26B99A' ],
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {
						if (consulta == "qtd") {
							return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
						} else {
							return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
						}

					}
				},
				legend : {

					x : 500,
					show : false,
					data : [ 'Produto' ]
				},
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'
							},
							type : [ 'line', 'bar' ],
							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
				},
				calculable : true,
				grid : {
					x : x_bar2,
					y : y_bar2,
					x2 : x2_bar2,
					y2 : y2_bar2,
					width : widht_bar2
				},
				xAxis : [ {
					type : 'category',
					boundaryGap : false,
					data : labels,
					axisLabel : {
						rotate : 30,
						interval : 0

					}
				} ],
				yAxis : [ {
					type : 'value',
					axisLabel : {
						formatter : function(val) {
							if (consulta == "qtd") {
								return valorFormater(val)
							} else {
								return valorFormater2(val)
							}

						},
					}
				} ],
				series : [ {
					name : consulta == "qtd" ? 'Quantidade' : "Valor R$",
					type : 'line',
					smooth : true,
					itemStyle : {
						normal : {
							areaStyle : {
								type : 'default'
							}
						}
					},
					data : datadash
				} ]
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

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
				html = html + "	<td style=\"text-align: center\">R$ " + data[t].val_totalprod + "</td>";
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

		}
	});

}

function dashProdDiaSingle(consulta) {

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

					if (consulta == "qtd") {
						datadash[datadash.length] = data[t].qtd;
						label = "Pedido - Quantidade";
					} else {
						datadash[datadash.length] = data[t].val_totalprod;
						label = "Pedido - R$";
					}

				}

				$("#dash_proddia_holder").html("");
				$("#dash_proddia_holder").html("<div style='height: 350px'  id=\"dash_proddia\"></div>");

				/*
				 * var ctx = document.getElementById("dash_proddia");
				 * 
				 * var lineChart = new Chart(ctx, { type : 'line', data : {
				 * labels : labels, datasets : [ { label : "Unidades",
				 * backgroundColor : "rgba(38, 185, 154, 0.31)", borderColor :
				 * "rgba(38, 185, 154, 0.7)", pointBorderColor : "rgba(38, 185,
				 * 154, 0.7)", pointBackgroundColor : "rgba(38, 185, 154, 0.7)",
				 * pointHoverBackgroundColor : "#fff", pointHoverBorderColor :
				 * "rgba(220,220,220,1)", pointBorderWidth : 1, data : datadash } ] },
				 * });
				 */

				var echartLine = echarts.init(document.getElementById('dash_proddia'));

				echartLine.setOption({
					title : {
						text : consulta == "qtd" ? 'Quantidade' : "Faturamento",
					},
					color : [ '#26B99A' ],
					tooltip : {
						trigger : 'axis',
						formatter : function(val) {
							if (consulta == "qtd") {
								return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
							} else {
								return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
							}

						}
					},
					legend : {
						x : 500,
						show : false,
						data : [ 'Produto' ]
					},
					toolbox : {
						show : true,
						feature : {
							magicType : {
								show : true,
								title : {
									line : 'Linha',
									bar : 'Barra'

								},
								type : [ 'line', 'bar' ],
								option : {
									line : {
										smooth : true,
										itemStyle : {
											normal : {
												areaStyle : {
													type : 'default'
												}
											}
										}

									}

								}
							}
						}
					},
					grid : {
						x : x_bar2,
						y : y_bar2,
						x2 : x2_bar2,
						y2 : y2_bar2,
						width : widht_bar2
					},
					calculable : true,
					xAxis : [ {
						type : 'category',
						boundaryGap : false,
						data : labels,
						axisLabel : {
							rotate : 45,
							interval : 0

						}
					} ],
					yAxis : [ {
						type : 'value',
						axisLabel : {
							formatter : function(val) {
								if (consulta == "qtd") {
									return valorFormater(val)
								} else {
									return valorFormater2(val)
								}

							},
						}
					} ],
					series : [ {
						name : consulta == "qtd" ? 'Quantidade' : "Valor R$",
						type : 'line',
						smooth : true,
						itemStyle : {
							normal : {
								areaStyle : {
									type : 'default'
								}
							}
						},
						data : datadash
					} ]
				});

				$.unblockUI();

			},
			error : function(msg) {
				$.unblockUI();

			}
		});
	}
}

function dashProdHora_single(consulta) {

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
					if (consulta == "qtd") {
						datadash[datadash.length] = data[t].qtd;
						label = "Pedido - Quantidade";
					} else {
						datadash[datadash.length] = data[t].val_totalprod;
						label = "Pedido - R$";
					}

				}

				// dash_proddia_holder,dash_prodhora_holder

				$("#dash_prodhora_holder").html("");
				$("#dash_prodhora_holder").html("<div style='height: 350px' id=\"dash_prodhora\"></div>");
				// $("#dash_prodhora_holder").html("<canvas
				// id=\"dash_prodhora\"></canvas>");

				/*
				 * var ctx = document.getElementById("dash_prodhora");
				 * 
				 * var lineChart = new Chart(ctx, { type : 'line', data : {
				 * labels : labels, datasets : [ { label : "Unidades",
				 * backgroundColor : "rgba(38, 185, 154, 0.31)", borderColor :
				 * "rgba(38, 185, 154, 0.7)", pointBorderColor : "rgba(38, 185,
				 * 154, 0.7)", pointBackgroundColor : "rgba(38, 185, 154, 0.7)",
				 * pointHoverBackgroundColor : "#fff", pointHoverBorderColor :
				 * "rgba(220,220,220,1)", pointBorderWidth : 1, data : datadash } ] },
				 * });
				 */

				var echartLine = echarts.init(document.getElementById('dash_prodhora'));

				echartLine.setOption({
					title : {
						text : consulta == "qtd" ? 'Quantidade' : "Faturamento",
					},
					color : [ '#26B99A' ],
					tooltip : {
						trigger : 'axis',
						formatter : function(val) {
							if (consulta == "qtd") {
								return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
							} else {
								return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
							}

						}
					},
					legend : {
						x : 500,
						show : false,
						data : [ 'Produto' ]
					},
					toolbox : {
						show : true,
						feature : {
							magicType : {
								show : true,
								title : {
									line : 'Linha',
									bar : 'Barra'

								},
								type : [ 'line', 'bar' ],
								option : {
									line : {
										smooth : true,
										itemStyle : {
											normal : {
												areaStyle : {
													type : 'default'
												}
											}
										}

									}

								}
							}
						}
					},
					grid : {
						x : x_bar2,
						y : y_bar2,
						x2 : x2_bar2,
						y2 : y2_bar2,
						width : widht_bar2
					},
					calculable : true,
					xAxis : [ {
						type : 'category',
						boundaryGap : false,
						data : labels,
						axisLabel : {
							rotate : 45,
							interval : 0

						}
					} ],
					yAxis : [ {
						type : 'value',
						axisLabel : {
							formatter : function(val) {
								if (consulta == "qtd") {
									return valorFormater(val)
								} else {
									return valorFormater2(val)
								}

							},
						}
					} ],
					series : [ {
						name : consulta == "qtd" ? 'Quantidade' : "Valor R$",
						type : 'line',
						smooth : true,
						itemStyle : {
							normal : {
								areaStyle : {
									type : 'default'
								}
							}
						},
						data : datadash
					} ]
				});

				$.unblockUI();

			},
			error : function(msg) {
				$.unblockUI();

			}
		});
	}
}

function dashProdInfosgerais_single() {// Produtos com mais faturamento

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

		$(window).trigger('resize');

		$.ajax({
			type : "POST",
			url : "home?ac=ajax",
			dataType : "json",
			async : true,
			data : {
				cmd : 'dashProdInfosgerais_single',
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
				$("#lbldash_qtdprod").val(data.qtd);
				$("#lbldash_valtotalprod").val(data.totalfat);
				$("#lbldash_mediaprod").val(data.media);

				$("#lbldash_valatual").val(data.val_prod);
				$("#lbldash_prodsit").val(data.flag_ativo);

			},
			error : function(msg) {
				$.unblockUI();

			}
		});

		$(window).trigger('resize');
	}
}

function dashProdBairroSingle(consulta) {// Produtos com mais faturamento

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

				for (t = 0; t < data.length; t++) {

					desc[desc.length] = data[t].desc;
					if (consulta == 'qtd')
						qtd[qtd.length] = data[t].qtd;
					else
						qtd[qtd.length] = data[t].valtotal;
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
					toolbox : {
						show : true,
						feature : {
							magicType : {
								show : true,
								title : {
									line : 'Linha',
									bar : 'Barra',

								},
								type : [ 'line', 'bar' ],
								option : {
									line : {
										smooth : true,
										itemStyle : {
											normal : {
												areaStyle : {
													type : 'default'
												}
											}
										}

									}

								}
							}
						}
					},
					tooltip : {
						trigger : 'axis',
						formatter : function(val) {
							if (consulta == 'qtd')
								return val["0"].name + "<br>  Quantidade: " + valorFormater(val["0"].value);
							else
								return val["0"].name + "<br>  Valor R$: " + valorFormater2(val["0"].value);
						}
					},
					legend : {
						x : 500,
						show : false,
						data : [ 'Produto' ]

					},

					calculable : true,
					xAxis : [ {
						type : 'value',
						boundaryGap : [ 0, 0.01 ],
						axisLabel : {
							formatter : function(val) {
								if (consulta == 'qtd')
									return valorFormater(val)
								else
									return valorFormater2(val)
							},

						},
						scale : true,
						min : 0,
						max : (consulta == "qtd" ? data[data.length - 1].qtdmax : data[data.length - 1].fatmax)
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

			}
		});

		$(window).trigger('resize');
	}
}

function dashDia(consulta) {// Produtos com mais faturamento

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	var descdataday = $("#descdataday").val();
	dias_semana = JSON.stringify(dias_semana);

	$("#dash_dias").html("");

	$(window).trigger('resize');

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashDiasDoMes',
			data_pedido_ini : data_pedido_ini,
			data_pedido_fim : data_pedido_fim,
			cod_bairro : cod_bairro,
			hora_final : hora_final,
			hora_inicial : hora_inicial,
			flag_servico : flag_servico,
			dias_semana : dias_semana,
			descdataday : descdataday

		},
		success : function(data) {

			var dataset = [];
			var desc = [];
			var qtd = [];
			var entrega = [];
			var retirada = [];

			for (t = 0; t < data.length; t++) {

				desc[desc.length] = data[t].desc;

				if (consulta == 'qtd') {
					qtd[qtd.length] = data[t].qtd;
					entrega[entrega.length] = data[t].entrega;
					retirada[retirada.length] = data[t].retirada;
				} else {
					qtd[qtd.length] = data[t].total;
					entrega[entrega.length] = data[t].valentregas;
					retirada[retirada.length] = data[t].valretirada;
				}

			}

			var echartBar = echarts.init(document.getElementById('dash_dias'));

			echartBar.setOption({
				color : [ '#9B59B6', '#26B99A', '#3498DB' ],
				title : {
					text : consulta == 'qtd' ? 'Quantidade' : "Faturamento",
					show : true

				},
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'

							},
							type : [ 'line', 'bar' ],
							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
				},
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {

						var html;
						if (consulta == 'qtd') {
							html = val["0"].name + "<br> <div style='height:10px; width:10px; background-color:#9B59B6;color:#9B59B6; display:inline; border-radius:5px ' >&nbsp;()</div>  Pedidos: " + valorFormater(val["0"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#26B99A;color:#26B99A; display:inline; border-radius:5px ' >&nbsp;()</div> Entregas: " + valorFormater(val["1"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#3498DB;color:#3498DB; display:inline; border-radius:5px ' >&nbsp;()</div> Retiradas: " + valorFormater(val["2"].value);
						} else {
							html = val["0"].name + "<br> <div style='height:10px; width:10px; background-color:#9B59B6;color:#9B59B6; display:inline; border-radius:5px ' >&nbsp;()</div>  Pedidos R$: " + valorFormater2(val["0"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#26B99A;color:#26B99A; display:inline; border-radius:5px ' >&nbsp;()</div> Entregas R$: " + valorFormater2(val["1"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#3498DB;color:#3498DB; display:inline; border-radius:5px ' >&nbsp;()</div> Retiradas R$: " + valorFormater2(val["2"].value);

						}

						return html;
					}
				},
				legend : {
					show : false,
					data : [ 'Total de pedidos', 'Entregas', 'Retiradas' ],
				},
				selectedMode : false,
				calculable : false,
				xAxis : [ {
					type : 'category',
					data : desc,
					boundaryGap : [ 0, 0.01 ],
					textStyle : {
						fontSize : 10
					},
					splitAreaType : {
						show : true
					},
					axisLabel : {
						rotate : 45,
						interval : 0
					}

				} ],
				grid : {
					x : x_bar2,
					y : y_bar2,
					x2 : x2_bar,
					y2 : 105,
					width : widht_bar2,

				},
				yAxis : [ {
					type : 'value',
					axisLabel : {
						formatter : function(val) {
							if (consulta == "qtd") {
								return valorFormater(val)
							} else {
								return valorFormater2(val)
							}

						},

						margin : 3
					}
				} ],

				series : [ {
					name : 'Total pedidos',
					type : 'bar',
					data : qtd,
					barGap : "20%",
				}, {
					name : 'Entrega pedidos',
					type : 'bar',
					data : entrega,
					barGap : "20%",
				}, {
					name : 'Retirada pedidos',
					type : 'bar',
					data : retirada,
					barGap : "20%",
				}

				]
			});

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

	$(window).trigger('resize');
}

function dashMeses(consulta) {// Produtos com mais faturamento

	var data_pedido_ini = $("#data_pedido_ini").val();
	var data_pedido_fim = $("#data_pedido_fim").val();
	var cod_bairro = $("#cod_bairro").val();
	var hora_final = $("#hora_final").val();
	var hora_inicial = $("#hora_inicial").val();
	var flag_servico = $("#flag_servico").val();
	var dias_semana = $("#dias_semana").val();
	dias_semana = JSON.stringify(dias_semana);

	$("#dash_meses").html("");

	$(window).trigger('resize');

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashMeses',
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

			var entrega = [];
			var retirada = [];

			for (t = 0; t < data.length; t++) {

				desc[desc.length] = data[t].desc;

				if (consulta == 'qtd') {
					qtd[qtd.length] = data[t].qtd;
					entrega[entrega.length] = data[t].entrega;
					retirada[retirada.length] = data[t].retirada;
				} else {
					qtd[qtd.length] = data[t].total;
					entrega[entrega.length] = data[t].valentregas;
					retirada[retirada.length] = data[t].valretirada;
				}

			}

			desc.reverse();
			qtd.reverse();
			entrega.reverse();
			retirada.reverse();

			var echartBar = echarts.init(document.getElementById('dash_meses'));

			echartBar.setOption({
				color : [ '#9B59B6', '#26B99A', '#3498DB' ],
				title : {
					text : consulta == 'qtd' ? 'Quantidade' : "Faturamento",
					show : true

				},
				toolbox : {
					show : true,
					feature : {
						magicType : {
							show : true,
							title : {
								line : 'Linha',
								bar : 'Barra'

							},
							type : [ 'line', 'bar' ],
							option : {
								line : {
									smooth : true,
									itemStyle : {
										normal : {
											areaStyle : {
												type : 'default'
											}
										}
									}

								}

							}
						}
					}
				},
				tooltip : {
					trigger : 'axis',
					formatter : function(val) {

						var html;
						if (consulta == 'qtd') {
							html = val["0"].name + "<br> <div style='height:10px; width:10px; background-color:#9B59B6;color:#9B59B6; display:inline; border-radius:5px ' >&nbsp;()</div>  Pedidos: " + valorFormater(val["0"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#26B99A;color:#26B99A; display:inline; border-radius:5px ' >&nbsp;()</div> Entregas: " + valorFormater(val["1"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#3498DB;color:#3498DB; display:inline; border-radius:5px ' >&nbsp;()</div> Retiradas: " + valorFormater(val["2"].value);
						} else {
							html = val["0"].name + "<br> <div style='height:10px; width:10px; background-color:#9B59B6;color:#9B59B6; display:inline; border-radius:5px ' >&nbsp;()</div>  Pedidos R$: " + valorFormater2(val["0"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#26B99A;color:#26B99A; display:inline; border-radius:5px ' >&nbsp;()</div> Entregas R$: " + valorFormater2(val["1"].value);
							html = html + "<br> <div style='height:10px; width:10px; background-color:#3498DB;color:#3498DB; display:inline; border-radius:5px ' >&nbsp;()</div> Retiradas R$: " + valorFormater2(val["2"].value);

						}

						return html;
					}
				},
				legend : {
					show : false,
					data : [ 'Total de pedidos', 'Entregas', 'Retiradas' ],
				},
				selectedMode : false,

				calculable : true,
				xAxis : [ {
					type : 'category',
					data : desc,
					boundaryGap : [ 0, 0.01 ],
					textStyle : {
						fontSize : 10
					},
					splitAreaType : {
						show : true
					},
					axisLabel : {
						rotate : 45,
						interval : 0
					}

				} ],
				grid : {
					x : x_bar2,
					y : y_bar2,
					x2 : x2_bar,
					y2 : 95,
					width : widht_bar2,

				},
				yAxis : [ {
					type : 'value',
					axisLabel : {
						formatter : function(val) {
							if (consulta == "qtd") {
								return valorFormater(val)
							} else {
								return valorFormater2(val)
							}

						},

						margin : 3
					}
				} ],
				series : [ {
					name : 'Total pedidos',
					type : 'bar',
					data : qtd,
					barGap : "20%"

				}, {
					name : 'Entrega pedidos',
					type : 'bar',
					data : entrega,
					barGap : "20%"
				}, {
					name : 'Retirada pedidos',
					type : 'bar',
					data : retirada,
					barGap : "20%"
				}

				]
			}).on(echarts.config.EVENT.CLICK, loadDias);
			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();

		}
	});

	$(window).trigger('resize');
}

function loadDias(param) {
	$("#dash_diastitle").html(param.name);
	$("#modal_dashdias").modal("show");
	$("#descdataday").val(param.name);
	$("#dash_dias").html("");
	$.blockUI({
		message : 'Aguarde...'
	});
	setTimeout(function() {
		dashDia('qtd');
		$.unblockUI();
	}, 500)

}
