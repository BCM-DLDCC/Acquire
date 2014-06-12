/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import static edu.bcm.dldcc.big.acquire.util.DataVisibility.NON_PHI;
import static edu.bcm.dldcc.big.acquire.util.DataVisibility.PUBLIC;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.convert.BooleanConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.DoubleConverter;
import javax.faces.convert.EnumConverter;
import javax.faces.convert.LongConverter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.jboss.seam.faces.conversion.ObjectConverter;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.security.Authorizations;
import edu.bcm.dldcc.big.acquire.util.DataVisibility;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.converter.DummyStringConverter;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;

/**
 * @author pew
 * 
 */
public enum SpecimenSearchFields implements
    SearchFields<From<?, Specimen>, Specimen>
{
  DISEASE_SITE("Disease Site", String.class, PUBLIC)
  {
    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.LIKE);
      search.addOperator();
      return search;
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      source.fetch("specimenCharacteristics");
      for (Object currentValue : values.getValues())
      {
        predicate = this.stringLikeSearch(cb, source
            .<SpecimenCharacteristics> get("specimenCharacteristics")
            .<String> get("tissueSite"), currentValue.toString(), predicate);
      }

      return predicate;
    }

  },
  ID("Specimen CaTissue Id", Long.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object currentValue : values.getValues())
      {
        predicate = SearchOperation.OR.combinePredicates(cb, predicate,
            SearchOperator.EQ.buildWhere(cb, source.<Long> get("id"),
                (Long) currentValue));
      }

      return predicate;
    }

    @Override
    public SearchCriteria<Long> getSearchCriteria()
    {
      SearchCriteria<Long> search = new SearchCriteria<Long>(Long.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      return search;

    }

    @Override
    public Converter getValueConverter()
    {
      return new LongConverter();
    }

  },
  BARCODE("Specimen Barcode", String.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object currentValue : values.getValues())
      {
        predicate = this.stringLikeSearch(cb, source.<String> get("barcode"),
            currentValue.toString(), predicate);
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.LIKE);
      search.addOperator();
      return search;

    }

  },
  LABEL("Specimen Label", String.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object currentValue : values.getValues())
      {
        predicate = this.stringLikeSearch(cb, source.<String> get("label"),
            currentValue.toString(), predicate);
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.LIKE);
      search.addOperator();
      return search;

    }

  },
  TUMOR_TYPE("Tumor Type", String.class, PUBLIC)
  {
    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object currentValue : values.getValues())
      {
        predicate = SearchOperation.OR.combinePredicates(
            cb,
            predicate,
            SearchOperator.LIKE.buildWhere(cb,
                source.<String> get("pathologicalStatus"),
                currentValue.toString()));
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.LIKE);
      search.addOperator();
      return search;

    }
  },
  DIAGNOSIS("Clinical Diagnosis", String.class, NON_PHI)
  {
    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object currentValue : values.getValues())
      {
        predicate = this.stringLikeSearch(cb, source
            .<SpecimenCollectionGroup> get("specimenCollectionGroup")
            .<String> get("clinicalDiagnosis"), currentValue.toString(),
            predicate);
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.LIKE);
      search.addOperator();
      return search;

    }
  },
  AMOUNT("Tumor Amount", Double.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (int i = 0; i < values.getValues().size(); i++)
      {
        Object currentValue = values.getValues().get(i);
        Double searchValue = null;

        if (currentValue instanceof Double)
        {
          searchValue = (Double) currentValue;
        }
        else
        {
          searchValue = Double.parseDouble(currentValue.toString());
        }
        SearchOperator operator = values.getSearchOperators().get(i);
        predicate = SearchOperation.OR.combinePredicates(cb, operator
            .buildWhere(cb, source.<Double> get("availableQuantity"),
                searchValue));
      }
      return predicate;
    }

    @Override
    public SearchCriteria<Double> getSearchCriteria()
    {
      SearchCriteria<Double> search = new SearchCriteria<Double>(Double.class);
      search.setSingleOperator(false);
      return search;
    }
    
    /* (non-Javadoc)
     * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
     */
    @Override
    public Converter getValueConverter()
    {
      return new DoubleConverter();
    }

  },
  SPECIMEN_COLLECTION_SITE("Specimen Collection Site", Site.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object value : values.getValues())
      {
        Site site = (Site) value;
        predicate = SearchOperation.OR.combinePredicates(cb, predicate,
            SearchOperator.EQ.buildWhere(cb, source
                .<SpecimenCollectionGroup> get("specimenCollectionGroup")
                .<Site> get("specimenCollectionSite"), site));
      }
      return predicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<Site> getSearchCriteria()
    {
      SearchCriteria<Site> search = new SearchCriteria<Site>(Site.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<Site>) this.getPermissibleValues());
      return search;
    }
    
    /* (non-Javadoc)
     * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
     */
    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class, 
          new AnnotationLiteral<Default>()
          {
          });
    }
    

  },
  TYPE("Normal Type", String.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (int i = 0; i < values.getValues().size(); i++)
      {
        String value = values.getValues().get(i).toString();
        predicate = SearchOperation.OR.combinePredicates(
            cb,
            predicate,
            values.getSearchOperators().get(i).buildWhere(cb,
                source.<String> get("specimenType"), value));
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(false);
      return search;
    }

  },
  NORMAL_PRESENT("Normal Present", Boolean.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {

      return cb.like(
          cb.lower(source
              .<SpecimenCollectionGroup> get("specimenCollectionGroup")
              .<CollectionProtocolEvent> get("collectionProtocolEvent")
              .<String> get("collectionPointLabel")), "%normal%");

    }

    @Override
    public SearchCriteria<Boolean> getSearchCriteria()
    {
      SearchCriteria<Boolean> search = new SearchCriteria<Boolean>(
          Boolean.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setSingleValue(true);
      search.setHasPermissibleValues(true);
      List<Boolean> values = new ArrayList<Boolean>();
      values.add(true);
      values.add(false);
      search.setPermissibleValues(values);
      return search;
    }
    
    /* (non-Javadoc)
     * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
     */
    @Override
    public Converter getValueConverter()
    {
      return new BooleanConverter();
    }
  },
  SPECIMEN_COLLECTION_SITE_ID("Specimen Collection Site by Id", Long.class,
      NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object value : values.getValues())
      {
        Long site = (Long) value;
        predicate = SearchOperation.OR.combinePredicates(
            cb,
            predicate,
            SearchOperator.EQ.buildWhere(cb, source
                .<SpecimenCollectionGroup> get("specimenCollectionGroup")
                .<Site> get("specimenCollectionSite").<Long> get("id"), site));
      }
      return predicate;
    }

    @Override
    public SearchCriteria<Long> getSearchCriteria()
    {
      SearchCriteria<Long> search = new SearchCriteria<Long>(Long.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();

      return search;
    }
    
    /* (non-Javadoc)
     * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
     */
    @Override
    public Converter getValueConverter()
    {
      return new LongConverter();
    }

  },
  SPECIMEN_COLLECTION_SITE_NAME("Specimen Collection Site by Name",
      String.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object value : values.getValues())
      {
        predicate = SearchOperation.OR.combinePredicates(cb, predicate,
            SearchOperator.EQ.buildWhere(cb, source
                .<SpecimenCollectionGroup> get("specimenCollectionGroup")
                .<Site> get("specimenCollectionSite").<String> get("name"),
                value.toString()));
      }
      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      return search;
    }

  },
  LABEL_EQUALS("Specimen Label Equal To", String.class, NON_PHI)
  {

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      for (Object currentValue : values.getValues())
      {
        predicate = SearchOperation.OR.combinePredicates(cb, predicate,
            SearchOperator.EQ.buildWhere(cb, source.<String> get("label"),
                currentValue.toString()));
      }

      return predicate;
    }

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      return search;

    }

  },
  PARENT("Specimen Parent", Specimen.class, NON_PHI)
  {
    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, Specimen> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      List<?> searchValues = values.getValues();
      int divisions = new Double(Math.ceil(searchValues.size() / 1000.0D))
          .intValue();
      int start = 0;
      int end = 999;
      for (int i = 0; i < divisions; i++)
      {
        int index = Math.min(end, searchValues.size());

        predicate = SearchOperation.OR.combinePredicates(
            cb,
            predicate,
            source.<Specimen> get("parentSpecimen").in(
                searchValues.subList(start, index)));

        start = end + 1;
        end = end + 1000;

      }

      return predicate;
    }

    @Override
    public SearchCriteria<Specimen> getSearchCriteria()
    {
      SearchCriteria<Specimen> search = new SearchCriteria<Specimen>(
          Specimen.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      return search;
    }
    
    /* (non-Javadoc)
     * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
     */
    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class, 
          new AnnotationLiteral<Default>()
          {
          });
    }

  };

  public Class<Specimen> getFromClass()
  {
    return Specimen.class;
  }

  private String displayName;

  private Class<?> valueClass;

  private List<?> permissibleValues;

  private DataVisibility visibility;

  private SpecimenSearchFields(String displayName, Class<?> valueType,
      DataVisibility type)
  {
    this.displayName = displayName;
    this.valueClass = valueType;
    this.visibility = type;
  }

  @Override
  public String toString()
  {
    return this.displayName;
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
    Authorizations auth = Resources.lookup(Authorizations.class,
        new AnnotationLiteral<Default>()
        {
        });
    return auth.isDataVisibleForProgram(this.visibility);
  }

  protected Predicate stringLikeSearch(CriteriaBuilder cb, Path<String> path,
      String value, Predicate predicate)
  {
    if (!value.isEmpty())
    {
      predicate = SearchOperation.OR.combinePredicates(
          cb,
          predicate,
          SearchOperator.LIKE.buildWhere(cb, cb.lower(path),
              "%" + value.toLowerCase() + "%"));
    }

    return predicate;
  }
  
  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.search.SearchFields#getFieldConverter()
   */
  @Override
  public Converter getFieldConverter()
  {
    return new EnumConverter(SpecimenSearchFields.class);
  }
  
  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
   */
  @Override
  public Converter getValueConverter()
  {
    return new DummyStringConverter();
  }

}
