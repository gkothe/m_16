//@ sourceURL=wizard_bairros_horarios.js

var horarios = [];

$(document).ready(function() {

	loadBairrosWizard();
	loadDiasWizard();

	$(".hora").inputmask("h:s", {
		"placeholder" : "00:00"
	});

	/*
	 * $('.hora').timepicker({ minuteStep : 1, showSeconds : false, showMeridian :
	 * false, defaultTime : false });
	 */

	$("#marca_bairros").click(function() {

		$('.bairripick').each(function() {
			this.checked = true;
		});

	});
	$("#btn_getgrade").click(function() {

		var nome = "Grade de horários"
		var w = window.open("home/"+nome+".pdf?ac=rel_gradehorarios", '_blank');
	    
	});
	$('[data-toggle="tooltip"]').tooltip();

	$("#desmarca_bairros").click(function() {

		$('.bairripick').each(function() {
			this.checked = false;
		});
	});

	$("#p_add_periodo").click(function() {
		addHorario();
	});

	$("#btn_prox_0").click(function() {
		$('#tabs_horarios a[href="#bairros"]').tab('show')

	});

	$("#btn_prox_3").click(function() {
		if (confirm("Tem certeza que deseja continuar?")) {
			salvarconfigs();
		}

	});

	$("#btn_prox_1").click(function() {
		$('#tabs_horarios a[href="#diashoras"]').tab('show')
	});

	$("#btn_prox_2").click(function() {
		$('#tabs_horarios a[href="#confir"]').tab('show')
	});

	$('#tabs_horarios a[href="#info"]').tab('show')

	var tabela = $('#table_horarios');

	$(tabela).bootstrapTable().on('dbl-click-cell.bs.table', function(field, value, row, element) {
		var id = element.seq_movimento;
		var form = document.getElementById('form');

	});

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_bairros')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_bairros').bootstrapTable('updateFooter');
	});

	$("#row_da_tabelabairro").find(".fixed-table-toolbar").append("<div class=\"pull-left \" style=\"padding-top: 10px;\"> <input type=\"checkbox\" id='check_custommode'> <label >Modo customizado</label></div>");

});

function removerPeriodo(periodo) {
	console.log(periodo);
	console.log(horarios);
	for (t = 0; t < horarios.length; t++) {
		if (horarios[t].id_horario == periodo) {
			horarios[t] = null;
		}
	}

	horarios = horarios.filter(function(n) {
		return n != undefined
	});

	$('#table_horarios').bootstrapTable('load', horarios);
	$('#table_horarios').bootstrapTable('resetView');

}

function salvarconfigs() {
	$.blockUI({
		message : 'Salvando...'
	});
	var bairrosbox = [];

	$('.bairripick').each(function() {
		var sThisVal = (this.checked ? $(this).val() : "");
		if (sThisVal != "")
			bairrosbox.push(sThisVal);
	});

	var diassemana = [];

	$('.diapic').each(function() {
		var sThisVal = (this.checked ? $(this).val() : "");
		if (sThisVal != "")
			diassemana.push(sThisVal);
	});

	var horariosjson = JSON.stringify(horarios);
	var tipoopc = $('input[name=opc_tipoopc]:checked').val();

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "salvarConfigsHorariosBairros",
			bairrosbox : bairrosbox.toString(),
			// diassemana : diassemana.toString(),
			horariosjson : horariosjson,
			tipoopc : tipoopc

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			if (data.msg == 'ok') {

				
				sysMsg("Configurações de bairros/horários salvos!",'M')
				location.reload(true);

			} else {
				sysMsg(data.erro,'E')
			}
			$.unblockUI();
		},
		error : function(data) {
			$.unblockUI();
		}
	});

}

var counter = 1;
function addHorario() {
	counter++;
	try {

		if ($("#b_hora_inicial").val() != "" && $("#b_hora_final").val() != "") {

			var diassemana = [];
			$('.diapic').each(function() {
				var sThisVal = (this.checked ? $(this).val() : "");
				if (sThisVal != "")
					diassemana.push(sThisVal);
			});
			var horariosstr = $("#b_hora_inicial").val() + " - " + $("#b_hora_final").val();

			var horaini_teste = parseFloat($("#b_hora_inicial").val().replace(":", "."));
			var horafim_teste = parseFloat($("#b_hora_final").val().replace(":", "."));

			for (t = 0; t < horarios.length; t++) {
				var horario = horarios[t];

				for (a = 0; a < diassemana.length; a++) {

					if (horario["HORARIO_" + diassemana[a]] != undefined && horario["HORARIO_" + diassemana[t]] != "") {
						horastrteste = horario["HORARIO_" + diassemana[t]]
						if (horastrteste != undefined) {
							var horas_jadd = horastrteste.split(" - ");
							var p_ini = parseFloat(horas_jadd[0].replace(":", "."));
							var p_fim = parseFloat(horas_jadd[1].replace(":", "."));

							if (horaini_teste >= p_ini && horaini_teste <= p_fim) {
								throw "O horario inicial adicionado conflita com o horario de " + horarios[t]["HORARIO_" + diassemana[t]];
							}

							if (horafim_teste >= p_ini && horafim_teste <= p_fim) {
								throw "O horario inicial adicionado conflita com o horario de " + horarios[t]["HORARIO_" + diassemana[t]];
							}
						}
					}
				}

			}

			var horario = new Object();
			horario.id_horario = counter;
			for (t = 0; t < diassemana.length; t++) { // o código esta fixo na
														// tabela, horario_1 =
														// segunda-fera, e etc
				horario["HORARIO_" + diassemana[t]] = horariosstr;
			}

			horarios.push(horario);

			$('#table_horarios').bootstrapTable('load', horarios);
			$('#table_horarios').bootstrapTable('resetView');
			$("#b_hora_inicial").val(""); 
			$("#b_hora_final").val("");
		} else {
			throw "Horário invalido! Preencha ambos os campos.";
		}

	} catch (err) {
		sysMsg(err,'E')
		
	}
}

function addHorario2() {

	try {

		if ($("#b_hora_inicial").val() != "" && $("#b_hora_final").val() != "") {

			var horaini = "";
			var horafim = "";

			if ($("#b_hora_inicial").val().length == 4) {
				horaini = "0" + $("#b_hora_inicial").val();
			} else {
				horaini = $("#b_hora_inicial").val();
			}

			if ($("#b_hora_final").val().length == 4) {
				horafim = "0" + $("#b_hora_final").val();
			} else {
				horafim = $("#b_hora_final").val();
			}

			var horaini_teste = parseFloat(horaini.replace(":", "."));
			var horafim_teste = parseFloat(horafim.replace(":", "."));

			if (horaini_teste >= horafim_teste) {

				throw "Horário invalido! A hora inicial deve ser maior que a hora final.";

			} else {

				var horario = new Object();

				for (t = 0; t < horarios.length; t++) {

					var p_ini = parseFloat(horarios[t]["HORARIO_INI"].replace(":", "."));
					var p_fim = parseFloat(horarios[t]["HORARIO_FIM"].replace(":", "."));

					if (horaini_teste >= p_ini && horaini_teste <= p_fim) {
						throw "O horario inicial adicionado conflita com o horario de " + horarios[t]["HORARIO_INI"] + " até " + horarios[t]["HORARIO_FIM"];
					}

					if (horafim_teste >= p_ini && horafim_teste <= p_fim) {
						throw "O horario inicial adicionado conflita com o horario de " + horarios[t]["HORARIO_INI"] + " até " + horarios[t]["HORARIO_FIM"];
					}

				}

				horario["HORARIO_INI"] = horaini;
				horario["HORARIO_FIM"] = horafim;

				horarios.push(horario);

				$('#table_horarios').bootstrapTable('load', horarios);
				$('#table_horarios').bootstrapTable('resetView');

				$("#b_hora_inicial").val("");
				$("#b_hora_final").val("");
			}
		} else {
			throw "Horário invalido! Preencha ambos os campos.";
		}

	} catch (err) {
		sysMsg(err,'E')
		
	}
}

function loadBairrosWizard() {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "loadBairrosWizard",

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			var html = [];

			for (t = 0; t < data.length; t++) {
				html.push("<tr> <td style=\"padding-right: 10px;   min-width: 200px;\" > <label> <input type=\"checkbox\" class=\"bairripick\" value=\"" + data[t].cod_bairro + "\"> " + data[t].desc_bairro + "</label>  	</td>");
				if (data[t + 1] != undefined) {
					html.push("<td style=\"padding-right: 10px; min-width: 200px;\" > <label> <input type=\"checkbox\" class=\"bairripick\" value=\"" + data[t + 1].cod_bairro + "\"> " + data[t + 1].desc_bairro + "</label>  	</td>");
					t++;
				}
				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px; min-width: 200px; \"> <label> <input type=\"checkbox\"  class=\"bairripick\" value=\"" + data[t + 1].cod_bairro + "\"> " + data[t + 1].desc_bairro + "</label> 	</td> ");
					t++;
				}
				html.push(" </tr>");
			}
			$("#tab_wizard_bairros").html(html.toString());
		},
		error : function(data) {
			
		}
	});

}

function btnFormater_horario(value, row, index) {

	counter++;
	var html = "";
	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Remover este período\" data-trigger=\"focus\"  onclick='removerPeriodo(" + row.id_horario + ")'  class=\"btn btn-default  btn_tabela \"     data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\"fa fa-trash-o\"></i>";
	html = html + "</a>";

	return html;
}
function loadDiasWizard() {

	$.ajax({
		type : 'POST',
		url : "home?ac=ajax",
		data : {
			cmd : "loadDiasSemana",

		},
		async : true,
		dataType : 'json',
		success : function(data) {

			var html = [];

			for (t = 0; t < data.length; t++) {
				html.push("<tr> <td style=\"padding-right: 10px;   min-width: 200px;\" > <label> <input type=\"checkbox\" class=\"diapic\" value=\"" + data[t].COD_DIA + "\"> " + data[t].DESC_DIA + "</label>  	</td>");
				if (data[t + 1] != undefined) {
					html.push("<td style=\"padding-right: 10px; min-width: 200px;\" > <label> <input type=\"checkbox\" class=\"diapic\" value=\"" + data[t + 1].COD_DIA + "\"> " + data[t + 1].DESC_DIA + "</label>  	</td>");
					t++;
				}
				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px; min-width: 200px; \"> <label> <input type=\"checkbox\"  class=\"diapic\" value=\"" + data[t + 1].COD_DIA + "\"> " + data[t + 1].DESC_DIA + "</label> 	</td> ");
					t++;
				}
				html.push(" </tr>");
			}
			$("#tab_wizard_dias").html(html.toString());
		},
		error : function(data) {
			
		}
	});

}