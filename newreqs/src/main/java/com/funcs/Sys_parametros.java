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
	String face_redirect_uri_webapp = "";
	String url_system = "";
	long sys_id_visistante = 0;
	int sys_smtp_port = 0;
	String sys_host_name_smtp = "";
	String sys_email = "";
	String sys_senha = "";
	String sys_fromemail = "";
	String sys_fromdesc = "";
	boolean sys_tls = true;
	int PED_HORASOKEY = 0;
	int NUM_TEMPOMAXCANC_MINUTO = 0;
	int cod_recusa_estoque = 0;
	String onesignal_key = "";
	String onesignal_url = "";
	String onesignal_appid = "";
	int num_minutos_not_final = 0;
	int num_segs_not_final_exec = 0;
	int cod_cancelamentosys = 0; 
	String path ="";
	String desc_webappfolder ="";
	String ignorar_regramaior18 ="";
	String tragoaqui_num_telefone ="";
	String tragoaqui_pag_facebook ="";
	String applicacao ="";
	int sys_minutes_agen_not_resp = 0;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPED_HORASOKEY() {
		return PED_HORASOKEY;
	}

	public void setPED_HORASOKEY(int pED_HORASOKEY) {
		PED_HORASOKEY = pED_HORASOKEY;
	}
	
	public int getSys_minutes_agen_not_resp() {
		return sys_minutes_agen_not_resp;
	}

	public void setSys_minutes_agen_not_resp(int sys_minutes_agen_not_resp) {
		this.sys_minutes_agen_not_resp = sys_minutes_agen_not_resp;
	}

	public String getApplicacao() {
		return applicacao;
	}

	public void setApplicacao(String applicacao) {
		this.applicacao = applicacao;
	}

	public String getFace_redirect_uri_webapp() {
		return face_redirect_uri_webapp;
	}

	public String getTragoaqui_num_telefone() {
		return tragoaqui_num_telefone;
	}

	public void setTragoaqui_num_telefone(String tragoaqui_num_telefone) {
		this.tragoaqui_num_telefone = tragoaqui_num_telefone;
	}

	public String getTragoaqui_pag_facebook() {
		return tragoaqui_pag_facebook;
	}

	public void setTragoaqui_pag_facebook(String tragoaqui_pag_facebook) {
		this.tragoaqui_pag_facebook = tragoaqui_pag_facebook;
	}

	public void setFace_redirect_uri_webapp(String face_redirect_uri_webapp) {
		this.face_redirect_uri_webapp = face_redirect_uri_webapp;
	}

	public String getIgnorar_regramaior18() {
		return ignorar_regramaior18;
	}

	public void setIgnorar_regramaior18(String ignorar_regramaior18) {
		this.ignorar_regramaior18 = ignorar_regramaior18;
	}

	public String getDesc_webappfolder() {
		return desc_webappfolder;
	}

	public void setDesc_webappfolder(String desc_webappfolder) {
		this.desc_webappfolder = desc_webappfolder;
	}

	public int getCod_cancelamentosys() {
		return cod_cancelamentosys;
	}

	public void setCod_cancelamentosys(int cod_cancelamentosys) {
		this.cod_cancelamentosys = cod_cancelamentosys;
	}

	public String getOnesignal_key() {
		return onesignal_key;
	}

	public void setOnesignal_key(String onesignal_key) {
		this.onesignal_key = onesignal_key;
	}

	public String getOnesignal_url() {
		return onesignal_url;
	}

	public int getNum_minutos_not_final() {
		return num_minutos_not_final;
	}

	public void setNum_minutos_not_final(int num_minutos_not_final) {
		this.num_minutos_not_final = num_minutos_not_final;
	}

	public int getNum_segs_not_final_exec() {
		return num_segs_not_final_exec;
	}

	public void setNum_segs_not_final_exec(int num_segs_not_final_exec) {
		this.num_segs_not_final_exec = num_segs_not_final_exec;
	}

	public void setOnesignal_url(String onesignal_url) {
		this.onesignal_url = onesignal_url;
	}

	public String getOnesignal_appid() {
		return onesignal_appid;
	}

	public void setOnesignal_appid(String onesignal_appid) {
		this.onesignal_appid = onesignal_appid;
	}

	public int getCod_recusa_estoque() {
		return cod_recusa_estoque;
	}

	public void setCod_recusa_estoque(int cod_recusa_estoque) {
		this.cod_recusa_estoque = cod_recusa_estoque;
	}

	public int getNUM_TEMPOMAXCANC_MINUTO() {
		return NUM_TEMPOMAXCANC_MINUTO;
	}

	public void setNUM_TEMPOMAXCANC_MINUTO(int nUM_TEMPOMAXCANC_MINUTO) {
		NUM_TEMPOMAXCANC_MINUTO = nUM_TEMPOMAXCANC_MINUTO;
	}

	public int getSys_smtp_port() {
		return sys_smtp_port;
	}

	public void setSys_smtp_port(int sys_smtp_port) {
		this.sys_smtp_port = sys_smtp_port;
	}

	public long getSys_id_visistante() {
		return sys_id_visistante;
	}

	public void setSys_id_visistante(long sys_id_visistante) {
		this.sys_id_visistante = sys_id_visistante;
	}

	public String getSys_host_name_smtp() {
		return sys_host_name_smtp;
	}

	public void setSys_host_name_smtp(String sys_host_name_smtp) {
		this.sys_host_name_smtp = sys_host_name_smtp;
	}

	public String getSys_email() {
		return sys_email;
	}

	public void setSys_email(String sys_email) {
		this.sys_email = sys_email;
	}

	public String getSys_senha() {
		return sys_senha;
	}

	public void setSys_senha(String sys_senha) {
		this.sys_senha = sys_senha;
	}

	public String getSys_fromemail() {
		return sys_fromemail;
	}

	public void setSys_fromemail(String sys_fromemail) {
		this.sys_fromemail = sys_fromemail;
	}

	public String getSys_fromdesc() {
		return sys_fromdesc;
	}

	public void setSys_fromdesc(String sys_fromdesc) {
		this.sys_fromdesc = sys_fromdesc;
	}

	public boolean getSys_tls() {
		return sys_tls;
	}

	public void setSys_tls(boolean sys_tls) {
		this.sys_tls = sys_tls;
	}

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

	public String getUrl_system() {
		return url_system;
	}

	public void setUrl_system(String url_system) {
		this.url_system = url_system;
	}

	public Sys_parametros(Connection conn) {
		// se for o caso futuramente, passar o cod_cidade
		try {
			PreparedStatement st = conn.prepareStatement(" select * from sys_parametros ");
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				this.setCod_cidade(rs.getInt("cod_cidade"));
				this.setId_usuario_admin(rs.getLong("ID_USUARIO_ADMIN"));
				this.setFlag_manutencao(rs.getString("FLAG_MANUTENCAO"));
				this.setDesc_key(rs.getString("DESC_KEY"));
				this.setSegs_teste_ajax(rs.getLong("SEGS_TESTE_AJAX"));
				this.setFACE_APP_ID(rs.getLong("FACE_APP_ID"));
				this.setFACE_APP_SECRETKEY(rs.getString("FACE_APP_SECRETKEY"));
				this.setFACE_APP_TOKEN(rs.getString("FACE_APP_TOKEN"));
				this.setFACE_REDIRECT_URI(rs.getString("FACE_REDIRECT_URI"));
				this.setUrl_system(rs.getString("url_system"));
				this.setSys_host_name_smtp(rs.getString("sys_host_name_smtp"));
				this.setSys_smtp_port(rs.getInt("sys_smtp_port"));
				this.setSys_email(rs.getString("sys_email"));
				this.setSys_senha(rs.getString("sys_senha"));
				this.setSys_fromemail(rs.getString("sys_fromemail"));
				this.setSys_fromdesc(rs.getString("sys_fromdesc"));
				this.setSys_tls(rs.getString("sys_tls").equalsIgnoreCase("S") ? true : false);
				this.setSys_id_visistante(rs.getLong("id_visitante"));
				this.setPED_HORASOKEY(rs.getInt("PED_HORASOKEY"));
				this.setNUM_TEMPOMAXCANC_MINUTO(rs.getInt("NUM_TEMPOMAXCANC_MINUTO"));
				this.setCod_recusa_estoque(rs.getInt("COD_RECUSA"));
				this.setOnesignal_key(rs.getString("onesignal_key"));
				this.setOnesignal_url(rs.getString("onesignal_url"));
				this.setOnesignal_appid(rs.getString("onesignal_appid"));
				this.setNum_minutos_not_final(rs.getInt("num_minutos_not_final"));
				this.setNum_segs_not_final_exec(rs.getInt("num_segs_not_final_exec"));
				this.setCod_cancelamentosys(rs.getInt("cod_cancelamentosys"));
				this.setDesc_webappfolder(rs.getString("desc_webappfolder"));
				this.setIgnorar_regramaior18(rs.getString("ignorar_regramaior18"));
				this.setPath(System.getProperty( "catalina.base" )+"/webapps/"+this.getDesc_webappfolder());
				this.setFace_redirect_uri_webapp(rs.getString("face_redirect_uri_webapp"));
				this.setTragoaqui_num_telefone(rs.getString("tragoaqui_num_telefone"));
				this.setTragoaqui_pag_facebook(rs.getString("tragoaqui_pag_facebook"));
				this.setApplicacao(rs.getString("applicacao"));
				this.setSys_minutes_agen_not_resp(rs.getInt("SYS_MINUTES_AGEN_NOT_RESP"));
					
				
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	

}
