//@ sourceURL=mobileusers.js
$(document).ready(function() {

	$("#add_user").click(function() {
		addlinha("", "", "");
		// addlinha("g.kothe@hotmail.com", "A", "blablo");
		// addlinha("w.matheus_@hotmail.com", "V", "lallalal");
		// addlinha("matheusgoltz@hotmail.com", "V", "oooooooo");
	});

	$("#btn_salvar").click(function() {
		salvarUsers();
	});

	loadUsers();

});



function removerow(obj){
	
	$(obj).closest(".row").remove();
	
}


function addlinha(email, role, obs) {
	$("#dados_users").append($("#copy_html").html());

	$("#dados_users").find(".opc_mail").val(email);
	$("#dados_users").find(".opc_mail").addClass("opc_mail_foi");
	$("#dados_users").find(".opc_mail").removeClass("opc_mail");

	$("#dados_users").find(".opc_flagperm").val(role);
	$("#dados_users").find(".opc_flagperm").addClass("opc_flagperm_foi");
	$("#dados_users").find(".opc_flagperm").removeClass("opc_flagperm");

	$("#dados_users").find(".opc_txtobs").val(obs);
	$("#dados_users").find(".opc_txtobs").addClass("opc_txtobs_foi");
	$("#dados_users").find(".opc_txtobs").removeClass("opc_txtobs");

}

function salvarUsers() {

	$.blockUI({
		message : 'Salvando...',
		baseZ : 9000
	});

	var mails = [];
	var i = 0;
	$('.opc_mail_foi').each(function() {
		mails[i++] = $(this).val();
	});

	var perm = [];
	i = 0;
	$('.opc_flagperm_foi').each(function() {
		perm[i++] = $(this).val();
	});

	var txtobs = [];
	i = 0;
	$('.opc_txtobs_foi').each(function() {
		txtobs[i++] = $(this).val();
	});

	var users = [];
	for (t = 0; t < mails.length; t++) {

		var item = {
			'desc_mail' : mails[t],
			'flag_role' : perm[t],
			'desc_obs' : txtobs[t],
		};

		users[t] = item;
	}

	var usersjson = JSON.stringify(users);

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'saveLojaMobileUsers',
			usersjson : usersjson
		},
		success : function(data) {

			if (data.msg == 'ok') {

				sysMsg("Dados salvos!", 'M')
				location.reload(true);
			} else if (data.erro != undefined) {
				sysMsg(data.erro, 'E')
			}

			$.unblockUI();
		},
		error : function(msg) {
			$.unblockUI();
		}
	});

}

function loadUsers() {

	$.blockUI({
		message : 'Carregando...',
		baseZ : 9000
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'loadlojamobileusers',
		},
		success : function(data) {

			var users = data.users;

			for (t = 0; t < users.length; t++) {
				addlinha(users[t].desc_mail, users[t].flag_role, users[t].desc_obs);
			}

			if (data.erro != undefined) {
				sysMsg(data.erro, 'E')
			}

			$.unblockUI();
		},
		error : function(msg) {
			$.unblockUI();
		}
	});

}
