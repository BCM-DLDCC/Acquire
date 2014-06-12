package edu.bcm.dldcc.big.acquire.query;

import java.util.List;
/**
 * Miner Result Column 
 * @author amcowiti
 *
 */

public interface MinerResultColumn {
	 public List<ColumnModel> getColumns();
	 public void createDynamicColumns() ;
}
