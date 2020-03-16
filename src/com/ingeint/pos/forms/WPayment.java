package com.ingeint.pos.forms;

import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.KeyStroke;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WNumberEditor;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Center;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Space;

@SuppressWarnings("rawtypes")
public class WPayment extends Payment implements EventListener {

	private Groupbox groupPanel;

	/** Default Font */
	private final String FONT_SIZE = "Font-size:medium;";
	/** Default Width */
	private final String HEIGHT = "height:33px;";
	/** Default Height */
	private final String WIDTH = "width:149px;";

	private Button bMinus;

	private Window selection;

	/** Panels */
	private String m_TenderType;
	private Grid v_StandarPanel;
	private Grid v_CheckPanel;
	private Grid v_CreditPanel;
	private Grid v_DebitPanel;
	private Properties p_ctx;
	private Listbox fTenderType;
	private WNumberEditor bAmountField = new WNumberEditor();

	private static Button btnCancel;
	private static Button btnProcess;

	/**
	 * Load standard Panel
	 * 
	 * @return void
	 */
	public void loadStandardPanel() {
		v_StandarPanel = GridFactory.newGridLayout();
		v_StandarPanel.setWidth("100%");
		v_StandarPanel.setHeight("75px");
		groupPanel.appendChild(v_StandarPanel);

		Rows rows = null;
		Row row = null;
		rows = v_StandarPanel.newRows();
		row = rows.newRow();

		// Payment type selection
		int AD_Column_ID = 8416; // C_Payment_v.TenderType
		MLookup lookup = MLookupFactory.get(Env.getCtx(), 0, 0, AD_Column_ID, DisplayType.List);
		ArrayList<Object> types = lookup.getData(true, false, true, true, true);

		// bMinus = v_Parent.createButtonAction("Delete",
		// KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.F3));
		// bMinus.addActionListener(this);
		row.setHeight("55px");

		fTenderType = ListboxFactory.newDropdownListbox();
		fTenderType.addActionListener(this);
		int pos = 0;
		// default to cash payment
		for (Object obj : types) {
			if (obj instanceof ValueNamePair) {
				ValueNamePair key = (ValueNamePair) obj;
				fTenderType.appendItem(key.getName(), key);

				/*
				 * if ( key.getID().equals(getTenderType())){ fTenderType.setSelectedIndex(pos);
				 * }
				 */
				pos++;
			}
		}

		fTenderType.setStyle(HEIGHT + WIDTH + FONT_SIZE);

		row.appendChild(fTenderType);

		Label lPayAmt = new Label(Msg.translate(p_ctx, "PayAmt"));
		lPayAmt.setWidth("225px");
		// TODO Verify
		row.appendChild(bAmountField.getComponent());
		row.appendChild(bMinus);
		bAmountField.setValue(new BigDecimal("0.0"));
		bAmountField.getComponent().addEventListener("onBlur", this);
		bAmountField.getComponent().addEventListener(Events.ON_CHANGING, this);
		bAmountField.getComponent().addEventListener(Events.ON_CHANGE, this);
	}

	public Window PaymentWindow() {

		selection = new Window();
		Panel mainPanel = new Panel();
		Panel panel = new Panel();
		selection.setTitle(Msg.translate(Env.getCtx(), "Payment"));
		Borderlayout mainLayout = new Borderlayout();
		Grid layout = GridFactory.newGridLayout();
		selection.appendChild(panel);
		selection.setWidth("800px");
		selection.setHeight("440px");
		// North
		Panel centerPanel = new Panel();
		mainPanel.appendChild(mainLayout);
		mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		//
		Center center = new Center();
		center.setStyle("border: none");
		mainLayout.appendChild(center);
		center.appendChild(centerPanel);
		centerPanel.appendChild(layout);
		layout.setWidth("100%");
		layout.setHeight("100%");
		selection.appendChild(mainPanel);

		v_StandarPanel = GridFactory.newGridLayout();
		v_StandarPanel.setWidth("100%");
		v_StandarPanel.setHeight("75px");

		Rows rows = null;
		Row row = null;
		rows = v_StandarPanel.newRows();
		row = rows.newRow();

		// Payment type selection
		int AD_Column_ID = 8416; // C_Payment_v.TenderType
		MLookup lookup = MLookupFactory.get(Env.getCtx(), 0, 0, AD_Column_ID, DisplayType.List);
		ArrayList<Object> types = lookup.getData(true, false, true, true, true);

		bAmountField.setValue(Env.ZERO);
		bAmountField.getComponent().addEventListener("onBlur", this);
		bAmountField.getComponent().addEventListener(Events.ON_CHANGING, this);
		bAmountField.getComponent().addEventListener(Events.ON_CHANGE, this);
		
		
		centerPanel.appendChild(bAmountField.getComponent());
		centerPanel.appendChild(v_StandarPanel);
		
		// Process Button
		btnProcess = new Button();
		btnProcess.setLabel(Msg.translate(Env.getCtx(), "Process").replace("&", ""));
		btnProcess.addEventListener(Events.ON_CLICK, this);
		selection.appendChild(btnProcess);

		selection.appendChild((Component) new Space());
		selection.appendChild((Component) new Space());
		selection.appendChild((Component) new Space());
		selection.appendChild((Component) new Space());
		selection.appendChild((Component) new Space());

		// Cancel Button
		btnCancel = new Button();
		btnCancel.setLabel(Msg.translate(Env.getCtx(), "Cancel").replace("&", ""));
		btnCancel.addEventListener(Events.ON_CLICK, this);
		selection.appendChild(btnCancel);

		
		return selection;

	}

	@Override
	public void onEvent(Event event) throws Exception {
		String temp = "";

		if (event.getTarget() instanceof Button) {

			if (event.getTarget().equals(btnCancel)) {

				selection.dispose();

			}

		}
	}

}
