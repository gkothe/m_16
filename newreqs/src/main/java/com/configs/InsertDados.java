package com.configs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	private static void dadosBairrosTeste() {
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

	private static void readProds() {

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

	private static void randomValuesProds(int iddistr) {

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
			conn.close();
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

	private static void insertRandomPedsAberto(int iddistr, int usuario, String dateini, String datefim, int qtdpedidos) {

		Connection conn = null;
		try {
			ResultSet rs;
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			PreparedStatement st;
			int ped = 0;
			String sql;
			double val_totalprod = 0;
			int qtdprods = ThreadLocalRandom.current().nextInt(1, 8 + 1);
			int countprod = 0;
			long offset = Timestamp.valueOf(dateini).getTime();
			long end = Timestamp.valueOf(datefim).getTime();
			int qtd = 0;
			StringBuffer varname1;
			long diff = end - offset + 1;
			Timestamp rand;
			long idpedido;
			Date dateped;
			String servico;
			int cod_bairro = 0;
			double VAL_TELE_ENTREGA = 0;
			ResultSet rs2;
			while (ped < qtdpedidos) {
				ped++;

				rand = new Timestamp(offset + (long) (Math.random() * diff));
				dateped = new Date(rand.getTime());
				idpedido = Utilitario.retornaIdinsertLong("pedido", "id_pedido", conn);

				// val_totalprod,val_entrega_value

				servico = Math.random() < 0.5 ? "T" : "L";

				cod_bairro = 0;
				VAL_TELE_ENTREGA = 0;
				if (servico.equalsIgnoreCase("T")) {

					sql = " select * from bairros ORDER BY RAND() limit 1  ";
					st = conn.prepareStatement(sql);
					rs2 = st.executeQuery();
					if (rs2.next()) {
						cod_bairro = rs2.getInt("cod_bairro");
					}

					sql = " select * from distribuidora where ID_DISTRIBUIDORA =  " + iddistr;
					st = conn.prepareStatement(sql);
					rs2 = st.executeQuery();
					if (rs2.next()) {
						VAL_TELE_ENTREGA = rs2.getDouble("VAL_TELE_ENTREGA");
					}

				}

				varname1 = new StringBuffer();
				varname1.append("INSERT INTO pedido ");
				varname1.append("            (id_pedido, ");
				varname1.append("             id_distribuidora, ");
				varname1.append("             id_usuario, ");
				varname1.append("             data_pedido, ");
				varname1.append("             flag_status, ");
//				varname1.append("             data_pedido_resposta, ");
				varname1.append("             num_ped, ");
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             cod_bairro, ");
				}
				varname1.append("             num_telefonecontato_cliente, ");
				varname1.append("             tempo_estimado_entrega, ");
				varname1.append("             desc_endereco_entrega, ");
				varname1.append("             desc_endereco_num_entrega, ");
				varname1.append("             desc_endereco_complemento_entrega, ");
				varname1.append("             flag_vizualizado, ");
				varname1.append("             flag_modopagamento, ");
				varname1.append("             desc_cartao, ");
				varname1.append("             nome_pessoa, ");
				varname1.append("             pag_token, ");
				varname1.append("             pag_mail, ");
				varname1.append("             pag_payid_tipocartao, ");
				varname1.append("             flag_pedido_ret_entre, ");
				varname1.append("             tempo_estimado_desejado,flag_modoentrega) ");
				varname1.append(" VALUES      (" + idpedido + ",");
				varname1.append("             " + iddistr + ", ");
				varname1.append("             " + usuario + ", ");
				varname1.append("             '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateped) + "', "); //data_pedido
				varname1.append("             'A', ");//aberto
//				varname1.append("             '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateped) + "', ");//resposta
				varname1.append("             " + Utilitario.getNextNumpad(conn, iddistr) + ", ");
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             " + cod_bairro + ", ");
				}
				varname1.append("             '999999999', ");//telefone
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             '00:00:00', "); //tempo estmiado entrega
				} else {
					varname1.append("             '00:00:00', ");//tempo estmiado entrega
				}

				varname1.append("             'Rua borges de medeiros', ");
				varname1.append("             '715', ");
				varname1.append("             'Sobrado', ");
				varname1.append("             'S', ");//vizualizado
				varname1.append("             '" + (Math.random() < 0.5 ? "D" : "C") + "', "); //pagamento
				varname1.append("             '', ");//nome
				varname1.append("             'Gabriel Dalcin Kothe', ");
				varname1.append("             '', ");
				varname1.append("             '', ");
				varname1.append("             '', ");
				varname1.append("             '" + servico + "', "); //entrega ou retirada
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             '01:00:00' ,  ");
				} else {
					varname1.append("             '00:00:00' ,");
				}
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             'T' ) ");
				} else {
					varname1.append("             '' ) ");
				}

				st = conn.prepareStatement(varname1.toString());
				st.executeUpdate();
				val_totalprod = 0;
				qtdprods = ThreadLocalRandom.current().nextInt(1, 15 + 1);
				countprod = 0;
				while (countprod < qtdprods) {
					sql = "select * from  produtos_distribuidora where id_distribuidora = " + iddistr + " and id_prod in (select id_prod from produtos  ORDER BY RAND() ) ORDER BY RAND()";
					st = conn.prepareStatement(sql);
					rs = st.executeQuery();
					if (rs.next()) {
						qtd = ThreadLocalRandom.current().nextInt(1, 15 + 1);
						varname1 = new StringBuffer();
						varname1.append("INSERT INTO pedido_item ");
						varname1.append("  ( `ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`) ");
						varname1.append("VALUES ");
						varname1.append("  (" + idpedido + ", " + Utilitario.retornaIdinsertChaveSecundaria("pedido_item", "id_pedido", idpedido + "", "SEQ_ITEM", conn) + ", " + rs.getDouble("VAL_PROD") + ", " + rs.getInt("ID_PROD") + ", " + qtd + ")");
						val_totalprod = val_totalprod + (qtd * rs.getDouble("VAL_PROD"));
						st = conn.prepareStatement(varname1.toString());
						st.executeUpdate();
					} else {
						throw new Exception("erro1");
					}

					countprod++;
				}

				if (countprod == 0) {
					throw new Exception("erro2");
				}

				sql = " update pedido set val_totalprod =  " + val_totalprod + " , VAL_ENTREGA = " + VAL_TELE_ENTREGA + " where id_pedido  = " + idpedido;
				st = conn.prepareStatement(sql);
				st.executeUpdate();

			}

			/*
			 * String sql = " select * from produtos ";
			 * 
			 * PreparedStatement st = conn.prepareStatement(sql); ResultSet rs = st.executeQuery(); while (rs.next()) {
			 * 
			 * sql = "INSERT INTO produtos_distribuidora ( `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES ( ?, ?, ?, ?)";
			 * 
			 * st = conn.prepareStatement(sql); st.setInt(1, rs.getInt("ID_PROD")); st.setInt(2, iddistr); Random r = new Random(); double randomValue = 1 + (30 - 1) * r.nextDouble(); st.setDouble(3, randomValue); st.setString(4, "S"); st.executeUpdate();
			 * 
			 * }
			 */

			conn.commit();
			conn.close();
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

	private static void insertRandomPeds(int iddistr, int usuario, String dateini, String datefim, int qtdpedidos) {

		Connection conn = null;
		try {
			ResultSet rs;
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			PreparedStatement st;
			int ped = 0;
			String sql;
			double val_totalprod = 0;
			int qtdprods = ThreadLocalRandom.current().nextInt(1, 8 + 1);
			int countprod = 0;
			long offset = Timestamp.valueOf(dateini).getTime();
			long end = Timestamp.valueOf(datefim).getTime();
			int qtd = 0;
			StringBuffer varname1;
			long diff = end - offset + 1;
			Timestamp rand;
			long idpedido;
			Date dateped;
			String servico;
			int cod_bairro = 0;
			double VAL_TELE_ENTREGA = 0;
			ResultSet rs2;
			while (ped < qtdpedidos) {
				ped++;

				rand = new Timestamp(offset + (long) (Math.random() * diff));
				dateped = new Date(rand.getTime());
				idpedido = Utilitario.retornaIdinsertLong("pedido", "id_pedido", conn);

				// val_totalprod,val_entrega_value

				servico = Math.random() < 0.5 ? "T" : "L";

				cod_bairro = 0;
				VAL_TELE_ENTREGA = 0;
				if (servico.equalsIgnoreCase("T")) {

					sql = " select * from bairros ORDER BY RAND() limit 1  ";
					st = conn.prepareStatement(sql);
					rs2 = st.executeQuery();
					if (rs2.next()) {
						cod_bairro = rs2.getInt("cod_bairro");
					}

					sql = " select * from distribuidora where ID_DISTRIBUIDORA =  " + iddistr;
					st = conn.prepareStatement(sql);
					rs2 = st.executeQuery();
					if (rs2.next()) {
						VAL_TELE_ENTREGA = rs2.getDouble("VAL_TELE_ENTREGA");
					}

				}

				varname1 = new StringBuffer();
				varname1.append("INSERT INTO pedido ");
				varname1.append("            (id_pedido, ");
				varname1.append("             id_distribuidora, ");
				varname1.append("             id_usuario, ");
				varname1.append("             data_pedido, ");
				varname1.append("             flag_status, ");
				varname1.append("             data_pedido_resposta, ");
				varname1.append("             num_ped, ");
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             cod_bairro, ");
				}
				varname1.append("             num_telefonecontato_cliente, ");
				varname1.append("             tempo_estimado_entrega, ");
				varname1.append("             desc_endereco_entrega, ");
				varname1.append("             desc_endereco_num_entrega, ");
				varname1.append("             desc_endereco_complemento_entrega, ");
				varname1.append("             flag_vizualizado, ");
				varname1.append("             flag_modopagamento, ");
				varname1.append("             desc_cartao, ");
				varname1.append("             nome_pessoa, ");
				varname1.append("             pag_token, ");
				varname1.append("             pag_mail, ");
				varname1.append("             pag_payid_tipocartao, ");
				varname1.append("             flag_pedido_ret_entre, ");
				varname1.append("             tempo_estimado_desejado,flag_modoentrega) ");
				varname1.append(" VALUES      (" + idpedido + ",");
				varname1.append("             " + iddistr + ", ");
				varname1.append("             " + usuario + ", ");
				varname1.append("             '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateped) + "', "); //data_pedido
				varname1.append("             'O', ");//aberto
				varname1.append("             '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateped) + "', ");//resposta
				varname1.append("             " + Utilitario.getNextNumpad(conn, iddistr) + ", ");
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             " + cod_bairro + ", ");
				}
				varname1.append("             '999999999', ");//telefone
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             '01:00:00', "); //tempo estmiado entrega
				} else {
					varname1.append("             '00:00:00', ");//tempo estmiado entrega
				}

				varname1.append("             'Rua borges de medeiros', ");
				varname1.append("             '715', ");
				varname1.append("             'Sobrado', ");
				varname1.append("             'S', ");//vizualizado
				varname1.append("             '" + (Math.random() < 0.5 ? "D" : "C") + "', "); //pagamento
				varname1.append("             '', ");//nome
				varname1.append("             'Gabriel Dalcin Kothe', ");
				varname1.append("             '', ");
				varname1.append("             '', ");
				varname1.append("             '', ");
				varname1.append("             '" + servico + "', "); //entrega ou retirada
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             '01:00:00' ,  ");
				} else {
					varname1.append("             '00:00:00' ,");
				}
				if (servico.equalsIgnoreCase("T")) {
					varname1.append("             'T' ) ");
				} else {
					varname1.append("             '' ) ");
				}

				st = conn.prepareStatement(varname1.toString());
				st.executeUpdate();
				val_totalprod = 0;
				qtdprods = ThreadLocalRandom.current().nextInt(1, 15 + 1);
				countprod = 0;
				while (countprod < qtdprods) {
					sql = "select * from  produtos_distribuidora where id_distribuidora = " + iddistr + " and id_prod in (select id_prod from produtos  ORDER BY RAND() ) ORDER BY RAND()";
					st = conn.prepareStatement(sql);
					rs = st.executeQuery();
					if (rs.next()) {
						qtd = ThreadLocalRandom.current().nextInt(1, 15 + 1);
						varname1 = new StringBuffer();
						varname1.append("INSERT INTO pedido_item ");
						varname1.append("  ( `ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`) ");
						varname1.append("VALUES ");
						varname1.append("  (" + idpedido + ", " + Utilitario.retornaIdinsertChaveSecundaria("pedido_item", "id_pedido", idpedido + "", "SEQ_ITEM", conn) + ", " + rs.getDouble("VAL_PROD") + ", " + rs.getInt("ID_PROD") + ", " + qtd + ")");
						val_totalprod = val_totalprod + (qtd * rs.getDouble("VAL_PROD"));
						st = conn.prepareStatement(varname1.toString());
						st.executeUpdate();
					} else {
						throw new Exception("erro1");
					}

					countprod++;
				}

				if (countprod == 0) {
					throw new Exception("erro2");
				}

				sql = " update pedido set val_totalprod =  " + val_totalprod + " , VAL_ENTREGA = " + VAL_TELE_ENTREGA + " where id_pedido  = " + idpedido;
				st = conn.prepareStatement(sql);
				st.executeUpdate();

			}

			/*
			 * String sql = " select * from produtos ";
			 * 
			 * PreparedStatement st = conn.prepareStatement(sql); ResultSet rs = st.executeQuery(); while (rs.next()) {
			 * 
			 * sql = "INSERT INTO produtos_distribuidora ( `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES ( ?, ?, ?, ?)";
			 * 
			 * st = conn.prepareStatement(sql); st.setInt(1, rs.getInt("ID_PROD")); st.setInt(2, iddistr); Random r = new Random(); double randomValue = 1 + (30 - 1) * r.nextDouble(); st.setDouble(3, randomValue); st.setString(4, "S"); st.executeUpdate();
			 * 
			 * }
			 */

			conn.commit();
			conn.close();
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
		// readProds();
		// randomValuesProds(1);
		// randomValuesProds(2);
		// insertRandomPeds(1, 17, "2016-01-01 00:00:00", "2016-10-31 23:59:59", 500);
		 insertRandomPeds(1, 27, "2015-01-01 00:00:00", "2016-10-31 23:59:59", 500);
		 insertRandomPeds(1, 27, "2015-01-01 00:00:00", "2016-10-31 23:59:59", 500);
		 insertRandomPeds(1, 27, "2015-01-01 00:00:00", "2016-10-31 23:59:59", 500);
		 insertRandomPeds(1, 27, "2016-01-01 00:00:00", "2016-10-31 23:59:59", 500);
		// insertRandomPeds(1, 17, "2016-01-01 00:00:00", "2016-10-31 23:59:59", 500);
		// insertRandomPeds(1, 17, "2016-01-01 00:00:00", "2016-10-31 23:59:59", 500);
//		   insertRandomPedsAberto(1, 17, "2016-12-6 8:45:00", "2016-12-6 8:58:00", 10); 
		// insertRandomPeds(1, 17, "2015-01-01 00:00:00", "2015-11-30 23:59:59", 500);
//		 insertRandomPeds(1, 17, "2015-01-01 00:00:00", "2015-11-30 23:59:59", 500);
//		 insertRandomPeds(1, 17, "2015-01-01 00:00:00", "2015-11-30 23:59:59", 500);
		// insertRandomPeds(1, 17, "2015-01-01 00:00:00", "2015-11-30 23:59:59", 500);
		// insertRandomPeds(1, 17, "2015-01-01 00:00:00", "2015-11-30 23:59:59", 500);
		//
		// try {
		// int cont = 0;
		// while (cont < 1000) {
		// cont++;
		// System.out.println(ThreadLocalRandom.current().nextInt(1, 3 + 1));
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// ;
		// // TODO: handle exception
		// }
	}

}
