
package edu.wustl.patientLookUp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.language.Metaphone;

import edu.wustl.patientLookUp.domain.PatientInformation;

/**
 * This class will provide teh utility method for the patient.
 * matching algorithm
 * @author geeta_jaggal
 */
public class Utility
{

	private static String lname = "";
	private static String fname = "";
	private static String lfname = "";
	private static String flname = "";

	private static String dblname = "";
	private static String dblname1 = "";
	private static String dbfname = "";

	private static String dblfname = "";
	private static String dbflname = "";
	private static String lnamemeta = "";
	private static String db_lnamemeta = "";

	/**
	 * @param name : user entered name.
	 * @return name
	 */
	public static String removeSuffix(String name)
	{
		String nameSufRemoved = null;
		if (name.length() > 3)
		{
			String suffix = name.substring((name.length() - 3));
			if (suffix.equals(" JR") || suffix.equals(" SR") || suffix.equals(" MD"))
			{
				nameSufRemoved = name.substring(0, name.length() - 3);
				name = nameSufRemoved;
			}
		}
		return name;
	}

	/**
	 * @param name : name.
	 * @return name
	 */
	public static String[] splitName2(final String name)
	{
		String[] names = new String[2];
		names = name.split(" ", 2);
		if (!(names.length > 1))
		{
			names = name.split(",", 2);
		}
		if (!(names.length > 1))
		{
			names = name.split("-", 2);
		}

		return names;
	}

	/**
	 * this will split the name if it has space,- or , in between.
	 * @param name :name
	 * @return name
	 */
	public static String splitName(final String name)
	{
		String partname = null;
		String[] names = new String[2];
		names = name.split(" ", 2);
		if (!(names.length > 1))
		{
			names = name.split(",", 2);
		}
		else if (!(names.length > 1))
		{
			names = name.split("-", 2);
		}
		partname = names[0];
		return partname;
	}

	/**
	 * check whether the ssn numeric value or not.
	 * @param ssn : ssn
	 * @return ssn
	 */
	public static int isNumeric(final String ssn)
	{
		int number = 0;
		try
		{
			number = Integer.parseInt(ssn);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			number = 0;
		}
		return number;
	}

	/**
	 * @param dateStr :date.
	 * @return return the parsed date
	 */
	public static Date parse(final String dateStr,String pattern)
	{
		Date date = null;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			date = format.parse(dateStr);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			Logger.out.info(e.getMessage(),e);

		}
		return date;
	}

	/**
	 * This function will merges last name matched participants and SSN matched participant.
	 * @param matchedPatientsBySSN : matchedPatientsBySSN
	 * @param matchedPatientsByName : matchedPatientsBySSN
	 * @return Merged list
	 */
	public static List<PatientInformation> mergeMatchedPatientLists(
			final List<PatientInformation> matchedPatientsBySSN,
			final List<PatientInformation> matchedPatientsByMRN)
	{
		/*
		List<PatientInformation> patientList = new ArrayList<PatientInformation>();

		if (matchedPatientsByMRN != null && matchedPatientsByMRN.size() > 0)
		{
			patientList.addAll(matchedPatientsByMRN);
		}
		if (matchedPatientsBySSN != null && matchedPatientsBySSN.size() > 0)
		{
			patientList.addAll(matchedPatientsBySSN);
		}
		sortListByScore(patientList);
		return patientList;

		*/

		List<PatientInformation> patientList = new ArrayList<PatientInformation>();
		Map<String, PatientInformation> patientDataMap = new LinkedHashMap<String, PatientInformation>();
		if (matchedPatientsByMRN != null && matchedPatientsByMRN.size() > 0)
		{
			for(int i=0;i<matchedPatientsByMRN.size();i++){
				patientDataMap.put(String.valueOf(matchedPatientsByMRN.get(i).getId()), matchedPatientsByMRN.get(i));
			}

		}
		if (matchedPatientsBySSN != null && matchedPatientsBySSN.size() > 0)
		{
			for(int i=0;i<matchedPatientsBySSN.size();i++){
				patientDataMap.put(String.valueOf(matchedPatientsBySSN.get(i).getId()), matchedPatientsBySSN.get(i));
			}
		}
		patientList.addAll(patientDataMap.values());
		sortListByScore(patientList);
		return patientList;
	}

	/**
	 * This function will filter out all the participants whose.
	 * score value is less than the threshold value.
	 * @param matchingPatientList
	 * @param threshold
	 * @param max_no_of_records
	 * @return List of matched patients
	 */
	public static List<PatientInformation> processMatchingListForFilteration(
			List<PatientInformation> matchingPatientList, int threshold, int max_no_of_records)
	{
		List<PatientInformation> filteredList = new ArrayList<PatientInformation>();
		for (int i = 0; i < matchingPatientList.size(); i++)
		{
			PatientInformation dbPatientInfo = (PatientInformation) matchingPatientList.get(i);
			if (dbPatientInfo.getMatchingScore() >= threshold)
			{
				filteredList.add(dbPatientInfo);
				if (filteredList.size() >= max_no_of_records)
				{
					break;
				}
			}
		}

		return filteredList;
	}

	/**
	 * This function will sort the patients in the list based on the score value in ascending order.
	 * @param matchingPatientList :matchingPatientList
	 */
	public static void sortListByScore(List<PatientInformation> matchingPatientList)
	{
		PatientSortComparator comparatorObj = new PatientSortComparator();
		Collections.sort(matchingPatientList, comparatorObj);
	}

	/**
	 * This function will calculate the score value by matching the patient information in the list with the
	 *  user entered patient information
	 * @param matchingPatientList : matchingPatientList
	 * @param userPatientInfo  :PatientInformation object
	 */
	public static void calculateScore(List<PatientInformation> matchingPatientList,
			PatientInformation userPatientInfo)
	{
		int score = 0;
		ScoreCalculator scoreCalculator = new ScoreCalculator();
		for (int i = 0; i < matchingPatientList.size(); i++)
		{

			PatientInformation dbPatientInfo = (PatientInformation) matchingPatientList.get(i);
			score = scoreCalculator.calculateScore(userPatientInfo, dbPatientInfo);
			dbPatientInfo.setMatchingScore(score);
		}
	}

	/**
	 * This function will calculate the score value by matching the patient information in the list with the
	 *  user entered patient information
	 * @param matchingPatientList : matchingPatientList
	 * @param userPatientInfo  :PatientInformation object
	 */
	public static void calculateScoreForDynamicAlgo(List<PatientInformation> matchingPatientList,
			PatientInformation userPatientInfo)
	{
		int score = 0;
		ScoreCalculatorForOldAlgo scoreCalculator = new ScoreCalculatorForOldAlgo();
		for (int i = 0; i < matchingPatientList.size(); i++)
		{

			PatientInformation dbPatientInfo = (PatientInformation) matchingPatientList.get(i);
			score = scoreCalculator.calculateScore(userPatientInfo, dbPatientInfo);
			dbPatientInfo.setMatchingScore(score);
		}
	}

	/**
	 * @param userPatientInfo : PatientInformation object.
	 * @param lname1 : name1
	 * @param lname2 : name2
	 * @param dbPatientinfo :PatientInformation object
	 * @return boolean value
	 */
	public static boolean compareNames(PatientInformation userPatientInfo, String lname1,
			String lname2, PatientInformation dbPatientinfo)
	{
		int namelen = 0;
		boolean status = false;
		lname = "";
		fname = "";
		lfname = "";
		flname = "";
		dblname = "";
		dblname1 = "";
		dbfname = "";
		dblfname = "";
		dbflname = "";
		lnamemeta = "";
		db_lnamemeta = "";

		processNames(userPatientInfo, dbPatientinfo);
		namelen = lname.length() < dblname.length() ? lname.length() : dblname.length();

		if (compareLName(userPatientInfo.getLastName(), dblname))
		{
			status = true;
		}
		else
		{
			status = compare(namelen, lname1, lname2);
		}
		return status;
	}

	/**
	 * @param userPatientInfo : user entered PatientInformation object.
	 * @param dbPatientinfo : DB PatientInformation object
	 */
	private static void processNames(PatientInformation userPatientInfo,
			PatientInformation dbPatientinfo)
	{
		lname = userPatientInfo.getLastName();
		fname = userPatientInfo.getFirstName();
		lfname = userPatientInfo.getLastName();
		lfname = lfname.concat(userPatientInfo.getFirstName());
		flname = userPatientInfo.getFirstName();
		flname = flname.concat(userPatientInfo.getLastName());

		dblname = dbPatientinfo.getLastName();
		dblname1 = dbPatientinfo.getLastName();
		dblname1 = splitName(dblname1);

		dbfname = dbPatientinfo.getFirstName();
		dblfname = dblname;
		dblfname = dblfname.concat(dbfname);

		dbflname = dblname;
		dbflname = dbflname.concat(dbfname);

		Metaphone metaphone = new Metaphone();
		lnamemeta = metaphone.metaphone(lname);
		db_lnamemeta = metaphone.metaphone(dbPatientinfo.getLastName());
	}

	/**
	 * @param namelen :name size.
	 * @param lname1 : lname1
	 * @param lname2 : lname2
	 * @return boolean value
	 */
	private static boolean compare(int namelen, String lname1, String lname2)
	{
		boolean status = false;
		if (lname.regionMatches(true, 0, dblname, 0, namelen) || lname.compareTo(dblname1) == 0)
		{
			status = true;
		}
		else if (lname1.compareTo(dblname) == 0 || lname2.compareTo(dblname) == 0)
		{
			status = true;
		}
		else if (lnamemeta.compareTo(db_lnamemeta) == 0)
		{
			status = true;
		}
		else if (fname.compareTo(dbfname) == 0 || lfname.compareTo(dblname) == 0
				|| flname.compareTo(dblname) == 0 || fname.compareTo(dblname) == 0)
		{
			status = true;
		}
		else if (lname.compareTo(dblfname) == 0 || lname.compareTo(dbflname) == 0
				|| lname.compareTo(dbfname) == 0 || fname.compareTo(dblname) == 0
				|| lname.compareTo("DOE") == 0
				&& (fname.compareTo("JANE") == 0 || (fname.compareTo("JOHN") == 0)))
		{
			status = true;
		}
		return status;
	}

	/**
	 * @param lname  : last name.
	 * @param dblname : db last name
	 * @param dblname1 : db last name1
	 * @return boolean value
	 */
	private static boolean compareLName(String lname, String dblname, String dblname1)
	{
		boolean status = false;
		int namelen = 0;
		namelen = lname.length() < dblname.length() ? lname.length() : dblname.length();

		if (lname.regionMatches(true, 0, dblname, 0, namelen) || lname.compareTo(dblname1) == 0)
		{
			status = true;
		}
		return status;
	}

	/**
	 * @param lname  : last name.
	 * @param dblname : db last name
	 * @return boolean value
	 */
	private static boolean compareLNames12(String lname1, String lname2, String dblname)
	{
		boolean status = false;
		if (lname1.compareTo(dblname) == 0 || lname2.compareTo(dblname) == 0)
		{
			status = true;
		}
		return status;
	}

	private static boolean compareLName(String lname, String dblname)
	{
		boolean status = false;
		int namelen = 0;
		if (lname.length() < dblname.length())
		{
			namelen = lname.length();
			if (lname.regionMatches(true, 0, dblname, 0, namelen))
			{
				status = true;
			}
		}
		if (dblname.length() < lname.length())
		{
			namelen = dblname.length();
			if (dblname.regionMatches(true, 0, lname, 0, namelen))
			{
				status = true;
			}
		}
		return status;
	}



	public static String compressLastName(String lastName){
		StringBuffer lastNameCompressed=new StringBuffer();
		String[] names = new String[2];
		for(int i=0;i<lastName.length();i++){
			char c=lastName.charAt(i);
			if(!(c == ' ' || c == '\'' || c=='-' || c=='"')){
				lastNameCompressed.append(c);
			}
		}
		return lastNameCompressed.toString();
	}
}
