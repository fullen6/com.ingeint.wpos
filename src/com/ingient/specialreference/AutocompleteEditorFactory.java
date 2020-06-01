package com.ingient.specialreference;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.factory.IEditorFactory;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.CLogger;

public class AutocompleteEditorFactory implements IEditorFactory{

	CLogger log = CLogger.getCLogger(AutocompleteEditorFactory.class);
	
	@Override
	public WEditor getEditor(GridTab gridTab, GridField gridField, boolean tableEditor) {
		if (gridField == null)
                {
                    return null; 
                }

                WEditor editor = null;
                int displayType = gridField.getDisplayType();


                /** Not a Field */
                if (gridField.isHeading())
                {
                    return null;
                }
                
                
                if(displayType == AutoCompleteTypeFactory.AutoComplete){
        	        log.warning("MY CUSTOM AUTOCOMPLETE DISPLAYTYPE");
        	        //editor = new WAutocompleteEditor(gridField, gridTab);
        	        //editor = new WAutocompleteEditorTest(gridField);
        	        //editor = new WStringEditorTest(gridField);
        	        editor = new WAutoCompleteEditor(gridField);
                }
                if(editor != null)
        	        editor.setTableEditor(tableEditor);
		
		return editor;
	}

}
