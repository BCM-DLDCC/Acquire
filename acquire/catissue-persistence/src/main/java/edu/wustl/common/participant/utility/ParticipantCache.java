package edu.wustl.common.participant.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.participant.domain.IParticipant;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.util.PatientLookupException;

// TODO: Auto-generated Javadoc
/**
 * The Class ParticipantCache.
 */
public final class ParticipantCache
{

 	/**
 	 * Private default constructor to avoid instantiation.
 	 */
    private ParticipantCache()
    {
        super();
    }

    /** The list of participants. */
    private static Map<Long,PatientInformation> listOfParticipants = new HashMap<Long,PatientInformation>();

    /**
     * Gets the all participants.
     *
     * @return the all participants
     */
    public static Map<Long,PatientInformation> getAllParticipants()
    {
    	return listOfParticipants;
    }

    /**
     * Inits the.
     *
     * @throws BizLogicException the biz logic exception
     */
    public static void init() throws BizLogicException
    {

    	listOfParticipants =  retrieveAllParticipants();
    }

    /**
     * Retrieve all participants.
     *
     * @return the map< long, patient information>
     *
     * @throws BizLogicException the biz logic exception
     */
    private static Map<Long,PatientInformation> retrieveAllParticipants() throws BizLogicException
	{
    	DAO hibernateDao = null;
		Map<Long,PatientInformation> mapOfParticipants = new HashMap<Long,PatientInformation>();
		String participantQueryStr = "from edu.wustl.catissuecore.domain.Participant where activityStatus !='Disabled'";
		List<PatientInformation> patientInformationList = new ArrayList<PatientInformation>();
		try
		{
			hibernateDao = ParticipantManagerUtility.getDAO();
			List<IParticipant> listOfParticipants = hibernateDao.executeQuery(participantQueryStr);
			if(listOfParticipants != null)
			{
				patientInformationList = ParticipantManagerUtility.populatePatientInfo(listOfParticipants);
				Iterator<PatientInformation> participantIterator = patientInformationList.iterator();
				while(participantIterator.hasNext())
				{
					PatientInformation patientInformation = (PatientInformation)participantIterator.next();
					Long participantId = patientInformation.getId();
//				PatientInformation patientInformation =	ParticipantManagerUtility.populatePatientObject(participant, null);
//				patientInformation.setId(participant.getId());
					mapOfParticipants.put(participantId, patientInformation);
				}
			}
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (PatientLookupException e)
		{
			throw new BizLogicException(null,e,e.getMessage());
		}
		finally
		{
			try
			{
				hibernateDao.closeSession();
			}
			catch (DAOException e)
			{
				throw new BizLogicException(e);
			}
		}
		return mapOfParticipants;
	}

    /**
     * Update cache.
     *
     * @param participant the participant
     */
    public static void updateCache(IParticipant participant)
    {
    	if(!"Active".equals(participant.getActivityStatus()))
    	{
    		listOfParticipants.remove(participant.getId());
    	}
    	else
    	{
    		PatientInformation patientInformation = ParticipantManagerUtility.populatePatientObject(participant, null);
    		listOfParticipants.put(participant.getId(), patientInformation);
    	}
    }
}
