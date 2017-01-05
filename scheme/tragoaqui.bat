@ECHO OFF



::Lets delete old backup file...
@IF EXIST "c:\Users\gkothe\Desktop\tragoaqui.sql" DEL /Q /F "c:\Users\gkothe\Desktop\tragoaqui.sql"

::Execute mysqldump to backup database(s) – Table(s)
@"c:\Program Files\MySQL\MySQL Workbench 6.3 CE\mysqldump.exe" --host="mysql05-farm68.kinghost.net"  --user="tragoaqui" --password="m3t4alupy0ur4ass" -Q --complete-insert --result-file="D:\phonegap_projects\m_16\scheme\tragoaqui"%date:~4,2%%date:~7,2%%date:~10,4%_%time:~0,2%%time:~3,2%%time:~6,2%".sql" --databases "tragoaqui"
