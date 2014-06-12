/*
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 */

package edu.wustl.catissuecore.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.bizlogic.OrderBizLogic;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * <p>
 * Title: RequestToOrderAction Class>
 * <p>
 * Description: This class initializes the fields of jsp page to request the
 * bio-specimens.
 * </p>
 *
 * @author deepti_phadnis
 * @version 1.00
 */
public class RequestToOrderAction extends BaseAction
{

	/**
	 * logger.
	 */

	private transient final Logger logger = Logger.getCommonLogger(RequestToOrderAction.class);

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
	 *
	 * @param mapping
	 *            object of ActionMapping
	 * @param form
	 *            object of ActionForm
	 * @param request
	 *            object of HttpServletRequest
	 * @param response
	 *            object of HttpServletResponse
	 * @throws Exception
	 *             generic exception
	 * @return ActionForward : ActionForward
	 */
	@Override
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// ajax call to change the available quantity on change of specimen
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
				.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);
		// Sets the Distribution Protocol Id List.
		final SessionDataBean sessionLoginInfo = this.getSessionData(request);
		final Long loggedInUserID = sessionLoginInfo.getUserId();
		final long csmUserId = new Long(sessionLoginInfo.getCsmUserId()).longValue();
		final Role role = SecurityManagerFactory.getSecurityManager().getUserRole(csmUserId);

		final List distributionProtocolList = orderBizLogic.loadDistributionProtocol(
				loggedInUserID, role.getName(), sessionLoginInfo);
		request.setAttribute(Constants.DISTRIBUTIONPROTOCOLLIST, distributionProtocolList);
		return mapping.findForward("requestOrderPage");
	}

}