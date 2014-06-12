/**
 * 
 */
package edu.bcm.dldcc.big.acquire.scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to hold counts of samples for a scoreboard display in Acquire.
 * 
 * @author pew
 *
 */
public class Scoreboard implements Comparable<Scoreboard>
{
  
  private long totalSamples;
  private long untreated;
  private long matchedNormal;
  private long percentTumor;
  private long percentNecrosis;
  private long time;
  private long normalAmount;
  private long qualified;
  private String name;
  private long twoWeeks;
  private long lastWeek;
  private boolean site;
  private long potential;
  private List<Scoreboard> children = new ArrayList<Scoreboard>();

  /**
   * Empty constructor
   */
  public Scoreboard()
  {
    super();
  }

  /**
   * Get the number of total samples for this scoreboard
   * @return a long representing the total samples counted for this scoreboard
   */
  public long getTotalSamples()
  {
    return totalSamples;
  }

  /**
   * Set the number of total samples for this scoreboard
   * @param totalSamples
   */
  public void setTotalSamples(long totalSamples)
  {
    this.totalSamples = totalSamples;
  }

  /**
   * Get the number of untreated primary specimens for this scoreboard
   * @return
   */
  public long getUntreated()
  {
    return untreated;
  }

  /**
   * Set the number of untreated primary specimens for this scoreboard
   * @param untreated
   */
  public void setUntreated(long untreated)
  {
    this.untreated = untreated;
  }

  public long getMatchedNormal()
  {
    return matchedNormal;
  }

  public void setMatchedNormal(long matchedNormal)
  {
    this.matchedNormal = matchedNormal;
  }

  public long getPercentTumor()
  {
    return percentTumor;
  }

  public void setPercentTumor(long percentTumor)
  {
    this.percentTumor = percentTumor;
  }

  public long getPercentNecrosis()
  {
    return percentNecrosis;
  }

  public void setPercentNecrosis(long percentNecrosis)
  {
    this.percentNecrosis = percentNecrosis;
  }

  public long getTime()
  {
    return time;
  }

  public void setTime(long time)
  {
    this.time = time;
  }

  public long getNormalAmount()
  {
    return normalAmount;
  }

  public void setNormalAmount(long normalAmount)
  {
    this.normalAmount = normalAmount;
  }

  public long getQualified()
  {
    return qualified;
  }

  public void setQualified(long qualified)
  {
    this.qualified = qualified;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<Scoreboard> getChildren()
  {
    return children;
  }

  public void setChildren(List<Scoreboard> children)
  {
    this.children = children;
  }
  
  public void addChild(Scoreboard child)
  {
    this.getChildren().add(child);
  }
  
  public void removeChild(Scoreboard child)
  {
    this.getChildren().remove(child);
  }

  /**
   * @return the twoWeeks
   */
  public long getTwoWeeks()
  {
    return this.twoWeeks;
  }

  /**
   * @param twoWeeks the twoWeeks to set
   */
  public void setTwoWeeks(long twoWeeks)
  {
    this.twoWeeks = twoWeeks;
  }

  /**
   * @return the lastWeek
   */
  public long getLastWeek()
  {
    return this.lastWeek;
  }

  /**
   * @param lastWeek the lastWeek to set
   */
  public void setLastWeek(long lastWeek)
  {
    this.lastWeek = lastWeek;
  }

  /**
   * @return the site
   */
  public boolean isSite()
  {
    return this.site;
  }

  /**
   * @param site the site to set
   */
  public void setSite(boolean site)
  {
    this.site = site;
  }

  /**
   * @return the potential
   */
  public long getPotential()
  {
    return this.potential;
  }

  /**
   * @param potential the potential to set
   */
  public void setPotential(long potential)
  {
    this.potential = potential;
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Scoreboard o)
  {
    return this.getName().compareTo(o.getName());
  }

}
