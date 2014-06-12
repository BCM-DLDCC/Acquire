
package edu.wustl.catissuecore.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.OrderBiospecimenArrayForm;
import edu.wustl.catissuecore.actionForm.OrderForm;
import edu.wustl.catissuecore.bizlogic.OrderBizLogic;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;

/**
 * @author renuka_bajpai
 */
public class OrderBiospecimenArrayAction extends BaseAction
{

	/**
	 * @param mapping
	 *            ActionMapping object
	 * @param form
	 *            ActionForm object
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 * @return ActionForward object
	 * @throws Exception
	 *             object
	 */
	@Override
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		final OrderBiospecimenArrayForm arrayObject = (OrderBiospecimenArrayForm) form;
		final HttpSession session = request.getSession();
		String target = null;

		if (session.getAttribute("OrderForm") != null)
		{
			final OrderForm orderForm = (OrderForm) session.getAttribute("OrderForm");
			arrayObject.setOrderForm(orderForm);

			if (orderForm.getDistributionProtocol() != null)
			{
				this.getProtocolName(request, arrayObject, orderForm);
			}

			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
					.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);

			final List specimenArrayList = orderBizLogic.getSpecimenArrayDataFromDatabase(request);
			request.setAttribute("SpecimenNameList", specimenArrayList);

			request.setAttribute("typeOf", "specimenArray");
			request.setAttribute("OrderBiospecimenArrayForm", arrayObject);
			target = Constants.SUCCESS;
		}
		else
		{
			target = Constants.FAILURE;
		}
		return mapping.findForward(target);

	}

	/**
	 * @param request
	 *            HttpServletRequest object
	 * @param arrayObject
	 *            OrderBiospecimenArrayForm object
	 * @param orderForm
	 *            OrderForm object
	 * @throws Exception
	 *             object
	 */
	private void getProtocolName(HttpServletRequest request, OrderBiospecimenArrayForm arrayObject,
			OrderForm orderForm) throws Exception
	{
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final OrderBizLogic orderBizLogic = (OrderBizLogic) factory
				.getBizLogic(Constants.REQUEST_LIST_FILTERATION_FORM_ID);
		final List protocolList = orderBizLogic.getDistributionProtocol(request);

		for (int i = 0; i < protocolList.size(); i++)
		{
			final NameValueBean obj = (NameValueBean) protocolList.get(i);

			if (orderForm.getDistributionProtocol().equals(obj.getValue()))
			{
				arrayObject.setDistrbutionProtocol(obj.getName());
			}
		}
	}

}
