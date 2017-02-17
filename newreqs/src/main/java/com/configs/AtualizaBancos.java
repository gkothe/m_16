package com.configs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.funcs.Utilitario;

public class AtualizaBancos {

	public static Connection getConexaoTeste() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		String url = "jdbc:mysql://mysql05-farm68.kinghost.net/";
		String dbName = "tragoaqui?characterEncoding=UTF-8";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "tragoaqui";
		String password = "m3t4alupy0ur4ass";

		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + dbName, userName, password);
		return conn;
	}
	
	public static Connection getConexaoFitTeste() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		String url = "jdbc:mysql://mysql05-farm68.kinghost.net/";
		String dbName = "tragoaqui03?characterEncoding=UTF-8";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "tragoaqui03";
		String password = "m3t4alupy0ur4ass";

		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + dbName, userName, password);
		return conn;
	}

	public static Connection getConexaoTragoAqui() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		String url = "jdbc:mysql://mysql05-farm68.kinghost.net/";
		String dbName = "tragoaqui01?characterEncoding=UTF-8";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "tragoaqui01";
		String password = "m3t4alupy0ur4ass";

		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + dbName, userName, password);
		return conn;
	}

	public static Connection getConexaoTragoAquiFit() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		String url = "jdbc:mysql://mysql05-farm68.kinghost.net/";
		String dbName = "tragoaqui02?characterEncoding=UTF-8";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "tragoaqui02";
		String password = "m3t4alupy0ur4ass";

		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + dbName, userName, password);
		return conn;
	}

	private static void rodar(Connection conn) throws Exception {

		atualizarversao(conn, "'0.0.19'");
		//altertable(conn);
	// 	
    //			update(conn);
	}

	private static void atualizarversao(Connection conn, String ver) throws Exception {

		StringBuffer sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
		sql.append(" UPDATE sys_parametros SET app_versao = "+ver+" WHERE COD_CIDADE = 1 ");
		sql.append("  ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		// st.setLong(1, Long.parseLong(id_proddistr));
		st.executeUpdate();

	}
	
	private static void update(Connection conn) throws Exception {

		StringBuffer sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
		sql.append(" UPDATE sys_parametros SET applicacao = 2 ");
		sql.append("  ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		// st.setLong(1, Long.parseLong(id_proddistr));
		st.executeUpdate();

	}

	private static void altertable(Connection conn) throws Exception {

		StringBuffer sql = new StringBuffer();// deleta item do carrinho se ele exister exite no carrinho, add depois
		sql.append(" ");
		sql.append(" alter table produtos add desc_key_words       text;   ");
		PreparedStatement st = conn.prepareStatement(sql.toString());
		st.executeUpdate();

	}

	public static void main(String[] args) {

		Connection conn = null;

		try {
			conn = getConexaoTeste();
			conn.setAutoCommit(false);
			rodar(conn);
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception e.printStackTrace(); }
			try {
				conn.rollback();
				conn.close();
			} catch (Exception ex) { // TODO: handle exception } }
			}
		}
		try {
			conn = getConexaoTragoAqui();
			conn.setAutoCommit(false);
			rodar(conn);
			conn.commit();
			conn.close();
		} catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
			e.printStackTrace();
			try {
				conn.rollback();
				conn.close();
			} catch (Exception ex) { // TODO: handle exception } }
			}
		}
		try {
			conn = getConexaoFitTeste();
			conn.setAutoCommit(false);
			rodar(conn);
			conn.commit();
			conn.close();
		} catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
			e.printStackTrace();
			try {
				conn.rollback();
				conn.close();
			} catch (Exception ex) { // TODO: handle exception } }
			}
		}
		
		try {
			conn = getConexaoTragoAquiFit();
			conn.setAutoCommit(false);
			rodar(conn);
			conn.commit();
			conn.close();
		} catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
			e.printStackTrace();
			try {
				conn.rollback();
				conn.close();
			} catch (Exception ex) { // TODO: handle exception } }
			}
		}
		

	}

}
