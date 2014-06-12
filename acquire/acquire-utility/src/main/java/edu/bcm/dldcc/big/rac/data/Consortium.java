package edu.bcm.dldcc.big.rac.data;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:37 AM
 */
public enum Consortium {
	TCGA("TCGA"),
	DBGAP("DbGap"),
	OTHER("Other");
	
	private String name;

	/**
	 * 
	 * @param name
	 */
	private Consortium(String name)
	{
	  this.name = name;
	}

	public String toString()
	{
		return this.name;
	}
}