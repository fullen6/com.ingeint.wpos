package com.ingeint.pos.forms;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MUOM;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import com.ingeint.pos.util.SqlBuilder;

public class OrderLines {

	public DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);
	private static SqlBuilder sqlBuilder;
	/** Logger */
	public static final CLogger log = CLogger.getCLogger(OrderLines.class);

	public static Vector<String> getColumnNames() {

		// Header Info
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.translate(Env.getCtx(), "Select"));
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_M_Product_ID));
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_QtyEntered));
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_C_UOM_ID));
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_PriceEntered));
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_C_Tax_ID));
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_LineNetAmt));

		return columnNames;
	}

	public static void setTableColumnClass(final IMiniTable table) {
		int i = 0;
		table.setColumnClass(i++, (Class) Boolean.class, false);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, false);
		table.setColumnClass(i++, (Class) String.class, false);
		table.setColumnClass(i++, (Class) BigDecimal.class, false);
		table.setColumnClass(i++, (Class) Integer.class, false);
		table.setColumnClass(i++, (Class) BigDecimal.class, true);
				
		table.autoSize();
		
	}

	public static Vector<Vector<Object>> setOrderLine(MProduct product, Integer M_PriceList_ID) throws IOException {
		
		MUOM uom = new MUOM(Env.getCtx(), product.getC_UOM_ID(), null);
				
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> line = new Vector<Object>();
		line.add(true);
		line.add(product.getValue() + "_" + product.getName());
		line.add(Env.ONE);
		line.add(uom.getName());
		line.add(Env.ONEHUNDRED);
		//line.add(getPriceActual(product.getM_Product_ID(), M_PriceList_ID));
		line.add(100);
		line.add(Env.ONEHUNDRED);
		data.add(line);
		
		return data;
	}

	public static BigDecimal getPriceActual(Integer M_Product_ID, Integer M_PriceList_ID) throws IOException {

		String sql = sqlBuilder.template("read-productPrice").build();

		BigDecimal priceActual = DB.getSQLValueBD(null, sql, new Object[] { M_Product_ID, M_PriceList_ID });
		if (priceActual == null)
			priceActual = Env.ZERO;

		return priceActual;

	}
}
