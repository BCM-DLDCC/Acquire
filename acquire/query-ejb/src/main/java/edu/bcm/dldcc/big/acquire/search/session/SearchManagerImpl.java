package edu.bcm.dldcc.big.acquire.search.session;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.listener.MinerSearchTerm;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.qualifiers.OperationsLiteral;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.AliquotSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.NaLabSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.ParticipantSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.ShipmentSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation_;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.inventory.entity.EntityMap_;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.SpecimenPosition;

@Stateful
@ConversationScoped
@Named("searchManager")
/**
 * @author pew
 *
 */
public class SearchManagerImpl implements SearchManager, Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 1513430878418889841L;

  protected EntityManager annotationEm;

  protected AnnotationLiteral emType;

  @Inject
  @Any
  private Instance<EntityManager> caTissueEms;

  @Inject
  private EntityResolver resolver;

  @Inject
  private Utilities util;

  private Long queryCount = 0L;

  private List<SearchResult> queryResult = new ArrayList<SearchResult>();

  private Boolean includeAliquots = false;

  private Boolean includeNormals = false;

  private Boolean includeClosedOrDisabled = false;

  private SearchOperation operation = SearchOperation.AND;

  private Map<SearchFields<?, ?>, SearchCriteria<?>> specimenFieldValues =
      new HashMap<SearchFields<?, ?>, SearchCriteria<?>>();

  private Map<SpecimenSearchFields, SearchCriteria<?>> specimenInternalFields =
      new EnumMap<SpecimenSearchFields, SearchCriteria<?>>(
          SpecimenSearchFields.class);

  private Map<AnnotationSearchFields, SearchCriteria<?>> specimenAnnotationFields =
      new EnumMap<AnnotationSearchFields, SearchCriteria<?>>(
          AnnotationSearchFields.class);

  private Map<AliquotSearchFields, SearchCriteria<?>> specimenAliquotFields =
      new EnumMap<AliquotSearchFields, SearchCriteria<?>>(
          AliquotSearchFields.class);

  private Map<NaLabSearchFields, SearchCriteria<?>> naLabFields =
      new EnumMap<NaLabSearchFields, SearchCriteria<?>>(NaLabSearchFields.class);

  private Map<SpecimenSearchFields, SearchCriteria<?>> normalFieldValues =
      new HashMap<SpecimenSearchFields, SearchCriteria<?>>();

  private Map<SearchFields<?, ?>, SearchCriteria<?>> patientFieldValues =
      new HashMap<SearchFields<?, ?>, SearchCriteria<?>>();

  private Map<SpecimenSearchFields, SearchCriteria<?>> patientSpecimenFields =
      new EnumMap<SpecimenSearchFields, SearchCriteria<?>>(
          SpecimenSearchFields.class);

  private Map<AnnotationSearchFields, SearchCriteria<?>> patientAnnotationFields =
      new EnumMap<AnnotationSearchFields, SearchCriteria<?>>(
          AnnotationSearchFields.class);
  private Map<ParticipantSearchFields, SearchCriteria<?>> patientParticipantFields =
      new EnumMap<ParticipantSearchFields, SearchCriteria<?>>(
          ParticipantSearchFields.class);

  private List<SearchFields<?, ?>> patientFields =
      new ArrayList<SearchFields<?, ?>>();

  private List<SearchFields<?, ?>> normalFields =
      new ArrayList<SearchFields<?, ?>>();

  private List<SearchFields<?, ?>> specimenFields =
      new ArrayList<SearchFields<?, ?>>();

  private static final String NORMAL_DESIGNATOR = "%Normal%";

  private SearchFields<?, ?> specimenField;

  public SearchManagerImpl()
  {
    super();
  }

  /**
   * 
   */
  @Inject
  public SearchManagerImpl(@Annotations @Operations EntityManager manager)
  {
    this();
    this.annotationEm = manager;
    this.emType = new OperationsLiteral();
  }

  @PostConstruct
  public void init()
  {
    this.specimenFields.add(SpecimenSearchFields.LABEL);
    this.specimenFields.add(SpecimenSearchFields.TUMOR_TYPE);
    this.specimenFields.add(SpecimenSearchFields.AMOUNT);
    this.specimenFields.add(AnnotationSearchFields.TUMOR_STAGE);
    this.specimenFields.add(AnnotationSearchFields.TUMOR_GRADE);
    this.specimenFields.add(AliquotSearchFields.PERCENT_NECROSIS);
    this.specimenFields.add(AliquotSearchFields.PERCENT_TUMOR);
    this.specimenFields.add(AnnotationSearchFields.PRIOR_TREATMENT);
    this.specimenFields.add(AnnotationSearchFields.WARM_ISCHEMIA);

    this.normalFields.add(SpecimenSearchFields.NORMAL_PRESENT);
    this.normalFields.add(SpecimenSearchFields.TYPE);
    this.normalFields.add(SpecimenSearchFields.LABEL);
    this.normalFields.add(SpecimenSearchFields.AMOUNT);

    this.patientFields.add(ParticipantSearchFields.MRN);
    this.patientFields.add(ParticipantSearchFields.RACE);
    this.patientFields.add(ParticipantSearchFields.ETHNICITY);
    this.patientFields.add(ParticipantSearchFields.GENDER);
    this.patientFields.add(SpecimenSearchFields.DIAGNOSIS);
    this.patientFields.add(SpecimenSearchFields.DISEASE_SITE);
    this.patientFields.add(AnnotationSearchFields.BINNED_COLLECTION_AGE);
    this.patientFields.add(SpecimenSearchFields.SPECIMEN_COLLECTION_SITE);
  }

  // Apollo TEST will remove
  @Produces
  @Named("maxUiResults")
  public Long maxUiResults()
  {
    return 2000L;
  }

  /**
   * For reset TODO make this type-safe
   */
  public void reset()
  {
    this.clearFields();
    this.clearResults();
  }

  /**
   * TEST will removed Servers as submit action, so that terms are available for
   * next page * TODO make this type-safe
   * 
   * @author amcowiti
   */
  public String collectTerms()
  {
    queryResult.clear();
    runCount();
    return "minerResult?faces-redirect=true";
  }

  /**
   * Convenience method to prepare the search parameters for next UI Will remove
   * 
   * @author amcowiti
   */
  public String getTerms()
  {

    StringBuilder terms = new StringBuilder();
    Map<SearchFields<?, ?>, SearchCriteria<?>> pmap = getPatientFieldValues();
    Map<SearchFields<?, ?>, SearchCriteria<?>> smap = getSpecimenFieldValues();
    Map<SearchFields<?, ?>, SearchCriteria<?>> nmap = getNormalFieldValues();

    if (pmap != null && pmap.size() > 0)
      terms.append(getSearchTerms(pmap));

    if (smap != null && smap.size() > 0)
    {
      if (terms.length() != 0)
        terms.append(getOperation().name()).append(" ");
      terms.append(getSearchTerms(smap));
    }
    if (nmap != null && nmap.size() > 0)
    {
      if (terms.length() != 0)
        terms.append(getOperation().name()).append(" ");
      terms.append(getSearchTerms(nmap));
    }

    if (terms.length() == 0)
    {
      MinerListener.log.info("No search terms ...");
    }
    return (terms.length() > 0) ? terms.toString() : MinerSearchTerm.NA
        .toString();
  }

  /**
   * Get Search Term for Given Map ( Search Section)
   * 
   * @param pmap
   * @return
   */
  private StringBuilder getSearchTerms(
      Map<SearchFields<?, ?>, SearchCriteria<?>> pmap)
  {
    SearchFields<?, ?> key;
    SearchCriteria<?> val;
    List<SearchOperator> ops;
    List<?> vals;
    StringBuilder opsb = new StringBuilder();
    String operator = "";
    int fieldCount = 0;
    int fCount = 0;

    boolean hasNoKey = false;
    if (pmap.size() > 0)
    {

      for (Map.Entry<SearchFields<?, ?>, SearchCriteria<?>> entry : pmap
          .entrySet())
      {
        key = entry.getKey();
        hasNoKey = (key == null || key.toString().trim().equals("")); // this
                                                                      // check
                                                                      // necessary
                                                                      // for
                                                                      // precaution

        if (hasNoKey)
          continue;

        val = entry.getValue();

        ops = val.getSearchOperators();

        // FIXME need logic for when you have multiple ops
        if (ops != null)
          for (SearchOperator op : ops)
          {
            operator = op.toString();
          }

        vals = val.getValues();

        /*
         * if (vals != null)
         * MinerListener.log.info(" #  selected values(vals) List size =" +
         * vals.size());
         */

        int count = vals.size();
        int cnt = 0;
        for (Object obj : vals)
        {
          opsb.append(key).append(" ");
          opsb.append(operator).append(" ");

          if (obj != null)
            opsb.append(obj.toString()).append(" ");
          if (++cnt < count)
            opsb.append(getOperation().name()).append(" ");
          /*
           * if (obj != null) MinerListener.log.info("value type=" +
           * obj.getClass().getName() + "value==" + obj.toString());
           */
        }

        if (!hasNoKey && (++fCount < fieldCount))
          opsb.append(getOperation().name()).append(" ");
      }
    }

    return opsb;
  }

  // end TEST apollo remove

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#runCount()
   */
  @Override
  public Long runCount()
  {
    this.queryCount = 0L;
    CriteriaBuilder cb = annotationEm.getCriteriaBuilder();
    for (CaTissueInstance instance : EnumSet.allOf(CaTissueInstance.class))
    {
      Map<Long, Specimen> caTissueResults = this.buildSpecimenQuery(instance);
      if (!caTissueResults.isEmpty())
      {
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        this.setupFromClause(criteria);
        for (Root<?> root : criteria.getRoots())
        {
          if (AliquotAnnotation.class.isAssignableFrom(root.getJavaType()))
          {
            criteria.select(cb.countDistinct(root));
          }
        }

        TypedQuery<Long> query =
            this.buildAcquireQuery(caTissueResults.keySet(), instance,
                criteria, true);

        this.queryCount += query.getSingleResult();

      }
    }
    return this.queryCount;
  }

  private Map<Long, Specimen> buildSpecimenQuery(CaTissueInstance instance)
  {
    EntityManager instanceEm =
        caTissueEms.select(new CaTissueLiteral(instance), this.emType).get();
    CriteriaBuilder cb = instanceEm.getCriteriaBuilder();
    CriteriaQuery<Specimen> criteria = cb.createQuery(Specimen.class);
    Root<Specimen> root = criteria.from(Specimen.class);
    criteria.distinct(true).select(root);
    this.setupFetchJoins(root);

    Predicate where = processClosedOrDisabled(cb, criteria, root);

    where = this.includeAliquotsAndNormals(cb, root, where);

    where = this.processSpecimenSearchFields(cb, root, where);

    where = this.processParticipantSearchFields(cb, criteria, root, where);

    where = this.processNormalSearchFields(cb, criteria, root, where);

    // add where clause to criteria
    criteria.where(where);

    TypedQuery<Specimen> query = instanceEm.createQuery(criteria);
    List<Specimen> specimenList = query.getResultList();

    Map<Long, Specimen> specimenMap = new HashMap<Long, Specimen>();

    for (Specimen current : specimenList)
    {
      specimenMap.put(current.getId(), current);
    }

    return specimenMap;

  }

  private Predicate processClosedOrDisabled(CriteriaBuilder cb,
      CriteriaQuery<Specimen> criteria, Root<Specimen> root)
  {
    /*
     * Don't include disabled Specimen
     */
    Predicate where =
        cb.and(cb.notEqual(cb.lower(root.<String> get("activityStatus")),
            "disabled"), cb.equal(
            cb.lower(root.<String> get("collectionStatus")), "collected"));
    if (!this.getIncludeClosedOrDisabled())
    {
      /*
       * Build subquery to find aliquots/derivatives that still have available
       * quantity
       */
      Subquery<Long> aliquotSq = criteria.subquery(Long.class);
      Root<Specimen> sqRoot = aliquotSq.correlate(root);
      Root<AbstractSpecimen> superRoot = aliquotSq.from(AbstractSpecimen.class);
      aliquotSq.select(sqRoot.<Long> get("id"));
      aliquotSq.where(cb.and(
          cb.equal(sqRoot.<Long> get("id"), superRoot.<Long> get("id")),
          cb.gt(root.<Integer> get("availableQuantity"), 0)));

      /*
       * Also need a subquery to find all aliquots/derivatives, to be used in a
       * not exist clause to account for specimen with no children
       */
      Subquery<Long> childrenSq = criteria.subquery(Long.class);
      Root<Specimen> childrenRoot = childrenSq.correlate(root);
      Join<Specimen, Specimen> childJoin =
          childrenRoot.<Specimen, Specimen> join("childSpecimenCollection");
      childrenSq.select(childJoin.<Long> get("id"));

      where =
          cb.and(where, cb.and(cb.notEqual(
              cb.lower(root.<String> get("activityStatus")), "closed"), cb.or(
              cb.gt(root.<Integer> get("availableQuantity"), 0),
              cb.or(cb.exists(childrenSq), cb.not(cb.exists(childrenSq))))));
    }
    return where;
  }

  private Predicate processNormalSearchFields(CriteriaBuilder cb,
      CriteriaQuery<Specimen> criteria, Root<Specimen> root, Predicate where)
  {
    Join<SpecimenCollectionGroup, CollectionProtocolRegistration> collectionProtocolJoin =
        root.join("specimenCollectionGroup").join(
            "collectionProtocolRegistration");

    Subquery<Specimen> normalSq = criteria.subquery(Specimen.class);
    Join<SpecimenCollectionGroup, CollectionProtocolRegistration> join =
        normalSq.correlate(collectionProtocolJoin);
    Join<SpecimenCollectionGroup, Specimen> specimenJoin =
        join.<CollectionProtocolRegistration, SpecimenCollectionGroup> join(
            "specimenCollectionGroupCollection")
            .<SpecimenCollectionGroup, Specimen> join("specimenCollection");
    normalSq.select(specimenJoin);

    Predicate presentWhere =
        SpecimenSearchFields.NORMAL_PRESENT.buildPredicate(cb, specimenJoin,
            SpecimenSearchFields.NORMAL_PRESENT.getSearchCriteria());
    Boolean normalPresent = null;
    List<?> normalPresentValues = new ArrayList<Object>();
    if (this.getNormalFieldValues().containsKey(
        SpecimenSearchFields.NORMAL_PRESENT))
    {
      SearchCriteria<?> normalPresentCriteria =
          this.getNormalFieldValues().get(SpecimenSearchFields.NORMAL_PRESENT);
      normalPresentValues = normalPresentCriteria.getValues();
    }

    /*
     * Temporary fix for JSF setting in strings. Better solution needs to be
     * worked out for next version
     */
    if (!normalPresentValues.isEmpty())
    {
      normalPresent = Boolean.valueOf(normalPresentValues.get(0).toString());
    }
    if (normalPresent != null && !normalPresent)
    {
      normalSq.where(presentWhere);
      where =
          this.getOperation()
              .combinePredicates(cb, cb.not(cb.exists(normalSq)));
    }
    else
    {
      boolean normalValues = false;
      Predicate nWhere = cb.conjunction();
      for (SpecimenSearchFields normalField : normalFieldValues.keySet())
      {
        if (normalField == SpecimenSearchFields.NORMAL_PRESENT)
        {
          break;
        }
        if (!this.normalFieldValues.get(normalField).getValues().isEmpty())
        {
          normalValues = true;
          nWhere =
              this.getOperation().combinePredicates(
                  cb,
                  nWhere,
                  normalField.buildPredicate(cb, specimenJoin,
                      normalFieldValues.get(normalField)));
        }
      }

      if ((normalPresent != null && normalPresent) || normalValues)
      {
        normalSq.where(cb.and(presentWhere, nWhere, cb.notEqual(
            cb.lower(specimenJoin.<String> get("activityStatus")), "disabled"),
            cb.equal(cb.lower(specimenJoin.<String> get("collectionStatus")),
                "collected")));
        where =
            this.getOperation().combinePredicates(cb, where,
                cb.exists(normalSq));
      }

    }
    return where;
  }

  private Predicate processParticipantSearchFields(CriteriaBuilder cb,
      CriteriaQuery<Specimen> criteria, Root<Specimen> root, Predicate where)
  {
    Subquery<Participant> participantSubquery =
        criteria.subquery(Participant.class);
    Root<Participant> participantRoot =
        participantSubquery.from(Participant.class);
    participantSubquery.select(participantRoot);

    Predicate pWhere = null;
    if (this.getIncludeClosedOrDisabled())
    {
      pWhere = cb.conjunction();
    }
    else
    {
      pWhere =
          cb.and(cb.notEqual(
              cb.lower(participantRoot.<String> get("activityStatus")),
              "closed"), cb.notEqual(
              cb.lower(participantRoot.<String> get("activityStatus")),
              "disabled"));
    }
    boolean participantValue = false;
    for (ParticipantSearchFields patientField : this.patientParticipantFields
        .keySet())
    {
      if (!this.patientParticipantFields.get(patientField).getValues()
          .isEmpty())
      {
        participantValue = true;
        pWhere =
            this.getOperation().combinePredicates(
                cb,
                pWhere,
                patientField.buildPredicate(cb, participantRoot,
                    this.patientParticipantFields.get(patientField)));
      }
    }

    if (participantValue)
    {
      participantSubquery.where(pWhere);
      where =
          this.getOperation().combinePredicates(
              cb,
              where,
              cb.in(
                  root.<SpecimenCollectionGroup> get("specimenCollectionGroup")
                      .<CollectionProtocolRegistration> get(
                          "collectionProtocolRegistration")
                      .<Participant> get("participant")).value(
                  participantSubquery));
    }
    return where;
  }

  private Predicate processSpecimenSearchFields(CriteriaBuilder cb,
      Root<Specimen> root, Predicate where)
  {
    for (SpecimenSearchFields searchField : specimenInternalFields.keySet())
    {
      if (!specimenInternalFields.get(searchField).getValues().isEmpty())
      {
        where =
            this.getOperation().combinePredicates(
                cb,
                where,
                searchField.buildPredicate(cb, root,
                    specimenInternalFields.get(searchField)));
      }
    }

    for (SpecimenSearchFields patientField : this.patientSpecimenFields
        .keySet())
    {
      if (!this.patientSpecimenFields.get(patientField).getValues().isEmpty())
      {
        where =
            this.getOperation().combinePredicates(
                cb,
                where,
                patientField.buildPredicate(cb, root,
                    this.patientSpecimenFields.get(patientField)));
      }
    }
    return where;
  }

  private void setupFetchJoins(Root<Specimen> root)
  {
    Fetch<Specimen, SpecimenCollectionGroup> scgFetch =
        root.<Specimen, SpecimenCollectionGroup> fetch("specimenCollectionGroup");
    scgFetch
        .<SpecimenCollectionGroup, CollectionProtocolRegistration> fetch(
            "collectionProtocolRegistration")
        .<CollectionProtocolRegistration, Participant> fetch("participant")
        .<Participant, ParticipantMedicalIdentifier> fetch(
            "participantMedicalIdentifierCollection");
    root.<Specimen, SpecimenPosition> fetch("specimenPosition", JoinType.LEFT);
    scgFetch.fetch("deIdentifiedSurgicalPathologyReport", JoinType.LEFT);
    scgFetch.fetch("identifiedSurgicalPathologyReport", JoinType.LEFT);
  }

  private Predicate includeAliquotsAndNormals(CriteriaBuilder cb,
      Root<Specimen> root, Predicate where)
  {
    if (!this.includeNormals)
    {
      where =
          cb.and(where, SearchOperator.NOTLIKE.buildWhere(cb, root
              .<SpecimenCollectionGroup> get("specimenCollectionGroup")
              .<CollectionProtocolEvent> get("collectionProtocolEvent")
              .<String> get("collectionPointLabel"),
              SearchManagerImpl.NORMAL_DESIGNATOR));
    }

    if (!this.includeAliquots)
    {
      Predicate lineageWhere =
          SearchOperator.EQ.buildWhere(cb,
              cb.lower(root.<String> get("lineage")), "new");
      root.fetch("parentSpecimen", JoinType.LEFT);

      where = cb.and(where, lineageWhere);
    }
    return where;
  }

  /**
   * @return the operation for this query
   */
  public SearchOperation getOperation()
  {
    return this.operation;
  }

  /**
   * @param searchOperation
   *          the searchAnd to set
   */
  public void setOperation(SearchOperation searchOperation)
  {
    this.operation = searchOperation;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#runQuery()
   */
  @Override
  public List<SearchResult> runQuery()
  {
    this.queryResult.clear();
    for (CaTissueInstance instance : EnumSet.allOf(CaTissueInstance.class))
    {
      Map<Long, Specimen> caTissueResults = this.buildSpecimenQuery(instance);
      if (!caTissueResults.isEmpty())
      {
        CriteriaBuilder cb = annotationEm.getCriteriaBuilder();
        CriteriaQuery<AliquotAnnotation> criteria =
            cb.createQuery(AliquotAnnotation.class);
        Root<AliquotAnnotation> root = criteria.from(AliquotAnnotation.class);

        criteria.distinct(true);

        criteria.select(root);
        root.fetch(AliquotAnnotation_.specimenFields, JoinType.LEFT);
        root.fetch(AliquotAnnotation_.map);
        TypedQuery<AliquotAnnotation> query = this.buildAcquireQuery(caTissueResults.keySet(), instance,
            criteria, true);
        List<AliquotAnnotation> acquireResults =
            query.getResultList();

        this.queryResult.addAll(this.compileResults(acquireResults,
            caTissueResults));

      }
    }
    return this.queryResult;
  }

  private List<SearchResult> compileResults(List<AliquotAnnotation> annotation,
      Map<Long, Specimen> specimenMap)
  {
    List<SearchResult> results = new ArrayList<SearchResult>();
    for (AliquotAnnotation current : annotation)
    {
      Specimen specimen = specimenMap.get(current.getMap().getEntityId());
      AbstractSpecimen parent = resolver.getAdam(specimen);
      SearchResult currentResult = new SearchResult();
      currentResult.setAnnotation(current);
      currentResult.setSpecimen(specimen);
      if (!specimen.equals(parent))
      {
        currentResult.setParent(parent);
      }
      currentResult.setParticipant(resolver
          .findParticipantForSpecimen(currentResult.getSpecimen()));
      results.add(currentResult);
    }
    return results;
  }

  private <T> void setupFromClause(CriteriaQuery<T> criteria)
  {
    criteria.from(AliquotAnnotation.class).alias("aliquot");
  }

  @SuppressWarnings("unchecked")
  private <T> TypedQuery<T> buildAcquireQuery(Set<Long> specimenIdSet,
      CaTissueInstance instance, CriteriaQuery<T> criteria, boolean multi)
  {
    List<Long> specimenIds = new ArrayList<Long>(specimenIdSet);
    CriteriaBuilder cb = annotationEm.getCriteriaBuilder();
    Root<AliquotAnnotation> annotation = null;

    for (Root<?> root : criteria.getRoots())
    {
      if (AliquotAnnotation.class.isAssignableFrom(root.getJavaType()))
      {
        annotation = (Root<AliquotAnnotation>) root;
      }
    }

    Path<EntityMap> mapPath = annotation.get(AliquotAnnotation_.map);

    Predicate predicate = cb.equal(mapPath.get(EntityMap_.caTissue), instance);
    int divisions =
        new Double(Math.ceil(specimenIds.size() / 1000.0D)).intValue();
    int start = 0;
    int end = 999;
    Predicate idPredicate = null;
    for (int i = 0; i < divisions; i++)
    {
      int index = Math.min(end, specimenIds.size());

      idPredicate =
          SearchOperation.OR.combinePredicates(
              cb,
              idPredicate,
              mapPath.get(EntityMap_.entityId).in(
                  specimenIds.subList(start, index)));

      start = end + 1;
      end = end + 1000;

    }
    predicate = cb.and(predicate, idPredicate);

    Predicate searchClause = this.getOperation().getBasePredicate(cb);

    Join<AliquotAnnotation, SpecimenAnnotation> specimenField =
        annotation.join(AliquotAnnotation_.specimenFields, JoinType.LEFT);
    searchClause = processPatientClauses(cb, specimenField, searchClause);

    searchClause = processSpecimenClauses(cb, specimenField, searchClause);

    searchClause =
        processAliquotClause(cb, criteria, specimenField, searchClause);

    criteria.where(cb.and(predicate, searchClause));

    return annotationEm.createQuery(criteria);

  }

  private Predicate processAliquotClause(CriteriaBuilder cb,
      CriteriaQuery<?> criteria,
      Join<AliquotAnnotation, SpecimenAnnotation> annotation,
      Predicate searchClause)
  {
    Subquery<AliquotAnnotation> aliquotSq =
        criteria.subquery(AliquotAnnotation.class);
    From<?, AliquotAnnotation> parentRoot = annotation.getParent();
    Root<AliquotAnnotation> root = aliquotSq.from(AliquotAnnotation.class);
    aliquotSq.select(root);
    Predicate sqWhere =
        cb.or(
            cb.equal(root.get(AliquotAnnotation_.entityId),
                parentRoot.get(AliquotAnnotation_.entityId)),
            cb.equal(annotation, root.get(AliquotAnnotation_.parent)));

    Predicate searchWhere = this.getOperation().getBasePredicate(cb);
    for (AliquotSearchFields field : this.specimenAliquotFields.keySet())
    {
      if (!this.specimenAliquotFields.get(field).getValues().isEmpty())
      {
        searchWhere =
            this.getOperation().combinePredicates(
                cb,
                searchWhere,
                field.buildPredicate(cb, root,
                    this.specimenAliquotFields.get(field)));
      }
    }

    Predicate naWhere =
        this.processNaLabClauses(cb,
            root.join(AliquotAnnotation_.naLabAnnotations, JoinType.LEFT));
    if (!searchWhere.getExpressions().isEmpty()
        || !naWhere.getExpressions().isEmpty())
    {
      Predicate combined =
          this.getOperation().combinePredicates(cb, searchWhere, naWhere);
      aliquotSq.where(cb.and(sqWhere, combined));
      searchClause =
          this.getOperation().combinePredicates(cb, searchClause,
              cb.exists(aliquotSq));
    }
    return searchClause;
  }

  private Predicate processNaLabClauses(CriteriaBuilder cb,
      From<?, NaLabAnnotation> join)
  {
    Predicate naClause = this.getOperation().getBasePredicate(cb);
    for (NaLabSearchFields field : this.naLabFields.keySet())
    {
      if (!this.naLabFields.get(field).getValues().isEmpty())
      {
        naClause =
            this.getOperation().combinePredicates(cb, naClause,
                field.buildPredicate(cb, join, this.naLabFields.get(field)));
      }

    }

    return naClause;

  }

  private Predicate processSpecimenClauses(CriteriaBuilder cb,
      Join<AliquotAnnotation, SpecimenAnnotation> annotation,
      Predicate searchClause)
  {
    for (AnnotationSearchFields field : this.specimenAnnotationFields.keySet())
    {
      if (!this.specimenAnnotationFields.get(field).getValues().isEmpty())
      {
        searchClause =
            this.getOperation().combinePredicates(
                cb,
                searchClause,
                field.buildPredicate(cb, annotation,
                    this.specimenAnnotationFields.get(field)));
      }
    }
    return searchClause;
  }

  private Predicate processPatientClauses(CriteriaBuilder cb,
      Join<AliquotAnnotation, SpecimenAnnotation> annotation,
      Predicate predicate)
  {
    for (AnnotationSearchFields field : this.patientAnnotationFields.keySet())
    {
      if (!this.patientAnnotationFields.get(field).getValues().isEmpty())
      {
        predicate =
            this.getOperation().combinePredicates(
                cb,
                predicate,
                field.buildPredicate(cb, annotation,
                    this.patientAnnotationFields.get(field)));
      }
    }
    return predicate;
  }

  @Override
  public void addSpecimenSearchField(SearchFields<?, ?> field)
  {
    if (field instanceof SpecimenSearchFields)
    {
      this.specimenInternalFields.put((SpecimenSearchFields) field,
          field.getSearchCriteria());
    }
    else if (field instanceof AnnotationSearchFields)
    {
      this.specimenAnnotationFields.put((AnnotationSearchFields) field,
          field.getSearchCriteria());
    }
    else if (field instanceof AliquotSearchFields)
    {
      this.specimenAliquotFields.put((AliquotSearchFields) field,
          field.getSearchCriteria());
    }
    else if (field instanceof NaLabSearchFields)
    {
      this.naLabFields
          .put((NaLabSearchFields) field, field.getSearchCriteria());
    }
    else
    {
      throw new IllegalArgumentException("Invalid SearchField supplied");
    }
  }

  /**
   * @return the fields
   */
  public Map<SearchFields<?, ?>, SearchCriteria<?>> getSpecimenFieldValues()
  {
    this.specimenFieldValues.clear();
    this.specimenFieldValues.putAll(this.specimenInternalFields);
    this.specimenFieldValues.putAll(this.specimenAnnotationFields);
    this.specimenFieldValues.putAll(this.specimenAliquotFields);
    this.specimenFieldValues.putAll(this.naLabFields);
    return this.specimenFieldValues;
  }

  @Override
  public Map<SearchFields<?, ?>, SearchCriteria<?>> getNormalFieldValues()
  {
    Map<SearchFields<?, ?>, SearchCriteria<?>> map =
        new HashMap<SearchFields<?, ?>, SearchCriteria<?>>();
    map.putAll(this.normalFieldValues);
    return map;
  }

  /**
   * @return the fields
   */
  public List<SearchFields<?, ?>> getSpecimenFields()
  {
    return Collections.unmodifiableList(this.specimenFields);
  }

  public <T> List<T> getSpecimenFieldValues(SearchFields<?, ?> field,
      Class<T> type)
  {
    return this.getFieldValues(field, type, this.getSpecimenFieldValues());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#getParticipantFieldValues
   * (edu.bcm.dldcc.big.acquire.query.data.SearchFields, java.lang.Class)
   */
  @Override
  public <T> List<T> getPatientFieldValues(SearchFields<?, ?> field,
      Class<T> type)
  {
    return this.getFieldValues(field, type, this.getPatientFieldValues());
  }

  @Override
  public Map<SearchFields<?, ?>, SearchCriteria<?>> getPatientFieldValues()
  {
    // XXX apollo perhaps not clear, save existing values
    // this.patientFieldValues.clear();
    this.patientFieldValues.putAll(this.patientParticipantFields);
    this.patientFieldValues.putAll(this.patientSpecimenFields);
    this.patientFieldValues.putAll(this.patientAnnotationFields);
    return this.patientFieldValues;
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> getFieldValues(SearchFields<?, ?> field, Class<T> type,
      Map<SearchFields<?, ?>, SearchCriteria<?>> map)
  {
    List<T> returnList = new ArrayList<T>();
    SearchCriteria<?> searchValues = map.get(field);
    if (searchValues != null)
    {
      if (!type.equals(searchValues.getTypeClass()))
      {
        throw new IllegalArgumentException();
      }
      returnList = (List<T>) searchValues.getValues();
    }
    return returnList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#getNormalFieldValues(edu.
   * bcm.dldcc.big.acquire.query.data.SearchFields, java.lang.Class)
   */
  @Override
  public <T> List<T> getNormalFieldValues(SearchFields<?, ?> field,
      Class<T> type)
  {
    return this.getFieldValues(field, type, this.getNormalFieldValues());

  }

  @Override
  public void addPatientSearchField(SearchFields<?, ?> field)
  {
    if (field instanceof ParticipantSearchFields)
    {
      this.patientParticipantFields.put((ParticipantSearchFields) field,
          field.getSearchCriteria());
    }
    else if (field instanceof SpecimenSearchFields)
    {
      this.patientSpecimenFields.put((SpecimenSearchFields) field,
          field.getSearchCriteria());
    }
    else if (field instanceof AnnotationSearchFields)
    {
      this.patientAnnotationFields.put((AnnotationSearchFields) field,
          field.getSearchCriteria());
    }
    else
    {
      throw new IllegalArgumentException("Invalid SearchField supplied");
    }

  }

  @Override
  public void addNormalSearchField(SearchFields<?, ?> field)
  {
    if (field instanceof SpecimenSearchFields)
    {
      this.normalFieldValues.put((SpecimenSearchFields) field,
          field.getSearchCriteria());
    }
    else
    {
      throw new IllegalArgumentException("Invalid SearchField supplied");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#getSearchField()
   */
  @Override
  public SearchFields<?, ?> getSearchField()
  {
    return this.specimenField;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#setSearchField(edu.bcm.dldcc
   * .big.acquire.query.data.SearchFields)
   */
  @Override
  public void setSearchField(SearchFields<?, ?> field)
  {
    this.specimenField = field;
  }

  @Override
  public List<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>>
      getSpecimenEntries()
  {
    return new ArrayList<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>>(this
        .getSpecimenFieldValues().entrySet());
  }

  @Override
  public List<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>>
      getPatientEntries()
  {
    return new ArrayList<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>>(this
        .getPatientFieldValues().entrySet());
  }

  @Override
  public List<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>>
      getNormalEntries()
  {
    return new ArrayList<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>>(this
        .getNormalFieldValues().entrySet());
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#getPatientFields()
   */
  @Override
  public List<SearchFields<?, ?>> getPatientFields()
  {
    return Collections.unmodifiableList(this.patientFields);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#getNormalFields()
   */
  @Override
  public List<SearchFields<?, ?>> getNormalFields()
  {
    return Collections.unmodifiableList(this.normalFields);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#removeSpecimenSearchField
   * (edu.bcm.dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void removeSpecimenSearchField(SearchFields<?, ?> field)
  {
    if (field instanceof SpecimenSearchFields)
    {
      this.specimenInternalFields.remove(field);
    }
    else if (field instanceof AnnotationSearchFields)
    {
      this.specimenAnnotationFields.remove(field);
    }
    else if (field instanceof AliquotSearchFields)
    {
      this.specimenAliquotFields.remove(field);
    }
    else if (field instanceof NaLabSearchFields)
    {
      this.naLabFields.remove(field);
    }
    else
    {
      throw new IllegalArgumentException("Invalid SearchField supplied");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#removePatientSearchField(
   * edu.bcm.dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void removePatientSearchField(SearchFields<?, ?> field)
  {
    if (field instanceof ParticipantSearchFields)
    {
      this.patientParticipantFields.remove((ParticipantSearchFields) field);
    }
    else if (field instanceof SpecimenSearchFields)
    {
      this.patientSpecimenFields.remove((SpecimenSearchFields) field);
    }
    else if (field instanceof AnnotationSearchFields)
    {
      this.patientAnnotationFields.remove((AnnotationSearchFields) field);
    }
    else
    {
      throw new IllegalArgumentException("Invalid SearchField supplied");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#removeNormalSearchField(edu
   * .bcm.dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void removeNormalSearchField(SearchFields<?, ?> field)
  {
    if (field instanceof SpecimenSearchFields)
    {
      this.normalFieldValues.remove((SpecimenSearchFields) field);
    }
    else
    {
      throw new IllegalArgumentException("Invalid SearchField supplied");
    }

  }

  @Override
  public void clearFields()
  {
    this.getSpecimenFieldValues().clear();
    this.specimenAliquotFields.clear();
    this.specimenAnnotationFields.clear();
    this.specimenInternalFields.clear();
    this.getPatientFieldValues().clear();
    this.patientParticipantFields.clear();
    this.patientSpecimenFields.clear();
    this.patientAnnotationFields.clear();
    this.normalFieldValues.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#toggleSpecimenSearchField
   * (edu.bcm.dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void toggleSpecimenSearchField(SearchFields<?, ?> field)
      throws SecurityException, IllegalArgumentException,
      InstantiationException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException

  {
    if (this.getSpecimenFieldValues().containsKey(field))
    {
      this.removeSpecimenSearchField(field);
    }
    else
    {
      this.addSpecimenSearchField(field);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#togglePatientSearchField(
   * edu.bcm.dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void togglePatientSearchField(SearchFields<?, ?> field)
      throws SecurityException, IllegalArgumentException,
      InstantiationException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException

  {
    if (this.getPatientFieldValues().containsKey(field))
    {
      this.removePatientSearchField(field);
    }
    else
    {
      this.addPatientSearchField(field);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#toggleNormalSearchField(edu
   * .bcm.dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void toggleNormalSearchField(SearchFields<?, ?> field)
      throws SecurityException, IllegalArgumentException,
      InstantiationException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException
  {
    if (this.normalFieldValues.containsKey(field))
    {
      this.removeNormalSearchField(field);
    }
    else
    {
      this.addNormalSearchField(field);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#clearResults()
   */
  @Override
  public void clearResults()
  {
    this.queryCount = 0L;
    this.queryResult.clear();
  }

  /**
   * @return the queryCount
   */
  @Produces
  @Named("queryCount")
  public Long getQueryCount()
  {
    return this.queryCount;
  }

  /**
   * @return the queryResult
   */
  @Produces
  @Named("queryResult")
  public List<SearchResult> getQueryResult()
  {
    return this.queryResult;
  }

  public void addAfterSubmitDateSearch(Date timestamp)
  {
    this.addSpecimenSearchField(AnnotationSearchFields.CREATE_DATE);
    SearchCriteria<?> search =
        this.getSpecimenFieldValues().get(AnnotationSearchFields.CREATE_DATE);
    search.setNewOperator(SearchOperator.GTE);
    search.addOperator();
    List<Date> values =
        this.getSpecimenFieldValues(AnnotationSearchFields.CREATE_DATE,
            Date.class);
    values.add(timestamp);
  }

  public void addBeforeSubmitDateSearch(Date timestamp)
  {
    this.addSpecimenSearchField(AnnotationSearchFields.CREATE_DATE);
    SearchCriteria<?> search =
        this.getSpecimenFieldValues().get(AnnotationSearchFields.CREATE_DATE);
    search.setNewOperator(SearchOperator.LTE);
    search.addOperator();
    List<Date> values =
        this.getSpecimenFieldValues(AnnotationSearchFields.CREATE_DATE,
            Date.class);
    values.add(timestamp);
  }

  public void addSiteQuery(SiteAnnotation site)
  {
    this.addSpecimenSearchField(SpecimenSearchFields.SPECIMEN_COLLECTION_SITE_ID);
    List<Long> searchSites =
        this.getSpecimenFieldValues(
            SpecimenSearchFields.SPECIMEN_COLLECTION_SITE_ID, Long.class);
    searchSites.addAll(this.fetchCaTissueIds(site));

  }

  private List<Long> fetchCaTissueIds(SiteAnnotation site)
  {
    CriteriaBuilder cb = annotationEm.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
    Root<EntityMap> root = criteria.from(EntityMap.class);
    criteria.select(root.get(EntityMap_.entityId));
    criteria.where(root.get(EntityMap_.id).in(
        this.getSiteHierarchyAnnotationIds(site)));
    return annotationEm.createQuery(criteria).getResultList();
  }

  private Set<String> getSiteHierarchyAnnotationIds(SiteAnnotation site)
  {
    Set<String> ids = new HashSet<String>();
    ids.add(site.getEntityId());
    for (SiteAnnotation child : site.getChildSites())
    {
      ids.addAll(this.getSiteHierarchyAnnotationIds(child));
    }

    return ids;
  }

  /**
   * @return the includeAliquots
   */
  public Boolean getIncludeAliquots()
  {
    return this.includeAliquots;
  }

  /**
   * @param includeAliquots
   *          the includeAliquots to set
   */
  public void setIncludeAliquots(Boolean includeAliquots)
  {
    this.includeAliquots = includeAliquots;
  }

  /**
   * @return the includeNormals
   */
  public Boolean getIncludeNormals()
  {
    return this.includeNormals;
  }

  /**
   * @param includeNormals
   *          the includeNormals to set
   */
  public void setIncludeNormals(Boolean includeNormals)
  {
    this.includeNormals = includeNormals;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.query.SearchManager#runShipmentSearch()
   */
  @Override
  public List<Shipment> runShipmentSearch(ShipmentSearchFields field,
      SearchCriteria<?> values)
  {
    CriteriaBuilder cb = this.annotationEm.getCriteriaBuilder();
    CriteriaQuery<Shipment> criteria = cb.createQuery(Shipment.class);
    Root<Shipment> root = criteria.from(Shipment.class);
    criteria.select(root);
    criteria.where(field.buildPredicate(cb, root, values));

    return this.annotationEm.createQuery(criteria).getResultList();

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#getIncludeClosedOrDisabled()
   */
  @Override
  public Boolean getIncludeClosedOrDisabled()
  {
    return this.includeClosedOrDisabled;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.query.SearchManager#setIncludeClosedOrDisabled
   * (java.lang.Boolean)
   */
  @Override
  public void setIncludeClosedOrDisabled(Boolean includeClosedDisabled)
  {
    this.includeClosedOrDisabled = includeClosedDisabled;
  }

}
