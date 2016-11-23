package com.funcs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.configs.Conexao;

public class Thread_OnOffEmp extends Thread {

	Connection conn = null;
	PreparedStatement st = null;
	ResultSet rs = null;
	Date date = null;
	Date agora = null;
	long secs_param = 0;
	long seconds = 0;
	long rodateste = 0;
	public void run() {

		try {

			conn = Conexao.getConexao();
			Sys_parametros sys = new Sys_parametros(conn);
			secs_param = sys.getSegs_teste_ajax();
			rodateste = 0;
			if(secs_param*100> 1000)
				rodateste = 10000; //testa de 10 em 10 segs
			else
				rodateste = secs_param*1000;  //se o tempo de teste for menor q 10, fazeoms a thread rodar no mesmo tempo
			
			
			while (true) {
				testeDistribuidorasonline(conn);
				this.sleep(rodateste);
			}

		} catch (Exception e) {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doShutdown() {

		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}

	}

	private void testeDistribuidorasonline(Connection conn) {

		try {
			// flag_ativo = 'S' significa que estava online, flag_ativo = 'F' significa que apesar de estar marcada como online, a empresa esta com o browser fechado e nao esta vendo os pedidos, e nem  os usuarios estão vendo os produtos disponiveis. 
			//Uma empresa no estado F , não poderia receber pedidos. Sera avisado para o usuario que ela está offline.
			st = conn.prepareStatement(" select ID_DISTRIBUIDORA,FLAG_ATIVO,DATE_LASTAJAX from distribuidora where FLAG_ATIVO_MASTER = 'S' and (FLAG_ATIVO='S' or  FLAG_ATIVO='F') ");
			rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getDate("DATE_LASTAJAX") != null) {

					date = rs.getTimestamp("DATE_LASTAJAX");
					agora = new Date();
					seconds = (agora.getTime() - date.getTime()) / 1000;

				//	System.out.println(rs.getInt("ID_DISTRIBUIDORA") +" "+ agora + " " + date + " " + seconds);
					
					if (seconds > secs_param && rs.getString("FLAG_ATIVO").equalsIgnoreCase("S")) { // empresa esta offine

						st = conn.prepareStatement("update distribuidora set FLAG_ATIVO = 'F' where id_distribuidora = ? ");
						st.setInt(1, rs.getInt("ID_DISTRIBUIDORA"));
						st.executeUpdate();
					
					} else if (seconds < secs_param && rs.getString("FLAG_ATIVO").equalsIgnoreCase("F")) {
					
						st = conn.prepareStatement("update distribuidora set FLAG_ATIVO = 'S' where id_distribuidora = ? ");
						st.setInt(1, rs.getInt("ID_DISTRIBUIDORA"));
						st.executeUpdate();
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

}