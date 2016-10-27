package com.funcs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.configs.Conexao;

public class InsertDados {
	// classe de teste
	private static void populateHorarios(String bairros, JSONArray dias, JSONArray horarios, Connection conn, int cod_distribuidora, boolean apagartodos) throws Exception {

		PreparedStatement st2 = null;
		PreparedStatement st3 = null;
		PreparedStatement st = null;

		if (apagartodos) {
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

			populateHorarios("", dias, horarios, conn, 1, true);
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

	private static String addCellXLS(Row row, int idx) throws Exception {
		String valor = "";
		try {
			int tipoCelula = row.getCell(idx).getCellType();
			switch (tipoCelula) {
			case Cell.CELL_TYPE_NUMERIC:
				DataFormatter formatter = new DataFormatter();
				valor = formatter.formatCellValue(row.getCell(idx));
				break;
			case Cell.CELL_TYPE_STRING:
				valor = row.getCell(idx).getStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				valor = row.getCell(idx).getBooleanCellValue() + "";
				break;
			case Cell.CELL_TYPE_BLANK:
				valor = "";
				break;

			default:
				break;
			}

		} catch (Exception ex) {
		}
		return valor;
	}

	public static void readProds() {

		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);

			String sql = "delete from produtos_distribuidora ";
			PreparedStatement st = conn.prepareStatement(sql);
			st.executeUpdate();

			sql = "delete from produtos ";
			st = conn.prepareStatement(sql);
			st.executeUpdate();

			int embranco = 0;

			JSONArray erro = new JSONArray();

			JSONArray registros = new JSONArray();

			FileInputStream fis = new FileInputStream("D:/lista_bebidas.xlt");
			HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);
			HSSFSheet sheet = myWorkBook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();
			PreparedStatement st3;
			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();
				int nRowIndex = row.getRowNum();

				if (nRowIndex > 0) {
					String cod = addCellXLS(row, 0);
					String descfull = addCellXLS(row, 1);
					String descabrev = addCellXLS(row, 2);

					System.out.println(cod + "," + "" + descfull + "," + descabrev);
					if (!descfull.trim().equalsIgnoreCase("")) {
						sql = "INSERT INTO produtos (`ID_PROD`, `DESC_PROD`, `DESC_ABREVIADO`, `FLAG_ATIVO`) VALUES (?, ?, ?, ?);";
						st3 = conn.prepareStatement(sql);
						st3.setInt(1, Integer.parseInt(cod));
						st3.setString(2, descfull);
						st3.setString(3, descfull);
						st3.setString(4, "S");
						st3.executeUpdate();
					}
				}
			}

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

	public static void randomValuesProds(int iddistr) {

		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);

			String sql = " select * from produtos ";

			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				sql = "INSERT INTO produtos_distribuidora ( `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES ( ?, ?, ?, ?)";

				st = conn.prepareStatement(sql);
				st.setInt(1, rs.getInt("ID_PROD"));
				st.setInt(2, iddistr);
				Random r = new Random();
				double randomValue = 1 + (30 - 1) * r.nextDouble();
				st.setDouble(3, randomValue);
				st.setString(4, "S");
				st.executeUpdate();

			}

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
		readProds();
		randomValuesProds(1);
		randomValuesProds(2);

	}

}
