/**
 * 
 */
package edu.bcm.dldcc.big.acquire.qualifiers;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author pew
 *
 */
public class AdminLiteral extends AnnotationLiteral<Admin> implements Admin
{

  /**
   * 
   */
  public static final Admin INSTANCE = new AdminLiteral();

}
