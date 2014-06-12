
package edu.wustl.catissuecore.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.XSSSupportedAction;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.dao.exception.DAOException;

/**
 * This class sets the link values for the Help.jsp page tab/icons.
 * @author sagar_baldwa
 */
public class RedirectToHelpAction extends XSSSupportedAction
{

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
	 * @throws IOException : IOException
	 * @throws ServletException : ServletException
	 * @throws DAOException : DAOException
	 * @return ActionForward : ActionForward
	 */
	@Override
	public ActionForward executeXSS(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException, DAOException
	{
		//Retrives link values from caTissueCore_Properties.xml file
		request.setAttribute(Constants.USER_GUIDE_LINK_PROPERTY, XMLPropertyHandler
				.getValue(Constants.USER_GUIDE_LINK_PROPERTY));
		request.setAttribute(Constants.TECHNICAL_GUIDE_LINK_PROPERTY, XMLPropertyHandler
				.getValue(Constants.TECHNICAL_GUIDE_LINK_PROPERTY));
		request.setAttribute(Constants.TRAINING_GUIDE_LINK_PROPERTY, XMLPropertyHandler
				.getValue(Constants.TRAINING_GUIDE_LINK_PROPERTY));
		request.setAttribute(Constants.UML_MODEL_LINK_PROPERTY, XMLPropertyHandler
				.getValue(Constants.UML_MODEL_LINK_PROPERTY));
		request.setAttribute(Constants.KNOWLEDGE_CENTER_FORUM_LINK_PROPERTY, XMLPropertyHandler
				.getValue(Constants.KNOWLEDGE_CENTER_FORUM_LINK_PROPERTY));
		request.setAttribute(Constants.KNOWLEDGE_CENTER_LINK_PROPERTY, XMLPropertyHandler
				.getValue(Constants.KNOWLEDGE_CENTER_LINK_PROPERTY));

		return mapping.findForward(Constants.SUCCESS);
	}
}
