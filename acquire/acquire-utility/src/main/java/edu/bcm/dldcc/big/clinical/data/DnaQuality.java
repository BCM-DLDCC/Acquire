/**
 * 
 */
package edu.bcm.dldcc.big.clinical.data;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author pew
 * 
 */
public enum DnaQuality
{
  NOT_DEGRADED("Not degraded"), PARTIALLY_DEGRADED("Partial degraded"), DEGRADED(
      "Degraded"), NO_DNA("No DNA");

  private DnaQuality(String name)
  {
    this.name = name;
  }

  private String name;

  @Override
  public String toString()
  {
    return this.name;
  }

  public static DnaQuality forString(String value)
      throws IllegalArgumentException
  {
    DnaQuality result = null;
    Set<DnaQuality> values = EnumSet.allOf(DnaQuality.class);
    for (DnaQuality quality : values)
    {
      if (quality.toString().equals(value))
      {
        result = quality;
        break;
      }
    }

    if (result == null)
    {
      throw new IllegalArgumentException(
          "There is no Enum constant that matches the value provided");
    }

    return result;
  }

}
