
package edu.wustl.patientLookUp.queryExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.wustl.common.participant.domain.IParticipant;
import edu.wustl.common.participant.domain.IParticipantMedicalIdentifier;
import edu.wustl.common.participant.domain.ISite;
import edu.wustl.common.participant.utility.ParticipantManagerUtility;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.util.Constants;
import edu.wustl.patientLookUp.util.Logger;
import edu.wustl.patientLookUp.util.PatientLookupException;

public class SQLQueryExecutorImpl extends AbstractQueryExecutor
{

	/**
	 * Constructor
	 */
	public SQLQueryExecutorImpl(JDBCDAO jdbcDAO)
	{
		this.jdbcDAO = jdbcDAO;
	}

	/**
	 * This method will fetch the lastName matched patients from database.
	 * @param name - user last name.
	 * @return list of lastName matched patients
	 * @throws PatientLookupException :PatientLookupException
	 */
	public List<PatientInformation> executeQueryForName(String lastName, Set<Long> protocolIdSet,
			String participantObjName) throws PatientLookupException
	{
		List<PatientInformation> patientInformationList = new ArrayList<PatientInformation>();
		DAO dao= null;
		try
		{
		    dao = ParticipantManagerUtility.getDAO();
			String fetchByLNameQry = ParticipantManagerUtility.getLastNameQry(protocolIdSet,
					participantObjName);
			List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
			columnValueBeans.add(new ColumnValueBean(lastName + "%", DBTypes.VARCHAR));
			columnValueBeans.add(new ColumnValueBean(Constants.ACTIVITY_STATUS_DISABLED,
					DBTypes.VARCHAR));
			List list = dao.executeQuery(fetchByLNameQry, columnValueBeans);
			if (!list.isEmpty())
			{
				patientInformationList = ParticipantManagerUtility.populatePatientInfo(list);
			}
		}
		catch (Exception e)
		{
			Logger.out.info(e.getMessage(), e);
			Logger.out.info("Error while retriving the matched patients based on name\n");
			throw new PatientLookupException(e.getMessage(), e);
		}finally{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				// TODO Auto-generated catch block
				throw new PatientLookupException(e.getMessage(), e);
			}
		}

		return patientInformationList;
	}

	public List<PatientInformation> executetQueryForPhonetic(String metaPhone,
			Set<Long> protocolIdSet, String participantObjName) throws PatientLookupException
	{
		List<PatientInformation> patientInformationList = new ArrayList<PatientInformation>();
		DAO dao = null;
		try
		{
			dao = ParticipantManagerUtility.getDAO();
			String fetchByMetaPhoneQry = ParticipantManagerUtility.getMetaPhoneQry(protocolIdSet,
					participantObjName);
			List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
			columnValueBeans.add(new ColumnValueBean(metaPhone, DBTypes.VARCHAR));
			columnValueBeans.add(new ColumnValueBean(Constants.ACTIVITY_STATUS_DISABLED,
					DBTypes.VARCHAR));
			List list = dao.executeQuery(fetchByMetaPhoneQry, columnValueBeans);
			if (!list.isEmpty())
			{
				patientInformationList = ParticipantManagerUtility.populatePatientInfo(list);
			}
		}
		catch (Exception e)
		{
			Logger.out.info(e.getMessage(), e);
			Logger.out.info("Error while retriving the matched patients based on metaphone \n");
			throw new PatientLookupException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				// TODO Auto-generated catch block
				throw new PatientLookupException(e.getMessage(), e);
			}
		}

		return patientInformationList;
	}

	/**
	 * This method will fetch the SSN matched patients from database.
	 * @param ssn - Social security number
	 * @return list of SSN matched patients
	 * @throws PatientLookupException : PatientLookupException
	 */
	public List<PatientInformation> executetQueryForSSN(String ssn, Set<Long> protocolIdSet,
			String participantObjName) throws PatientLookupException
	{
		List<PatientInformation> patientInformationList = new ArrayList<PatientInformation>();
		DAO dao = null;
		try
		{
			String ssnA = ssn.substring(0, 3);
			String ssnB = ssn.substring(3, 5);
			String ssnC = ssn.substring(5, 9);
			ssn = ssnA + "-" + ssnB + "-" + ssnC;
			dao = ParticipantManagerUtility.getDAO();
			String fetchBySSNQry = ParticipantManagerUtility.getSSNQuery(protocolIdSet,
					participantObjName);
			LinkedList<ColumnValueBean> columnValueBeanList = new LinkedList<ColumnValueBean>();
			columnValueBeanList.add(new ColumnValueBean(ssn, DBTypes.VARCHAR));
			columnValueBeanList.add(new ColumnValueBean(Constants.ACTIVITY_STATUS_DISABLED,
					DBTypes.VARCHAR));

			List list = dao.executeQuery(fetchBySSNQry, columnValueBeanList);

			if (!list.isEmpty())
			{
				patientInformationList = ParticipantManagerUtility.populatePatientInfo(list);
			}
		}
		catch (Exception e)
		{
			Logger.out.info(e.getMessage(), e);
			Logger.out.info("Error while retriving the matched patients based on ssn\n");
			throw new PatientLookupException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				// TODO Auto-generated catch block
				throw new PatientLookupException(e.getMessage(), e);
			}
		}

		return patientInformationList;
	}

	/**
	 * This method will fetch the MRN matched patients from database.
	 * @param mrn : User entered MRN value
	 * @return list of mrn matched patients from db
	 * @throws PatientLookupException : PatientLookupException
	 */
	public List<PatientInformation> executeQueryForMRN(String mrn, String siteId,
			Set<Long> protocolIdSet, String pmiObjName) throws PatientLookupException
	{
		List<PatientInformation> patientInformationList = new ArrayList<PatientInformation>();
		DAO dao = null;
		try
		{
			dao = ParticipantManagerUtility.getDAO();
			String fetchByMRNQry = ParticipantManagerUtility.getMRNQuery(protocolIdSet, pmiObjName);

			LinkedList<ColumnValueBean> columnValueBeanList = new LinkedList<ColumnValueBean>();
			columnValueBeanList.add(new ColumnValueBean(mrn, DBTypes.VARCHAR));
			columnValueBeanList.add(new ColumnValueBean(Long.valueOf(siteId), DBTypes.LONG));
			columnValueBeanList.add(new ColumnValueBean(Constants.ACTIVITY_STATUS_DISABLED,
					DBTypes.VARCHAR));

			List list = dao.executeQuery(fetchByMRNQry, columnValueBeanList);

			if (!list.isEmpty())
			{
				patientInformationList = ParticipantManagerUtility.populatePatientInfo(list);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			Logger.out.info(e.getMessage(), e);
			Logger.out.info("Error while retriving matched patients based on MRN");
			throw new PatientLookupException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				// TODO Auto-generated catch block
				throw new PatientLookupException(e.getMessage(), e);
			}
		}
		return patientInformationList;
	}

//	/**
//	 * @param list - matched patient information list.
//	 * @return List of PatientInformation objects.
//	 * @throws PatientLookupException - throws PatientLookupException
//	 */
//	private List<PatientInformation> populatePatientInfo(List list) throws PatientLookupException
//	{
//		PatientInformation patientInfo = null;
//		List<PatientInformation> patientInfoList = new LinkedList<PatientInformation>();
//		try
//		{
//			for (int i = 0; i < list.size(); i++)
//			{
//				IParticipant participant = (IParticipant) list.get(i);
//				Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> participantInfoMedIdCol = null;
//				Collection<String> participantMedIdCol = null;
//				if (participant != null)
//				{
//					patientInfo = new PatientInformation();
//					patientInfo.setId(participant.getId());
//					patientInfo.setLastName(participant.getLastName());
//					patientInfo.setFirstName(participant.getFirstName());
//					patientInfo.setMiddleName(participant.getMiddleName());
//					if (participant.getBirthDate() != null
//							&& !"".equals(participant.getBirthDate()))
//					{
//						patientInfo.setDob((Date) participant.getBirthDate());
//					}
//					if ((participant.getDeathDate()) != null
//							&& !("".equals(participant.getDeathDate())))
//					{
//						patientInfo.setDeathDate((Date) participant.getDeathDate());
//					}
//					patientInfo.setVitalStatus(participant.getVitalStatus());
//
//					patientInfo.setActivityStatus(participant.getActivityStatus());
//					patientInfo.setGender(participant.getGender());
//					patientInfo.setSsn(participant.getSocialSecurityNumber());
//					if (participant.getSocialSecurityNumber() != null
//							&& !("".equals(participant.getSocialSecurityNumber())))
//					{
//						String[] ssn = (participant.getSocialSecurityNumber()).split("-");
//						patientInfo.setSsn(ssn[0] + ssn[1] + ssn[2]);
//					}
//
//					participantInfoMedIdCol = participant
//							.getParticipantMedicalIdentifierCollection();
//					if (participantInfoMedIdCol != null && !participantInfoMedIdCol.isEmpty())
//					{
//						Iterator iterator = participantInfoMedIdCol.iterator();
//						participantMedIdCol = new ArrayList<String>();
//						while (iterator.hasNext())
//						{
//							IParticipantMedicalIdentifier<IParticipant, ISite> participantMedId = (IParticipantMedicalIdentifier<IParticipant, ISite>) iterator
//									.next();
//							if (participantMedId.getMedicalRecordNumber() != null
//									&& !"".equals(participantMedId.getMedicalRecordNumber()))
//							{
//								participantMedIdCol.add(participantMedId.getMedicalRecordNumber());
//								participantMedIdCol.add(String.valueOf(participantMedId.getSite()
//										.getId()));
//								participantMedIdCol.add(participantMedId.getSite().getName());
//							}
//						}
//					}
//					patientInfo.setParticipantMedicalIdentifierCollection(participantMedIdCol);
//					patientInfoList.add(patientInfo);
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			Logger.out.info(e.getMessage(), e);
//			throw new PatientLookupException(e.getMessage(), e);
//		}
//		return patientInfoList;
//	}

	public void fetchRegDateFacilityAndMRNOfPatient(List<PatientInformation> patientMatchingList)
			throws PatientLookupException
	{
		// TODO Auto-generated method stub

	}
}
