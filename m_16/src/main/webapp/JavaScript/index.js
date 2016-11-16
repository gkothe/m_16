//@ sourceURL=index.js
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
		url : "",
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


function sysMsg(text,tipo){
	
	if(tipo=='E'){
		$("#msg_erro_aviso").html("<label style=\"font-size: 20px; color: red\" > ERRO!</label>");	
	}else if(tipo=='A'){
		$("#msg_erro_aviso").html("<label style=\"font-size: 20px; color:#ff9900\" > Aviso!</label>");	
	}else if(tipo=='M'){
		$("#msg_erro_aviso").html("<label style=\"font-size: 20px; color:green\" > Mensagem</label>");
	}
	
	
	
	
	$("#modal_erros").modal('show');
	$("#msg_erro").html(text);
	
}