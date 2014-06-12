 package edu.bcm.dldcc.big.acquire.scoreboard;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ejb.Local;

import org.apache.camel.Exchange;

/**
 * 
 * ScoreboardGenerator specifies methods that can be used to generate scoreboards
 * for Acquire. If the class as a whole is needed in EL, it is referenced
 * as "scoreboard".
 *
 */
@Local
public interface ScoreboardGenerator
{
  /**
   * Compile information required for a scoreboard representing total samples
   * collected, not broken down in any manner. The return value is available
   * in EL as "totalScores."
   * @return Scoreboard containing the total score information.
   * @throws InterruptedException 
   * @throws ExecutionException 
   */
  Scoreboard getTotalScores();
  
  /**
   * Compiles information required to generate a scoreboard with samples
   * broken down according to SampleSite. The return value is avaliable
   * in EL as "siteScores."
   * @return List&lt;Scoreboard&gt; containing the site score information
   * @throws ExecutionException 
   * @throws InterruptedException 
   */
  List<Scoreboard> getSiteScores();
  
  /**
   * Compiles information required to generate a scoreboard with samples
   * broken down according to ICDO3 Morphology code. The return value is 
   * available in EL as "diseaseScores"
   * @return List&lt;Scoreboard&gt; containing the disease score information
   * @throws ExecutionException 
   * @throws InterruptedException 
   */
  List<Scoreboard> getDiseaseScores();
  
  /**
   * This method will return a List of timepoint objects used in creating
   * a line graph of the collection over time. The return value is available
   * in EL as "timepoints"
   * 
   * @return List&lt;TumorTimepoint&gt; the timepoints to be used in a graph
   */
  List<TumorTimepoint> getTimepoints();
  
  /**
   * Convenience method that will take a Double and format it as a percent.
   * 
   * @param value the Double to be formatted
   * @return a String in the form ###.#%
   */
  String formatPercent(Double value);
  
  /**
   * Method to update the values used in the scoreboards
   */
  void updateScoreboards(Exchange exchange);
}
