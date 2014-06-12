/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;

import edu.bcm.dldcc.big.acquire.query.data.AliquotSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.NaLabSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author pew
 * 
 */
public enum Reports
{
  WARM_ISCHEMIA_QUALIFIED("Warm Ischemia Qualified")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AnnotationSearchFields.WARM_ISCHEMIA);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          AnnotationSearchFields.WARM_ISCHEMIA);
      criteria.setNewOperator(SearchOperator.LTE);
      criteria.addOperator();
      List<Integer> values = sm.getSpecimenFieldValues(
          AnnotationSearchFields.WARM_ISCHEMIA, Integer.class);
      values.add(60);
    }
  },
  PRIMARY_UNTREATED_QUALIFIED("Primary Untreated Qualified")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(SpecimenSearchFields.TUMOR_TYPE);
      List<String> primaryValues = sm.getSpecimenFieldValues(
          SpecimenSearchFields.TUMOR_TYPE, String.class);
      primaryValues.add("primary");
      sm.addSpecimenSearchField(AnnotationSearchFields.PRIOR_TREATMENT);
      List<YesNoChoices> untreatedValues = sm.getSpecimenFieldValues(
          AnnotationSearchFields.PRIOR_TREATMENT, YesNoChoices.class);
      untreatedValues.add(YesNoChoices.NO);
      untreatedValues.add(YesNoChoices.NOT_APPLICABLE);
      untreatedValues.add(YesNoChoices.UNKNOWN);
    }
  },
  MATCHED_NORMAL_QUALIFIED("Matched Normal Qualified")
  {

    @Override
    public void initReport(SearchManager sm)
    {
      sm.addNormalSearchField(SpecimenSearchFields.NORMAL_PRESENT);
      List<Boolean> values = sm.getNormalFieldValues(
          SpecimenSearchFields.NORMAL_PRESENT, Boolean.class);
      values.add(true);

    }

  },
  PERCENT_TUMOR_QUALIFIED("Percent Tumor Qualified")
  {

    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AliquotSearchFields.PERCENT_TUMOR);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          AliquotSearchFields.PERCENT_TUMOR);
      criteria.setNewOperator(SearchOperator.GTE);
      criteria.addOperator();
      List<Integer> values = sm.getSpecimenFieldValues(
          AliquotSearchFields.PERCENT_TUMOR, Integer.class);
      values.add(60);
    }

  },
  PERCENT_NECROSIS_QUALIFIED("Percent Necrosis Qualified")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AliquotSearchFields.PERCENT_NECROSIS);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          AliquotSearchFields.PERCENT_NECROSIS);
      criteria.setNewOperator(SearchOperator.LTE);
      criteria.addOperator();
      List<Integer> values = sm.getSpecimenFieldValues(
          AliquotSearchFields.PERCENT_NECROSIS, Integer.class);
      values.add(20);
    }
  },
  FULLY_QUALIFIED("All TCGA Qualified Specimen")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      MATCHED_NORMAL_QUALIFIED.initReport(sm);
      PERCENT_NECROSIS_QUALIFIED.initReport(sm);
      PERCENT_TUMOR_QUALIFIED.initReport(sm);
      PRIMARY_UNTREATED_QUALIFIED.initReport(sm);
      WARM_ISCHEMIA_QUALIFIED.initReport(sm);
    }
  },
  WARM_ISCHEMIA_NULL("Warm Ischemia Null")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AnnotationSearchFields.WARM_ISCHEMIA);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          AnnotationSearchFields.WARM_ISCHEMIA);
      criteria.setNewOperator(SearchOperator.NULL);
      criteria.addOperator();
      List<Integer> values = sm.getSpecimenFieldValues(
          AnnotationSearchFields.WARM_ISCHEMIA, Integer.class);
      values.add(null);
    }
  },
  PERCENT_NECROSIS_NULL("Percent Necrosis Null")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AliquotSearchFields.PERCENT_NECROSIS);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          AliquotSearchFields.PERCENT_NECROSIS);
      criteria.setNewOperator(SearchOperator.NULL);
      criteria.addOperator();
      List<Integer> values = sm.getSpecimenFieldValues(
          AliquotSearchFields.PERCENT_NECROSIS, Integer.class);
      values.add(null);
    }
  },
  PERCENT_TUMOR_NULL("Percent Tumor Null")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AliquotSearchFields.PERCENT_TUMOR);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          AliquotSearchFields.PERCENT_TUMOR);
      criteria.setNewOperator(SearchOperator.NULL);
      criteria.addOperator();
      List<Integer> values = sm.getSpecimenFieldValues(
          AliquotSearchFields.PERCENT_TUMOR, Integer.class);
      values.add(null);
    }
  },
  PRIMARY_UNTREATED_NULL("Primary Untreated Null")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(SpecimenSearchFields.TUMOR_TYPE);
      SearchCriteria<?> criteria = sm.getSpecimenFieldValues().get(
          SpecimenSearchFields.TUMOR_TYPE);
      List<SearchOperator> operators = criteria.getSearchOperators();
      operators.clear();
      operators.add(SearchOperator.NULL);
      // List<String> primaryValues = sm.getSpecimenFieldValues(
      // SpecimenSearchFields.TUMOR_TYPE, String.class);
      // primaryValues.add("not specified");

      sm.addSpecimenSearchField(AnnotationSearchFields.PRIOR_TREATMENT);
      SearchCriteria<?> ptCriteria = sm.getSpecimenFieldValues().get(
          AnnotationSearchFields.PRIOR_TREATMENT);
      List<SearchOperator> ptOperators = ptCriteria.getSearchOperators();
      ptOperators.clear();
      ptOperators.add(SearchOperator.NULL);
      List<YesNoChoices> untreatedValues = sm.getSpecimenFieldValues(
          AnnotationSearchFields.PRIOR_TREATMENT, YesNoChoices.class);
      untreatedValues.add(null);
    }
  },
  POTENTIAL_QUALIFIED("TCRB Potentially Qualifying Tumor Specimen")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      WARM_ISCHEMIA_NULL.initReport(sm);
      PERCENT_NECROSIS_NULL.initReport(sm);
      PERCENT_TUMOR_NULL.initReport(sm);
      PRIMARY_UNTREATED_NULL.initReport(sm);
    }
  },
  ALL("All TCRB Tumor Specimen")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      /*
       * Empty method--in this case, there is no search fields to add
       */
    }
  },
  SHIPPED_TCGA("All Tumor Specimen Shipped to TCGA")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.addSpecimenSearchField(AnnotationSearchFields.STATUS);
      List<SpecimenStatus> statusValues = sm.getSpecimenFieldValues(
          AnnotationSearchFields.STATUS, SpecimenStatus.class);
      statusValues.add(SpecimenStatus.SHIPPED_TCGA);
    }
  },
  QC_REPORT("Quality Control Reports By Site")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      // the only search fields are set outside of this method
    }
  },
  NA_LAB_REPORT("NA Lab Quality Results By Site")
  {
    @Override
    public void initReport(SearchManager sm)
    {
      sm.setIncludeAliquots(true);
      sm.setIncludeNormals(true);
      sm.addSpecimenSearchField(NaLabSearchFields.TYPE);
      List<DerivativeType> typeValues = sm.getSpecimenFieldValues(NaLabSearchFields.TYPE, DerivativeType.class);
      typeValues.add(DerivativeType.DNA);
      for(Entry<SearchFields<?,?>, SearchCriteria<?>> entry : sm.getSpecimenEntries())
      {
        entry.getValue().getSearchOperators().add(SearchOperator.NOT_NULL);
      }
      
    }
  };

  private String displayName;

  public abstract void initReport(SearchManager sm);

  @Override
  public String toString()
  {
    return this.displayName;
  }

  private Reports(String display)
  {
    this.displayName = display;
  }
}
