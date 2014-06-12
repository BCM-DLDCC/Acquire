/**
 * 
 */
package edu.bcm.dldcc.big.clinical.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import edu.bcm.dldcc.big.acquire.annotations.integration.CaTissueEntity;
import edu.bcm.dldcc.big.acquire.util.EntityAnnotation;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.annotation.listeners.AliquotAnnotationListener;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
@Entity
@EntityListeners(value = AliquotAnnotationListener.class)
@Audited
public class AliquotAnnotation implements EntityAnnotation
{

  private Integer version;
  private String entityId;
  private Integer percentNuclei;
  private Integer percentNecrosis;
  private Integer percentStroma;
  private String rationale;
  private String images;
  private SpecimenAnnotation parent;
  private SpecimenAnnotation specimenFields;
  private EntityMap map;
  private String imageObjectId;
  private Map<Class<? extends BaseAliquotAnnotation>, BaseAliquotAnnotation> annotations = new HashMap<Class<? extends BaseAliquotAnnotation>, BaseAliquotAnnotation>();
  private List<NaLabAnnotation> naLabAnnotations = new ArrayList<NaLabAnnotation>();
  private Set<SpecimenStatus> status = new HashSet<SpecimenStatus>();

  /**
   * 
   */
  public AliquotAnnotation()
  {
    super();
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
   * @return the aliquotId
   */
  @Override
  @CaTissueEntity(entity = Specimen.class)
  @Id
  public String getEntityId()
  {
    return this.entityId;
  }

  /**
   * @param aliquotId
   *          the aliquotId to set
   */
  @Override
  public void setEntityId(String aliquotId)
  {
    this.entityId = aliquotId;
  }

  /**
   * @return the percentNuclei
   */
  @Min(0)
  @Max(100)
  public Integer getPercentNuclei()
  {
    return this.percentNuclei;
  }

  /**
   * @param percentNuclei
   *          the percentNuclei to set
   */
  public void setPercentNuclei(Integer percentNuclei)
  {
    this.percentNuclei = percentNuclei;
  }

  /**
   * @return the percentNecrosis
   */
  @Min(0)
  @Max(100)
  public Integer getPercentNecrosis()
  {
    return this.percentNecrosis;
  }

  /**
   * @param percentNecrosis
   *          the percentNecrosis to set
   */
  public void setPercentNecrosis(Integer percentNecrosis)
  {
    this.percentNecrosis = percentNecrosis;
  }

  /**
   * @return the percentStroma
   */
  @Min(0)
  @Max(100)
  public Integer getPercentStroma()
  {
    return this.percentStroma;
  }

  /**
   * @param percentStroma
   *          the percentStroma to set
   */
  public void setPercentStroma(Integer percentStroma)
  {
    this.percentStroma = percentStroma;
  }

  /**
   * @return the images
   */
  public String getImages()
  {
    return this.images;
  }

  /**
   * @param images
   *          the images to set
   */
  public void setImages(String images)
  {
    this.images = images;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.entityId == null) ? 0 : this.entityId.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AliquotAnnotation other = (AliquotAnnotation) obj;
    if (this.entityId == null)
    {
      if (other.entityId != null)
        return false;
    }
    else if (!this.entityId.equals(other.entityId))
      return false;
    return true;
  }

  /**
   * @return the rationale
   */
  public String getRationale()
  {
    return this.rationale;
  }

  /**
   * @param rationale
   *          the rationale to set
   */
  @Length(max = 4001)
  public void setRationale(String rationale)
  {
    this.rationale = rationale;
  }

  /**
   * @return the parent
   */
  @ManyToOne(fetch = FetchType.LAZY)
  public SpecimenAnnotation getParent()
  {
    return this.parent;
  }

  /**
   * @param parent
   *          the parent to set
   */
  public void setParent(SpecimenAnnotation parent)
  {
    this.parent = parent;
  }

  /**
   * @return the specimenFields
   */
  @OneToOne(mappedBy = "aliquotFields")
  public SpecimenAnnotation getSpecimenFields()
  {
    return this.specimenFields;
  }

  /**
   * @param specimenFields
   *          the specimenFields to set
   */
  public void setSpecimenFields(SpecimenAnnotation specimenFields)
  {
    this.specimenFields = specimenFields;
  }

  /**
   * @return the map
   */
  @OneToOne
  public EntityMap getMap()
  {
    return this.map;
  }

  /**
   * @param map
   *          the map to set
   */
  public void setMap(EntityMap map)
  {
    this.map = map;
  }

  /**
   * @return the imageObjectId
   */
  public String getImageObjectId()
  {
    return this.imageObjectId;
  }

  /**
   * @param imageObjectId
   *          the imageObjectId to set
   */
  public void setImageObjectId(String imageObjectId)
  {
    this.imageObjectId = imageObjectId;
  }

  /**
   * Gets the map of Clinical Annotations associated with this specimen.
   * Although it can't be fully enforced, the convention is that the value will
   * be an Annotation of the class represented by the key. Because of the
   * limitations of generics, in most cases it will be easier to use
   * getAnnotation(Class&lt;T extends BaseSpecimenAnnotation&gt;) to retrieve
   * the specific annotation desired.
   * 
   * @return the annotations
   */
  @OneToMany(mappedBy = "parent")
  @ForeignKey(name = "childFK")
  @MapKeyColumn(name = "child_key")
  public Map<Class<? extends BaseAliquotAnnotation>, BaseAliquotAnnotation> getAnnotations()
  {
    return this.annotations;
  }

  /**
   * The setter is private to prevent the entire map from being set outside of
   * JPA. This will help to prevent mismatches between the key and the value
   * 
   * @param annotations
   *          the annotations to set
   */
  @SuppressWarnings("unused")
  private void setAnnotations(
      Map<Class<? extends BaseAliquotAnnotation>, BaseAliquotAnnotation> annotations)
  {
    this.annotations = annotations;
  }

  /**
   * Returns the Clinical Annotation associated with this specimen that is of
   * the matching type.
   * 
   * @param type
   *          The class of annotation being retrieved
   * @return the annotation of the given class associated with this specimen
   */
  @SuppressWarnings("unchecked")
  public <T extends BaseAliquotAnnotation> T getAnnotation(Class<T> type)
  {
    return (T) this.getAnnotations().get(type);
  }

  /**
   * Adds a Clinical Annotation to this specimen.
   * 
   * @param type
   * @param annotation
   */
  public <T extends BaseAliquotAnnotation> void addAnnotation(Class<T> type,
      T annotation)
  {
    this.getAnnotations().put(type, annotation);
    annotation.setParent(this);
  }

  public <T extends BaseAliquotAnnotation> void removeAnnotation(Class<T> type)
  {
    this.getAnnotations().remove(type);
  }

  /**
   * @return the naLabAnnotations
   */
  @OneToMany(mappedBy = "parent")
  public List<NaLabAnnotation> getNaLabAnnotations()
  {
    return this.naLabAnnotations;
  }

  /**
   * @param naLabAnnotations
   *          the naLabAnnotations to set
   */
  public void setNaLabAnnotations(List<NaLabAnnotation> naLabAnnotations)
  {
    this.naLabAnnotations = naLabAnnotations;
  }

  public void addNaLabAnnotation(NaLabAnnotation annotation)
  {
    this.getNaLabAnnotations().add(annotation);
    annotation.setParent(this);
  }

  public void removeNaLabAnnotation(NaLabAnnotation annotation)
  {
    this.getNaLabAnnotations().remove(annotation);
    annotation.setParent(null);
  }

  /**
   * @return the status
   */
  @ElementCollection
  @Enumerated(EnumType.STRING)
  public Set<SpecimenStatus> getStatus()
  {
    return this.status;
  }

  /**
   * @param status
   *          the status to set
   */
  public void setStatus(Set<SpecimenStatus> status)
  {
    this.status = status;
  }

  @Transient
  public SpecimenAnnotation getSpecimen()
  {
    return this.getSpecimenFields() != null ? this.getSpecimenFields() : this
        .getParent();
  }
  
  @PrePersist
  @PreUpdate
  private void determinePathologyStatus()
  {
    if (this.getPercentNecrosis() != null
        || this.getPercentNuclei() != null)
    {
      this.getStatus().remove(SpecimenStatus.PATHOLOGY);
      this.getSpecimen().getStatus().remove(SpecimenStatus.PATHOLOGY);
    }
    else
    {
      this.getStatus().add(SpecimenStatus.PATHOLOGY);
      this.getSpecimen().checkPathologyStatus();
    }
    
  }
}
