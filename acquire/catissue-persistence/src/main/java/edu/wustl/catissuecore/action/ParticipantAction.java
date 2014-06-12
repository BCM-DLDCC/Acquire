/**
 * <p>
 * Title: ParticipantAction Class>
 * <p>
 * Description: This class initializes the fields in the Participant Add/Edit
 * webpage.
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 *
 * @author Gautam Shetty
 * @version 1.00 Created on Apr 7, 2005
 */

package edu.wustl.catissuecore.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.wustl.catissuecore.action.annotations.AnnotationConstants;
import edu.wustl.catissuecore.actionForm.ParticipantForm;
import edu.wustl.catissuecore.bean.ConsentBean;
import edu.wustl.catissuecore.bean.ConsentResponseBean;
import edu.wustl.catissuecore.bizlogic.CollectionProtocolBizLogic;
import edu.wustl.catissuecore.bizlogic.ParticipantBizLogic;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ConsentTier;
import edu.wustl.catissuecore.domain.ConsentTierResponse;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.util.CatissueCoreCacheManager;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;

/**
 * This class initializes the fields in the Participant Add/Edit webpage.
 *
 * @author gautam_shetty
 */
public class ParticipantAction extends SecureAction
{

	/** logger. */
	private static final Logger LOGGER = Logger.getCommonLogger(ParticipantAction.class);

	/**
	 * Overrides the execute method of Action class. Sets the various fields in
	 * Participant Add/Edit webpage.
	 *
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 *
	 * @return value for ActionForward object
	 *
	 * @throws Exception generic exception
	 */
	@Override
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		final String refPart = request.getParameter("refresh");
		if (refPart != null)
		{
			request.setAttribute("refresh", refPart);

		}

		ParticipantForm participantForm = (ParticipantForm) form;
		final HttpSession session = request.getSession();
		// This if condition is for participant lookup. When participant is
		// selected from the list then
		// that participant gets stored in request as participantform1.
		// After that we have to show the slected participant in o/p
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final ParticipantBizLogic partBiz = (ParticipantBizLogic) factory
				.getBizLogic(Constants.PARTICIPANT_FORM_ID);
		final IBizLogic bizlogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);

		if (request.getAttribute("participantSelect") != null && request.getAttribute("participantForm1") != null)
		{
				participantForm = (ParticipantForm) request.getAttribute("participantForm1");
				request.setAttribute("participantForm", participantForm);
		}

		if (participantForm.getOperation().equals(Constants.ADD))
		{
			final String clrConsentSess = request.getParameter("clearConsentSession");
			if (clrConsentSess != null && clrConsentSess.equals("true"))
			{
					session.removeAttribute(Constants.CONSENT_RESPONSE);
			}
		}
		/*
		 * Sachin bug id 5317 Set Selcted Cp id in participant form so that it
		 * is get reflected whi;le adding SCG
		 */
		final String cpid = request.getParameter("cpSearchCpId");
		if (participantForm.getCpId() == -1 && cpid != null)
		{
			participantForm.setCpId(Long.valueOf(cpid));
		}

		if (participantForm.getOperation().equals(Constants.EDIT))
		{
			request.setAttribute("participantId", String.valueOf(participantForm.getId()));
			// Setting Consent Response Bean to Session
			// Abhishek Mehta
			final Map consentResponseHT = participantForm.getConsentResponseHashTable();
			if (consentResponseHT != null)
			{
				session.setAttribute(Constants.CONSENT_RESPONSE, consentResponseHT);
			}
		}
		/*
		 * Falguni Sachde bug id :8150 Set participantId as request attribute as
		 * Its required in case of viewannotations view of Edit participant
		 */
		if (participantForm.getOperation().equals(Constants.VIEW_ANNOTATION))
		{
			request.setAttribute("participantId", String.valueOf(participantForm.getId()));

		}
		final List key = new ArrayList();
		key.add("ParticipantMedicalIdentifier:outer_Site_id");
		key.add("ParticipantMedicalIdentifier:outer_medicalRecordNumber");
		key.add("ParticipantMedicalIdentifier:outer_id");

		// Gets the map from ActionForm
		final Map map = participantForm.getValues();

		final String delRegistration = request.getParameter("deleteRegistration");
		final String status = request.getParameter("status");
		if (delRegistration == null && status != null && status.equalsIgnoreCase("true"))
		{
			// Calling DeleteRow of BaseAction class
			MapDataParser.deleteRow(key, map, request.getParameter("status"));
		}

		// Start Collection Protocol Registration For Participant
		// Abhishek Mehta
		final List cprKey = new ArrayList();

		cprKey
				.add("CollectionProtocolRegistration:outer_CollectionProtocol_id");
		cprKey
				.add("CollectionProtocolRegistration:outer_CollectionProtocol_shortTitle");
		cprKey
				.add("CollectionProtocolRegistration:outer_protocolParticipantIdentifier");
		cprKey.add("CollectionProtocolRegistration:outer_id");
		cprKey
				.add("CollectionProtocolRegistration:outer_registrationDate");
		cprKey
				.add("CollectionProtocolRegistration:outer_isConsentAvailable");
		cprKey
				.add("CollectionProtocolRegistration:outer_activityStatus");

		final Map mapCPR = participantForm
				.getCollectionProtocolRegistrationValues();

		final String fromSubmitAction = request.getParameter("fromSubmitAction");
		if (fromSubmitAction == null)
		{


			if (mapCPR != null
					&& !mapCPR.isEmpty())
			{
				final int count = participantForm.getCollectionProtocolRegistrationValueCounter();
				for (int i = 1; i <= count; i++)
				{
					final String cprActStatusKey = "CollectionProtocolRegistration:"
							+ i + "_activityStatus";
					if (mapCPR
							.get(cprActStatusKey) == null)
					{
						participantForm.setCollectionProtocolRegistrationValue(
								cprActStatusKey,
								Status.ACTIVITY_STATUS_ACTIVE.toString());
					}
				}
			}


		}
		else
		{

			/**
			 * Name: Vijay Pande Reviewer Name: Aarti Sharma Following method
			 * call is added to set ParticipantMedicalNumber id in the map after
			 * add/edit operation
			 */
			final int count = participantForm.getCollectionProtocolRegistrationValueCounter();
			this.setParticipantMedicalNumberId(bizlogic, participantForm.getId(), map);
			// Updating Collection Protocol Registration
			this.updateCollectionProtocolRegistrationCollection(bizlogic, participantForm,
					count);
			final int cprCount = this.updateCollectionProtocolRegistrationMap(
					mapCPR, count);
			participantForm.setCollectionProtocolRegistrationValueCounter(cprCount);
		}

		MapDataParser.deleteRow(cprKey,
				mapCPR, "true");

		// Sets the collection Protocol if page is opened from collection
		// protocol registration
		if (participantForm.getOperation().equals(Constants.ADD))
		{
			final String pageOf = request.getParameter(Constants.PAGE_OF);
			if (pageOf.equalsIgnoreCase(Constants.PAGE_OF_PARTICIPANT_CP_QUERY))
			{
				final String collProtId = request.getParameter(Constants.CP_SEARCH_CP_ID);
				if (collProtId != null)
				{
					 String cpIdKey = "CollectionProtocolRegistration:1_CollectionProtocol_id";
					 String isConsentAvailKey = "CollectionProtocolRegistration:1_isConsentAvailable";
					 String cprActivityStausKey = "CollectionProtocolRegistration:1_activityStatus";
					 String cprDateKey = "CollectionProtocolRegistration:1_registrationDate";

					participantForm.setCollectionProtocolRegistrationValue(cpIdKey,
							collProtId);

					final Collection consentList = this.getConsentList(bizlogic,
							collProtId);
					if (consentList != null && consentList.isEmpty())
					{
						participantForm.setCollectionProtocolRegistrationValue(
								isConsentAvailKey, Constants.NO_CONSENTS_DEFINED);
					}
					else if (consentList != null && !consentList.isEmpty())
					{
						participantForm
								.setCollectionProtocolRegistrationValue(isConsentAvailKey,
										Constants.PARTICIPANT_CONSENT_ENTER_RESPONSE);
					}
					participantForm.setCollectionProtocolRegistrationValue(
							cprActivityStausKey,
							Status.ACTIVITY_STATUS_ACTIVE.toString());
					final String cprDateValue = CommonUtilities
							.parseDateToString(Calendar.getInstance().getTime(),
									CommonServiceLocator.getInstance().getDatePattern());
					participantForm.setCollectionProtocolRegistrationValue(
							cprDateKey,
							cprDateValue);
					participantForm.setCollectionProtocolRegistrationValueCounter(1);
				}
			}
		}

		// Sets the collection Protocol if page is opened in add mode or if that
		// participant doesnt have any registration
		if (mapCPR != null
				&& mapCPR.isEmpty()
				|| participantForm.getCollectionProtocolRegistrationValueCounter() == 0)
		{
			 String collProtRegDateKey = "CollectionProtocolRegistration:1_registrationDate";
			 String collectionProtocolRegistrationActivityStausKey = "CollectionProtocolRegistration:1_activityStatus";
			 String collProtRegDatVal = CommonUtilities
					.parseDateToString(Calendar.getInstance().getTime(), CommonServiceLocator
							.getInstance().getDatePattern());
			participantForm.setDefaultCollectionProtocolRegistrationValue(
					collProtRegDateKey, collProtRegDatVal);
			participantForm.setDefaultCollectionProtocolRegistrationValue(
					collectionProtocolRegistrationActivityStausKey, Status.ACTIVITY_STATUS_ACTIVE
							.toString());
			participantForm.setCollectionProtocolRegistrationValueCounter(1);
		}

		// Abhishek Mehta
		// End Collection Protocol Registration For Participant

		// Gets the value of the operation parameter.
		final String operation = request.getParameter(Constants.OPERATION);

		// Sets the operation attribute to be used in the Add/Edit Participant
		// Page.
		request.setAttribute(Constants.OPERATION, operation);

		// Sets the pageOf attribute (for Add,Edit or Query Interface)
		final String pageOf = request.getParameter(Constants.PAGE_OF);

		request.setAttribute(Constants.PAGE_OF, pageOf);

		// Sets the genderList attribute to be used in the Add/Edit Participant
		// Page.
		final List genderList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_GENDER, null);
		genderList.remove(0);
		request.setAttribute(Constants.GENDER_LIST, genderList);

		// Sets the genotypeList attribute to be used in the Add/Edit
		// Participant Page.
		final List genotypeList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_GENOTYPE, null);
		request.setAttribute(Constants.GENOTYPE_LIST, genotypeList);

		final List ethnicityList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_ETHNICITY, null);
		request.setAttribute(Constants.ETHNICITY_LIST, ethnicityList);

		// Sets the raceList attribute to be used in the Add/Edit Participant
		// Page.
		final List raceList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_RACE, null);
		request.setAttribute(Constants.RACELIST, raceList);

		// Sets the vitalStatus attribute to be used in the Add/Edit Participant
		// Page.
		final List vitalStatusList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_VITAL_STATUS, null);
		vitalStatusList.remove(0);
		request.setAttribute(Constants.VITAL_STATUS_LIST, vitalStatusList);

		// Sets the activityStatusList attribute to be used in the Site Add/Edit
		// Page.
		request.setAttribute(Constants.ACTIVITYSTATUSLIST, Constants.ACTIVITY_STATUS_VALUES);
		// By Abhishek
		// ParticipantBizLogic bizlogic = (ParticipantBizLogic)
		// BizLogicFactory.getInstance()
		// .getBizLogic(Constants.PARTICIPANT_FORM_ID);

		// Sets the Site list of corresponding type.
		final SessionDataBean sessionDataBean = this.getSessionData(request);
		List siteList = new ArrayList();
		final String sourceObjectName = Site.class.getName();
		final String[] displayNameFields = {"name"};
		final String valueField = Constants.SYSTEM_IDENTIFIER;
		siteList = partBiz.getList(sourceObjectName, displayNameFields, valueField,
				true);
		request.setAttribute(Constants.SITELIST, siteList);

		List list = new ArrayList();
		if (sessionDataBean != null && sessionDataBean.isAdmin())
		{
			// Set the collection protocol title list
			final String cpSrcObjName = CollectionProtocol.class.getName();
			final String[] cpDisplayNameFields = {"shortTitle"};
			final String cpValueField = Constants.SYSTEM_IDENTIFIER;
			list = partBiz.getList(cpSrcObjName, cpDisplayNameFields,
					cpValueField, true);
		}
		else
		{
			final CollectionProtocolBizLogic cpBizLogic = (CollectionProtocolBizLogic) factory
					.getBizLogic(Constants.COLLECTION_PROTOCOL_FORM_ID);
			final String cpId = request.getParameter(Constants.CP_SEARCH_CP_ID);
			list = partBiz.getCPForUserWithRegistrationAcess(sessionDataBean
					.getUserId());

			// This is done when participant is added in cp based view.
			// Adding the CP selected in the cp based view to the list of CPs
			// used in CPR section
			if (cpId != null && list.size() == 1)
			{
					final String shortTitle = cpBizLogic.getShortTitle(Long.valueOf(cpId));
					final NameValueBean nvb = new NameValueBean(shortTitle, cpId);
					list.add(nvb);
			}
		}
		request.setAttribute(Constants.PROTOCOL_LIST, list);
		// report id from session
		// Long reportIdFormSession = (Long)
		// session.getAttribute(Constants.IDENTIFIED_REPORT_ID);
		// set associated identified report id
		Long reportId = this.getAssociatedIdentifiedReportId(partBiz, participantForm
				.getId());
		if (reportId == null)
		{
			reportId = Long.valueOf(-1);
		}
		else if (AppUtility.isQuarantined(reportId))
		{
			reportId = Long.valueOf(-2);
		}
		session.setAttribute(Constants.IDENTIFIED_REPORT_ID, reportId);
		// Falguni:Performance Enhancement.
		Long participantEntityId = null;
		if (CatissueCoreCacheManager.getInstance().getObjectFromCache(
				AnnotationConstants.PARTICIPANT_REC_ENTRY_ENTITY_ID) != null)
		{
			participantEntityId = (Long) CatissueCoreCacheManager.getInstance().getObjectFromCache(
					AnnotationConstants.PARTICIPANT_REC_ENTRY_ENTITY_ID);
		}
		else
		{
			participantEntityId = AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_PARTICIPANT_REC_ENTRY);
			CatissueCoreCacheManager.getInstance().addObjectToCache(
					AnnotationConstants.PARTICIPANT_REC_ENTRY_ENTITY_ID, participantEntityId);
		}
		request.setAttribute(AnnotationConstants.PARTICIPANT_REC_ENTRY_ENTITY_ID,
				participantEntityId);

		LOGGER.debug("pageOf :---------- " + pageOf);

		return mapping.findForward(pageOf);
	}

	/**
	 * Update collection protocol registration collection.
	 *
	 * @param bizLogic : bizLogic
	 * @param participantForm : participantForm
	 * @param count : count
	 *
	 * @throws Exception : Exception
	 */
	private void updateCollectionProtocolRegistrationCollection(IBizLogic bizLogic,
			ParticipantForm participantForm, int count)
			throws Exception
	{
		// Gets the collection Protocol Registration map from ActionForm
		final Map mapCollectionProtocolRegistration = participantForm
				.getCollectionProtocolRegistrationValues();

		if (mapCollectionProtocolRegistration != null
				&& !mapCollectionProtocolRegistration.isEmpty())
		{
			final Collection consentResponseBeanCollection = participantForm
					.getConsentResponseBeanCollection();
			this.setParticipantCollectionProtocolRegistrationId(bizLogic, participantForm.getId(),
					mapCollectionProtocolRegistration, consentResponseBeanCollection, count);
		}
	}

	/**
	 * Update collection protocol registration map.
	 *
	 * @param mapCollectionProtocolRegistration : mapCollectionProtocolRegistration
	 * @param count : count
	 *
	 * @return int : int
	 *
	 * @throws Exception : Exception
	 */
	private int updateCollectionProtocolRegistrationMap(Map mapCollectionProtocolRegistration,
			int count) throws Exception
	{
		int cprCount = 0;
		for (int i = 1; i <= count; i++)
		{
			final String isActive = "CollectionProtocolRegistration:" + i + "_activityStatus";
			final String collectionProtocolTitle = "CollectionProtocolRegistration:" + i
					+ "_CollectionProtocol_id";
			final String activityStatus = (String) mapCollectionProtocolRegistration.get(isActive);
			final String cpId = (String) mapCollectionProtocolRegistration
					.get(collectionProtocolTitle);
			if (activityStatus == null && cpId == null)
			{
				cprCount++;
				continue;
			}
			if (activityStatus == null)
			{
				mapCollectionProtocolRegistration.put(isActive, Status.ACTIVITY_STATUS_ACTIVE
						.toString());
			}

			if (activityStatus != null && activityStatus.equalsIgnoreCase(Constants.DISABLED)
					|| (cpId != null && cpId.equalsIgnoreCase("-1")))
			{

				final String collectionProtocolParticipantId = "CollectionProtocolRegistration:"
						+ i + "_protocolParticipantIdentifier";
				final String collectionProtocolRegistrationDate = "CollectionProtocolRegistration:"
						+ i + "_registrationDate";
				final String collectionProtocolIdentifier = "CollectionProtocolRegistration:" + i
						+ "_id";
				final String isConsentAvailable = "CollectionProtocolRegistration:" + i
						+ "_isConsentAvailable";
				final String collectionProtocolParticipantTitle = "CollectionProtocolRegistration:"
						+ i + "_CollectionProtocol_shortTitle";

				mapCollectionProtocolRegistration.remove(collectionProtocolTitle);
				mapCollectionProtocolRegistration.remove(collectionProtocolParticipantId);
				mapCollectionProtocolRegistration.remove(collectionProtocolRegistrationDate);
				mapCollectionProtocolRegistration.remove(collectionProtocolIdentifier);
				mapCollectionProtocolRegistration.remove(isActive);
				mapCollectionProtocolRegistration.remove(isConsentAvailable);
				mapCollectionProtocolRegistration.remove(collectionProtocolParticipantTitle);
				cprCount++;
			}
		}
		return (count - cprCount);
	}

	/**
	 * Gets the consent list.
	 *
	 * @param bizLogic : bizLogic
	 * @param cpId : cpId
	 *
	 * @return Collection : Collection
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	private Collection getConsentList(IBizLogic bizLogic, String cpId) throws BizLogicException
	{
		final Collection consentTierCollection = (Collection) bizLogic.retrieveAttribute(
				CollectionProtocol.class.getName(), Long.parseLong(cpId),
				"elements(consentTierCollection)");
		return consentTierCollection;
	}

	/**
	 * * THis method sets the ParticipantMedicalNumber id in the map Bug_id: 4386
	 * After adding new participant medical number CommonAddEdit was unable to
	 * set id in the value map for participant medical number Therefore here
	 * expicitly id of the participant medical number are set.
	 *
	 * @param bizLogic bizLogic
	 * @param participantId : participantId
	 * @param map : map
	 *
	 * @throws Exception : Exception
	 */
	private void setParticipantMedicalNumberId(IBizLogic bizLogic, Long participantId, Map map)
			throws Exception
	{
		// By Abhishek
		// ParticipantBizLogic bizLogic = (ParticipantBizLogic)
		// BizLogicFactory.getInstance
		// ().getBizLogic(Participant.class.getName());
		final Collection paticipantMedicalIdentifierCollection = (Collection) bizLogic
				.retrieveAttribute(Participant.class.getName(), participantId,
						"elements(participantMedicalIdentifierCollection)");
		final Iterator iter = paticipantMedicalIdentifierCollection.iterator();
		while (iter.hasNext())
		{
			final ParticipantMedicalIdentifier pmi = (ParticipantMedicalIdentifier) iter.next();
			for (int i = 1; i <= paticipantMedicalIdentifierCollection.size(); i++)
			{
				// check for null medical record number since for participant
				// having no PMI an empty PMI object is added
				if (pmi.getMedicalRecordNumber() != null
						&& pmi.getSite().getId().toString() != null)
				{
					// check for site id and medical number, if they both
					// matches then set id to the respective participant medical
					// number
					if (((String) (map.get(AppUtility.getParticipantMedicalIdentifierKeyFor(i,
							Constants.PARTICIPANT_MEDICAL_IDENTIFIER_MEDICAL_NUMBER))))
							.equalsIgnoreCase(pmi.getMedicalRecordNumber())
							&& ((String) (map.get(AppUtility.getParticipantMedicalIdentifierKeyFor(
									i, Constants.PARTICIPANT_MEDICAL_IDENTIFIER_SITE_ID))))
									.equalsIgnoreCase(pmi.getSite().getId().toString()))
					{
						map.put(AppUtility.getParticipantMedicalIdentifierKeyFor(i,
								Constants.PARTICIPANT_MEDICAL_IDENTIFIER_ID), pmi.getId()
								.toString());
						break;
					}
				}
			}
		}
	}

	/**
	 * Sets the participant collection protocol registration id.
	 *
	 * @param bizLogic : bizLogic
	 * @param participantId : participantId
	 * @param map : map
	 * @param consentResponseBeanCollection : consentResponseBeanCollection
	 * @param cprCount : cprCount
	 *
	 * @throws Exception : Exception
	 */
	private void setParticipantCollectionProtocolRegistrationId(IBizLogic bizLogic,
			Long participantId, Map map, Collection consentResponseBeanCollection, int cprCount)
			throws Exception
	{
		this.LOGGER.debug("Action ::: participant id :: " + participantId);
		// By Abhishek Mehta
		// ParticipantBizLogic bizLogic = (ParticipantBizLogic)
		// BizLogicFactory.getInstance
		// ().getBizLogic(Participant.class.getName());
		final Collection collectionProtocolRegistrationCollection = (Collection) bizLogic
				.retrieveAttribute(Participant.class.getName(), participantId,
						"elements(collectionProtocolRegistrationCollection)");
		final Iterator iter = collectionProtocolRegistrationCollection.iterator();

		while (iter.hasNext())
		{
			final CollectionProtocolRegistration cpri = (CollectionProtocolRegistration) iter
					.next();
			for (int i = 1; i <= cprCount; i++)
			{
				if (cpri.getCollectionProtocol() != null)
				{
					// Added by geeta
					// DFCI requirement : barcode should be same as identifier
					List list = null;
					String barcode = null;
					final IFactory factory = AbstractFactoryConfig.getInstance()
							.getBizLogicFactory();
					final IBizLogic bizLogic1 = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
					list = bizLogic1.retrieve(CollectionProtocolRegistration.class.getName(),
							new String[]{"barcode"}, new String[]{"id"}, new String[]{"="},
							new Long[]{cpri.getId()}, null);
					if (list != null && !list.isEmpty())
					{
						barcode = ((String) list.get(0));
					}
					final String collectionProtocolIdKey = "CollectionProtocolRegistration:" + i
							+ "_CollectionProtocol_id";
					final String collectionProtocolRegistrationIdKey = "CollectionProtocolRegistration:"
							+ i + "_id";
					final String isActive = "CollectionProtocolRegistration:" + i
							+ "_activityStatus";
					// barcodekey added by geeta
					final String barcodeKey = "CollectionProtocolRegistration:" + i + "_barcode";
					if (map.containsKey(collectionProtocolIdKey))
					{
						if (((String) map.get(collectionProtocolIdKey)).equalsIgnoreCase(cpri
								.getCollectionProtocol().getId().toString()))
						{
							map.put(collectionProtocolRegistrationIdKey, cpri.getId().toString());
							map.put(isActive, cpri.getActivityStatus());
							map.put(barcodeKey, barcode);
							// poplulate the Protocol Participant Id in map of
							// Participant Form
							map.put("CollectionProtocolRegistration:" + i
									+ "_protocolParticipantIdentifier", cpri
									.getProtocolParticipantIdentifier());
							if (consentResponseBeanCollection != null)
							{
								this.setConsentResponseId(bizLogic, cpri.getId(), cpri
										.getCollectionProtocol().getId(),
										consentResponseBeanCollection);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the consent response id.
	 *
	 * @param bizLogic : bizLogic
	 * @param cprId : cprId
	 * @param colProtId : colProtId
	 * @param consentResponseBeanCollection : consentResponseBeanCollection
	 *
	 * @throws Exception : Exception
	 */
	private void setConsentResponseId(IBizLogic bizLogic, Long cprId, Long colProtId,
			Collection consentResponseBeanCollection) throws Exception
	{
		// By Abhishek Mehta
		final Collection consentTierResponseCollection = (Collection) bizLogic.retrieveAttribute(
				CollectionProtocolRegistration.class.getName(), cprId,
				"elements(consentTierResponseCollection)");

		final Iterator itrRespBean = consentResponseBeanCollection.iterator();
		while (itrRespBean.hasNext())
		{
			final ConsentResponseBean consentResponseBean = (ConsentResponseBean) itrRespBean.next();
			final long cpId = consentResponseBean.getCollectionProtocolID();
			if (cpId == colProtId) // Searching for same collection protocol
			{

				this.LOGGER.debug("Action ::: collection protocol id :: " + colProtId);
				this.LOGGER.debug("Action ::: collection protocol registration id  :: " + cprId);
				final Iterator iter = consentTierResponseCollection.iterator();
				while (iter.hasNext())
				{
					final ConsentTierResponse consentTierResponse = (ConsentTierResponse) iter
							.next();
					if (consentTierResponse.getId() != null)
					{
						final ConsentTier consentTier = consentTierResponse.getConsentTier();
						final String consentTierId = consentTier.getId().toString();
						final Collection consentResponse = consentResponseBean.getConsentResponse();
						final Iterator itResponse = consentResponse.iterator();
						while (itResponse.hasNext())
						{
							final ConsentBean consentBean = (ConsentBean) itResponse.next();
							final String ctId = consentBean.getConsentTierID();
							if (ctId.equals(consentTierId))
							{
								this.LOGGER.debug("Action ::: consent response  :: "
										+ consentTierResponse.getResponse());
								consentBean.setParticipantResponseID(consentTierResponse.getId()
										.toString());
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the associated identified report id.
	 *
	 * @param participantBizlogic : participantBizlogic
	 * @param participantId : participantId
	 *
	 * @return Long : Long
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	private Long getAssociatedIdentifiedReportId(ParticipantBizLogic participantBizlogic,
			Long participantId) throws BizLogicException
	{
		// By Abhishek Mehta
		Long value = null;
		final List idList = participantBizlogic.getSCGList(participantId);
		if (idList != null && !idList.isEmpty())
		{
			final Object[] obj = (Object[]) idList.get(0);
			value = ((Long) obj[2]);
		}
		return value;
	}

	/**
	 * Gets the object id.
	 *
	 * @return String : String
	 */
	public String getObjectId()
	{
		return "";
	}

	/**
	 * Gets the object id.
	 *
	 * @param form : form
	 *
	 * @return String : String
	 */
	protected String getObjectId(AbstractActionForm form)
	{
		final ParticipantForm participantForm = (ParticipantForm) form;
		DAO dao = null;

		if (participantForm.getCpId() != 0L && participantForm.getCpId() != -1L)
		{
			return Constants.COLLECTION_PROTOCOL_CLASS_NAME + "_" + participantForm.getCpId();
		}

		else if (participantForm.getCpId() == -1L && participantForm.getId() != 0L)
		{
			try
			{
				dao = AppUtility.openDAOSession(null);
				final StringBuffer buffer = new StringBuffer();
				Participant participant;

				participant = (Participant) dao.retrieveById(Participant.class.getName(),
						participantForm.getId());

				final Collection<CollectionProtocolRegistration> collection = participant
						.getCollectionProtocolRegistrationCollection();

				if (collection != null && !collection.isEmpty())
				{
					buffer.append(Constants.COLLECTION_PROTOCOL_CLASS_NAME);
					for (final CollectionProtocolRegistration cpr : collection)
					{
						buffer.append('_').append(cpr.getCollectionProtocol().getId());
					}
				}

				return buffer.toString();
			}
			catch (final Exception e)
			{
				LOGGER.error(e.getMessage(), e);
				return null;
			}
			finally
			{
				try
				{
					AppUtility.closeDAOSession(dao);
				}
				catch (final ApplicationException e)
				{
					LOGGER.error(e.getMessage(), e);
				}
			}

		}
		else
		{
			return null;
		}

	}
}
