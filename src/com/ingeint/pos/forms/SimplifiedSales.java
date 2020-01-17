package com.ingeint.pos.forms;


import org.compiere.model.MProduct;
import java.util.LinkedList;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.Date;
import org.compiere.util.DB;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import org.compiere.minigrid.IMiniTable;
import org.compiere.util.Msg;
import org.compiere.util.Env;
import java.util.Vector;
import org.compiere.util.CLogger;

public class SimplifiedSales {
	public static CLogger log;

	static {
		SimplifiedSales.log = CLogger.getCLogger((Class) SimplifiedSales.class);
	}

	public Vector<String> getTableColumnNames() {
		final Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelCode"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelProd"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelUom"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelPrice"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelQty"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelTotal"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelTax"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelDestination"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelNotes"));
		columnNames.add(Msg.translate(Env.getCtx(), "SMJLabelIsSent"));
		return columnNames;
	}

	public void setTableColumnClass(final IMiniTable table) {
		int i = 0;
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, false);
		table.setColumnClass(i++, (Class) BigDecimal.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, true);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) String.class, true);
		table.autoSize();
	}

	protected Vector<Vector<Object>> getTmpWebSales(final Integer clientId, final Integer orgId) {
		final StringBuffer sql = new StringBuffer();
		sql.append(" SELECT t.smj_tmpWebSales_ID, b.name, t.created FROM smj_tmpWebSales t,  C_BPartner b ");
		sql.append(" WHERE t.isActive = 'Y' AND t.AD_Client_ID = " + clientId);
		sql.append(" AND t.AD_Org_ID = " + orgId);
		sql.append(" AND b.C_BPartner_ID = t.C_BPartner_ID ORDER BY name ASC ");
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		final Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = (PreparedStatement) DB.prepareStatement(sql.toString(), (String) null);
			rs = pstmt.executeQuery();
			Vector<Object> line = new Vector<Object>();
			while (rs.next()) {
				line = new Vector<Object>();
				line.add(rs.getInt("smj_tmpWebSales_ID"));
				final Timestamp cDate = rs.getTimestamp("created");
				final String dato = String.valueOf(rs.getString("name")) + " ** " + sdf.format(cDate);
				line.add(dato);
				data.add(line);
			}
		} catch (Exception e) {
			SimplifiedSales.log.log(Level.SEVERE, sql.toString(), (Throwable) e);
			return data;
		} finally {
			DB.close(rs, (Statement) pstmt);
			rs = null;
			pstmt = null;
		}
		DB.close(rs, (Statement) pstmt);
		rs = null;
		pstmt = null;
		return data;
	}

	

	protected LinkedList<Integer> getWOTmpLines(final Integer woId, final Integer tmpId) {
		final LinkedList<Integer> value = new LinkedList<Integer>();
		final StringBuffer sql = new StringBuffer();
		sql.append(" SELECT SMJ_TmpWebSalesLine_ID FROM SMJ_TmpWebSalesLine ");
		sql.append(" WHERE R_Request_ID = " + woId + " ");
		sql.append(" AND smj_TmpWebSales_ID = " + tmpId + " ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = (PreparedStatement) DB.prepareStatement(sql.toString(), (String) null);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				final Integer code = rs.getInt("SMJ_TmpWebSalesLine_ID");
				value.add(code);
			}
		} catch (Exception e) {
			SimplifiedSales.log.log(Level.SEVERE,
					String.valueOf(this.getClass().getCanonicalName()) + ".getWOTmpLines - ERROR: " + sql.toString(),
					(Throwable) e);
			return value;
		} finally {
			DB.close(rs, (Statement) pstmt);
			rs = null;
			pstmt = null;
		}
		DB.close(rs, (Statement) pstmt);
		rs = null;
		pstmt = null;
		return value;
	}
}
