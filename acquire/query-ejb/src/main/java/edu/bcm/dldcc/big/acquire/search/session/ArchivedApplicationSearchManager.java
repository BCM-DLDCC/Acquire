/**
 * 
 */
package edu.bcm.dldcc.big.acquire.search.session;

import java.text.ParseException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.rac.data.ApplicationStatus;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.rac.entity.Application_;
import edu.bcm.dldcc.big.search.SimpleSearchManager;

/**
 * @author pew
 * 
 */
public class ArchivedApplicationSearchManager extends
    SimpleSearchManager<Application>
{

  public ArchivedApplicationSearchManager()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.search.SimpleSearchManager#buildWhere(javax.persistence
   * .criteria.CriteriaBuilder, javax.persistence.criteria.Root)
   */
  @Override
  protected Predicate buildWhere(CriteriaBuilder cb, Root<Application> root)
      throws ParseException
  {
    return cb.and(
        super.buildWhere(cb, root),
        root.get(Application_.applicationStatus).in(
            ApplicationStatus.getArchivedStatus()));
  }

}
