
package edu.wustl.patientLookUp.lookUpServiceBizLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.queryExecutor.IQueryExecutor;
import edu.wustl.patientLookUp.util.PatientLookupException;
import edu.wustl.patientLookUp.util.Utility;

/**
 * This class will searches the patients.
 * based on the MRN value and returns all the MRN matched patients.
 * @author geeta_jaggal
 */
public class PatientInfoByMRN
{

	final private Map<String, PatientInformation> patientDataMap = new LinkedHashMap<String, PatientInformation>();

	/**
	 * @param queryExecutor : object of query executor class.
	 * @param patientInformation : object which contains user entered patient information.
	 * @param threshold :cutoff points value.
	 * @param maxNoOfRecords :max no of records to be returned by algorithm.
	 * @return List of all the MRN matched patients.
	 * @throws PatientLookupException : PatientLookupException
	 */
	public List<PatientInformation> performMathchOnMRN(IQueryExecutor queryExecutor,
			PatientInformation patientInformation, int threshold, int maxNoOfRecords)
			throws PatientLookupException
	{
		List<PatientInformation> matchedParticipantList = new ArrayList<PatientInformation>();
		List<PatientInformation> patientListByMRN = null;
		try
		{
			Iterator<String> itr = patientInformation.getParticipantMedicalIdentifierCollection()
					.iterator();
			while (itr.hasNext())
			{
				String mrn = (String) itr.next();
				String siteId = (String) itr.next();
				if(mrn!=null){
					patientListByMRN = queryExecutor.executeQueryForMRN(mrn, siteId,patientInformation.getProtocolIdSet(),patientInformation.getPmiObjName());
					if (patientListByMRN != null && !patientListByMRN.isEmpty() )
					{
						for (int index = 0; index < patientListByMRN.size(); index++)
						{
							patientDataMap.put(String.valueOf(patientListByMRN.get(index).getId()), patientListByMRN
								.get(index));
						}
					}
					// If exact MRN match not found perform the fuzzy match on MRN.
					mrnFuzzyMatch1(mrn, siteId, queryExecutor,patientInformation.getProtocolIdSet(),patientInformation.getPmiObjName());
					mrnFuzzyMatch2(mrn, siteId, queryExecutor,patientInformation.getProtocolIdSet(),patientInformation.getPmiObjName());
				}
			}
			matchedParticipantList.addAll(patientDataMap.values());
			queryExecutor.fetchRegDateFacilityAndMRNOfPatient(matchedParticipantList);
			Utility.calculateScore(matchedParticipantList, patientInformation);
			Utility.sortListByScore(matchedParticipantList);
			matchedParticipantList = Utility.processMatchingListForFilteration(matchedParticipantList,
					threshold, maxNoOfRecords);

		}
		catch (Exception e)
		{
			throw new PatientLookupException(e.getMessage(), e);
		}
		return matchedParticipantList;
	}

	/**
	 * This method will generate the different combinations of MRN values by
	 * interchanging the adjacent digits,and fetch the matched patients for each
	 * combination of MRN value.
	 * @param mrn : user entered MRN value.
	 * @param siteId : user entered siteId value.
	 * @param queryExecutor : object of query executor class.
	 * @return List of MRN partially matched patients.
	 * @throws PatientLookupException : PatientLookupException
	 */
	private void mrnFuzzyMatch1(String mrn, String siteId, IQueryExecutor queryExecutor,Set<Long> protocolIdSet,String pmiObjName)
			throws PatientLookupException
	{
		List<PatientInformation> patientListByMRN = null;
		StringBuffer tempMRNStr = new StringBuffer();
		for (int i = 0; i < mrn.length() - 1; i++)
		{
			for (int j = 0; j < mrn.length(); j++)
			{
				if (j == i)
				{
					tempMRNStr.append(mrn.charAt(i + 1));
					j++;
					tempMRNStr.append(mrn.charAt(i));
				}
				else
				{
					tempMRNStr.append(mrn.charAt(j));
				}
			}
			patientListByMRN = queryExecutor.executeQueryForMRN(tempMRNStr.toString(), siteId,protocolIdSet,pmiObjName);
			// For removing the duplicate MRN matched records
			if (patientListByMRN != null && !patientListByMRN.isEmpty())
			{
				for (int index = 0; index < patientListByMRN.size(); index++)
				{
					//patientDataMap.put((patientListByMRN.get(index)).getUpi(), patientListByMRN.get(index));
					patientDataMap.put(String.valueOf(patientListByMRN.get(index).getId()), patientListByMRN.get(index));
				}

			}
			tempMRNStr.delete(0, tempMRNStr.length());
		}

	}

	/**
	 * This method will generate the different combinations of MRN value by
	 * incrementing and decrementing each digits,and fetch the matched
	 * patients for each combination of MRN value.
	 * @param mrn user entered MRN value.
	 * @param  user entered siteId value.
	 * @param  queryExecutor : object of query executor class.
	 * @return list of MRN partially matched patients.
	 * @throws PatientLookupException
	 */
	private void mrnFuzzyMatch2(String mrn, String siteId, IQueryExecutor queryExecutor,Set<Long> protocolIdSet,String pmiObjName)
			throws PatientLookupException
	{
		List<PatientInformation> patientListByMRN = null;
		char[] charArray = null;
		char digit = 0;
		StringBuffer tempMRNStr = new StringBuffer();
		charArray = mrn.toCharArray();
		for (int i = 0; i < mrn.length(); i++)
		{
			digit = charArray[i];
			if (digit <= '9')
			{
				charArray[i] = ++charArray[i];
				tempMRNStr.append(charArray);
				patientListByMRN = queryExecutor.executeQueryForMRN(tempMRNStr.toString(), siteId,protocolIdSet,pmiObjName);
				// For removing the duplicate MRN matched records
				if (patientListByMRN != null && !patientListByMRN.isEmpty())
				{
					for (int index = 0; index < patientListByMRN.size(); index++)
					{
						//patientDataMap.put((patientListByMRN.get(index)).getUpi(), patientListByMRN.get(index));
						patientDataMap.put(String.valueOf((patientListByMRN.get(index)).getId()), patientListByMRN.get(index));
					}
				}
			}
			charArray[i] = --charArray[i];
			tempMRNStr = tempMRNStr.delete(0, tempMRNStr.length());
			if (digit >= '0')
			{
				charArray[i] = --charArray[i];
				tempMRNStr.append(charArray);
				patientListByMRN = queryExecutor.executeQueryForMRN(tempMRNStr.toString(), siteId,protocolIdSet,pmiObjName);
				// For removing the duplicate MRN matched records
				if (patientListByMRN != null && !patientListByMRN.isEmpty())
				{
					for (int index = 0; index < patientListByMRN.size(); index++)
					{
						patientDataMap.put(((patientListByMRN.get(index)).getId()).toString(), patientListByMRN
								.get(index));
					}
				}
				charArray[i] = ++charArray[i];
			}
			tempMRNStr = tempMRNStr.delete(0, tempMRNStr.length());
		}
	}
}
