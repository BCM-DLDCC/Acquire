/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import edu.bcm.dldcc.big.acquire.query.data.SearchResult;

/**
 * Utility class that enables multi-select of search results from a data table.
 * @author pew
 *
 */
@ConversationScoped
@Named("data")
public class Data implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -8175662563961999018L;
  private SearchResult[] selectedShipment;

  /**
   * 
   */
  public Data()
  {
    super();
  }

  /**
   * Gets the selected rows from the table
   * 
   * @return the selectedShipment
   */
  public SearchResult[] getSelectedShipment()
  {
    return this.selectedShipment;
  }

  /**
   * Sets the selected rows from the table
   * 
   * @param selectedShipment the selectedShipment to set
   */
  public void setSelectedShipment(SearchResult[] selectedShipment)
  {
    this.selectedShipment = selectedShipment;
  }

}
