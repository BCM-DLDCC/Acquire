/**
 * <p>
 * Title: ParticipantSelectAction Class>
 * <p>
 * Description: This Class is used when participant is selected from the list.
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 *
 * @author vaishali_khandelwal
 * @Created on June 06, 2006
 */

package edu.wustl.catissuecore.action;

import java.util.HashMap;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.wustl.catissuecore.actionForm.ParticipantForm;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IDomainObjectFactory;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.factory.IForwordToFactory;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.logger.Logger;

/**
 * @author renuka_bajpai
 */
public class ParticipantSelectAction extends BaseAction
{

	/**
	 * logger.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(ParticipantSelectAction.class);

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
	 *
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
		final AbstractDomainObject abstractDomain = null;
		final ActionMessages messages = null;
		String target = null;
		final AbstractActionForm abstractForm = (AbstractActionForm) form;
		final ParticipantForm participantForm = (ParticipantForm) form;
		final IDomainObjectFactory iDomainObjectFactory = AbstractFactoryConfig.getInstance()
				.getDomainObjectFactory();

		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final IBizLogic bizLogic = factory.getBizLogic(abstractForm.getFormId());

		final String objectName = iDomainObjectFactory
				.getDomainObjectName(abstractForm.getFormId());

		LOGGER.info("Participant Id-------------------"
				+ request.getParameter("participantId"));

		final Object object = bizLogic.retrieve(objectName, new Long(request
				.getParameter("participantId")));
		request.removeAttribute("participantForm");
		final Participant participant = (Participant) object;

		LOGGER.info("Last name in ParticipantSelectAction:" + participant.getLastName());
		/**
		 * Name: Vijay Pande Reviewer Name: Aarti Sharma Instead of
		 * setAllValues() method retrieveFroEditMode() method is called to
		 * bypass lazy loading error in domain object
		 */
		// participantForm.setAllValues(participant);
		final DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
		defaultBizLogic.populateUIBean(Participant.class.getName(), participant.getId(),
				participantForm);

		// Setting the ParticipantForm in request for storing selected
		// participant's data.
		// ParticipantSelect attribute is used for deciding in next action
		// weather that action is called after ParticipantSelectAction or not
		request.setAttribute("participantForm1", participantForm);
		request.setAttribute("participantSelect", "yes");

		// Attributes to decide AddNew action
		final String submittedFor = request.getParameter(Constants.SUBMITTED_FOR);

		LOGGER.info("submittedFor in ParticipantSelectAction:" + submittedFor);
		// ------------------------------------------------ AddNewAction
		// Starts----------------------------
		// if AddNew action is executing, load FormBean from Session and
		// redirect to Action which initiated AddNew action
		if ((submittedFor != null) && (submittedFor.equals("AddNew")))
		{
			final HttpSession session = request.getSession();
			final Stack formBeanStack = (Stack) session.getAttribute(Constants.FORM_BEAN_STACK);

			if (formBeanStack != null)
			{
				// Retrieving AddNewSessionDataBean from Stack
				final AddNewSessionDataBean addNewSessionDataBean = (AddNewSessionDataBean) formBeanStack
						.pop();

				if (addNewSessionDataBean != null)
				{
					// Retrieving FormBean stored into AddNewSessionDataBean
					final AbstractActionForm sessionFormBean = addNewSessionDataBean
							.getAbstractActionForm();

					final String forwardTo = addNewSessionDataBean.getForwardTo();
					LOGGER.debug("forwardTo in ParticipantSelectAction--------->" + forwardTo);

					LOGGER.info("Id-----------------" + abstractDomain.getId());
					// Setting Identifier of new object into the FormBean to
					// populate it on the JSP page
					sessionFormBean.setAddNewObjectIdentifier(addNewSessionDataBean.getAddNewFor(),
							abstractDomain.getId());

					sessionFormBean.setMutable(false);

					// cleaning FORM_BEAN_STACK from Session if no
					// AddNewSessionDataBean available... Storing appropriate
					// value of SUBMITTED_FOR attribute
					if (formBeanStack.isEmpty())
					{
						session.removeAttribute(Constants.FORM_BEAN_STACK);
						request.setAttribute(Constants.SUBMITTED_FOR, "Default");
						LOGGER.debug("SubmittedFor set as Default in "
								+ "ParticipantSelectAction");

						LOGGER.debug("cleaning FormBeanStack from session*************");
					}
					else
					{
						request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
					}

					// Storing FormBean into Request to populate data on the
					// page being forwarded after AddNew activity,
					// FormBean should be stored with the name defined into
					// Struts-Config.xml to populate data properly on JSP page
					final String formBeanName = CommonUtilities.getFormBeanName(sessionFormBean);
					request.setAttribute(formBeanName, sessionFormBean);

					LOGGER.debug("InitiliazeAction operation=========>"
							+ sessionFormBean.getOperation());

					// Storing Success messages into Request to display on JSP
					// page being forwarded after AddNew activity
					if (messages != null)
					{
						this.saveMessages(request, messages);
					}

					// Status message key.
					final String statusMessageKey = String.valueOf(abstractForm.getFormId() + "."
							+ String.valueOf(abstractForm.isAddOperation()));
					request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMessageKey);

					// Changing operation attribute in parth specified in
					// ForwardTo mapping, If AddNew activity started from Edit
					// page
					if ((sessionFormBean.getOperation().equals("edit")))
					{
						LOGGER.debug("Edit object Identifier while"
								+ " AddNew is from Edit operation==>" + sessionFormBean.getId());
						final ActionForward editForward = new ActionForward();

						final String addPath = (mapping.findForward(forwardTo)).getPath();
						LOGGER.debug("Operation before edit==========>" + addPath);

						final String editPath = addPath.replaceFirst("operation=add",
								"operation=edit");
						LOGGER.debug("Operation edited=============>" + editPath);
						editForward.setPath(editPath);

						return editForward;
					}

					return (mapping.findForward(forwardTo));
				}
				// Setting target as FAILURE if AddNewSessionDataBean is null
				else
				{
					target = new String(Constants.FAILURE);

					final ActionErrors errors = new ActionErrors();
					final ActionError error = new ActionError("errors.item.unknown",
							AbstractDomainObject.parseClassName(objectName));
					errors.add(ActionErrors.GLOBAL_ERROR, error);
					this.saveErrors(request, errors);
				}
			}
		}
		// ------------------------------------------------ AddNewAction
		// Ends----------------------------
		// ----------ForwardTo Starts----------------
		else if ((submittedFor != null) && (submittedFor.equals("ForwardTo")))
		{
			LOGGER
					.debug("SubmittedFor is ForwardTo in CommonAddEditAction...................");

			// Storing appropriate value of SUBMITTED_FOR attribute
			request.setAttribute(Constants.SUBMITTED_FOR, "Default");

			// storing HashMap of forwardTo data into Request
			request.setAttribute("forwardToHashMap", this.generateForwardToHashMap(abstractForm,
					abstractDomain));
		}
		// ----------ForwardTo Ends----------------

		// setting target to ForwardTo attribute of submitted Form
		if (abstractForm.getForwardTo() != null && abstractForm.getForwardTo().trim().length() > 0)
		{
			final String forwardTo = abstractForm.getForwardTo();
			LOGGER.debug("ForwardTo in Add :-- : " + forwardTo);
			target = forwardTo;
			// return (mapping.findForward(forwardTo));
		}

		LOGGER.info("target in ParticipantSelectAction:" + target);
		return (mapping.findForward(target));

	}

	/**
	 * 	 * This method generates HashMap of data required to be forwarded if Form is
		 * submitted for ForwardTo request.
	 * @param abstractForm : abstractForm
	 * @param abstractDomain : abstractDomain
	 * @return HashMap : HashMap
	 * @throws BizLogicException : BizLogicException
	 */
	private HashMap generateForwardToHashMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws BizLogicException
	{
		// getting instance of ForwardToProcessor
		// AbstractForwardToProcessor forwardToProcessor=
		// ForwardToFactory.getForwardToProcessor();

		final IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory();
		final AbstractForwardToProcessor forwardToProcessor = factory.getForwardToProcessor();

		// Populating HashMap of the data required to be forwarded on next page
		final HashMap forwardToHashMap = (HashMap) forwardToProcessor.populateForwardToData(
				abstractForm, abstractDomain);

		return forwardToHashMap;
	}
}
