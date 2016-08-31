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
	long FACE_APP_ID = 0;
	String FACE_APP_SECRETKEY = "";
	String FACE_APP_TOKEN = "";
	String FACE_REDIRECT_URI = "";
	
	public int getCod_cidade() {
		return cod_cidade;
	}

	public long getFACE_APP_ID() {
		return FACE_APP_ID;
	}

	public void setFACE_APP_ID(long fACE_APP_ID) {
		FACE_APP_ID = fACE_APP_ID;
	}

	public String getFACE_APP_SECRETKEY() {
		return FACE_APP_SECRETKEY;
	}

	public void setFACE_APP_SECRETKEY(String fACE_APP_SECRETKEY) {
		FACE_APP_SECRETKEY = fACE_APP_SECRETKEY;
	}

	public String getFACE_APP_TOKEN() {
		return FACE_APP_TOKEN;
	}

	public void setFACE_APP_TOKEN(String fACE_APP_TOKEN) {
		FACE_APP_TOKEN = fACE_APP_TOKEN;
	}

	public String getFACE_REDIRECT_URI() {
		return FACE_REDIRECT_URI;
	}

	public void setFACE_REDIRECT_URI(String fACE_REDIRECT_URI) {
		FACE_REDIRECT_URI = fACE_REDIRECT_URI;
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
				this.setFACE_APP_ID(rs.getLong("FACE_APP_ID"));
				this.setFACE_APP_SECRETKEY(rs.getString("FACE_APP_SECRETKEY"));
				this.setFACE_APP_TOKEN(rs.getString("FACE_APP_TOKEN")); 
				this.setFACE_REDIRECT_URI(rs.getString("FACE_REDIRECT_URI"));
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	
}
