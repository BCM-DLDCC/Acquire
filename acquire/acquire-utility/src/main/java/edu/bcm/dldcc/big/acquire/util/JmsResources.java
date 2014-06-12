/**
 * 
 */
package edu.bcm.dldcc.big.acquire.util;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import edu.bcm.dldcc.big.acquire.qualifiers.AnnotationUpdate;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLab;

/**
 * Class to provide resources for using JMS services
 * 
 * @author pew
 * 
 */
@ApplicationScoped
public class JmsResources
{

  /**
   * 
   */
  public JmsResources()
  {
    super();
  }

 
  @Produces
  @Resource(mappedName = "java:/JmsXA")
  private ConnectionFactory pooledFactory;

  
  @Resource(mappedName = "queue/annotationUpdate")
  private Queue annotationUpdateQueue;
  
  @Resource(mappedName="queue/naLabReport")
  private Queue naLabQueue;

  @Produces
  public Connection createAnnotationConnection() throws JMSException
  {
    return this.pooledFactory.createConnection();
  }

  public void closeAnnotationConnection(
      @Disposes Connection connection) throws JMSException
  {
    connection.close();
  }

  @Produces
  public Session createAnnotationSession(Connection connection)
      throws JMSException
  {
    return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  public void closeAnnotationSession(@Disposes Session session)
      throws JMSException
  {
    session.close();
  }

  @Produces
  @AnnotationUpdate
  public MessageProducer createAnnotationMessageProducer(
      Session session) throws JMSException
  {
    return session.createProducer(this.annotationUpdateQueue);
  }

  public void closeAnnotationMessageProducer(
      @Disposes @AnnotationUpdate MessageProducer producer) throws JMSException
  {
    producer.close();
  }
  
  @Produces
  @NaLab
  public MessageProducer createNaLabMessageProducer(
      Session session) throws JMSException
  {
    return session.createProducer(this.naLabQueue);
  }

  public void closeNaLabMessageProducer(
      @Disposes @NaLab MessageProducer producer) throws JMSException
  {
    producer.close();
  }

}
