/**
 * 
 */
package edu.bcm.dldcc.big.acquire.listener;

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * Utility class to bypass limitation in primefaces tabs. It is used to
 * store the current tab for the home page, allowing the tab to be set
 * through a request parameter.
 * 
 * @author pew
 *
 */
@ViewScoped
@Named
public class IndexInformation implements Serializable
{

  private int activeTab = 0;
  
  /**
   * Default constructor
   */
  public IndexInformation()
  {
    super();
  }

  /**
   * @return the activeTab
   */
  public int getActiveTab()
  {
    return this.activeTab;
  }

  /**
   * @param activeTab the activeTab to set
   */
  public void setActiveTab(int activeTab)
  {
    this.activeTab = activeTab;
  }

}
