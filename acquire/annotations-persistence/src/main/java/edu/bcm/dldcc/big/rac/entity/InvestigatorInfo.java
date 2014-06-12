package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.rac.data.Degree;
import edu.bcm.dldcc.big.rac.data.InstitutionType;
import edu.bcm.dldcc.big.util.iso21090.model.EmailAddress;
import edu.bcm.dldcc.big.util.iso21090.model.PhoneNumber;
import edu.bcm.dldcc.big.util.iso21090.model.UsAddress;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:07 AM
 */
@Entity
@Audited
public class InvestigatorInfo implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 2662685831889095071L;
  private Long id;
  private Integer version;
  private String firstName;
  private String lastName;
  private String title;
  private Degree degree;
  private String institutionName;
  private InstitutionType institutionType;
  private String department;
  private UsAddress address = new UsAddress();
  private PhoneNumber workPhone = new PhoneNumber();
  private PhoneNumber faxNumber = new PhoneNumber();
  private EmailAddress email = new EmailAddress();
  private Boolean fcoi;
  private Application application;

  public InvestigatorInfo()
  {
    super();
  }
  
  /**
   * @return the id
   */
  @Id
  public Long getId()
  {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @return the version
   */
  @Version
  public Integer getVersion()
  {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(Integer version)
  {
    this.version = version;
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
   * @return the title
   */
  public String getTitle()
  {
    return this.title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * @return the degree
   */
  @Enumerated(EnumType.STRING)
  public Degree getDegree()
  {
    return this.degree;
  }

  /**
   * @param degree the degree to set
   */
  public void setDegree(Degree degree)
  {
    this.degree = degree;
  }

  /**
   * @return the institutionName
   */
  public String getInstitutionName()
  {
    return this.institutionName;
  }

  /**
   * @param institutionName the institutionName to set
   */
  public void setInstitutionName(String institutionName)
  {
    this.institutionName = institutionName;
  }

  /**
   * @return the institutionType
   */
  @Enumerated(EnumType.STRING)
  public InstitutionType getInstitutionType()
  {
    return this.institutionType;
  }

  /**
   * @param institutionType the institutionType to set
   */
  public void setInstitutionType(InstitutionType institutionType)
  {
    this.institutionType = institutionType;
  }

  /**
   * @return the department
   */
  public String getDepartment()
  {
    return this.department;
  }

  /**
   * @param department the department to set
   */
  public void setDepartment(String department)
  {
    this.department = department;
  }

  /**
   * @return the workPhone
   */
  @AttributeOverride(name="value",
      column=@Column(name="WORKNUMBER"))
  public PhoneNumber getWorkPhone()
  {
    if(this.workPhone == null)
    {
      this.workPhone = new PhoneNumber();
    }
    return this.workPhone;
  }

  /**
   * @param workPhone the workPhone to set
   */
  public void setWorkPhone(PhoneNumber workPhone)
  {
    this.workPhone = workPhone;
  }

  /**
   * @return the faxNumber
   */
  @AttributeOverride(name="value",
      column=@Column(name="FAXNUMBER"))
  public PhoneNumber getFaxNumber()
  {
    if(this.faxNumber == null)
    {
      this.faxNumber = new PhoneNumber();
    }
    return this.faxNumber;
  }

  /**
   * @param faxNumber the faxNumber to set
   */
  public void setFaxNumber(PhoneNumber faxNumber)
  {
    this.faxNumber = faxNumber;
  }

  /**
   * @return the email
   */
  @AttributeOverride(name="value",
      column=@Column(name="EMAIL"))
  public EmailAddress getEmail()
  {
    if(this.email == null)
    {
      this.email = new EmailAddress();
    }
    return this.email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(EmailAddress email)
  {
    this.email = email;
  }

  /**
   * @return the fcoi
   */
  public Boolean getFcoi()
  {
    return this.fcoi;
  }

  /**
   * @param fcoi the fcoi to set
   */
  public void setFcoi(Boolean fcoi)
  {
    this.fcoi = fcoi;
  }

  /**
   * @return the application
   */
  @OneToOne
  @MapsId
  public Application getApplication()
  {
    return this.application;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(Application application)
  {
    this.application = application;
  }

  /**
   * @return the address
   */
  @Embedded
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

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((this.email == null) ? 0 : this.email.hashCode());
    result =
        prime * result
            + ((this.firstName == null) ? 0 : this.firstName.hashCode());
    result =
        prime * result
            + ((this.lastName == null) ? 0 : this.lastName.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    InvestigatorInfo other = (InvestigatorInfo) obj;
    if (this.email == null)
    {
      if (other.email != null)
      {
        return false;
      }
    }
    else if (!this.email.equals(other.email))
    {
      return false;
    }
    if (this.firstName == null)
    {
      if (other.firstName != null)
      {
        return false;
      }
    }
    else if (!this.firstName.equals(other.firstName))
    {
      return false;
    }
    if (this.lastName == null)
    {
      if (other.lastName != null)
      {
        return false;
      }
    }
    else if (!this.lastName.equals(other.lastName))
    {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString()
  {
    return this.getFirstName() + " " + this.getLastName();
  }

}