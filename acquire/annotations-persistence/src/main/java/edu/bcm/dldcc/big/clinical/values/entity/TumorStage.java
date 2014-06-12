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
public class TumorStage extends DropDownEntity implements Serializable
{
  // seam-gen attributes (you should probably edit these)
  private Long id;
  private Integer version;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
      generator = "tumorStageGenerator")
  @SequenceGenerator(name = "tumorStageGenerator",
      sequenceName = "TUMOR_STAGE_SEQ")
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
}
