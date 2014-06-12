package edu.bcm.dldcc.big.clinical.values.entity;

import javax.persistence.Cacheable;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

@MappedSuperclass
@Cacheable
public abstract class DropDownEntity
{
  private String name;

  @Length(max=200)
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DropDownEntity other = (DropDownEntity) obj;
    if (name == null)
    {
      if (other.name != null)
        return false;
    }
    else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public String toString()
  {
    return this.getName();
  }


}
