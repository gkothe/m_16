package com.funcs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.configs.Conexao;

public class InsertDados {

	private static void populateHorarios(String bairros, JSONArray dias, JSONArray horarios, Connection conn, int cod_distribuidora, boolean apagartodos) throws Exception {

		PreparedStatement st2 = null;
		PreparedStatement st3 = null;
		PreparedStatement st  = null;
		
		if(apagartodos){
			st = conn.prepareStatement("delete from distribuidora_horario_dia_entre where id_distribuidora = ? ");
			st.setInt(1, cod_distribuidora);
			st.executeUpdate();
			
			st = conn.prepareStatement("delete from distribuidora_bairro_entrega where id_distribuidora = ? ");
			st.setInt(1, cod_distribuidora);
			st.executeUpdate();
			
		}
		
	
		String sql = "select * from bairros ";

		if (!bairros.equalsIgnoreCase("")) {// se vier vazio add todos
			sql += "bairros in (" + bairros + ")";
		}
		st = conn.prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			int iddistrbair = Utilitario.retornaIdinsert("distribuidora_bairro_entrega", "ID_DISTR_BAIRRO", conn);

			st2 = conn.prepareStatement("INSERT INTO distribuidora_bairro_entrega (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (?, ?, ?, ?, ?);");
			st2.setInt(1, iddistrbair);
			st2.setInt(2, rs.getInt("cod_bairro"));
			st2.setInt(3, cod_distribuidora);
			st2.setDouble(4, 0.0);
			st2.setString(5, "N");
			st2.executeUpdate();

			for (int i = 0; i < dias.size(); i++) {
				int coddia = Integer.parseInt(dias.get(i).toString());
				for (int j = 0; j < horarios.size(); j++) {
					JSONObject horariosobj = (JSONObject) horarios.get(j);
					sql = "INSERT INTO distribuidora_horario_dia_entre (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (?, ?, ?, ?, ?, ?);";
					st3 = conn.prepareStatement(sql);
					st3.setLong(1, Utilitario.retornaIdinsertLong("distribuidora_horario_dia_entre", "id_horario", conn));
					st3.setInt(2, cod_distribuidora);
					st3.setInt(3, coddia);
					st3.setInt(4, iddistrbair);
					st3.setString(5, horariosobj.get("HORARIO_INI").toString());
					st3.setString(6, horariosobj.get("HORARIO_FIM").toString());
					st3.executeUpdate();
				}
			}
		}
	}

	public static void dadosBairrosTeste() {
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			JSONArray dias = new JSONArray();
			dias.add(1);
			dias.add(2);
			dias.add(3);
			dias.add(4);
			dias.add(5);
			dias.add(6);
			dias.add(7);

			JSONArray horarios = new JSONArray();
			JSONObject horariosobj = new JSONObject();
			horariosobj.put("HORARIO_INI", "09:00:00");
			horariosobj.put("HORARIO_FIM", "15:00:00");
			horarios.add(horariosobj);
			horariosobj = new JSONObject();
			horariosobj.put("HORARIO_INI", "13:25:00");
			horariosobj.put("HORARIO_FIM", "20:00:00");
			horarios.add(horariosobj);

			populateHorarios("", dias, horarios, conn, 1,true);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			try {
				conn.close();
			} catch (Exception e2) {
			}
		}

	}

	public static void main(String[] args) {
		dadosBairrosTeste();
	}

}
