
package edu.wustl.catissuecore.action.shippingtracking;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.shippingtracking.ShipmentRequestForm;
import edu.wustl.catissuecore.bizlogic.shippingtracking.ShipmentRequestBizLogic;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.shippingtracking.ShipmentRequest;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.shippingtracking.Constants;
import edu.wustl.catissuecore.util.shippingtracking.ShippingTrackingUtility;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;

/**
 * this class implements the action for viewing shipment request summary.
 */
public class ViewRequestSummaryAction extends SecureAction
{

	/**
	 * logger.
	 */
	Logger logger = Logger.getCommonLogger(ViewRequestSummaryAction.class);

	/**
	 * action method for shipment request summary.
	 * @param mapping
	 *            object of ActionMapping class.
	 * @param form
	 *            object of ActionForm class.
	 * @param request
	 *            object of HttpServletRequest class.
	 * @param response
	 *            object of HttpServletResponse class.
	 * @return forward mapping.
	  */
	@Override
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		String target = edu.wustl.catissuecore.util.global.Constants.SUCCESS;
		String operationToPerform = request
				.getParameter(edu.wustl.catissuecore.util.global.Constants.OPERATION);
		if (operationToPerform == null || operationToPerform.equals(""))
		{
			operationToPerform = (String) request
					.getAttribute(edu.wustl.catissuecore.util.global.Constants.OPERATION);
		}
		if (operationToPerform == null || operationToPerform.equals(""))
		{
			operationToPerform = ((AbstractActionForm) form).getOperation();
		}
		request.setAttribute(edu.wustl.catissuecore.util.global.Constants.OPERATION,
				operationToPerform);
		final ActionErrors actionErrors = new ActionErrors();
		// Create DAO for passing as an argument to bizlogic's validate
		DAO dao = null;
		// Create ShipmentRequest Object explicitly
		ShipmentRequest shipmentRequest = new ShipmentRequest();
		try
		{
			dao = AppUtility.openDAOSession(null);
			final ShipmentRequestForm shipmentRequestForm = (ShipmentRequestForm) form;
			if (form == null)
			{
				form = (ShipmentRequestForm) request.getAttribute("shipmentRequestForm");
			}
			request.setAttribute("shipmentRequestForm", shipmentRequestForm);
			// Call ShipmentRequestBizlogic's method to validate the contents of
			// the shipment request
			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final ShipmentRequestBizLogic bizLogic = (ShipmentRequestBizLogic) factory
					.getBizLogic(Constants.SHIPMENT_REQUEST_FORM_ID);
			if (operationToPerform != null
					&& operationToPerform.equals(edu.wustl.catissuecore.util.global.Constants.EDIT))
			{
				if (shipmentRequestForm.getId() != 0l)
				{
					final List shipmentRequestList = dao.retrieve(ShipmentRequest.class.getName(),
							edu.wustl.catissuecore.util.global.Constants.SYSTEM_IDENTIFIER,
							shipmentRequestForm.getId());
					if (shipmentRequestList != null && shipmentRequestList.size() == 1)
					{
						shipmentRequest = (ShipmentRequest) shipmentRequestList.get(0);
					}
				}
			}
			shipmentRequest.setAllValues(shipmentRequestForm);
			// Check whether user have privilege to create request.
			// If not throws UserNotAuthorizedException.
			bizLogic.isAuthorized(dao, shipmentRequest, this.getSessionData(request));
			// Validations.
			boolean isValid = false;
			isValid = bizLogic.validate(shipmentRequest, dao, operationToPerform);
			if (isValid)
			{
				final Collection<ShipmentRequest> shipmentRequestCollection = bizLogic
						.createRequestObjects(shipmentRequest, dao, operationToPerform);
				final Integer[] specimenCountArr = new Integer[shipmentRequestCollection.size()];
				final Integer[] containerCountArr = new Integer[shipmentRequestCollection.size()];
				final String[] specimenLabelArr = new String[shipmentRequestForm
						.getSpecimenCounter()];
				final String[] containerLabelArr = new String[shipmentRequestForm
						.getContainerCounter()];
				// for holding reciever site's names
				final String[] recieverSiteNameArr = new String[shipmentRequestCollection.size()];
				int specimenCount = 0;
				int containerCount = 0;
				int count = 0;
				if (shipmentRequestCollection != null)
				{
					final Iterator<ShipmentRequest> shipmentRequestIterator = shipmentRequestCollection
							.iterator();
					ShipmentRequest shipmentRequestObject = shipmentRequestIterator.next();
					ShipmentRequestForm shipmentReqFormTemp = new ShipmentRequestForm();
					shipmentReqFormTemp.setAllValues(shipmentRequestObject);
					specimenCount = this.getSpecimenCounter(specimenLabelArr, specimenCount,
							shipmentReqFormTemp);
					containerCount = this.getContainerCount(containerLabelArr, containerCount,
							shipmentReqFormTemp);
					specimenCountArr[count] = shipmentReqFormTemp.getSpecimenCounter();
					containerCountArr[count] = shipmentReqFormTemp.getContainerCounter();
					// adding reciever site's names to the array
					recieverSiteNameArr[count] = shipmentRequestObject.getReceiverSite().getName();
					count++;
					shipmentReqFormTemp = new ShipmentRequestForm();
					shipmentReqFormTemp.reset(mapping, request);
					while (shipmentRequestIterator.hasNext())
					{
						shipmentRequestObject = shipmentRequestIterator.next();
						shipmentReqFormTemp = new ShipmentRequestForm();
						shipmentReqFormTemp.setAllValues(shipmentRequestObject);
						specimenCountArr[count] = shipmentReqFormTemp.getSpecimenCounter();
						containerCountArr[count] = shipmentReqFormTemp.getContainerCounter();
						// adding reciever site's names to the array
						recieverSiteNameArr[count] = shipmentRequestObject.getReceiverSite()
								.getName();
						count++;
						specimenCount = this.getSpecimenCounter(specimenLabelArr, specimenCount,
								shipmentReqFormTemp);
						containerCount = this.getContainerCount(containerLabelArr, containerCount,
								shipmentReqFormTemp);
						shipmentReqFormTemp.reset(mapping, request);
					}
				}
				request.getSession().setAttribute("shipmentRequestCollection",
						shipmentRequestCollection);
				request.setAttribute("siteCount", shipmentRequestCollection.size());
				request.setAttribute("specimenCountArr", specimenCountArr);
				request.setAttribute("containerCountArr", containerCountArr);
				request.setAttribute("specimenLabelArr", specimenLabelArr);
				request.setAttribute("containerLabelArr", containerLabelArr);
				request.setAttribute("recieverSiteNameArr", recieverSiteNameArr);
			}
			// Sets the sender and reciever site list attribute
			final String sourceObjectName = Site.class.getName();
			final String[] displayNameField = {edu.wustl.catissuecore.util.global.Constants.NAME};
			final String valueField = edu.wustl.catissuecore.util.global.Constants.SYSTEM_IDENTIFIER;
			final List siteList = bizLogic.getList(sourceObjectName, displayNameField, valueField,
					false);
			request.setAttribute(Constants.REQUESTERS_SITE_LIST, siteList);
			request.setAttribute("senderSiteName", ShippingTrackingUtility.getDisplayName(siteList,
					"" + shipmentRequestForm.getSenderSiteId()));
		}
		catch (final ApplicationException appExcep)
		{
			target = edu.wustl.catissuecore.util.global.Constants.FAILURE;
			actionErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item", appExcep
					.getMessage()));
			this.logger.error(appExcep.getMessage(), appExcep);
			appExcep.printStackTrace();
		}
		finally
		{
			try
			{
				AppUtility.closeDAOSession(dao);
			}
			catch (final ApplicationException exception)
			{
				this.logger.error(exception.getMessage(),exception);
				exception.printStackTrace();
				actionErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
						exception.getMessage()));
			}
		}
		this.saveErrors(request, actionErrors);
		return mapping.findForward(target);
	}

	/**
	 * This method gets container count.
	 * @param containerLabelArr container Label Array.
	 * @param containerCount container Count
	 * @param shipmentReqFormTemp ShipmentRequestForm
	 * @return containerCount
	 */
	private int getContainerCount(final String[] containerLabelArr, int containerCount,
			ShipmentRequestForm shipmentReqFormTemp)
	{
		if (shipmentReqFormTemp.getContainerCounter() > 0)
		{
			for (int containerCounter = 0; containerCounter < shipmentReqFormTemp
					.getContainerCounter(); containerCounter++)
			{
				containerLabelArr[containerCount++] = (String) shipmentReqFormTemp
						.getContainerDetails("containerLabel_" + (containerCounter + 1));
			}
		}
		return containerCount;
	}

	/**
	 * This method gets Specimen Counter.
	 * @param specimenLabelArr specimen Label Array.
	 * @param specimenCount specimen Count
	 * @param shipmentReqFormTemp Shipment Request Form
	 * @return specimen Count
	 */
	private int getSpecimenCounter(final String[] specimenLabelArr, int specimenCount,
			ShipmentRequestForm shipmentReqFormTemp)
	{
		if (shipmentReqFormTemp.getSpecimenCounter() > 0)
		{
			for (int specimenCounter = 0; specimenCounter < shipmentReqFormTemp
					.getSpecimenCounter(); specimenCounter++)
			{
				specimenLabelArr[specimenCount++] = (String) shipmentReqFormTemp
						.getSpecimenDetails("specimenLabel_" + (specimenCounter + 1));
			}
		}
		return specimenCount;
	}

	/**
	 * checks for the authorization.
	 * @param arg0
	 *            the request to be processed.
	 * @return boolean result of the operation.
	 * @throws Exception
	 *             if some problem occurs.
	 */
	protected boolean isAuthorizedToExecute(HttpServletRequest arg0) throws Exception
	{
		return true;
	}

	/**
	 * gets the class name.
	 * @param name
	 *            string to be parsed for.
	 * @return string containing the class name.
	 */
	public String getActualClassName(String name)
	{
		if (name != null && name.trim().length() != 0)
		{
			final String splitter = "\\.";
			final String[] arr = name.split(splitter);
			if (arr != null && arr.length != 0)
			{
				return arr[arr.length - 1];
			}
		}
		return name;
	}
}
