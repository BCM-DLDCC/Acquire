/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory;

import javax.ejb.Local;
import javax.enterprise.inject.Instance;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.inventory.data.NaLabData;

/**
 * @author pew
 *
 */
@Local
public interface InventoryProcessor
{
  static final Double CONCENTRATION_CONVERSION_FACTOR = .001D;
  
  /**
   * Update the provided instance of caTissue with data relating to NA Lab 
   * processing as given in the provided NaLabData. This will include updating
   * the amount available of the parent specimen, as well as creating 
   * Derivative specimen to represent the NA Lab processing.
   * 
   * @param data
   * @param entityManagers
   */
  void importNaLab(NaLabData data, Instance<EntityManager> entityManagers);
}
