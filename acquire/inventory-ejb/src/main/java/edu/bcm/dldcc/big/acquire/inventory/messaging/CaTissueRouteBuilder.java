package edu.bcm.dldcc.big.acquire.inventory.messaging;

import org.apache.camel.Expression;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spring.SpringRouteBuilder;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;

public class CaTissueRouteBuilder extends SpringRouteBuilder
{

  private String componentName = "tcrbJpa";
  private CaTissueInstance instance = CaTissueInstance.TCRB;
  private static final long SHORT_TIMEOUT = 500;
  private static final long DEFAULT_TIMEOUT = 1000;
  private static final long DEFAULT_DELAY = 500;
  private static final long SPECIMEN_DELAY = 2000;
  private static final long UPDATE_TIMEOUT = 3000;

  public CaTissueRouteBuilder()
  {
    super();
  }

  public CaTissueRouteBuilder(CaTissueInstance instance, String name)
  {
    super();
    this.instance = instance;
    this.componentName = name;
  }

  /**
   * @return the componentName
   */
  protected String getComponentName()
  {
    return this.componentName;
  }

  /**
   * @param componentName
   *          the componentName to set
   */
  protected void setComponentName(String componentName)
  {
    this.componentName = componentName;
  }

  /**
   * @return the instance
   */
  protected CaTissueInstance getInstance()
  {
    return this.instance;
  }

  /**
   * @param instance
   *          the instance to set
   */
  protected void setInstance(CaTissueInstance instance)
  {
    this.instance = instance;
  }

  @Override
  public void configure() throws Exception
  {
    /*
     * Setup error handler
     */
    errorHandler(deadLetterChannel("jms:queue:deadLetterQueue?transferExchange=true"));

    /*
     * New User Route
     */
    this.buildRoute("edu.bcm.dldcc.big.utility.entity.NewUserLog", "",
        "newUserTopic",
        CaTissueRouteBuilder.SHORT_TIMEOUT,
        CaTissueRouteBuilder.DEFAULT_DELAY);

    /*
     * Update User Route
     */
    this.buildRoute("edu.bcm.dldcc.big.utility.entity.UpdateUserLog", "",
        "userChangeTopic",
        CaTissueRouteBuilder.DEFAULT_TIMEOUT,
        CaTissueRouteBuilder.DEFAULT_DELAY);

    /*
     * New Site Route
     */
    this.buildRoute("edu.bcm.dldcc.big.utility.entity.NewSiteLog", "",
        "newSiteTopic",
        CaTissueRouteBuilder.SHORT_TIMEOUT,
        CaTissueRouteBuilder.DEFAULT_DELAY);

    /*
     * New Participant Route
     */
    this.buildRoute("edu.bcm.dldcc.big.utility.entity.NewParticipantLog", "",
        "newParticipantTopic",
        CaTissueRouteBuilder.SHORT_TIMEOUT,
        CaTissueRouteBuilder.DEFAULT_DELAY);

    /*
     * Update Consent Route
     */
    this.triggerScoreboard(this.buildRoute(
        "edu.bcm.dldcc.big.utility.entity.UpdateConsentLog", "",
        "updateConsentTopic",
        CaTissueRouteBuilder.DEFAULT_TIMEOUT,
        CaTissueRouteBuilder.DEFAULT_DELAY));

    /*
     * New Specimen Route
     */
    this.triggerScoreboard(this.buildRoute(
        "edu.bcm.dldcc.big.utility.entity.NewSpecimenLog",
        "",
        "newSpecimenTopic",
        CaTissueRouteBuilder.DEFAULT_TIMEOUT,
        CaTissueRouteBuilder.SPECIMEN_DELAY));

    /*
     * Update Specimen Route
     */
    this.triggerScoreboard(this.buildRoute(
        "edu.bcm.dldcc.big.utility.entity.UpdateSpecimenLog", "",
        "updateSpecimenTopic",
        CaTissueRouteBuilder.DEFAULT_TIMEOUT,
        CaTissueRouteBuilder.SPECIMEN_DELAY
        ));

    /*
     * Dynamic Extension Route
     */
    this.triggerScoreboard(this.buildRoute(
        "edu.bcm.dldcc.big.utility.entity.DynamicExtensionUpdateLog", "",
        "dynamicExtensionTopic",
        CaTissueRouteBuilder.DEFAULT_TIMEOUT,
        CaTissueRouteBuilder.DEFAULT_DELAY));

    /*
     * Specimen Characteristic Route
     */
    this.triggerScoreboard(from(
        this.getComponentName() + ":"
            + "edu.bcm.dldcc.big.utility.entity.SpecimenCharacteristicsLog"
            + "?persistenceUnit=" + this.getInstance().getPersistenceUnit()
            + "&consumer.delay=" + CaTissueRouteBuilder.SPECIMEN_DELAY + 
            "&consumeLockEntity=false").transacted()
        .removeHeaders("Camel*")
        .setHeader("instance", this.constant(this.getInstance().name())));

    from("jms:queue:annotationUpdateQueue")
        .aggregate(header("JMSCorrelationID"),
            new MostRecentAggregationStrategy()).completionTimeout(
                CaTissueRouteBuilder.UPDATE_TIMEOUT)
        .to("jms:queue:scoreboardQueue");

    from("jms:queue:scoreboardQueue").removeHeaders("Camel*").beanRef(
        "scoreboard", "updateScoreboards");

  }

  /**
   * 
   */
  protected ProcessorDefinition<?> buildRoute(String entityName,
      String options,
      String topicName,
      long correlationTimeout,
      long delay)
  {
    return from(
        this.getComponentName() + ":" + entityName + "?persistenceUnit="
            + this.getInstance().getPersistenceUnit()
            + "&consumer.delay=" + delay + "&consumeLockEntity=false" + options)
        .removeHeaders("Camel*")
        .setHeader("instance", this.constant(this.getInstance().name()))
        .process(new CaTissueLogProcessor())
        .aggregate(header("JMSCorrelationID"), new MostRecentAggregationStrategy())
        .completionTimeout(correlationTimeout)
        .to("jms:topic:" + topicName);
  }

  protected void triggerScoreboard(ProcessorDefinition<?> route)
  {
    route.setHeader("JMSCorrelationID", this.constant("Acquire")).to(
        "jms:queue:annotationUpdateQueue");
  }

}