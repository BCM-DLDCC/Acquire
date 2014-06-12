/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.bcm.dldcc.big.acquire.inventory.InventoryProcessor;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
public class NaLabReportData implements Serializable
{
  AliquotAnnotation aliquot;
  SpecimenAnnotation annotation;
  NaLabAnnotation naLab;
  Specimen derivative;
  AbstractSpecimen parent;
  Specimen aliquotSpecimen;
  Participant participant;

  /**
   * 
   */
  public NaLabReportData()
  {
    super();
  }

  /**
   * @return the aliquot
   */
  public AliquotAnnotation getAliquot()
  {
    return this.aliquot;
  }

  /**
   * @param aliquot
   *          the aliquot to set
   */
  public void setAliquot(AliquotAnnotation aliquot)
  {
    this.aliquot = aliquot;
  }

  /**
   * @return the annotation
   */
  public SpecimenAnnotation getAnnotation()
  {
    return this.annotation;
  }

  /**
   * @param annotation
   *          the annotation to set
   */
  public void setAnnotation(SpecimenAnnotation annotation)
  {
    this.annotation = annotation;
  }

  /**
   * @return the naLab
   */
  public NaLabAnnotation getNaLab()
  {
    return this.naLab;
  }

  /**
   * @param naLab
   *          the naLab to set
   */
  public void setNaLab(NaLabAnnotation naLab)
  {
    this.naLab = naLab;
  }

  /**
   * @return the derivative
   */
  public Specimen getDerivative()
  {
    return this.derivative;
  }

  /**
   * @param derivative
   *          the derivative to set
   */
  public void setDerivative(Specimen derivative)
  {
    this.derivative = derivative;
  }

  /**
   * @return the parent
   */
  public AbstractSpecimen getParent()
  {
    return this.parent;
  }

  /**
   * @param parent
   *          the parent to set
   */
  public void setParent(AbstractSpecimen parent)
  {
    this.parent = parent;
  }

  /**
   * @return the particpant
   */
  public Participant getParticipant()
  {
    return this.participant;
  }

  /**
   * @param particpant
   *          the particpant to set
   */
  public void setParticipant(Participant particpant)
  {
    this.participant = particpant;
  }

  /**
   * @return the aliquotSpecimen
   */
  public Specimen getAliquotSpecimen()
  {
    return this.aliquotSpecimen;
  }

  /**
   * @param aliquotSpecimen
   *          the aliquotSpecimen to set
   */
  public void setAliquotSpecimen(Specimen aliquotSpecimen)
  {
    this.aliquotSpecimen = aliquotSpecimen;
  }

  public String getAliquotLabel()
  {
    return this.getAliquotSpecimen().getLabel();
  }

  public String getParentLabel()
  {
    return this.parent != null ? this.parent.getLabel() : "--";
  }

  public List<String> getMrn()
  {
    Collection<Object> identifiers = this.getParticipant()
        .getParticipantMedicalIdentifierCollection();
    List<String> mrns = new ArrayList<String>();
    for (Object identifier : identifiers)
    {
      ParticipantMedicalIdentifier mrn = (ParticipantMedicalIdentifier) identifier;
      mrns.add(mrn.getMedicalRecordNumber());
    }
    return mrns;
  }

  public String getConcatenatedMrn()
  {
    String concatenatedMrn = "";

    for (String mrn : this.getMrn())
    {
      if (mrn != null && mrn.length() > 0)
      {
        concatenatedMrn = concatenatedMrn + mrn + ", ";
      }
    }

    // lop off the final two chars which are the extra commas, making sure we
    // have enough text to do this
    int endIndex = concatenatedMrn.length() - 2;
    if (endIndex < 0)
      endIndex = 0;
    return concatenatedMrn.substring(0, endIndex);
  }

  public String getNaLabId()
  {
    return this.getDerivative().getLabel();
  }

  public DerivativeType getNaType()
  {
    return this.getNaLab().getType();
  }

  public Double getConcentration()
  {
    return ((MolecularSpecimen) this.getDerivative())
        .getConcentrationInMicrogramPerMicroliter()
        / InventoryProcessor.CONCENTRATION_CONVERSION_FACTOR;
  }

  public Double getQuantity()
  {
    return this.getDerivative().getAvailableQuantity();
  }

  public BigDecimal getRinValue()
  {
    return this.getNaLab().getRin();
  }

  public DnaQuality getQuality()
  {
    return this.getNaLab().getQuality();
  }
}
