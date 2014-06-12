
package edu.wustl.catissuecore.action;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.bizlogic.TreeDataBizLogic;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.logger.Logger;

/**
 * @author renuka_bajpai
 */
public class StorageContainerTreeAction extends BaseAction
{

	/**
	 * logger.
	 */
	private transient final Logger logger = Logger
			.getCommonLogger(StorageContainerTreeAction.class);

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
	 * @param mapping object of Action Mapping
	 * @param request object of HttpServlet Request
	 * @param form object of ActionForm
	 * @param response object of HttpServletResponse
	 * @throws Exception Generic exception
	 * @return ActionForward  ActionForward
	 */

	@Override
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		/**
		 * finalDataListVector.
		 */
		Vector finalDataListVector = null;
		final String pageOf = request.getParameter(Constants.PAGE_OF);
		this.logger.debug("pageOf in treeview......." + pageOf);
		request.setAttribute(Constants.PAGE_OF, pageOf);
		final String operation = request.getParameter(Constants.OPERATION);
		request.setAttribute(Constants.OPERATION, operation);
		String reload = null;
		String target = Constants.SUCCESS;
		if (pageOf.equals(Constants.PAGE_OF_STORAGE_LOCATION))
		{
			final String storageContainerType = request
					.getParameter(Constants.STORAGE_CONTAINER_TYPE);
			request.setAttribute(Constants.STORAGE_CONTAINER_TYPE, storageContainerType);
			final String storageContainerID = request
					.getParameter(Constants.STORAGE_CONTAINER_TO_BE_SELECTED);
			request.setAttribute(Constants.STORAGE_CONTAINER_TO_BE_SELECTED, storageContainerID);
			final String position = request.getParameter(Constants.STORAGE_CONTAINER_POSITION);
			request.setAttribute(Constants.STORAGE_CONTAINER_POSITION, position);
		}
		try
		{
			reload = request.getParameter(Constants.RELOAD);
			final SessionDataBean sessionData = this.getSessionData(request);
			if (reload != null && reload.equals("true"))
			{
				final String treeNodeIDToBeReloaded = request.getParameter(Constants.TREE_NODE_ID);
				request.setAttribute(Constants.TREE_NODE_ID, treeNodeIDToBeReloaded);
				request.setAttribute(Constants.RELOAD, reload);
			}
			List dataList = new Vector();
			if (pageOf.equals(Constants.PAGE_OF_STORAGE_LOCATION)
					|| pageOf.equals(Constants.PAGE_OF_MULTIPLE_SPECIMEN)
					|| pageOf.equals(Constants.PAGE_OF_SPECIMEN)
					|| pageOf.equals(Constants.PAGE_OF_ALIQUOT))
			{
				final TreeDataBizLogic treeBizLogic = new TreeDataBizLogic();
				dataList = treeBizLogic.getSiteWithDummyContainer(sessionData.getUserId());
			}
			else if (pageOf.equals(Constants.PAGE_OF_STORAGE_CONTAINER))
			{
				final TreeDataBizLogic treeBizLogic = new TreeDataBizLogic();
				dataList = treeBizLogic.getSiteWithDummyContainer(sessionData.getUserId());
				target = Constants.PAGE_OF_STORAGE_CONTAINER;
			}
			if (dataList != null)
			{
				finalDataListVector = new Vector();
			}
			finalDataListVector = AppUtility.createTreeNodeVector(dataList, finalDataListVector);
			request.setAttribute(Constants.TREE_DATA, finalDataListVector);

		}
		catch (final Exception exp)
		{
			this.logger.error(exp.getMessage(), exp);
		}
		return mapping.findForward(target);
	}
}