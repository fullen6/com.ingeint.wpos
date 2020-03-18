package com.ingeint.pos.forms;

import java.util.Properties;

import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Window;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class InfoBox  implements EventListener {
	
	
	private Window box;
	private Button btnOk;
	private Label lblMsg;
	
	Properties ctx;
	
	
	
	public Window newInfoBox(String Title, String msg) {
		
		ctx = Env.getCtx();
		
		box = new Window();
		box.setTitle(Title);
		box.setStyle("width: 40%; height: 45%; padding: 0; margin: 0");

		Panel mainPanel = new Panel();
		mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
		mainPanel.setParent(box);

		Borderlayout mainLayout = new Borderlayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		mainLayout.setParent(mainPanel);
		
		lblMsg = new Label();
		lblMsg.setText(msg);
		
		btnOk = new Button();
		btnOk.setName(Msg.translate(ctx, "OK"));
		btnOk.addEventListener(Events.ON_CLICK, this);
		
		return box;

	}
 
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
