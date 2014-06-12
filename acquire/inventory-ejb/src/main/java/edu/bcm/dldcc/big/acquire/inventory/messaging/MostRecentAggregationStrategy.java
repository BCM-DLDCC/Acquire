/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.messaging;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * @author pew
 * 
 */
public class MostRecentAggregationStrategy implements
    AggregationStrategy
{

  /**
   * 
   */
  public MostRecentAggregationStrategy()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org.
   * apache.camel.Exchange, org.apache.camel.Exchange)
   */
  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange)
  {
    return newExchange;
  }


}
