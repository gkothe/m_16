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
		url : "php/sys_index_ajax.php",
		data : {
			cmd : "sendEmail",
			email : email
		},
		async : true,
		dataType : 'json',
		success : function(data) {
			if (data.msg == 'ok') {
				alert("Email enviado! ");
				
				$("#modal_ajuda").modal("hide");
			} else {

				alert(data.erro);

			}

			$.unblockUI();
		},
		error : function(data) {
			$.unblockUI();
			alert(data.responseText);
		}
	});

}