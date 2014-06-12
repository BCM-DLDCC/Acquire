package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.internet.InternetAddress;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultExchangeHolder;
import org.jboss.seam.mail.api.MailMessage;

import edu.bcm.dldcc.big.acquire.event.MailExceptionEvent;

/**
 * Message-Driven Bean implementation class for: NewParticipantMessage
 * 
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "deadLetterQueue") }, mappedName = "deadLetterQueue")
public class DeadMessageProcessor implements MessageListener
{

  @Inject
  private CamelContext context;

  @Inject
  private Event<MailExceptionEvent> event;
  
  @Inject
  private Instance<MailMessage> mail;
  
  @Inject
  private InternetAddress support;
  
  @Inject
  @Named("serverName") 
  private String server;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public DeadMessageProcessor()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
   */
  @Override
  public void onMessage(Message message)
  {
    MailExceptionEvent exception = null;

    try
    {
      DefaultExchangeHolder holder =
          (DefaultExchangeHolder) ((ObjectMessage) message).getObject();
      Exchange exchange = new DefaultExchange(this.context);
      DefaultExchangeHolder.unmarshal(exchange, holder);
      exception =
          new MailExceptionEvent("Error in JMS with message on server: ",
              exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class));

    }
    catch (JMSException e)
    {
      exception =
          new MailExceptionEvent("Error in "
              + "messaging, and further error retrieving information "
              + "about the error on server: ", e);
    }
    catch (ClassCastException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Error in messaging on ");
      sb.append(server + ".\n\n ");
      sb.append("Dead message is: " +  message + "\n");
      try
      {
        if(message instanceof ObjectMessage)
        {
          sb.append("Message body is " + ((ObjectMessage) message).getObject()
              + "\n");
        }

        sb.append("Message is for id " + message.getJMSCorrelationID());
      }
      catch (JMSException e1)
      {
        sb.append("Unable to get additional information from message");
       
      }

      mail.get().from("Benjamin Pew<pew@bcm.edu>").to(support)
          .subject("Error with a message in Acquire on " + server)
          .bodyText(sb.toString()).send();
      return;
    }

    this.event.fire(exception);

  }

}
