
package edu.wustl.patientLookUp.util;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.codec.language.Metaphone;

import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.patientLookUp.domain.PatientInformation;

// TODO: Auto-generated Javadoc
/**
 * This class will provides the method for calculating the matching score
 * value for each matched patients.
 *
 * @author geeta_jaggal
 */
public class ScoreCalculator
{

	/** The have lname. */
	private int haveLname;

	/** The have number. */
	private int haveNumber;

	/** The have bonus. */
	private int haveBonus;

	/** The have fname. */
	private int haveFname;

	/** The have dob. */
	private int haveDob;

	/** The have ssn. */
	private int haveSSN;

	/** The score. */
	private int score;

	/**
	 * This method will calculate the score value each patients in the matched patient list
	 * by comparing it with the user entered patient information.
	 *
	 * @param userPatientInfo : user entered PatientInformation object
	 * @param dbPatientInfo : matched patient information object
	 *
	 * @return score value
	 */
	public int calculateScore(final PatientInformation userPatientInfo,
			PatientInformation dbPatientInfo)
	{
		score = 0;
		haveLname = 0;
		haveFname = 0;
		haveBonus = 0;
		haveNumber = 0;
		haveDob = 0;
		haveSSN = 0;

		if (userPatientInfo.getParticipantMedicalIdentifierCollection() != null
				&& userPatientInfo.getParticipantMedicalIdentifierCollection().size() > 0)
		{
			Iterator itr = userPatientInfo.getParticipantMedicalIdentifierCollection().iterator();
			while (itr.hasNext())
			{
				String mrn = (String) itr.next();
				String siteId = (String) itr.next();
				String siteName = (String) itr.next();
				score = getMRNScore(mrn, siteId, dbPatientInfo);
				if (score > 0)
				{
					break;
				}
			}

		}
		if ((userPatientInfo.getLastName() != null && userPatientInfo.getLastName().length() > 0)
				&& (dbPatientInfo.getLastName() != null && dbPatientInfo.getLastName().length() > 0))
		{
			haveLname = userPatientInfo.getLastName().length();
			score += getLNameScore(userPatientInfo.getLastName().toUpperCase(), dbPatientInfo
					.getLastName().toUpperCase());
		}

		if ((userPatientInfo.getFirstName() != null && userPatientInfo.getFirstName().length() > 0)
				&& (dbPatientInfo.getFirstName() != null && dbPatientInfo.getFirstName().length() > 0))
		{
			haveFname = userPatientInfo.getFirstName().length();
			score += getFNameScore(userPatientInfo.getFirstName().toUpperCase(), dbPatientInfo
					.getFirstName().toUpperCase());
		}
		if ((userPatientInfo.getMiddleName() != null && userPatientInfo.getMiddleName().length() > 0)
				&& (dbPatientInfo.getMiddleName() != null && dbPatientInfo.getMiddleName().length() > 0))
		{
			score += getMNameScore(userPatientInfo.getMiddleName().toUpperCase(), dbPatientInfo
					.getMiddleName().toUpperCase());
		}

		if ((userPatientInfo.getSsn() != null && userPatientInfo.getSsn().length() > 0)
				&& (dbPatientInfo.getSsn() != null && dbPatientInfo.getSsn().length() > 0))
		{
			score += getSSNScore(userPatientInfo.getSsn(), dbPatientInfo.getSsn());
		}

		if ((userPatientInfo.getDob() != null && userPatientInfo.getDob().toString().length() > 0)
				&& (dbPatientInfo.getDob() != null && dbPatientInfo.getDob().toString().length() > 0))
		{
			score += getDOBScore(userPatientInfo.getDob(), dbPatientInfo.getDob());
		}

		if ((userPatientInfo.getGender() != null && userPatientInfo.getGender().length() > 0)
				&& (dbPatientInfo.getGender() != null && dbPatientInfo.getGender().length() > 0))
		{
			score += getGenderScore(userPatientInfo.getGender().toUpperCase(), dbPatientInfo
					.getGender().toUpperCase());
		}

		if ((userPatientInfo.getRaceCollection() != null && userPatientInfo.getRaceCollection()
				.size() > 0)
				&& (dbPatientInfo.getRaceCollection() != null && dbPatientInfo.getRaceCollection()
						.size() > 0))
		{
			score += getRaceScore(userPatientInfo.getRaceCollection(), dbPatientInfo
					.getRaceCollection());
		}

		if (haveBonus >= 15
				&& (haveBonus == (haveNumber - 1) || haveBonus == haveNumber)
				&& score < Integer.valueOf(XMLPropertyHandler
						.getValue(Constants.PARTICIPANT_LOOKUP_CUTOFF)))
		{
			score = Integer.valueOf(XMLPropertyHandler
					.getValue(Constants.PARTICIPANT_LOOKUP_CUTOFF));
		}
		return score;
	}

	/**
	 * Gets the race score.
	 *
	 * @param raceCollectionUser : user enterd raceCollection
	 * @param dbRaceCollection : race collection of matched patient
	 *
	 * @return score value for the race match
	 */
	private int getRaceScore(Collection raceCollectionUser, Collection dbRaceCollection)
	{
		int score = 0;
		String[] raceTypes = null;

		if (raceCollectionUser != null && raceCollectionUser.size() > 0)
		{
			raceTypes = new String[raceCollectionUser.size()];
			int idx = 0;
			Iterator iterator = raceCollectionUser.iterator();
			while (iterator.hasNext())
			{
				raceTypes[idx] = ((String) iterator.next()).toUpperCase();
				idx++;
			}
		}
		score = getRScore(raceTypes, dbRaceCollection);
		return score;
	}

	/**
	 * Gets the r score.
	 *
	 * @param raceTypes :list of race names
	 * @param dbRaceCollection : dbRaceCollection
	 *
	 * @return race matching score
	 */
	private int getRScore(String[] raceTypes, Collection dbRaceCollection)
	{
		boolean raceFound = false;
		int score = 0;
		String raceName = null;
		if (dbRaceCollection != null)
		{
			Iterator iterator = dbRaceCollection.iterator();
			while (iterator.hasNext())
			{
				raceName = (String) iterator.next();
			}
		}
		if (raceTypes != null)
		{
			for (int k = 0; k < raceTypes.length; k++)
			{
				if (raceTypes[k].charAt(0) == raceName.charAt(0))
				{
					score = Integer.valueOf(XMLPropertyHandler
							.getValue(Constants.PARTICIPANT_RACE_EXACT));
					haveBonus++;
					haveNumber++;
					break;
				}
			}
		}
		return score;
	}

	/**
	 * Gets the gender score.
	 *
	 * @param gender : user entered gender
	 * @param dbGender : matched patient gender
	 *
	 * @return return the score value for gender match
	 */
	private int getGenderScore(String gender, String dbGender)
	{
		int score = 0;
		haveNumber++;
		if (gender.toUpperCase().equals(dbGender))
		{
			score = Integer
					.valueOf(XMLPropertyHandler.getValue(Constants.PARTICIPANT_GENDER_EXACT));
			haveBonus++;
		}
		return score;
	}

	/**
	 * Gets the dob score.
	 *
	 * @param dobUser : user enterd DOB
	 * @param dobDB : matched patient DOB
	 *
	 * @return score value for the DOB match
	 */
	private int getDOBScore(Date dobUser, Date dobDB)
	{
		int score = 0;
		String dob = "";
		haveNumber += 5;
		dob = dobUser.toString();
		if (dob.compareTo(dobDB.toString()) == 0)
		{
			score += Integer.valueOf(XMLPropertyHandler.getValue(Constants.PARTICIPANT_DOB_EXACT));
			haveDob = 1;
			haveBonus += 5;
		}
		else if (haveNumber > 0 && haveFname > 0)
		{
			score += checkDateOfBirth(dobUser, dobDB);
			haveDob = 1;
		}
		return score;
	}

	/**
	 * Gets the ssn score.
	 *
	 * @param ssn : user entered SSN
	 * @param dbSSN : matched patient SSN
	 *
	 * @return score value for SSN match
	 */
	private int getSSNScore(String ssn, String dbSSN)
	{
		int score = 0;
		haveNumber++;
		if (ssn.equals(dbSSN))
		{
			score = Integer.valueOf(XMLPropertyHandler.getValue(Constants.PARTICIPANT_SSN_EXACT));
			haveSSN = 1;
			haveBonus += 5;
		}
		else
		{
			score = getSSNPartialScore(ssn, dbSSN);
			if (score == 0)
			{
				score = processSSNAndGetScore1(ssn, dbSSN);
			}
			if (score == 0)
			{
				score = processSSNAndGetScore2(ssn, dbSSN);
			}

		}
		return score;
	}

	/**
	 * Process ssn and get score1.
	 *
	 * @param ssn : user entered ssn value
	 * @param dbSSN : matched patient ssn value
	 *
	 * @return return the matching score of ssn value
	 */
	private int processSSNAndGetScore1(final String ssn, String dbSSN)
	{
		int i, j;
		int score = 0;
		StringBuffer tempssnStr = new StringBuffer();
		for (i = 0; i < ssn.length() - 1; i++)
		{
			for (j = 0; j < ssn.length(); j++)
			{
				if (j == i)
				{
					tempssnStr.append(ssn.charAt(i + 1));
					j++;
					tempssnStr.append(ssn.charAt(i));
				}
				else
				{
					tempssnStr.append(ssn.charAt(j));
				}
			}
			score = getSSNPartialScore(tempssnStr.toString(), dbSSN);
			if (score > 0)
			{
				break;
			}
			tempssnStr.delete(0, tempssnStr.length());
		}
		return score;
	}

	/**
	 * This method will.
	 *
	 * @param ssn : user entered ssn value
	 * @param dbSSN : matched patient ssn value
	 *
	 * @return return the ssn matching score
	 */
	private int processSSNAndGetScore2(final String ssn, String dbSSN)
	{
		int i, j;
		char[] charArray = null;
		char digit = 0;
		int score = 0;
		StringBuffer tempssnStr = new StringBuffer();
		charArray = ssn.toCharArray();
		for (i = 0; i < ssn.length(); i++)
		{
			digit = charArray[i];
			if (digit < '9')
			{
				charArray[i] = ++charArray[i];
				tempssnStr.append(charArray);
				charArray[i] = --charArray[i];
				score = getSSNPartialScore(tempssnStr.toString(), dbSSN);
				if (score > 0)
				{
					break;
				}
			}
			tempssnStr = tempssnStr.delete(0, tempssnStr.length());
			if (digit > '0')
			{
				charArray[i] = --charArray[i];
				tempssnStr.append(charArray);
				score = getSSNPartialScore(tempssnStr.toString(), dbSSN);
				if (score > 0)
				{
					break;
				}
				charArray[i] = ++charArray[i];
			}
			tempssnStr = tempssnStr.delete(0, tempssnStr.length());
		}
		return score;
	}

	/**
	 * Gets the sSN partial score.
	 *
	 * @param tempssnStr the tempssn str
	 * @param dbSSN the db ssn
	 *
	 * @return the sSN partial score
	 */
	private int getSSNPartialScore(String tempssnStr, String dbSSN)
	{
		int score = 0;
		if (tempssnStr.length() == 4)
		{
			if (tempssnStr.regionMatches(true, 0, dbSSN, 5, tempssnStr.length()))
			{
				score = Integer.valueOf(XMLPropertyHandler
						.getValue(Constants.PARTICIPANT_SSN_PARTIAL));
				haveSSN = 1;
				haveBonus++;
			}
		}
		else
		{
			if (tempssnStr.equals(dbSSN))
			{
				score = Integer.valueOf(XMLPropertyHandler
						.getValue(Constants.PARTICIPANT_SSN_PARTIAL));
				haveSSN = 1;
				haveBonus++;
			}
		}
		return score;
	}

	/**
	 * Gets the mRN score.
	 *
	 * @param mrn the mrn
	 * @param siteId the site id
	 * @param dbPatientInfo the db patient info
	 *
	 * @return the mRN score
	 */
	private int getMRNScore(String mrn, String siteId, PatientInformation dbPatientInfo)
	{
		int score = 0;
		if (dbPatientInfo.getParticipantMedicalIdentifierCollection() != null
				&& dbPatientInfo.getParticipantMedicalIdentifierCollection().size() > 0)
		{
			Iterator<String> itr = dbPatientInfo.getParticipantMedicalIdentifierCollection()
					.iterator();
			while (itr.hasNext())
			{
				String dbMRN = (String) itr.next();
				String dbSiteId = (String) itr.next();
				String dbSiteName = (String) itr.next();
				// Only MRN is considered because eMPI patients hv no siteID.
				if (mrn.equals(dbMRN))
				{
					score = Integer.valueOf(XMLPropertyHandler
							.getValue(Constants.PARTICIPANT_PMI_EXACT));
				}
				else
				{
					score = getMRNPartialScore(mrn, dbMRN);
					if (score == 0)
					{
						score = processMRNAndGetScore1(mrn, dbMRN);
					}
					if (score == 0)
					{
						score = processMRNAndGetScore2(mrn, dbMRN);
					}

				}
			}
		}
		return score;
	}

	/**
	 * Process mrn and get score1.
	 *
	 * @param mrn the mrn
	 * @param dbMRN the db mrn
	 *
	 * @return the int
	 */
	private int processMRNAndGetScore1(String mrn, String dbMRN)
	{
		int i, j;
		int score = 0;
		StringBuffer tempssnStr = new StringBuffer();
		for (i = 0; i < mrn.length() - 1; i++)
		{
			for (j = 0; j < mrn.length(); j++)
			{
				if (j == i)
				{
					tempssnStr.append(mrn.charAt(i + 1));
					j++;
					tempssnStr.append(mrn.charAt(i));
				}
				else
				{
					tempssnStr.append(mrn.charAt(j));
				}
			}
			score = getMRNPartialScore(tempssnStr.toString(), dbMRN);
			if (score > 0)
			{
				break;
			}
			tempssnStr.delete(0, tempssnStr.length());
		}
		return score;
	}

	/**
	 * Process mrn and get score2.
	 *
	 * @param mrn the mrn
	 * @param dbMRN the db mrn
	 *
	 * @return the int
	 */
	private int processMRNAndGetScore2(String mrn, String dbMRN)
	{
		int i, j;
		char[] charArray = null;
		char digit = 0;
		int score = 0;
		StringBuffer tempssnStr = new StringBuffer();
		charArray = mrn.toCharArray();
		for (i = 0; i < mrn.length(); i++)
		{
			digit = charArray[i];
			if (digit < '9')
			{
				charArray[i] = ++charArray[i];
				tempssnStr.append(charArray);
				charArray[i] = --charArray[i];
				score = getMRNPartialScore(tempssnStr.toString(), dbMRN);
				if (score > 0)
				{
					break;
				}
			}
			tempssnStr = tempssnStr.delete(0, tempssnStr.length());
			if (digit > '0')
			{
				charArray[i] = --charArray[i];
				tempssnStr.append(charArray);
				score = getMRNPartialScore(tempssnStr.toString(), dbMRN);
				if (score > 0)
				{
					break;
				}
				charArray[i] = ++charArray[i];
			}
			tempssnStr = tempssnStr.delete(0, tempssnStr.length());
		}
		return score;
	}

	/**
	 * Gets the mRN partial score.
	 *
	 * @param tempMRNStr the temp mrn str
	 * @param dbMRN the db mrn
	 *
	 * @return the mRN partial score
	 */
	private int getMRNPartialScore(String tempMRNStr, String dbMRN)
	{
		int score = 0;
		if (tempMRNStr.equals(dbMRN))
		{
			score = Integer.valueOf(XMLPropertyHandler.getValue(Constants.PARTICIPANT_PMI_PARTIAL));
			haveSSN = 1;
			haveBonus++;
		}
		return score;
	}

	/**
	 * Gets the m name score.
	 *
	 * @param mName the m name
	 * @param dbLName the db l name
	 *
	 * @return the m name score
	 */
	private int getMNameScore(String mName, String dbLName)
	{
		int score = 0;
		haveNumber++;
		if (mName.equals(dbLName))
		{
			score = Integer.valueOf(XMLPropertyHandler
					.getValue(Constants.PARTICIPANT_MIDDLE_NAME_EXACT));
			haveBonus++;
		}
		else
		{
			score = getMNamePartialScore(mName, dbLName);
		}
		return score;
	}

	/**
	 * Gets the m name partial score.
	 *
	 * @param mName the m name
	 * @param dbLName the db l name
	 *
	 * @return the m name partial score
	 */
	private int getMNamePartialScore(String mName, String dbLName)
	{
		int score = 0;
		if (((mName.length() == 1 || dbLName.length() == 1) && (mName.charAt(0) == dbLName
				.charAt(0)))
				|| dbLName.length() == 0)
		{
			score = Integer.valueOf(XMLPropertyHandler
					.getValue(Constants.PARTICIPANT_MIDDLE_NAME_PARTIAL));
			haveBonus++;
		}
		return score;
	}

	/**
	 * Gets the f name score.
	 *
	 * @param fname the fname
	 * @param dbLName the db l name
	 *
	 * @return the f name score
	 */
	private int getFNameScore(String fname, String dbLName)
	{
		int score = 0;
		haveNumber += 5;
		if (fname.equals(dbLName))
		{
			score = Integer.valueOf(XMLPropertyHandler
					.getValue(Constants.PARTICIPANT_FIRST_NAME_EXACT));
			haveFname = 1;
			haveBonus += 5;
		}
		else
		{
			if (fname.charAt(0) == dbLName.charAt(0))
			{
				score = Integer.valueOf(XMLPropertyHandler
						.getValue(Constants.PARTICIPANT_FIRST_NAME_PARTIAL));
				haveFname = 1;
			}
		}
		return score;
	}

	/**
	 * Gets the l name score.
	 *
	 * @param lname the lname
	 * @param dbLName the db l name
	 *
	 * @return the l name score
	 */
	private int getLNameScore(String lname, String dbLName)
	{
		int score = 0;
		int lnamelen = 0;
		int lnamelendiff = 0;
		String lnamemeta = "";
		String db_lnamemeta = "";

		// Get the metaphone value of each  and compare
		Metaphone metaphone = new Metaphone();
		lnamemeta = metaphone.metaphone(lname);
		db_lnamemeta = metaphone.metaphone(dbLName);
		if (lname.length() < dbLName.length())
		{
			lnamelen = lname.length() - 1;
			lnamelendiff = dbLName.length() - lname.length();
		}
		else
		{
			lnamelen = dbLName.length() - 1;
			lnamelendiff = lname.length() - dbLName.length();
		}
		haveNumber += 5;
		if (lname.compareTo(dbLName) == 0
				|| (lnamelendiff == 1 && (lname.regionMatches(true, 0, dbLName, 0, lnamelen))))
		{
			score = Integer.valueOf(XMLPropertyHandler
					.getValue(Constants.PARTICIPANT_LAST_NAME_EXACT));
			haveLname = 1;
			haveBonus += 5;
		}
		else if (lnamemeta.compareTo(db_lnamemeta) == 0)
		{
			score = Integer.valueOf(XMLPropertyHandler
					.getValue(Constants.PARTICIPANT_LAST_NAME_PARTIAL));
			haveLname = 1;
		}
		return score;
	}

	/**
	 * Check date of birth.
	 *
	 * @param userBirthDate the user birth date
	 * @param dbPatientBirthDate the db patient birth date
	 *
	 * @return the int
	 */
	private int checkDateOfBirth(Date userBirthDate, Date dbPatientBirthDate)
	{
		int score = 0;

		if (compareMonthYear(userBirthDate, dbPatientBirthDate))
		{
			score = Integer.valueOf(XMLPropertyHandler.getValue(Constants.PARTICIPANT_DOB_PARTIAL));
		}
		else if (compareDateMonthYear(userBirthDate, dbPatientBirthDate))
		{
			score = Integer.valueOf(XMLPropertyHandler.getValue(Constants.PARTICIPANT_DOB_PARTIAL));
		}
		/*else if (compareDateYear(userBirthDate, dbPatientBirthDate))
		{
			score = Constants.MPI_DOB_P;
		}*/
		return score;
	}

	/**
	 * Compare month year.
	 *
	 * @param userBirthDate the user birth date
	 * @param dbPatientBirthDate the db patient birth date
	 *
	 * @return true, if successful
	 */
	private boolean compareMonthYear(Date userBirthDate, Date dbPatientBirthDate)
	{
		boolean flag = false;
		if (userBirthDate.getMonth() == dbPatientBirthDate.getMonth()
				&& userBirthDate.getYear() == dbPatientBirthDate.getYear())
		{

			flag = true;
		}
		return flag;
	}

	/**
	 * Compare date month year.
	 *
	 * @param userBirthDate the user birth date
	 * @param dbPatientBirthDate the db patient birth date
	 *
	 * @return true, if successful
	 */
	private boolean compareDateMonthYear(Date userBirthDate, Date dbPatientBirthDate)
	{
		boolean flag = false;
		if ((userBirthDate.getDate() == dbPatientBirthDate.getDate() && userBirthDate.getMonth() == dbPatientBirthDate
				.getMonth())
				&& (userBirthDate.getYear() >= (Math.abs(dbPatientBirthDate.getYear() - 2)) && userBirthDate
						.getYear() <= (Math.abs(dbPatientBirthDate.getYear() + 2))))
		{
			flag = true;
		}

		return flag;
	}

	/**
	 * Compare date year.
	 *
	 * @param userBirthDate the user birth date
	 * @param dbPatientBirthDate the db patient birth date
	 *
	 * @return true, if successful
	 */
	private boolean compareDateYear(Date userBirthDate, Date dbPatientBirthDate)
	{
		boolean flag = false;
		if ((userBirthDate.getDate() == dbPatientBirthDate.getDate() && userBirthDate.getYear() == dbPatientBirthDate
				.getYear()))
		{

			flag = true;
		}
		return flag;
	}

}
