package edu.bcm.dldcc.big.acquire.security;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.admin.PermissionManager;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.Admin;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.Announcements;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.NonPHI;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.PHI;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.PathologyTab;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.PublicData;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.Rac;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.RacChair;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.RacTab;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.ShipmentForms;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.SuperAdmin;
import edu.bcm.dldcc.big.acquire.listener.AdminResources;
import edu.bcm.dldcc.big.acquire.util.DataVisibility;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.util.qualifier.Current;

/**
 * Session Bean interface Authorizations. Available in EL as "authorizations"
 */
@Stateless
@LocalBean
@Named("authorizations")
public class Authorizations implements Serializable
{

  @Inject
  private Identity identity;

  @Inject
  private PermissionManager pm;

  @Inject
  private AdminResources admin;

  @Admin
  @Secures
  public boolean isAdmin(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isSuperAdmin();
    if (!result)
    {
      result = this.checkFunction("Admin", program);
    }
    return result;
  }

  @Announcements
  @Secures
  public boolean isAnnouncements(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isAdmin(program);
    if (!result)
    {
      result = this.checkFunction("Announcement Management", program);
    }

    return result;
  }

  @NonPHI
  @Secures
  public boolean isNonPhi(@Named("currentProgram") String program, String site)
      throws IdentityException
  {
    boolean result = this.isPhi(program, site);
    if (!result)
    {
      result = this.checkRole("Non PHI", site);
    }
    return result;
  }

  @PathologyTab
  @Secures
  public boolean isPathology(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isAdmin(program);
    if (!result)
    {
      result = this.checkFunction("Pathology Tab", program);
    }
    return result;
  }

  @PHI
  @Secures
  public boolean isPhi(@Named("currentProgram") String program, String site)
      throws IdentityException
  {
    boolean result = this.pm.isSiteInProgram(site, program);
    if (result)
    {
      result = this.isAdmin(program);
      if (!result)
      {
        result = this.checkRole("PHI", site);
      }
    }
    return result;
  }

  @PublicData
  @Secures
  public boolean isPublicData(@Named("currentProgram") String program,
      String site) throws IdentityException
  {
    boolean result = this.isNonPhi(program, site);

    if (!result)
    {
      result = this.checkFunction("Public Data", program);
    }
    return result;
  }

  @RacChair
  @Secures
  public boolean isRacChair(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isAdmin(program);
    if (!result)
    {
      result = this.checkFunction("RAC Chair", program);
    }
    return result;
  }

  @RacTab
  @Secures
  public boolean isRacMember(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isAdmin(program);
    if (!result)
    {
      result = this.checkFunction("RAC Committee", program);
    }
    return result;
  }

  @Rac
  @Secures
  public boolean isRac(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isRacChair(program);
    if (!result)
    {
      result = this.isRacMember(program);
    }
    return result;
  }

  @ShipmentForms
  @Secures
  public boolean isShipment(@Named("currentProgram") String program)
      throws IdentityException
  {
    boolean result = this.isAdmin(program);
    if (!result)
    {
      result = this.checkFunction("Shipment Form", program);
    }
    return result;
  }

  @SuperAdmin
  @Secures
  public boolean isSuperAdmin() throws IdentityException
  {
    AcquireUserInformation current = this.admin.getCurrentUser();
    return current != null ? current.getSuperAdmin()
        : false;
  }

  public boolean checkFunction(String roleName, String program)
  {
    return identity.hasRole(roleName, program, Resources.TYPE_PROGRAM);
  }

  public boolean checkRole(String roleName, String site)
  {
    return identity.hasRole(roleName, site, Resources.TYPE_SITE);
  }

  public boolean isDataVisibleForProgram(DataVisibility visibility)
      throws IdentityException, FeatureNotSupportedException
  {
    boolean result = false;
    this.pm.setUser(this.admin.getCurrentUser());
    pm.setProgram(pm.getUserCurrentProgram());
    List<String> siteNames = pm.getProgramSites();
    for (String site : siteNames)
    {
      switch (visibility)
      {
      case PUBLIC:
      {
        result = this.isPublicData(pm.getUserCurrentProgram(), site);
        break;
      }
      case NON_PHI:
      {
        result = this.isNonPhi(pm.getUserCurrentProgram(), site);
        break;
      }
      case PHI:
      {
        result = this.isPhi(pm.getUserCurrentProgram(), site);
        break;
      }
      }

      if (result)
      {
        break;
      }
    }
    return result;
  }

  /**
   * Method used to determine if the current user has only public data
   * permissions.
   * 
   * @return true if the only role for the current user is the public data role,
   *         false otherwise.
   * @throws IdentityException
   */
  public boolean isPublicUser() throws IdentityException
  {
    boolean result = false;
    /*
     * Check for each program. If role is other than Public Data in any program,
     * then this is not a public user
     */
    for (String program : pm.getUserPrograms())
    {
      result = this.checkFunction("Public Data", program);
      if (!result)
      {
        break;
      }
    }

    return result;
  }

}
