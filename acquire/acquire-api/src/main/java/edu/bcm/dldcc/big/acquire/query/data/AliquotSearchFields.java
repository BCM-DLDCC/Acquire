/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import static edu.bcm.dldcc.big.acquire.util.DataVisibility.NON_PHI;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.convert.Converter;
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
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation_;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation_;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author pew
 * 
 */
public enum AliquotSearchFields implements
    SearchFields<From<?, AliquotAnnotation>, AliquotAnnotation>
{
  PERCENT_NECROSIS("Percent Tumor Necrosis", Integer.class, NON_PHI)
  {
    @Override
    public Predicate
        buildSinglePredicate(CriteriaBuilder cb,
            From<?, AliquotAnnotation> source, SearchOperator operator,
            Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(AliquotAnnotation_.percentNecrosis));
      }
      else
      {
        Integer intValue = null;

        if (value instanceof Integer)
        {
          intValue = (Integer) value;
        }
        else
        {
          intValue = Integer.parseInt(value.toString());
        }

        predicate =
            operator.buildWhere(cb,
                source.get(AliquotAnnotation_.percentNecrosis), intValue);
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
  PERCENT_TUMOR("Percent Tumor Nuclei", Integer.class, NON_PHI)
  {
    @Override
    public Predicate
        buildSinglePredicate(CriteriaBuilder cb,
            From<?, AliquotAnnotation> source, SearchOperator operator,
            Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(AliquotAnnotation_.percentNuclei));
      }
      else
      {
        Integer intValue = null;

        if (value instanceof Integer)
        {
          intValue = (Integer) value;
        }
        else
        {
          intValue = Integer.parseInt(value.toString());
        }
        predicate =
            operator.buildWhere(cb,
                source.get(AliquotAnnotation_.percentNuclei), intValue);
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
  PARENT("Parent Specimen", SpecimenAnnotation.class, NON_PHI)
  {
    @Override
    public SearchCriteria<SpecimenAnnotation> getSearchCriteria()
    {
      SearchCriteria<SpecimenAnnotation> search =
          new SearchCriteria<SpecimenAnnotation>(SpecimenAnnotation.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      return search;
    }

    @Override
    public Predicate
        buildSinglePredicate(CriteriaBuilder cb,
            From<?, AliquotAnnotation> source, SearchOperator operator,
            Object value)
    {
      return SearchOperation.OR.combinePredicates(cb, operator.buildWhere(cb,
          source.get(AliquotAnnotation_.parent), (SpecimenAnnotation) value),
          operator.buildWhere(cb,
              source.get(AliquotAnnotation_.specimenFields),
              (SpecimenAnnotation) value));
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder cb,
        From<?, AliquotAnnotation> source, SearchCriteria<?> values)
    {
      Predicate predicate = null;
      List<?> searchValues = values.getValues();
      List<Object> searchValues2 = new ArrayList<Object>(values.getValues());
      int divisions =
          new Double(Math.ceil(searchValues.size() / 1000.0D)).intValue();
      int start = 0;
      int end = 999;
      for (int i = 0; i < divisions; i++)
      {
        int index = Math.min(end, searchValues.size());

        List<?> sublist = searchValues.subList(start, index);
        predicate =
            SearchOperation.OR.combinePredicates(
                cb,
                predicate,
                source.get(AliquotAnnotation_.parent).in(
                    sublist));       

        start = end + 1;
        end = end + 1000;

      }

      return predicate;
    }

    @Override
    public Converter getValueConverter()
    {
      return Resources.lookup(ObjectConverter.class,
          new AnnotationLiteral<Default>()
          {
          });
    }
  };

  private String displayName;

  @Override
  public String toString()
  {
    return this.displayName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.data.SearchFields#buildPredicate(javax.
   * persistence.criteria.CriteriaBuilder, javax.persistence.criteria.From,
   * edu.bcm.dldcc.big.acquire.query.data.SearchCriteria)
   */
  @Override
  public Predicate buildPredicate(CriteriaBuilder cb,
      From<?, AliquotAnnotation> source, SearchCriteria<?> values)
  {
    Predicate predicate = null;
    List<?> searchValues = values.getValues();
    List<SearchOperator> operators = values.getSearchOperators();
    for (int i = 0; i < searchValues.size(); i++)
    {
      predicate =
          SearchOperation.OR.combinePredicates(cb, predicate, this
              .buildSinglePredicate(cb, source, operators.get(i),
                  searchValues.get(i)));
    }

    return predicate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.data.SearchFields#getFromClass()
   */
  @Override
  public Class<AliquotAnnotation> getFromClass()
  {
    return AliquotAnnotation.class;
  }

  abstract protected Predicate buildSinglePredicate(CriteriaBuilder cb,
      From<?, AliquotAnnotation> source, SearchOperator operator, Object value);

  private Class<?> valueClass;

  private List<?> permissibleValues;

  private DataVisibility visibility;

  private AliquotSearchFields(String displayName, Class<?> valueType,
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
    return new EnumConverter(AliquotSearchFields.class);
  }

}
