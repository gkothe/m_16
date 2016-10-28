//@ sourceURL=dash1_pedidos.js

$(document).ready(function() {
	
	dashServico();
	dashModo();
	
	
    $('.collapse-link').on('click', function() {
        var $BOX_PANEL = $(this).closest('.x_panel'),
            $ICON = $(this).find('i'),
            $BOX_CONTENT = $BOX_PANEL.find('.x_content');
        
        // fix for some div with hardcoded fix class
        if ($BOX_PANEL.attr('style')) {
            $BOX_CONTENT.slideToggle(200, function(){
                $BOX_PANEL.removeAttr('style');
            });
        } else {
            $BOX_CONTENT.slideToggle(200); 
            $BOX_PANEL.css('height', 'auto');  
        }

        $ICON.toggleClass('fa-chevron-up fa-chevron-down');
    });

	
});


function dashModo(){
	
	$.blockUI({
		message : 'Carregando...'
	});

	$.ajax({
		type : "POST",
		url : "home?ac=ajax",
		dataType : "json",
		async : true,
		data : {
			cmd : 'dashPagamento'

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
			
			var html  = "";
			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].desc;
				datadash[datadash.length] = data[t].qtd;
				html = html + "<tr>";
				html = html + "	<td style=\"height: 30px; !important \">";
				html = html + "		<p>";
				if(t==0)
					html = html + "			<i class=\"fa fa-square blue\"></i>&nbsp;&nbsp;" + data[t].desc;
				else
					html = html + "			<i class=\"fa fa-square green\"></i>&nbsp;&nbsp;" + data[t].desc;
				html = html + "		</p>";
				html = html + "	</td>";
				html = html + "	<td style=\"text-align: center\">"+data[t].qtddf+"</td>";
				html = html + "	<td style=\"text-align: center\">"+data[t].perc+"%</td>";
				html = html + "</tr>";

			}
			html = html + "<tr><td>&nbsp<td></tr>";
			$("#dash_pagtable").append(html);
			
			 var options = {
			          legend: false,
			          responsive: false
			        };

			        new Chart(document.getElementById("pie_pagamento"), {
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
			
			var html  = "";
			for (t = 0; t < data.length; t++) {

				labels[labels.length] = data[t].desc;
				datadash[datadash.length] = data[t].qtd;
				html = html + "<tr>";
				html = html + "	<td style=\"height: 30px; !important \">";
				html = html + "		<p>";
				if(t==0)
					html = html + "			<i class=\"fa fa-square blue\"></i>&nbsp;&nbsp;" + data[t].desc;
				else
					html = html + "			<i class=\"fa fa-square green\"></i>&nbsp;&nbsp;" + data[t].desc;
				html = html + "		</p>";
				html = html + "	</td>";
				html = html + "	<td style=\"text-align: center\">"+data[t].qtddf+"</td>";
				html = html + "	<td style=\"text-align: center\">"+data[t].perc+"%</td>";
				html = html + "</tr>";

			}
			html = html + "<tr><td>&nbsp<td></tr>";
			$("#dash_servtable").append(html);
			
			 var options = {
			          legend: false,
			          responsive: false
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