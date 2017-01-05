<?php

  if($_SERVER["REMOTE_ADDR"] != "189.38.85.36"){ 
			die("Acesso nao Autorizado"); 
  }else{ 
		   $command = "mysqldump -h mysql05-farm68.kinghost.net -u tragoaqui -pm3t4alupy0ur4ass tragoaqui | gzip > /home/tragoaqui/backups/banco_tragoaqui_`date '+%Y-%m-%d_%H%M'`.gzip";
		   exec($command);
  }

?>
	
	
  

