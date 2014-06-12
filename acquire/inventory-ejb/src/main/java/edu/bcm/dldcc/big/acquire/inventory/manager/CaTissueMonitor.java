package edu.bcm.dldcc.big.acquire.inventory.manager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jpa.JpaComponent;
import org.apache.camel.component.sjms.SjmsComponent;
import org.apache.camel.component.sjms.jms.ConnectionFactoryResource;
import org.springframework.transaction.PlatformTransactionManager;

import edu.bcm.dldcc.big.acquire.exception.AcquireException;
import edu.bcm.dldcc.big.acquire.inventory.messaging.CaTissueRouteBuilder;
import edu.bcm.dldcc.big.acquire.inventory.session.CaTissueUtilities;
import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.AnnotationUpdate;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLab;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.ParticipantSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.ValueManager;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage;
import edu.bcm.dldcc.big.inventory.entity.DynamicExtensionMetadata;
import edu.bcm.dldcc.big.inventory.entity.DynamicExtensionMetadata_;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Session Bean implementation class ParticipantManager
 */
@Singleton
@LocalBean
@Startup
public class CaTissueMonitor
{

  /**
   * Provides access to all EntityManagers, so that we can make use of each
   * caTissue instance's EntityManager below
   */
  @Inject
  @Any
  private Instance<EntityManager> caTissueEntityManager;

  /**
   * Provides access to the EntityManager for the Annotations, EntityMap and
   * others.
   */
  @Inject
  @Annotations
  @Admin
  private EntityManager annotationEntityManager;

  @Inject
  private CamelContext camelContext;

  @Inject
  private ConnectionFactory connectionFactory;

  @Inject
  private PlatformTransactionManager transactionManager;

  /**
   * Provides access to the Security IdentitySession
   */

  @Inject
  private ValueManager vm;

  @Inject
  @AnnotationUpdate
  private MessageProducer updateProducer;

  @Inject
  @NaLab
  private MessageProducer naLabProducer;

  @Inject
  private Session updateSession;

  @Inject
  private CaTissueUtilities caTissueUtil;

  private ParameterExpression<CaTissueInstance> deInstanceParameter;

  private List<String> caTissueElementNames = new ArrayList<String>();
  {
    caTissueElementNames.add("RESPONSE");
    caTissueElementNames.add("PATHOLOGICAL_STATUS");
  }

  private final String TABLE_CAPTION = "tcrbannotation";
  private final String PRIOR_CAPTION = "priortreatment";
  private final String ESCHEMIA_CAPTION = "warmischemiatimeinminutes";

  private final String tableSqlString = "select name "
      + "from dyextn_database_properties "
      + "where identifier in (select identifier "
      + "from dyextn_table_properties " + "where abstract_entity_id in "
      + "(select abstract_entity_id " + "from dyextn_container "
      + "where lower(caption) " + "= '" + TABLE_CAPTION.toLowerCase() + "'))";

  private final String formSqlString = "select identifier "
      + "from dyextn_abstract_form_context where container_id in "
      + "(select identifier from dyextn_container " + "where lower(caption) "
      + "= '" + TABLE_CAPTION.toLowerCase() + "')";

  private final String priorSqlString = "select name "
      + "from dyextn_database_properties "
      + "where identifier in (select identifier "
      + "from dyextn_column_properties " + "where primitive_attribute_id = "
      + "(select base_abst_atr_id " + "from DYEXTN_CONTROL "
      + "where lower(caption) = '" + PRIOR_CAPTION.toLowerCase() + "'"
      + "AND container_id = " + "(SELECT identifier FROM dyextn_container "
      + "WHERE lower(caption) " + "= '" + TABLE_CAPTION.toLowerCase() + "')))";

  private final String eschemiaSqlString = "select name "
      + "from dyextn_database_properties "
      + "where identifier in (select identifier "
      + "from dyextn_column_properties " + "where primitive_attribute_id = "
      + "(select base_abst_atr_id " + "from DYEXTN_CONTROL "
      + "where lower(caption) = '" + ESCHEMIA_CAPTION.toLowerCase() + "'"
      + "AND container_id = " + "(SELECT identifier FROM dyextn_container "
      + "WHERE lower(caption) " + "= '" + TABLE_CAPTION.toLowerCase() + "')))";

  String identifierSqlString = "select name from dyextn_database_properties"
      + " where identifier in (select identifier "
      + "from dyextn_column_properties where cnstr_key_prop_id in "
      + "(select identifier from dyextn_constraintkey_prop "
      + "where tgt_constraint_key_id in ("
      + "select identifier from dyextn_constraint_properties "
      + "where association_id in ("
      + "select identifier from dyextn_association where "
      + "target_entity_id in (select abstract_entity_id "
      + "from dyextn_container " + "where lower(caption) = '"
      + TABLE_CAPTION.toLowerCase() + "')))))";

  /**
   * Default constructor.
   */
  public CaTissueMonitor()
  {
    super();
  }

  @PostConstruct
  public void setup() throws AcquireException, JMSException
  {
    this.dailySetup();

    try
    {
      /*
       * Need to set up connection information for jms component
       */
      ConnectionFactoryResource connectionResource =
          new ConnectionFactoryResource();
      connectionResource.setConnectionFactory(this.connectionFactory);

      JmsComponent jms = new JmsComponent();
      jms.setConnectionFactory(this.connectionFactory);
      jms.setTransactionManager(this.transactionManager);

      /*
       * Add the jmsComponent to the camel context
       */
      this.camelContext.addComponent("jms", jms);

      /*
       * For each CaTissueInstance, set up a JpaComponent to check for changes,
       * and register the needed routes
       */
      for (CaTissueInstance instance : EnumSet.allOf(CaTissueInstance.class))
      {
        /*
         * Create a JpaComponent, and assign it the appropriate
         * EntityManagerFactory and TransactionManager
         */
        JpaComponent tcrb = new JpaComponent();

        tcrb.setEntityManagerFactory(caTissueEntityManager
            .select(new AdminLiteral(), new CaTissueLiteral(instance)).get()
            .getEntityManagerFactory());

        tcrb.setTransactionManager(this.transactionManager);
        String componentName = instance.toString().toLowerCase() + "Jpa";

        /*
         * Add the component to the camel context
         */
        this.camelContext.addComponent(componentName, tcrb);
        tcrb.start();

        /*
         * Add the route builders for the various update checks.
         */
        this.camelContext.addRoutes(new CaTissueRouteBuilder(instance,
            componentName));

      }

      /*
       * start the camel context
       */
      this.camelContext.start();

    }
    catch (Exception e)
    {
      throw new AcquireException(e.getMessage(), e);
    }
  }

  @Schedule(persistent = false, dayOfWeek = "*")
  public void dailySetup() throws JMSException
  {
    this.setupDynamicExtensionMetadata();
    this.setSearchFieldValues();
    Message message = this.updateSession.createMessage();
    message.setJMSCorrelationID("Acquire");
    this.updateProducer.send(message);
    this.naLabProducer.send(this.updateSession.createMessage());
  }

  private void setSearchFieldValues()
  {
    SpecimenSearchFields.SPECIMEN_COLLECTION_SITE.configurePermissibleValues(
        vm.getValueList(Site.class), Site.class);
    AnnotationSearchFields.PRIOR_TREATMENT.configurePermissibleValues(
        vm.getValueList(YesNoChoices.class), YesNoChoices.class);
    AnnotationSearchFields.TUMOR_STAGE.configurePermissibleValues(
        vm.getValueList(TumorStage.class), TumorStage.class);
    AnnotationSearchFields.TUMOR_GRADE.configurePermissibleValues(
        vm.getValueList(TumorGrade.class), TumorGrade.class);
    AnnotationSearchFields.T_STAGING.configurePermissibleValues(
        vm.getValueList(TStaging.class), TStaging.class);
    AnnotationSearchFields.N_STAGING.configurePermissibleValues(
        vm.getValueList(NStaging.class), NStaging.class);
    AnnotationSearchFields.M_STAGING.configurePermissibleValues(
        vm.getValueList(MStaging.class), MStaging.class);
    ParticipantSearchFields.ETHNICITY.configurePermissibleValues(
        this.caTissueUtil.getEthnicityList(), String.class);
    ParticipantSearchFields.GENDER.configurePermissibleValues(
        this.caTissueUtil.getGenderList(), String.class);
    ParticipantSearchFields.RACE.configurePermissibleValues(
        this.caTissueUtil.getRaceList(), String.class);
  }

  private void setupDynamicExtensionMetadata()
  {

    /*
     * Set up criteria to check for pre-existence
     */
    TypedQuery<DynamicExtensionMetadata> findQuery =
        this.setupDynamicExtensionMetadataQuery();

    /*
     * Loop through all the caTissue instances to get the table and column names
     * for that instance
     */
    for (CaTissueInstance instance : EnumSet.allOf(CaTissueInstance.class))
    {
      EntityManager instanceEm = getEntityManagerForInstance(instance);

      /*
       * Instantiate a DynamicExtensionMetadata entity to hold the information
       */
      DynamicExtensionMetadata metadata = null;

      /*
       * Check if this instance is already recorded in the database
       */

      findQuery.setParameter(this.deInstanceParameter, instance);
      try
      {
        metadata = findQuery.getSingleResult();
      }
      catch (NoResultException e)
      {
        metadata = new DynamicExtensionMetadata();
        metadata.setInstance(instance);
        annotationEntityManager.persist(metadata);
      }

      /*
       * Get the name of the table
       */
      metadata.setTableName(this
          .extractDEColumnName(instanceEm, tableSqlString));

      /*
       * Get the name of the prior treatment column
       */
      metadata.setPriorTreatmentColumn(this.extractDEColumnName(instanceEm,
          priorSqlString));

      /*
       * Get the name of the warm ischemia column
       */
      metadata.setWarmEschemiaColumn(this.extractDEColumnName(instanceEm,
          this.eschemiaSqlString));

      /*
       * Get the name of the id column
       */
      metadata.setIdentifierColumn(this.extractDEColumnName(instanceEm,
          this.identifierSqlString));

      metadata.setFormId(this.extractDEColumnName(instanceEm,
          this.formSqlString));
    }
  }

  private String extractDEColumnName(EntityManager instanceEm, String sql)
  {
    /*
     * Create a native query for the table name -- investigate using a
     * namedquery instead
     */
    Query query = instanceEm.createNativeQuery(sql);

    /*
     * Get the query result, and return it as a String
     */
    return query.getSingleResult().toString();

  }

  private TypedQuery<DynamicExtensionMetadata>
      setupDynamicExtensionMetadataQuery()
  {
    CriteriaBuilder cb = annotationEntityManager.getCriteriaBuilder();
    CriteriaQuery<DynamicExtensionMetadata> criteria =
        cb.createQuery(DynamicExtensionMetadata.class);
    Root<DynamicExtensionMetadata> root =
        criteria.from(DynamicExtensionMetadata.class);
    criteria.select(root);
    this.deInstanceParameter = cb.parameter(CaTissueInstance.class, "instance");
    criteria.where(cb.equal(root.get(DynamicExtensionMetadata_.instance),
        this.deInstanceParameter));
    return annotationEntityManager.createQuery(criteria);
  }

  private EntityManager getEntityManagerForInstance(CaTissueInstance instance)
  {
    EntityManager instanceEm =
        caTissueEntityManager.select(new CaTissueLiteral(instance),
            new AdminLiteral()).get();
    return instanceEm;
  }

}
