/**
 * 
 */
package edu.bcm.dldcc.big.acquire.search.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;

/**
 * @author pew
 *
 */
@Admin
@Dependent
public class ScoreboardSearchManager extends SearchManagerImpl
{

  /**
   * 
   */
  private static final long serialVersionUID = 4691558823964005334L;

  /**
   * 
   */
  @Inject
  public ScoreboardSearchManager(@Annotations @Admin EntityManager manager)
  {
    super();
    this.annotationEm = manager;
    this.emType = new AdminLiteral();
  }

}
