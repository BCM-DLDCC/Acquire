package edu.wustl.common.participant.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.wustl.catissuecore.domain.Race;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.lookup.DefaultLookupParameters;
import edu.wustl.common.lookup.DefaultLookupResult;
import edu.wustl.common.participant.client.IParticipantManager;
import edu.wustl.common.participant.client.IParticipantManagerLookupLogic;
import edu.wustl.common.participant.domain.IParticipant;
import edu.wustl.common.participant.domain.IParticipantMedicalIdentifier;
import edu.wustl.common.participant.domain.IRace;
import edu.wustl.common.participant.domain.ISite;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.util.PatientLookupException;

// TODO: Auto-generated Javadoc
/**
 * The Class ParticipantManagerUtility.
 */
public class ParticipantManagerUtility
{

  /** The logger. */
  private static final Logger LOGGER = Logger
      .getCommonLogger(ParticipantManagerUtility.class);

  /**
   * Gets the participant medical identifier obj.
   * 
   * @param mrn
   *          the mrn
   * @param facilityId
   *          the facility id
   * 
   * @return the participant medical identifier obj
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws ParticipantManagerException
   *           the participant manager exception
   * @throws Exception
   *           the exception
   */
  public static IParticipantMedicalIdentifier<IParticipant, ISite> getParticipantMedicalIdentifierObj(
      final String mrn, final String facilityId) throws BizLogicException,
      ParticipantManagerException
  {

    ISite site = null;
    site = getSiteObject(facilityId);
    IParticipantMedicalIdentifier<IParticipant, ISite> partiMedId = null;
    if (site != null)
    {
      partiMedId = getPMIInstance();
      partiMedId.setMedicalRecordNumber(mrn);
      partiMedId.setSite(site);
    }
    return partiMedId;
  }

  /**
   * Gets the site object.
   * 
   * @param facilityId
   *          the facility id
   * 
   * @return the site object
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws ParticipantManagerException
   *           the participant manager exception
   * @throws Exception
   *           the exception
   */
  public static ISite getSiteObject(final String facilityId)
      throws BizLogicException, ParticipantManagerException
  {
    String sourceObjectName = ISite.class.getName();
    String selectColumnNames[] =
    { "id", "name" };
    DefaultBizLogic bizLogic = new DefaultBizLogic();
    ISite site = null;
    QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
    try
    {
      queryWhereClause.addCondition(new EqualClause("facilityId", '?'));
    }
    catch (DAOException e)
    {
      throw new BizLogicException(e);
    }

    List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
    columnValueBeans.add(new ColumnValueBean(facilityId));
    // List siteObject = bizLogic.retrieve(sourceObjectName, selectColumnNames,
    // queryWhereClause,
    // columnValueBeans);

    List<Object> siteObject = bizLogic.retrieve(sourceObjectName,
        selectColumnNames, queryWhereClause);

    if (siteObject != null && siteObject.size() > 0)
    {
      Object siteList[] = (Object[]) siteObject.get(0);
      Long siteId = (Long) siteList[0];
      String siteName = (String) siteList[1];
      site = (ISite) ParticipantManagerUtility.getSiteInstance();
      site.setId(siteId);
      site.setName(siteName);
    }
    return site;
  }

  /**
   * Gets the object.
   * 
   * @param bizLogicFactoryName
   *          the biz logic factory name
   * 
   * @return the object
   * 
   * @throws BizLogicException
   *           the biz logic exception
   */
  public static Object getObject(final String bizLogicFactoryName)
      throws BizLogicException
  {
    try
    {
      Class className = Class.forName(bizLogicFactoryName);
      Object newobj = className.newInstance();
      return newobj;
    }
    catch (IllegalAccessException e)
    {
      LOGGER.info(e.getMessage());
      throw new BizLogicException(null, null, "IllegalAccessException",
          "IllegalAccessException");
    }
    catch (InstantiationException e)
    {
      LOGGER.info(e.getMessage());
      throw new BizLogicException(null, null, "InstantiationException",
          "InstantiationException");
    }
    catch (ClassNotFoundException e)
    {
      LOGGER.info(e.getMessage());
      throw new BizLogicException(null, null, "ClassNotFoundException",
          "ClassNotFoundException");
    }

  }

  /**
   * This function takes identifier as parameter and returns corresponding
   * Participant.
   * 
   * @param identifier
   *          the identifier
   * 
   * @return - Participant object
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws Exception
   */
  public static IParticipant getParticipantById(final Long identifier)
      throws BizLogicException
  {
    // Initializing instance of IBizLogic
    IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();

    IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
    String sourceObjectName = IParticipant.class.getName();
    // getting all the participants from the database
    // List participantList = bizLogic.retrieve(sourceObjectName, "id",
    // identifier);
    QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
    try
    {
      queryWhereClause.addCondition(new EqualClause("id", '?'));
    }
    catch (DAOException e)
    {
      throw new BizLogicException(e);
    }

    List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
    columnValueBeans.add(new ColumnValueBean(identifier));
    // List participantList = bizLogic.retrieve(sourceObjectName, null,
    // queryWhereClause,
    // columnValueBeans);

    List<Object> participantList = new ArrayList<Object>();
    return (IParticipant) participantList.get(0);

  }

  /**
   * Generates key for ParticipantMedicalIdentifierMap.
   * 
   * @param idx
   *          serial number
   * @param keyFor
   *          Attribute based on which respective key is to generate
   * 
   * @return key for map attribute
   */
  public static String getParticipantMedicalIdentifierKeyFor(int idx,
      String keyFor)
  {
    return (Constants.PARTICIPANT_MEDICAL_IDENTIFIER + idx + keyFor);
  }

  /**
   * Gets the mrn value.
   * 
   * @param medIdcol
   *          the med idcol
   * 
   * @return the mrn value
   */
  public static String getMrnValue(Collection medIdcol)
  {
    String mrn = null;

    if (medIdcol != null && !medIdcol.isEmpty())
    {
      Iterator iterator = medIdcol.iterator();
      while (iterator.hasNext())
      {
        IParticipantMedicalIdentifier<IParticipant, ISite> partMedId = (IParticipantMedicalIdentifier<IParticipant, ISite>) iterator
            .next();
        mrn = partMedId.getMedicalRecordNumber();
        break;
      }
    }
    return mrn;
  }

  /**
   * Gets the list of matching participants.
   * 
   * @param participant
   *          the participant
   * @param lookupAlgorithm
   *          the lookup algorithm
   * @param protocolId
   *          the protocol id
   * 
   * @return the list of matching participants
   * 
   * @throws Exception
   *           the exception
   * @throws PatientLookupException
   *           the patient lookup exception
   */
  public static List<DefaultLookupResult> getListOfMatchingParticipants(
      IParticipant participant, String lookupAlgorithm, Long protocolId)
      throws PatientLookupException
  {
    List<DefaultLookupResult> matchParticipantList = null;
    try
    {
      // get all the associated CS ids for the MISC if match within MICS is
      // enabled
      Set<Long> protocolIdList = getProtocolIdLstForMICSEnabledForMatching(protocolId);

      matchParticipantList = findMatchedParticipants(participant,
          lookupAlgorithm, protocolIdList);
    }
    catch (Exception exp)
    {
      throw new PatientLookupException(exp.getMessage(), exp);
    }
    return matchParticipantList;
  }

  /**
   * Gets the list of matching participants.
   * 
   * @param participant
   *          the participant
   * @param lookupAlgorithm
   *          the lookup algorithm
   * @param protocolIdSet
   *          the protocol id set
   * 
   * @return the list of matching participants
   * 
   * @throws Exception
   *           the exception
   * @throws PatientLookupException
   *           the patient lookup exception
   */
  public static List<DefaultLookupResult> findMatchedParticipants(
      IParticipant participant, String lookupAlgorithm, Set<Long> protocolIdSet)
      throws PatientLookupException
  {
    List<DefaultLookupResult> matchParticipantList = null;
    try
    {
      IParticipantManagerLookupLogic partLookupLgic = getLookUPLogicObj(lookupAlgorithm);
      DefaultLookupParameters params = new DefaultLookupParameters();
      params.setObject(participant);
      matchParticipantList = partLookupLgic.lookup(params, protocolIdSet);

    }
    catch (Exception exp)
    {
      throw new PatientLookupException(exp.getMessage(), exp);
    }
    return matchParticipantList;
  }

  /**
   * Gets the look up logic obj.
   * 
   * @param lookupAlgorithm
   *          the lookup algorithm
   * 
   * @return the look up logic obj
   */
  private static IParticipantManagerLookupLogic getLookUPLogicObj(
      String lookupAlgorithm)
  {
    IParticipantManagerLookupLogic partLookupLgic = null;
    if (lookupAlgorithm == null)
    {
      partLookupLgic = (IParticipantManagerLookupLogic) Utility
          .getObject(XMLPropertyHandler
              .getValue(Constants.PARTICIPANT_LOOKUP_ALGO));
    }
    else
    {
      partLookupLgic = (IParticipantManagerLookupLogic) Utility
          .getObject(XMLPropertyHandler.getValue(lookupAlgorithm));
    }
    return partLookupLgic;
  }

  /**
   * Process list for match within cs.
   * 
   * @param matchPartpantLst
   *          the match partpant lst
   * @param csIdList
   *          the cs id list
   * 
   * @return the list
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static List processListForMatchWithinCS(
      List<DefaultLookupResult> matchPartpantLst, Set csIdList)
      throws DAOException
  {
    List idList = ParticipantManagerUtility.getPartcipantIdsList(csIdList);
    matchPartpantLst = filerMatchedList(matchPartpantLst, idList);
    return matchPartpantLst;
  }

  /**
   * Filer matched list.
   * 
   * @param matchPartpantLst
   *          the match partpant lst
   * @param idList
   *          the id list
   * 
   * @return the list< default lookup result>
   */
  public static List<DefaultLookupResult> filerMatchedList(
      List<DefaultLookupResult> matchPartpantLst, List idList)
  {

    List<DefaultLookupResult> matchPartpantLstFiltred = new ArrayList<DefaultLookupResult>();
    Iterator<DefaultLookupResult> itr = matchPartpantLst.iterator();
    if (!idList.isEmpty() && idList.get(0) != null
        && !"".equals(String.valueOf(idList.get(0))))
    {
      List participantIdList = idList;
      while (itr.hasNext())
      {
        DefaultLookupResult result = itr.next();
        IParticipant participant = (IParticipant) result.getObject();
        if (participantIdList.contains(String.valueOf(participant.getId()
            .longValue())))
        {
          matchPartpantLstFiltred.add(result);
        }
      }
    }
    return matchPartpantLstFiltred;
  }

  /**
   * Gets the partcipant ids list.
   * 
   * @param cpIdList
   *          the cp id list
   * 
   * @return the partcipant ids list
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static List getPartcipantIdsList(Set cpIdList) throws DAOException
  {
    List idListArray = null;
    List<String> idList = new ArrayList<String>();
    JDBCDAO dao = null;
    String query = null;
    try
    {
      query = "SELECT PARTICIPANT_ID FROM CATISSUE_CLINICAL_STUDY_REG WHERE CLINICAL_STUDY_ID=?";
      dao = getJDBCDAO();

      if (cpIdList != null && !cpIdList.isEmpty())
      {
        Iterator<Long> iterator = cpIdList.iterator();
        while (iterator.hasNext())
        {
          Long id = (Long) iterator.next();
          LinkedList<ColumnValueBean> columnValueBeanList = new LinkedList<ColumnValueBean>();
          columnValueBeanList.add(new ColumnValueBean("CLINICAL_STUDY_ID", id,
              DBTypes.LONG));

          idListArray = dao.executeQuery(query, null, columnValueBeanList);
          if (!idListArray.isEmpty() && !"".equals(idListArray.get(0)))
          {
            for (Iterator<List> itr = idListArray.iterator(); itr.hasNext();)
            {
              idList.add(String.valueOf(itr.next().get(0)));
            }
          }
        }
      }
    }
    catch (DAOException exp)
    {

      throw new DAOException(exp.getErrorKey(), exp, exp.getMsgValues());
    } finally
    {
      dao.closeSession();
    }
    return idList;
  }

  /**
   * Gets the race instance.
   * 
   * @return the race instance
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static Object getRaceInstance() throws BizLogicException,
      ParticipantManagerException
  {
    String raceclassName = (String) PropertyHandler
        .getValue(Constants.RACE_CLASS);
    return getObject(raceclassName);
  }

  /**
   * Gets the site instance.
   * 
   * @return the site instance
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static Object getSiteInstance() throws BizLogicException,
      ParticipantManagerException
  {
    String siteClassName = (String) PropertyHandler
        .getValue(Constants.SITE_CLASS);
    return getObject(siteClassName);
  }

  /**
   * Gets the participant instance.
   * 
   * @return the participant instance
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static Object getParticipantInstance() throws BizLogicException,
      ParticipantManagerException
  {
    String participantClassName = (String) PropertyHandler
        .getValue(Constants.PARTICIPANT_CLASS);
    return getObject(participantClassName);
  }

  /**
   * Gets the pMI instance.
   * 
   * @return the pMI instance
   * 
   * @throws BizLogicException
   *           the biz logic exception
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static IParticipantMedicalIdentifier<IParticipant, ISite> getPMIInstance()
      throws BizLogicException, ParticipantManagerException
  {
    String pmiClassName = (String) PropertyHandler
        .getValue(Constants.PMI_CLASS);
    Object PMIInstance = getObject(pmiClassName);
    return (IParticipantMedicalIdentifier<IParticipant, ISite>) PMIInstance;
  }

  /**
   * Gets the sSN.
   * 
   * @param ssn
   *          the ssn
   * 
   * @return the sSN
   */
  public static String getSSN(String ssn)
  {
    String ssnA = "";
    String ssnB = "";
    String ssnC = "";
    boolean result = false;
    Pattern pattern = Pattern.compile("[0-9]{3}-[0-9]{2}-[0-9]{4}", 2);
    Matcher mat = pattern.matcher(ssn);
    result = mat.matches();
    if (result)
    {
      return ssn;
    }
    if (ssn.length() >= 9)
    {
      ssnA = ssn.substring(0, 3);
      ssnB = ssn.substring(3, 5);
      ssnC = ssn.substring(5, 9);
    }
    else if (ssn.length() >= 4)
    {
      ssnC = ssn.substring(0, 3);
    }
    else
    {
      return ssn;
    }
    ssn = ssnA + "-" + ssnB + "-" + ssnC;
    return ssn;
  }

  /**
   * Gets the column list.
   * 
   * @param columnList
   *          the column list
   * 
   * @return the column list
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static List<String> getColumnList(List<String> columnList)
      throws DAOException
  {
    List<String> displayList = new ArrayList<String>();

    JDBCDAO jdbcDao = null;
    try
    {
      jdbcDao = getJDBCDAO();

      String sql = "SELECT  columnData.COLUMN_NAME,displayData.DISPLAY_NAME FROM CATISSUE_INTERFACE_"
          + "COLUMN_DATA columnData,CATISSUE_TABLE_RELATION relationData,CATISSUE_QUERY_TABLE"
          + "_DATA tableData,CATISSUE_SEARCH_DISPLAY_DATA displayData where relationData.CHIL"
          + "D_TABLE_ID = columnData.TABLE_ID and relationData.PARENT_TABLE_ID = tableData.TA"
          + "BLE_ID and relationData.RELATIONSHIP_ID = displayData.RELATIONSHIP_ID and column"
          + "Data.IDENTIFIER = displayData.COL_ID and tableData.ALIAS_NAME = 'Participant'";
      LOGGER.debug("DATA ELEMENT SQL : " + sql);
      List list = jdbcDao.executeQuery(sql, null, null);
      for (Iterator iterator1 = columnList.iterator(); iterator1.hasNext();)
      {
        String colName1 = (String) iterator1.next();
        Iterator iterator2 = list.iterator();
        while (iterator2.hasNext())
        {
          List rowList = (List) iterator2.next();
          String colName2 = (String) rowList.get(0);
          if (colName1.equals(colName2))
          {
            displayList.add((String) rowList.get(1));
          }
        }
      }
    } finally
    {
      jdbcDao.closeSession();
    }

    return displayList;
  }

  /**
   * Checks if is call to lookup logic needed.
   * 
   * @param participant
   *          the participant
   * 
   * @return true, if is call to lookup logic needed
   */
  public static boolean isCallToLookupLogicNeeded(IParticipant participant)
  {
    boolean flag = true;
    if ((participant.getFirstName() == null || participant.getFirstName()
        .length() == 0)
        && (participant.getMiddleName() == null || participant.getMiddleName()
            .length() == 0)
        && (participant.getLastName() == null || participant.getLastName()
            .length() == 0)
        && (participant.getSocialSecurityNumber() == null || participant
            .getSocialSecurityNumber().length() == 0)
        && participant.getBirthDate() == null
        && (participant.getParticipantMedicalIdentifierCollection() == null || participant
            .getParticipantMedicalIdentifierCollection().size() == 0))
    {
      flag = false;
    }
    return flag;
  }

  /**
   * Gets the column heading list.
   * 
   * @return the column heading list
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static List<String> getColumnHeadingList() throws DAOException
  {
    String columnHeaderList[] =
    { "LAST_NAME", "FIRST_NAME", "MIDDLE_NAME", "BIRTH_DATE", "DEATH_DATE",
        "VITAL_STATUS", "GENDER", "SOCIAL_SECURITY_NUMBER",
        "MEDICAL_RECORD_NUMBER" };
    List<String> columnList = Arrays.asList(columnHeaderList);

    List<String> displayList = new ArrayList<String>();
    // displayList.add("eMPI Id");
    List<String> displayListTemp = getColumnList(columnList);
    displayList.addAll(displayListTemp);
    return displayList;
  }

  /**
   * Gets the participant display list.
   * 
   * @param participantList
   *          the participant list
   * 
   * @return the participant display list
   */
  public static List<List<String>> getParticipantDisplayList(
      List<DefaultLookupResult> participantList)
  {
    List<List<String>> pcpantDisplaylst = new ArrayList<List<String>>();
    Iterator<DefaultLookupResult> itr = participantList.iterator();
    String medicalRecordNo = "";
    List<String> participantInfo;
    for (; itr.hasNext(); pcpantDisplaylst.add(participantInfo))
    {
      DefaultLookupResult result = itr.next();
      IParticipant participant = (IParticipant) result.getObject();
      participantInfo = new ArrayList<String>();
      // participantInfo.add(participant.getEmpiId());
      participantInfo.add(ParticipantManagerUtility
          .modifyNameWithProperCase(participant.getLastName()));
      participantInfo.add(ParticipantManagerUtility
          .modifyNameWithProperCase(participant.getFirstName()));
      participantInfo.add(ParticipantManagerUtility
          .modifyNameWithProperCase(participant.getMiddleName()));
      participantInfo.add(Utility.parseDateToString(participant.getBirthDate(),
          Constants.DATE_FORMAT));
      participantInfo.add(Utility.parseDateToString(participant.getDeathDate(),
          Constants.DATE_FORMAT));
      participantInfo.add(Utility.toString(participant.getVitalStatus()));
      participantInfo.add(Utility.toString(participant.getGender()));
      participantInfo.add(Utility.toString(participant
          .getSocialSecurityNumber()));
      if (participant.getParticipantMedicalIdentifierCollection() != null)
      {
        medicalRecordNo = getParticipantMrnDisplay(participant);
      }
      participantInfo.add(Utility.toString(medicalRecordNo));
      participantInfo.add(String.valueOf(participant.getId()));
    }

    return pcpantDisplaylst;
  }

  /**
   * Gets the participant mrn display.
   * 
   * @param participant
   *          the participant
   * 
   * @return the participant mrn display
   */
  private static String getParticipantMrnDisplay(IParticipant participant)
  {
    StringBuffer mrn = new StringBuffer();
    if (participant.getParticipantMedicalIdentifierCollection() != null)
    {
      Iterator pmiItr = participant.getParticipantMedicalIdentifierCollection()
          .iterator();
      do
      {
        if (!pmiItr.hasNext())
        {
          break;
        }
        IParticipantMedicalIdentifier<IParticipant, ISite> participantMedicalIdentifier = (IParticipantMedicalIdentifier<IParticipant, ISite>) pmiItr
            .next();
        if (participantMedicalIdentifier.getSite() != null
            && (participantMedicalIdentifier.getSite()).getId() != null)
        {
          String siteName = participantMedicalIdentifier.getSite().getName();
          mrn.append(participantMedicalIdentifier.getMedicalRecordNumber());
          mrn.append(':');
          mrn.append(siteName);
          mrn.append("\n<br>");
        }
      }
      while (true);
    }
    return mrn.toString();
  }

  /**
   * Gets the jDBCDAO.
   * 
   * @return the jDBCDAO
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static JDBCDAO getJDBCDAO() throws DAOException
  {
    String appName = CommonServiceLocator.getInstance().getAppName();
    IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(
        appName);
    JDBCDAO jdbcdao = null;
    jdbcdao = daoFactory.getJDBCDAO();
    jdbcdao.openSession(null);
    return jdbcdao;
  }

  /**
   * Gets the dAO.
   * 
   * @return the dAO
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static DAO getDAO() throws DAOException
  {
    DAO dao = null;
    String appName = CommonServiceLocator.getInstance().getAppName();
    IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(
        appName);
    dao = daoFactory.getDAO();
    dao.openSession(null);
    return dao;
  }

  /**
   * Populate patient object.
   * 
   * @param participant
   *          the participant
   * @param protocolIdSet
   *          the protocol id set
   * 
   * @return the patient information
   */
  public static PatientInformation populatePatientObject(
      IParticipant participant, Set<Long> protocolIdSet)
  {
    PatientInformation patientInformation = new PatientInformation();
    patientInformation.setId(participant.getId());
    patientInformation.setLastName(participant.getLastName());
    patientInformation.setFirstName(participant.getFirstName());
    patientInformation.setMiddleName(participant.getMiddleName());
    String ssn = participant.getSocialSecurityNumber();
    if (ssn != null)
    {
      String ssnValue[] = ssn.split("-");
      ssn = ssnValue[0];
      ssn = ssn + ssnValue[1];
      ssn = ssn + ssnValue[2];
    }
    patientInformation.setSsn(ssn);
    if (participant.getBirthDate() != null)
    {
      patientInformation.setDob(participant.getBirthDate());
    }
    patientInformation.setGender(participant.getGender());
    Collection<String> participantInfoMedicalIdentifierCollection = new LinkedList<String>();
    Collection participantMedicalIdentifierCollection = participant
        .getParticipantMedicalIdentifierCollection();
    if (participantMedicalIdentifierCollection != null)
    {
      Iterator itr = participantMedicalIdentifierCollection.iterator();
      do
      {
        if (!itr.hasNext())
        {
          break;
        }
        IParticipantMedicalIdentifier<IParticipant, ISite> participantMedicalIdentifier = (IParticipantMedicalIdentifier<IParticipant, ISite>) itr
            .next();
        if (participantMedicalIdentifier.getMedicalRecordNumber() != null)
        {
          participantInfoMedicalIdentifierCollection
              .add(participantMedicalIdentifier.getMedicalRecordNumber());
          participantInfoMedicalIdentifierCollection.add(String
              .valueOf((participantMedicalIdentifier.getSite()).getId()));
          participantInfoMedicalIdentifierCollection
              .add(((ISite) participantMedicalIdentifier.getSite()).getName());
        }
      }
      while (true);
    }
    patientInformation
        .setParticipantMedicalIdentifierCollection(participantInfoMedicalIdentifierCollection);
    Collection<String> participantInfoRaceCollection = new HashSet<String>();
    for (Race race : participant.getRaceCollection())
    {
      participantInfoRaceCollection.add(race.getRaceName());
    }
    patientInformation.setRaceCollection(participantInfoRaceCollection);

    patientInformation.setProtocolIdSet(protocolIdSet);

    return patientInformation;
  }

  /**
   * Delete processed participant.
   * 
   * @param id
   *          the id
   * 
   * @return true, if successful
   * 
   * @throws DAOException
   *           the DAO exception
   */
  public static void deleteProcessedParticipant(Long id) throws DAOException
  {

    JDBCDAO jdbcdao = null;
    try
    {
      jdbcdao = getJDBCDAO();
      LinkedList<LinkedList<ColumnValueBean>> columnValueBeans = new LinkedList<LinkedList<ColumnValueBean>>();
      LinkedList<ColumnValueBean> columnValueBeanList = new LinkedList<ColumnValueBean>();
      columnValueBeanList.add(new ColumnValueBean(id));
      columnValueBeans.add(columnValueBeanList);
      String query = "DELETE FROM MATCHED_PARTICIPANT_MAPPING WHERE SEARCHED_PARTICIPANT_ID=?";
      jdbcdao.executeUpdate(query, columnValueBeans);
      jdbcdao.commit();

    }
    catch (DAOException e)
    {
      jdbcdao.rollback();
      throw new DAOException(e.getErrorKey(), e, e.getMessage());
    } finally
    {
      jdbcdao.closeSession();
    }
  }

  /**
   * Gets the old participant.
   * 
   * @param dao
   *          the dao
   * @param identifier
   *          the identifier
   * 
   * @return the old participant
   * 
   * @throws BizLogicException
   *           the biz logic exception
   */
  public static IParticipant getOldParticipant(DAO dao, Long identifier)
      throws BizLogicException
  {
    IParticipant oldParticipant;
    try
    {
      oldParticipant = (IParticipant) dao.retrieveById(
          "edu.wustl.clinportal.domain.Participant", identifier);
    }
    catch (DAOException e)
    {
      LOGGER.debug(e.getMessage(), e);
      throw new BizLogicException(e.getErrorKey(), e, e.getMsgValues());
    }
    return oldParticipant;
  }

  /**
   * Modify name with proper case.
   * 
   * @param name
   *          the name
   * 
   * @return the string
   */
  public static String modifyNameWithProperCase(String name)
  {
    String modifiedName = "";
    if (name != null && !"".equals(name))
    {
      modifiedName = name.substring(0, 1).toUpperCase()
          + name.substring(1, name.length()).toLowerCase();
    }
    return modifiedName;
  }

  /**
   * Method gets all the protocol ids for the MICS to which the protocol is is
   * associated with Suppose protocolId associated with MICSId and MICSId
   * enabled for participant match withing mics then this method will return all
   * the protocol ids associated with the MICSid.
   * 
   * @param protocolId
   *          the protocol id
   * 
   * @return the associted mutli inst protocol id list
   * 
   * @throws ParticipantManagerException
   *           the participant manager exception
   * @throws ApplicationException
   *           the application exception
   */
  private static Set<Long> getProtocolIdLstForMICSEnabledForMatching(
      Long protocolId) throws ParticipantManagerException, ApplicationException
  {
    String PartiManagerImplClassName = (String) edu.wustl.common.participant.utility.PropertyHandler
        .getValue(Constants.PARTICIPANT_MANAGER_IMPL_CLASS);
    IParticipantManager participantManagerImplObj = (IParticipantManager) ParticipantManagerUtility
        .getObject(PartiManagerImplClassName);
    Set<Long> protocolIdList = participantManagerImplObj
        .getProtocolIdLstForMICSEnabledForMatching(protocolId);
    return protocolIdList;
  }

  /**
   * Gets the protocol ids string.
   * 
   * @param protocolIdSet
   *          the protocol id set
   * 
   * @return the protocol ids string
   */
  public static String getProtocolIdsString(Set<Long> protocolIdSet)
  {
    String protocolIdsStr = new String();
    if (protocolIdSet != null && !protocolIdSet.isEmpty())
    {
      Iterator<Long> iterator = protocolIdSet.iterator();
      while (iterator.hasNext())
      {
        Long protocolId = (Long) iterator.next();
        protocolIdsStr = protocolIdsStr.concat(String.valueOf((protocolIdsStr
            .length() == 0) ? protocolId : "," + protocolId));
      }
    }
    return protocolIdsStr;
  }

  /**
   * Gets the participant mgr impl obj.
   * 
   * @return the participant mgr impl obj
   * 
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static IParticipantManager getParticipantMgrImplObj()
      throws ParticipantManagerException
  {
    IParticipantManager participantManagerImplObj = null;
    try
    {
      String PartiManagerImplClassName = (String) edu.wustl.common.participant.utility.PropertyHandler
          .getValue(edu.wustl.common.participant.utility.Constants.PARTICIPANT_MANAGER_IMPL_CLASS);
      participantManagerImplObj = (IParticipantManager) ParticipantManagerUtility
          .getObject(PartiManagerImplClassName);
    }
    catch (BizLogicException e)
    {
      // TODO Auto-generated catch block
      throw new ParticipantManagerException(e.getMessage(), e);
    }
    return participantManagerImplObj;

  }

  /**
   * Gets the last name qry.
   * 
   * @param protocolIdSet
   *          the protocol id set
   * @param participantObjName
   *          the participant obj name
   * 
   * @return the last name qry
   * 
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static String getLastNameQry(Set<Long> protocolIdSet,
      String participantObjName) throws ParticipantManagerException
  {
    String fetchByNameQry = null;
    IParticipantManager participantManagerImplObj = getParticipantMgrImplObj();

    fetchByNameQry = participantManagerImplObj.getLastNameQuery(protocolIdSet);

    return fetchByNameQry;
  }

  /**
   * Gets the meta phone qry.
   * 
   * @param protocolIdSet
   *          the protocol id set
   * @param participantObjName
   *          the participant obj name
   * 
   * @return the meta phone qry
   * 
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static String getMetaPhoneQry(Set<Long> protocolIdSet,
      String participantObjName) throws ParticipantManagerException
  {
    String fetchByMetaPhoneQry = null;
    IParticipantManager participantManagerImplObj = getParticipantMgrImplObj();
    fetchByMetaPhoneQry = participantManagerImplObj
        .getMetaPhoneCodeQuery(protocolIdSet);
    return fetchByMetaPhoneQry;
  }

  /**
   * Gets the sSN query.
   * 
   * @param protocolIdSet
   *          the protocol id set
   * @param participantObjName
   *          the participant obj name
   * 
   * @return the sSN query
   * 
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static String getSSNQuery(Set<Long> protocolIdSet,
      String participantObjName) throws ParticipantManagerException
  {
    String fetchBySSNQry = null;
    IParticipantManager participantManagerImplObj = getParticipantMgrImplObj();

    fetchBySSNQry = participantManagerImplObj.getSSNQuery(protocolIdSet);

    return fetchBySSNQry;

  }

  /**
   * Gets the mRN query.
   * 
   * @param protocolIdSet
   *          the protocol id set
   * @param pmiObjName
   *          the pmi obj name
   * 
   * @return the mRN query
   * 
   * @throws ParticipantManagerException
   *           the participant manager exception
   */
  public static String getMRNQuery(Set<Long> protocolIdSet, String pmiObjName)
      throws ParticipantManagerException
  {
    IParticipantManager participantManagerImplObj = getParticipantMgrImplObj();
    String fetchByMRNQry = null;
    fetchByMRNQry = participantManagerImplObj.getMRNQuery(protocolIdSet);
    return fetchByMRNQry;
  }

  /**
   * Fetch the PI and cordinators ids.
   * 
   * @param participantId
   * @return
   * @throws ApplicationException
   */
  public static LinkedHashSet<Long> getParticipantPICordinators(
      long participantId) throws ApplicationException
  {
    IParticipantManager participantManagerImplObj;
    try
    {
      participantManagerImplObj = getParticipantMgrImplObj();
    }
    catch (ParticipantManagerException e)
    {
      throw new ApplicationException(null, e, e.getMessage());
    }
    return participantManagerImplObj.getParticipantPICordinators(participantId);
  }

  /**
   * @param list
   *          - matched patient information list.
   * @return List of PatientInformation objects.
   * @throws PatientLookupException
   *           - throws PatientLookupException
   */
  public static List<PatientInformation> populatePatientInfo(List list)
      throws PatientLookupException
  {
    PatientInformation patientInfo = null;
    List<PatientInformation> patientInfoList = new LinkedList<PatientInformation>();
    try
    {
      for (int i = 0; i < list.size(); i++)
      {
        IParticipant participant = (IParticipant) list.get(i);
        Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> participantInfoMedIdCol = null;
        Collection<String> participantMedIdCol = null;
        if (participant != null)
        {
          patientInfo = new PatientInformation();
          patientInfo.setId(participant.getId());
          patientInfo.setLastName(participant.getLastName());
          patientInfo.setFirstName(participant.getFirstName());
          patientInfo.setMiddleName(participant.getMiddleName());
          if (participant.getBirthDate() != null
              && !"".equals(participant.getBirthDate()))
          {
            patientInfo.setDob((Date) participant.getBirthDate());
          }
          if ((participant.getDeathDate()) != null
              && !("".equals(participant.getDeathDate())))
          {
            patientInfo.setDeathDate((Date) participant.getDeathDate());
          }
          patientInfo.setVitalStatus(participant.getVitalStatus());

          patientInfo.setActivityStatus(participant.getActivityStatus());
          patientInfo.setGender(participant.getGender());
          patientInfo.setSsn(participant.getSocialSecurityNumber());
          if (participant.getSocialSecurityNumber() != null
              && !("".equals(participant.getSocialSecurityNumber())))
          {
            String[] ssn = (participant.getSocialSecurityNumber()).split("-");
            patientInfo.setSsn(ssn[0] + ssn[1] + ssn[2]);
          }

          participantInfoMedIdCol = participant
              .getParticipantMedicalIdentifierCollection();
          if (participantInfoMedIdCol != null
              && !participantInfoMedIdCol.isEmpty())
          {
            Iterator<IParticipantMedicalIdentifier<IParticipant, ISite>> iterator = participantInfoMedIdCol
                .iterator();
            participantMedIdCol = new ArrayList<String>();
            while (iterator.hasNext())
            {
              IParticipantMedicalIdentifier<IParticipant, ISite> participantMedId = iterator
                  .next();
              if (participantMedId.getMedicalRecordNumber() != null
                  && !"".equals(participantMedId.getMedicalRecordNumber()))
              {
                participantMedIdCol.add(participantMedId
                    .getMedicalRecordNumber());
                participantMedIdCol.add(String.valueOf(participantMedId
                    .getSite().getId()));
                participantMedIdCol.add(participantMedId.getSite().getName());
              }
            }
          }
          patientInfo
              .setParticipantMedicalIdentifierCollection(participantMedIdCol);
          Collection<String> participantInfoRaceCollection = new HashSet<String>();
          /*
           * OK, so this isn't set up properly for IRace, but it will do for
           * now, since there's only one implementation of IRace anyway...
           */
          Collection<Race> participantRaceCollection = participant
              .getRaceCollection();
          if (participantRaceCollection != null)
          {
            Iterator itr = participantRaceCollection.iterator();
            do
            {
              if (!itr.hasNext())
              {
                break;
              }
              IRace<IParticipant> race = (IRace<IParticipant>) itr.next();
              if (race != null)
              {
                participantInfoRaceCollection.add(race.getRaceName());
              }
            }
            while (true);
          }
          patientInfo.setRaceCollection(participantInfoRaceCollection);
          patientInfoList.add(patientInfo);
        }
      }
    }
    catch (Exception e)
    {
      Logger.out.info(e.getMessage(), e);
      throw new PatientLookupException(e.getMessage(), e);
    }
    return patientInfoList;
  }
}
