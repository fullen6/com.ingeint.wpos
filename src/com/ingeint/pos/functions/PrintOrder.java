package com.ingeint.pos.functions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.adempiere.webui.component.Window;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.SimplePDFViewer;
import org.compiere.model.MOrder;

public class PrintOrder {

	public void printPOSOrder(MOrder order) {

		Window win;
		try {
			win = new SimplePDFViewer("NE #" + order.getDocumentNo(), new FileInputStream(order.createPDF()));
			win.setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
			SessionManager.getAppDesktop().showWindow(win, "center");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
