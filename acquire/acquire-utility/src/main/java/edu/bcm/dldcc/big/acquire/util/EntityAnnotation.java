/**
 * 
 */
package edu.bcm.dldcc.big.acquire.util;

import java.io.Serializable;

/**
 * @author pew
 *
 */
public interface EntityAnnotation extends Serializable
{
  String getEntityId();
  void setEntityId(String id);
}
