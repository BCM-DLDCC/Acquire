package edu.bcm.dldcc.big.acquire.inventory.session;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CollectionSite;
import edu.bcm.dldcc.big.acquire.qualifiers.DiseaseSite;
import edu.bcm.dldcc.big.acquire.query.Reports;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.acquire.scoreboard.Scoreboard;
import edu.bcm.dldcc.big.acquire.scoreboard.ScoreboardGenerator;
import edu.bcm.dldcc.big.acquire.scoreboard.TumorTimepoint;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;

/**
 * {@inheritDoc}
 * 
 * ScoreboardGenerator compiles the information for the scoreboard into {@link}
 * Scoreboard
 */
@Singleton
@Named("scoreboard")
@ApplicationScoped
public class ScoreboardGeneratorImpl implements ScoreboardGenerator
{
  @Inject
  @Admin
  @Annotations
  private EntityManager annotateEm;

  @Inject
  @CollectionSite
  private List<SiteAnnotation> parentSiteList;

  @Inject
  private Map<String, List<String>> diseaseSiteMap;

  @Inject
  @Admin
  private SearchManager sm;

  private static ReadablePeriod week = Weeks.ONE;
  private static ReadablePeriod twoWeeks = Weeks.TWO;
  private DateTime now;

  private static DecimalFormat percent = new DecimalFormat("###.#%");
  
  @Produces
  @RequestScoped
  @Named("totalScores")
  private static Scoreboard totalScores = new Scoreboard();
  
  @Produces
  @Named("siteScores")
  @RequestScoped
  @CollectionSite
  private static List<Scoreboard> siteScores = new ArrayList<Scoreboard>();
  
  @Produces
  @Named("diseaseScores")
  @RequestScoped
  @DiseaseSite
  private static List<Scoreboard> diseaseScores = new ArrayList<Scoreboard>();
  
  @Produces
  @Named("timepoints")
  @RequestScoped
  private static List<TumorTimepoint> timepoints = new ArrayList<TumorTimepoint>();

  /**
   * Default constructor.
   */
  public ScoreboardGeneratorImpl()
  {
    super();
  }

  

  /**
   * Returns a list of TumorTimepoint objects representing the collection 
   * size over time. Each timepoint represents the collection at the end of 
   * a month, starting with the current month and ending with 11 months previous
   */
  public List<TumorTimepoint> getTimepoints()
  {
    
    List<TumorTimepoint> timepoints = new ArrayList<TumorTimepoint>();

    DateMidnight endOfMonth = now.dayOfMonth().withMaximumValue()
        .toDateMidnight();
    timepoints
    .add(new TumorTimepoint(endOfMonth.minus(Months.ELEVEN).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.TEN).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.NINE).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.EIGHT).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.SEVEN).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.SIX).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.FIVE).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.FOUR).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.THREE).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.TWO).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.minus(Months.ONE).dayOfMonth().withMaximumValue().toDate()));
    timepoints.add(new TumorTimepoint(endOfMonth.toDate()));
    this.sm.setIncludeClosedOrDisabled(true);
    for (TumorTimepoint point : timepoints)
    {
      this.sm.clearFields();
      this.sm.addBeforeSubmitDateSearch(point.getTimepoint());
      point.setTotalSamples(this.sm.runCount());
      Reports.FULLY_QUALIFIED.initReport(this.sm);
      point.setQualifiedSamples(this.sm.runCount());
    }
    ScoreboardGeneratorImpl.timepoints = timepoints;
    this.sm.setIncludeClosedOrDisabled(false);
    return timepoints;
  }

  /**
   * {@inheritDoc}
   * 
   * The scores are collected inside a {@link}Scoreboard object
   * 
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public Scoreboard getTotalScores()
  {
    Scoreboard scores = new Scoreboard();
    scores.setName("Total Scores");

    this.sm.setIncludeClosedOrDisabled(true);
    this.sm.clearFields();
    scores.setTotalSamples(this.sm.runCount());

    this.sm.clearFields();
    Reports.WARM_ISCHEMIA_QUALIFIED.initReport(this.sm);
    scores.setTime(this.sm.runCount());

    this.sm.clearFields();
    Reports.MATCHED_NORMAL_QUALIFIED.initReport(this.sm);
    scores.setMatchedNormal(this.sm.runCount());

    this.sm.clearFields();
    Reports.PERCENT_NECROSIS_QUALIFIED.initReport(this.sm);
    scores.setPercentNecrosis(this.sm.runCount());

    this.sm.clearFields();
    Reports.PERCENT_TUMOR_QUALIFIED.initReport(this.sm);
    scores.setPercentTumor(this.sm.runCount());

    this.sm.clearFields();
    Reports.PRIMARY_UNTREATED_QUALIFIED.initReport(this.sm);
    scores.setUntreated(this.sm.runCount());
    
    this.sm.clearFields();
    Reports.POTENTIAL_QUALIFIED.initReport(this.sm);
    scores.setPotential(this.sm.runCount());

    this.sm.clearFields();
    Reports.FULLY_QUALIFIED.initReport(this.sm);
    scores.setQualified(this.sm.runCount());
    
    this.sm.setIncludeClosedOrDisabled(false);

    ScoreboardGeneratorImpl.totalScores = scores;
    return scores;

  }

  /**
   * {@inheritDoc}
   * 
   * The scores are collected inside a List<Scoreboard>, which is made available
   * to JSF as a DataModel.
   * 
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public List<Scoreboard> getSiteScores()
  {
    now = new DateTime();
    List<Scoreboard> scores = new ArrayList<Scoreboard>();
    this.sm.setIncludeClosedOrDisabled(true);
    for (SiteAnnotation site : parentSiteList)
    {
      site = annotateEm.merge(site);
      Scoreboard siteScoreboard = populateSiteHierarchy(site);

      scores.add(siteScoreboard);

    }
    Collections.sort(scores);
    ScoreboardGeneratorImpl.siteScores = scores;
    this.sm.setIncludeClosedOrDisabled(false);
    return scores;
  }

  private Scoreboard populateSiteHierarchy(SiteAnnotation site)
  {
    
    Scoreboard siteScoreboard = populateSiteScoreboard(site);
    for (SiteAnnotation child : site.getChildSites())
    {
      siteScoreboard.addChild(this.populateSiteHierarchy(child));
    }
    return siteScoreboard;
  }

  public List<Scoreboard> getDiseaseScores()
  {
    List<Scoreboard> scores = new ArrayList<Scoreboard>();
    this.sm.setIncludeClosedOrDisabled(true);
    for (Map.Entry<String, List<String>> disease : diseaseSiteMap.entrySet())
    {
      scores.add(this.populateDiseaseScoreboard(disease.getKey(),
          disease.getValue()));
    }

    this.sm.setIncludeClosedOrDisabled(false);
    ScoreboardGeneratorImpl.diseaseScores = scores;
    
    return scores;
  }

  private Scoreboard populateSiteScoreboard(SiteAnnotation site)
  {
    Scoreboard siteScoreboard = new Scoreboard();
    siteScoreboard.setSite(true);
    siteScoreboard.setName(site.getName());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    siteScoreboard.setTotalSamples(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.WARM_ISCHEMIA_QUALIFIED.initReport(this.sm);
    siteScoreboard.setTime(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.MATCHED_NORMAL_QUALIFIED.initReport(this.sm);
    siteScoreboard.setMatchedNormal(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.PERCENT_NECROSIS_QUALIFIED.initReport(this.sm);
    siteScoreboard.setPercentNecrosis(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.PERCENT_TUMOR_QUALIFIED.initReport(this.sm);
    siteScoreboard.setPercentTumor(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.PRIMARY_UNTREATED_QUALIFIED.initReport(this.sm);
    siteScoreboard.setUntreated(this.sm.runCount());
    
    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.POTENTIAL_QUALIFIED.initReport(this.sm);
    siteScoreboard.setPotential(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    Reports.FULLY_QUALIFIED.initReport(this.sm);
    siteScoreboard.setQualified(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    this.sm.addAfterSubmitDateSearch(this.now.minus(ScoreboardGeneratorImpl.week)
        .toDate());
    siteScoreboard.setLastWeek(this.sm.runCount());

    this.sm.clearFields();
    this.sm.addSiteQuery(site);
    this.sm.addAfterSubmitDateSearch(this.now.minus(
        ScoreboardGeneratorImpl.twoWeeks).toDate());
    siteScoreboard.setTwoWeeks(this.sm.runCount());

    return siteScoreboard;
  }

  private Scoreboard populateDiseaseScoreboard(String disease,
      List<String> terms)
  {
    Scoreboard diseaseScoreboard = new Scoreboard();
    diseaseScoreboard.setName(disease);

    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    diseaseScoreboard.setTotalSamples(this.sm.runCount());

    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.WARM_ISCHEMIA_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setTime(this.sm.runCount());

    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.MATCHED_NORMAL_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setMatchedNormal(this.sm.runCount());

    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.PERCENT_NECROSIS_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setPercentNecrosis(this.sm.runCount());

    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.PERCENT_TUMOR_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setPercentTumor(this.sm.runCount());

    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.PRIMARY_UNTREATED_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setUntreated(this.sm.runCount());
    
    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.POTENTIAL_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setPotential(this.sm.runCount());
    
    this.sm.clearFields();
    this.addDiseaseQuery(terms);
    Reports.FULLY_QUALIFIED.initReport(this.sm);
    diseaseScoreboard.setQualified(this.sm.runCount());
    return diseaseScoreboard;
  }

  private void addDiseaseQuery(List<String> terms)
  {
    this.sm.addSpecimenSearchField(SpecimenSearchFields.DISEASE_SITE);
    List<String> searchTerms = this.sm.getSpecimenFieldValues(
        SpecimenSearchFields.DISEASE_SITE, String.class);
    searchTerms.addAll(terms);
  }

  public String formatPercent(Double value)
  {
    return percent.format(value);
  }



  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.inventory.ScoreboardGenerator#updateScoreboards()
   */
  @Override
  @Consume(uri="jms:queue:scoreboardQueue")
  public void updateScoreboards(Exchange exchange)
  {
	  
    this.getTotalScores();
    this.getSiteScores();
    this.getDiseaseScores();
    this.getTimepoints();
   
  }

}
