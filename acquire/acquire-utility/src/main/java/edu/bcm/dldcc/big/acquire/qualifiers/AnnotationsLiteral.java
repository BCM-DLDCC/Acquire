/**
 * 
 */
package edu.bcm.dldcc.big.acquire.qualifiers;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author pew
 *
 */
public class AnnotationsLiteral extends AnnotationLiteral<Annotations>
    implements Annotations
{

  /**
   * 
   */
  public static final Annotations INSTANCE = new AnnotationsLiteral();

}
