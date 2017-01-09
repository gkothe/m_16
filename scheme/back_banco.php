<?php

  if($_SERVER["REMOTE_ADDR"] != "189.38.85.36"){ 
			die("Acesso nao Autorizado"); 
  }else{ 
		   $command = "mysqldump -h mysql05-farm68.kinghost.net -u tragoaqui -pm3t4alupy0ur4ass tragoaqui | gzip > /home/tragoaqui/backups/dbteste_tragoaqui_`date '+%Y-%m-%d_%H%M'`.gzip";
		   exec($command);
		   
		   $command = "mysqldump -h mysql05-farm68.kinghost.net -u tragoaqui01 -pm3t4alupy0ur4ass tragoaqui01 | gzip > /home/tragoaqui/backups/dbdrink_tragoaqui_`date '+%Y-%m-%d_%H%M'`.gzip";
		   exec($command);
		   
		   $command = "mysqldump -h mysql05-farm68.kinghost.net -u tragoaqui02 -pm3t4alupy0ur4ass tragoaqui02 | gzip > /home/tragoaqui/backups/dbfit_tragoaqui_`date '+%Y-%m-%d_%H%M'`.gzip";
		   exec($command);
		   
		   $command = "zip -r  /home/tragoaqui/backups/webapss_`date '+%Y-%m-%d_%H%M'`.zip  /home/tragoaqui/webapps";
		   exec($command);
		   
		   
		   
  }
  

?>
	
	
  

