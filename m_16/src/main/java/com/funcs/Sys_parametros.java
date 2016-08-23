package com.funcs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Sys_parametros {
	int cod_cidade = 0;
	long id_usuario_admin = 0;
	String flag_manutencao = "";
	String desc_key = "";
	long segs_teste_ajax = 0;
	
	public int getCod_cidade() {
		return cod_cidade;
	}

	public void setCod_cidade(int cod_cidade) {
		this.cod_cidade = cod_cidade;
	}

	public long getId_usuario_admin() {
		return id_usuario_admin;
	}

	public void setId_usuario_admin(long id_usuario_admin) {
		this.id_usuario_admin = id_usuario_admin;
	}

	public String getFlag_manutencao() {
		return flag_manutencao;
	}

	public void setFlag_manutencao(String flag_manutencao) {
		this.flag_manutencao = flag_manutencao;
	}

	public String getDesc_key() {
		return desc_key;
	}

	public void setDesc_key(String desc_key) {
		this.desc_key = desc_key;
	}

	public long getSegs_teste_ajax() {
		return segs_teste_ajax;
	}

	public void setSegs_teste_ajax(long segs_teste_ajax) {
		this.segs_teste_ajax = segs_teste_ajax;
	}
	

	
	public Sys_parametros(Connection conn){
		//se for o caso futuramente, passar o cod_cidade
		try {
			PreparedStatement st = conn.prepareStatement("select * from sys_parametros ");
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				this.setCod_cidade(rs.getInt("cod_cidade"));
				this.setId_usuario_admin(rs.getLong("ID_USUARIO_ADMIN"));
				this.setFlag_manutencao(rs.getString("FLAG_MANUTENCAO"));
				this.setDesc_key(rs.getString("DESC_KEY"));
				this.setSegs_teste_ajax(rs.getLong("SEGS_TESTE_AJAX"));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	
}
