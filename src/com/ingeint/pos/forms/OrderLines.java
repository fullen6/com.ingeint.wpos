package com.ingeint.pos.forms;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class OrderLines {

	/** Logger */
	public static final CLogger log = CLogger.getCLogger(OrderLines.class);
	
	public DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);
	
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
		columnNames.add(Msg.translate(Env.getCtx(), MOrderLine.COLUMNNAME_C_OrderLine_ID));
		

		return columnNames;
	}

	public static void setTableColumnClass(final IMiniTable table) {
		int i = 0;
		table.setColumnClass(i++, (Class) Boolean.class, false);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, false);
		table.setColumnClass(i++, (Class) String.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, false);
		table.setColumnClass(i++, (Class) BigDecimal.class, true);
		table.setColumnClass(i++, (Class) BigDecimal.class, true);
		table.setColumnClass(i++, (Class) Integer.class, true);
				
		table.autoSize();
		
		
	}

	public static Vector<Vector<Object>> setOrderLine(MProduct product, Integer M_PriceList_ID, MOrder order) throws IOException {
		
		MOrderLine oline = createOrderLine(order, product.getM_Product_ID());
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> line = new Vector<Object>();
		line.add(true);
		line.add(product.getValue() + "_" + product.getName());
		line.add(Env.ONE);
		line.add("Unidad");
		line.add(getPriceActual(product.getM_Product_ID(), M_PriceList_ID));
		line.add(BigDecimal.valueOf(2.4));
		line.add(BigDecimal.valueOf(22.4));
		line.add(oline.getC_OrderLine_ID());
		data.add(line);
			
		return data;
	}

	public static BigDecimal getPriceActual(Integer M_Product_ID, Integer M_PriceList_ID) throws IOException {
		
		/*
		 * SqlBuilder sqlBuilder = SqlBuilder.builder();
		 * 
		 * String sql = sqlBuilder.template("readproductprice").build();
		 * 
		 * BigDecimal priceActual = DB.getSQLValueBD(null, sql, new Object[] {
		 * M_Product_ID, M_PriceList_ID }); if (priceActual == null) priceActual =
		 * Env.ZERO;
		 */

		//return priceActual;
		return Env.ONEHUNDRED;
	}
	
	private static MOrderLine createOrderLine(MOrder order, int M_Product_ID) {

		MOrderLine oline = new MOrderLine(null, 0, null);
		oline.setAD_Org_ID(order.getAD_Org_ID());
		oline.setC_Order_ID(order.getC_Order_ID());
		oline.setM_Product_ID(M_Product_ID);
		oline.setQty(Env.ONE);
		oline.setPriceActual(Env.ONE);
		oline.saveEx();
		
		return oline;

	}
	
	
}
