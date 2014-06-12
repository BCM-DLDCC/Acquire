
package edu.wustl.patientLookUp.queryExecutor;

import edu.wustl.patientLookUp.util.PatientLookupException;

/**
 * This class will provide the queries to fetch the patients based on MRN,SSN and
 * lastName match.
 * @author geeta_jaggal
 */
public class QueryGenerator
{

	private static String dbSchema = null;

	/**
	 * Constructor.
	 */
	public QueryGenerator()
	{

	}

	/**
	 * @param schema :database schema name.
	 * @throws PatientLookupException : PatientLookupException
	 */
	public static void setDBSchema(String schema) throws PatientLookupException
	{
		try
		{
			dbSchema = schema;
		}
		catch (Exception e)
		{
			throw new PatientLookupException(e.getMessage(), e);
		}
	}

	/**
	 * @return query for MRN match
	 */
	public static String getMRNQuery()
	{
		String query = null;
		query = "with all_persons(upi,lastname,firstname,middlename,dob,ssn,gender,demo) as ( "
				+ " select d1.upi,dx1.lastname,dx1.firstname,dx1.middlename,"
				+ " date(dx1.dateofbirth) as "
				+ " dob,dx1.ssn,dx1.gender,dx1.demo"
				+ " from "
				+ dbSchema
				+ ".demographics d1, xmltable('$d1/Person/demographicsCollection/demographics'"
				+ " passing d1.\"XMLDATA\" as \"d1\""
				+ " columns"
				+ " firstName varchar(255)  path 'personName/firstName',"
				+ " lastName varchar(255)   path 'personName/lastName',"
				+ " middleName varchar(255) path 'personName/middleName',"
				+ " ssn varchar(12)        path 'socialSecurityNumber',"
				+ " dateOfBirth timestamp  path 'dateOfBirth',"
				+ " gender bigint          path 'gender/id', "
				+ "	demo xml               path '.') dx1"
				+ "	where upi in ("
				+ "  	   select e1.upi from "
				+ dbSchema
				+ ".ENCOUNTERS e1, xmltable("
				+ "       '$e1/Encounter[medicalRecordNumber/value = $mrn]'"
				+ "    	passing e1.\"XMLDATA\" as \"e1\","
				+ "       cast (? as varchar(255)) as \"mrn\")"
				+ "	where e1.facility in "
				+" (5572,2574,4674,3049,5107,6729,2572,3148,3269,160559,6116)"
				+ "	and current timestamp between e1.start_ts and e1.end_ts)"
				+ "   and current timestamp between d1.start_ts and d1.end_ts)"
				+ " select ap.upi,ap.lastname,ap.firstname,ap.middlename,ap.dob,ap.ssn,"
				+ " ap.gender,dx1.race as raceID,"
				+ " dx1.address,dx1.city,dx1.state as stateid,dx1.zip"
				+ " from all_persons ap, xmltable('for $race in "
				+ " $demo/(raceCollection/race,.[not(raceCollection/race)]/<nothing/>),"
				+ " $addr in $demo/(addressCollection/address,."
				+ "[not(addressCollection/address)]/<nothing/>)"
				+ " let $ad := $addr[type/id = 5280]"
				+ " return <return><r>{$race}</r><ad>{$ad}</ad></return>'"
				+ " passing ap.demo as \"demo\""
				+ " columns race    bigint      path 'r/race/id',"
				+ " address varchar(255) path 'ad/address/line1',"
				+ " city    varchar(50) path 'ad/address/city',"
				+ " state   bigint      path 'ad/address/state/id',"
				+ " zip     varchar(20) path 'ad/address/postalCode') dx1";
		return query;
	}

	/**
	 * @return return query for SSN match
	 */
	public static String getSSNQuery()
	{
		String queryBySSN = "with all_persons(upi,lastname,firstname,middlename,dob,ssn,gender,demo) as ("
				+ "select d1.upi,dx1.lastname,dx1.firstname,dx1.middlename,date(dx1.dateofbirth) as "
	  		    + "dob,dx1.ssn,dx1.gender,dx1.demo"
				+ " from "
				+ dbSchema
				+ ".demographics d1, "
				+ "xmltable('$d1/Person/demographicsCollection/demographics[socialSecurityNumber = $pssn]'"
				+ "  passing d1.\"XMLDATA\" as \"d1\","
				+ "  cast (? as varchar(9)) as \"pssn\""
				+ "  columns "
				+ "  firstName varchar(255)  path 'personName/firstName',"
				+ "  lastName varchar(255)   path 'personName/lastName', "
				+ "  middleName varchar(255) path 'personName/middleName',"
				+ "  ssn varchar(12)        path 'socialSecurityNumber',"
				+ "  dateOfBirth timestamp  path 'dateOfBirth',"
				+ " gender bigint          path 'gender/id',"
				+ "  demo xml               path '.') dx1 "
				+ " where current timestamp between d1.start_ts and d1.end_ts)"
				+ " select ap.upi,ap.lastname,ap.firstname,ap.middlename,ap.dob,"
				+ " ap.ssn,ap.gender,dx1.race as raceID,"
				+ " dx1.address,dx1.city,dx1.state as stateid,dx1.zip"
				+ " from all_persons ap, xmltable('for $race in "
				+ " $demo/(raceCollection/race,.[not(raceCollection/race)]/<nothing/>),"
				+ " $addr in $demo/(addressCollection/address,"
				+ ".[not(addressCollection/address)]/<nothing/>)"
				+ " let $ad := $addr[type/id = 5280]"
				+ " return <return><r>{$race}</r><ad>{$ad}</ad></return>'"
				+ " passing ap.demo as \"demo\""
				+ " columns race    bigint      path 'r/race/id',"
				+ " address varchar(255) path 'ad/address/line1',"
				+ " city    varchar(50) path 'ad/address/city',"
				+ " state   bigint      path 'ad/address/state/id',"
				+ " zip     varchar(20) path 'ad/address/postalCode')dx1";

		return queryBySSN;
	}

	/**
	 * @return return the query for lastName match.
	 */
	public static String getNameQuery()
	{
		String queryByName = "with all_persons(upi,lastname,firstname,middlename,dob,ssn,gender,demo) as ("
				+ " select d1.upi,dx1.lastname,dx1.firstname,dx1.middlename,date(dx1.dateofbirth) "
				+ " as dob,dx1.ssn,dx1.gender,dx1.demo"
				+ " from "
				+ dbSchema
				+ ".demographics d1,"
				+ " xmltable('$d1/Person/demographicsCollection/demographics[personName/lastNameCompressed"
				+ "[.>= $plname and .< $plnamemax]]'"
				+ " passing d1.\"XMLDATA\" as \"d1\", "
				+ " cast (? as varchar(255)) as \"plname\","
				+ " cast (? as varchar(255)) as \"plnamemax\""
				+ " columns "
				+ " firstName varchar(255)  path 'personName/firstName',"
				+ " lastName varchar(255)   path 'personName/lastName',"
				+ " middleName varchar(255) path 'personName/middleName',"
				+ " ssn varchar(12)        path 'socialSecurityNumber',"
				+ " dateOfBirth timestamp  path 'dateOfBirth',"
				+ " gender bigint          path 'gender/id',"
				+ " demo xml               path '.') dx1"
				+ " where current timestamp between d1.start_ts and d1.end_ts)"
				+ " select ap.upi,ap.lastname,ap.firstname,ap.middlename,ap.dob,"
				+ " ap.ssn,ap.gender,dx1.race as raceID,"
				+ " dx1.address,dx1.city,dx1.state as stateid,dx1.zip"
				+ " from all_persons ap, xmltable('for $race in "
				+ " $demo/(raceCollection/race,.[not(raceCollection/race)]/<nothing/>),"
				+ " $addr in $demo/(addressCollection/address,.[not(addressCollection/address)]/<nothing/>)"
				+ " let $ad := $addr[type/id = 5280] "
				+ " return <return><r>{$race}</r><ad>{$ad}</ad></return>'"
				+ " passing ap.demo as \"demo\""
				+ " columns race    bigint      path 'r/race/id', "
				+ " address varchar(255) path 'ad/address/line1', "
				+ " city    varchar(50) path 'ad/address/city', "
				+ " state   bigint      path 'ad/address/state/id', "
				+ " zip     varchar(20) path 'ad/address/postalCode') dx1";
		return queryByName;
	}

	/**
	 * @return return the query for phonetic match.
	 */
	public static String getMetaPhoneQuery()
	{
		String query = "with all_persons(upi,lastname,firstname,middlename,dob,ssn,gender,demo) as ( "
				+ " select d1.upi,dx1.lastname,dx1.firstname,dx1.middlename,date(dx1.dateofbirth) "
				+ " as dob,dx1.ssn,dx1.gender,dx1.demo"
				+ " from "
				+ dbSchema
				+ ".demographics d1, xmltable('$d1/Person/demographicsCollection/demographics"
				+ "[personName/lastNameMetaphone = $plnamemp]'"
				+ " passing d1.\"XMLDATA\" as \"d1\","
				+ " cast (? as varchar(20)) as \"plnamemp\""
				+ " columns firstName varchar(255)  path 'personName/firstName',"
				+ " lastName varchar(255)   path 'personName/lastName',"
				+ " middleName varchar(255) path 'personName/middleName',"
				+ " ssn varchar(12)        path 'socialSecurityNumber',"
				+ " dateOfBirth timestamp  path 'dateOfBirth',"
				+ " gender bigint          path 'gender/id',"
				+ " demo xml               path '.') dx1"
				+ " where current timestamp between d1.start_ts and d1.end_ts)"
				+ " select ap.upi,ap.lastname,ap.firstname,ap.middlename,ap.dob,ap.ssn,"
				+ " ap.gender,dx1.race as raceID,"
				+ " dx1.address,dx1.city,dx1.state as stateid,dx1.zip"
				+ " from all_persons ap, xmltable('for $race in "
				+ " $demo/(raceCollection/race,.[not(raceCollection/race)]/<nothing/>),"
				+ " $addr in $demo/(addressCollection/address,"
				+ ".[not(addressCollection/address)]/<nothing/>)"
				+ " let $ad := $addr[type/id = 5280]"
				+ " return <return><r>{$race}</r><ad>{$ad}</ad></return>'"
				+ " passing ap.demo as \"demo\""
				+ " columns race    bigint      path 'r/race/id',"
				+ " address varchar(255) path 'ad/address/line1',"
				+ " city    varchar(100) path 'ad/address/city',"
				+ " state   bigint      path 'ad/address/state/id',"
				+ " zip     varchar(20) path 'ad/address/postalCode') dx1";

		return query;
	}

	/**
	 * @return return the query for fetching mrn,facility Visited,date visited.
	 */
	public static String getQuery()
	{

	/*
		String query = "select date(ex1.regts) as regDate, e1.facility,ex1.mrn"
				+ "  from "
				+ dbSchema
				+ ".encounters e1, "
				+ "xmltable('$e/Encounter/encounterDetailsCollection/encounterDetails/registrationTimeStamp'"
				+ " passing e1.\"XMLDATA\" as \"e\""
				+ " columns regts timestamp path '.',"
				+ " mrn varchar(50) path '../../../medicalRecordNumber/value') ex1"
				+ " where e1.upi = ? and e1.facility in "
				+ "(5572,2574,4674,3049,5107,6729,2572,3148,3269,160559,6116)"
				+ " and current timestamp between e1.start_ts and e1.end_ts"
				+ " and regts = (select max(regts) from "
				+ dbSchema
				+ ".encounters e1, "
				+ "xmltable('$e/Encounter/encounterDetailsCollection/encounterDetails/registrationTimeStamp'"
				+ " passing e1.\"XMLDATA\" as \"e\"  columns regts timestamp path '.') ex1 "
				+ " where e1.upi = ? and e1.facility in "
				+ "(5572,2574,4674,3049,5107,6729,2572,3148,3269,160559,6116)"
				+ " and current timestamp between e1.start_ts and e1.end_ts )";

		*/


		/*

		String query = "select  ex1.mrn, e1.facility from "
				+ dbSchema+ ".encounters e1, xmltable('$e/Encounter'" +
				" passing e1.\"XMLDATA\" as \"e\"" +
				" columns mrn varchar(20) path 'medicalRecordNumber/value'," +
				" facility varchar(25) path 'facility/id') ex1" +
				" where e1.upi = ?" +
				" and e1.facility in (5572,2574,4674,3049,5107,6729,2572,3148,3269,160559,6116)" +
				" and current timestamp between e1.start_ts and e1.end_ts" ;

		*/

		String query="select distinct ex1.mrn,fac.facility_id,fac.print_name from "
			 		+ dbSchema + ".encounters e1,"
			 		+ dbSchema + ".facility fac, xmltable('$e/Encounter'" +
			 		" passing e1.\"XMLDATA\" as \"e\" " +
			 		" columns mrn varchar(255) path 'medicalRecordNumber/value'," +
			 		" facility varchar(255) path 'facility/id') ex1 " +
			 		" where e1.upi = ? " +
			 		" and e1.facility in (5572,2574,4674,3049,5107,6729,2572,3148,3269,160559,6116)" +
			 		" and current timestamp between e1.start_ts and e1.end_ts " +
			 		" and e1.facility = fac.facility_conceptid";

		return query;
	}

}
