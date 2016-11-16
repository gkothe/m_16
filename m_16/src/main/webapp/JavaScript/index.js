function showTrocaEmail() {

	$("#modal_ajuda").modal("show");

}

$(document).ready(function() {

	$("#btn_envia_email").click(function() {

		sendEmail();

	});

});

function limpaModal() {

	$("#r_desc_mail").val("");

}

function sendEmail() {

	var email = $("#r_desc_mail").val();

	$.blockUI({
		message : 'Enviando, aguarde...',
		 baseZ: 2000
	});
	
	$.ajax({
		type : 'POST',
		url : "sys?acao=senha_email",
		data : {
			acao : "senha_email",
			email : email
		},
		async : true,
		dataType : 'json',
		success : function(data) {
			if (data.msg == 'ok') {
				
				sysMsg("Email enviado!",'M')
				
				$("#modal_ajuda").modal("hide");
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