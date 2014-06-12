
package edu.wustl.patientLookUp.lookUpServiceBizLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.queryExecutor.IQueryExecutor;
import edu.wustl.patientLookUp.util.PatientLookupException;
import edu.wustl.patientLookUp.util.Utility;

/**
 * This class will searches the patients based on the SSN value and returns all the
 * SSN matched patients.
 * @author geeta_jaggal
 */
public class PatientInfoBySSN
{

	private Map<String, PatientInformation> patientDataMap = new LinkedHashMap<String, PatientInformation>();

	/**
	 * This method will fetch the SSN matched patients from the DB.
	 * @param queryExecutor :  object of query executor class.
	 * @param patientInfo : PatientInformation object which has the user entered patient values.
	 * @param threshold : cutoff point value.
	 * @param maxNoOfRecords :max no of records to be returned by the algorithm
	 * @return List of all SSN matched patients.
	 * @throws PatientLookupException : PatientLookupException
	 */
	public List<PatientInformation> performMatchOnSSN(final PatientInformation patientInfo,
			final IQueryExecutor queryExecutor, final int threshold, final int maxNoOfRecords)
			throws PatientLookupException
	{
		List<PatientInformation> matchedParticipantList = new ArrayList<PatientInformation>();
		List<PatientInformation> patientListBySSN = null;
		try
		{
			if (patientInfo.getSsn().length() > 0)
			{
				patientListBySSN = queryExecutor.executetQueryForSSN(patientInfo.getSsn(),
						patientInfo.getProtocolIdSet(), patientInfo.getParticipantObjName());
				if (patientListBySSN != null && patientListBySSN.size() > 0)
				{
					for (int index = 0; index < patientListBySSN.size(); index++)
					{
						patientDataMap.put(((patientListBySSN.get(index)).getId()).toString(),
								patientListBySSN.get(index));
					}
				}
				// if SSN exact SSN match not found perform the fuzzy match
				ssnFuzzyMatch1(patientInfo, queryExecutor);
				ssnFuzzyMatch2(patientInfo, queryExecutor);
				matchedParticipantList.addAll(patientDataMap.values());
				queryExecutor.fetchRegDateFacilityAndMRNOfPatient(matchedParticipantList);
				Utility.calculateScore(matchedParticipantList, patientInfo);
				Utility.sortListByScore(matchedParticipantList);
				matchedParticipantList = Utility.processMatchingListForFilteration(
						matchedParticipantList, threshold, maxNoOfRecords);
			}
		}
		catch (Exception e)
		{
			throw new PatientLookupException(e.getMessage(), e);
		}

		return matchedParticipantList;
	}

	/**
	 * This method will generate the different combinations of ssn values by
	 * interchanging the adjacent digits,and fetch the matched patients for each combination of ssn value.
	 * @param ssn : user entered ssn value
	 * @param queryExecutor : object of query executor class.
	 * @return list of ssn matched patients.
	 * @throws PatientLookupException
	 */
	private void ssnFuzzyMatch1(PatientInformation patientInfo, IQueryExecutor queryExecutor)
			throws PatientLookupException
	{
		List<PatientInformation> patientListTemp = null;
		String ssn = patientInfo.getSsn();
		StringBuffer tempssnStr = new StringBuffer();
		for (int i = 0; i < ssn.length() - 1; i++)
		{
			for (int j = 0; j < ssn.length(); j++)
			{
				if (j == i)
				{
					tempssnStr.append((ssn.charAt(i + 1)));
					j++;
					tempssnStr.append((ssn.charAt(i)));
				}
				else
				{
					tempssnStr.append((ssn.charAt(j)));
				}
			}
			patientListTemp = queryExecutor.executetQueryForSSN(tempssnStr.toString(), patientInfo
					.getProtocolIdSet(), patientInfo.getParticipantObjName());
			if (patientListTemp != null && patientListTemp.size() > 0)
			{
				for (int index = 0; index < patientListTemp.size(); index++)
				{
					//patientDataMap.put((patientListTemp.get(index)).getUpi(), patientListTemp.get(index));
					patientDataMap.put(((patientListTemp.get(index)).getId()).toString(),
							patientListTemp.get(index));
				}
			}
			tempssnStr.delete(0, tempssnStr.length());
		}

	}

	/**
	 * This method will generate the different combinations of ssn value by
	 * incrementing and decrementing each digits,and fetch the matched
	 * patients for each combination of ssn value.
	 * @param ssn : user entered ssn value
	 * @param queryExecutor : object of query executor class.
	 * @return list of ssn matched patients.
	 * @throws PatientLookupException : PatientLookupException
	 */
	private void ssnFuzzyMatch2(PatientInformation patientInfo, IQueryExecutor queryExecutor)
			throws PatientLookupException
	{
		List<PatientInformation> patientListTemp = null;
		String ssn = patientInfo.getSsn();
		char[] charArray = null;
		char digit = 0;
		StringBuffer tempssnStr = new StringBuffer();
		charArray = ssn.toCharArray();
		for (int i = 0; i < ssn.length(); i++)
		{
			digit = charArray[i];
			if (digit < '9')
			{
				charArray[i] = ++charArray[i];
				tempssnStr.append(charArray);
				patientListTemp = queryExecutor.executetQueryForSSN(tempssnStr.toString(),patientInfo.getProtocolIdSet(),patientInfo.getParticipantObjName());
				if (patientListTemp != null && patientListTemp.size() > 0)
				{
					for (int index = 0; index < patientListTemp.size(); index++)
					{
						//patientDataMap.put((patientListTemp.get(index)).getUpi(), patientListTemp.get(index));
						patientDataMap.put(((patientListTemp.get(index)).getId()).toString(),
								patientListTemp.get(index));
					}
				}
				charArray[i] = --charArray[i];
			}
			tempssnStr = tempssnStr.delete(0, tempssnStr.length());
			if (digit > '0')
			{
				charArray[i] = --charArray[i];
				tempssnStr.append(charArray);
				patientListTemp = queryExecutor.executetQueryForSSN(tempssnStr.toString(),patientInfo.getProtocolIdSet(),patientInfo.getParticipantObjName());
				if (patientListTemp != null && patientListTemp.size() > 0)
				{
					for (int index = 0; index < patientListTemp.size(); index++)
					{
						//patientDataMap.put((patientListTemp.get(index)).getUpi(), patientListTemp.get(index));
						patientDataMap.put(((patientListTemp.get(index)).getId()).toString(),
								patientListTemp.get(index));
					}
				}
				charArray[i] = ++charArray[i];
			}
			tempssnStr = tempssnStr.delete(0, tempssnStr.length());
		}
	}
}
