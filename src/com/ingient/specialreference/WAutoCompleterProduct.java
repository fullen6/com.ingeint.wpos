/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2009 SC ARHIPAC SERVICE SRL. All Rights Reserved.            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package com.ingient.specialreference;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.adempiere.exceptions.DBException;
import org.adempiere.webui.component.AutoComplete;
import org.compiere.model.MSysConfig;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Timer;



/**
 * @author Cristina Ghita - www.arhipac.ro
 *
 */
public class WAutoCompleterProduct extends AutoComplete implements EventListener<Event>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5130826429569842714L;

	private static final int PopupDelayMillis = 500;

	private final Timer timer = new Timer(PopupDelayMillis);

	private ProductVO m_product = null;

	private ArrayList<ProductVO> m_products = new ArrayList<ProductVO>();

	private ArrayList<ProductVO> m_productsShow = new ArrayList<ProductVO>();

	private final int m_maxRows = MSysConfig.getIntValue(MSysConfig.LOCATION_MAX_CITY_ROWS, 7);

	//public static final CityVO ITEM_More = new CityVO(-1, "...", -1, "");
	//public static final ProductVO ITEM_More = new ProductVO(-1, "...", -1, "");

	private final int m_windowNo;

	public WAutoCompleterProduct(int m_windowNo)
	{ 
		super();
		this.m_windowNo = m_windowNo;
		this.addEventListener(Events.ON_SELECT, this);
	}

	private void showPopupDelayed()
	{
		timer.setRepeats(false);
		timer.start();
	}

	@Override
	public void onChanging(InputEvent evt) 
	{
		showPopupDelayed();
		refreshData(evt.getValue());
		super.onChanging(evt);
	}

	public void refreshData(String val) 
	{
		String search = val;
		if (m_product != null && m_product.ProductName.contains(search))
		{
			setProduct(null);
		}
		m_productsShow.clear();
		this.removeAllItems();
		this.setDict(null);
		this.setDescription(null);
		boolean truncated = false;
		search = search.toUpperCase();
		int i = 0;
		for (ProductVO vo : m_products) {
			if (vo.ProductName.toUpperCase().contains(search)) {
				if (i > 0 && i == m_maxRows+1)
				{
					m_productsShow.add(new ProductVO(-1, "...", -1, ""));
					truncated = true;
					break;
				}
				m_productsShow.add(vo);
				i++;
			}
		}
		//if there is no city on the list return false, to not show the popup
		if (m_productsShow.isEmpty())
		{
			return;
		}
		else
		{
			ProductVO product = (ProductVO) m_productsShow.get(0);
			if (product.ProductName.equalsIgnoreCase(search))
			{
				m_product = product;
				return;
			}	
		}
		//if the list has only one item, but that item is not equals with m_city
		//return false to not show any popup
		if (!truncated && m_productsShow.size() == 1
				&& m_product != null && m_productsShow.get(0).equals(this.m_product))
		{
			return;
		}
		
		String[] productValues = new String[m_productsShow.size()];
		String[] productDesc = new String[m_productsShow.size()];
		i = 0;
		for (ProductVO vo : m_productsShow) {
			productValues[i] = vo.ProductName;
			productDesc[i] = vo.CategoryName;
			i++;
		}
		//
		this.removeAllItems();
		this.setDict(productValues);
		this.setDescription(productDesc);
	}

	public void fillList()
	{
		// Carlos Ruiz - globalqss - improve to avoid going to the database on every keystroke
		m_products.clear();
		m_productsShow.clear();
		ArrayList<Object> params = new ArrayList<Object>();
		/*final StringBuilder sql = new StringBuilder(
				"SELECT cy.C_City_ID, cy.Name, cy.C_Region_ID, r.Name"
				+" FROM C_City cy"
				+" LEFT OUTER JOIN C_Region r ON (r.C_Region_ID=cy.C_Region_ID)"
				+" WHERE cy.AD_Client_ID IN (0,?) AND cy.IsActive = 'Y'");*/
		final StringBuilder sql = new StringBuilder(
				"SELECT p.M_Product_ID, p.Name, pc.M_Product_Category_ID, pc.Name"
				+" FROM M_Product p"
				+" LEFT OUTER JOIN M_Product_Category pc ON (p.M_Product_Category_ID=pc.M_Product_Category_ID)"
				+" WHERE p.AD_Client_ID IN (0,?) AND p.IsActive = 'Y'");
		params.add(getAD_Client_ID());
		/*if (getC_Region_ID() > 0)
		{
			sql.append(" AND cy.C_Region_ID=?");
			params.add(getC_Region_ID());
		}		
		if (getC_Country_ID() > 0)
		{
			sql.append(" AND cy.C_Country_ID=?");
			params.add(getC_Country_ID());
		}*/
		sql.append(" ORDER BY p.Name, pc.Name");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			DB.setParameters(pstmt, params);
			rs = pstmt.executeQuery();
			int i = 0;
			while(rs.next())
			{
				//CityVO vo = new CityVO(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4));
				ProductVO vo = new ProductVO(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4));
				m_products.add(vo);
				if (i <= m_maxRows) {
					m_productsShow.add(vo);
				} else if (i == m_maxRows + 1 && i > 0) {
					m_productsShow.add(new ProductVO(-1, "...", -1, ""));
				}
				i++;
			}
		}
		catch (SQLException e)
		{
			throw new DBException(e, sql.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		refreshData("");
	}

	private void setProduct(ProductVO vo)
	{
		m_product = vo;
	}
	public int getM_Product_ID()
	{
		return m_product != null ? m_product.M_Product_ID : -1;
	}
	public int getAD_Client_ID()
	{
		return Env.getAD_Client_ID(Env.getCtx());
	}
	public int getC_Country_ID()
	{
		return Env.getContextAsInt(Env.getCtx(), m_windowNo, Env.TAB_INFO, "C_Country_ID");
	}
	public int getM_Product_Category_ID()
	{
		return Env.getContextAsInt(Env.getCtx(), m_windowNo, Env.TAB_INFO, "M_Product_Category_ID");
	}


	public void onEvent(Event event) throws Exception 
	{
		//System.out.println("Event: " + event.getName());
		//event.toString();
		int index = this.getSelectedIndex();
		//System.out.println("Index = " +index	);
		if (index>=0)
		{
			ProductVO product = (ProductVO) m_productsShow.get(index);
	
			if(event == null || product.equals(new ProductVO(-1, "...", -1, "")))
			{
				setProduct(null);
				return;
			}
	
			setProduct(product);
			Env.setContext(Env.getCtx(), m_windowNo, Env.TAB_INFO, "M_Product_Category_ID", String.valueOf(product.M_Product_Category_ID));
			try {
				this.setValue(product.ProductName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class ProductVO
	{
		public final int M_Product_ID;
		public final String ProductName;
		public final int M_Product_Category_ID;
		public final String CategoryName;
		public ProductVO(int Product_ID, String productName, int ProductCategory_ID, String categoryname)
		{
			super();
			M_Product_ID = Product_ID;
			ProductName = productName;
			M_Product_Category_ID = ProductCategory_ID;
			CategoryName = categoryname;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + M_Product_ID;
			result = prime * result + M_Product_Category_ID;
			result = prime * result + ((ProductName == null) ? 0 : ProductName.hashCode());
			result = prime * result + ((CategoryName == null) ? 0 : CategoryName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ProductVO other = (ProductVO) obj;
			if (M_Product_ID != other.M_Product_ID)
				return false;
			if (M_Product_Category_ID != other.M_Product_Category_ID)
				return false;
			if (ProductName == null)
			{
				if (other.ProductName != null)
					return false;
			}
			else if (!ProductName.equals(other.ProductName))
				return false;
			if (CategoryName == null)
			{
				if (other.CategoryName != null)
					return false;
			}
			else if (!CategoryName.equals(other.CategoryName))
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			if (this.ProductName != null)
			{
				sb.append(this.ProductName);
			}
			if (this.CategoryName != null)
			{
				sb.append(" (").append(this.CategoryName).append(")");
			}
			return sb.toString();
		}

	}

}
