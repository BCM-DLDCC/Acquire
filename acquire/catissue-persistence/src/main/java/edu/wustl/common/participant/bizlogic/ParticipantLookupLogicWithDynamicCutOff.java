package edu.wustl.common.participant.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.lookup.DefaultLookupParameters;
import edu.wustl.common.lookup.DefaultLookupResult;
import edu.wustl.common.lookup.LookupParameters;
import edu.wustl.common.participant.client.IParticipantManagerLookupLogic;
import edu.wustl.common.participant.domain.IParticipant;
import edu.wustl.common.participant.domain.IParticipantMedicalIdentifier;
import edu.wustl.common.participant.domain.ISite;
import edu.wustl.common.participant.utility.Constants;
import edu.wustl.common.participant.utility.ParticipantCache;
import edu.wustl.common.participant.utility.ParticipantManagerException;
import edu.wustl.common.participant.utility.ParticipantManagerUtility;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.lookUpServiceBizLogic.PatientInfoLookUpService;
import edu.wustl.patientLookUp.queryExecutor.SQLQueryExecutorImpl;
import edu.wustl.patientLookUp.util.PatientLookupException;
import edu.wustl.patientLookUp.util.Utility;

public class ParticipantLookupLogicWithDynamicCutOff implements IParticipantManagerLookupLogic
{

	/** The CUTOFFPOINTSFROMPROPERTIES. */
	protected static transient int CUTOFFPOINTSFROMPROPERTIES;

	/** The TOTALPOINTSFROMPROPERTIES. */
	protected static transient int TOTALPOINTSFROMPROPERTIES;

	/** The is ssn or pmi. */
	protected static transient boolean isSSNOrPMI;

	/** The exact match. */
	protected static transient boolean exactMatch;

	/** The cutoff points. */
	protected static transient int cutoffPoints;

	/** The total points. */
	protected static transient int totalPoints;

	/** The max no of participants to return. */
	protected static transient int maxNoOfParticipantsToReturn;

	/** The Constant logger. */

	private static final int pointsForSSNExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_SSN_EXACT));
	private static final int pointsForDOBExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_DOB_EXACT));
	private static final int pointsForLastNameExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_LAST_NAME_EXACT));
	private static final int pointsForFirstNameExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_FIRST_NAME_EXACT));
	private static final int pointsForMiddleNameExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_MIDDLE_NAME_EXACT));
	private static final int pointsForRaceExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_RACE_EXACT));
	private static final int pointsForGenderExact = Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_GENDER_EXACT));

	/**
	 * Instantiates a new participant lookup logic.
	 */
	public ParticipantLookupLogicWithDynamicCutOff()
	{
		isSSNOrPMI = false;
		exactMatch = true;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.lookup.LookupLogic#lookup(edu.wustl.common.lookup.LookupParameters)
	 */
	public List<DefaultLookupResult> lookup(LookupParameters params) throws PatientLookupException
	{

		if (params == null)
		{
			throw new PatientLookupException("Params can not be null", null);
		}
		else
		{
			final List<DefaultLookupResult> matchingPartisList = searchMatchingParticipant(params,
					null);
			return matchingPartisList;
		}

	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.lookup.LookupLogic#lookup(edu.wustl.common.lookup.LookupParameters)
	 */
	public List<DefaultLookupResult> lookup(LookupParameters params, Set<Long> csSet)
			throws PatientLookupException
	{
		if (params == null)
		{
			throw new PatientLookupException("Params can not be null", null);
		}
		else
		{
			final List<DefaultLookupResult> matchingPartisList = searchMatchingParticipant(params,
					csSet);
			return matchingPartisList;
		}

	}

	private PatientInformation populatePatientObject(LookupParameters params, Set<Long> protocolIdSet)
	{
		final DefaultLookupParameters participantParams = (DefaultLookupParameters) params;
		final IParticipant participant = (IParticipant) participantParams.getObject();
		final PatientInformation patientInfo = ParticipantManagerUtility
				.populatePatientObject(participant,protocolIdSet);
		CUTOFFPOINTSFROMPROPERTIES =Integer.parseInt(XMLPropertyHandler.getValue(edu.wustl.patientLookUp.util.Constants.PARTICIPANT_LOOKUP_CUTOFF));
			TOTALPOINTSFROMPROPERTIES = pointsForFirstNameExact + pointsForMiddleNameExact + pointsForLastNameExact
			+ pointsForDOBExact + pointsForSSNExact + pointsForGenderExact + pointsForRaceExact;
			totalPoints = calculateTotalPoints(participant);
		cutoffPoints = CUTOFFPOINTSFROMPROPERTIES * totalPoints / TOTALPOINTSFROMPROPERTIES;
//		cutoffPoints = Integer.parseInt(XMLPropertyHandler.getValue(Constants.PATIENT_THRESHOLD));
		maxNoOfParticipantsToReturn = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.MAX_NO_OF_PATIENS));
		return patientInfo;
	}

	/**
	 *  This function calculates the total based on values entered by user
	 * @param participant - participant object
	 * @return - cutoff points
	 */
	private int calculateTotalPoints(IParticipant participant)
	{
		int totalPointsForParticipant = 0;
		if (participant.getBirthDate() != null)
		{
			totalPointsForParticipant += pointsForDOBExact;
		}
		if (participant.getFirstName() != null && !participant.getFirstName().trim().equals(""))
		{
			totalPointsForParticipant += pointsForFirstNameExact;
		}
		if (participant.getMiddleName() != null && !participant.getMiddleName().trim().equals(""))
		{
			totalPointsForParticipant += pointsForMiddleNameExact;
		}
		if (participant.getLastName() != null && !participant.getLastName().trim().equals(""))
		{
			totalPointsForParticipant += pointsForLastNameExact;
		}
		if(participant.getSocialSecurityNumber() != null && !participant.getSocialSecurityNumber().trim().equals(""))
		{
			totalPointsForParticipant += pointsForSSNExact;
		}
		if (participant.getGender() != null && !participant.getGender().trim().equals(""))
		{
			totalPointsForParticipant += pointsForGenderExact;
		}
		if (participant.getRaceCollection() != null && participant.getRaceCollection().isEmpty() == false)
		{
			totalPointsForParticipant += pointsForRaceExact;
		}
		return totalPointsForParticipant;
	}

	/**
	 * Search matching participant.
	 *
	 * @param patientInformation the patient information
	 *
	 * @return the list
	 *
	 * @throws PatientLookupException the patient lookup exception
	 * @throws ParticipantManagerException
	 * @throws ApplicationException the application exception
	 */
	protected List<DefaultLookupResult> searchMatchingParticipant(LookupParameters params,
			Set<Long> csSet) throws PatientLookupException
	{

		final PatientInformation patientInfoInput = populatePatientObject(params, csSet);

		final List<DefaultLookupResult> matchingPartisList = new ArrayList<DefaultLookupResult>();
		final PatientInfoLookUpService patientLookupObj = new PatientInfoLookUpService();
		PatientInformation patientInfo = null;
		JDBCDAO jdbcDAO = null;
		try
		{
			Map<Long, PatientInformation>matchedParticipantList = ParticipantCache.getAllParticipants();
			List listOfParticipants = new ArrayList();
			listOfParticipants.addAll(matchedParticipantList.values());
			jdbcDAO = ParticipantManagerUtility.getJDBCDAO();
			final edu.wustl.patientLookUp.queryExecutor.IQueryExecutor queryExecutor = new SQLQueryExecutorImpl(
					jdbcDAO);
			Utility.calculateScoreForDynamicAlgo(listOfParticipants, patientInfoInput);
//			Utility.sortListByScore(listOfParticipants);
			final List patientInfoList = Utility.processMatchingListForFilteration(listOfParticipants,
					cutoffPoints, maxNoOfParticipantsToReturn);
//			final List patientInfoList = patientLookupObj.patientLookupService(patientInfoInput,
//					queryExecutor, cutoffPoints, maxNoOfParticipantsToReturn);
			if (patientInfoList != null && !patientInfoList.isEmpty())
			{
				for (int i = 0; i < patientInfoList.size(); i++)
				{
					patientInfo = (PatientInformation) patientInfoList.get(i);
					final DefaultLookupResult result = new DefaultLookupResult();
					final IParticipant partcipantNew = (IParticipant) ParticipantManagerUtility
							.getParticipantInstance();
					partcipantNew.setId(patientInfo.getId());
					partcipantNew.setLastName(patientInfo.getLastName());
					partcipantNew.setFirstName(patientInfo.getFirstName());
					partcipantNew.setMiddleName(patientInfo.getMiddleName());
					partcipantNew.setBirthDate(patientInfo.getDob());
					partcipantNew.setDeathDate(patientInfo.getDeathDate());
					partcipantNew.setVitalStatus(patientInfo.getVitalStatus());
					partcipantNew.setGender(patientInfo.getGender());
					partcipantNew.setActivityStatus(patientInfo.getActivityStatus());
					if (patientInfo.getSsn() != null && !"".equals(patientInfo.getSsn()))
					{
						final String ssn = ParticipantManagerUtility.getSSN(patientInfo.getSsn());
						partcipantNew.setSocialSecurityNumber(ssn);
					}
					final Collection participantInfoMedicalIdentifierCollection = patientInfo
							.getParticipantMedicalIdentifierCollection();
					final Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> participantMedicalIdentifierCollectionNew = new LinkedHashSet<IParticipantMedicalIdentifier<IParticipant, ISite>>();
					if (participantInfoMedicalIdentifierCollection != null
							&& participantInfoMedicalIdentifierCollection.size() > 0)
					{
						IParticipantMedicalIdentifier<IParticipant, ISite> participantMedicalIdentifier;
						for (Iterator iterator = participantInfoMedicalIdentifierCollection
								.iterator(); iterator.hasNext(); participantMedicalIdentifierCollectionNew
								.add(participantMedicalIdentifier))
						{
							final String mrn = (String) iterator.next();
							final String siteIdStr = (String) iterator.next();
							Long siteId = null;
							final String siteName = (String) iterator.next();
							final ISite site = (ISite) ParticipantManagerUtility.getSiteInstance();
							if (siteIdStr != null && !"".equals(siteIdStr))
							{
								siteId = Long.valueOf(siteIdStr);
							}
							site.setId(siteId);
							site.setName(siteName);
							participantMedicalIdentifier = (IParticipantMedicalIdentifier<IParticipant, ISite>) ParticipantManagerUtility
									.getPMIInstance();
							participantMedicalIdentifier.setMedicalRecordNumber(mrn);
							participantMedicalIdentifier.setSite(site);
						}

					}
					partcipantNew
							.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollectionNew);
					result.setObject(partcipantNew);
					result.setWeight(new Double(patientInfo.getMatchingScore()));
					matchingPartisList.add(result);
				}
			}

		}
		catch (BizLogicException e)
		{
			throw new PatientLookupException(e.getMessage(), e);
		}
		catch (DAOException daoExp)
		{
			throw new PatientLookupException(daoExp.getMsgValues(), daoExp);
		}
		catch (ParticipantManagerException e)
		{
			// TODO Auto-generated catch block
			throw new PatientLookupException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				jdbcDAO.closeSession();
			}
			catch (DAOException daoExp)
			{
				// TODO Auto-generated catch block
				throw new PatientLookupException(daoExp.getMsgValues(), daoExp);
			}
		}
		return matchingPartisList;
	}

	public void initParticipantCache() throws Exception
	{
		ParticipantCache.init();
	}

	public void updatePartiicpantCache(IParticipant participant)
	{
		ParticipantCache.updateCache(participant);
	}
}
