
package edu.wustl.patientLookUp.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * @author geeta_jaggal
 * Domain object for Irb Project related information.
 * It represents the information that needs to be persisted in the DB
 */

/**
 * @author geeta_jaggal
 *
 */
public class PatientInformation
{

	/** unique id of the patient. */
	private Long id;

	/** patient UPI. */
	private String upi;

	/** Patient first name. */
	private String firstName="";

	/** Patient last name. */
	private String lastName;

	/** Patient middle name. */
	private String middleName="";

	/** gender. */
	private String gender="";

	/** Patient SSN. */
	private String ssn="";

	/** Patient DOB. */
	private Date dob;

	/** Patient address. */
	private Address address;

	/** Patient facility visited. */
	private String facilityVisited;

	/** facility id. */
	private String facilityId;


	protected String participantObjName;

	public String getParticipantObjName()
	{
		return participantObjName;
	}

	public void setParticipantObjName(String participantObjName)
	{
		this.participantObjName = participantObjName;
	}

	protected String pmiObjName;


	public String getPmiObjName()
	{
		return pmiObjName;
	}


	public void setPmiObjName(String pmiObjName)
	{
		this.pmiObjName = pmiObjName;
	}

	protected String CSRPackageName;



	public String getCSRPackageName()
	{
		return CSRPackageName;
	}



	public void setCSRPackageName(String cSRPackageName)
	{
		CSRPackageName = cSRPackageName;
	}





	/** The activity status. */
	private String activityStatus="";

	/**
	 * Gets the activity status.
	 *
	 * @return the activity status
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * Sets the activity status.
	 *
	 * @param activityStatus the new activity status
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * Gets the facility id.
	 *
	 * @return facilityId.
	 */
	public String getFacilityId()
	{
		return facilityId;
	}

	/**
	 * Sets the facility id.
	 *
	 * @param facilityId : facility id.
	 */
	public void setFacilityId(String facilityId)
	{
		this.facilityId = facilityId;
	}

	/** Patient date visited. */
	private Date dateVisited;

	/** Patient death date. */
	private Date deathDate;

	/** The vital status. */
	private String vitalStatus="";

	/** matching score value. */
	private int matchingScore = 0;

	Set<Long> protocolIdSet =new HashSet<Long>();


	public Set<Long> getProtocolIdSet()
	{
		return protocolIdSet;
	}


	public void setProtocolIdSet(Set<Long> protocolIdSet)
	{
		this.protocolIdSet = protocolIdSet;
	}

	/**
	 * Gets the matching score.
	 *
	 * @return matchingScore.
	 */
	public int getMatchingScore()
	{
		return matchingScore;
	}

	/**
	 * Sets the matching score.
	 *
	 * @param matchingScore :matchingScore.
	 */
	public void setMatchingScore(int matchingScore)
	{
		this.matchingScore = matchingScore;
	}

	/**
	 * Gets the facility visited.
	 *
	 * @return facilityVisited.
	 */
	public String getFacilityVisited()
	{
		return facilityVisited;
	}

	/**
	 * Sets the facility visited.
	 *
	 * @param facilityVisited : facilityVisited.
	 */
	public void setFacilityVisited(String facilityVisited)
	{
		this.facilityVisited = facilityVisited;
	}

	/**
	 * Gets the date visited.
	 *
	 * @return dateVisited.
	 */
	public Date getDateVisited()
	{
		return dateVisited;
	}

	/**
	 * Sets the date visited.
	 *
	 * @param dateVisited :dateVisited
	 */
	public void setDateVisited(Date dateVisited)
	{
		this.dateVisited = dateVisited;
	}

	/** A collection of medical record identification number that refers to a Participant. */

	private Collection<String> participantMedicalIdentifierCollection = new LinkedHashSet<String>();// = new HashSet();

	/** Participant's race origination. */
	private Collection<String> raceCollection = new HashSet<String>();

	/**
	 * Gets the participant medical identifier collection.
	 *
	 * @return patient's participantMedicalIdentifierCollection.
	 */
	public Collection<String> getParticipantMedicalIdentifierCollection()
	{
		return participantMedicalIdentifierCollection;
	}

	/**
	 * Sets the participant medical identifier collection.
	 *
	 * @param participantMedicalIdentifierCollection :
	 * patient's participantMedicalIdentifierCollection.
	 */
	public void setParticipantMedicalIdentifierCollection(
			Collection<String> participantMedicalIdentifierCollection)
	{
		this.participantMedicalIdentifierCollection = participantMedicalIdentifierCollection;
	}

	/**
	 * Gets the race collection.
	 *
	 * @return raceCollection.
	 */
	public Collection<String> getRaceCollection()
	{
		return raceCollection;
	}

	/**
	 * Sets the race collection.
	 *
	 * @param raceCollection : raceCollection.
	 */
	public void setRaceCollection(Collection<String> raceCollection)
	{
		this.raceCollection = raceCollection;
	}

	/**
	 * Gets the upi.
	 *
	 * @return upi.
	 */
	public String getUpi()
	{
		return upi;
	}

	/**
	 * Sets the upi.
	 *
	 * @param upi :patient's upi
	 */
	public void setUpi(String upi)
	{
		this.upi = upi;
	}

	/**
	 * Gets the first name.
	 *
	 * @return firstName.
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName :firstName.
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return lastName.
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName :lastName.
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Gets the middle name.
	 *
	 * @return middleName.
	 */
	public String getMiddleName()
	{
		return middleName;
	}

	/**
	 * Sets the middle name.
	 *
	 * @param middleName the middle name
	 */
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	/**
	 * Gets the gender.
	 *
	 * @return gender.
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * Sets the gender.
	 *
	 * @param gender : gender.
	 */
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	/**
	 * Gets the ssn.
	 *
	 * @return ssn.
	 */
	public String getSsn()
	{
		return ssn;
	}

	/**
	 * Sets the ssn.
	 *
	 * @param ssn : ssn.
	 */
	public void setSsn(String ssn)
	{
		this.ssn = ssn;
	}

	/**
	 * Gets the dob.
	 *
	 * @return dob.
	 */
	public Date getDob()
	{
		return dob;
	}

	/**
	 * Sets the dob.
	 *
	 * @param dob : patient's dob.
	 */
	public void setDob(Date dob)
	{
		this.dob = dob;
	}

	/**
	 * Gets the address.
	 *
	 * @return address.
	 */
	public Address getAddress()
	{
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address :patient's address.
	 */
	public void setAddress(Address address)
	{
		this.address = address;
	}

	/**
	 * Gets the id.
	 *
	 * @return patient's id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id : patient id.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the death date.
	 *
	 * @return death date.
	 */
	public Date getDeathDate()
	{
		return deathDate;
	}

	/**
	 * Sets the death date.
	 *
	 * @param deathDate : set death date.
	 */
	public void setDeathDate(Date deathDate)
	{
		this.deathDate = deathDate;
	}

	/**
	 * Gets the vital status.
	 *
	 * @return vital status.
	 */
	public String getVitalStatus()
	{
		return vitalStatus;
	}

	/**
	 * Sets the vital status.
	 *
	 * @param vitalStatus : set the vital status.
	 */
	public void setVitalStatus(String vitalStatus)
	{
		this.vitalStatus = vitalStatus;
	}
}
