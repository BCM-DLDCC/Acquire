
package edu.wustl.catissuecore.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.DepartmentForm;
import edu.wustl.catissuecore.bizlogic.DepartmentBizLogic;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.CommonAddEditAction;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author renuka_bajpai
 */
public class AddDepartmentAction extends CommonAddEditAction
{

	/**
	 * logger.
	 */
	private transient final Logger logger = Logger.getCommonLogger(AddDepartmentAction.class);

	/**
	 * @param mapping : mapping
	 * @param form : form
	 * @param request : request
	 * @param response : response
	 * @throws IOException : IOException
	 * @throws ServletException : ServletException
	 * @return ActionForward
	 */
	@Override
	public ActionForward executeXSS(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		final String departmentName = request.getParameter(Constants.DEPARTMENT_NAME);
		final DepartmentBizLogic bizlogic = new DepartmentBizLogic();
		String departmentId = null;
		String responseString = null;

		/**
		 * Setting the department name to form
		 */
		final DepartmentForm departmentForm = (DepartmentForm) form;
		departmentForm.setOperation(Constants.ADD);
		departmentForm.setName(departmentName);

		// Saving the department to the Database using COmmonAddEditAction
		final ActionForward forward = super.executeXSS(mapping, departmentForm, request, response);
		if ((forward != null) && (forward.getName().equals(Constants.FAILURE)))
		{
			responseString = AppUtility.getResponseString(request, responseString);
		}
		else
		{
			try
			{
				departmentId = bizlogic.getLatestDepartment(departmentName);
				responseString = departmentId + Constants.RESPONSE_SEPARATOR + departmentName;
			}
			catch (final BizLogicException bizEx)
			{
				this.logger.error("Exception occurred in retrieving Department",bizEx);
			}
		}

		final PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		/**
		 * sending the response as departmentId @ departmentName
		 */
		out.write(responseString);

		return null;
	}
}
