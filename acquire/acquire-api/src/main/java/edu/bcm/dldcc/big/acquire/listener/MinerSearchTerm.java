package edu.bcm.dldcc.big.acquire.listener;

/**
 * Miner Common terms
 * Need use message.properties for these
 * To avoid scattering Strings all over, pew will move this somewhere
 * @author amcowiti
 *
 */
public enum MinerSearchTerm {
	CRITERIA("Current Query Criteria: "),
	NA ("No Search Criteria Supplied");
	
	private String term;
	
	  private MinerSearchTerm(String term)
	  {
	    this.term = term;
	  }

	  @Override
	  public String toString()
	  {
	    return this.term;
	  }
}
