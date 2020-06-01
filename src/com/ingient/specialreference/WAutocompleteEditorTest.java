package com.ingient.specialreference;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.UUID;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.adempiere.webui.ValuePreference;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.AutoComplete;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ContextMenuEvent;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.window.WFieldRecordInfo;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.Lookup;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.MTest;
import org.compiere.model.StateChangeEvent;
import org.compiere.model.StateChangeListener;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Comboitem;

public class WAutocompleteEditorTest extends WEditor  implements StateChangeListener{

	
	private CLogger log = CLogger.getCLogger(WAutocompleteEditorTest.class);
	public final static String[] LISTENER_EVENTS = {Events.ON_SELECT};
	private Object oldValue;
	 
	public WAutocompleteEditorTest(GridField gridField, GridTab gridTab) {
		super(new WAutoCompleterProduct(gridTab.getWindowNo()), gridField);
		gridField.getGridTab().addStateChangeListener(this);
		WAutoCompleterProduct autoproduct = (WAutoCompleterProduct) this.getComponent();
		autoproduct.addEventListener(Events.ON_CHANGING, this);
		getComponent().addEventListener(Events.ON_BLUR, this);
		getComponent().addEventListener(Events.ON_SELECT, this);
	}
	 
	public WAutocompleteEditorTest(Component comp, GridField gridField) {
		super(comp, gridField);
		// TODO Auto-generated constructor stub
	}

	public WAutocompleteEditorTest(GridField gridField) {
		//super(new WAutoCompleterProduct(gridTab.getWindowNo()), gridField);
		super(new AutoComplete(),gridField);
		gridField.getGridTab().addStateChangeListener(this);
		this.getComponent().addEventListener(Events.ON_BLUR, this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		log.warning("------------");
		if (event.getTarget() instanceof AutoComplete) {
			GridTab gridTab = gridField.getGridTab();
			gridTab.getTableModel().setCompareDB(false);
			gridTab.setValue(gridField.getColumnName(), 1000000);
			setValue(1000000);
		}
		
	}

	@Override
	public void stateChange(StateChangeEvent event) {
		log.warning("-----------------");

		if (event.getEventType() == StateChangeEvent.DATA_SAVE) {
			//gridTab.setValue(gridField.getColumnName(), 1000000);			
		}
		
	}

	@Override
	public void setReadWrite(boolean readWrite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReadWrite() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValue(Object value) {
    	if (value != null && (value instanceof Integer || value instanceof String || value instanceof Timestamp || value instanceof BigDecimal))
        {
    		getComponent().setValue(value);
    		 if (!getComponent().isSelected(value))
             {
             	Object curValue = oldValue;
                 oldValue = value;
                 
             	/*if (isReadWrite() && lookup != null)
             	{
             		lookup.refresh();
             	}
             	else
             	{
                 	refreshList();
             	}*/
                 
                 //still not in list, reset to zero
                 if (!getComponent().isSelected(value))
                 {
                 	if (value instanceof Integer && gridField != null && gridField.getDisplayType() != DisplayType.ID && 
                 			(gridTab==null || !gridTab.getTableModel().isImporting())) // for IDs is ok to be out of the list
                 	{
                 		//if it is problem with record lock, just keep value (no trigger change) and set field readonly
                 		MRole role = MRole.getDefault(Env.getCtx(), false);
                 		int refTableID = -1;
                 		if (gridTab != null) // fields process para don't represent a column ID
                 		{
                     		MColumn col = MColumn.get(Env.getCtx(), gridField.getAD_Column_ID());
                     		if (col.get_ID() > 0) {
                     			String refTable = col.getReferenceTableName();
                     			if (refTable != null) {
                         			MTable table = MTable.get(Env.getCtx(), refTable);
                         			refTableID = table.getAD_Table_ID();
                     			}
                     		}
                 		}
                 		if (refTableID > 0 && ! role.isRecordAccess(refTableID, (int)value, false))
                 		{
                 			oldValue = value;
                 			setReadWrite(false);
                 			gridField.setLockedRecord(true);
                 		}
                 		else
                 		{
                 			//getComponent().setValue(null);
                 			if (curValue == null)
                 				curValue = value;
                 			ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), curValue, null);
                 			super.fireValueChange(changeEvent);
                 			oldValue = null;
                 			if (gridField!=null)
                 				gridField.setLockedRecord(false);
                 		}
                 	}
                 }
             }
        }
		
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplay() {
        String display = null;
        Comboitem selItem = getComponent().getSelectedItem();
        if (selItem != null)
        {
        	display = selItem.getLabel();
        }
        return display;
	}
	
    public String[] getEvents()
    {
        return LISTENER_EVENTS;
    }
    
    @Override
	public Combobox getComponent() {
		return (Combobox) component;
	}
    
}
