package edu.bcm.dldcc.big.acquire.query;

import java.io.Serializable;

/**
 * Miner Result Column Model
 * @author amcowiti
 *
 */
public class ColumnModel implements Serializable {

   private static final long serialVersionUID = -4489767822944614525L;
	private String header;
    private String property;

    public ColumnModel(String header, String property) {
        this.header = header;
        this.property = property;
    }

    public String getHeader() {
        return header;
    }

    public String getProperty() {
        return property;
    }

}
