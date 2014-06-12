package edu.bcm.dldcc.big.rac.data;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:36 AM
 */
public enum ApplicationStatus
{
  DRAFT("Draft", false), 
  SUBMITTED("Submitted", false), 
  IN_REVIEW("In Review", false), 
  APPROVED("Approved", true), 
  REJECTED("Rejected", true), 
  DISTRIBUTED("Distributed", true);

  private String name;
  
  private boolean selectable;

  /**
   * 
   * @param name
   */
  private ApplicationStatus(String name, boolean selectable)
  {
    this.name= name;
    this.selectable = selectable;
  }

  public String toString()
  {
    return this.name;
  }
  
  public boolean isSelectable()
  {
    return this.selectable;
  }
  
  public static Set<ApplicationStatus> getInboxStatus()
  {
    return EnumSet.of(SUBMITTED, IN_REVIEW, APPROVED);
  }
  
  public static Set<ApplicationStatus> getArchivedStatus()
  {
    return EnumSet.of(REJECTED, DISTRIBUTED);
  }
}