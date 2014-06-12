/**
 * 
 */
package edu.bcm.dldcc.big.acquire.qualifiers;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author pew
 *
 */
public class OperationsLiteral extends AnnotationLiteral<Operations> implements
    Operations
{

  /**
   * 
   */
  public static final Operations INSTANCE = new OperationsLiteral();

}
