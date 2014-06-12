/**
 * 
 */
package edu.bcm.dldcc.big.acquire.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import edu.bcm.dldcc.big.acquire.query.data.AliquotSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.ParticipantSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.RacSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.ShipmentSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.search.SearchFields;

/**
 * Converter for SearchFields. All implementations of this interface
 * should be enums, so this class just passes through to EnumConverter.
 * 
 * @author pew
 *
 */
@RequestScoped
@FacesConverter(forClass=SearchFields.class)
public class SearchFieldConverter implements Converter
{
  List<EnumConverter> converterList = new ArrayList<EnumConverter>();
  
  /**
   * 
   */
  public SearchFieldConverter()
  {
    super();
    converterList.add(new EnumConverter(SpecimenSearchFields.class));
    converterList.add(new EnumConverter(ParticipantSearchFields.class));
    converterList.add(new EnumConverter(AnnotationSearchFields.class));
    converterList.add(new EnumConverter(AliquotSearchFields.class));
    converterList.add(new EnumConverter(RacSearchFields.class));
    converterList.add(new EnumConverter(ShipmentSearchFields.class));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
   * javax.faces.component.UIComponent, java.lang.String)
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component,
      String value)
  {
    Object object = null;
    Iterator<EnumConverter> iter = converterList.iterator();
    while(iter.hasNext())
    {
      EnumConverter converter = iter.next();
      try
      {
        object = converter.getAsObject(context, component, value);
        if(object != null)
        {
          break;
        }
      }
      catch(ConverterException e)
      {
        if(!iter.hasNext())
        {
          /*
           * if there's no more converters to try, then we need
           * to throw the exception. 
           */
          throw e;
          
        }
      }
    }
    
    return object;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
   * javax.faces.component.UIComponent, java.lang.Object)
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component,
      Object value)
  {
    String result = null;
    
    Iterator<EnumConverter> iter = converterList.iterator();
    while(iter.hasNext())
    {
      EnumConverter converter = iter.next();
      try
      {
        result = converter.getAsString(context, component, value);
        if(result != null)
        {
          break;
        }
      }
      catch(ConverterException e)
      {
        if(!iter.hasNext())
        {
          /*
           * if there's no more converters to try, then we need
           * to throw the exception. 
           */
          throw e;
          
        }
      }
    }
    
    return result;
  }

}
