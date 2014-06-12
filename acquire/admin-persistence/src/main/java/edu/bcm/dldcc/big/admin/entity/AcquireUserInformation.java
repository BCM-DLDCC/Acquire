/**
 * 
 */
package edu.bcm.dldcc.big.admin.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.security.model.UserInformation;
import edu.bcm.dldcc.big.util.iso21090.model.PhoneNumber;
import edu.bcm.dldcc.big.util.iso21090.model.UsAddress;

/**
 * @author pew
 *
 */
@Entity
@Audited
public class AcquireUserInformation extends UserInformation implements Serializable
{
  private String firstName;
  private String lastName;
  private String institution;
  private String affiliation;
  private Boolean tcrb = false;
  private Boolean superAdmin = false;
  private Boolean caTissueUser = false;
  private UsAddress address = new UsAddress();
  private PhoneNumber phone = new PhoneNumber();
  private Boolean bcmUser = false;
  
  
  /**
   * 
   */
  public AcquireUserInformation()
  {
    super();
    
  }


  /**
   * @return the firstName
   */
  public String getFirstName()
  {
    return this.firstName;
  }


  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }


  /**
   * @return the lastName
   */
  public String getLastName()
  {
    return this.lastName;
  }


  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }


  /**
   * @return the institution
   */
  public String getInstitution()
  {
    return this.institution;
  }


  /**
   * @param institution the institution to set
   */
  public void setInstitution(String institution)
  {
    this.institution = institution;
  }


  /**
   * @return the tcrb
   */
  public Boolean getTcrb()
  {
    return this.tcrb;
  }


  /**
   * @param tcrb the tcrb to set
   */
  public void setTcrb(Boolean tcrb)
  {
    this.tcrb = tcrb;
  }


  /**
   * @return the address
   */
  public UsAddress getAddress()
  {
    if(this.address == null)
    {
      this.address = new UsAddress();
    }
    return this.address;
  }


  /**
   * @param address the address to set
   */
  public void setAddress(UsAddress address)
  {
    this.address = address;
  }


  /**
   * @return the phone
   */
  @AttributeOverride(name="value",
      column=@Column(name="PHONE"))
  public PhoneNumber getPhone()
  {
    if(this.phone ==  null)
    {
      this.phone = new PhoneNumber();
    }
    return this.phone;
  }


  /**
   * @param phone the phone to set
   */
  public void setPhone(PhoneNumber phone)
  {
    this.phone = phone;
  }


  /**
   * @return the superAdmin
   */
  public Boolean getSuperAdmin()
  {
    return this.superAdmin;
  }


  /**
   * @param superAdmin the superAdmin to set
   */
  public void setSuperAdmin(Boolean superAdmin)
  {
    this.superAdmin = superAdmin;
  }


  /**
   * @return the caTissueUser
   */
  public Boolean getCaTissueUser()
  {
    return this.caTissueUser;
  }


  /**
   * @param caTissueUser the caTissueUser to set
   */
  public void setCaTissueUser(Boolean caTissueUser)
  {
    this.caTissueUser = caTissueUser;
  }


  /**
   * @return the affiliation
   */
  public String getAffiliation()
  {
    return this.affiliation;
  }


  /**
   * @param affiliation the affiliation to set
   */
  public void setAffiliation(String affiliation)
  {
    this.affiliation = affiliation;
  }
  
  @Transient
  public String getFullName()
  {
    return this.getFirstName() + " " + this.getLastName();
  }


  /**
   * @return the bcmUser
   */
  public Boolean getBcmUser()
  {
    return this.bcmUser;
  }


  /**
   * @param bcmUser the bcmUser to set
   */
  public void setBcmUser(Boolean bcmUser)
  {
    this.bcmUser = bcmUser;
  }
  

}
