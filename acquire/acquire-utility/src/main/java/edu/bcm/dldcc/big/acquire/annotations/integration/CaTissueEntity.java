/**
 * 
 */
package edu.bcm.dldcc.big.acquire.annotations.integration;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author pew
 *
 */
@Target(
{ METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface CaTissueEntity
{
  Class<? extends AbstractDomainObject> entity();
}
