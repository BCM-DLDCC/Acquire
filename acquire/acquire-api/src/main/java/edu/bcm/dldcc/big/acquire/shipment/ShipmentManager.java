/**
 * 
 */
package edu.bcm.dldcc.big.acquire.shipment;

import java.util.List;

import javax.ejb.Local;

import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.ShipmentSearchFields;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.search.SearchCriteria;

/**
 * @author pew
 *
 */
@Local
public interface ShipmentManager<T extends Shipment>
{
  void addSpecimens();
  void addSpecimen(SearchResult specimen);
  void setShipmentField(ShipmentSearchFields field);
  ShipmentSearchFields getShipmentField();
  SearchCriteria<?> getSearchValue();
  List<T> getShipmentResults();
  void shipmentSearch();
  T getShipment();
  void setShipment(T form);
  void exportShipment();
  void clearShipment();
  void saveShipment();
  
}
