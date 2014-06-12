/**
 * <p>
 * Title: RequestListAction Class>
 * <p>
 * Description: This class initializes the fields of
 * RequestListAdministratorView.jsp Page
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 *
 * @author Ashish Gupta
 * @version 1.00 Created on Oct 04,2006
 */

package edu.wustl.catissuecore.action;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.RequestListFilterationForm;
import edu.wustl.catissuecore.bean.RequestViewBean;
import edu.wustl.catissuecore.bizlogic.OrderBizLogic;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.Validator;

/**
 * @author renuka_bajpai
 *
 */
public class RequestListAction extends SecureAction
{

	/**
	 * Overrides the execute method of Action class. Initializes the various
	 * fields in RequestListAdministratorView.jsp Page.
	 *
	 * @param mapping
	 *            object
	 * @param form
	 *            object
	 * @param request
	 *            object
	 * @param response
	 *            object
	 * @return ActionForward object
	 * @throws Exception
	 *             object
	 */
	@Override
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		new Validator();
		List requestViewBeanList = null;
		List showList = null;
		final RequestListFilterationForm requestListForm = (RequestListFilterationForm) form;

		// For Pagenation
		// Gets the session of this request.
		final HttpSession session = request.getSession();
		final String pageNumStr = request.getParameter(Constants.PAGE_NUMBER);
		int pageNum = 0;
		if (!Validator.isEmpty(pageNumStr))
		{
			pageNum = Integer.parseInt(pageNumStr);
			request.setAttribute(Constants.PAGE_NUMBER, pageNumStr);
		}
		// The start index in the list of users to be approved/rejected.
		int startIndex = Constants.ZERO;
		// The end index in the list of users to be approved/rejected.
		int endIndex = Constants.NUMBER_RESULTS_PER_PAGE;
		final SessionDataBean sessionData = this.getSessionData(request);

		if (pageNum == Constants.START_PAGE)
		{

			// Request List to display
			if (requestListForm.getRequestStatusSelected() != null
					&& !requestListForm.getRequestStatusSelected().trim().equalsIgnoreCase(""))
			{
				final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
						.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);
				requestViewBeanList = orderBizLogic.getRequestList(requestListForm
						.getRequestStatusSelected(), sessionData.getUserName(), sessionData
						.getUserId());
				final int totalResults = requestViewBeanList.size();
				final Iterator iter = requestViewBeanList.iterator();
				int serialNo = 1;
				while (iter.hasNext())
				{
					final RequestViewBean requestViewBean = (RequestViewBean) iter.next();
					requestViewBean.setSerialNo(serialNo);
					serialNo++;
				}
				// Setting the number of new and pending requests
				this.setNumberOfNewAndPendingRequests(requestListForm, requestViewBeanList);
				session.setAttribute(Constants.TOTAL_RESULTS, Integer.toString(totalResults));
			}
			if (Constants.NUMBER_RESULTS_PER_PAGE > requestViewBeanList.size())
			{
				endIndex = requestViewBeanList.size();
			}
			// Save the list of users in the sesson.
			session.setAttribute(Constants.ORIGINAL_DOMAIN_OBJECT_LIST, requestViewBeanList);
		}
		else
		{
			// Get the list of users from the session.
			requestViewBeanList = (List) session
					.getAttribute(Constants.ORIGINAL_DOMAIN_OBJECT_LIST);
			// Set the start index of the users in the list.
			startIndex = (pageNum - 1) * Constants.NUMBER_RESULTS_PER_PAGE;
			// Set the end index of the users in the list.
			endIndex = startIndex + Constants.NUMBER_RESULTS_PER_PAGE;
			if (endIndex > requestViewBeanList.size())
			{
				endIndex = requestViewBeanList.size();
			}
			// Setting the number of new and pending requests
			this.setNumberOfRequests(requestListForm, sessionData.getUserName(), sessionData
					.getUserId());
		}
		// Gets the list of users to be shown on the page.
		showList = requestViewBeanList.subList(startIndex, endIndex);
		// Saves the list of users to be shown on the page in the request.
		request.setAttribute("RequestList", showList);
		// OrderDetails Status to display in drop down
		final List requestStatusListToDisplay = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_REQUEST_STATUS, null);
		// Deleting list.add(0,new NameValueBean(Constants.SELECT_OPTION,"-1"));
		requestStatusListToDisplay.remove(0);
		request.setAttribute(Constants.REQUEST_LIST, requestStatusListToDisplay);

		return mapping.findForward("success");
	}

	/**
	 * @param request
	 * @return
	 */
	/*
	 * private List getUserSitesWithDistributionPrev(HttpServletRequest
	 * request,Boolean isSuperAdmin) { SessionDataBean sessionData =
	 * getSessionData(request); System.out.println(""); PrivilegeManager
	 * privilegeManager = PrivilegeManager.getInstance(); PrivilegeCache
	 * privilegeCache =
	 * privilegeManager.getPrivilegeCache(sessionData.getUserName());
	 * OrderBizLogic orderBizLogic =
	 * (OrderBizLogic)BizLogicFactory.getInstance()
	 * .getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID); List siteIds =
	 * (
	 * List)orderBizLogic.getRelatedSiteIds(sessionData.getUserId(),privilegeCache
	 * ,isSuperAdmin); return siteIds; }
	 */

	/**
	 *
	 * @param requestListFilterationForm : requestListFilterationForm
	 * @param userName : userName
	 * @param userId : userId
	 * @throws BizLogicException : BizLogicException
	 */
	private void setNumberOfRequests(RequestListFilterationForm requestListFilterationForm,
			String userName, Long userId) throws BizLogicException
	{

		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
				.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);
		final List requestViewBeanList = orderBizLogic.getRequestList("All", userName, userId);
		this.setNumberOfNewAndPendingRequests(requestListFilterationForm, requestViewBeanList);

	}

	/**
	 * @param requestListFilterationForm
	 *            : requestListFilterationForm
	 * @param requestViewBeanList
	 *            : requestViewBeanList
	 */
	private void setNumberOfNewAndPendingRequests(
			RequestListFilterationForm requestListFilterationForm, List requestViewBeanList)
	{
		int newStatus = 0, pendingStatus = 0;
		if (requestViewBeanList != null)
		{
			final Iterator iter = requestViewBeanList.iterator();
			while (iter.hasNext())
			{
				final RequestViewBean requestViewBean = (RequestViewBean) iter.next();
				if (requestViewBean.getStatus().trim().equalsIgnoreCase("New"))
				{
					newStatus++;
				}
				else if (requestViewBean.getStatus().trim().equalsIgnoreCase("Pending"))
				{
					pendingStatus++;
				}
			}
		}
		requestListFilterationForm.setNewRequests(newStatus);
		requestListFilterationForm.setPendingRequests(pendingStatus);
	}

}