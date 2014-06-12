/**
 * 
 */
package edu.bcm.dldcc.big.acquire.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.enterprise.context.NonexistentConversationException;
import javax.enterprise.inject.Instance;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExternalContext;
import javax.inject.Named;
import javax.mail.internet.InternetAddress;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.security.Identity;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.exception.rac.DeleteCommentException;

/**
 * @author pew
 * 
 */
@HandlesExceptions
public class ExceptionHandler
{

  /**
   * 
   */
  public ExceptionHandler()
  {
    super();
  }

  @SuppressWarnings("unused")
  private void identityExceptions(
      @Handles CaughtException<IdentityException> e, Messages message)
  {
    message.error("Unable to authorize");
    e.handled();
  }

  @SuppressWarnings("unused")
  private void deleteCommentException(
      @Handles CaughtException<DeleteCommentException> e, Messages message)
  {
    message.error(e.getException().getMessage());
    e.handled();
  }

  @SuppressWarnings("unused")
  private void noConversationException(
      @Handles CaughtException<NonexistentConversationException> evt)
  {
    evt.handled();
  }

  @SuppressWarnings("unused")
  private void viewExpired(@Handles CaughtException<ViewExpiredException> evt,
      ExternalContext context)
  {
    evt.handled();
    try
    {
      context.redirect(context.getRequestContextPath()
          + evt.getException().getViewId());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unused")
  private void
      redirectOnException(@Handles CaughtException<Exception> evt,
          ExternalContext context, Resources message,
          Instance<MailMessage> mail, Identity user, InternetAddress support,
          @Named("serverName") String server)
  {
    try
    {
      String from = "pew@bcm.edu";
      StringBuilder sb = new StringBuilder();
      sb.append("An error has occurred with Acquire on ");
      sb.append(server + ".\n ");
      if (user != null && user.getUser() != null)
      {
        from = user.getUser().getId();
        sb.append("The user experiencing the error is: " + from + ".\n\n");
      }
      StringWriter stackTrace = new StringWriter();
      evt.getException().printStackTrace(new PrintWriter(stackTrace));
      sb.append(stackTrace.toString());
      mail.get().from(from).to(support)
          .subject("Error with Acquire: " + evt.getException().getMessage())
          .bodyText(sb.toString()).send();
      evt.handled();
      message.setErrorMessage(evt.getException().getMessage());
      context.redirect(context.getRequestContextPath() + "/error.jsf");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unused")
  private void dataForClassCast(
      @Handles CaughtException<ClassCastException> evt, Resources message,
      Instance<MailMessage> mail, Identity user, InternetAddress support,
      @Named("serverName") String server)
  {

    String from = "pew@bcm.edu";
    StringBuilder sb = new StringBuilder();
    sb.append("A ClassCastException error has occurred with Acquire on ");
    sb.append(server + ".\n ");
    if (user != null && user.getUser() != null)
    {
      from = user.getUser().getId();
      sb.append("The user experiencing the error is: " + from + ".\n\n");
    }
    StringWriter stackTrace = new StringWriter();
    new Throwable().printStackTrace(new PrintWriter(stackTrace));
    sb.append(stackTrace.toString());
    mail.get().from(from).to(support)
        .subject("Error with Acquire: " + evt.getException().getMessage())
        .bodyText(sb.toString()).send();
    evt.handled();
    message.setErrorMessage(evt.getException().getMessage());

  }

}
