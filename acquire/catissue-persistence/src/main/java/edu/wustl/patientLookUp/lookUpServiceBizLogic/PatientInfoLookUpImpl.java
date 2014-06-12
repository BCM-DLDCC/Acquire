
package edu.wustl.patientLookUp.lookUpServiceBizLogic;

import java.util.List;

import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.queryExecutor.IQueryExecutor;
import edu.wustl.patientLookUp.util.PatientLookupException;
import edu.wustl.patientLookUp.util.Utility;

/**
 * This class is for finding all the matching patients for the given patient information.
 * It provides implementation for the searchMatchingParticipant method of IPatientLookUp interface
 * which will returns list of all the matched patients for the given patient information.
 * @author geeta_jaggal
 */
public class PatientInfoLookUpImpl implements IPatientLookUp
{

	private int haveLName = 0;
	private int haveFName = 0;
	private int haveMName = 0;
	private int ssnLength = 0;
	private String lastName = "";
	private String firstName = "";
	private String tempFirstName = "";
	private String tempMiddleName = "";
	private String middleName = "";
	private IQueryExecutor queryExecutor;

	/**
	 * public constructor.Initialises the Db configuration parameters.
	 * @throws PatientLookupException :PatientLookupException
	 */
	public PatientInfoLookUpImpl() throws PatientLookupException
	{

	}

	/* (non-Javadoc)
	 * @see edu.wustl.patientLookUp.lookUpServiceBizLogic.IPatientLookUp#searchMatchingParticipant
	 * (edu.wustl.patientLookUp.domain.PatientInformation, int, int)
	 */
	public List<PatientInformation> searchMatchingParticipant(
			PatientInformation patientInformation, int threshold, int maxNoOfRecords)
			throws PatientLookupException
	{
		PatientInfoByMRN patientInfoByMRNObj = new PatientInfoByMRN();
		PatientInfoByName patientInfoByNameObj = new PatientInfoByName();
		PatientInfoBySSN patientInfoBySSNObj = new PatientInfoBySSN();
		List<PatientInformation> matchingParticipantList = null;
		List<PatientInformation> matchedPatientsBySSN = null;
		List<PatientInformation> matchedPatientsByMRN = null;
		try
		{
			processLName(patientInformation);
			processFName(patientInformation);
			processMName(patientInformation);
			processSSN(patientInformation);

			// Perform the match on MRN value if provided by user
			if (patientInformation.getParticipantMedicalIdentifierCollection() != null
					&& patientInformation.getParticipantMedicalIdentifierCollection().size() > 0)
			{
				matchedPatientsByMRN = patientInfoByMRNObj.performMathchOnMRN(queryExecutor,
						patientInformation, threshold, maxNoOfRecords);
			}
			// perform the match on SSN if provided by user
			if (patientInformation.getSsn() != null && ssnLength == 9)
			{
				matchedPatientsBySSN = patientInfoBySSNObj.performMatchOnSSN(patientInformation,
						queryExecutor, threshold, maxNoOfRecords);
			}
			// merge both the MRN and SSN matched patient records...
			matchingParticipantList = Utility.mergeMatchedPatientLists(matchedPatientsBySSN,
					matchedPatientsByMRN);

			// No exact metching records found for  MrN and SSN value, and User has enter Lastname, search for lastname
			if (matchingParticipantList.size() == 0 && haveLName > 0)
			{
				matchingParticipantList = patientInfoByNameObj.performMatchOnName(
						patientInformation, queryExecutor, threshold, maxNoOfRecords);
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PatientLookupException(e.getMessage(), e);
		}
		return matchingParticipantList;
	}

	/**
	 * This method will process the last name.Splits the name if there any space,- or ',' characters in the name
	 * @param patientInformation :object which contains user entered patient information.
	 */
	private void processLName(PatientInformation patientInformation)
	{

		if (patientInformation.getLastName() != null
				&& patientInformation.getLastName().length() > 0)
		{
			haveLName = patientInformation.getLastName().length();
			if (haveLName > 0)
			{
				//lastName = patientInformation.getLastName().trim().toUpperCase();
				lastName = patientInformation.getLastName().trim();
				lastName = Utility.removeSuffix(lastName);
			}
		}
		patientInformation.setLastName(lastName);
	}

	/**
	 * This will process the first name.
	 * @param patientInformation : patient info
	 */
	private void processFName(PatientInformation patientInformation)
	{

		if (patientInformation.getFirstName() != null
				&& patientInformation.getFirstName().length() > 0)
		{
			haveFName = patientInformation.getFirstName().length();
			firstName = patientInformation.getFirstName().toUpperCase();
			tempFirstName = firstName;
		}
		patientInformation.setFirstName(firstName);
	}

	/**
	 * Process teh middle name.If not entered by user and there are two wprds in the first name
	 * second part of the name will be assigned to middle name.
	 * @param patientInformation : user entered patient info
	 */
	private void processMName(PatientInformation patientInformation)
	{
		if (patientInformation.getMiddleName() != null
				&& patientInformation.getMiddleName().length() > 0)
		{
			haveMName = patientInformation.getMiddleName().length();
			middleName = patientInformation.getMiddleName().toUpperCase();
			patientInformation.setMiddleName(middleName);
		}
		if (haveMName == 0 && haveFName > 0)
		{
			splitFirstName(patientInformation);
		}
	}

	/**Split teh first name if there are two words in the first name.
	 * @param patientInformation : user entered patient info
	 */
	private void splitFirstName(PatientInformation patientInformation)
	{
		String[] names = Utility.splitName2(tempFirstName);
		tempFirstName = names[0];
		if (names.length > 1)
		{
			tempMiddleName = names[1];
		}

		patientInformation.setFirstName(tempFirstName);
		firstName = tempFirstName;

		haveMName = tempMiddleName.length();
		if (haveMName > 0)
		{
			middleName = tempMiddleName;
		}
		patientInformation.setMiddleName(tempMiddleName);
	}

	/**
	 * Process the SSN check whether the entered SSN is valid or not.
	 * @param patientInformation user entered patient info
	 * @throws PatientLookupException PatientLookupException
	 */
	private void processSSN(PatientInformation patientInformation) throws PatientLookupException
	{
		int number = 0;
		if (patientInformation.getSsn() != null && patientInformation.getSsn().length() > 0)
		{
			ssnLength = patientInformation.getSsn().length();
			try
			{
				number = Integer.parseInt(patientInformation.getSsn());
				checkForSSNLength(patientInformation.getSsn().length());
			}
			catch (Exception e)
			{
				ssnLength = 0;
				throw new PatientLookupException(e.getMessage(), e);
			}
		}
	}

	/**
	 * @param ssnLength : SSN value.
	 */
	private void checkForSSNLength(int ssnLength)
	{
		if (!(ssnLength == 9 || ssnLength == 4))
		{
			ssnLength = 0;
		}
	}

	/* (non-Javadoc)
	 * @see edu.wustl.patientLookUp.lookUpServiceBizLogic.IPatientLookUp#setQueryExecutor(edu.wustl.patientLookUp.queryExecutor.IQueryExecutor)
	 */
	public void setQueryExecutor(IQueryExecutor queryExecutorObj) throws PatientLookupException
	{
		try
		{
			if (queryExecutorObj != null)
			{
				this.queryExecutor = queryExecutorObj;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PatientLookupException(e.getMessage(), e);
		}

	}

	public IQueryExecutor getQueryExecutor()
	{
		return queryExecutor;
	}

}
