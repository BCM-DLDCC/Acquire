package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.rac.data.RequestedSampleType;
import edu.bcm.dldcc.big.rac.data.SpecimenRequestType;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:07 AM
 */
@Entity
@Table(name = "MaterialRequest")
@Audited
public class MaterialRequestInformation implements Serializable
{

  private Long id;
  private Integer version;
  private List<String> race = new ArrayList<String>();
  private List<String> ethnicity = new ArrayList<String>();
  private Set<AgeAtCollection> ageAtCollection = new HashSet<AgeAtCollection>();
  private List<String> gender = new ArrayList<String>();
  private List<String> organSite = new ArrayList<String>();
  private String diagnosis;
  private YesNoChoices priorTreatment;
  private Integer numberCases = 0;
  private List<String> pathologicalStatus = new ArrayList<String>();
  private Map<RequestedSampleType, SpecimenInformation> tissueTypes =
      new HashMap<RequestedSampleType, SpecimenInformation>();
  private SpecimenRequestType bloodNormal;
  private SpecimenRequestType skinNormal;
  private SpecimenRequestType matchedNormal;
  private SpecimenRequestType adjacentNormal;
  private Boolean anyNormal;
  private String otherNormal;
  private Integer numberNormals = 0;
  private Map<RequestedSampleType, SpecimenInformation> normalTypes =
      new HashMap<RequestedSampleType, SpecimenInformation>();
  private Application application;
  private Boolean excludeStandardData = false;
  private String additionalDataRequest;

  public MaterialRequestInformation()
  {
    super();
  }

  /**
   * @return the id
   */
  @Id
  public Long getId()
  {
    return this.id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @return the version
   */
  @Version
  public Integer getVersion()
  {
    return this.version;
  }

  /**
   * @param version
   *          the version to set
   */
  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the race
   */
  @ElementCollection
  @CollectionTable(name = "MaterialRequestRace", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public List<String> getRace()
  {
    return this.race;
  }

  /**
   * @param race
   *          the race to set
   */
  public void setRace(List<String> race)
  {
    this.race = race;
  }

  /**
   * @return the ethnicity
   */
  @ElementCollection
  @CollectionTable(name = "MaterialRequestEthnic", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public List<String> getEthnicity()
  {
    return this.ethnicity;
  }

  /**
   * @param ethnicity
   *          the ethnicity to set
   */
  public void setEthnicity(List<String> ethnicity)
  {
    this.ethnicity = ethnicity;
  }

  /**
   * @return the ageAtCollection
   */
  @ElementCollection
  @Embedded
  @CollectionTable(name = "MaterialRequestAge", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public Set<AgeAtCollection> getAgeAtCollection()
  {
    return this.ageAtCollection;
  }

  /**
   * @param ageAtCollection
   *          the ageAtCollection to set
   */
  public void setAgeAtCollection(Set<AgeAtCollection> ageAtCollection)
  {
    this.ageAtCollection = ageAtCollection;
  }

  /**
   * @return the gender
   */
  @ElementCollection
  @CollectionTable(name = "MaterialRequestGender", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public List<String> getGender()
  {
    return this.gender;
  }

  /**
   * @param gender
   *          the gender to set
   */
  public void setGender(List<String> gender)
  {
    this.gender = gender;
  }

  /**
   * @return the organSite
   */
  @ElementCollection
  @CollectionTable(name = "MaterialRequestOrganSite", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public List<String> getOrganSite()
  {
    return this.organSite;
  }

  /**
   * @param organSite
   *          the organSite to set
   */
  public void setOrganSite(List<String> organSite)
  {
    this.organSite = organSite;
  }

  /**
   * @return the diagnosis
   */
  public String getDiagnosis()
  {
    return this.diagnosis;
  }

  /**
   * @param diagnosis
   *          the diagnosis to set
   */
  public void setDiagnosis(String diagnosis)
  {
    this.diagnosis = diagnosis;
  }

  /**
   * @return the priortTreatment
   */
  public YesNoChoices getPriorTreatment()
  {
    return this.priorTreatment;
  }

  /**
   * @param priorTreatment
   *          the priortTreatment to set
   */
  public void setPriorTreatment(YesNoChoices priorTreatment)
  {
    this.priorTreatment = priorTreatment;
  }

  /**
   * @return the numberCases
   */
  public Integer getNumberCases()
  {
    return this.numberCases;
  }

  /**
   * @param numberCases
   *          the numberCases to set
   */
  public void setNumberCases(Integer numberCases)
  {
    this.numberCases = numberCases;
  }

  /**
   * @return the pathologicalStatus
   */
  @ElementCollection
  @CollectionTable(name = "MaterialRequestPathStatus", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public List<String> getPathologicalStatus()
  {
    return this.pathologicalStatus;
  }

  /**
   * @param pathologicalStatus
   *          the pathologicalStatus to set
   */
  public void setPathologicalStatus(List<String> pathologicalStatus)
  {
    this.pathologicalStatus = pathologicalStatus;
  }

  /**
   * @return the tissueTypes
   */
  @OneToMany(cascade =
  { CascadeType.ALL }, orphanRemoval = true)
  @MapKey(name = "type")
  @JoinTable(name = "TISSUEINFORMATION", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public Map<RequestedSampleType, SpecimenInformation> getTissueTypes()
  {
    return this.tissueTypes;
  }

  /**
   * @param tissueTypes
   *          the tissueTypes to set
   */
  public void setTissueTypes(
      Map<RequestedSampleType, SpecimenInformation> tissueTypes)
  {
    this.tissueTypes = tissueTypes;
  }

  public void addTissueType(SpecimenInformation info)
  {
    this.getTissueTypes().put(info.getType(), info);
  }

  public void toggleTissueType(RequestedSampleType type)
  {
    if (this.getTissueTypes().containsKey(type))
    {
      this.getTissueTypes().remove(type);
    }
    else
    {
      SpecimenInformation info = new SpecimenInformation();
      info.setType(type);
      this.getTissueTypes().put(type, info);
    }
  }

  /**
   * @return the bloodNormal
   */
  @Enumerated(EnumType.STRING)
  public SpecimenRequestType getBloodNormal()
  {
    return this.bloodNormal;
  }

  /**
   * @param bloodNormal
   *          the bloodNormal to set
   */
  public void setBloodNormal(SpecimenRequestType bloodNormal)
  {
    this.bloodNormal = bloodNormal;
  }

  /**
   * @return the skinNormal
   */
  @Enumerated(EnumType.STRING)
  public SpecimenRequestType getSkinNormal()
  {
    return this.skinNormal;
  }

  /**
   * @param skinNormal
   *          the skinNormal to set
   */
  public void setSkinNormal(SpecimenRequestType skinNormal)
  {
    this.skinNormal = skinNormal;
  }

  /**
   * @return the matchedNormal
   */
  @Enumerated(EnumType.STRING)
  public SpecimenRequestType getMatchedNormal()
  {
    return this.matchedNormal;
  }

  /**
   * @param matchedNormal
   *          the matchedNormal to set
   */
  public void setMatchedNormal(SpecimenRequestType matchedNormal)
  {
    this.matchedNormal = matchedNormal;
  }

  /**
   * @return the adjacentNormal
   */
  @Enumerated(EnumType.STRING)
  public SpecimenRequestType getAdjacentNormal()
  {
    return this.adjacentNormal;
  }

  /**
   * @param adjacentNormal
   *          the adjacentNormal to set
   */
  public void setAdjacentNormal(SpecimenRequestType adjacentNormal)
  {
    this.adjacentNormal = adjacentNormal;
  }

  /**
   * @return the anyNormal
   */
  public Boolean getAnyNormal()
  {
    return this.anyNormal;
  }

  /**
   * @param anyNormal
   *          the anyNormal to set
   */
  public void setAnyNormal(Boolean anyNormal)
  {
    this.anyNormal = anyNormal;
  }

  /**
   * @return the otherNormal
   */
  public String getOtherNormal()
  {
    return this.otherNormal;
  }

  /**
   * @param otherNormal
   *          the otherNormal to set
   */
  public void setOtherNormal(String otherNormal)
  {
    this.otherNormal = otherNormal;
  }

  /**
   * @return the numberNormals
   */
  public Integer getNumberNormals()
  {
    return this.numberNormals;
  }

  /**
   * @param numberNormals
   *          the numberNormals to set
   */
  public void setNumberNormals(Integer numberNormals)
  {
    this.numberNormals = numberNormals;
  }

  /**
   * @return the normalTypes
   */
  @OneToMany(cascade =
  { CascadeType.ALL }, orphanRemoval = true)
  @MapKey(name = "type")
  @JoinTable(name = "NORMALINFORMATION", joinColumns =
  { @JoinColumn(name = "MaterialRequest_id") })
  public Map<RequestedSampleType, SpecimenInformation> getNormalTypes()
  {
    return this.normalTypes;
  }

  public void addNormalType(SpecimenInformation info)
  {
    this.getNormalTypes().put(info.getType(), info);
  }

  public void toggleNormalType(RequestedSampleType type)
  {
    if (this.getNormalTypes().containsKey(type))
    {
      this.getNormalTypes().remove(type);
    }
    else
    {
      SpecimenInformation info = new SpecimenInformation();
      info.setType(type);
      this.getNormalTypes().put(type, info);
    }
  }

  /**
   * @param normalTypes
   *          the normalTypes to set
   */
  public void setNormalTypes(
      Map<RequestedSampleType, SpecimenInformation> normalTypes)
  {
    this.normalTypes = normalTypes;
  }

  /**
   * @return the application
   */
  @OneToOne
  @MapsId
  @JoinColumn(name = "app_id")
  public Application getApplication()
  {
    return this.application;
  }

  /**
   * @param application
   *          the application to set
   */
  public void setApplication(Application application)
  {
    this.application = application;
  }

  /**
   * @return the excludeStandardData
   */
  public Boolean getExcludeStandardData()
  {
    return this.excludeStandardData;
  }

  /**
   * @param excludeStandardData
   *          the excludeStandardData to set
   */
  public void setExcludeStandardData(Boolean excludeStandardData)
  {
    this.excludeStandardData = excludeStandardData;
  }

  /**
   * @return the additionalDataRequest
   */
  public String getAdditionalDataRequest()
  {
    return this.additionalDataRequest;
  }

  /**
   * @param additionalDataRequest
   *          the additionalDataRequest to set
   */
  public void setAdditionalDataRequest(String additionalDataRequest)
  {
    this.additionalDataRequest = additionalDataRequest;
  }

  @Transient
  public List<AgeAtCollection> getAgesAsList()
  {
    return new ArrayList<AgeAtCollection>(new TreeSet<AgeAtCollection>(
        this.getAgeAtCollection()));
  }
  
  @Transient
  public boolean getTissueInformation(RequestedSampleType type)
  {
    return this.getTissueTypes().containsKey(type);
  }
  
  public void setTissueInformation(RequestedSampleType type)
  {
    this.toggleTissueType(type);
  }

}