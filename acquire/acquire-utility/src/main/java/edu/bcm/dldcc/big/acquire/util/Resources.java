package edu.bcm.dldcc.big.acquire.util;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.TransactionManager;

import org.jboss.solder.core.ExtensionManaged;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminDatabase;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissue;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.qualifiers.Permissions;
import edu.bcm.dldcc.big.annotations.qualifier.ExcludeSalt;
import edu.bcm.dldcc.big.annotations.qualifier.Exclusion;
import edu.bcm.dldcc.big.annotations.qualifier.IdentityManagement;
import edu.bcm.dldcc.big.annotations.qualifier.SearchConfiguration;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;
import edu.bcm.dldcc.big.rac.data.AgeRange;
import edu.bcm.dldcc.big.rac.data.ApplicationStatus;
import edu.bcm.dldcc.big.rac.data.Consortium;
import edu.bcm.dldcc.big.rac.data.Degree;
import edu.bcm.dldcc.big.rac.data.InstitutionType;
import edu.bcm.dldcc.big.rac.data.IrbStatus;
import edu.bcm.dldcc.big.rac.data.RequestedSampleType;
import edu.bcm.dldcc.big.rac.data.SpecimenRequestType;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.bcm.dldcc.big.submission.nalab.data.BoxLocation;
import edu.bcm.dldcc.big.submission.nalab.data.PatientRole;
import edu.bcm.dldcc.big.submission.nalab.data.ProbandRelationship;
import edu.bcm.dldcc.big.submission.nalab.data.SampleType;
import edu.bcm.dldcc.big.util.qualifier.UserDatabase;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence
 * context, to CDI beans
 * 
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
@ApplicationScoped
@Named
public class Resources
{

  public Resources()
  {
    super();
  }

  public final static String TYPE_SITE = "COLLECTION_SITE";
  public final static String TYPE_PROGRAM = "PROGRAM";

  @PostConstruct
  @SuppressWarnings("unused")
  private void setupLists()
  {
    this.fluidSampleTypes = new ArrayList<SampleType>(this.fluidSet);
    this.sampleTypes =
        new ArrayList<SampleType>(EnumSet.complementOf(this.fluidSet));
  }

  // use @SuppressWarnings to tell IDE to ignore warnings about field not being
  // referenced directly
  @SuppressWarnings("unused")
  @ExtensionManaged
  @ConversationScoped
  @Annotations
  @Operations
  @SearchConfiguration
  @Produces
  @PersistenceUnit(unitName = "acquire")
  private EntityManagerFactory annotationEntityManager;

  @SuppressWarnings("unused")
  @Annotations
  @Admin
  @Produces
  @PersistenceContext(unitName = "acquire")
  private EntityManager annotationEm;

  @SuppressWarnings("unused")
  @CaTissue(instance = CaTissueInstance.TCRB)
  @Admin
  @Produces
  @PersistenceContext(unitName = "caTissueBase")
  private EntityManager caTissueTcrbEntityManager;

  @SuppressWarnings("unused")
  @ExtensionManaged
  @ConversationScoped
  @CaTissue(instance = CaTissueInstance.TCRB)
  @Operations
  @Produces
  @PersistenceUnit(unitName = "caTissueBase")
  private EntityManagerFactory caTissueTcrbEMF;

  @SuppressWarnings("unused")
  @Produces
  @AdminDatabase
  @Default
  @PersistenceContext(unitName = "admin")
  private EntityManager adminEntityManager;

  @SuppressWarnings("unused")
  @ExtensionManaged
  @ConversationScoped
  @PersistenceUnit(unitName = "admin")
  @UserDatabase
  @AdminDatabase
  @Operations
  @Produces
  private EntityManagerFactory adminEMF;

  @Produces
  @IdentityManagement
  public List<String> getGroupTypes()
  {
    List<String> types = new ArrayList<String>();
    types.add(Resources.TYPE_PROGRAM);
    types.add(Resources.TYPE_SITE);
    return types;
  }

  @Produces
  @IdentityManagement
  @SuppressWarnings("unused")
  private String userType = "USER";

  @Produces
  @IdentityManagement
  @SuppressWarnings("unused")
  private boolean useLdap = false;

  @SuppressWarnings("unused")
  @Produces
  @RequestScoped
  @Named("searchOperators")
  private List<SearchOperator> searchOperators = new ArrayList<SearchOperator>(
      EnumSet.complementOf(EnumSet.of(SearchOperator.BETWEEN,
          SearchOperator.NULL, SearchOperator.NOT_NULL)));

  @SuppressWarnings("unused")
  @Produces
  @RequestScoped
  @Named("ageRanges")
  private List<AgeRange> ranges = new ArrayList<AgeRange>(
      EnumSet.allOf(AgeRange.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("boxLocations")
  private List<BoxLocation> boxLocations = new ArrayList<BoxLocation>(
      EnumSet.allOf(BoxLocation.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("probandRelationships")
  private List<ProbandRelationship> relationships =
      new ArrayList<ProbandRelationship>(
          EnumSet.allOf(ProbandRelationship.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("patientRoles")
  private List<PatientRole> patientRoles = new ArrayList<PatientRole>(
      EnumSet.allOf(PatientRole.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("tissueSampleTypes")
  private List<SampleType> sampleTypes;

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("fluidSampleTypes")
  private List<SampleType> fluidSampleTypes;

  private EnumSet<SampleType> fluidSet = EnumSet.of(SampleType.BLOOD_NORMAL,
      SampleType.RECURRENT_BLOOD, SampleType.PRIMARY_BLOOD, SampleType.SALIVA);

  @Produces
  @ApplicationScoped
  @Named("dnaQualityValues")
  private List<DnaQuality> qualityValues = new ArrayList<DnaQuality>(
      EnumSet.allOf(DnaQuality.class));

  @SuppressWarnings("unused")
  @Produces
  @Named("dnaQualityFilterOptions")
  private SelectItem[] getDnaQualityFilters()
  {
    SelectItem[] options = new SelectItem[this.qualityValues.size() + 1];
    options[0] = new SelectItem("", "Select");
    for (int i = 0; i < this.qualityValues.size(); i++)
    {
      options[i + 1] = new SelectItem(this.qualityValues.get(i).toString());
    }

    return options;
  }

  @Produces
  @ExcludeSalt
  private Boolean excludeSalt = true;

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("applicationStatusChoices")
  private List<ApplicationStatus> applicationStatus =
      new ArrayList<ApplicationStatus>(EnumSet.allOf(ApplicationStatus.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("consortiumChoices")
  private List<Consortium> consortium = new ArrayList<Consortium>(
      EnumSet.allOf(Consortium.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("degreeChoices")
  private List<Degree> degree = new ArrayList<Degree>(
      EnumSet.allOf(Degree.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("institutionTypeChoices")
  private List<InstitutionType> institutionType =
      new ArrayList<InstitutionType>(EnumSet.allOf(InstitutionType.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("irbStatusChoices")
  private List<IrbStatus> irbStatus = new ArrayList<IrbStatus>(
      EnumSet.allOf(IrbStatus.class));

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("tumorTypeChoices")
  private List<RequestedSampleType> sampleType =
      new ArrayList<RequestedSampleType>(RequestedSampleType.tumorTypes());

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("normalTypeChoices")
  private List<RequestedSampleType> normalType =
      new ArrayList<RequestedSampleType>(RequestedSampleType.normalTypes());

  @SuppressWarnings("unused")
  @Produces
  @ApplicationScoped
  @Named("specimenRequestTypeChoices")
  private List<SpecimenRequestType> specimenRequestType =
      new ArrayList<SpecimenRequestType>(
          EnumSet.allOf(SpecimenRequestType.class));

  @Produces
  @ApplicationScoped
  @Named("naTypeList")
  private List<DerivativeType> typeList = new ArrayList<DerivativeType>(
      EnumSet.allOf(DerivativeType.class));

  @SuppressWarnings("unused")
  @Produces
  @Named("naTypeFilterOptions")
  private SelectItem[] getNaTypeFilters()
  {
    SelectItem[] options = new SelectItem[this.typeList.size() + 1];
    options[0] = new SelectItem("", "Select");
    for (int i = 0; i < this.typeList.size(); i++)
    {
      options[i + 1] = new SelectItem(this.typeList.get(i).toString());
    }

    return options;
  }

  private String rationale;

  private String errorMessage;

  public static final String ACQUIRE_EXTERNAL_ID_NAME = "Acquire ID";

  @Produces
  @Named("auditRationale")
  public String getRationale()
  {
    return this.rationale;
  }

  public void setRationale(String reason)
  {
    this.rationale = reason;
  }

  @Produces
  @SessionScoped
  @Permissions
  public Map<String, List<String>> getProgramSiteMap(
      IdentitySession identitySession) throws IdentityException
  {
    Map<String, List<String>> programSiteMap =
        new TreeMap<String, List<String>>();
    Collection<Group> programs =
        identitySession.getPersistenceManager().findGroup(TYPE_PROGRAM);
    for (Group program : programs)
    {
      Collection<Group> children =
          identitySession.getRelationshipManager().findAssociatedGroups(
              program, TYPE_SITE, true, true, null);
      List<String> siteList = new ArrayList<String>();
      for (Group child : children)
      {
        siteList.add(child.getName());
      }
      programSiteMap.put(program.getName(), siteList);
    }
    return programSiteMap;
  }

  @Produces
  @ApplicationScoped
  private InternetAddress
      getSupportAddress(@Named("supportAddress") String address,
          @Named("supportName") String name) throws AddressException,
          UnsupportedEncodingException
  {
    return new InternetAddress(address, name);
  }

  @Produces
  @Exclusion
  private List<String> getExclusions()
  {
    List<String> exclusions = new ArrayList<String>();
    exclusions.add("/public/.*");
    exclusions.add("/resources/.*");
    exclusions.add("/javax.faces.resource/.*");
    exclusions.add("/error.xhtml");
    exclusions.add("/error.jsf");
    return exclusions;
  }

  @Produces
  @Named("requiredMessage")
  private final String getRequiredMessage()
  {
    return "Please fill in all fields marked with a red asterisk "
        + "before continuing.";
  }

  @Produces
  @Named("updateStatus")
  private Set<SpecimenStatus> updateStatus = EnumSet.complementOf(EnumSet.of(
      SpecimenStatus.SHIPPED_NA_LAB, SpecimenStatus.SHIPPED_TCGA,
      SpecimenStatus.SHIPPED_CELL_LAB, SpecimenStatus.TCGA_QUALIFIED));

  @Produces
  @Resource(mappedName = "java:jboss/TransactionManager")
  private TransactionManager transactionManager;

  @Produces
  @Named("txManager")
  private PlatformTransactionManager getSpringTransactions(
      TransactionManager manager)
  {
    return new JtaTransactionManager(transactionManager);
  }

  @SuppressWarnings("unchecked")
  public static <T> T lookup(BeanManager manager, Class<T> beanClass,
      Annotation... qualifiers)
  {
    return (T) lookup(manager, (Type) beanClass, qualifiers);
  }

  @SuppressWarnings(
  { "unchecked" })
  public static Object lookup(BeanManager manager, Type beanType,
      Annotation... qualifiers)
  {
    Set<?> beans = manager.getBeans(beanType, qualifiers);
    if (beans.size() != 1)
    {
      if (beans.size() == 0)
      {
        throw new RuntimeException("No beans of class " + beanType + " found.");
      }
      else
      {
        throw new RuntimeException("Multiple beans of class " + beanType
            + " found: " + beans + ".");
      }
    }

    Bean myBean = (Bean) beans.iterator().next();

    return manager.getReference(myBean, beanType,
        manager.createCreationalContext(myBean));
  }

  @SuppressWarnings(
  { "unchecked" })
  public static <T> T lookup(Class<T> beanClass, Annotation... qualifiers)
  {
    return (T) lookup((Type) beanClass, qualifiers);
  }

  public static Object lookup(Type beanType, Annotation... qualifiers)
  {
    return lookup(getBeanManager(), beanType, qualifiers);
  }

  @SuppressWarnings(
  { "unchecked" })
  public static <T> T lookup(BeanManager manager, String name)
  {
    Set<?> beans = manager.getBeans(name);
    if (beans.size() != 1)
    {
      if (beans.size() == 0)
      {
        throw new RuntimeException("No beans with name " + name + " found.");
      }
      else
      {
        throw new RuntimeException("Multiple beans with name " + name
            + " found: " + beans + ".");
      }
    }

    Bean<T> myBean = (Bean<T>) beans.iterator().next();

    return (T) manager.getReference(myBean, myBean.getBeanClass(),
        manager.createCreationalContext(myBean));
  }

  public static <T> T lookup(String name)
  {
    return Resources.<T> lookup(getBeanManager(), name);
  }

  private static BeanManager getBeanManager()
  {
    try
    {
      return (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
    }
    catch (NamingException e)
    {
      try
      {
        return (BeanManager) new InitialContext()
            .lookup("java:app/BeanManager");
      }
      catch (NamingException e1)
      {
        throw new RuntimeException(e1);
      }

    }
  }

  /**
   * @return the errorMessage
   */
  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  /**
   * @param errorMessage
   *          the errorMessage to set
   */
  public void setErrorMessage(String errorMessage)
  {
    this.errorMessage = errorMessage;
  }

}
