/**
 * 
 */
package edu.bcm.dldcc.big.acquire.annotations.security.authorizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.seam.security.annotations.SecurityBindingType;

/**
 * @author pew
 *
 */
@SecurityBindingType
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface PathologyTab
{

}
