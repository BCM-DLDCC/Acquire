/**
 * <p>Title: UpdateAvailableQuantity Class>
 * <p>Description:	Ajax Action Class for updated available quantity.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ashish Gupta
 * @version 1.00
 * Created on Nov 17,2006
 */

package edu.wustl.catissuecore.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.bizlogic.OrderBizLogic;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;

/**
 * @author ashish_gupta
 *
 */
public class UpdateAvailableQuantity extends BaseAction
{

	/**
	* Overrides the execute method in Action class.
	* @param mapping ActionMapping object
	* @param form ActionForm object
	* @param request HttpServletRequest object
	* @param response HttpServletResponse object
	* @return ActionForward object
	* @throws Exception object
	*/
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//The selected specimen Id.
		final String specimenId = request.getParameter("selectedSpecimen");
		//The row number.
		//String finalSpecimenListId = (String) request.getParameter("finalSpecimenListId");
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
				.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);

		String quantity = "";
		Specimen specimen = null;
		if (specimenId != null && !specimenId.equals("") && !specimenId.equals("#"))
		{
			final Long specId = Long.parseLong(specimenId);
			specimen = orderBizLogic.getSpecimenObject(specId);
			quantity = specimen.getAvailableQuantity().toString();

		}
		//Writing to response
		final PrintWriter out = response.getWriter();
		out.print(quantity);

		return null;
	}

}
