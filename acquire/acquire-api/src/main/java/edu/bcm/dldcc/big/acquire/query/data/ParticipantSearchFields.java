/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import static edu.bcm.dldcc.big.acquire.util.DataVisibility.PHI;
import static edu.bcm.dldcc.big.acquire.util.DataVisibility.PUBLIC;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
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
import edu.bcm.dldcc.big.converter.DummyStringConverter;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.Race;

/**
 * @author pew
 * 
 */
public enum ParticipantSearchFields implements
    SearchFields<From<?, Participant>, Participant>
{
  MRN("Patient MRN", String.class, PHI)
  {

    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Participant> source, Object value)
    {
      return SearchOperator.LIKE.buildWhere(
          cb,
          cb.lower(source.<Participant, ParticipantMedicalIdentifier> join(
              "participantMedicalIdentifierCollection").<String> get(
              "medicalRecordNumber")), "%" + value.toString().toLowerCase()
              + "%");
    }


  },
  RACE("Race", String.class, PUBLIC)
  {
    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Participant> source, Object value)
    {
      return SearchOperator.LIKE.buildWhere(cb,
          cb.lower(source.<Participant, Race> join("raceCollection")
              .<String> get("raceName")), value.toString().toLowerCase());
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.LIKE);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<String>) this.getPermissibleValues());
      return search;
    }
    
  },
  ETHNICITY("Ethnicity", String.class, PUBLIC)
  {
    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Participant> source, Object value)
    {
      return SearchOperator.EQ.buildWhere(cb, cb.lower(source
          .<String> get("ethnicity")), value.toString().toLowerCase());
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<String>) this.getPermissibleValues());
      return search;
    }

  },
  GENDER("Gender", String.class, PUBLIC)
  {
    @Override
    protected Predicate buildSinglePredicate(CriteriaBuilder cb,
        From<?, Participant> source, Object value)
    {
      return SearchOperator.EQ.buildWhere(cb, cb.lower(source
          .<String> get("gender")), value.toString().toLowerCase());
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchCriteria<String> getSearchCriteria()
    {
      SearchCriteria<String> search = new SearchCriteria<String>(String.class);
      search.setSingleOperator(true);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      search.setHasPermissibleValues(true);
      search.setPermissibleValues((List<String>) this.getPermissibleValues());
      return search;
    }

  };

  public Class<Participant> getFromClass()
  {
    return Participant.class;
  }

  private String displayName;

  @Override
  public String toString()
  {
    return this.displayName;
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

  @Override
  public Predicate buildPredicate(CriteriaBuilder cb,
      From<?, Participant> source, SearchCriteria<?> values)
  {
    Predicate predicate = null;
    for (Object value : values.getValues())
    {
      predicate = SearchOperation.OR.combinePredicates(cb, predicate,
          this.buildSinglePredicate(cb, source, value));
    }

    return predicate;
  }

  protected abstract Predicate buildSinglePredicate(CriteriaBuilder cb,
      From<?, Participant> source, Object value);

  private Class<?> valueClass;

  private List<?> permissibleValues;

  private DataVisibility visibility;

  private ParticipantSearchFields(String displayName, Class<?> valueType,
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
