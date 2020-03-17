package com.ingeint.pos.forms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MOrder;
import org.compiere.model.MPOS;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.X_C_POSPayment;
import org.compiere.model.X_C_POSTenderType;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Center;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;

import com.ingeint.pos.util.Styles;

@SuppressWarnings("rawtypes")
public class WPayment extends Payment implements EventListener {

	private Groupbox groupPanel;

	MOrder newOrder = null;

	MPOS wpos = null;

	/** Default Font */
	private final String FONT_SIZE = "Font-size:medium;";
	/** Default Width */
	private final String HEIGHT = "height:33px;";
	/** Default Height */
	private final String WIDTH = "width:149px;";

	private Button bMinus;

	private Button btnCancelPayment;

	private Window selection;

	private Properties ctx = Env.getCtx();

	private Window winBox;
	X_C_POSTenderType TT;
	BigDecimal AmountReturned;

	ArrayList<ArrayList<Object>> dataPaymentID;
	Vector<Vector<Object>> dataPayment;

	private Component space = (Component) new Space();

	private Label lblTotalAmt;
	private Label lblAmt;

	WListbox lstPaymetSelect;

	Textbox txtTotalPayAmt;
	NumberBox txtPayAmt;
	Label lbRoutingNo;
	Label lbTotal;
	Label vlTotal;
	Textbox txtRoutingNo;
	Label lbLotNo;
	Textbox txtLotNo;
	Label lbPT;
	Listbox fldPT;
	MPaymentTerm PT;
	int mPT = 0;

	Listbox fldTT;
	int mTT = 0;

	Rows rows = null;
	Row row = null;

	private Button btnProcessPayment;
	private Button okButtonAmountReturned;

	Styles st = new Styles();

	public Window PaymentWindow(MOrder order, MPOS pos) throws Exception {

		newOrder = order;
		wpos = pos;
		vlTotal = new Label();
		vlTotal.setText(newOrder.getGrandTotal().toString());

		PaymentSearch();

		return this.selection;
	}

	private void ProcessPayment(int mOrder, MPOS wpos) {

		BigDecimal AmtTotalPayment = new BigDecimal(vlTotal.getValue());
		BigDecimal AmtTotalPaymentTemp = new BigDecimal(txtTotalPayAmt.getValue());
		BigDecimal PayAmt = new BigDecimal(txtPayAmt.getValue().toString());

		AmtTotalPaymentTemp = AmtTotalPaymentTemp.subtract(PayAmt);

		if (AmtTotalPaymentTemp.signum() >= 0) {
			X_C_POSPayment posPayment = new X_C_POSPayment(ctx, 0, wpos.get_TrxName());

			posPayment.setC_Order_ID(mOrder);
			posPayment.setAD_Org_ID(Env.getAD_Org_ID(ctx));
			posPayment.setC_POSTenderType_ID(TT.getC_POSTenderType_ID());
			posPayment.setTenderType(TT.getTenderType());
			posPayment.setPayAmt(PayAmt);
			posPayment.setRoutingNo(txtRoutingNo.getValue());
			posPayment.set_ValueOfColumn("C_POS_ID", wpos.getC_POS_ID());
			posPayment.saveEx();
		} else if (TT.getTenderType().equals(X_C_POSTenderType.TENDERTYPE_Cash)) {
			X_C_POSPayment posPayment = new X_C_POSPayment(ctx, 0, wpos.get_TrxName());

			posPayment.setC_Order_ID(mOrder);
			posPayment.setAD_Org_ID(wpos.getAD_Org_ID());
			posPayment.setC_POSTenderType_ID(TT.getC_POSTenderType_ID());
			posPayment.setTenderType(TT.getTenderType());
			posPayment.setPayAmt(new BigDecimal(txtTotalPayAmt.getValue()));
			posPayment.setRoutingNo(txtRoutingNo.getValue());
			posPayment.setDepositGroup(txtLotNo.getValue());
			posPayment.set_ValueOfColumn("C_POS_ID", wpos.getC_POS_ID());
			posPayment.saveEx();

			AmountReturned = AmtTotalPaymentTemp.multiply(new BigDecimal(-1));
			MessageAmountReturned();
		} else {
			throw new AdempiereException("El Monto indicado sobrepasa el Monto a Pagar");
		}

		loadDataPayment();

		BigDecimal AmtTotal = Env.ZERO;
		for (int i = 0; i < lstPaymetSelect.getRowCount(); i++) {
			AmtTotal = AmtTotal.add((BigDecimal) lstPaymetSelect.getValueAt(i, 2));
		}
		AmtTotal = AmtTotalPayment.subtract(AmtTotal);
		
		if(AmtTotal.equals(new BigDecimal("0.00")))
			selection.dispose();

		loadTenderType();

		txtTotalPayAmt.setValue(String.valueOf(AmtTotal));
		txtPayAmt.setValue(String.valueOf(AmtTotal));
		txtRoutingNo.setValue("");
		fldTT.setFocus(true);
	}

	private void buildPaymentDetails(Borderlayout mainLayout) throws Exception {
		Center centerLayout = new Center();
		centerLayout.setParent(mainLayout);
		centerLayout.setAutoscroll(true);

		Hlayout lyTable = new Hlayout();
		ZKUpdateUtil.setWidth(lyTable, "100%");
		ZKUpdateUtil.setHeight(lyTable, "100%");
		lyTable.setStyle("border: 2px solid lightgray; border-radius: 5px; overflow:auto; padding: 2px 3px;");
		lyTable.setParent(centerLayout);

		lstPaymetSelect = ListboxFactory.newDataTable();
		lstPaymetSelect.clear();
		lstPaymetSelect.getModel().removeTableModelListener(null);
		lstPaymetSelect.setOddRowSclass(null);
		// lstPaymetSelect.setSizedByContent(true);
		lstPaymetSelect.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "infoListbox");
		// lstPaymetSelect.addEventListener("onAfterRender", this);
		lstPaymetSelect.setSclass("z-word-nowrap");
		lstPaymetSelect.setStyle("overflow:auto;");
		ZKUpdateUtil.setVflex(lstPaymetSelect, "min");
		ZKUpdateUtil.setHflex(lstPaymetSelect, "min");
		lstPaymetSelect.setParent(lyTable);
	}

	private void loadDataPayment() {

		Vector<Vector<Object>> data = getDataRawPayment();
		Vector<String> columnNamesSearch = setColumnClassNamesPayment(lstPaymetSelect);

		ListModelTable modelI = new ListModelTable(data);
		modelI.addTableModelListener(null);

		lstPaymetSelect.setData(modelI, columnNamesSearch);
	}

	public Vector<Vector<Object>> getDataRawPayment() {

		Vector<Object> line = new Vector<Object>();

		line.add(dataPayment.size() + 1);
		line.add(TT.getName());
		line.add(txtPayAmt.getValue());
		line.add(txtRoutingNo.getValue());
		dataPayment.add(line);

		ArrayList<Object> line2 = new ArrayList<Object>();

		line2.add(dataPaymentID.size() + 1);
		line2.add(TT.getName());
		line2.add(txtPayAmt);
		line2.add(txtRoutingNo);

		line2.add(TT.getC_POSTenderType_ID());

		dataPaymentID.add(line2);

		return dataPayment;
	}

	public Vector<String> setColumnClassNamesPayment(IMiniTable mTable) {
		int i = 0;

		Vector<String> columnNames = new Vector<String>();

		mTable.setColumnClass(i++, Integer.class, true);
		columnNames.add(Msg.getMsg(ctx, "Line"));

		mTable.setColumnClass(i++, String.class, true);
		columnNames.add(Msg.getMsg(ctx, "payment.type").replace("&", ""));

		mTable.setColumnClass(i++, BigDecimal.class, true);
		columnNames.add(Msg.getMsg(ctx, "Amount").replace("&", ""));

		mTable.setColumnClass(i++, String.class, true);
		columnNames.add(Msg.getMsg(ctx, "AuthNo").replace("&", ""));

		return columnNames;
	}

	private void loadTenderType() {
		if (fldTT != null) {
			String sql = "select c_postendertype_id, name \n" + "from C_POSTenderType \n" + "where AD_Client_ID = "
					+ String.valueOf(Env.getAD_Client_ID(ctx)) + " \n" + "	and isactive = 'Y'";

			int ItemSel = 0;
			KeyNamePair[] processData = DB.getKeyNamePairs(sql, true);
			fldTT.removeAllItems();
			for (KeyNamePair item : processData) {
				fldTT.appendItem(item.getName(), item);
				if (mTT != 0)
					if (item.getKey() == mTT)
						ItemSel = fldTT.getItemCount() - 1;
			}
			fldTT.setSelectedIndex(ItemSel);
			TT = mTT != 0 ? new X_C_POSTenderType(ctx, mTT, null) : null;
			setVisibleTT();
		}
	}

	private void loadPaymentTerm() {
		if (fldPT != null) {
			String sql = "select C_PaymentTerm_ID, Name \n" + "from C_PaymentTerm \n" + "where AD_Client_ID = "
					+ String.valueOf(Env.getAD_Client_ID(ctx)) + " \n" + "	and IsActive = 'Y' \n"
					+ "	and IsDeployedPOS = 'Y' ";

			int ItemSel = 0;
			KeyNamePair[] processData = DB.getKeyNamePairs(sql, true);
			fldPT.removeAllItems();
			for (KeyNamePair item : processData) {
				fldPT.appendItem(item.getName(), item);
				if (mPT != 0)
					if (item.getKey() == mPT)
						ItemSel = fldPT.getItemCount() - 1;
			}
			fldPT.setSelectedIndex(ItemSel);
			PT = mPT != 0 ? new MPaymentTerm(ctx, mPT, null) : null;
		}
	}

	private void setVisibleTT() {
		if (TT == null) {
			lbRoutingNo.setVisible(false);
			txtRoutingNo.setVisible(false);

		} else {
			if (TT.getTenderType().equals(X_C_POSTenderType.TENDERTYPE_Cash)) {
				lbRoutingNo.setVisible(false);
				txtRoutingNo.setVisible(false);
			} else {
				lbRoutingNo.setVisible(true);
				txtRoutingNo.setVisible(true);
			}
		}
	}

	private void PaymentSearch() throws Exception {
		String msg = Msg.getMsg(ctx, "Payment");

		dataPayment = new Vector<Vector<Object>>();
		dataPaymentID = new ArrayList<ArrayList<Object>>();

		selection = new Window();
		selection.setTitle(msg);
		selection.setStyle("width: 40%; height: 45%; padding: 0; margin: 0");

		Panel mainPanel = new Panel();
		mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
		mainPanel.setParent(selection);

		Borderlayout mainLayout = new Borderlayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		mainLayout.setParent(mainPanel);

		buildPaymentParameters(mainLayout);
		buildPaymentDetails(mainLayout);
		buildPaymentProcess(mainLayout);

		AEnv.showWindow(selection);
	}

	private void buildPaymentParameters(Borderlayout mainLayout) throws Exception {

		vlTotal.setWidth("100%");
		vlTotal.setStyle("width:100%;" + st.getFONTSIZELARGE());

		lbRoutingNo = new Label(Msg.translate(ctx, MPayment.COLUMNNAME_R_AuthCode).replace("&", ""));
		lbRoutingNo.setWidth("100%");
		lbRoutingNo.setStyle("text-align: right; width:100%;");

		txtRoutingNo = new Textbox();
		txtRoutingNo.addEventListener(Events.ON_OK, this);
		txtRoutingNo.setStyle("text-align: left; width:100%;");
		ZKUpdateUtil.setWidth(txtRoutingNo, "100%");
		ZKUpdateUtil.setHeight(txtRoutingNo, "100%");

		// C_POSTenderType_ID
		fldTT = ListboxFactory.newDropdownListbox();
		fldTT.addActionListener(this);
		fldTT.setWidth("100%");
		loadTenderType();

		txtTotalPayAmt = new Textbox();
		txtTotalPayAmt.addEventListener(Events.ON_OK, this);
		txtTotalPayAmt.setReadonly(true);
		txtTotalPayAmt.setStyle("text-align: right; width:100%;");
		txtTotalPayAmt.setValue(vlTotal.getValue());
		ZKUpdateUtil.setWidth(txtTotalPayAmt, "100%");
		ZKUpdateUtil.setHeight(txtTotalPayAmt, "100%");

		txtPayAmt = new NumberBox(false);
		txtPayAmt.setValue(Env.ZERO);
		txtPayAmt.addEventListener(Events.ON_OK, this);
		txtPayAmt.setStyle("text-align: right; width:100%;");
		txtPayAmt.setValue(vlTotal.getValue());
		ZKUpdateUtil.setWidth(txtPayAmt, "100%");
		ZKUpdateUtil.setHeight(txtPayAmt, "100%");

		btnProcessPayment = new Button();
		btnProcessPayment.setLabel(Msg.getMsg(ctx, "Include").replace("&", ""));
		btnProcessPayment.addEventListener(Events.ON_CLICK, this);
		btnProcessPayment.setWidth("100%");
		btnProcessPayment.setStyle("width:100%;" + st.getFONTSIZELARGE());
		btnProcessPayment.setImage(ThemeManager.getThemeResource("images/SaveCreate24.png"));
		btnProcessPayment.setTooltiptext(Msg.getMsg(ctx, "Include").replace("&", ""));

		North topLayout = new North();
		topLayout.setStyle("border: none");
		topLayout.setParent(mainLayout);

		Panel parameterPanel = new Panel();
		parameterPanel.setParent(topLayout);

		Grid parameterLayout = GridFactory.newGridLayout();
		parameterLayout.setWidth("100%");
		parameterLayout.setParent(parameterPanel);

		rows = parameterLayout.newRows();

		row = rows.newRow();
		row.appendCellChild(new Label(Msg.getMsg(ctx, "payment.type").replace("&", "")).rightAlign());
		row.appendCellChild(fldTT, 2);

		row = rows.newRow();
		row.appendCellChild(new Label(Msg.translate(ctx, "OpenAmt").replace("&", "")).rightAlign());
		row.appendCellChild(txtTotalPayAmt, 1);
		row.appendCellChild(lbRoutingNo);
		row.appendCellChild(txtRoutingNo, 1);

		row = rows.newRow();
		row.appendCellChild(new Label(Msg.translate(ctx, MPayment.COLUMNNAME_PayAmt).replace("&", "")).rightAlign());
		row.appendCellChild(txtPayAmt, 1);

		row = rows.newRow();
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(btnProcessPayment, 3);
		row.appendCellChild(new Label("").rightAlign());
	}

	private void buildPaymentProcess(Borderlayout mainLayout) throws Exception {
		btnCancelPayment = new Button();
		// btnCancelPayment.setLabel(Msg.getMsg(ctx, "Cancel").replace("&", ""));
		btnCancelPayment.addEventListener(Events.ON_CLICK, this);
		btnCancelPayment.setWidth("100%");
		btnCancelPayment.setStyle("width:100%;" + st.getFONTSIZELARGE());
		btnCancelPayment.setImage(ThemeManager.getThemeResource("images/Cancel24.png"));
		btnCancelPayment.setTooltiptext(Msg.getMsg(ctx, "Cancel").replace("&", ""));

		South southLayout = new South();
		southLayout.setStyle("border: none");
		southLayout.setParent(mainLayout);

		Panel processPanel = new Panel();
		processPanel.setParent(southLayout);

		Grid processLayout = GridFactory.newGridLayout();
		processLayout.setWidth("100%");
		processLayout.setParent(processPanel);

		rows = processLayout.newRows();

		row = rows.newRow();
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(new Label("").rightAlign());
		// row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(btnCancelPayment);

	}

	private void MessageAmountReturned() {
		String msg = Msg.translate(Env.getCtx(), "AmountReturned").replace("&", "");

		winBox = new Window();
		winBox.setTitle(msg);
		winBox.setStyle("width: 400px; height: 200px; padding: 0px; margin: 0");

		Label lbDesc = new Label();
		lbDesc.setValue(Msg.translate(Env.getCtx(), "AmountReturned").replace("&", ""));
		lbDesc.setWidth("100%");
		lbDesc.setStyle("text-align: left; width:100%;" + st.getFONTSIZELARGE());

		Label lbPriceSTD = new Label();
		lbPriceSTD.setValue(String.valueOf(AmountReturned));
		lbPriceSTD.setWidth("100%");
		lbPriceSTD.setStyle("text-align: left; width:100%;" + st.getFONTSIZELARGE());

		okButtonAmountReturned = new Button();
		// yesButton.setLabel(Msg.getMsg(ctx, "Ok").replace("&", ""));
		okButtonAmountReturned.addEventListener(Events.ON_CLICK, this);
		okButtonAmountReturned.setWidth("100%");
		okButtonAmountReturned.setStyle("width:100%;" + st.getFONTSIZELARGE());
		okButtonAmountReturned.setImage(ThemeManager.getThemeResource("images/Ok24.png"));
		okButtonAmountReturned.setTooltiptext(Msg.getMsg(ctx, "Yes").replace("&", ""));

		Panel mainPanel = new Panel();
		mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
		mainPanel.setParent(winBox);

		Borderlayout mainLayout = new Borderlayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		mainLayout.setParent(mainPanel);

		Center centerLayout = new Center();
		centerLayout.setStyle("border: none; padding: 10px;");
		centerLayout.setParent(mainLayout);

		Panel messagePanel = new Panel();
		messagePanel.setParent(centerLayout);

		Grid messageLayout = GridFactory.newGridLayout();
		messageLayout.setWidth("100%");
		messageLayout.setParent(messagePanel);

		rows = messageLayout.newRows();

		row = rows.newRow();
		row.appendCellChild(lbDesc, 2);
		row.appendCellChild(lbPriceSTD, 2);

		South southLayout = new South();
		southLayout.setStyle("border: none; padding: 10px;");
		southLayout.setParent(mainLayout);

		Panel processPanel = new Panel();
		processPanel.setParent(southLayout);

		Grid processLayout = GridFactory.newGridLayout();
		processLayout.setWidth("100%");
		processLayout.setParent(processPanel);

		rows = processLayout.newRows();

		row = rows.newRow();
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(new Label("").rightAlign());
		row.appendCellChild(okButtonAmountReturned);

		AEnv.showWindow(winBox);
	}

	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget() instanceof Button) {

			if (event.getTarget().equals(btnCancelPayment)) {
				
				DB.executeUpdate("DELETE FROM C_POSPayment WHERE C_Order_ID = ? ", newOrder.getC_Order_ID(), null);

				selection.dispose();

			}

			else if (event.getTarget().equals(btnProcessPayment)) {

				if (TT == null) {
					throw new AdempiereException("Debe seleccionar un Tipo de Pago");
				}

				if (txtPayAmt.getValue() == null || txtPayAmt.getValue().signum() == 0) {
					throw new AdempiereException("Debe seleccionar un Monto a Pagar");
				}

				if (txtRoutingNo.isVisible() && txtRoutingNo.getValue().equals("")) {
					throw new AdempiereException("Debe seleccionar una Autorización No.");
				}

				ProcessPayment(newOrder.getC_Order_ID(), wpos);
			}

		}

		if (event.getTarget().equals(fldTT)) {
			int processId = ((KeyNamePair) fldTT.getSelectedItem().getValue()).getKey();
			if (processId == -1)
				TT = null;
			else
				TT = new X_C_POSTenderType(ctx, processId, null);
			setVisibleTT();
		}

		if (event.getTarget().equals(fldPT)) {
			int processId = ((KeyNamePair) fldPT.getSelectedItem().getValue()).getKey();
			if (processId == -1)
				PT = null;
			else
				PT = new MPaymentTerm(ctx, processId, null);
		}

	}

}
