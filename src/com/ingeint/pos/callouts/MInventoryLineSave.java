package com.ingeint.pos.callouts;

import java.awt.event.KeyEvent;

import org.compiere.model.MInventoryLine;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import com.ingeint.pos.base.CustomCallout;

public class MInventoryLineSave extends CustomCallout {
	

	@Override
	protected String start() {

		if (getTab().getValue(MInventoryLine.COLUMNNAME_Description) != null
				&& getTab().getValue(MInventoryLine.COLUMNNAME_Description).equals("S")) {
			
			getTab().dataSave(true);
			getTab().dataNew(false);
			getTab().refreshParentTabs();
			
			onOK();
			
		
			
		}
		
		return null;
	}
	
	private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {
		  if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
		      System.out.println("pRueba");
		   }
		} 
	
	
	@Wire
    private Textbox username;
    @Wire
    private Textbox password;
 
    @Listen("onOK = #form")
    public void onOK() {
        //handle login
        System.out.println("ok");
    }
		
}

			


