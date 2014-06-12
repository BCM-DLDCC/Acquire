/**
 * 
 */
package edu.bcm.dldcc.big.acquire.qualifiers;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author pew
 *
 */
public class CaTissueLiteral extends AnnotationLiteral<CaTissue> implements
    CaTissue
{

  /**
   * 
   */
  private final CaTissueInstance instance;

  /**
   * @param instance
   */
  public CaTissueLiteral(CaTissueInstance instance)
  {
    this.instance = instance;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.qualifiers.CaTissue#instance()
   */
  @Override
  public CaTissueInstance instance()
  {
    return instance;
  }

}
