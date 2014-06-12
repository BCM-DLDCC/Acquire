/**
 * 
 */
package edu.bcm.dldcc.big.acquire.search.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.criteria.From;

import edu.bcm.dldcc.big.acquire.qualifiers.ApplicationSearch;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLabShipmentSearch;
import edu.bcm.dldcc.big.acquire.query.data.RacSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.ShipmentSearchFields;
import edu.bcm.dldcc.big.acquire.search.session.ArchivedApplicationSearchManager;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.annotations.qualifier.SearchConfiguration;
import edu.bcm.dldcc.big.clinical.ValueManager;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SimpleSearchManager;

/**
 * @author pew
 * 
 */
@ApplicationScoped
public class SearchResources
{

  /**
   * 
   */
  public SearchResources()
  {
    super();
  }

  @Produces
  @ApplicationSearch
  @ConversationScoped
  @Named("applicationSearch")
  private SimpleSearchManager<Application> initApplicationSearch(
      @New ArchivedApplicationSearchManager manager)
  {
    List<SearchFields<From<?, Application>, Application>> fields =
        new ArrayList<SearchFields<From<?, Application>, Application>>();
    fields.add(RacSearchFields.SUBMITTER_LAST_NAME);
    fields.add(RacSearchFields.SUBMISSION_DATE);
    fields.add(RacSearchFields.PROJECT_TITLE);
    fields.add(RacSearchFields.DISEASE_SITE);
    manager.setFieldList(fields);
    manager.setSearchField(RacSearchFields.SUBMITTER_LAST_NAME);
    return manager;

  }

  @Produces
  @NaLabShipmentSearch
  @ConversationScoped
  @Named("naLabShipmentSearch")
  private SimpleSearchManager<Shipment> initShipmentSearch(
      @New SimpleSearchManager<Shipment> shipmentSearch, ValueManager vm)
  {
    ShipmentSearchFields.COLLECTION_SITE.configurePermissibleValues(
        vm.getSiteAnnotations(), SiteAnnotation.class);
    List<SearchFields<From<?, Shipment>, Shipment>> fields =
        new ArrayList<SearchFields<From<?, Shipment>, Shipment>>();
    fields.add(ShipmentSearchFields.SHIPMENT_ID);
    /*
     * Leave out date for now, until it can be better designed.
     * 
     * fields.add(ShipmentSearchFields.CREATION_DATE);
     */
    fields.add(ShipmentSearchFields.SHIPMENT_TITLE);
    fields.add(ShipmentSearchFields.COLLECTION_SITE);
    shipmentSearch.setFieldList(fields);
    return shipmentSearch;
  }

  @Produces
  @SearchConfiguration
  private DateFormat searchFormat = new SimpleDateFormat("MM/yyyy");

}
