/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import static edu.bcm.dldcc.big.acquire.util.DataVisibility.NON_PHI;

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.convert.BigDecimalConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.EnumConverter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.security.Authorizations;
import edu.bcm.dldcc.big.acquire.util.DataVisibility;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation_;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author pew
 * 
 */
public enum NaLabSearchFields implements
    SearchFields<From<?, NaLabAnnotation>, NaLabAnnotation>
{
  TYPE("Derivative Type", DerivativeType.class, NON_PHI)
  {

    @Override
    public SearchCriteria<DerivativeType> getSearchCriteria()
    {
      SearchCriteria<DerivativeType> search = new SearchCriteria<DerivativeType>(
          DerivativeType.class);
      search.setSingleOperator(false);
      return search;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, NaLabAnnotation> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(NaLabAnnotation_.type));
      }
      else
      {
        predicate = operator.buildWhere(cb,
                source.get(NaLabAnnotation_.type), (DerivativeType) value);
      }

      return predicate;
    }

    @Override
    public Converter getValueConverter()
    {
      return new EnumConverter(DerivativeType.class);
    }

  },
  QUALITY("DNA Quality", DnaQuality.class, NON_PHI)
  {

    @Override
    public SearchCriteria<DnaQuality> getSearchCriteria()
    {
      SearchCriteria<DnaQuality> search = new SearchCriteria<DnaQuality>(
          DnaQuality.class);
      search.setSingleOperator(false);
      return search;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, NaLabAnnotation> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(NaLabAnnotation_.quality));
      }
      else
      {
        predicate = operator.buildWhere(cb,
                source.get(NaLabAnnotation_.quality), (DnaQuality) value);
      }

      return predicate;
    }
    
    @Override
    public Converter getValueConverter()
    {
      return new EnumConverter(DnaQuality.class);
    }
    
  },
  RIN("RIN #", BigDecimal.class, NON_PHI)
  {

    @Override
    public SearchCriteria<BigDecimal> getSearchCriteria()
    {
      SearchCriteria<BigDecimal> search = new SearchCriteria<BigDecimal>(
          BigDecimal.class);
      search.setSingleOperator(false);
      return search;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, NaLabAnnotation> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(NaLabAnnotation_.rin));
      }
      else
      {
        predicate = operator.buildWhere(cb,
                source.get(NaLabAnnotation_.rin), (BigDecimal) value);
      }

      return predicate;
    }
    
    @Override
    public Converter getValueConverter()
    {
      return new BigDecimalConverter();
    }
    
  };

  private String displayName;

  @Override
  public String toString()
  {
    return this.displayName;
  }

  @Override
  public Class<NaLabAnnotation> getFromClass()
  {
    return NaLabAnnotation.class;
  }

  private Class<?> valueClass;

  private List<?> permissibleValues;

  private DataVisibility visibility;

  private NaLabSearchFields(String displayName, Class<?> valueType,
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
    Authorizations auth = Resources.lookup(Authorizations.class,
        new AnnotationLiteral<Default>()
        {
        });
    return auth.isDataVisibleForProgram(this.visibility);
  }

  @Override
  public Predicate buildPredicate(CriteriaBuilder cb,
      From<?, NaLabAnnotation> source, SearchCriteria<?> values)
  {
    Predicate predicate = cb.disjunction();
    List<?> searchValues = values.getValues();
    List<SearchOperator> operators = values.getSearchOperators();
    for (int i = 0; i < searchValues.size(); i++)
    {
      predicate = cb.or(
          predicate,
          this.buildSinglePredicate(cb, source, operators.get(i),
              searchValues.get(i)));
    }

    return predicate;
  }

  abstract protected Predicate buildSinglePredicate(CriteriaBuilder cb,
      From<?, NaLabAnnotation> source, SearchOperator operator, Object value);

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.search.SearchFields#getFieldConverter()
   */
  @Override
  public Converter getFieldConverter()
  {
    return new EnumConverter(NaLabSearchFields.class);
  }

}
