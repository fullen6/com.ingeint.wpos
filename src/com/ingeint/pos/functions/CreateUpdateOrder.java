package com.ingeint.pos.functions;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrder;
import org.compiere.util.Env;
import org.compiere.util.Trx;

import com.ingeint.pos.forms.WPos;

public class CreateUpdateOrder {

	public MOrder createUpdateOrder(Integer C_Order_ID, WPos wp, String trx) {

		if (wp.getAD_Org_ID() <= 0 || wp.getC_BPartner_ID() <= 0 || wp.getC_DocType_ID() <= 0
				|| wp.getM_PriceList_ID() <= 0 || wp.getM_Warehouse_ID() <= 0 || wp.getSalesRep_ID() <= 0)
			throw new AdempiereException("Debe completar los campos del encabezado");

		String p_TrxName = null;
		Trx OrderTrx = null;
		p_TrxName = Trx.createTrxName();
		OrderTrx = Trx.get(p_TrxName, true);

		MOrder order = null;

		if (C_Order_ID > 0)
			order = new MOrder(Env.getCtx(), C_Order_ID, p_TrxName);
		else
			order = new MOrder(Env.getCtx(), 0, p_TrxName);

		order.setIsSOTrx(true);
		order.setAD_Org_ID(wp.getAD_Org_ID());
		order.setC_BPartner_ID(wp.getC_BPartner_ID());
		order.setC_DocTypeTarget_ID(wp.getC_DocType_ID());
		order.setM_Warehouse_ID(wp.getM_Warehouse_ID());
		order.setSalesRep_ID(wp.getSalesRep_ID());
		order.setM_PriceList_ID(wp.getM_PriceList_ID());
		order.setPaymentRule(MOrder.PAYMENTRULE_MixedPOSPayment);
		order.setC_POS_ID(wp.getC_POS_ID());
	
		order.saveEx();

		try {
			OrderTrx.commit();
		} catch (Exception e) {
			OrderTrx.rollback();
			throw new AdempiereException(e.getMessage());
		} finally {
			OrderTrx.close();
		}

		return order;

	}

}
