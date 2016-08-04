//@ sourceURL=config_empresa.js
var rstimer = false;
$(window).resize(function() {
	if (rstimer) {
		clearTimeout(rstimer);
	}

	rstimer = setTimeout(function() {
		$('#table_bairros').bootstrapTable('resetView');
	}, 500);
});

var horarios_bairros = "";
var bairro_edicao = "";
var id_horariomax = "";
$(document).ready(function() {
	horarios_bairros = "";
	bairro_edicao = "";

	$("#val_min_entrega").autoNumeric('init', numerico);
	$("#val_padrao_tele").autoNumeric('init', numerico);
	$("#b_val_tele").autoNumeric('init', numerico);
	
	$('.lbl_save').blink({
		delay : 600
	});	
	
	

	
	
//	$("#lbl_save").show();	
	
	$('.hora').timepicker({
		minuteStep : 1,
		showSeconds : false,
		showMeridian : false,
		defaultTime : false
	});

	$('.keep-open', $('.fixed-table-toolbar')).prependTo($('#colunas'));
	$('.fixed-table-toolbar').remove();
	$('#colunas').addClass("fixed-table-toolbar");
	$('label', $('#colunas')).css('padding', '3px 20px').css('font-weight', 'normal');
	$('.dropdown-menu', $('#colunas')).css('min-width', '180px');
	$('th', $('#table_notas')).css('background-color', 'rgb(248, 248, 248)');

	var tabela = $('#table_bairros');

	$(tabela).bootstrapTable().on('dbl-click-cell.bs.table', function(field, value, row, element) {
		var id = element.seq_movimento;
		var form = document.getElementById('form');
		Enviar(form, $("#app_root").val() + "?ac=nfse&id=" + id);

	});

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_bairros')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_bairros').bootstrapTable('updateFooter');
	});

	var tabela = $('#table_horarios');

	$(tabela).bootstrapTable().on('dbl-click-cell.bs.table', function(field, value, row, element) {
		var id = element.seq_movimento;
		var form = document.getElementById('form');
		Enviar(form, $("#app_root").val() + "?ac=nfse&id=" + id);

	});

	$(tabela).on('sort.bs.table reset-view.bs.table post-body.bs.table', function() {
		$('th', $('#table_bairros')).css('background-color', 'rgb(248, 248, 248)');
	});

	$(tabela).on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function() {
		$('#table_bairros').bootstrapTable('updateFooter');
	});

	$("#row_da_tabelabairro").find(".fixed-table-toolbar").append("<div class=\"pull-left \" style=\"padding-top: 10px;\"> <input type=\"checkbox\" id='check_custommode'> <label >Modo customizado</label></div>");



	$("#check_custommode").change(function() {
		ativaWarningSalvar();
	});
	
	
	$(".warn-change").change(function(){
		ativaWarningSalvar();
	});
	
	$("#p_add_bairro").click(function() {
		addBairro();
	});

	$(".salvaconf").click(function() {
		salvarTela();
	});

	$("#flag_online").change(function() {
		testComboOnline();
		ativaWarningSalvar();
	});

	$("#b_val_tele").change(function() {
		if (bairro_edicao != "" && bairro_edicao != undefined) {
			bairro_edicao.val_tele = $("#b_val_tele").autoNumeric('get');
		}
	});

	$("#flag_telebairro").change(function() {
		if (bairro_edicao != "" && bairro_edicao != undefined) {

			if ($("#flag_telebairro").is(":checked")) {
				bairro_edicao.flag_telebairro = 'S'
			} else {
				bairro_edicao.flag_telebairro = 'N'
			}
			;

			ativaWarningSalvar();
		}
	});

	$("#p_add_periodo").click(function() {
		addHorario();
	});

	$('#b_modal_bairro').on('hidden.bs.modal', function(e) {
		bairro_edicao = "";
	})

	loadCidade();
	carregaBairros();
	loadBairrosParam();
	loadDiasSemana();

	loadDados();

	$('[data-toggle="tooltip"]').tooltip();
});

function btnFormater(value, row, index) {
	var html = "";
	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Editar horários para este bairro\" data-trigger=\"focus\"  onclick='loadDadosBairro(" + row.cod_bairro + ")'  class=\"btn btn-default  btn_tabela \" data-valor=\"" + row.cod_bairro + "\"     data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\"fa fa-pencil-square-o\"></i>";
	html = html + "</a>";

	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Remover este bairro\" data-trigger=\"focus\"  onclick='removerBairro(" + row.cod_bairro + ",\"" + row.desc_bairro + "\")'  class=\"btn btn-default  btn_tabela \" data-valor=\"" + row.cod_bairro + "\"     data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\"fa fa-trash-o\"></i>";
	html = html + "</a>";

	return html;
}

function btnFormater_horario(value, row, index) {
	var html = "";
	html = html + "<a  data-toggle=\"tooltip\" tabindex=\"0\" role=\"button\" title=\"Remover este período\" data-trigger=\"focus\"  onclick='removerPeriodo(" + row.id_horario + ")'  class=\"btn btn-default  btn_tabela \"     data-container=\"body\" data-placement=\"left\" >";
	html = html + "<i class=\"fa fa-trash-o\"></i>";
	html = html + "</a>";

	return html;
}

function removerBairro(bairro, desc) {

	if (confirm("Tem certeza que deseja remover o bairro " + desc + "?")) {

		for (t = 0; t < horarios_bairros.length; t++) {

			if (horarios_bairros[t].cod_bairro == bairro) {
				horarios_bairros[t] = null;
			}

		}

		horarios_bairros = horarios_bairros.filter(function(n) {
			return n != undefined
		});

		$('#table_bairros').bootstrapTable('load', horarios_bairros);
		$('#table_bairros').bootstrapTable('resetView');
		
		ativaWarningSalvar();
	}

}

function ativaWarningSalvar(){
	$(".lbl_save").show();
}

function removerPeriodo(periodo) {

	var casa_dia = 0;
	for (t = 0; t < bairro_edicao.dias.length; t++) {

		if (bairro_edicao.dias[t].cod_dia == $("input[name=radioDiasemana]:radio").filter(':checked').val()) {
			casa_dia = t;
			break;
		}
	}

	for (t = 0; t < bairro_edicao.dias[casa_dia].horarios.length; t++) {

		if (bairro_edicao.dias[casa_dia].horarios[t].id_horario == periodo) {
			bairro_edicao.dias[casa_dia].horarios[t] = null;
		}

	}

	bairro_edicao.dias[casa_dia].horarios = bairro_edicao.dias[casa_dia].horarios.filter(function(n) {
		return n != undefined
	});

	$('#table_horarios').bootstrapTable('load', bairro_edicao.dias[casa_dia].horarios);
	$('#table_horarios').bootstrapTable('resetView');
	ativaWarningSalvar();

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

			var html = "<option value=''  > Escolha um bairro  </option>  ";

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

function testComboOnline() {

	if ($("#flag_online").val() == 'S') {

		$("#flag_online").css("background-color", 'rgb(0, 255, 0)');
	} else if ($("#flag_online").val() == 'N') {

		$("#flag_online").css("background-color", 'red');
	}

}

function salvarTela() {

	var desc_razaosocial = $("#desc_razaosocial").val();
	var desc_fantasia = $("#desc_fantasia").val();
	var cod_cidade = $("#cod_cidade").val();
	var num_telefone = $("#num_telefone").val();
	var desc_endereco = $("#desc_endereco").val();
	var desc_num = $("#desc_num").val();
	var desc_mail = $("#desc_mail").val();
	var desc_complemento = $("#desc_complemento").val();
	var val_min_entrega = $("#val_min_entrega").autoNumeric('get');
	var val_padrao_tele = $("#val_padrao_tele").autoNumeric('get');
	var flag_custom = "";

	if ($("#check_custommode").is(":checked")) {
		flag_custom = "S"
	} else {
		flag_custom = "N"
	}

	var flag_online = $("#flag_online").val();
	var bairros = JSON.stringify(horarios_bairros);

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'salvarConfigsEmp',
			desc_razaosocial : desc_razaosocial,
			desc_fantasia : desc_fantasia,
			cod_cidade : cod_cidade,
			num_telefone : num_telefone,
			desc_endereco : desc_endereco,
			desc_num : desc_num,
			desc_complemento : desc_complemento,
			val_min_entrega : val_min_entrega,
			val_padrao_tele : val_padrao_tele,
			flag_online : flag_online,
			flag_custom : flag_custom,
			bairros : bairros,
			desc_mail:desc_mail
			

		},
		success : function(data) {

			if (data.msg == 'ok') {

				alert("Dado salvos!");
				$(".lbl_save").hide();
				location.reload(true);
				
			} else {

				alert(data.erro);

			}

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function addBairro() {

	var obj = new Object();
	obj["cod_bairro"] = $("#cod_bairro").val();
	obj["desc_bairro"] = $("#cod_bairro option:selected").text();

	if ($("#cod_bairro").val() != "") {
		var dias = [];
		$("input[name=radioDiasemana]:radio").each(function() {
			var dia = new Object();

			dia["cod_dia"] = $(this).val();
			dia["horarios"] = [];
			dias.push(dia);
		});

		obj["dias"] = dias;
		obj["val_tele"] = "0";
		obj["flag_telebairro"] = 'N';

		var jatem = false;
		if (horarios_bairros != null) {
			for (t = 0; t < horarios_bairros.length; t++) {

				if (horarios_bairros[t].cod_bairro == $("#cod_bairro").val()) {
					alert("Atenção! Bairro já existe na relação de bairros para entrega. O sistema irá carregar as informaçoes existentes.");
					jatem = true;
					loadDadosBairro($("#cod_bairro").val());
				}

			}
		} else {
			horarios_bairros = [];
		}

		if (!jatem) {
			horarios_bairros.push(obj);

			$('#table_bairros').bootstrapTable('load', horarios_bairros);
			$('#table_bairros').bootstrapTable('resetView');
			ativaWarningSalvar();
			loadDadosBairro($("#cod_bairro").val());
		}
	} else {
		alert("Escolha um bairro");
	}
}

function loadDadosBairro(codbairro) {
	
	
	$('input:radio[name="radioDiasemana"]').each(function(){
		$(this).prop('checked', false);
	 });
	
	bairro_edicao = "";
	for (t = 0; t < horarios_bairros.length; t++) {

		if (horarios_bairros[t].cod_bairro == codbairro) {
			bairro_edicao = horarios_bairros[t];
			break;
		}
	}

	$('input:radio[name="radioDiasemana"]').filter('[value="1"]').prop('checked', true);
	trocaDiaSemanaDados(1);
	$("#b_desc_bairro").html(bairro_edicao.desc_bairro);

	$("#b_val_tele").autoNumeric('set', parseFloat(bairro_edicao.val_tele));

	if (bairro_edicao.flag_telebairro == 'S') {
		$("#flag_telebairro").prop('checked', true);
	} else {
		$("#flag_telebairro").prop('checked', false);
	}

	$("#b_modal_bairro").modal("show");


}

function trocaDiaSemanaDados(dia) {

	for (t = 0; t < bairro_edicao.dias.length; t++) {

		if (bairro_edicao.dias[t].cod_dia == dia) {

			$('#table_horarios').bootstrapTable('load', bairro_edicao.dias[t].horarios);
			$('#table_horarios').bootstrapTable('resetView');
			$('[data-toggle="tooltip"]').tooltip();
			break;
		}
	}

}

function loadCidade() {

	$.blockUI({
		message : 'Salvando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'loadCidade'
		},
		success : function(data) {

			var html = "";

			for (t = 0; t < data.length; t++) {

				html = html + "<option value='" + data[t].COD_CIDADE + "'  > " + data[t].DESC_CIDADE + "  </option>  ";

			}

			$("#cod_cidade").html(html);

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function loadBairrosParam() {
	horarios_bairros = "";
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'loadBairrosParam'
		},
		success : function(data) {

			horarios_bairros = data;
			bairro_edicao = "";
			$('#table_bairros').bootstrapTable('load', horarios_bairros);
			$('#table_bairros').bootstrapTable('resetView');
			$('[data-toggle="tooltip"]').tooltip();
			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function loadDados() {

	$.blockUI({
		message : 'Salvando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'loadDadosEmp'
		},
		success : function(data) {

			$("#cod_cidade").val(data[0].COD_CIDADE);
			$("#desc_razaosocial").val(data[0].DESC_RAZAO_SOCIAL);
			$("#desc_fantasia").val(data[0].DESC_NOME_ABREV);
			$("#num_telefone").val(data[0].DESC_TELEFONE);
			$("#desc_endereco").val(data[0].DESC_ENDERECO);
			$("#val_min_entrega").autoNumeric('set', data[0].VAL_ENTREGA_MIN);
			$("#val_padrao_tele").autoNumeric('set', data[0].VAL_TELE_ENTREGA);
			$("#desc_num").val(data[0].NUM_ENDEREC);
			$("#desc_complemento").val(data[0].DESC_COMPLEMENTO);
			$("#flag_online").val(data[0].flag_ativo);
			$("#desc_mail").val(data[0].desc_mail);
			
			$("#emp_logo").attr("src", data[0].nome_img);

			if (data[0].flag_custom == 'S') {
				$("#check_custommode").prop('checked', true);
			} else {
				$("#check_custommode").prop('checked', false);
			}

			id_horariomax = data[0].id_horariomax;
			testComboOnline();
			$('[data-toggle="tooltip"]').tooltip();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});

}

function addHorario() {

	
	try {
	
	
		if ($("#b_hora_inicial").val() != "" && $("#b_hora_final").val() != "") {
			var horario = new Object();
	
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
				
			}else{
				
			
				var casa_dia = 0;
				for (t = 0; t < bairro_edicao.dias.length; t++) {
	
					if (bairro_edicao.dias[t].cod_dia == $("input[name=radioDiasemana]:radio").filter(':checked').val()) {
						casa_dia = t;
						break;
					}
				}
				
				for (t = 0; t < bairro_edicao.dias[casa_dia].horarios.length; t++) {
					
					var p_ini = parseFloat(bairro_edicao.dias[casa_dia].horarios[t]["HORARIO_INI"].replace(":", "."));
					var p_fim = parseFloat(bairro_edicao.dias[casa_dia].horarios[t]["HORARIO_FIM"].replace(":", "."));
					
					if(horaini_teste>=p_ini && horaini_teste<=p_fim){
						throw "O horario inicial adicionado conflita com o horario de " + bairro_edicao.dias[casa_dia].horarios[t]["HORARIO_INI"] + " até "+ bairro_edicao.dias[casa_dia].horarios[t]["HORARIO_FIM"];
					}
					
					if(horafim_teste>=p_ini && horafim_teste<=p_fim){
						throw "O horario inicial adicionado conflita com o horario de " + bairro_edicao.dias[casa_dia].horarios[t]["HORARIO_INI"] + " até "+ bairro_edicao.dias[casa_dia].horarios[t]["HORARIO_FIM"];
					}
					
					
				}
				
				horario["HORARIO_INI"] = horaini;
				horario["HORARIO_FIM"] = horafim;
	
				horario["id_horario"] = id_horariomax++;
	
				bairro_edicao.dias[casa_dia].horarios.push(horario);
				$('#table_horarios').bootstrapTable('load', bairro_edicao.dias[casa_dia].horarios);
				$('#table_horarios').bootstrapTable('resetView');
	
				$("#b_hora_inicial").val("");
				$("#b_hora_final").val("");
				ativaWarningSalvar();
			}
		}else{
			throw "Horário invalido! Preencha ambos os campos.";
		}
	
	}
	catch(err) {
	   alert(err);
	}
}

function loadDiasSemana() {

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

				html.push(" <tr> <td style=\"padding-right: 10px;\"> ");
				html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
				html.push("<label> <input type=\"radio\" name='radioDiasemana' class=\"DIA\" value=\"" + data[t].COD_DIA + "\"> " + data[t].DESC_DIA + "</label> </div> 	</td>");

				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px;\"> ");
					html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
					html.push("<label> <input type=\"radio\" name='radioDiasemana' class=\"DIA\" value=\"" + data[t + 1].COD_DIA + "\"> " + data[t + 1].DESC_DIA + "</label> </div> 	</td>");
					t++;
				}

				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px;\"> ");
					html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
					html.push("<label> <input type=\"radio\"  name='radioDiasemana' class=\"DIA\" value=\"" + data[t + 1].COD_DIA + "\"> " + data[t + 1].DESC_DIA + "</label> </div> 	</td>");
					t++;
				}

				if (data[t + 1] != undefined) {

					html.push("<td style=\"padding-right: 10px;\"> ");
					html.push("<div class=\"checkbox\" style=\"margin-top: 0px; margin-bottom: 0px;\">");
					html.push("<label> <input type=\"radio\"  name='radioDiasemana' class=\"DIA\" value=\"" + data[t + 1].COD_DIA + "\"> " + data[t + 1].DESC_DIA + "</label> </div> 	</td>");
					t++;
				}

				html.push(" </tr>");

			}

			$("#desc_dias_semana").html(html);

			$("input[name=radioDiasemana]:radio").change(function() {

				trocaDiaSemanaDados($(this).filter(':checked').val());
			});

		},
		error : function(data) {
			alert(data.responseText);
		}
	});

}
