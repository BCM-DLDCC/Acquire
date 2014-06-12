/**
 * <p>
 * Title: BiohazardAction Class>
 * <p>
 * Description: This class initializes the fields of Biohazard.jsp Page
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 *
 * @author Aniruddha Phadnis
 * @version 1.00 Created on Jul 18, 2005
 */

package edu.wustl.catissuecore.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.util.EventsUtil;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.logger.Logger;

/**
 * @author renuka_bajpai
 */
public class NewSpecimenEventParametersAction extends SecureAction
{

	/**
	 * logger.
	 */
	private transient final Logger logger = Logger
			.getCommonLogger(NewSpecimenEventParametersAction.class);

	/**
	 * Overrides the execute method of Action class. Initializes the various
	 * fields in Biohazard.jsp Page.
	 *
	 * @param mapping
	 *            object of ActionMapping
	 * @param form
	 *            object of ActionForm
	 * @param request
	 *            object of HttpServletRequest
	 * @param response
	 *            object of HttpServletResponse
	 * @throws IOException
	 *             I/O exception
	 * @throws ServletException
	 *             servlet exception
	 * @return value for ActionForward object
	 */
	@Override
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		// //Gets the value of the operation parameter.
		// String operation = request.getParameter(Constants.OPERATION);
		//
		// //Sets the operation attribute to be used in the Add/Edit Institute
		// Page.
		// request.setAttribute(Constants.OPERATION, operation);
		try
		{
			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);

			String identifier = (String) request.getAttribute("specimenIdentifier");
			if (identifier == null)
			{
				identifier = request.getParameter("specimenIdentifier");
			}

			final Object object = bizLogic.retrieve(Specimen.class.getName(), new Long(identifier));

			if (object != null)
			{
				final Specimen specimen = (Specimen) object;

				// Setting Specimen Event Parameters' Grid
				final Collection specimenEventCollection = specimen.getSpecimenEventCollection();

				if (specimenEventCollection != null)
				{
					final List gridData = new ArrayList();
					final Iterator it = specimenEventCollection.iterator();
					// int i=1;

					while (it.hasNext())
					{
						final List rowData = new ArrayList();
						final SpecimenEventParameters eventParameters = (SpecimenEventParameters) it
								.next();

						if (eventParameters != null)
						{
							final String[] events = EventsUtil.getEvent(eventParameters);
							rowData.add(String.valueOf(eventParameters.getId()));
							rowData.add(events[0]);// Event Name

							final User user = eventParameters.getUser();
							rowData.add(user.getLastName() + ", " + user.getFirstName());
							rowData.add(CommonUtilities.parseDateToString(eventParameters
									.getTimestamp(), CommonServiceLocator.getInstance()
									.getDatePattern()));
							rowData.add(events[1]);// pageOf
							gridData.add(rowData);
						}
					}

					request.setAttribute(
							edu.wustl.simplequery.global.Constants.SPREADSHEET_DATA_LIST, gridData);
				}
			}

			request.setAttribute(Constants.EVENT_PARAMETERS_LIST, Constants.EVENT_PARAMETERS);
		}
		catch (final Exception e)
		{
			this.logger.error(e.getMessage(), e);
		}

		return mapping.findForward(request.getParameter(Constants.PAGE_OF));
	}
}