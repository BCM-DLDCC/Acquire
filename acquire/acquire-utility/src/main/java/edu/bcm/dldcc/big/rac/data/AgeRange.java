package edu.bcm.dldcc.big.rac.data;

import gov.nih.nci.iso21090.Int;
import gov.nih.nci.iso21090.Ivl;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:36 AM
 */
public enum AgeRange
{
  ZERO_SEVENTEEN("0-17", 0, 17), 
  EIGHTEEN_THIRTY("18-30", 18, 30), 
  THIRTY_FORTY("30-40", 30, 40), 
  FORTY_FIFTY("40-50", 40, 50), 
  FIFTY_SIXTY("50-60", 50, 60), 
  SIXTY_SEVENTY("60-70", 60, 70), 
  SEVENTY_EIGHTY("70-80", 70, 80), 
  EIGHTY_PLUS("80+", 80, 200);

  private String name;
  private Ivl<Int> range;

  /**
   * 
   * @param name
   */
  private AgeRange(String name, int rangeBegin, int rangeEnd)
  {
    this.name = name;
    this.range = new Ivl<Int>();
    Int low = new Int();
    low.setValue(rangeBegin);
    this.range.setLow(low);
    Int high = new Int();
    high.setValue(rangeEnd);
    this.range.setHigh(high);
    this.range.setHighClosed(false);
    this.range.setLowClosed(true);
  }

  public String toString()
  {
    return this.name;
  }

  public Ivl<Int> getRange()
  {
    return this.range;
  }
}