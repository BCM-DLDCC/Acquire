package edu.bcm.dldcc.big.acquire.util;

import java.util.Collection;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.primefaces.event.TabChangeEvent;

import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;

/**
 * Session Bean implementation class Utilities
 */
@Singleton
@LocalBean
@Named
@ApplicationScoped
public class Utilities
{
  @Inject
  private Conversation conversation;
  
  /**
   * Default constructor.
   */
  public Utilities()
  {
    super();
  }

  public int calculateAgeAtCollection(Specimen specimen, Participant participant)
  {
    Years ageYear = Years.years(0);
    Collection<SpecimenEventParameters> events = specimen
        .getSpecimenEventCollection();
    DateTime collectionDate = null;
    for (SpecimenEventParameters event : events)
    {
      if (event instanceof CollectionEventParameters)
      {
        collectionDate = new DateTime(event.getTimestamp());
        break;
      }
    }

    if (collectionDate != null)
    {
      DateTime birth = new DateTime(participant.getBirthDate());
      ageYear = Years.yearsBetween(birth, collectionDate);
    }
    return ageYear.getYears();
  }
  
  public void startConversation()
  {
    if(conversation.isTransient())
    {
      conversation.begin();
    }
  }
  
  public void endConversation()
  {
    if (!conversation.isTransient())
    {
      this.conversation.end();
    }
  }
  
  public void switchTab(TabChangeEvent event)
  {
    if(event.getTab().getTitle().equals("Pathology") || 
        event.getTab().getTitle().equals("Specimen Updates") ||
        event.getTab().getTitle().equals("RAC"))
    {
      this.startConversation();
    }
  }

  

}
