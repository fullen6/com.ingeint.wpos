package com.ingeint.pos.forms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;

import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;

import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
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
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.ingeint.pos.base.CustomForm;

public class WSimplifiedSales extends CustomForm
		implements IFormController, WTableModelListener, ValueChangeListener, EventListener<Event> {

	private static int bigDecimalScale;
	private static final long serialVersionUID = 4169799434596051062L;
	public static CLogger log;
	private BigDecimal bPartnerDiscount;
	private Integer m_warehouse_id;
	private MOrder order;
	public final static int COLUMN_C_PERIOD_AD_ORG_ID = 839;

	private Rows custRows;
	private Window mainLayout;
	private Vbox vmainLayout;
	private Grid custLayout;
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

	private Button btnSave;
	private Button btnDiscart;
	private Button btnProcess;
	private Button btnPrint;
	private Button cancelButton;
	private Button payButton;
	private Label totalLabel;
	private Label totalDecimalbox;
	private Label subTotalLabel;
	private Label subTotalDecimalbox;
	private Label taxLabel;
	private Label taxDecimalbox;
	private Label dctLabel;
	private Label dctDecimalbox;
	private Label lblCredit;
	private Textbox bpartnerTextBox;
	private Button deleteAllButton;
	private Label entryDateLabel;
	private Label lblDateScheduled;
	private WDateEditor entryDate;
	private WDateEditor dateScheduled;

	private BigDecimal grandTotal;
	private BigDecimal taxTotal;
	private BigDecimal subTotal;
	private MLookup lookupProduct;
	private MLookup lookupBP;

	private int AD_Column_ID_BPartner;
	private int AD_Column_ID_Product;
	private int AD_Column_ID_MPriceList;
	private int AD_Column_ID_C_DocType_ID;
	
	private WTableDirEditor docTypePick;
	private WTableDirEditor pricelistPick;	
	private Label organizationLabel = new Label();
	private Label warehouseLabel = new Label();
	private WTableDirEditor organizationPick;
	private WTableDirEditor fldPL;
	private WTableDirEditor fldWH;
	
	Vector<Vector<Object>> dataProductSelect = new Vector<Vector<Object>>();
	Vector<Vector<Object>> dataProductSelectID = new Vector<Vector<Object>>();

	private NumberFormat nf;
	Checkbox chbBeneficiary;
	Textbox txtPoints;
	private Object tmp;
	WSearchEditor fldBP;
	WSearchEditor fldPR;
	Listbox fldDT;
	int mBP = 0;

	Properties ctx = Env.getCtx();

	@Override
	protected void initForm() {

		Env.setContext(Env.getCtx(), this.m_WindowNo, "IsSOTrx", "Y");
		this.bPartnerDiscount = Env.ZERO;
		this.m_warehouse_id = 0;

		this.mainLayout = new Window();
		this.vmainLayout = new Vbox();
		this.custLayout = GridFactory.newGridLayout();
		this.pcustLayout = new Window();
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
		this.tLine1 = new Hbox();
		this.tLine2 = new Hbox();
		this.tLineButtons = new Hbox();
		this.dataTable = ListboxFactory.newDataTable();
		this.productLabel = new Label();
		this.productTextBox = new Textbox();
		this.bPartnerLabel = new Label();
		this.doctypeLabel = new Label();
		this.priceListLabel = new Label();

		this.btnSave = new Button();
		this.btnDiscart = new Button();
		this.btnProcess = new Button();
		this.btnPrint = new Button();
	
		this.cancelButton = new Button();

		this.payButton = new Button();
		this.totalLabel = new Label();
		this.totalDecimalbox = new Label();
		this.subTotalLabel = new Label();
		this.subTotalDecimalbox = new Label();
		this.taxLabel = new Label();
		this.taxDecimalbox = new Label();
		this.dctLabel = new Label();
		this.dctDecimalbox = new Label();
		this.lblCredit = new Label();
		this.bpartnerTextBox = new Textbox();
	
		this.deleteAllButton = new Button();
	
		this.entryDateLabel = new Label();
		this.lblDateScheduled = new Label();
		this.entryDate = new WDateEditor();
		this.dateScheduled = new WDateEditor();
	
		this.grandTotal = Env.ZERO;
		this.taxTotal = Env.ZERO;
		this.subTotal = Env.ZERO;
		this.lookupProduct = null;
		this.lookupBP = null;
		this.AD_Column_ID_BPartner = 2893;
		this.AD_Column_ID_Product = 1402;
		this.AD_Column_ID_MPriceList = 2202;
		this.AD_Column_ID_C_DocType_ID = 2172;

		this.chbBeneficiary = new Checkbox();
		this.txtPoints = new Textbox();
		try {
			this.m_warehouse_id = Integer.parseInt(Env.getCtx().getProperty("#M_Warehouse_ID"));
		} catch (Exception ex) {
			this.m_warehouse_id = 0;
		}
		try {

			WSimplifiedSales.bigDecimalScale = MSysConfig.getIntValue("SMJ_POS_DECIMAL_NUMBERS", 0);
			this.grandTotal = this.grandTotal.setScale(WSimplifiedSales.bigDecimalScale, RoundingMode.HALF_UP);
			this.subTotal = this.subTotal.setScale(WSimplifiedSales.bigDecimalScale, RoundingMode.HALF_UP);
			this.bPartnerDiscount = this.bPartnerDiscount.setScale(WSimplifiedSales.bigDecimalScale,
					RoundingMode.HALF_UP);
			this.taxTotal = this.taxTotal.setScale(WSimplifiedSales.bigDecimalScale, RoundingMode.HALF_UP);

			this.dynInit();
			this.zkInit();
			this.addEventListener("onPayment", (EventListener) this);

		} catch (Exception e) {
			WSimplifiedSales.log.log(Level.SEVERE, "WSimplifiedSales constructor error", (Throwable) e);
			SessionManager.getAppDesktop().closeActiveWindow();
		}
	}

	private void dynInit() throws Exception {
		try {

			// BPartner
			lookupBP = MLookupFactory.get(ctx, this.getWindowNo(), 0, this.AD_Column_ID_BPartner, DisplayType.Search);
			fldBP = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);
			fldBP.addValueChangeListener(this);
			fldBP.setReadWrite(true);

			// Product
			lookupProduct = MLookupFactory.get(ctx, this.getWindowNo(), 0, this.AD_Column_ID_Product,
					DisplayType.Search);
			fldPR = new WSearchEditor("M_Product_ID", true, false, true, lookupProduct);
			fldPR.addValueChangeListener(this);
			fldPR.setReadWrite(true);
			
			// Organization filter selection
			int AD_Column_ID = COLUMN_C_PERIOD_AD_ORG_ID; //C_Period.AD_Org_ID (needed to allow org 0)
			MLookup lookupOrg = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			organizationPick = new WTableDirEditor("AD_Org_ID", true, false, true, lookupOrg);
			organizationPick.setValue(Env.getAD_Org_ID(Env.getCtx()));
			organizationPick.addValueChangeListener(this);

			// DocType
			AD_Column_ID = 2172;
			MLookup lookupDT = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			docTypePick = new WTableDirEditor("C_DocType_ID", true, false, true, lookupDT);
			docTypePick.addValueChangeListener(this);
						
			// PriceList
			AD_Column_ID = 2204;
			MLookup lookupPL = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldPL = new WTableDirEditor("M_PriceList_ID", true, false, true, lookupPL);
			fldPL.addValueChangeListener(this);
			
			// Warehouse
			AD_Column_ID = 2202;
			MLookup lookupWH = MLookupFactory.get(ctx, this.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldWH = new WTableDirEditor("M_Warehouse_ID", true, false, true, lookupWH);
			fldWH.addValueChangeListener(this);

			// this.listTmpSales();
			final Timestamp time = Env.getContextAsDate(Env.getCtx(), "#Date");
			this.entryDate.setValue((Object) time);
			this.dateScheduled.setValue((Object) time);
		
			// this.validateSalesRep();
		} catch (Exception e) {
			WSimplifiedSales.log.log(Level.SEVERE,
					String.valueOf(this.getClass().getCanonicalName()) + ".dynInit - ERROR: " + e.getMessage(),
					(Throwable) e);
		}
	}

	
	private void zkInit() throws Exception {

		final String borderStyle = "border: 1px solid #C0C0C0; border-radius:5px;";
		final String borderStyle2 = "border: 1px solid #C0C0C0; border-radius:5px;";
		final String totalStyle = "background:#7c7bad;Height:35px;Width:160px;border: 3px solid #D2D2FF;border-radius:8px;padding: 1px 1px;font-family: Arial, Helvetica, sans-serif;font-size: 16px;font-weight: normal; letter-spacing:0.1em; color: white";
		final String columnStyle = "font-size: 12px;font-weight:bold; color: black; ";
		final String labelStyle = "border: 0px solid #C0C0C0; border-radius:5px;font-size: 13px;font-weight: bolder; background:#ffffff; text-align: center;";

		final String buttonRed = "background:#7c7bad;Height:15px;Width:120px;border: 1px solid #D2D2FF;border-radius:4px;padding: 1px 1px;font-family: Arial; font-weight:bold; font-size: 12px;font-weight: 400; color: white";
		final String buttonGreen = "background:#7c7bad;Height:35px;Width:200px;border: 3px solid#848484;border-radius:10px;padding: 1px 1px;font-family: Arial, Helvetica, sans-serif;font-size: 12px;font-weight: normal; letter-spacing:0.1em; color: white";
		
		final String buttonTomato = "background:#7c7bad;Height:35px;Width:160px;border: 3px solid #D2D2FF;border-radius:8px;padding: 1px 1px;font-family: Arial, Helvetica, sans-serif;font-size: 12px;font-weight: normal; letter-spacing:0.1em; color: white";
		final String buttonYellow = "background:yellow;Height:35px;Width:200px;border: 3px solid#5b5a91;border-radius:8px;padding: 1px 1px;font-family: Arial, Helvetica, sans-serif;font-size: 12px;font-weight: bolder;color: black";
		final String rowStyle = "Height:30px; border-style: dotted;";
		final String spacing1 = "30rem";
		final String spacing2 = "120rem";
		
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.mainLayout, "1");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.mainLayout, "1");
		this.mainLayout.setParent((Component) this);
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this, "1");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.vmainLayout, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.vmainLayout, "1");
		this.dataTable.setAttribute("org.zkoss.zul.nativebar", (Object) "true");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.dataTable, "1");
		this.vmainLayout.setStyle("margin-left: 5px;margin-top: 5px;");
		this.mainLayout.appendChild((Component) this.vmainLayout);
		this.pcustLayout.setBorder("normal");
		this.pcustLayout.setHflex("1");
		this.pcustLayout.setVflex("6");
		this.pcustLayout.setStyle(borderStyle);
		this.pprodLayout.setBorder("normal");
		this.pprodLayout.setHflex("1");
		this.pprodLayout.setVflex("2");
		this.pprodLayout.setStyle(borderStyle);
		this.pgLayout.setBorder("normal");
		this.pgLayout.setStyle(borderStyle);
		this.pgLayout.setVflex("13");
		this.pgLayout.setHflex("1");
		this.pbarLayout.setBorder("normal");
		this.pbarLayout.setStyle(borderStyle2);
		this.ptotalLayout.setBorder("normal");
		this.ptotalLayout.setStyle(borderStyle);
		this.ptotalLayout.setLeft("1px");
		this.ptotalLayout.setHflex("1");
		this.ptotalLayout.setVflex("4");
		this.custLayout.setAlign("left");
		this.custLayout.setWidth("99%");
		
		//final Columns columns = this.getColumnsFlex("10", "18", "3", "0", "6", "9", "6", "9", "8", "9", "7", "9", "7");
		//this.custLayout.appendChild((Component)columns);
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
		this.prodLayout.setWidths("4%,18%,63%,5%,5%,5%");

		this.tLine1.setWidths("50%,5%,35%,10%,10%,10%,10%");
		this.tLine2.setWidth("99%");
		this.tLine2.setWidths("20%,5%,10%,5%,10%,10%,20%");
		this.tLineButtons.setWidth("99%");
		this.tLineButtons.setWidths("20%,5%,10%,5%,10%,10%,20%");
		this.vmainLayout.appendChild((Component) this.pcustLayout);
		this.pcustLayout.appendChild((Component) this.custLayout);
		this.vmainLayout.appendChild((Component) this.pprodLayout);
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
		this.bPartnerLabel.setTooltiptext(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_BPartner")).toString());
		this.doctypeLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_DocType")).toString());
		this.organizationLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "Organization")).toString());
		this.priceListLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "PriceList")).toString());
		this.warehouseLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "M_Warehouse_ID")).toString());
		this.lblCredit.setText(Msg.translate(Env.getCtx(), "Credits"));
		this.bpartnerTextBox.setTooltiptext(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_BPartner")).toString());

		this.productLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "M_Product_ID")).toString());
		this.productTextBox.addEventListener("onBlur", (EventListener) this);
		this.productTextBox.setTooltiptext(new StringBuilder().append(Msg.translate(Env.getCtx(), "XProduct")).toString());
		this.totalLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "Total")).toString());
		this.subTotalLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "SubTotal")).toString());
		this.taxLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_Tax_ID")).toString());
		this.dctLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "discount.amt")).toString());
		this.entryDateLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "DateAcct")).toString());
		this.lblDateScheduled.setText(Msg.translate(Env.getCtx(), "DateOrdered"));

		this.btnSave.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "save")).toString());
		this.btnSave.addActionListener((EventListener) this);
		this.btnSave.setStyle(buttonTomato);

		this.btnDiscart.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Discart")).toString());
		this.btnDiscart.addActionListener((EventListener) this);
		this.btnDiscart.setStyle(buttonTomato);

		this.btnProcess.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Process")).toString());
		this.btnProcess.addActionListener((EventListener) this);
		this.btnProcess.setStyle(buttonTomato);
		
		this.btnPrint.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Print")).toString());
		this.btnPrint.addActionListener((EventListener) this);
		this.btnPrint.setStyle(buttonTomato);
		
		final Hbox cbuttons = new Hbox();
		// cbuttons.appendChild((Component) this.newCButton);
		// cbuttons.appendChild((Component) new Space());
		//ZKUpdateUtil.setWidth((HtmlBasedComponent) cbuttons, "60px");
		this.cancelButton.setStyle(buttonTomato);
		this.cancelButton.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Borrar")).toString());
		this.cancelButton.addActionListener((EventListener) this);
		this.payButton.setStyle(buttonTomato);
		this.payButton.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "pay")).toString());
		this.payButton.addActionListener((EventListener) this);

		this.bpartnerTextBox.addEventListener("onBlur", (EventListener) this);
		this.deleteAllButton.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Exit")).toString());
		this.deleteAllButton.addActionListener((EventListener) this);
		this.deleteAllButton.setStyle(buttonYellow);

		Row row = this.custRows.newRow();
		
		// Principal Buttons
		this.btnSave.setWidth("100%");
		row.appendChild((Component) this.btnSave);
		
		row.appendChild((Component) this.btnDiscart);	
		this.btnPrint.setWidth("100%");
		row.appendChild((Component) this.btnPrint);
		this.btnProcess.setWidth("100%");
		row.appendChild((Component) this.btnProcess);
		row.appendChild((Component) new Space());
	
		//Organization
		row.appendChild((Component) new Space());		
		organizationLabel.setStyle(columnStyle);
		row.appendCellChild(organizationLabel);
		row.appendCellChild(organizationPick.getComponent(),2);

		row = this.custRows.newRow(); // enter

		//this.bPartnerLabel.setWidth("100%");
		this.bPartnerLabel.setStyle(columnStyle);
		row.appendCellChild(this.bPartnerLabel);
		row.appendCellChild(fldBP.getComponent(), 2);

		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());

		//DateOrdered
		this.lblDateScheduled.setStyle(columnStyle);		
		row.appendChild(this.lblDateScheduled);
		row.appendChild((Component) this.dateScheduled.getComponent());

		row = this.custRows.newRow(); // enter

		//DocType
		doctypeLabel.setStyle(columnStyle);
		row.appendCellChild(doctypeLabel);
		ZKUpdateUtil.setHflex(docTypePick.getComponent(), "true");
		row.appendCellChild(docTypePick.getComponent(),2);
		row = this.custRows.newRow(); // enter
		
		//PriceList
		priceListLabel.setStyle(columnStyle);
		row.appendCellChild(priceListLabel);
		ZKUpdateUtil.setHflex(fldPL.getComponent(), "true");
		row.appendCellChild(fldPL.getComponent(), 2);
		row.appendChild((Component) new Space());
		
		row = this.custRows.newRow(); // enter
		
		//Warehouse
		warehouseLabel.setStyle(columnStyle);
		row.appendCellChild(warehouseLabel);
		ZKUpdateUtil.setHflex(fldWH.getComponent(), "true");
		row.appendCellChild(fldWH.getComponent(), 2);
		row.appendChild((Component) new Space());
		
		row = this.custRows.newRow(); // enter
		
		//Products
		this.productLabel.setStyle(columnStyle);
		ZKUpdateUtil.setHflex((HtmlBasedComponent)this.productLabel, "2");
		prodLayout.appendChild((Component) productLabel);
		this.productTextBox.setWidth("80px");
		this.prodLayout.appendChild(fldPR.getComponent());
		
		prodLayout.appendChild((Component) new Space());	      
	        
	       
		// OrderLines
		this.dataTable.setStyle(
				"div.z-grid-header { background: none repeat scroll 0 0 #FFFFFF; border: 1px solid #CFCFCF; overflow: hidden; }");
		this.dataTable.setClass(
				"div.z-grid-header { background: none repeat scroll 0 0 #FFFFFF; border: 1px solid #CFCFCF; overflow: hidden; }");
		this.barLayout.setWidth("250px");
		this.dsLayout.setStyle(labelStyle);
		
		this.dsLayout.setAlign("end");
		this.dctLabel.rightAlign();
		this.dctLabel.setStyle(columnStyle);
		this.dsLayout.appendChild((Component) this.dctLabel);
		this.dctDecimalbox.rightAlign();
		ZkCssHelper.appendStyle((HtmlBasedComponent) this.dctDecimalbox, columnStyle);
		this.dsLayout.appendChild((Component) this.dctDecimalbox);
		this.t1Layout.setStyle(labelStyle);
		this.t1Layout.setAlign("right");
		this.subTotalLabel.rightAlign();
		this.subTotalLabel.setStyle(columnStyle);
		this.t1Layout.appendChild((Component) this.subTotalLabel);
		this.subTotalDecimalbox.setWidth("150px");
		this.subTotalDecimalbox.rightAlign();
		// this.subTotalDecimalbox.setText(this.nf.format(0L));
		this.subTotalDecimalbox.setStyle(columnStyle);
		this.t1Layout.appendChild((Component) this.subTotalDecimalbox);
		this.t1Layout.setWidth("200px");
		this.txLayout.setStyle(labelStyle);
		this.txLayout.setAlign("end");
		this.taxLabel.setStyle(columnStyle);
		this.taxLabel.setWidth("150px");
		this.txLayout.appendChild((Component) this.taxLabel);
		this.taxDecimalbox.rightAlign();
		// this.taxDecimalbox.setText(this.nf.format(0L));
		ZkCssHelper.appendStyle((HtmlBasedComponent) this.taxDecimalbox, columnStyle);
		this.txLayout.appendChild((Component) this.taxDecimalbox);
		this.txLayout.setWidth("200px");
		this.t2Layout.setStyle(columnStyle);
		this.t2Layout.setAlign("center");
		this.totalLabel.setStyle(totalStyle);
		this.t2Layout.appendChild((Component) this.totalLabel);
		// this.totalDecimalbox.setText(this.nf.format(0L));
		this.totalDecimalbox.setStyle(totalStyle);
		this.t2Layout.appendChild((Component) this.totalDecimalbox);
		this.t2Layout.setWidth("200px");
		this.t2Layout.setHeight("30px");
		this.tLine1.setStyle(rowStyle);
		this.tLine1.setAlign("right");
		this.tLine1.setSpacing(spacing1);
		this.tLine1.appendChild((Component) this.dsLayout);
		this.tLine1.appendChild((Component) this.t1Layout);
		this.tLine2.setStyle(rowStyle);
		this.tLine2.setAlign("right");
		this.tLine2.setSpacing(spacing2);
		this.tLine2.appendChild((Component) this.txLayout);
		this.tLine2.appendChild((Component) this.t2Layout);
		this.tLineButtons.setStyle(rowStyle);
		this.tLineButtons.setAlign("right");
		this.tLineButtons.setSpacing(spacing2);
		this.tLineButtons.appendChild((Component) this.barLayout);
		this.tLineButtons.appendChild((Component) this.payButton);
		this.tLineButtons.appendChild((Component) this.cancelButton);

		//this.bPartnerLabel.setClass("menu-href z-a");
	
	}
	
			
	public Vector<String> setColumnClassNames(IMiniTable mTable) {
		int i = 0;

		Vector<String> columnNames = new Vector<String>();

		mTable.setColumnClass(i++, Boolean.class, false);
		columnNames.add(Msg.getMsg(ctx, "Select"));

		mTable.setColumnClass(i++, Integer.class, true);
		columnNames.add(Msg.getMsg(ctx, "Line"));

		mTable.setColumnClass(i++, BigDecimal.class, true);
		columnNames.add(Msg.getMsg(ctx, "Qty"));

		mTable.setColumnClass(i++, BigDecimal.class, true);
		columnNames.add(Msg.getMsg(ctx, "CoPayment"));

		mTable.setColumnClass(i++, String.class, true);
		columnNames.add(Msg.getMsg(ctx, "Value"));

		mTable.setColumnClass(i++, String.class, true);
		columnNames.add(Msg.getMsg(ctx, "Name"));

		mTable.setColumnClass(i++, BigDecimal.class, true);
		columnNames.add(Msg.getMsg(ctx, "standard.price"));

		mTable.setColumnClass(i++, BigDecimal.class, true);
		columnNames.add(Msg.getMsg(ctx, "list.price"));

		mTable.setColumnClass(i++, BigDecimal.class, true);
		columnNames.add(Msg.getMsg(ctx, "Rate"));

		return columnNames;
	}

	 
	 
	 private Columns getColumnsFlex(final String... flexWeights) {
	        final Columns columns = new Columns();
	        for (final String flexWeight : flexWeights) {
	            final Column column = new Column();
	            ZKUpdateUtil.setHflex((HtmlBasedComponent)column, flexWeight);
	            ZKUpdateUtil.setVflex((HtmlBasedComponent)column, "1");
	            columns.appendChild((Component)column);
	        }
	        return columns;
	    }

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		// TODO Auto-generated method stub

	}
}