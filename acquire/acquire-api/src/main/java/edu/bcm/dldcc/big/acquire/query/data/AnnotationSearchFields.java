/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import static edu.bcm.dldcc.big.acquire.util.DataVisibility.NON_PHI;
import static edu.bcm.dldcc.big.acquire.util.DataVisibility.PHI;
import static edu.bcm.dldcc.big.acquire.util.DataVisibility.PUBLIC;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.EnumConverter;
import javax.faces.convert.IntegerConverter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.jboss.seam.faces.conversion.ObjectConverter;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.security.Authorizations;
import edu.bcm.dldcc.big.acquire.util.DataVisibility;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation_;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging_;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging_;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging_;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade_;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage_;
import edu.bcm.dldcc.big.converter.DummyStringConverter;
import edu.bcm.dldcc.big.rac.data.AgeRange;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author pew
 * 
 */
public enum AnnotationSearchFields implements
    SearchFields<From<?, SpecimenAnnotation>, SpecimenAnnotation>
{
  COLLECTION_AGE("Age at Time of Collection", Integer.class, PHI)
  {

    @Override
    public SearchCriteria<Integer> getSearchCriteria()
    {
      SearchCriteria<Integer> search =
          new SearchCriteria<Integer>(Integer.class);
      search.setSingleOperator(false);
      return search;
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (int i = 0; i < values.getValues().size(); i++)
      {
        Object currentValue = values.getValues().get(i);
        SearchOperator operator = values.getSearchOperators().get(i);
        predicate =
            SearchOperation.OR.combinePredicates(cb, predicate, cb.and(cb
                .isNotNull(source.get(SpecimenAnnotation_.ageAtCollection)),
                operator.buildWhere(cb,
                    source.get(SpecimenAnnotation_.ageAtCollection),
                    (Integer) currentValue)));
      }

      return predicate;
    }

    @Override
    public Converter getValueConverter()
    {
      return new IntegerConverter();
    }

  },
  PRIOR_TREATMENT("Prior Treatment", YesNoChoices.class, PUBLIC)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Object value = values.getValues().get(0);
      if (value == null)
      {
        return cb.isNull(source.get(SpecimenAnnotation_.priorTreatment));
      }

      return cb.equal(source.get(SpecimenAnnotation_.priorTreatment),
          (YesNoChoices) value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<YesNoChoices> getSearchCriteria()
    {
      SearchCriteria<YesNoChoices> search =
          new SearchCriteria<YesNoChoices>(YesNoChoices.class);
      search.setSingleValue(true);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<YesNoChoices>) this
          .getPermissibleValues());
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return new EnumConverter(YesNoChoices.class);
    }

  },
  TUMOR_STAGE("Tumor Stage", TumorStage.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object current : values.getValues())
      {
        if (current == null)
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb,
                  cb.isNull(source.get(SpecimenAnnotation_.tumorStage)));
        }
        else
        {
          predicate =
              SearchOperation.OR.combinePredicates(
                  cb,
                  predicate,
                  SearchOperator.EQ.buildWhere(
                      cb,
                      source.get(SpecimenAnnotation_.tumorStage), 
                      (TumorStage) current));
        }

      }

      return predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<TumorStage> getSearchCriteria()
    {
      SearchCriteria<TumorStage> search =
          new SearchCriteria<TumorStage>(TumorStage.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<TumorStage>) this
          .getPermissibleValues());
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class,
          new AnnotationLiteral<Default>()
          {
          });
    }

  },
  TUMOR_GRADE("Tumor Grade", TumorGrade.class, PUBLIC)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object current : values.getValues())
      {
        if (current == null)
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb,
                  cb.isNull(source.get(SpecimenAnnotation_.tumorGrade)));
        }
        else
        {
          predicate =
              SearchOperation.OR.combinePredicates(
                  cb,
                  predicate,
                  SearchOperator.EQ.buildWhere(
                      cb,
                      source.get(SpecimenAnnotation_.tumorGrade), 
                      (TumorGrade) current));
        }
      }

      return predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<TumorGrade> getSearchCriteria()
    {
      SearchCriteria<TumorGrade> search =
          new SearchCriteria<TumorGrade>(TumorGrade.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<TumorGrade>) this
          .getPermissibleValues());
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class,
          new AnnotationLiteral<Default>()
          {
          });
    }

  },
  WARM_ISCHEMIA("Warm Ischemia Time", Integer.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (int i = 0; i < values.getValues().size(); i++)
      {
        Object value = values.getValues().get(i);
        if (value == null)
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb,
                  cb.isNull(source.get(SpecimenAnnotation_.warmIschemiaTime)));
        }
        else
        {
          SearchOperator operator = values.getSearchOperators().get(i);
          predicate =
              SearchOperation.OR
                  .combinePredicates(cb, predicate, operator.buildWhere(cb,
                      source.get(SpecimenAnnotation_.warmIschemiaTime), 
                      (Integer) value));
        }
      }

      return predicate;
    }

    @Override
    public SearchCriteria<Integer> getSearchCriteria()
    {
      SearchCriteria<Integer> search =
          new SearchCriteria<Integer>(Integer.class);
      search.setSingleOperator(false);
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return new IntegerConverter();
    }

  },
  STATUS("Status", SpecimenStatus.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      return source.join(SpecimenAnnotation_.status).in(values.getValues());
    }

    @Override
    public SearchCriteria<SpecimenStatus> getSearchCriteria()
    {
      SearchCriteria<SpecimenStatus> search =
          new SearchCriteria<SpecimenStatus>(SpecimenStatus.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return new EnumConverter(SpecimenStatus.class);
    }

  },
  UUID("Specimen UUID", String.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate =
          source.get(SpecimenAnnotation_.entityId).in(values.getValues());
      if (values.getSearchOperators().get(0) == SearchOperator.NE)
      {
        predicate = cb.not(predicate);
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return new DummyStringConverter();
    }

  },
  CREATE_DATE("Date Submitted", Date.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate =
          values
              .getSearchOperators()
              .get(0)
              .buildWhere(cb, source.get(SpecimenAnnotation_.createDate),
                  (Date) values.getValues().get(0));
      if (values.getSearchOperators().get(0).equals(SearchOperator.LTE))
      {
        predicate =
            SearchOperation.OR.combinePredicates(
                cb,
                predicate,
                SearchOperator.NULL.buildWhere(cb,
                    source.get(SpecimenAnnotation_.createDate), null));
      }
      return predicate;
    }

    @Override
    public SearchCriteria<Date> getSearchCriteria()
    {
      SearchCriteria<Date> search = new SearchCriteria<Date>(Date.class);
      search.setSingleOperator(true);
      search.setSingleValue(true);
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      DateTimeConverter converter = new DateTimeConverter();
      converter.setTimeZone(TimeZone.getDefault());
      converter.setPattern("MM/dd/yyyy");
      return converter;
    }

  },
  T_STAGING("pT Staging", TStaging.class, PUBLIC)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object current : values.getValues())
      {
        if (current == null)
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb,
                  cb.isNull(source.get(SpecimenAnnotation_.TStaging)));
        }
        else
        {
          predicate =
              SearchOperation.OR.combinePredicates(
                  cb,
                  predicate,
                  SearchOperator.EQ.buildWhere(
                      cb,
                      source.get(SpecimenAnnotation_.TStaging), 
                      (TStaging) current));
        }
      }

      return predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<TStaging> getSearchCriteria()
    {
      SearchCriteria<TStaging> search =
          new SearchCriteria<TStaging>(TStaging.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<TStaging>) this.getPermissibleValues());
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class,
          new AnnotationLiteral<Default>()
          {
          });
    }

  },
  N_STAGING("pN Staging", NStaging.class, PUBLIC)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object current : values.getValues())
      {
        if (current == null)
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb,
                  cb.isNull(source.get(SpecimenAnnotation_.NStaging)));
        }
        else
        {
          predicate =
              SearchOperation.OR.combinePredicates(
                  cb,
                  predicate,
                  SearchOperator.EQ.buildWhere(
                      cb,
                      source.get(SpecimenAnnotation_.NStaging), 
                      (NStaging) current));
        }
      }

      return predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<NStaging> getSearchCriteria()
    {
      SearchCriteria<NStaging> search =
          new SearchCriteria<NStaging>(NStaging.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<NStaging>) this.getPermissibleValues());
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class,
          new AnnotationLiteral<Default>()
          {
          });
    }

  },
  M_STAGING("pM Staging", MStaging.class, PUBLIC)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object current : values.getValues())
      {
        if (current == null)
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb,
                  cb.isNull(source.get(SpecimenAnnotation_.MStaging)));
        }
        else
        {
          predicate =
              SearchOperation.OR.combinePredicates(
                  cb,
                  predicate,
                  SearchOperator.EQ.buildWhere(
                      cb,
                      source.get(SpecimenAnnotation_.MStaging), 
                      (MStaging) current));
        }
      }

      return predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<MStaging> getSearchCriteria()
    {
      SearchCriteria<MStaging> search =
          new SearchCriteria<MStaging>(MStaging.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<MStaging>) this.getPermissibleValues());
      return search;
    }

    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class,
          new AnnotationLiteral<Default>()
          {
          });
    }

  },
  BINNED_COLLECTION_AGE("Age at Time of Collection", AgeRange.class, PUBLIC)
  {
    {
      this.configurePermissibleValues(
          new ArrayList<AgeRange>(EnumSet.allOf(AgeRange.class)),
          AgeRange.class);
    }

    @Override
    public SearchCriteria<AgeRange> getSearchCriteria()
    {
      SearchCriteria<AgeRange> search =
          new SearchCriteria<AgeRange>(AgeRange.class);
      search.setSingleOperator(false);
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<AgeRange>) this.getPermissibleValues());
      return search;
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, SpecimenAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (int i = 0; i < values.getValues().size(); i++)
      {
        AgeRange currentValue = (AgeRange) values.getValues().get(i);

        SearchOperator operator = values.getSearchOperators().get(i);
        Predicate base =
            cb.isNotNull(source.get(SpecimenAnnotation_.ageAtCollection));
        switch (operator)
        {
        case GT:
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb, predicate, cb.and(base,
                  operator.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getHigh().getValue())));
          break;
        }
        case GTE:
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb, predicate, cb.and(base,
                  operator.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getLow().getValue())));
          break;

        }
        case EQ:
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb, predicate, cb.and(base,
                  SearchOperator.BETWEEN.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getLow().getValue(), currentValue
                          .getRange().getHigh().getValue())));
          break;

        }
        case LT:
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb, predicate, cb.and(base,
                  operator.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getLow().getValue())));
          break;
        }
        case LTE:
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb, predicate, cb.and(base,
                  operator.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getHigh().getValue())));
          break;
        }
        case NE:
        {
          predicate =
              SearchOperation.OR.combinePredicates(cb, predicate, cb.and(base,
                  SearchOperator.LT.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getLow().getValue()),
                  SearchOperator.GT.buildWhere(cb,
                      source.get(SpecimenAnnotation_.ageAtCollection),
                      currentValue.getRange().getHigh().getValue())));
          break;
        }
        default:
        {
          // do nothing--leave predicate as is
        }

        }

      }

      return predicate;
    }

    @Override
    public Converter getValueConverter()
    {
      return new EnumConverter(AgeRange.class);
    }

  };

  private String displayName;

  @Override
  public String toString()
  {
    return this.displayName;
  }

  @Override
  public Class<SpecimenAnnotation> getFromClass()
  {
    return SpecimenAnnotation.class;
  }

  private Class<?> valueClass;

  private List<?> permissibleValues;

  private DataVisibility visibility;

  private AnnotationSearchFields(String displayName, Class<?> valueType,
      DataVisibility type)
  {
    this.displayName = displayName;
    this.valueClass = valueType;
    this.visibility = type;
  }

  @Override
  public <E> void configurePermissibleValues(List<E> values, Class<E> type)
  {
    if (type.equals(this.valueClass))
    {
      this.permissibleValues = values;
    }
  }

  protected List<?> getPermissibleValues()
  {
    return this.permissibleValues;
  }

  public boolean isVisible() throws IdentityException,
      FeatureNotSupportedException
  {
    Authorizations auth =
        Resources.lookup(Authorizations.class, new AnnotationLiteral<Default>()
        {
        });
    return auth.isDataVisibleForProgram(this.visibility);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.search.SearchFields#getFieldConverter()
   */
  @Override
  public Converter getFieldConverter()
  {
    return new EnumConverter(AnnotationSearchFields.class);
  }

}
