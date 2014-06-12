package edu.bcm.dldcc.big.clinical.values.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

@Entity
public class TStaging extends DropDownEntity implements Serializable, Comparable<TStaging>
{
  // seam-gen attributes (you should probably edit these)
  private Long id;
  private Integer version;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
      generator = "tstagingGenerator")
  @SequenceGenerator(name = "tstagingGenerator", sequenceName = "TSTAGING_SEQ")
  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  @Version
  public Integer getVersion()
  {
    return version;
  }

  private void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   * 
   * Other should be last in any comparison. Otherwise, sort based on 
   * the name of the TStaging
   */
  @Override
  public int compareTo(TStaging o)
  {
    int result = this.getName().compareTo(o.getName());
    if(result != 0)
    {
      if(this.getName().equals("Other"))
      {
        result = 1;
      }
      else if (o.getName().equals("Other"))
      {
        result = -1;
      }
    }
    return result;
  }
}
