/**
 * <p>
 * Title: RequestDetailsAction Class>
 * <p>
 * Description: This class initializes the fields of RequestDetails.jsp Page
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 *
 * @author Ashish Gupta
 * @version 1.00 Created on Oct 05,2006
 */

package edu.wustl.catissuecore.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.wustl.catissuecore.actionForm.RequestDetailsForm;
import edu.wustl.catissuecore.bean.DefinedArrayDetailsBean;
import edu.wustl.catissuecore.bean.DefinedArrayRequestBean;
import edu.wustl.catissuecore.bean.ExistingArrayDetailsBean;
import edu.wustl.catissuecore.bean.RequestDetailsBean;
import edu.wustl.catissuecore.bean.RequestViewBean;
import edu.wustl.catissuecore.bizlogic.OrderBizLogic;
import edu.wustl.catissuecore.domain.DerivedSpecimenOrderItem;
import edu.wustl.catissuecore.domain.DistributionProtocol;
import edu.wustl.catissuecore.domain.ExistingSpecimenArrayOrderItem;
import edu.wustl.catissuecore.domain.ExistingSpecimenOrderItem;
import edu.wustl.catissuecore.domain.NewSpecimenArrayOrderItem;
import edu.wustl.catissuecore.domain.OrderDetails;
import edu.wustl.catissuecore.domain.OrderItem;
import edu.wustl.catissuecore.domain.PathologicalCaseOrderItem;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenArray;
import edu.wustl.catissuecore.domain.SpecimenOrderItem;
import edu.wustl.catissuecore.util.IdComparator;
import edu.wustl.catissuecore.util.OrderingSystemUtil;
import edu.wustl.catissuecore.util.SpecimenComparator;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.Role;

/**
 * @author renuka_bajpai
 */
public class RequestDetailsAction extends BaseAction
{

	/**
	 * logger.
	 */

	private static final Logger LOGGER = Logger.getCommonLogger(RequestDetailsAction.class);

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
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
		final RequestDetailsForm requestDetailsForm = (RequestDetailsForm) form;
		// The request Id on which the user has clicked
		String requestId = "";
		String orderDetailsId = "";

		if (request.getParameter("id") != null && !request.getParameter("id").equals("0"))
		{
			orderDetailsId = request.getParameter("id");

		}
		else if (request.getParameter("type") != null
				&& request.getParameter("type").equalsIgnoreCase("directDistribution"))
		{
			orderDetailsId = request.getAttribute("id").toString();
			final ActionMessages actionMessages = new ActionMessages();
			actionMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"pending.order.created", "Order_" + orderDetailsId));
			this.saveMessages(request, actionMessages);
		}

		if (orderDetailsId != null && !orderDetailsId.equals(""))
		{
			requestId = orderDetailsId;
			// Setting the order id in the form to retrieve the corresponding
			// orderitems from the db in CommonAddEditAction.
			requestDetailsForm.setId((new Long(requestId)).longValue());
		}
		else
		// while returning from specimen page
		{
			final Object obj = request.getSession().getAttribute("REQUEST_DETAILS_FORM");
			RequestDetailsForm rDForm = null;
			if (obj != null)
			{
				rDForm = (RequestDetailsForm) obj;
			}
			requestId = "" + rDForm.getId();
			orderDetailsId = requestId;
			requestDetailsForm.setId((new Long(requestId)).longValue());

		}

		if (requestDetailsForm.getDistributionProtocolId() == null
				|| requestDetailsForm.getDistributionProtocolId().equals(""))
		{
			requestDetailsForm.setOrderName("Order_" + orderDetailsId);

		}

		// ajax call to change the available quantity on change of specimen
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
				.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);
		final boolean isAjaxCalled = this.isAjaxCalled(request);

		if (isAjaxCalled && request.getParameter("specimenId") != null)
		{
			this.setSpecimenDetails(request, response, orderBizLogic);
			// for ajax return null as Actionservlet returns ActionForward
			// object
			return null;
		}
		if (isAjaxCalled && request.getParameter("distributionProtId") != null)
		{
			this.setRequesterName(request, response, orderBizLogic);
			// for ajax return null as Actionservlet returns ActionForward
			// object
			return null;
		}

		// order items status to display
		final List requestedItemsStatusList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_REQUESTED_ITEMS_STATUS, null);
		// removing the --select-- from the list and adding --select Status for
		// all-- to the list
		requestedItemsStatusList.remove(0);
		requestedItemsStatusList.add(0, new NameValueBean(ApplicationProperties
				.getValue("orderingSystem.details.distributionStatus"), "-1"));
		request.setAttribute(Constants.REQUESTED_ITEMS_STATUS_LIST, requestedItemsStatusList);

		// Setting the Site List
		final List siteList = this.getSiteListToDisplay();
		request.setAttribute(Constants.SITE_LIST_OBJECT, siteList);

		// setting Item Status List in Request
		request.setAttribute(Constants.ITEM_STATUS_LIST, OrderingSystemUtil
				.getPossibleStatusList(Constants.ORDER_REQUEST_STATUS_NEW));

		// setting Items status list without "Distributed" option.
		final List tempList = OrderingSystemUtil
				.getPossibleStatusList(Constants.ORDER_REQUEST_STATUS_NEW);
		final Iterator tempListIter = tempList.iterator();
		while (tempListIter.hasNext())
		{
			final NameValueBean nameValueBean = (NameValueBean) tempListIter.next();
			if (nameValueBean.getValue().equalsIgnoreCase(
					Constants.ORDER_REQUEST_STATUS_DISTRIBUTED)
					|| nameValueBean.getValue().equalsIgnoreCase(
							Constants.ORDER_REQUEST_STATUS_DISTRIBUTED_AND_CLOSE))
			{
				tempList.remove(nameValueBean);
				tempList.add(new NameValueBean(
						Constants.ORDER_REQUEST_STATUS_READY_FOR_ARRAY_PREPARATION,
						Constants.ORDER_REQUEST_STATUS_READY_FOR_ARRAY_PREPARATION));
				break;
			}
		}
		// For orderitems in defined array, "Ready for array preparation" status
		// is present instead of "Distribute"
		request.setAttribute(Constants.ITEM_STATUS_LIST_FOR_ITEMS_IN_ARRAY, tempList);

		// The order items list corresponding to the request Id
		this.getRequestDetailsList(requestId, requestDetailsForm, request, response);

		if (request.getParameter(Constants.TAB_INDEX_ID) != null)
		{
			final Integer tabIndexId = new Integer(request.getParameter(Constants.TAB_INDEX_ID));
			requestDetailsForm.setTabIndex(tabIndexId.intValue());
		}

		// Sets the Distribution Protocol Id List.
		final SessionDataBean sessionLoginInfo = this.getSessionData(request);
		final Long loggedInUserID = sessionLoginInfo.getUserId();
		final long csmUserId = new Long(sessionLoginInfo.getCsmUserId()).longValue();
		final Role role = SecurityManagerFactory.getSecurityManager().getUserRole(csmUserId);

		final List distributionProtocolList = orderBizLogic.loadDistributionProtocol(
				loggedInUserID, role.getName(), sessionLoginInfo);
		request.setAttribute(Constants.DISTRIBUTIONPROTOCOLLIST, distributionProtocolList);

		return mapping.findForward("success");
	}

	/**
	 * This method will be called to set the specimen Quantity and quantity
	 * type.
	 *
	 * @param request : request
	 * @param response : response
	 * @param orderBizLogic : orderBizLogic
	 * @throws Exception : Exception
	 */
	private void setSpecimenDetails(HttpServletRequest request, HttpServletResponse response,
			OrderBizLogic orderBizLogic) throws Exception
	{
		final String identifier = request.getParameter("identifier");
		final String specimenIdentifier = request.getParameter("specimenId");
		Specimen specimen = null;
		if (!specimenIdentifier.equals("#"))
		{
			final Long specimenId = Long.parseLong(request.getParameter("specimenId"));
			specimen = orderBizLogic.getSpecimenObject(specimenId);
		}
		this.sendSpecimenDetails(specimen, response, identifier);
	}

	/**
	 * This method will be called to set the requester name.
	 *
	 * @param request : request
	 * @param response : response
	 * @param orderBizLogic : orderBizLogic
	 * @throws IOException : IOException
	 */
	private void setRequesterName(HttpServletRequest request, HttpServletResponse response,
			OrderBizLogic orderBizLogic) throws IOException
	{
		final String distributionProtId = request.getParameter("distributionProtId");
		String requesterName = "";
		if (!distributionProtId.equals("-1"))
		{
			final DistributionProtocol distributionProtocol = orderBizLogic
					.retrieveDistributionProtocol(distributionProtId);
			requesterName = distributionProtocol.getPrincipalInvestigator().getLastName() + ", "
					+ distributionProtocol.getPrincipalInvestigator().getFirstName();
		}
		final PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.write(requesterName);
	}

	/**
	 *
	 * @param request : request
	 */
	private void cleanSession(HttpServletRequest request)
	{
		request.getSession().removeAttribute(Constants.EXISISTINGARRAY_REQUESTS_LIST);
		request.getSession().removeAttribute(Constants.DEFINEDARRAY_REQUESTS_LIST);
		request.getSession().removeAttribute(Constants.REQUEST_DETAILS_LIST);
	}

	/**
	 * This function constructs lists of RequestDetails bean objects,map of
	 * definedarrays and ExistingArrayDetails bean instances to display on
	 * RequestDetails.jsp and ArrayRequests.jsp by setting all the lists in
	 * HttpServletRequest object.
	 *
	 * @param id
	 *            String containing the requestId
	 * @param requestDetailsForm
	 *            RequestDetailsForm object
	 * @param request
	 *            HttpServletRequest object
	 * @param response : response
	 * @throws Exception : Exception
	 */
	private void getRequestDetailsList(String id, RequestDetailsForm requestDetailsForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// fetching the order object corresponding to obtained id.

		final long startTime = System.currentTimeMillis();
		final IDAOFactory daoFact = DAOConfigFactory.getInstance().getDAOFactory(
				CommonServiceLocator.getInstance().getAppName());
		DAO dao = null;
		dao = daoFact.getDAO();

		try
		{
			dao.openSession(null);
			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
					.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);

			final OrderDetails orderDetails = orderBizLogic.getOrderListFromDB(id, dao);

			// The request details corresponding to the request Id
			RequestViewBean requestListBean = null;

			requestListBean = this.getRequestObject(orderDetails);
			if (orderDetails.getName() == null || orderDetails.getDistributionProtocol() == null)
			{
				requestDetailsForm.setIsDirectDistribution(Boolean.TRUE);
			}
			request.setAttribute(Constants.REQUEST_HEADER_OBJECT, requestListBean);

			final Collection orderItemsListFromDB = orderDetails.getOrderItemCollection();

			// populating Map in the form only on loading. If error is present,
			// use form present in request.
			final ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
			if (errors == null)
			{
				this.cleanSession(request);
				// Showing specimen tab by default.
				requestDetailsForm.setTabIndex(1);
				requestDetailsForm.setAllValuesForOrder(orderDetails, request, dao);

				List requestDetailsList = new ArrayList();
				OrderItem orderItem = null;
				// List containing the list of defined arrays and existing
				// arrays
				final List arrayRequestDetailsList = new ArrayList();
				final List arrayRequestDetailsMapList = new ArrayList();
				if ((orderItemsListFromDB != null) && (!orderItemsListFromDB.isEmpty()))
				{
					final List<OrderItem> orderItemsList = new ArrayList<OrderItem>(
							orderItemsListFromDB);
					// Sorting by Order.Id
					Collections.sort(orderItemsList, new IdComparator());
					final ListIterator<OrderItem> iter = orderItemsList.listIterator();
					while (iter.hasNext())
					{
						orderItem = iter.next();
						if (orderItem instanceof SpecimenOrderItem)
						{
							requestDetailsList = this
									.populateRequestDetailsListForSpecimenOrderItems(orderItem,
											request, requestDetailsList);
						}
						// In case of Defined Array
						if (orderItem instanceof NewSpecimenArrayOrderItem)
						{
							Map arrayMap = new HashMap();
							final NewSpecimenArrayOrderItem newSpecimenArrayOrderItem = (NewSpecimenArrayOrderItem) orderItem;
							arrayMap = this.populateArrayMap(newSpecimenArrayOrderItem);
							// Add defined-array list into the
							// arrayRequestDetails list
							arrayRequestDetailsMapList.add(arrayMap);
						}
						// In case of Existing BioSpecimen Array Items
						if (orderItem instanceof ExistingSpecimenArrayOrderItem)
						{
							final ExistingSpecimenArrayOrderItem existingSpecimenArrayOrderItem = (ExistingSpecimenArrayOrderItem) orderItem;
							final ExistingArrayDetailsBean existingArrayDetailsBean = this
									.populateExistingArrayItemsList(existingSpecimenArrayOrderItem);
							// Add existingArrayBEan list into
							// arrayRequestDetails list
							arrayRequestDetailsList.add(existingArrayDetailsBean);
						}
					}// End while
				}
				// Call to populateItemStatusList() when order items present in
				// OrderList
				if (requestDetailsList.size() > 0)
				{
					this.populateItemStatusList(requestDetailsList, request, "addToOrderList");
				}
				// Call to populateItemStatusList() when order items are added
				// to Defined Array
				if (arrayRequestDetailsMapList.size() > 0)
				{
					this.populateItemStatusList(arrayRequestDetailsMapList, request,
							"addToDefinedArrays");
				}
				// Call to populateItemStatusList() when order items are added
				// to Existing Array
				if (arrayRequestDetailsList.size() > 0)
				{
					this.populateItemStatusList(arrayRequestDetailsList, request,
							"addToExistingArrays");
				}

				final long endTime = System.currentTimeMillis();
				System.out.println("Execute time of getRequestDetailsList :"
						+ (endTime - startTime));

			}
			else
			{
				this.populateRequestForMap(requestDetailsForm, orderDetails);
			}

		}

		catch (final DAOException e)
		{
			LOGGER.error(e.getMessage(), e);

		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (final DAOException daoEx)
			{
				LOGGER.error(daoEx.getMessage(), daoEx);
			}
		}
	}

	/**
	 *
	 * @param orderDetails : orderDetails
	 * @param orderItemId : orderItemId
	 * @return OrderItem : OrderItem
	 */
	private OrderItem getOrderItem(OrderDetails orderDetails, Long orderItemId)
	{
		final Collection orderItemsList = orderDetails.getOrderItemCollection();

		final Iterator orderItemsListItr = orderItemsList.iterator();
		while (orderItemsListItr.hasNext())
		{
			final OrderItem orderItem = (OrderItem) orderItemsListItr.next();
			if (orderItem.getId().equals(orderItemId))
			{
				return orderItem;
			}
		}

		return null;

	}

	/**
	 * Need to change this method ... to remove all the DB retrieves.
	 * @param requestDetailsForm : requestDetailsForm
	 * @param orderDetails : orderDetails
	 * @throws DAOException : DAOException
	 * @throws BizLogicException BizLogic Exception
	 */
	private void populateRequestForMap(RequestDetailsForm requestDetailsForm,
			OrderDetails orderDetails) throws DAOException, BizLogicException
	{
		final Map valuesMap = requestDetailsForm.getValues();
		final Set keySet = valuesMap.keySet();
		final Iterator iter = keySet.iterator();
		final Map requestForMap = new HashMap();
		while (iter.hasNext())
		{
			String rowNumber = "";
			String orderItemId = "";
			final String key = (String) iter.next();

			if (key.endsWith("orderItemId"))
			{
				List allSpecimensToDisplay = new ArrayList();
				rowNumber = this.getRowNumber(key);
				// Fetching the order item id
				orderItemId = (String) valuesMap.get(key);
				final OrderItem orderItem = this.getOrderItem(orderDetails, Long
						.parseLong(orderItemId));
				// OrderItem orderItem = (OrderItem) orderItemListFromDb.get(0);

				// List finalChildrenSpecimenList = new ArrayList();

				if (orderItem instanceof DerivedSpecimenOrderItem)
				{
					List allSpecimen = new ArrayList();
					final DerivedSpecimenOrderItem derivedSpecimenOrderItem = (DerivedSpecimenOrderItem) orderItem;
					/*
					 * Collection childrenSpecimenList =
					 * OrderingSystemUtil.getAllChildrenSpecimen(
					 * derivedSpecimenOrderItem
					 * .getParentSpecimen().getChildSpecimenCollection());
					 * finalChildrenSpecimenList =
					 * OrderingSystemUtil.getChildrenSpecimenForClassAndType
					 * (childrenSpecimenList,
					 * derivedSpecimenOrderItem.getSpecimenClass(),
					 * derivedSpecimenOrderItem.getSpecimenType());
					 */

					allSpecimen = OrderingSystemUtil.getAllSpecimen(derivedSpecimenOrderItem
							.getParentSpecimen());
					final SpecimenComparator comparator = new SpecimenComparator();
					Collections.sort(allSpecimen, comparator);
					allSpecimensToDisplay = OrderingSystemUtil.getNameValueBeanList(allSpecimen,
							null);
					final SpecimenOrderItem specimenOrderItem = (SpecimenOrderItem) orderItem;
					if (specimenOrderItem.getNewSpecimenArrayOrderItem() == null)
					{
						requestForMap.put("RequestForDropDownList:" + rowNumber,
								allSpecimensToDisplay);
					}
					else
					{
						requestForMap.put("RequestForDropDownListArray:" + rowNumber,
								allSpecimensToDisplay);
					}

				}
				if (orderItem instanceof ExistingSpecimenOrderItem)
				{
					List allSpecimen = new ArrayList();
					final ExistingSpecimenOrderItem existingSpecimenOrderItem = (ExistingSpecimenOrderItem) orderItem;
					allSpecimen = OrderingSystemUtil.getAllSpecimen(existingSpecimenOrderItem
							.getSpecimen());
					final SpecimenComparator comparator = new SpecimenComparator();
					Collections.sort(allSpecimen, comparator);
					allSpecimensToDisplay = OrderingSystemUtil.getNameValueBeanList(allSpecimen,
							existingSpecimenOrderItem.getSpecimen());
					requestForMap.put("RequestForDropDownList:" + rowNumber, allSpecimensToDisplay);
				}

				if (orderItem instanceof PathologicalCaseOrderItem)
				{
					/* chnages finsh */
					final PathologicalCaseOrderItem pathologicalCaseOrderItem = (PathologicalCaseOrderItem) orderItem;
					final List totalChildrenSpecimenColl = OrderingSystemUtil
							.getAllSpecimensForPathologicalCases(pathologicalCaseOrderItem
									.getSpecimenCollectionGroup(), pathologicalCaseOrderItem);
					allSpecimensToDisplay = OrderingSystemUtil.getNameValueBeanList(
							totalChildrenSpecimenColl, null);
					final SpecimenOrderItem specimenOrderItem = (SpecimenOrderItem) orderItem;
					if (specimenOrderItem.getNewSpecimenArrayOrderItem() == null)
					{
						requestForMap.put("RequestForDropDownList:" + rowNumber,
								allSpecimensToDisplay);
					}
					else
					{
						requestForMap.put("RequestForDropDownListArray:" + rowNumber,
								allSpecimensToDisplay);
					}
				}

			}
		}
		requestDetailsForm.setRequestForDropDownMap(requestForMap);
	}

	/**
	 * @param str : str
	 * @return String : String
	 */
	private String getRowNumber(String str)
	{
		final StringTokenizer stringTokenizer = new StringTokenizer(str, "_");
		final String firstToken = stringTokenizer.nextToken();
		final int indexOfColon = firstToken.indexOf(":");
		final String rowNumber = firstToken.substring(indexOfColon + 1);
		return rowNumber;
	}

	/**
	 * @param orderItem : orderItem
	 * @param request : request
	 * @param requestDetailsList : requestDetailsList
	 * @return List : List
	 * @throws DAOException : DAOException
	 */
	private List populateRequestDetailsListForSpecimenOrderItems(OrderItem orderItem,
			HttpServletRequest request, List requestDetailsList) throws DAOException
	{
		// The row number to update available quantity on selecting the required
		// specimen from 'request for' drop down.
		int finalSpecimenListId = 0;
		final SpecimenOrderItem specimenOrderItem = (SpecimenOrderItem) orderItem;
		// Incase of order items in the Order List
		if (specimenOrderItem.getNewSpecimenArrayOrderItem() == null)
		{
			RequestDetailsBean requestDetailsBean = new RequestDetailsBean();
			if (orderItem instanceof ExistingSpecimenOrderItem)
			{
				final ExistingSpecimenOrderItem existingSpecimenorderItem = (ExistingSpecimenOrderItem) orderItem;
				requestDetailsBean = this.populateRequestDetailsBeanForExistingSpecimen(
						requestDetailsBean, existingSpecimenorderItem);
				requestDetailsList.add(requestDetailsBean);
				finalSpecimenListId++;
			}
			else if (orderItem instanceof DerivedSpecimenOrderItem)
			{
				final DerivedSpecimenOrderItem derivedSpecimenorderItem = (DerivedSpecimenOrderItem) orderItem;
				requestDetailsBean = this.populateRequestDetailsBeanForDerivedSpecimen(
						requestDetailsBean, derivedSpecimenorderItem, request, finalSpecimenListId);
				requestDetailsList.add(requestDetailsBean);
				finalSpecimenListId++;
			}
			else if (orderItem instanceof PathologicalCaseOrderItem)
			{
				final PathologicalCaseOrderItem pathologicalCaseOrderItem = (PathologicalCaseOrderItem) orderItem;
				requestDetailsBean = this
						.populateRequestDetailsBeanForPathologicalCase(requestDetailsBean,
								pathologicalCaseOrderItem, request, finalSpecimenListId);
				requestDetailsList.add(requestDetailsBean);
				finalSpecimenListId++;
			}
		}
		return requestDetailsList;
	}

	/**
	 * 	 * This function populates existingArrayDetailsList with the existing Bio
		 * Speicmen Array Information by fetching the data from
		 * ExistingSpecimenArrayOrderItem domain object.
	 * @param existingSpecimenArrayOrderItem : existingSpecimenArrayOrderItem
	 * @return ExistingArrayDetailsBean : ExistingArrayDetailsBean
	 */
	private ExistingArrayDetailsBean populateExistingArrayItemsList(
			ExistingSpecimenArrayOrderItem existingSpecimenArrayOrderItem)
	{
		final ExistingArrayDetailsBean existingArrayDetailsBean = new ExistingArrayDetailsBean();
		existingArrayDetailsBean.setOrderItemId(existingSpecimenArrayOrderItem.getId().toString());
		existingArrayDetailsBean.setBioSpecimenArrayName(existingSpecimenArrayOrderItem
				.getSpecimenArray().getName());
		existingArrayDetailsBean.setDescription(existingSpecimenArrayOrderItem.getDescription());
		existingArrayDetailsBean.setAssignedStatus(existingSpecimenArrayOrderItem.getStatus());
		existingArrayDetailsBean.setArrayId(existingSpecimenArrayOrderItem.getSpecimenArray()
				.getId().toString());
		existingArrayDetailsBean.setRequestedQuantity(existingSpecimenArrayOrderItem
				.getRequestedQuantity().toString());

		return existingArrayDetailsBean;
	}

	/**
	 * This function populates the arrayDetailsList with map objects.Each map
	 * contains arrayRequestBean object as teh key and list of order items (for
	 * that defined array) as the value.
	 *
	 * @param newSpecimenArrayOrderItem
	 *            NewSpecimenArrayOrderItem object
	 * @return definedArrayMap Map object
	 * @throws DAOException
	 *             object
	 */
	private Map populateArrayMap(NewSpecimenArrayOrderItem newSpecimenArrayOrderItem)
			throws DAOException
	{
		final Map definedArrayMap = new HashMap();
		final List arrayItemsList = new ArrayList();
		// Create new instance of ArrayRequestBean to save the
		// name,type,dimensions of the defined array
		DefinedArrayRequestBean arrayRequestBean = new DefinedArrayRequestBean();
		arrayRequestBean = this.populateArrayRequestBean(arrayRequestBean,
				newSpecimenArrayOrderItem);
		final Collection specimenOrderItemCollection = newSpecimenArrayOrderItem
				.getSpecimenOrderItemCollection();
		// Calculating the condition to enable or disable "Create Array Button"
		String condition = OrderingSystemUtil
				.determineCreateArrayCondition(specimenOrderItemCollection);

		if (arrayRequestBean.getAssignedStatus().trim().equalsIgnoreCase(
				Constants.ORDER_REQUEST_STATUS_DISTRIBUTED)
				|| arrayRequestBean.getAssignedStatus().trim().equalsIgnoreCase(
						Constants.ORDER_REQUEST_STATUS_DISTRIBUTED_AND_CLOSE))
		{
			condition = "true";
		}
		arrayRequestBean.setCreateArrayButtonDisabled(condition);
		final Iterator specimenOrderItemCollectionItr = specimenOrderItemCollection.iterator();
		while (specimenOrderItemCollectionItr.hasNext())
		{
			final SpecimenOrderItem specimenOrderItem = (SpecimenOrderItem) specimenOrderItemCollectionItr
					.next();
			DefinedArrayDetailsBean arrayDetailsBean = new DefinedArrayDetailsBean();
			if (specimenOrderItem instanceof ExistingSpecimenOrderItem)
			{
				arrayDetailsBean = this.populateExistingSpecimensForArrayDetails(arrayDetailsBean,
						specimenOrderItem);
			}
			else if (specimenOrderItem instanceof DerivedSpecimenOrderItem)
			{
				arrayDetailsBean = this.populateDerivedSpecimensForArrayDetails(arrayDetailsBean,
						specimenOrderItem);
			}
			else if (specimenOrderItem instanceof PathologicalCaseOrderItem)
			{
				arrayDetailsBean = this.populatePathologicalCasesForArrayDetails(arrayDetailsBean,
						specimenOrderItem);
			}
			// arrayDetailsBean.setArrayRequestBean(arrayRequestBean);
			// Add all the arrayDetailsBean in the list
			arrayItemsList.add(arrayDetailsBean);
		}
		definedArrayMap.put(arrayRequestBean, arrayItemsList);

		// Return the list containing arrayDetailsBean instances
		return definedArrayMap;
	}

	/**
	 * @param arrayRequestBean
	 *            object
	 * @param newSpecimenArrayOrderItem
	 *            object
	 * @return DefinedArrayRequestBean object
	 */
	private DefinedArrayRequestBean populateArrayRequestBean(
			DefinedArrayRequestBean arrayRequestBean,
			NewSpecimenArrayOrderItem newSpecimenArrayOrderItem)
	{
		arrayRequestBean.setArrayName(newSpecimenArrayOrderItem.getName());
		arrayRequestBean.setArrayClass(newSpecimenArrayOrderItem.getSpecimenArrayType()
				.getSpecimenClass());
		arrayRequestBean.setOneDimensionCapacity((newSpecimenArrayOrderItem.getSpecimenArrayType()
				.getCapacity().getOneDimensionCapacity()).toString());
		arrayRequestBean.setTwoDimensionCapacity((newSpecimenArrayOrderItem.getSpecimenArrayType()
				.getCapacity().getTwoDimensionCapacity()).toString());
		arrayRequestBean.setArrayType(newSpecimenArrayOrderItem.getSpecimenArrayType().getName());
		arrayRequestBean.setArrayTypeId(newSpecimenArrayOrderItem.getSpecimenArrayType().getId()
				.toString());
		arrayRequestBean.setAssignedStatus(newSpecimenArrayOrderItem.getStatus());
		arrayRequestBean.setOrderItemId(newSpecimenArrayOrderItem.getId().toString());

		final SpecimenArray specimenArrayObj = newSpecimenArrayOrderItem.getSpecimenArray();
		if (specimenArrayObj != null)
		{
			arrayRequestBean.setArrayId(specimenArrayObj.getId().toString());
		}
		// Populate status list of individual array
		final List arrayStatusList = OrderingSystemUtil
				.getPossibleStatusList(Constants.ORDER_REQUEST_STATUS_NEW);
		arrayRequestBean.setArrayStatusList(arrayStatusList);
		return arrayRequestBean;
	}

	/**
	 * @param arrayDetailsBean
	 *            object
	 * @param specimenOrderItem
	 *            object
	 * @return DefinedArrayDetailsBean object
	 */
	private DefinedArrayDetailsBean populateExistingSpecimensForArrayDetails(
			DefinedArrayDetailsBean arrayDetailsBean, SpecimenOrderItem specimenOrderItem)
	{
		final ExistingSpecimenOrderItem existingSpecimenOrderItem = (ExistingSpecimenOrderItem) specimenOrderItem;
		arrayDetailsBean.setRequestedItem(existingSpecimenOrderItem.getSpecimen().getLabel());
		// Add empty list since it is the case of existing speicmen
		arrayDetailsBean.setSpecimenList(new ArrayList());
		arrayDetailsBean
				.setSpecimenId((existingSpecimenOrderItem.getSpecimen().getId()).toString());
		arrayDetailsBean.setRequestedQuantity(existingSpecimenOrderItem.getRequestedQuantity()
				.toString());
		arrayDetailsBean.setAvailableQuantity(existingSpecimenOrderItem.getSpecimen()
				.getAvailableQuantity().toString());
		arrayDetailsBean.setAssignedStatus(existingSpecimenOrderItem.getStatus());
		arrayDetailsBean.setClassName(existingSpecimenOrderItem.getSpecimen().getClassName());
		arrayDetailsBean.setType(existingSpecimenOrderItem.getSpecimen().getSpecimenType());
		arrayDetailsBean.setDescription(existingSpecimenOrderItem.getDescription());
		arrayDetailsBean.setOrderItemId(existingSpecimenOrderItem.getId().toString());
		arrayDetailsBean.setInstanceOf("Existing");

		return arrayDetailsBean;
	}

	/**
	 * @param arrayDetailsBean
	 *            object
	 * @param specimenOrderItem
	 *            object
	 * @return DefinedArrayDetailsBean object
	 */
	private DefinedArrayDetailsBean populateDerivedSpecimensForArrayDetails(
			DefinedArrayDetailsBean arrayDetailsBean, SpecimenOrderItem specimenOrderItem)
	{
		final DerivedSpecimenOrderItem derivedSpecimenOrderItem = (DerivedSpecimenOrderItem) specimenOrderItem;
		arrayDetailsBean.setRequestedItem(derivedSpecimenOrderItem.getParentSpecimen().getLabel());
		arrayDetailsBean.setSpecimenId(derivedSpecimenOrderItem.getParentSpecimen().getId()
				.toString());
		// Obtain all children specimens
		final Collection childrenSpecimenList = OrderingSystemUtil
				.getAllChildrenSpecimen(derivedSpecimenOrderItem.getParentSpecimen()
						.getChildSpecimenCollection());
		// Obtain only those specimens of this class and type from the above
		// list
		final List finalChildrenSpecimenList = OrderingSystemUtil
				.getChildrenSpecimenForClassAndType(childrenSpecimenList, derivedSpecimenOrderItem
						.getSpecimenClass(), derivedSpecimenOrderItem.getSpecimenType());
		final List childrenSpecimenListToDisplay = OrderingSystemUtil.getNameValueBeanList(
				finalChildrenSpecimenList, null);
		arrayDetailsBean.setSpecimenList(childrenSpecimenListToDisplay);

		arrayDetailsBean.setRequestedQuantity(derivedSpecimenOrderItem.getRequestedQuantity()
				.toString());
		arrayDetailsBean.setAvailableQuantity(derivedSpecimenOrderItem.getParentSpecimen()
				.getAvailableQuantity().toString());
		arrayDetailsBean.setAssignedStatus(derivedSpecimenOrderItem.getStatus());
		arrayDetailsBean.setClassName(derivedSpecimenOrderItem.getSpecimenClass());
		arrayDetailsBean.setType(derivedSpecimenOrderItem.getSpecimenType());
		arrayDetailsBean.setDescription(derivedSpecimenOrderItem.getDescription());
		arrayDetailsBean.setOrderItemId(derivedSpecimenOrderItem.getId().toString());
		arrayDetailsBean.setInstanceOf("Derived");

		return arrayDetailsBean;
	}

	/**
	 * @param arrayDetailsBean
	 *            object
	 * @param specimenOrderItem
	 *            object
	 * @return DefinedArrayDetailsBean object
	 */
	private DefinedArrayDetailsBean populatePathologicalCasesForArrayDetails(
			DefinedArrayDetailsBean arrayDetailsBean, SpecimenOrderItem specimenOrderItem)
	{
		final PathologicalCaseOrderItem pathologicalCaseOrderItem = (PathologicalCaseOrderItem) specimenOrderItem;
		boolean isDerived = false;
		boolean isNotTissueBlock = false;
		arrayDetailsBean.setRequestedItem(pathologicalCaseOrderItem.getSpecimenCollectionGroup()
				.getSurgicalPathologyNumber());
		final Collection childrenSpecimenList = pathologicalCaseOrderItem
				.getSpecimenCollectionGroup().getSpecimenCollection();
		// Removing distributed option if no specimens are present in that SCG.
		// ie. childrenSpecimenList.size() == 0
		// TODO
		if (childrenSpecimenList.size() == 0)
		{
			isDerived = true;
		}
		if (pathologicalCaseOrderItem.getSpecimenClass() != null
				&& pathologicalCaseOrderItem.getSpecimenType() != null
				&& !pathologicalCaseOrderItem.getSpecimenClass().trim().equalsIgnoreCase("")
				&& !pathologicalCaseOrderItem.getSpecimenType().trim().equalsIgnoreCase(""))
		{
			isNotTissueBlock = true;
			isDerived = true;
		}
		final List totalChildrenSpecimenColl = this.getTotalChildspecimens(
				pathologicalCaseOrderItem, isNotTissueBlock, childrenSpecimenList);

		final List childrenSpecimenListToDisplay = OrderingSystemUtil.getNameValueBeanList(
				totalChildrenSpecimenColl, null);
		arrayDetailsBean.setSpecimenList(childrenSpecimenListToDisplay);
		arrayDetailsBean.setSpecimenCollGroupId(pathologicalCaseOrderItem
				.getSpecimenCollectionGroup().getId().toString());
		if (isDerived)
		{
			arrayDetailsBean.setInstanceOf("DerivedPathological");
		}
		else
		{
			arrayDetailsBean.setInstanceOf("Pathological");
		}
		arrayDetailsBean.setRequestedQuantity(pathologicalCaseOrderItem.getRequestedQuantity()
				.toString());
		// Displaying the quantity of the first specimen in the request for drop
		// down.
		if (childrenSpecimenListToDisplay.size() > 1)
		{
			arrayDetailsBean.setAvailableQuantity(((Specimen) totalChildrenSpecimenColl.get(1))
					.getAvailableQuantity().toString());
		}
		else
		{
			arrayDetailsBean.setAvailableQuantity("");
		}
		// Assigned Quantity
		if (pathologicalCaseOrderItem.getDistributedItem() != null)
		{
			arrayDetailsBean.setAssignedQty(pathologicalCaseOrderItem.getDistributedItem()
					.getQuantity().toString());
		}
		arrayDetailsBean.setAssignedStatus(pathologicalCaseOrderItem.getStatus());
		arrayDetailsBean.setClassName(pathologicalCaseOrderItem.getSpecimenClass());
		arrayDetailsBean.setType(pathologicalCaseOrderItem.getSpecimenType());
		arrayDetailsBean.setDescription(pathologicalCaseOrderItem.getDescription());
		arrayDetailsBean.setOrderItemId(pathologicalCaseOrderItem.getId().toString());

		return arrayDetailsBean;
	}

	// Populates a list of Requset object to display as header info on
	// RequestDetails.jsp
	/**
	 * @param orderDetails
	 *            OrderDetails
	 * @return RequestViewBean object
	 * @throws DAOException
	 *             object
	 */
	private RequestViewBean getRequestObject(OrderDetails orderDetails) throws DAOException
	{

		RequestViewBean requestViewBean = null;
		requestViewBean = OrderingSystemUtil.getRequestViewBeanToDisplay(orderDetails);
		requestViewBean.setComments(orderDetails.getComment());

		return requestViewBean;
	}

	/**
	 * @param id
	 * @return
	 * @throws BizLogicException
	 */
	/*
	 * private Specimen getSpecimenFromDB(String id) throws BizLogicException {
	 * IFactory factory =
	 * AbstractFactoryConfig.getInstance().getBizLogicFactory();
	 * NewSpecimenBizLogic newSpecimenBizLogic = (NewSpecimenBizLogic) factory
	 * .getBizLogic(Constants.NEW_SPECIMEN_FORM_ID); Object object =
	 * newSpecimenBizLogic.retrieve(Specimen.class.getName(), new Long(id));
	 * return (Specimen) object; }
	 */

	/**
	 * @param id
	 * @return
	 * @throws BizLogicException
	 * @throws NumberFormatException
	 * @throws DAOException
	 */
	/*
	 * private SpecimenCollectionGroup getSpecimenCollGrpFromDB(String id)
	 * throws BizLogicException { IFactory factory =
	 * AbstractFactoryConfig.getInstance().getBizLogicFactory();
	 * SpecimenCollectionGroupBizLogic specimenCollectionGroupBizLogic =
	 * (SpecimenCollectionGroupBizLogic) factory
	 * .getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID); Object object
	 * = specimenCollectionGroupBizLogic.retrieve(SpecimenCollectionGroup.class
	 * .getName(), new Long(id)); return (SpecimenCollectionGroup) object; }
	 */

	/**
	 * @param id
	 * @return
	 * @throws BizLogicException
	 */
	/*
	 * private OrderItem getOrderItemFromDB(String id) throws BizLogicException
	 * { IFactory factory =
	 * AbstractFactoryConfig.getInstance().getBizLogicFactory(); OrderBizLogic
	 * orderBizLogic = (OrderBizLogic) factory
	 * .getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID); Object object =
	 * orderBizLogic.retrieve(OrderItem.class.getName(), new Long(id)); return
	 * (OrderItem) object; }
	 */

	/**
	 * @return List : List of site objects.
	 * @throws BizLogicException : BizLogicException
	 */
	private List getSiteListToDisplay() throws BizLogicException
	{
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
				.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);
		// Sets the Site list.
		final String sourceObjectName = Site.class.getName();
		final String[] displayNameFields = {"name"};
		final String valueField = Constants.SYSTEM_IDENTIFIER;

		final List siteList = orderBizLogic.getList(sourceObjectName, displayNameFields,
				valueField, true);

		return siteList;
	}

	/**
	 * @param requestDetailsBean
	 *            RequestDetailsBean
	 * @param existingSpecimenorderItem
	 *            ExistingSpecimenOrderItem
	 * @return RequestDetailsBean object
	 * @throws DAOException : DAOException
	 */
	private RequestDetailsBean populateRequestDetailsBeanForExistingSpecimen(
			RequestDetailsBean requestDetailsBean,
			ExistingSpecimenOrderItem existingSpecimenorderItem) throws DAOException
	{
		requestDetailsBean.setRequestedItem(existingSpecimenorderItem.getSpecimen().getLabel());
		requestDetailsBean
				.setSpecimenId(existingSpecimenorderItem.getSpecimen().getId().toString());

		final List childrenSpecimenListToDisplay = OrderingSystemUtil
				.getNameValueBean(existingSpecimenorderItem.getSpecimen());
		requestDetailsBean.setSpecimenList(childrenSpecimenListToDisplay);

		requestDetailsBean.setInstanceOf("Existing");

		if (existingSpecimenorderItem.getSpecimen().getConsentTierStatusCollection().isEmpty())
		{
			requestDetailsBean.setConsentVerificationkey(Constants.NO_CONSENTS);

		}
		else
		{
			requestDetailsBean.setConsentVerificationkey(Constants.VIEW_CONSENTS);

		}

		// condition added by vaishali beacause requested Qty is coming NULL.
		// @FIX ME

		if (existingSpecimenorderItem.getRequestedQuantity() != null)
		{
			requestDetailsBean.setRequestedQty(existingSpecimenorderItem.getRequestedQuantity()
					.toString());
		}
		requestDetailsBean.setAvailableQty(existingSpecimenorderItem.getSpecimen()
				.getAvailableQuantity().toString());

		requestDetailsBean.setAssignedStatus(existingSpecimenorderItem.getStatus());
		requestDetailsBean.setClassName(existingSpecimenorderItem.getSpecimen().getClassName());
		requestDetailsBean.setType(existingSpecimenorderItem.getSpecimen().getSpecimenType());
		requestDetailsBean.setDescription(existingSpecimenorderItem.getDescription());
		requestDetailsBean.setOrderItemId(existingSpecimenorderItem.getId().toString());
		// Assigned Quantity
		if (existingSpecimenorderItem.getDistributedItem() != null)
		{
			requestDetailsBean.setAssignedQty(existingSpecimenorderItem.getDistributedItem()
					.getQuantity().toString());
		}

		return requestDetailsBean;
	}

	/**
	 * This function populates RequestDetailsBean object by fethcing data from
	 * DerivedSpecimenOrderItem domain object.
	 *
	 * @param requestDetailsBean
	 *            RequestDetailsBean object
	 * @param derivedSpecimenorderItem
	 *            DerivedSpecimenOrderItem object
	 * @param request
	 *            HttpServletRequest object
	 * @param finalSpecimenListId
	 *            primitive integer value
	 * @return RequestDetailsBean object
	 */
	private RequestDetailsBean populateRequestDetailsBeanForDerivedSpecimen(
			RequestDetailsBean requestDetailsBean,
			DerivedSpecimenOrderItem derivedSpecimenorderItem, HttpServletRequest request,
			int finalSpecimenListId)
	{
		requestDetailsBean
				.setRequestedItem(derivedSpecimenorderItem.getParentSpecimen().getLabel());
		final Long specimenId = derivedSpecimenorderItem.getParentSpecimen().getId();
		final Collection childrenSpecimenList = OrderingSystemUtil
				.getAllChildrenSpecimen(derivedSpecimenorderItem.getParentSpecimen()
						.getChildSpecimenCollection());
		final List finalChildrenSpecimenList = OrderingSystemUtil
				.getChildrenSpecimenForClassAndType(childrenSpecimenList, derivedSpecimenorderItem
						.getSpecimenClass(), derivedSpecimenorderItem.getSpecimenType());
		// removing final specimen List from session
		request.getSession().removeAttribute("finalSpecimenList" + finalSpecimenListId);
		// To display the available quantity of the selected specimen from
		// RequestFor dropdown.
		// request.getSession().setAttribute("finalSpecimenList"+
		// finalSpecimenListId, finalChildrenSpecimenList);

		final List childrenSpecimenListToDisplay = OrderingSystemUtil.getNameValueBeanList(
				finalChildrenSpecimenList, null);

		// setting requestFor list in request
		// request.setAttribute(Constants.REQUEST_FOR_LIST,
		// childrenSpecimenListToDisplay);

		requestDetailsBean.setSpecimenList(childrenSpecimenListToDisplay);
		requestDetailsBean.setSpecimenId(specimenId.toString());
		requestDetailsBean.setInstanceOf("Derived");
		requestDetailsBean.setRequestedQty(derivedSpecimenorderItem.getRequestedQuantity()
				.toString());
		// Displaying the quantity of the first specimen in the request for drop
		// down.
		requestDetailsBean.setAvailableQty("NA");
		requestDetailsBean.setSelectedSpecimenType("NA");

		/*
		 * if (childrenSpecimenListToDisplay.size() > 1) {
		 * requestDetailsBean.setAvailableQty(((Specimen)
		 * finalChildrenSpecimenList.get(1)).getAvailableQuantity().toString());
		 * } else {
		 * requestDetailsBean.setAvailableQty("NA");//derivedSpecimenorderItem
		 * .getSpecimen().getAvailableQuantity().getValue().toString() }
		 */
		// Assigned Quantity
		if (derivedSpecimenorderItem.getDistributedItem() != null)
		{
			requestDetailsBean.setAssignedQty(derivedSpecimenorderItem.getDistributedItem()
					.getQuantity().toString());
		}
		requestDetailsBean.setAssignedStatus(derivedSpecimenorderItem.getStatus());
		requestDetailsBean.setClassName(derivedSpecimenorderItem.getSpecimenClass());
		requestDetailsBean.setType(derivedSpecimenorderItem.getSpecimenType());
		requestDetailsBean.setDescription(derivedSpecimenorderItem.getDescription());
		requestDetailsBean.setOrderItemId(derivedSpecimenorderItem.getId().toString());

		return requestDetailsBean;
	}

	/**
	 * This function populates RequestDetailsBean instances by fetching data
	 * from PathologicalCaseOrderItem domain instance.
	 *
	 * @param requestDetailsBean
	 *            RequestDetailsBean object
	 * @param pathologicalCaseOrderItem
	 *            DerivedSpecimenOrderItem object
	 * @param request
	 *            HttpServletRequest object
	 * @param finalSpecimenListId
	 *            primitive integer value
	 * @return requestDetailsBean RequestDetailsBean object
	 */
	private RequestDetailsBean populateRequestDetailsBeanForPathologicalCase(
			RequestDetailsBean requestDetailsBean,
			PathologicalCaseOrderItem pathologicalCaseOrderItem, HttpServletRequest request,
			int finalSpecimenListId)
	{
		boolean isDerived = false;
		boolean isNotTissueBlock = false;
		requestDetailsBean.setRequestedItem(pathologicalCaseOrderItem.getSpecimenCollectionGroup()
				.getSurgicalPathologyNumber());

		final Collection childrenSpecimenList = pathologicalCaseOrderItem
				.getSpecimenCollectionGroup().getSpecimenCollection();
		// Removing distributed option if no specimens are present in that SCG.
		// ie. childrenSpecimenList.size() == 0
		// TODO find better option for this
		if (childrenSpecimenList.size() == 0)
		{
			isDerived = true;
		}

		if (pathologicalCaseOrderItem.getSpecimenClass() != null
				&& pathologicalCaseOrderItem.getSpecimenType() != null
				&& !pathologicalCaseOrderItem.getSpecimenClass().trim().equalsIgnoreCase("")
				&& !pathologicalCaseOrderItem.getSpecimenType().trim().equalsIgnoreCase(""))
		{
			isNotTissueBlock = true;
			isDerived = true;
		}

		final List totalChildrenSpecimenColl = this.getTotalChildspecimens(
				pathologicalCaseOrderItem, isNotTissueBlock, childrenSpecimenList);

		// removing final specimen List from session
		request.getSession().removeAttribute("finalSpecimenList" + finalSpecimenListId);
		// To display the available quantity of the selected specimen from
		// RequestFor dropdown.
		// request.getSession().setAttribute("finalSpecimenList"+
		// finalSpecimenListId, totalChildrenSpecimenColl);
		final List childrenSpecimenListToDisplay = OrderingSystemUtil.getNameValueBeanList(
				totalChildrenSpecimenColl, null);
		requestDetailsBean.setSpecimenList(childrenSpecimenListToDisplay);

		requestDetailsBean.setSpecimenCollGroupId(pathologicalCaseOrderItem
				.getSpecimenCollectionGroup().getId().toString());
		if (isDerived)
		{
			requestDetailsBean.setInstanceOf("DerivedPathological");
		}
		else
		{
			requestDetailsBean.setInstanceOf("Pathological");
		}
		requestDetailsBean.setRequestedQty(pathologicalCaseOrderItem.getRequestedQuantity()
				.toString());
		// Displaying the quantity of the first specimen in the request for drop
		// down.
		/*
		 * if (childrenSpecimenListToDisplay.size() != 0) {
		 * requestDetailsBean.setAvailableQty(((Specimen)
		 * totalChildrenSpecimenColl.get(0)).getAvailableQuantity().toString());
		 * } else { requestDetailsBean.setAvailableQty("-"); }
		 */
		// Assigned Quantity
		if (pathologicalCaseOrderItem.getDistributedItem() != null)
		{
			requestDetailsBean.setAssignedQty(pathologicalCaseOrderItem.getDistributedItem()
					.getQuantity().toString());
		}
		requestDetailsBean.setAssignedStatus(pathologicalCaseOrderItem.getStatus());
		requestDetailsBean.setClassName(pathologicalCaseOrderItem.getSpecimenClass());
		requestDetailsBean.setType(pathologicalCaseOrderItem.getSpecimenType());
		requestDetailsBean.setDescription(pathologicalCaseOrderItem.getDescription());
		requestDetailsBean.setOrderItemId(pathologicalCaseOrderItem.getId().toString());

		return requestDetailsBean;
	}

	/**
	 * This method will be called to get all the child specimens
	 * @param pathologicalCaseOrderItem
	 * @param isNotTissueBlock
	 * @param childrenSpecimenList
	 * @return
	 */
	private List getTotalChildspecimens(PathologicalCaseOrderItem pathologicalCaseOrderItem,
			boolean isNotTissueBlock, final Collection childrenSpecimenList)
	{
		final Iterator childrenSpecimenListIterator = childrenSpecimenList.iterator();
		List totalChildrenSpecimens = new ArrayList();
		while (childrenSpecimenListIterator.hasNext())
		{
			final Specimen specimen = (Specimen) childrenSpecimenListIterator.next();
			final List childSpecimenCollection = OrderingSystemUtil.getAllChildrenSpecimen(specimen
					.getChildSpecimenCollection());

			if (isNotTissueBlock)
			{
				// "Derived"
				totalChildrenSpecimens = OrderingSystemUtil.getChildrenSpecimenForClassAndType(
						childSpecimenCollection, pathologicalCaseOrderItem.getSpecimenClass(),
						pathologicalCaseOrderItem.getSpecimenType());
			}
			else
			{
				// "Block" . Specimen class = "Tissue" , Specimen Type =
				// "Block".
				totalChildrenSpecimens = OrderingSystemUtil.getChildrenSpecimenForClassAndType(
						childSpecimenCollection, "Tissue", "Block");
			}

		}
		return totalChildrenSpecimens;
	}

	/**
	 * This function populates the items status for individual order items in
	 * OrderList,DefinedArray List and Existing Array List and sets each of the
	 * list in the request attribute.
	 *
	 * @param requestList
	 *            ArrayList containing the list of orderitem objects
	 * @param request
	 *            HttpServletRequest object
	 * @param itemsAddedToArray
	 *            String indicating whether items are to be added to defined
	 *            array,existing array or to the speicmen orderlist
	 */
	void populateItemStatusList(List requestList, HttpServletRequest request,
			String itemsAddedToArray)
	{
		final Iterator requestlistItr = requestList.iterator();
		while (requestlistItr.hasNext())
		{
			final Object orderItemObj = requestlistItr.next();
			// In case of define array
			if (orderItemObj instanceof Map)
			{
				final Map defineArrayMap = (HashMap) orderItemObj;
				final Set keys = defineArrayMap.keySet();
				final Iterator keySetItr = keys.iterator();
				while (keySetItr.hasNext())
				{
					final DefinedArrayRequestBean definedArrayRequestBean = (DefinedArrayRequestBean) keySetItr
							.next();
					// Obtain request list for each defined array
					final List defineArrayDetailsList = (ArrayList) defineArrayMap
							.get(definedArrayRequestBean);
					final Iterator defineArrayDetailsListItr = defineArrayDetailsList.iterator();
					while (defineArrayDetailsListItr.hasNext())
					{
						final DefinedArrayDetailsBean definedArrayDetailsBean = (DefinedArrayDetailsBean) defineArrayDetailsListItr
								.next();
						// Get possible next statuses for a given status
						final List possibleStatusList = (List) request
								.getAttribute(Constants.ITEM_STATUS_LIST_FOR_ITEMS_IN_ARRAY);
						// Set possibleStatusList in the DefinedArrayDetails
						// bean instance.
						definedArrayDetailsBean.setItemStatusList(possibleStatusList);
					} // End while (while loop to iterate definedArrayList)
				}// End while (While loop to iterate list containing map objects
				// for deifned array)
			}// End if(orderItemObj instanceof Map)
			else
			{ // In case of Existing Array
				if (orderItemObj instanceof ExistingArrayDetailsBean)
				{
					final ExistingArrayDetailsBean existingArrayDetailsBean = (ExistingArrayDetailsBean) orderItemObj;
					final List possibleStatusList = OrderingSystemUtil
							.getPossibleStatusList(Constants.ORDER_REQUEST_STATUS_NEW);
					existingArrayDetailsBean.setItemStatusList(possibleStatusList);
				}
				// In case of Request Details List displayed in
				// RequestDetails.jsp
				if (orderItemObj instanceof RequestDetailsBean)
				{
					final RequestDetailsBean requestDetailsBean = (RequestDetailsBean) orderItemObj;
					List possibleStatusList = null;
					possibleStatusList = OrderingSystemUtil
							.getPossibleStatusList(Constants.ORDER_REQUEST_STATUS_NEW);
					requestDetailsBean.setItemsStatusList(possibleStatusList);
				}
			}// End else
		}// End OuterMost while
		/* Set the requestList in request Attribute */
		if (itemsAddedToArray.equals("addToDefinedArrays"))
		{ // Set the request attribute ARRAY_REQUESTSMAP_LIST when items are to
			// be displayed in the DefinedArrays List(ArrayRequests.jsp)
			request.setAttribute(Constants.DEFINEDARRAY_REQUESTS_LIST, requestList);
		}
		if (itemsAddedToArray.equals("addToExistingArrays"))
		{ // Set the request attribute ARRAY_REQUESTSMAP_LIST when items are to
			// be displayed in the Existing Arrays List(ArrayRequests.jsp)
			request.setAttribute(Constants.EXISISTINGARRAY_REQUESTS_LIST, requestList);
		}
		if (itemsAddedToArray.equals("addToOrderList"))
		{ // Set the request attribute REQUEST_DETAILS_LIST when items are to be
			// displayed in the RequestDetails.jsp
			request.setAttribute(Constants.REQUEST_DETAILS_LIST, requestList);
		}
	}

	/**
	 * method for getting isOnChange from request.
	 *
	 * @param request
	 *            :object of HttpServletResponse
	 * @return isOnChange :boolean
	 */
	private boolean isAjaxCalled(HttpServletRequest request)
	{
		boolean isOnChange = false;
		final String str = request.getParameter("isOnChange");
		if (str != null && str.equals("true"))
		{
			isOnChange = true;
		}
		return isOnChange;
	}

	/**
	 *
	 * @param specimen : specimen
	 * @param response : response
	 * @param identifier : identifier
	 * @throws Exception : Exception
	 */
	private void sendSpecimenDetails(Specimen specimen, HttpServletResponse response,
			String identifier) throws Exception
	{
		final PrintWriter out = response.getWriter();
		String responseString = "";
		if (specimen != null)
		{
			final String specimenQuantityUnit = OrderingSystemUtil.getUnit(specimen);
			responseString = identifier + Constants.RESPONSE_SEPARATOR
					+ specimen.getAvailableQuantity().toString() + Constants.RESPONSE_SEPARATOR
					+ specimen.getSpecimenType() + Constants.RESPONSE_SEPARATOR
					+ specimenQuantityUnit;
		}
		else
		{
			responseString = identifier + Constants.RESPONSE_SEPARATOR + "NA"
					+ Constants.RESPONSE_SEPARATOR + "NA" + Constants.RESPONSE_SEPARATOR + "NA";
		}
		response.setContentType("text/html");
		out.write(responseString);
	}

}