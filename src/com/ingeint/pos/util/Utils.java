package com.ingeint.pos.util;

import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Listheader;

public class Utils {	
	
	/** Logger */
	public static final CLogger log = CLogger.getCLogger(Utils.class);
	
	 public static void setWidths(final ListHead head, final String... widths) {
		
		  for (int i = 0; i < head.getChildren().size(); ++i) { ((HtmlBasedComponent)
		 head.getChildren().get(i)).setWidth(widths[i]); if
		 (widths[i].matches("[0-9]+")) {
		 ZKUpdateUtil.setHflex((HtmlBasedComponent)head.getChildren().get(i),
		 widths[i]); } ((Listheader) head.getChildren().get(i)).setSort("auto"); }
		 
	    }

}
