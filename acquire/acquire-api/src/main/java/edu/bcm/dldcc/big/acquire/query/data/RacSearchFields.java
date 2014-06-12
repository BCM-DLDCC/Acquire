/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.util.AnnotationLiteral;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.EnumConverter;
import javax.faces.convert.LongConverter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.joda.time.DateTime;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.annotations.qualifier.SearchConfiguration;
import edu.bcm.dldcc.big.converter.DummyStringConverter;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.rac.entity.Application_;
import edu.bcm.dldcc.big.rac.entity.InvestigatorInfo_;
import edu.bcm.dldcc.big.rac.entity.MaterialRequestInformation_;
import edu.bcm.dldcc.big.rac.entity.ProjectInformation_;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author pew
 * 
 */
public enum RacSearchFields implements
    SearchFields<From<?, Application>, Application>
{
  SUBMISSION_DATE("Submission Date")
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
        From<?, Application> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      if (value == null)
      {
        predicate = cb.isNull(source.get(Application_.submissionDate));
      }
      else if(value instanceof Date)
      {
        DateTime date = new DateTime((Date) value);
        DateTime monthStart = date.dayOfMonth().withMinimumValue();
        DateTime monthEnd = date.dayOfMonth().withMaximumValue();
        predicate =
            operator.buildWhere(cb, source.get(Application_.submissionDate),
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
      converter.setTimeZone(TimeZone.getDefault());
      converter.setPattern(((SimpleDateFormat) format).toPattern());
      return converter;
    }

  },
  PROJECT_TITLE("Title")
  {

    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> criteria =
          new SearchCriteria<String>(String.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.LIKE);
      criteria.addOperator();
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Application> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      Path<String> path =
          source.get(Application_.project)
              .get(ProjectInformation_.projectTitle);
      if (value == null)
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
  APPLICATION_ID("Application Id")
  {

    @Override
    public SearchCriteria<Long> getSearchCriteria()
    {
      SearchCriteria<Long> criteria = new SearchCriteria<Long>(Long.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.EQ);
      criteria.addOperator();
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Application> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      Path<Long> path = source.get(Application_.id);
      if (value == null)
      {
        predicate = cb.isNull(path);
      }
      else
      {
        predicate = operator.buildWhere(cb, path, (Long) value);
      }

      return predicate;
    }
    
    @Override
    public Converter getValueConverter()
    {
      return new LongConverter();
    }

  },
  SUBMITTER_LAST_NAME("Submitter Last Name")
  {
    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> criteria =
          new SearchCriteria<String>(String.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.LIKE);
      criteria.addOperator();
      criteria.setSingleValue(true);
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Application> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;
      Path<String> path =
          source.get(Application_.investigator).get(InvestigatorInfo_.lastName);
      if (value == null)
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
  DISEASE_SITE("Disease Site")
  {
    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> criteria =
          new SearchCriteria<String>(String.class);
      criteria.setHasPermissibleValues(false);
      criteria.setSingleOperator(true);
      criteria.setNewOperator(SearchOperator.LIKE);
      criteria.addOperator();
      criteria.setSingleValue(true);
      return criteria;
    }

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Application> source, SearchOperator operator, Object value)
    {
      Predicate predicate = null;

      Path<String> path =
          source.join(Application_.materialRequests).join(
              MaterialRequestInformation_.organSite);

      if (value == null)
      {
        predicate = cb.isNull(path);
      }
      else
      {
        predicate = operator.buildWhere(cb, path, value.toString());
      }

      return predicate;
    }
  };

  @Override
  public Predicate buildPredicate(CriteriaBuilder cb,
      From<?, Application> source, SearchCriteria<?> values)
  {
    Predicate predicate = cb.disjunction();
    List<?> searchValues = values.getValues();
    List<SearchOperator> operators = values.getSearchOperators();
    for (int i = 0; i < searchValues.size(); i++)
    {
      predicate =
          this.buildSinglePredicate(cb, source, operators.get(i),
              searchValues.get(i));
    }

    return predicate;
  }

  abstract protected Predicate buildSinglePredicate(CriteriaBuilder cb,
      From<?, Application> source, SearchOperator operator, Object value);

  @Override
  public Class<Application> getFromClass()
  {
    return Application.class;
  }

  @Override
  public <E> void configurePermissibleValues(List<E> values, Class<E> type)
  {
    //Empty method--none of these have permissible values at this time.
  }

  @Override
  public boolean isVisible() throws IdentityException,
      FeatureNotSupportedException
  {
    return true;
  }

  private RacSearchFields(String title)
  {
    this.displayName = title;
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
