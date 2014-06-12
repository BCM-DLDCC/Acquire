/**
 * 
 */
package edu.bcm.dldcc.big.acquire.qualifiers;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Inherited
@Target(
{ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
/**
 * @author pew
 *
 */
public @interface Permissions
{

}
