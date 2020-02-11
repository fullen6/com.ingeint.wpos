package com.ingeint.pos.forms;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
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
import com.ingeint.pos.util.Styles;

public class WHeaderForm extends CustomForm
		implements IFormController, WTableModelListener, ValueChangeListener, EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4874950749352354164L;

	public static final CLogger log = CLogger.getCLogger(WHeaderForm.class);

	Properties ctx = Env.getCtx();
	private CustomForm form;

	private Window mainLayout;
	private Window pgLayout;
	private Window pcustLayout;
	private Window pbarLayout;

	private Vbox vmainLayout;
	private Vbox gLayout;

	private Grid custLayout;

	private Rows custRows;

	private Button btnSave;
	private Button btnDiscart;
	private Button btnProcess;
	private Button btnPrint;
	private Button payButton;

	private Label organizationLabel;
	private Label warehouseLabel;

	private Textbox bpartnerTextBox;
	private WDateEditor entryDate;
	private WDateEditor dateScheduled;

	private WTableDirEditor docTypePick;
	private WTableDirEditor organizationPick;
	private WTableDirEditor fldPL;
	private WTableDirEditor fldWH;
	private WTableDirEditor fldSR;

	WSearchEditor fldBP;
	private MLookup lookupBP;

	private Label bPartnerLabel;
	private Label doctypeLabel;
	private Label priceListLabel;
	private Label documentNo;
	private Label lbldocumentNo;
	private Label lblSalesRep;
	private Label entryDateLabel;
	private Label lblDateScheduled;

	@Override
	protected void initForm() {

		this.mainLayout = new Window();

		this.pcustLayout = new Window();
		this.pcustLayout = new Window();
		this.pgLayout = new Window();
		this.pbarLayout = new Window();

		this.vmainLayout = new Vbox();
		this.gLayout = new Vbox();

		this.organizationLabel = new Label();
		this.warehouseLabel = new Label();
		this.bPartnerLabel = new Label();
		this.doctypeLabel = new Label();
		this.priceListLabel = new Label();
		this.documentNo = new Label();
		this.lbldocumentNo = new Label();
		this.lblSalesRep = new Label();
		this.entryDateLabel = new Label();
		this.lblDateScheduled = new Label();

		this.bpartnerTextBox = new Textbox();

		this.entryDate = new WDateEditor();
		this.dateScheduled = new WDateEditor();

		this.btnSave = new Button();
		this.btnDiscart = new Button();
		this.btnProcess = new Button();
		this.btnPrint = new Button();
		this.payButton = new Button();

		this.custLayout = GridFactory.newGridLayout();

		try

		{
			
			Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", "Y");

			dynInit();
			zkInit();
			this.addEventListener("onPayment", (EventListener) this);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void dynInit() throws Exception {

		try {

			// BPartner
			int AD_Column_ID = 2893;
			lookupBP = MLookupFactory.get(ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
			fldBP = new WSearchEditor(MOrder.COLUMNNAME_C_BPartner_ID, true, false, true, lookupBP);
			fldBP.addValueChangeListener(this);
			fldBP.setReadWrite(true);

			// Organization
			AD_Column_ID = 2163;
			MLookup lookupOrg = MLookupFactory.get(ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			organizationPick = new WTableDirEditor("AD_Org_ID", true, false, true, lookupOrg);
			organizationPick.setValue(Env.getAD_Org_ID(Env.getCtx()));
			organizationPick.addValueChangeListener(this);

			// DocType
			AD_Column_ID = 2172;
			MLookup lookupDT = MLookupFactory.get(ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			docTypePick = new WTableDirEditor(MOrder.COLUMNNAME_C_DocType_ID, true, false, true, lookupDT);
			docTypePick.addValueChangeListener(this);

			// PriceList
			AD_Column_ID = 2204;
			MLookup lookupPL = MLookupFactory.get(ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldPL = new WTableDirEditor(MOrder.COLUMNNAME_M_PriceList_ID, true, false, true, lookupPL);
			fldPL.addValueChangeListener(this);

			// Warehouse
			AD_Column_ID = 1151;
			MLookup lookupWH = MLookupFactory.get(ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldWH = new WTableDirEditor(MOrder.COLUMNNAME_M_Warehouse_ID, true, false, true, lookupWH);
			fldWH.addValueChangeListener(this);

			// SalesRep_ID
			AD_Column_ID = 2186;
			MLookup lookupSR = MLookupFactory.get(ctx, form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
			fldSR = new WTableDirEditor(MOrder.COLUMNNAME_SalesRep_ID, true, false, true, lookupSR);
			fldSR.addValueChangeListener(this);

			final Timestamp time = Env.getContextAsDate(Env.getCtx(), "#Date");
			this.entryDate.setValue((Object) time);
			this.dateScheduled.setValue((Object) time);

		} catch (Exception e) {
			log.log(Level.SEVERE,
					String.valueOf(this.getClass().getCanonicalName()) + ".dynInit - ERROR: " + e.getMessage(),
					(Throwable) e);
			// SessionManager.getAppDesktop().closeActiveWindow();

		}
	}

	private void zkInit() {

		Styles styles = new Styles();

		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.mainLayout, "1");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.mainLayout, "1");
		this.mainLayout.setParent((Component) form);
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this, "1");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.vmainLayout, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.vmainLayout, "1");
		this.vmainLayout.setStyle("margin-left: 5px;margin-top: 6px;");
		this.mainLayout.appendChild((Component) this.vmainLayout);
		this.pcustLayout.setBorder("normal");
		this.pcustLayout.setHflex("1");
		this.pcustLayout.setVflex("7");
		this.pcustLayout.setStyle(styles.getBorderStyle());
		this.pgLayout.setBorder("normal");
		this.pgLayout.setVflex("13");
		this.pgLayout.setHflex("1");
		this.pbarLayout.setBorder("normal");
		this.pbarLayout.setStyle(styles.getBorderStyle2());
		this.vmainLayout.setStyle("margin-left: 5px;margin-top: 6px;");
		this.mainLayout.appendChild((Component) this.vmainLayout);
		this.pcustLayout.setBorder("normal");
		this.pcustLayout.setHflex("1");
		this.pcustLayout.setVflex("7");
		this.pcustLayout.setStyle(styles.getBorderStyle());
		this.custLayout.setAlign("left");
		this.custLayout.setWidth("100%");
		(this.custRows = this.custLayout.newRows()).setHeight("20px");
		final Div overGLayout = new Div();
		overGLayout.setStyle("overflow:auto;position:relative");
		overGLayout.appendChild((Component) this.gLayout);
		overGLayout.setVflex("1");
		overGLayout.setHflex("2");
		ZKUpdateUtil.setHflex((HtmlBasedComponent) this.gLayout, "1");
		ZKUpdateUtil.setVflex((HtmlBasedComponent) this.gLayout, "1");
		this.pgLayout.appendChild((Component) overGLayout);

		this.bPartnerLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "customer")).toString());
		this.bPartnerLabel
				.setTooltiptext(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_BPartner_ID")).toString());
		this.doctypeLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_DocType")).toString());
		this.organizationLabel
				.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "Organization")).toString());
		this.priceListLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "PriceList")).toString());
		this.warehouseLabel
				.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "M_Warehouse_ID")).toString());
		this.lbldocumentNo.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "DocumentNo")).toString());
		this.lblSalesRep.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "SalesRep_ID")).toString());

		this.entryDateLabel.setText(new StringBuilder().append(Msg.translate(Env.getCtx(), "DateAcct")).toString());
		this.lblDateScheduled.setText(Msg.translate(Env.getCtx(), "DateOrdered"));

		this.btnSave.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "save")).toString());
		this.btnSave.addActionListener((EventListener) this);
		this.btnSave.setStyle(styles.getBigButtomStyle());

		this.btnDiscart.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Discart")).toString());
		this.btnDiscart.addActionListener((EventListener) this);
		this.btnDiscart.setStyle(styles.getBigButtomStyle());

		this.btnProcess.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Process")).toString());
		this.btnProcess.addActionListener((EventListener) this);
		this.btnProcess.setStyle(styles.getBigButtomStyle());

		this.btnPrint.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "Print")).toString());
		this.btnPrint.addActionListener((EventListener) this);
		this.btnPrint.setStyle(styles.getBigButtomStyle());

		final Hbox cbuttons = new Hbox();
		this.payButton.setStyle(styles.getBigButtomStyle());
		this.payButton.setLabel(new StringBuilder().append(Msg.translate(Env.getCtx(), "C_Payment_ID")).toString());
		this.payButton.addActionListener((EventListener) this);

		this.bpartnerTextBox.addEventListener("onBlur", (EventListener) this);

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

		// Organization
		row.appendChild((Component) new Space());
		organizationLabel.setStyle(styles.getColumnStyle());
		row.appendCellChild(organizationLabel);
		row.appendCellChild(organizationPick.getComponent(), 2);

		row = this.custRows.newRow(); // enter

		// this.bPartnerLabel.setWidth("100%");
		this.bPartnerLabel.setStyle(styles.getColumnStyle());
		row.appendCellChild(this.bPartnerLabel);
		row.appendCellChild(fldBP.getComponent(), 2);

		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());

		// DateOrdered
		this.lblDateScheduled.setStyle(styles.getColumnStyle());
		row.appendChild(this.lblDateScheduled);
		row.appendChild((Component) this.dateScheduled.getComponent());

		row = this.custRows.newRow(); // enter

		// DocType
		doctypeLabel.setStyle(styles.getColumnStyle());
		row.appendCellChild(doctypeLabel);
		ZKUpdateUtil.setHflex(docTypePick.getComponent(), "true");
		row.appendCellChild(docTypePick.getComponent(), 2);

		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());

		lbldocumentNo.setStyle(styles.getColumnStyle());
		row.appendCellChild(lbldocumentNo);
		documentNo.setStyle(styles.getColumnStyle());
		ZKUpdateUtil.setHflex(documentNo, "true");
		documentNo.setLeft("0%");
		row.appendCellChild(documentNo, 2);
		row.appendChild((Component) new Space());

		row = this.custRows.newRow(); // enter

		// PriceList
		priceListLabel.setStyle(styles.getColumnStyle());
		row.appendCellChild(priceListLabel);
		ZKUpdateUtil.setHflex(fldPL.getComponent(), "true");
		row.appendCellChild(fldPL.getComponent(), 2);
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());
		row.appendChild((Component) new Space());

		// SalesRep
		lblSalesRep.setStyle(styles.getColumnStyle());
		row.appendCellChild(lblSalesRep);
		row.appendCellChild(fldSR.getComponent(), 2);

		row = this.custRows.newRow(); // enter

		// Warehouse
		warehouseLabel.setStyle(styles.getColumnStyle());
		row.appendCellChild(warehouseLabel);
		ZKUpdateUtil.setHflex(fldWH.getComponent(), "true");
		row.appendCellChild(fldWH.getComponent(), 2);
		row.appendChild((Component) new Space());

		// row = this.custRows.newRow(); // enter

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
