/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.messaging;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;

import edu.bcm.dldcc.big.utility.entity.CaTissueLog;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * Provides additional processing for CaTissueLog objects, for example
 * adding the appropriate correlation id to the exchange
 * 
 * @author pew
 *
 */
public class CaTissueLogProcessor implements Processor
{

  /**
   * 
   */
  public CaTissueLogProcessor()
  {
    super();
  }

  /**
   * Adds a JMSCorrelationId to the exchange, using 
   * CaTissueLog.getCorrelationId() if the body extends CaTissueLog.
   * If it is an instance of Specimen, it will use the specimen id.
   * Otherwise, body.toString will be used.
   */
  @Override
  public void process(Exchange exchange) throws TypeConversionException
  {
    String correlationId = null;
    Message message = exchange.getIn();
    Object body = message.getBody();
    if(body instanceof CaTissueLog)
    {
      correlationId = ((CaTissueLog) body).getCorrelationId();
    }
    else if(body instanceof Specimen)
    {
      correlationId = ((Specimen) body).getId().toString();
    }
    else
    {
      correlationId = body.toString();
    }
    message.setHeader("JMSCorrelationID", correlationId);
   
  }

}
