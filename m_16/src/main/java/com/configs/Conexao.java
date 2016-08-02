package com.configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

	   public final static String url = "jdbc:mysql://localhost:3306/";
	   public final static   String dbName = "bebidas_novo?characterEncoding=UTF-8";
	   public final static   String driver = "com.mysql.jdbc.Driver";
	   public final static String userName = "root";
	   public final static String password = "root";
	
		    
		public static Connection getConexao() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException{
			Class.forName(driver).newInstance();
			Connection  conn = DriverManager.getConnection(url+dbName,userName,password);
			return conn;
		}
	    
		
	
	
}
