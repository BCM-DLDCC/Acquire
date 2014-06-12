/**
 * 
 */
package edu.bcm.dldcc.big.acquire.scoreboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author pew
 *
 */
public class TumorTimepoint
{
  private Date timepoint;
  private Long totalSamples;
  private Long qualifiedSamples;
  
  private static DateFormat format = new SimpleDateFormat("MM/yy");
  
  public TumorTimepoint()
  {
    super();
  }
  
  public TumorTimepoint(Date timepoint)
  {
    this();
    this.timepoint = timepoint;
  }
  /**
   * @return the timepoint
   */
  public Date getTimepoint()
  {
    return this.timepoint;
  }
  /**
   * @param timepoint the timepoint to set
   */
  public void setTimepoint(Date timepoint)
  {
    this.timepoint = timepoint;
  }
  /**
   * @return the totalSamples
   */
  public Long getTotalSamples()
  {
    return this.totalSamples;
  }
  /**
   * @param totalSamples the totalSamples to set
   */
  public void setTotalSamples(Long totalSamples)
  {
    this.totalSamples = totalSamples;
  }
  /**
   * @return the qualifiedSamples
   */
  public Long getQualifiedSamples()
  {
    return this.qualifiedSamples;
  }
  /**
   * @param qualifiedSamples the qualifiedSamples to set
   */
  public void setQualifiedSamples(Long qualifiedSamples)
  {
    this.qualifiedSamples = qualifiedSamples;
  }
  
  public String getTimepointString()
  {
    return format.format(this.getTimepoint());
  }
}
