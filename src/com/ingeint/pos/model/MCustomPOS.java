package com.ingeint.pos.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MPOS;
import org.compiere.model.Query;

public class MCustomPOS extends MPOS {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1372437376759575590L;

	public MCustomPOS(Properties ctx, int C_POS_ID, String trxName) {
		super(ctx, C_POS_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCustomPOS(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public static MCustomPOS getbyUser(int AD_Client_ID, int AD_Org_ID, int AD_User_ID) {
		
		String where = "AD_Client_ID = ? AND AD_Org_ID = ? AND SalesRep_ID = ? ";
		
		MCustomPOS pos = new Query(null, MCustomPOS.Table_Name, where, null)
				.setParameters(new Object[] {AD_Client_ID, AD_Org_ID, AD_User_ID})
				.first();
		
		return pos;
	}
}
