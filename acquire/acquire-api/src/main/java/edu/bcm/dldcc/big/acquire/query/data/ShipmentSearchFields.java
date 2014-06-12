/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.EnumConverter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.jboss.seam.faces.conversion.ObjectConverter;
import org.joda.time.DateTime;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment_;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.annotations.qualifier.SearchConfiguration;
import edu.bcm.dldcc.big.converter.DummyStringConverter;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation_;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author pew
 * 
 */
public enum ShipmentSearchFields implements
    SearchFields<From<?, Shipment>, Shipment>
{
  CREATION_DATE("Create Date", Date.class)
  {

    @Override
    public SearchCriteria<Date> getSearchCriteria()
    {
      SearchCriteria<Date> criteria = new SearchCriteria<Date>(Date.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.BETWEEN);
      criteria.addOperator();
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Shipment> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(Shipment_.creationDate));
      }
      else if(value instanceof Date)
      {
        DateTime date = new DateTime((Date) value);
        DateTime monthStart = date.dayOfMonth().withMinimumValue();
        DateTime monthEnd = date.dayOfMonth().withMaximumValue();
        predicate =
            operator.buildWhere(cb, source.get(Shipment_.creationDate),
                monthStart.toDate(), monthEnd.toDate());
      }
      
      return predicate;
    }
    
    /* (non-Javadoc)
     * @see edu.bcm.dldcc.big.search.SearchFields#getValueConverter()
     */
    @Override
    public Converter getValueConverter()
    {
      DateTimeConverter converter = new DateTimeConverter();
      DateFormat format = Resources.lookup(DateFormat.class,
          new AnnotationLiteral<SearchConfiguration>()
          {
          });
      converter.setPattern(((SimpleDateFormat) format).toPattern());
      return converter;
    }

  },
  SHIPMENT_TITLE("Title", String.class)
  {

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> criteria = new SearchCriteria<String>(String.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.LIKE);
      criteria.addOperator();
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Shipment> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      Path<String> path = source.get(Shipment_.title);
      if(value == null)
      {
        predicate = cb.isNull(path);
      }
      else
      {
        predicate = operator.buildWhere(cb, path, value.toString());
      }
        
      return predicate;
    }
    
  },
  SHIPMENT_ID("Shipment Id", String.class)
  {

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> criteria = new SearchCriteria<String>(String.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.LIKE);
      criteria.addOperator();
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Shipment> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      Path<String> path = source.get(Shipment_.externalShippingId);
      if(value == null)
      {
        predicate = cb.isNull(path);
      }
      else
      {
        predicate = operator.buildWhere(cb, path, value.toString());
      }
        
      return predicate;
    }
    
  },
  COLLECTION_SITE("Collection Site", SiteAnnotation.class)
  {

    @Override
    @SuppressWarnings("unchecked")
    public SearchCriteria<SiteAnnotation> getSearchCriteria()
    {
      SearchCriteria<SiteAnnotation> search =
          new SearchCriteria<SiteAnnotation>(SiteAnnotation.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<SiteAnnotation>) this.getPermissibleValues());
      return search;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Shipment> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      Path<SiteAnnotation> path = source.get(Shipment_.collectionSite);
      if(value == null)
      {
        predicate = cb.isNull(path);
      }
      else if(value instanceof SiteAnnotation)
      {
        SiteAnnotation searchValue = (SiteAnnotation) value;
        predicate = cb.or(operator.buildWhere(cb, path, searchValue),
            operator.buildWhere(cb, path.get(SiteAnnotation_.parent), 
                searchValue));
      }
      else
      {
        predicate = cb.or(operator.buildWhere(cb, 
            path.get(SiteAnnotation_.name), value.toString()),
            operator.buildWhere(cb, 
                path.get(SiteAnnotation_.parent).get(SiteAnnotation_.name), 
                value.toString()));
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
  
  private Class<?> valueClass;

  private List<?> permissibleValues;


  @Override
  public Predicate buildPredicate(CriteriaBuilder cb, From<?, Shipment> source,
      SearchCriteria<?> values)
  {
    Predicate predicate = cb.disjunction();
    List<?> searchValues = values.getValues();
    List<SearchOperator> operators = values.getSearchOperators();
    for (int i = 0; i < searchValues.size(); i++)
    {
      predicate = this.buildSinglePredicate(cb, source, operators.get(i),
          searchValues.get(i));
    }

    return predicate;
  }

  abstract protected Predicate buildSinglePredicate(CriteriaBuilder cb,
      From<?, Shipment> source, SearchOperator operator, Object value);

  @Override
  public Class<Shipment> getFromClass()
  {
    return Shipment.class;
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

  @Override
  public boolean isVisible() throws IdentityException,
      FeatureNotSupportedException
  {
    return true;
  }

  private ShipmentSearchFields(String title, Class<?> valueType)
  {
    this.displayName = title;
    this.valueClass = valueType;
  }

  private String displayName = "";
  
  @Override
  public String toString()
  {
    return this.displayName;
  }
  
  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.search.SearchFields#getFieldConverter()
   */
  @Override
  public Converter getFieldConverter()
  {
    return new EnumConverter(ParticipantSearchFields.class);
  }
  
  @Override
  public Converter getValueConverter()
  {
    return new DummyStringConverter();
  }

}
