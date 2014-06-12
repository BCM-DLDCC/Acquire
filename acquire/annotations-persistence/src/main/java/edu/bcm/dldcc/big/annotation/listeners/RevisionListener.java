/**
 * 
 */
package edu.bcm.dldcc.big.annotation.listeners;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.NamingException;

import org.jboss.seam.security.Identity;

import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.auditing.entity.AcquireAuditingRevision;
import edu.bcm.dldcc.big.util.qualifier.Current;
import edu.bcm.dldcc.big.util.qualifier.NamedLiteral;

/**
 * @author pew
 * 
 */
public class RevisionListener implements org.hibernate.envers.RevisionListener
{

  /**
   * 
   */
  public RevisionListener()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hibernate.envers.RevisionListener#newRevision(java.lang.Object)
   */
  @Override
  public void newRevision(Object revisionEntity)
  {
    AcquireAuditingRevision revision = (AcquireAuditingRevision) revisionEntity;
    try
    {
      AcquireUserInformation identity = Resources
          .lookup(AcquireUserInformation.class, new AnnotationLiteral<Current>()
          {
          });
      if(identity != null)
      {
        revision.setUsername(identity.getIdentityId());
        revision.setRationale(Resources.lookup(String.class, new NamedLiteral(
            "auditRationale")));
      }
      else
      {
        this.enterSystemInformation(revision);
      }
    }
    catch (ContextNotActiveException e)
    {
      this.enterSystemInformation(revision);
    }
    catch(RuntimeException e)
    {
      if(e.getCause() instanceof NamingException)
      {
        this.enterSystemInformation(revision);
      }
      else
      {
        throw e;
      }
    }

    

  }

  protected void enterSystemInformation(AcquireAuditingRevision revision)
  {
    revision.setUsername("system");
    revision.setRationale("System management");
  }

}
