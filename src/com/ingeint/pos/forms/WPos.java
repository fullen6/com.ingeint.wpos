package com.ingeint.pos.forms;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.DocumentLink;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListCell;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Searchbox;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.ZkCssHelper;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.ingeint.pos.base.CustomForm;
import com.ingeint.pos.functions.CreateUpdateOrder;
import com.ingeint.pos.functions.PrintOrder;
import com.ingeint.pos.model.MCustomPOS;
import com.ingeint.pos.util.Styles;
import com.ingeint.pos.util.Utils;

public class WPos extends CustomForm
		implements IFormController, WTableModelListener, ValueChangeListener, EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7614729730751655335L;

	/** Logger */
	public static final CLogger log = CLogger.getCLogger(WPos.class);

	private Boolean isNew = true;
	ListModelTable modelOl;

	private Button btnSave;
	private Button btnExit;
	private Button btnProcess;
	private Button btnPrint;
	private Button cancelButton;
	private Button payButton;
	private Button orderSearch;

	private Rows custRows;
	private Grid custLayout;
	private Window mainLayout;
	private Vbox vmainLayout;
	private Window pcustLayout;
	private Hbox prodLayout;
	private Window pprodLayout;
	private Vbox gLayout;
	private Window pgLayout;
	private Hbox barLayout;
	private Window pbarLayout;
	private Vbox totalLayout;
	private Hbox dsLayout;
	private Hbox t1Layout;
	private Hbox t2Layout;
	private Hbox txLayout;
	private Window ptotalLayout;
	private Hbox tLine1;
	private Hbox tLine2;
	private Hbox tLineButtons;
	private WListbox dataTable;
	private Label productLabel;
	private Textbox productTextBox;
	private Label bPartnerLabel;
	private Label doctypeLabel;
	private Label priceListLabel;

	private Label lblTotal;
	private Label totalDecimalbox;
	private Label subTotalLabel;
	private Label subTotalDecimalbox;
	private Label lblTax;
	private Label taxDecimalbox;
	private Label dctLabel;
	private Label dctDecimalbox;
	private Label lblCredit;
	private Label entryDateLabel;
	private Label lblDateScheduled;
	private Label lbldocumentNo;

	private Label documentNo;
	private Label lblSalesRep;

	private Label organizationLabel;
	private Label warehouseLabel;

	private Textbox bpartnerTextBox;
	private WDateEditor entryDate;
	private WDateEditor dateScheduled;

	private BigDecimal grandTotal;
	private BigDecimal taxTotal;
	private BigDecimal subTotal;
	private MLookup lookupProduct;
	private MLookup lookupBP;

	private WTableDirEditor pricelistPick;
	private WTableDirEditor fldOR;
	private WTableDirEditor fldPL;
	private WTableDirEditor fldWH;
	private WTableDirEditor fldSR;
	private WTableDirEditor flUOM;
	private WTableDirEditor fldDT;

	Styles st = null;

	// PAYMENT INFO

	Window Payment;

	// END PAYMENT INFO

	DocumentLink lkOrder;

	MOrder newOrder;

	int AD_Org_ID = 0;
	int C_DocType_ID = 0;
	int C_BPartner_ID = 0;
	int M_Warehouse_ID = 0;
	int M_PriceList_ID = 0;
	int SalesRep_ID = 0;
	int M_Product_ID = 0;
	int C_POS_ID = 0;

	BigDecimal openAmt = Env.ZERO;

	Properties ctx = Env.getCtx();
	int LoginAD_User_ID = Env.getAD_User_ID(ctx);
	int LoginAD_Client_ID = Env.getAD_Client_ID(ctx);
	int LoginAD_Org_ID = Env.getAD_Org_ID(ctx);

	WSearchEditor fldBP;
	WSearchEditor fldPR;

	int COLUMNNAME_QTY = 3;
	int COLUMNNAME_PRICE = 4;
	int COLUMNNAME_DELETELINE = 8;

	MCustomPOS pos = MCustomPOS.getbyUser(LoginAD_Client_ID, LoginAD_Org_ID, LoginAD_User_ID);

	public Label getLbldocumentNo() {
		return lbldocumentNo;
	}

	public void setLbldocumentNo(Label lbldocumentNo) {
		this.lbldocumentNo = lbldocumentNo;
	}

	public int getAD_Org_ID() {
		return AD_Org_ID;
	}

	public void setAD_Org_ID(int aD_Org_ID) {
		AD_Org_ID = aD_Org_ID;
	}

	public int getC_BPartner_ID() {
		return C_BPartner_ID;
	}

	public void setC_BPartner_ID(int c_BPartner_ID) {
		C_BPartner_ID = c_BPartner_ID;
	}

	public int getC_DocType_ID() {
		return C_DocType_ID;
	}

	public void setC_DocType_ID(int c_DocType_ID) {
		C_DocType_ID = c_DocType_ID;
	}

	public int getM_Warehouse_ID() {
		return M_Warehouse_ID;
	}

	public void setM_Warehouse_ID(int m_Warehouse_ID) {
		M_Warehouse_ID = m_Warehouse_ID;
	}

	public int getM_PriceList_ID() {
		return M_PriceList_ID;
	}

	public void setM_PriceList_ID(int m_PriceList_ID) {
		M_PriceList_ID = m_PriceList_ID;
	}

	public int getSalesRep_ID() {
		return SalesRep_ID;
	}

	public void setSalesRep_ID(int salesRep_ID) {
		SalesRep_ID = salesRep_ID;
	}

	public int getC_POS_ID() {
		return C_POS_ID;
	}

	public void setC_POS_ID(int C_POSID) {
		C_POS_ID = C_POSID;
	}

	public BigDecimal getOpenAmt() {
		return openAmt;
	}

	public void setOpenAmt(BigDecimal openamt) {
		openAmt = openamt;
	}

	BigDecimal priceActual = Env.ZERO;
	BigDecimal qtyEntered = Env.ONE;

	private MProduct product;
	Boolean isForUpdate = false;

	@Override
	protected void initForm() {

		if (LoginAD_Org_ID == 0)
			throw new AdempiereException(Msg.translate(ctx, "Org0NotAllowed"));

		if (pos == null)
			throw new AdempiereException(Msg.translate(ctx, "NoPOSForUser"));

		try {
			initializeData();
		} catch (Exception e1) {
			FDialog.error(this.m_WindowNo, e1.toString());
			e1.printStackTrace();
		}

		this.mainLayout = new Window();
		this.vmainLayout = new Vbox();
		this.pcustLayout = new Window();
		this.custLayout = GridFactory.newGridLayout();
		this.prodLayout = new Hbox();
		this.pprodLayout = new Window();
		this.gLayout = new Vbox();
		this.pgLayout = new Window();
		this.barLayout = new Hbox();
		this.pbarLayout = new Window();
		this.totalLayout = new Vbox();
		this.dsLayout = new Hbox();
		this.t1Layout = new Hbox();
		this.t2Layout = new Hbox();
		this.txLayout = new Hbox();

		this.ptotalLayout = new Window();
		this.Payment = new Window();
		this.tLine1 = new Hbox();
		this.tLine2 = new Hbox();
		this.tLineButtons = new Hbox();

		this.dataTable = ListboxFactory.newDataTable();

		this.bPartnerLabel = new Label();
		this.doctypeLabel = new Label();
		this.priceListLabel = new Label();
		this.documentNo = new Label();
		this.lbldocumentNo = new Label();
		this.lblSalesRep = new Label();

		this.btnSave = new Button();
		this.btnExit = new Button();
		this.btnProcess = new Button();
		this.btnPrint = new Button();
		this.cancelButton = new Button();
		this.payButton = new Button();

		this.lblTotal = new Label();
		this.totalDecimalbox = new Label();
		this.subTotalLabel = new Label();
		this.subTotalDecimalbox = new Label();
		this.lblTax = new Label();
		this.taxDecimalbox = new Label();
		this.dctLabel = new Label();
		this.dctDecimalbox = new Label();
		this.lblCredit = new Label();
		this.organizationLabel = new Label();
		this.warehouseLabel = new Label();

		this.bpartnerTextBox = new Textbox();

		this.entryDateLabel = new Label();
		this.lblDateScheduled = new Label();
		this.entryDate = new WDateEditor();
		this.dateScheduled = new WDateEditor();

		this.grandTotal = Env.ZERO;
		this.taxTotal = Env.ZERO;
		this.subTotal = Env.ZERO;

		this.lookupProduct = null;
		this.lookupBP = null;

		try {
			this.dynInit();
			this.zkInit();
			this.addEventListener("onPayment", (EventListener) this);
			this.dataTable.addEventListener("onChange", (EventListener) this);

		}

		catch (Exception e) {
			log.log(Level.SEVERE,
					String.valueOf(this.getClass().getCanonicalName()) + ".dynInit - ERROR: " + e.getMessage(),
					(Throwable) e);
			SessionManager.getAppDesktop().closeActiveWindow();
		}

	}

	private void dynInit() throws Exception {

		try {

			// Organization
			int AD_Column_ID = 2163;
			MLookup lookupOrg = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, AD_Column_ID,
					DisplayType.TableDir);
			fldOR = new WTableDirEditor("AD_Org_ID", true, false, true, lookupOrg);
			fldOR.setValue(Env.getAD_Org_ID(Env.getCtx()));
			fldOR.addValueChangeListener(this);

			fldOR.setValue(getAD_Org_ID());

			// BPartner
			AD_Column_ID = 2893;
			lookupBP = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
			fldBP = new WSearchEditor(MOrder.COLUMNNAME_C_BPartner_ID, true, false, true, lookupBP);
			fldBP.addValueChangeListener(this);
			fldBP.setReadWrite(true);

			if (pos.getC_BPartnerCashTrx_ID() > 0) {
				fldBP.setValue(getC_BPartner_ID());
				C_BPartner_ID = (Integer) fldBP.getValue();
			}

			// Product
			AD_Column_ID = 1402;
			lookupProduct = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
			fldPR = new WSearchEditor(MOrderLine.COLUMNNAME_M_Product_ID, true, false, true, lookupProduct);
			fldPR.addValueChangeListener(this);
			fldPR.setReadWrite(true);

			// DocType
			AD_Column_ID = 2172;
			MLookup lookupDT = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldDT = new WTableDirEditor(MOrder.COLUMNNAME_C_DocType_ID, true, false, true, lookupDT);
			fldDT.addValueChangeListener(this);

			fldDT.setValue(getC_DocType_ID());

			// PriceList
			AD_Column_ID = 2204;
			MLookup lookupPL = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldPL = new WTableDirEditor(MOrder.COLUMNNAME_M_PriceList_ID, true, false, true, lookupPL);
			fldPL.addValueChangeListener(this);

			fldPL.setValue(getM_PriceList_ID());

			// Warehouse
			AD_Column_ID = 1151;
			MLookup lookupWH = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldWH = new WTableDirEditor(MOrder.COLUMNNAME_M_Warehouse_ID, true, false, true, lookupWH);
			fldWH.addValueChangeListener(this);

			fldWH.setValue(getM_Warehouse_ID());

			// SalesRep_ID
			AD_Column_ID = 2186;
			MLookup lookupSR = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldSR = new WTableDirEditor(MOrder.COLUMNNAME_SalesRep_ID, true, false, true, lookupSR);
			fldSR.addValueChangeListener(this);

			fldSR.setValue(getSalesRep_ID());

			// UOM
			AD_Column_ID = 2222;
			MLookup lookupUOM = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.Table);
			flUOM = new WTableDirEditor(MOrderLine.COLUMNNAME_C_UOM_ID, true, false, true, lookupUOM);
			flUOM.addValueChangeListener(this);

			final Timestamp time = Env.getContextAsDate(Env.getCtx(), "#Date");
			this.entryDate.setValue((Object) time);
			this.dateScheduled.setValue((Object) time);

		} catch (Exception e) {
			log.log(Level.SEVERE,
					String.valueOf(this.getClass().getCanonicalName()) + ".dynInit - ERROR: " + e.getMessage(),
					(Throwable) e);
		}
	}

	@SuppressWarnings("unchecked")
	private void zkInit() throws Exception {

		st = new Styles();

		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.mainLayout, "1");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.mainLayout, "1");
		this.mainLayout.setParent((Component) this);
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this, "1");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.vmainLayout, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.vmainLayout, "1");
		this.dataTable.setAttribute("org.zkoss.zul.nativebar", (Object) "true");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.dataTable, "1");
		this.vmainLayout.setStyle("margin-left: 5px;margin-top: 6px;");
		this.mainLayout.appendChild((Component) this.vmainLayout);
		this.pcustLayout.setBorder("normal");
		this.pcustLayout.setHflex("1");
		this.pcustLayout.setVflex("7");
		this.pcustLayout.setStyle(st.getBorderStyle());
		this.pprodLayout.setBorder("normal");
		this.pprodLayout.setHflex("1");
		this.pprodLayout.setVflex("2");
		this.pprodLayout.setStyle(st.getBorderStyle());
		this.pgLayout.setBorder("normal");
		this.pgLayout.setStyle(st.getBorderStyle());
		this.pgLayout.setVflex("13");
		this.pgLayout.setHflex("1");
		this.pbarLayout.setBorder("normal");
		this.pbarLayout.setStyle(st.getBorderStyle2());
		this.ptotalLayout.setBorder("normal");
		this.ptotalLayout.setStyle(st.getBorderStyle());
		this.ptotalLayout.setLeft("1px");
		this.ptotalLayout.setHflex("1");
		this.ptotalLayout.setVflex("4");
		this.custLayout.setAlign("left");
		this.custLayout.setWidth("100%");
		(this.custRows = this.custLayout.newRows()).setHeight("20px");
		this.prodLayout.setAlign("left");
		this.prodLayout.setWidths("4%,18%,60%,5%,5%,5%");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.prodLayout, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.prodLayout, "1");
		final Div overGLayout = new Div();
		overGLayout.setStyle("overflow:auto;position:relative");
		overGLayout.appendChild((Component) this.gLayout);
		overGLayout.setVflex("1");
		overGLayout.setHflex("2");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.gLayout, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.gLayout, "1");
		this.totalLayout.setAlign("left");
		this.totalLayout.setWidth("99%");
		this.tLine1.setWidth("99%");
		this.tLine1.setWidths("50%,5%,35%,10%,10%,10%,10%");
		this.tLine2.setWidth("99%");
		this.tLine2.setWidths("20%,5%,10%,5%,10%,10%,20%");
		this.tLineButtons.setWidth("100%");
		this.tLineButtons.setWidths("20%,5%,10%,5%,10%,10%,20%");
		this.vmainLayout.appendChild((Component) this.pcustLayout);
		this.pcustLayout.appendChild((Component) this.custLayout);
		//this.vmainLayout.appendChild((Component) this.pprodLayout);
		this.pprodLayout.appendChild((Component) this.prodLayout);
		this.vmainLayout.appendChild((Component) this.pgLayout);
		this.pgLayout.appendChild((Component) overGLayout);
		this.gLayout.appendChild((Component) this.dataTable);
		this.vmainLayout.appendChild((Component) this.ptotalLayout);
		this.ptotalLayout.appendChild((Component) this.totalLayout);
		this.totalLayout.appendChild((Component) this.tLine1);
		this.totalLayout.appendChild((Component) this.tLine2);
		this.totalLayout.appendChild((Component) this.tLineButtons);
		this.bPartnerLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "customer")).toString());
		this.bPartnerLabel
				.setTooltiptext(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_BPartner_ID")).toString());
		this.doctypeLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_DocType_ID")).toString());
		this.organizationLabel
				.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "Organization")).toString());
		this.priceListLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "PriceList")).toString());
		this.warehouseLabel
				.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "M_Warehouse_ID")).toString());
		this.lbldocumentNo.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "DocumentNo")).toString());
		this.lblSalesRep.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "SalesRep_ID")).toString());
		this.lblCredit.setText(Msg.translate(Env.getCtx(), "Credits"));

		this.lblTotal.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "Total")).toString());
		this.subTotalLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "SubTotal")).toString());
		this.lblTax.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_Tax_ID")).toString());
		this.dctLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "discount.amt")).toString());
		this.entryDateLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "DateAcct")).toString());
		this.lblDateScheduled.setText(Msg.translate(Env.getCtx(), "DateOrdered"));

		this.btnProcess.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Confirm")).toString());
		this.btnProcess.addEventListener(Events.ON_CLICK, this);
		//this.btnProcess.setImage(ThemeManager.getThemeResource("images/Process24.png"));
		this.btnProcess.setStyle(st.getBigButtomStyle());

		this.btnExit.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Logout")).toString());
		this.btnExit.addEventListener(Events.ON_CLICK, this);
		//this.btnExit.setImage(ThemeManager.getThemeResource("images/Previous24.png"));
		this.btnExit.setStyle(st.getBigButtomStyle());

		this.btnPrint
				.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Print")).toString().replace("&", ""));
		this.btnPrint.addEventListener(Events.ON_CLICK, this);
		//this.btnPrint.setImage(ThemeManager.getThemeResource("images/Print24.png"));
		this.btnPrint.setStyle(st.getBigButtomStyle());

		final Hbox cbuttons = new Hbox();
		this.payButton.setStyle(st.getBigButtomStyle());
		//this.payButton.setImage(ThemeManager.getThemeResource("images/Payment24.png"));
		this.payButton.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_Payment_ID")).toString());
		this.payButton.addEventListener(Events.ON_CLICK, this);

		orderSearch = new Button();
		orderSearch.setLabel(Msg.getMsg(ctx, "search") + " " + Msg.getMsg(ctx, "sales.order"));
		orderSearch.addEventListener(Events.ON_CLICK, this);
		orderSearch.setWidth("80%");
		//orderSearch.setImage(ThemeManager.getThemeResource("images/Find24.png"));
		orderSearch.setTooltiptext(Msg.getMsg(ctx, "Import") + " " + Msg.getMsg(ctx, "sales.order"));
		orderSearch.setStyle(st.getBigButtomStyle());

		Row row = this.custRows.newRow();

		row.appendChild((Component) new Space());

		// Principal Buttons
		this.btnProcess.setWidth("80%");
		row.appendCellChild((Component) this.btnProcess, 2);

		this.btnExit.setWidth("80%");
		row.appendCellChild((Component) this.btnExit, 2);

		this.btnPrint.setWidth("80%");
		row.appendCellChild((Component) this.btnPrint, 2);

		this.payButton.setWidth("80%");
		row.appendCellChild((Component) this.payButton, 2);

		this.orderSearch.setWidth("100%");
		row.appendCellChild((Component) this.orderSearch, 3);

		row = this.custRows.newRow(); // enter

		// this.bPartnerLabel.setWidth("100%");
		row.appendChild((Component) new Space());
		this.bPartnerLabel.setStyle(st.getColumnStyle());
		row.appendCellChild(this.bPartnerLabel);
		this.bPartnerLabel.setMandatory(true);
		row.appendCellChild(fldBP.getComponent(), 3);
		fldBP.showMenu();
		fldBP.setMandatory(true);
		// Organization

		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		organizationLabel.setStyle(st.getColumnStyle());
		row.appendCellChild(organizationLabel.rightAlign());
		row.appendCellChild(fldOR.getComponent(), 6);

		row = this.custRows.newRow(); // enter

		row.appendChild((Component) new Space());
		// DocType
		doctypeLabel.setStyle(st.getColumnStyle());
		row.appendCellChild(doctypeLabel);
		ZKUpdateUtil.setHflex(fldDT.getComponent(), "true");
		row.appendCellChild(fldDT.getComponent(), 3);

		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());

		// DateOrdered
		this.lblDateScheduled.setStyle(st.getColumnStyle());
		row.appendChild(this.lblDateScheduled);
		row.appendCellChild((Component) this.dateScheduled.getComponent(), 3);

		row = this.custRows.newRow(); // enter

		row.appendChild((Component) new Space());

		// PriceListtrx
		priceListLabel.setStyle(st.getColumnStyle());
		row.appendCellChild(priceListLabel);
		ZKUpdateUtil.setHflex(fldPL.getComponent(), "true");
		row.appendCellChild(fldPL.getComponent(), 3);
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());

		lbldocumentNo.setStyle(st.getColumnStyle());
		row.appendCellChild(lbldocumentNo);
		lkOrder.setStyle(st.getLabelStype());
		ZKUpdateUtil.setHflex(lkOrder, "true");
		lkOrder.setLeft("0%");
		row.appendCellChild(lkOrder, 4);
		row.appendChild((Component) new Space());

		row = this.custRows.newRow(); // enter

		row.appendChild((Component) new Space());
		// SalesRep
		lblSalesRep.setStyle(st.getColumnStyle());
		row.appendCellChild(lblSalesRep);
		row.appendCellChild(fldSR.getComponent(), 3);

		row.appendCellChild((Component) new Space());
		row.appendCellChild((Component) new Space());
		row.appendCellChild((Component) new Space());

		// Warehouse
		warehouseLabel.setStyle(st.getColumnStyle());
		row.appendCellChild(warehouseLabel);
		ZKUpdateUtil.setHflex(fldWH.getComponent(), "true");
		row.appendCellChild(fldWH.getComponent(), 2);
		row.appendChild((Component) new Space());

		row = this.custRows.newRow(); // enter

		// OrderLines
		this.dataTable.setStyle(
				"div.z-grid-header { background: none repeat scroll 0 0 #FFFFFF; border: 1px solid #CFCFCF; overflow: hidden; }");
			this.barLayout.setWidth("250px");
		this.dsLayout.setStyle(st.getLabelStype());

		this.dsLayout.setAlign("end");
		this.dctLabel.rightAlign();
		this.dctLabel.setStyle(st.getLabelStype());
		this.dsLayout.appendChild((Component) this.dctLabel);
		this.dctDecimalbox.rightAlign();
		ZkCssHelper.appendStyle((HtmlBasedComponent) this.dctDecimalbox, st.getLabelStype());
		this.dsLayout.appendChild((Component) this.dctDecimalbox);
		this.t1Layout.setStyle(st.getLabelStype());
		this.t1Layout.setAlign("right");
		this.subTotalLabel.rightAlign();
		this.subTotalLabel.setStyle(st.getColumnStyle());
		this.t1Layout.appendChild((Component) this.subTotalLabel);
		this.subTotalDecimalbox.setWidth("150px");
		this.subTotalDecimalbox.rightAlign();
		// this.subTotalDecimalbox.setText(this.nf.format(0L));
		this.subTotalDecimalbox.setStyle(st.getColumnStyle());
		this.t1Layout.appendChild((Component) this.subTotalDecimalbox);
		this.t1Layout.setWidth("200px");
		this.txLayout.setStyle(st.getLabelStype());
		this.txLayout.setAlign("end");
		this.lblTax.setStyle(st.getColumnStyle());
		this.lblTax.setWidth("150px");
		this.txLayout.appendChild((Component) this.lblTax);
		this.taxDecimalbox.rightAlign();
		// this.taxDecimalbox.setText(this.nf.format(0L));
		ZkCssHelper.appendStyle((HtmlBasedComponent) this.taxDecimalbox, st.getColumnStyle());
		this.txLayout.appendChild((Component) this.taxDecimalbox);
		this.txLayout.setWidth("200px");
		this.t2Layout.setStyle(st.getColumnStyle());
		this.t2Layout.setAlign("center");
		this.lblTotal.setStyle(st.getTotalStyle());
		this.t2Layout.appendChild((Component) this.lblTotal);
		// this.totalDecimalbox.setText(this.nf.format(0L));
		this.totalDecimalbox.setStyle(st.getTotalStyle());
		this.t2Layout.appendChild((Component) this.totalDecimalbox);
		this.t2Layout.setWidth("200px");
		this.t2Layout.setHeight("30px");
		this.tLine1.setStyle(st.getRowStyle());
		this.tLine1.setAlign("right");
		this.tLine1.setSpacing(st.getSpacing1());
		this.tLine1.appendChild((Component) this.dsLayout);
		this.tLine1.appendChild((Component) this.t1Layout);
		this.tLine2.setStyle(st.getRowStyle());
		this.tLine2.setAlign("right");
		this.tLine2.setSpacing(st.getSpacing2());
		this.tLine2.appendChild((Component) this.txLayout);
		this.tLine2.appendChild((Component) this.t2Layout);
		this.tLineButtons.setStyle(st.getRowStyle());
		this.tLineButtons.setAlign("right");
		// this.tLineButtons.setSpacing(st.getSpacing1());
		this.tLineButtons.appendChild((Component) this.barLayout);
		// this.tLineButtons.appendChild((Component) this.payButton);

		this.dataTable.addEventListener("onDoubleClick", (EventListener) new EventListener<Event>() {
			public void onEvent(final Event event) throws Exception {
				dataTableOnDoubleClick();
			}
		});

		this.dataTable.addEventListener("onClick", (EventListener) new EventListener<Event>() {
			public void onEvent(final Event event) throws Exception {
				dataTableOnCLick();
			}

		});

		Vector<String> columnNames = OrderLines.getColumnNames();
		Vector<Vector<Object>> data = OrderLines.setOrderLine(product, M_PriceList_ID, newOrder);
		modelOl = new ListModelTable(data);
		dataTable.setData(modelOl, columnNames);
		log.warning("Registros: " + dataTable.getRows());
		modelOl.addTableModelListener(this);
		dataTable.getModel().addTableModelListener(this);
		log.warning("Registros: " + dataTable.getRows());
		modelOl.removeFromSelection(7);

		OrderLines.setTableColumnClass((IMiniTable) this.dataTable);

		Utils.setWidths(this.dataTable.getListHead(), "8", "30", "16" ,"11", "11", "11", "11", "11", "11", "11");

		refreshContext();

	}

	public void createLines(MProduct product, MOrder order) throws IOException {
		Vector<String> columnNames = OrderLines.getColumnNames();
		Vector<Vector<Object>> data = OrderLines.setOrderLine(product, M_PriceList_ID, order);
		Vector<Vector<Object>> nullData = OrderLines.setOrderLine(null, 0, null);
		Vector<Object> emptyRow = nullData.get(0);
		data.add(emptyRow);
		
		modelOl = new ListModelTable(data);
		dataTable.setData(modelOl, columnNames);
		modelOl.addTableModelListener(this);
		dataTable.getModel().addTableModelListener(this);
		dataTable.setFocus(true);
		isNew = false;

		product = null;		
		
		dataTable.setSelectedIndex(data.indexOf(emptyRow));
		Events.postEvent(Events.ON_CLICK, dataTable, null);
		Events.postEvent(Events.ON_CHANGE, dataTable, data.indexOf(emptyRow)-1);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {

		if (evt.getPropertyName().equals(MOrderLine.COLUMNNAME_M_Product_ID)) {
			
			

			// New Order
			if (isNew) {
				CreateUpdateOrder co = new CreateUpdateOrder();
				newOrder = co.createUpdateOrder(0, this, null);

				lkOrder.setLabel(newOrder.getDocumentNo());
				lkOrder.setRecordId(newOrder.get_ID());
				//dataTable.clear();

			}

			if (evt.getNewValue() != null) {

				MProduct product = new MProduct(ctx, (Integer.valueOf(evt.getNewValue().toString())), null);

				try {

					createLines(product, newOrder);

					newOrder.load(null);
					isNew = false;

					Utils.setWidths(this.dataTable.getListHead(), "8", "30","16", "12", "12", "12", "12", "12", "12", "1");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			updateTotal(newOrder);
			setOpenAmt(newOrder.getGrandTotal());

		}

		if (evt.getPropertyName().equals(MOrder.COLUMNNAME_AD_Org_ID)) {
			if (evt.getNewValue() != null) {
				AD_Org_ID = ((Integer) evt.getNewValue()).intValue();
				fldOR.setValue(AD_Org_ID);
			} else {
				AD_Org_ID = 0;
				fldOR.setValue(null);
			}
		}

		if (evt.getPropertyName().equals(MOrder.COLUMNNAME_C_BPartner_ID)) {
			if (evt.getNewValue() != null) {
				C_BPartner_ID = ((Integer) evt.getNewValue()).intValue();
				fldBP.setValue(C_BPartner_ID);
			} else {
				C_BPartner_ID = 0;
				fldBP.setValue(null);
			}
		}

		if (evt.getPropertyName().equals(MOrder.COLUMNNAME_C_DocType_ID)) {
			if (evt.getNewValue() != null) {
				C_DocType_ID = ((Integer) evt.getNewValue()).intValue();
				fldDT.setValue(C_DocType_ID);
			} else {
				C_DocType_ID = 0;
				fldDT.setValue(null);
			}
		}

		if (evt.getPropertyName().contentEquals(MOrder.COLUMNNAME_M_Warehouse_ID)) {
			if (evt.getNewValue() != null) {
				M_Warehouse_ID = ((Integer) evt.getNewValue()).intValue();
				fldWH.setValue(M_Warehouse_ID);
			} else {
				M_Warehouse_ID = 0;
				fldWH.setValue(null);
			}
		}

		if (evt.getPropertyName().contentEquals(MOrder.COLUMNNAME_M_PriceList_ID)) {
			if (evt.getNewValue() != null) {
				M_PriceList_ID = ((Integer) evt.getNewValue()).intValue();
				fldPL.setValue(M_PriceList_ID);
			} else {
				M_PriceList_ID = 0;
				fldWH.setValue(null);
			}
		}

		if (evt.getPropertyName().equals(MOrder.COLUMNNAME_SalesRep_ID)) {
			if (evt.getNewValue() != null) {
				SalesRep_ID = ((Integer) evt.getNewValue()).intValue();
				fldSR.setValue(SalesRep_ID);
			} else {
				SalesRep_ID = 0;
				fldSR.setValue(null);
			}
		}

		
	}

	public void updateTotal(MOrderLine oline) {

		totalDecimalbox.setValue(oline.getC_Order().getGrandTotal().toString());
		setOpenAmt(oline.getC_Order().getGrandTotal());

	}

	public void updateTotal(MOrder order) {

		totalDecimalbox.setValue(order.getGrandTotal().toString());
		setOpenAmt(order.getGrandTotal());

	}

	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget() instanceof Button) {

			if (event.getTarget().equals(btnPrint)) {
				printOrder(newOrder);
			} else if (event.getTarget().equals(btnExit)) {
				this.dispose();
			} else if (event.getTarget().equals(payButton)) {

				if (getOpenAmt().signum() > 0) {

					WPayment payWin = new WPayment();
					if (newOrder == null)
						throw new AdempiereException(Msg.translate(ctx, "OrderNotGenerated"));
					AEnv.showWindow(payWin.PaymentWindow(newOrder, pos, this));

				} else
					throw new AdempiereException(Msg.translate(ctx, "IsPaid"));

			}

			else if (event.getTarget().equals(btnProcess)) {

				if (newOrder.getC_DocTypeTarget().getDocSubTypeSO().equals("WR")) {

					if (getOpenAmt().signum() > 0)
						throw new AdempiereException(Msg.translate(ctx, "unallocated.payments"));

					completeOrder(newOrder);

				}

			}
		}else if (event.getTarget() instanceof WListbox){
			int index = (int) event.getData();
			List<Component> children = dataTable.getChildren().get(index + 1).getChildren();
			ListCell cellQuantity = (ListCell) children.get(3);
			List<Component> componentsCellQuantity = cellQuantity.getChildren();
			NumberBox quantity = (NumberBox) componentsCellQuantity.get(0);
			quantity.setFocus(true);
		}
	}

	private void completeOrder(MOrder order) throws Exception {

		try {

			order.prepareIt();
			order.setDocAction(DocAction.ACTION_Complete);
			String StrTemp = order.completeIt();
			if (!StrTemp.equals(DocAction.ACTION_Complete)) {
				throw new AdempiereException(Msg.getMsg(ctx, order.getProcessMsg()));
			}

			order.setDocStatus(StrTemp);
			order.setDocAction(StrTemp);
			order.saveEx();
			order.setProcessed(true);

		} catch (Exception e) {
			FDialog.error(this.getWindowNo(), Msg.translate(ctx, e.toString()));
		}

		initializeData();
		clearForm();

	}

	private void printOrder(MOrder order) {

		if (order == null)
			return;

		PrintOrder po = new PrintOrder();
		po.printPOSOrder(order);
	}

	private void dataTableOnCLick() {
		final Integer index = this.dataTable.getSelectedIndex();

		if (index < 0) {
			return;
		}

		List<Component> children = dataTable.getChildren().get(index + 1).getChildren();
		ListCell cellProductSearch = (ListCell) children.get(1);
		List<Component> lsComponentProductSearch = cellProductSearch.getChildren();

		if (lsComponentProductSearch.size() == 0) {
			int AD_Column_ID = 2221;
			MLookup lookupPRD = MLookupFactory.get(ctx, this.m_WindowNo, 0, AD_Column_ID, DisplayType.Search);
			WSearchEditor fldPRD = new WSearchEditor("M_Product_ID", true, false, true, lookupPRD);
			fldPRD.addValueChangeListener(this);
			fldPRD.setReadWrite(true);
			fldPRD.setValue(null);
			Searchbox searchbox = fldPRD.getComponent();
			searchbox.setHflex(null);
			searchbox.setWidth("96%");
			lsComponentProductSearch.add(searchbox);
		}

	}

	private void dataTableOnDoubleClick() {
		dataTableOnCLick();
	}

	@Override
	public void tableChanged(WTableModelEvent event) {

		boolean isUpdate = (event.getType() == WTableModelEvent.CONTENTS_CHANGED);
		int column = event.getColumn();
		int row = event.getLastRow();

		// Is a table update

		if (isUpdate) {

			// Render now
			ListModelTable model = (ListModelTable) event.getModel();
			MOrderLine oline = new MOrderLine(ctx, (int) model.getDataAt(row, 9), null);

			if (column == COLUMNNAME_QTY) { // Updated QtyEntered
				BigDecimal qty = new BigDecimal(model.getDataAt(row, 3).toString());
				qtyEntered = qty;

				BigDecimal price = new BigDecimal(model.getDataAt(row, 5).toString());
				priceActual = price.setScale(2, RoundingMode.HALF_UP);
				
				oline.setQtyEntered(qtyEntered);
				oline.setQtyOrdered(qtyEntered);
				BigDecimal totalNetAmt = priceActual.multiply(qtyEntered).setScale(2, RoundingMode.HALF_UP);
				oline.setLineNetAmt(totalNetAmt);
				oline.saveEx();

				oline.load(null);
				newOrder.load(null);
				updateTotal(oline);
				
				model.setDataAt(priceActual.multiply(qtyEntered), row, 8);

			}

			if (column == COLUMNNAME_PRICE) { // Price updated

				BigDecimal price = new BigDecimal(model.getDataAt(row, 5).toString());
				priceActual = price;
				model.setDataAt(priceActual.multiply(qtyEntered), row, 8);
			}

			if (column == COLUMNNAME_DELETELINE) {

				log.warning("Toy");

			}

			log.warning("ID De Linea :" + oline.getC_OrderLine_ID());
		}
	}

	private void initializeData() throws Exception {

		newOrder = null;
		lkOrder = new DocumentLink(Msg.translate(ctx, "New"), MOrder.Table_ID, 0);
		setAD_Org_ID(pos.getAD_Org_ID());
		setC_BPartner_ID(pos.getC_BPartnerCashTrx_ID());
		setC_DocType_ID(pos.getC_DocType_ID());
		setM_Warehouse_ID(pos.getM_Warehouse_ID());
		setM_PriceList_ID(pos.getM_PriceList_ID());
		setSalesRep_ID(pos.getSalesRep_ID());
		setC_POS_ID(pos.getC_POS_ID());		
	}

	private void refreshContext() {

		Env.setContext(ctx, this.getWindowNo(), "M_PriceList_ID", getM_PriceList_ID());
		Env.setContext(ctx, this.getWindowNo(), "M_Warehouse_ID", getM_Warehouse_ID());
		Env.setContext(ctx, this.m_WindowNo, "IsSOTrx", "Y");
	}

	private void clearForm() throws IOException {

		isNew = true;
		newOrder = null;

		lkOrder = new DocumentLink(Msg.translate(ctx, "New"), MOrder.Table_ID, 0);
		lkOrder.setLabel("New");
		lkOrder.setRecordId(0);

		fldOR.setValue(getAD_Org_ID());
		fldBP.setValue(null);
		fldDT.setValue(getC_DocType_ID());
		fldPL.setValue(getM_PriceList_ID());
		fldSR.setValue(getSalesRep_ID());
		fldWH.setValue(getM_Warehouse_ID());
		subTotalDecimalbox.setValue(Env.ZERO.toString());
		totalDecimalbox.setValue(Env.ZERO.toString());
		taxDecimalbox.setValue(Env.ZERO.toString());
		product = null;

		dataTable.clear();
		Vector<String> columnNames = OrderLines.getColumnNames();
		Vector<Vector<Object>> data = OrderLines.setOrderLine(product, M_PriceList_ID, newOrder);
		modelOl = new ListModelTable(data);
		dataTable.setData(modelOl, columnNames);
		Utils.setWidths(this.dataTable.getListHead(), "8", "28", "16", "12", "12", "12", "12", "12", "12", "1");
	}

}
