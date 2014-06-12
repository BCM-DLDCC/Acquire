package edu.bcm.dldcc.big.acquire.listener;

import org.apache.commons.lang.StringUtils;

import edu.bcm.dldcc.big.acquire.query.data.AliquotSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.ParticipantSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.search.SearchFields;

/**
 * There is probably a need to take what is here and hormonize with the
 * #SearchFields<?, ?> Easiest way is to add another property to the search
 * Fields, SearchFields<?, ?> object, which would be used to locate that
 * property's result from the SearchResult
 * 
 * @author amcowiti
 * 
 */
public enum MinerSearchResultField
{

  S_LABEL(SpecimenSearchFields.LABEL, "Specimen Label", "specimenLabel", true)
  {
    public String getData(SearchResult result)
    {
      return result.getSpecimenLabel();
    }
  },
  P_UUID(null, "Patient UUID", "uuid", true)
  {
    public String getData(SearchResult result)
    {
      return result.getUuid();
    }
  },
  P_SCOL_SITE(SpecimenSearchFields.SPECIMEN_COLLECTION_SITE, "Collection site",
      "collectionSite", true)
  {
    public String getData(SearchResult result)
    {
      return (result.getCollectionSite() != null) ? result.getCollectionSite()
          .getName() : "";
    }
  },
  // 11.30.2012
  // S_TYPE(SpecimenSearchFields.TUMOR_TYPE,"Specimen Type","specimenType",true),//or
  // ?SpecimenSearchFields.TYPE
  S_TYPE(SpecimenSearchFields.TYPE, "Specimen Type", "specimenType", true)
  {
    public String getData(SearchResult result)
    {
      return result.getSpecimenType();
    }
  }, // or ?SpecimenSearchFields.TYPE
  P_D_DIAG(SpecimenSearchFields.DIAGNOSIS, "Disease Diagnosis",
      "diseaseDiagnosis", true)
  {
    public String getData(SearchResult result)
    {
      return result.getDiseaseDiagnosis();
    }
  },
  P_D_SITE(SpecimenSearchFields.DISEASE_SITE, "Disease site", "diseaseSite",
      true)
  {
    public String getData(SearchResult result)
    {
      return result.getDiseaseSite();
    }
  },

  AMOUNT(SpecimenSearchFields.AMOUNT, "specimenAmount", false)
  {
    public String getData(SearchResult result)
    {
      return (result.getSpecimenAmount() != null) ? result.getSpecimenAmount()
          .toString() : "";
    }
  }, // ??
  TUMOR_STAGE(AnnotationSearchFields.TUMOR_STAGE, "tumorStage", false)
  {
    public String getData(SearchResult result)
    {
      return (result.getTumorStage() != null) ? result.getTumorStage()
          .toString() : "";
    }
  },
  TUMOR_GRADE(AnnotationSearchFields.TUMOR_GRADE, "tumorGrade", false)
  {
    public String getData(SearchResult result)
    {
      return (result.getTumorGrade() != null) ? result.getTumorGrade()
          .toString() : "";
    }
  },
  PERCENT_NECROSIS(AliquotSearchFields.PERCENT_NECROSIS, "percentNecrosis",
      false)
  {
    public String getData(SearchResult result)
    {
      return (result.getPercentNecrosis() != null) ? result
          .getPercentNecrosis().toString() : "";
    }
  },
  PERCENT_TUMOR(AliquotSearchFields.PERCENT_TUMOR, "percentNuclei", false)
  {
    public String getData(SearchResult result)
    {
      return (result.getPercentNuclei() != null) ? result.getPercentNuclei()
          .toString() : "";
    }
  },

  MRN(ParticipantSearchFields.MRN, "mrn", false)
  {
    public String getData(SearchResult result)
    {
      return (result.getMrn() != null) ? StringUtils.join(result.getMrn()
          .toArray(), LIST_SEPARATOR) : "";
    }
  },
  RACE(ParticipantSearchFields.RACE, "race", false)
  {
    public String getData(SearchResult result)
    {
      return (result.getRace() != null) ? StringUtils.join(result.getRace()
          .toArray(), LIST_SEPARATOR) : "";
    }
  },
  ETHNICITY(ParticipantSearchFields.ETHNICITY, "ethnicity", false)
  {
    public String getData(SearchResult result)
    {
      return result.getEthnicity();
    }
  },
  GENDER(ParticipantSearchFields.GENDER, "gender", false)
  {
    public String getData(SearchResult result)
    {
      return result.getGender();
    }
  },
  COLLECTION_AGE(AnnotationSearchFields.COLLECTION_AGE, "ageAtCollection",
      false)
  {
    public String getData(SearchResult result)
    {
      return (result.getAgeAtCollection() != null) ? result
          .getAgeAtCollection().toString() : "";
    }
  },

  TUMOR_TYPE(SpecimenSearchFields.TUMOR_TYPE, "tumorType", false)
  {
    public String getData(SearchResult result)
    {
      return result.getTumorType();
    }
  },
  PRIOR_TREATMENT(AnnotationSearchFields.PRIOR_TREATMENT, "priorTreatment",
      false)
  {
    public String getData(SearchResult result)
    {
      return result.getPriorTreatment();
    }
  },
  WARM_ISCHEMIA(AnnotationSearchFields.WARM_ISCHEMIA, "warmIschemiaTime", false)
  {
    public String getData(SearchResult result)
    {
      return result.getWarmIschemiaTime() != null ? result
          .getWarmIschemiaTime().toString() : "";
    }
  };

  /*
   * disable
   * NORMAL_PRESENT(SpecimenSearchFields.NORMAL_PRESENT,SpecimenSearchFields
   * .NORMAL_PRESENT.toString(),"percentStroma",false),//???
   * SPEC_AMOUNT(SpecimenSearchFields
   * .AMOUNT,SpecimenSearchFields.AMOUNT.toString
   * (),"specimenAmount",false);//??;
   */
  private boolean core;
  private SearchFields<?, ?> field;
  private String label;
  private String property;
  private static final String LIST_SEPARATOR = ",";

  private MinerSearchResultField(SearchFields<?, ?> field, String label,
      String property, boolean core)
  {
    this.field = field;
    this.label = label;
    this.property = property;
    this.core = core;
  }

  private MinerSearchResultField(SearchFields<?, ?> field, String property,
      boolean core)
  {
    this(field, field.toString(), property, core);
  }

  public SearchFields<?, ?> getField()
  {
    return field;
  }

  public String getLabel()
  {
    return label;
  }

  public String getProperty()
  {
    return property;
  }

  public boolean isCore()
  {
    return core;
  }

  public abstract String getData(SearchResult result);

  public static MinerSearchResultField getMinerSearchResultField(
      SearchFields<?, ?> searchField)
  {
    for (MinerSearchResultField sfield : MinerSearchResultField.values())
    {
      if (sfield.getField() == searchField)
      {
        return sfield;
      }
    }
    return null;
  }

  public static MinerSearchResultField getMinerSearchResultFieldByProperty(
      String property)
  {

    for (MinerSearchResultField sfield : MinerSearchResultField.values())
    {
      if (sfield.getProperty().equals(property))
      {
        return sfield;
      }
    }
    return null;
  }

}
