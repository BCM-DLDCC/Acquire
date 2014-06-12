/**
 * CommonDefaultBizLogic.java
 * Purpose:
 */
package edu.wustl.common.participant.bizlogic;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.DAO;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;


// TODO: Auto-generated Javadoc
/**
 * The Class CommonDefaultBizLogic.
 *
 * @author geeta_jaggal
 * @created-on Nov 20, 2009
 */
public class CommonDefaultBizLogic extends DefaultBizLogic
{


	/**
	 * This method return true if authorized user.
	 *
	 * @param dao DAO object.
	 * @param domainObject Domain object.
	 * @param sessionDataBean  SessionDataBean object.
	 *
	 * @return true if authorized user.
	 *
	 * @throws BizLogicException generic BizLogic Exception
	 */

	//@see edu.wustl.common.bizlogic.IBizLogic#
	// isAuthorized(edu.wustl.common.dao.AbstractDAO, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	public boolean isAuthorized(final DAO dao,final Object domainObject, final SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		boolean isAuthorized = false;
		try
		{
			final PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			if (sessionDataBean == null)
			{
				isAuthorized = false;
			}
			else
			{
				if (domainObject == null)
				{
					isAuthorized = true;
				}
				else
				{
					final PrivilegeCache privilegeCache = privilegeManager
							.getPrivilegeCache(sessionDataBean.getUserName());
					isAuthorized = privilegeCache.hasPrivilege(
							getObjectIdForSecureMethodAccess(domainObject), Permissions.EXECUTE);
				}
			}

			if (!isAuthorized)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("access.execute.action.denied"),
						null, "");

			}
		}
		catch (SMException smException)
		{
			throw handleSMException(smException);
		}

		return isAuthorized;
	}

	/**
	 * Returns the object id of the protection element that represents
	 * the Action that is being requested for invocation.
	 *
	 * @param domainObject the domain object
	 *
	 * @return the object id for secure method access
	 */
	protected String getObjectIdForSecureMethodAccess(final Object domainObject)
	{
		return domainObject.getClass().getName();
	}

	/**
	 * Handle sm exception.
	 *
	 * @param exp the exp
	 *
	 * @return the biz logic exception
	 */
	protected BizLogicException handleSMException(final SMException exp)
	{

		final StringBuffer message = new StringBuffer("Security Exception: " + exp.getMessage());
		if (exp.getCause() != null){
			message.append( " : " ).append( exp.getCause().getMessage());
		}
		return new BizLogicException(ErrorKey.getErrorKey("error.security"), exp,
				"Security Exception");
	}
}
