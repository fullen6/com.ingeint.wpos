select pp.pricelist, pl.name, plv.name as version 
from m_pricelist pl
join m_pricelist_version plv on plv.m_pricelist_id = pl.m_pricelist_id
join m_productprice pp on pp.m_pricelist_version_id = plv.m_pricelist_version_id
join M_Product p on p.M_product_Id = pp.m_product_id
where pp.m_product_id = ? and plv.isactive = 'Y' and pl.IsSOPriceList = 'Y'
and pl.m_pricelist_id = ?