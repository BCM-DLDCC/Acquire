/**
 * <p>Title: RequestReceivedAction Class.</p>
 * <p>Description:RequestReceivedAction contains all the requestId.</p>
 * Copyright: Copyright (c) year 2008.
 * Company:
 * @author vijay_chittem .
 * @version 1.00.
 * Created on July 29th 2008.
 */

package edu.wustl.catissuecore.action.shippingtracking;

import edu.wustl.catissuecore.util.shippingtracking.Constants;

/**
 * this class implements the requests received action.
 */
public class RequestReceivedAction extends ProcessShipmentRequestsAction
{

	/**
	 * method to find the forward mapping.
	 * @param activityStatus status to be checked for.
	 * @param operation to be performed.
	 * @return mapping to which request is to be forwarded.
	 */
	@Override
	protected String getForwardTo(String activityStatus, String operation)
	{
		String forwardTo = edu.wustl.catissuecore.util.global.Constants.FAILURE;
		if (activityStatus.equals(Constants.ACTIVITY_STATUS_IN_PROGRESS))
		{
			if (operation != null
					&& operation.equals(edu.wustl.catissuecore.util.global.Constants.ADD))
			{
				forwardTo = Constants.CREATE_SHIPMENT_FOR_REQUEST;
			}
			else
			{
				forwardTo = Constants.VIEW_SHIPMENT_REQUEST;
			}
		}
		//bug 12814
		else if (activityStatus.equals("Rejected") || activityStatus.equals("Processed"))
		{
			forwardTo = Constants.VIEW_NONEDITABLE_SHIPMENT_REQUEST;
			//forwardTo = Constants.VIEW_SHIPMENT_REQUEST;
		}
		//Bug 13551
		else if (activityStatus.equals("Drafted"))
		{
			forwardTo = Constants.EDIT_SHIPMENT_REQUEST;
		}
		return forwardTo;
	}

}
