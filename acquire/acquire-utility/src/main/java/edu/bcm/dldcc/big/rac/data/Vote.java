/**
 * 
 */
package edu.bcm.dldcc.big.rac.data;

/**
 * @author pew
 *
 */
public enum Vote
{
  NOT_VOTED("No vote"),
  YES("Yes"),
  NO("No");
  
  private String value;
  
  private Vote(String text)
  {
    this.value = text;
  }
  
  @Override
  public String toString()
  {
    return this.value;
  }
  
  public static Vote valueForBoolean(Boolean vote)
  {
    Vote value = NO;
    if(vote == null)
    {
      value = NOT_VOTED;
    }
    else if(vote)
    {
      value = YES;
    }
    
    return value;
  }
}
