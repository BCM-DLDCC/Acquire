/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

/**
 * @author pew
 *
 */
public class SearchResultDataModel extends ListDataModel<SearchResult>
    implements SelectableDataModel<SearchResult>
{
  
  Map<String, SearchResult> keyMap = new TreeMap<String, SearchResult>();

  /**
   * 
   */
  public SearchResultDataModel()
  {
    super();
    this.setWrappedData(new ArrayList<SearchResult>());
  }

  /**
   * @param list
   */
  public SearchResultDataModel(List<SearchResult> list)
  {
    super(list);
    this.addToMap(list);
  }

  /* (non-Javadoc)
   * @see org.primefaces.model.SelectableDataModel#getRowData(java.lang.String)
   */
  @Override
  public SearchResult getRowData(String key)
  {
    return this.keyMap.get(key);
  }

  /* (non-Javadoc)
   * @see org.primefaces.model.SelectableDataModel#getRowKey(java.lang.Object)
   */
  @Override
  public Object getRowKey(SearchResult row)
  {
    return row.getUuid();
  }
  
  public void clear()
  {
    this.setWrappedData(new ArrayList<SearchResult>());
    this.keyMap.clear();
  }
  
  public boolean addAll(Collection<? extends SearchResult> items)
  {
    this.addToMap(items);
    
    return this.unwrap().addAll(items);
  }

  private void addToMap(Collection<? extends SearchResult> items)
  {
    for(SearchResult current : items)
    {
      this.keyMap.put(current.getUuid(), current);
    }
  }
  
  @SuppressWarnings("unchecked")
  private List<SearchResult> unwrap()
  {
    return (List<SearchResult>) this.getWrappedData();
  }

}
