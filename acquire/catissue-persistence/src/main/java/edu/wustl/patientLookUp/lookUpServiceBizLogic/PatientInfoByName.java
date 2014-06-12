
package edu.wustl.patientLookUp.lookUpServiceBizLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.language.Metaphone;

import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.queryExecutor.IQueryExecutor;
import edu.wustl.patientLookUp.util.PatientLookupException;
import edu.wustl.patientLookUp.util.Utility;

/**
 * This class will searches the patients based on the LastName  value
 * and returns all the lastName matched patients.
 * @author geeta_jaggal
 */
public class PatientInfoByName
{

	private Map<String, PatientInformation> patientDataMap = new LinkedHashMap<String, PatientInformation>();

	/**
	 * This method will perform the patient match on lastName and also perform the
	 * phonetic match on lastName and returns the lastName matched patients.
	 * @param patientInformation : object which contains user entered patient information.
	 * @param queryExecutor : object of query executor class.
	 * @param threshold :cutoff points value.
	 * @param maxNoOfRecords :max no of records to be returned by algorithm.
	 * @return List of all lastName matched patients.
	 * @throws PatientLookupException
	 */
	public List<PatientInformation> performMatchOnName(PatientInformation patientInformation,
			IQueryExecutor queryExecutor, int threshold, int maxNoOfRecords)
			throws PatientLookupException
	{

		List<PatientInformation> matchedPatientsByName = null;
		List<PatientInformation> matchedPatientsByMetaPhone = null;
		List<PatientInformation> matchedParticipantList = new ArrayList<PatientInformation>();
		try
		{
			String lastName = Utility.compressLastName(patientInformation.getLastName());
			matchedPatientsByName = queryExecutor.executeQueryForName(lastName, patientInformation
					.getProtocolIdSet(), patientInformation.getParticipantObjName());
			// get meta phone value
			Metaphone metaPhoneObj = new Metaphone();
			String metaPhone = metaPhoneObj.metaphone(patientInformation.getLastName());
			// Search for metaPhoen matched records
			if (metaPhone != null && metaPhone != "")
			{
				matchedPatientsByMetaPhone = queryExecutor.executetQueryForPhonetic(metaPhone,
						patientInformation.getProtocolIdSet(), patientInformation
								.getParticipantObjName());
				matchedPatientsByName.addAll(matchedPatientsByMetaPhone);
			}
			Utility.calculateScore(matchedPatientsByName, patientInformation);
			Utility.sortListByScore(matchedPatientsByName);
			matchedPatientsByName = Utility.processMatchingListForFilteration(
					matchedPatientsByName, threshold, maxNoOfRecords);
			if (matchedPatientsByName != null && matchedPatientsByName.size() > 0)
			{
				for (int index = 0; index < matchedPatientsByName.size(); index++)
				{
					patientDataMap.put(String.valueOf((matchedPatientsByName.get(index)).getId()),
							matchedPatientsByName.get(index));
				}
			}
			matchedParticipantList.addAll(patientDataMap.values());
			queryExecutor.fetchRegDateFacilityAndMRNOfPatient(matchedPatientsByName);
			return matchedParticipantList;
		}
		catch (Exception e)
		{
			throw new PatientLookupException(e.getMessage(), e);
		}
	}
}
