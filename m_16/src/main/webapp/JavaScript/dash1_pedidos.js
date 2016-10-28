//@ sourceURL=dash1_pedidos.js

$(document).ready(function() {
	
	dashServico();
	
	
	
});

function dashServico(){
	
	
	
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashServico'

		},
		success : function(data) {

			var labels = [];
			var datadash  = [];
			var backgroundColor =[
					                "#26B99A",
					                "#3498DB"
					              ];
			var hoverBackgroundColor = [
						                "#36CAAB",
						                "#49A9EA"
						              ];
			
			console.log(data);
			var html  = "";
			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].servico;
				datadash[datadash.length] = data[t].qtd;
				html = html + "<tr>";
				html = html + "	<td>";
				html = html + "		<p>";
				if(t==0)
					html = html + "			<i class=\"fa fa-square blue\"></i>" + data[t].servico;
				else
					html = html + "			<i class=\"fa fa-square green\"></i>" + data[t].servico;
				html = html + "		</p>";
				html = html + "	</td>";
				html = html + "	<td>"+data[t].perc+"%</td>";
				html = html + "</tr>";
				

			}
			
			$("#dash_servtable").append(html);
			
			 var options = {
			          legend: true,
			          responsive: true
			        };

			        new Chart(document.getElementById("pie_servico"), {
			          type: 'doughnut',
			          tooltipFillColor: "rgba(51, 51, 51, 0.55)",
			          data: {
			            labels:labels,
			            datasets: [{
			              data: datadash,
			              backgroundColor: backgroundColor,
			              hoverBackgroundColor: hoverBackgroundColor
			            }]
			          },
			          options: options
			        });
	

			$.unblockUI();

		},
		error : function(msg) {
			$.unblockUI();
			alert("Erro: " + msg.msg);
		}
	});


	
	
	
	
}