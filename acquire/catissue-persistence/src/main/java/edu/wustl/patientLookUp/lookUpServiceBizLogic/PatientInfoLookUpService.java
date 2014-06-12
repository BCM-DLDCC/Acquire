
package edu.wustl.patientLookUp.lookUpServiceBizLogic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.queryExecutor.IQueryExecutor;
import edu.wustl.patientLookUp.util.Logger;
import edu.wustl.patientLookUp.util.PatientLookUpFactory;
import edu.wustl.patientLookUp.util.PatientLookupException;
import edu.wustl.patientLookUp.util.PropertyHandler;

/**
 * @author geeta_jaggal
 */
public class PatientInfoLookUpService
{

	List<PatientInformation> patientMatchingList = new ArrayList<PatientInformation>();
	private int threshold = 0;
	private int maxNoOfRecords = 100;

	/**
	 * @param patientInformaton : object which has the user entered patient values.
	 * @param threshold : cutoff point value
	 * @param maxNoOfRecords : max no of records to be returned by the algorithm
	 * @return list of all matched patients
	 * @throws PatientLookupException throws PatientLookupException
	 */
	public List<PatientInformation> patientLookupService(PatientInformation patientInformaton,
			IQueryExecutor queryExecutor, int threshold, int maxNoOfRecords)
			throws PatientLookupException
	{
		try
		{
			org.apache.log4j.Logger logger = Logger.getLogger(Logger.class);
			IPatientLookUp patientLookUpObj = PatientLookUpFactory.getPatientLookupServiceImpl();
			patientLookUpObj.setQueryExecutor(queryExecutor);
			patientMatchingList = patientLookUpObj.searchMatchingParticipant(patientInformaton,
					threshold, maxNoOfRecords);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PatientLookupException(e.getMessage(), e);
		}
		return patientMatchingList;
	}

	/**
	 * @param patientInformaton : object which has the user entered patient values.
	 * @return list of matched patient
	 * @throws PatientLookupException
	 */
	public List<PatientInformation> patientLookupService(PatientInformation patientInformaton,
			IQueryExecutor queryExecutor) throws PatientLookupException
	{
		try
		{
			FileWriter writer = null;
			org.apache.log4j.Logger logger = Logger.getLogger(Logger.class);
			IPatientLookUp patientLookUpObj = PatientLookUpFactory.getPatientLookupServiceImpl();
			patientLookUpObj.setQueryExecutor(queryExecutor);
			if (PropertyHandler.getValue("max_no_of_records") != null
					&& PropertyHandler.getValue("max_no_of_records") != "")
			{
				maxNoOfRecords = Integer.parseInt(PropertyHandler.getValue("max_no_of_records"));
			}
			if (PropertyHandler.getValue("max_no_of_records") != null
					&& PropertyHandler.getValue("max_no_of_records") != "")
			{
				threshold = Integer.parseInt(PropertyHandler.getValue("threshold"));
			}
			patientMatchingList = patientLookUpObj.searchMatchingParticipant(patientInformaton,
					threshold, maxNoOfRecords);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PatientLookupException(e.getMessage(), e);
		}
		return patientMatchingList;
	}
}
