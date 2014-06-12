/**
 * 
 */
package edu.bcm.dldcc.big.acquire.shipment;

import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.NaLabShipment;

/**
 * @author pew
 *
 */
public enum ShipmentType
{
  NA_LAB("Nucleic Acids Lab", NaLabShipment.class);
  private String label = "";
  private ShipmentType(String label, Class<? extends Shipment> shipmentType)
  {
    this.label = label;
    this.shipmentClass = shipmentType;
  }
  
  @Override
  public String toString()
  {
    return this.label;
  }
  
  private Class<? extends Shipment> shipmentClass;
  
  
}
