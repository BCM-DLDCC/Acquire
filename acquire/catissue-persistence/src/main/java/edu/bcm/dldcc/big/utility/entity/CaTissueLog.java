/**
 * 
 */
package edu.bcm.dldcc.big.utility.entity;

/**
 * Interface for logs of changes in caTissue
 * @author pew
 *
 */
public interface CaTissueLog
{
  /**
   * Method that provides a String to be used as a correlation ID 
   * for JMS messages, so that multiple more-or-less simultaneous 
   * updates to the same entity can be processed only once
   * 
   * @return the correlation id to be used.
   */
  String getCorrelationId();
}
