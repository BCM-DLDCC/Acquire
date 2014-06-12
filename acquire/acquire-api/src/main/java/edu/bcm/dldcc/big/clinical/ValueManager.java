/**
 * 
 */
package edu.bcm.dldcc.big.clinical;

import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;

/**
 * @author pew
 *
 */
@Local
public interface ValueManager
{
  <T> List<T> getValueList(Class<T> type);
  List<TStaging> getTStagingList();
  List<MStaging> getMStagingList();
  List<NStaging> getNStagingList();
  List<TumorStage> getTumorStageList();
  List<TumorGrade> getTumorGradeList();
  List<SiteAnnotation> getSiteAnnotations();
  List<YesNoChoices> getYesNoList();
}
