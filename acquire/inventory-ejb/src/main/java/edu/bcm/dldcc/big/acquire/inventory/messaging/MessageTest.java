/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.messaging;

import javax.inject.Named;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;

/**
 * @author pew
 *
 */
@Named
public class MessageTest
{

  @EndpointInject(uri="sjms:topic:testTopic?transacted=true")
  private ProducerTemplate producer;
  /**
   * 
   */
  public MessageTest()
  {
    super();
  }
  
  public void testMessage()
  {
    producer.sendBody("This is a test");
  }

}
