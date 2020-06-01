package com.ingient.specialreference;

public class ProductVO {
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
