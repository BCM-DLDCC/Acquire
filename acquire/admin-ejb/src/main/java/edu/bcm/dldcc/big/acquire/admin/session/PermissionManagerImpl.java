package edu.bcm.dldcc.big.acquire.admin.session;

import static edu.bcm.dldcc.big.acquire.util.Resources.TYPE_PROGRAM;
import static edu.bcm.dldcc.big.acquire.util.Resources.TYPE_SITE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.security.Identity;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RelationshipManager;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.admin.PermissionManager;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.Admin;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.SuperAdmin;
import edu.bcm.dldcc.big.acquire.qualifiers.Permissions;
import edu.bcm.dldcc.big.acquire.security.Authorizations;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation_;
import edu.bcm.dldcc.big.security.controller.AdminUtility;
import edu.bcm.dldcc.big.security.controller.SuperAdminUtility;
import edu.bcm.dldcc.big.security.exception.AddRelationshipException;
import edu.bcm.dldcc.big.security.exception.UserNotFoundException;
import edu.bcm.dldcc.big.util.qualifier.Current;

/**
 * Session Bean implementation class PermissionManagerImpl
 */
@Stateful
@Named("acquirePermissionManager")
@ConversationScoped
public class PermissionManagerImpl implements PermissionManager
{

  private static final String BLOCKED = "Blocked";
  private static final String NO_SELECTION = "No Selection";
  private String userCurrentProgram = "TCRB";
  private AcquireUserInformation user;
  private String site;
  private String program;
  private String role;
  private List<String> function;
  private String programRole;
  private List<String> addedFunctions = new ArrayList<String>();
  private List<String> removedFunctions = new ArrayList<String>();
  private List<String> programList = new ArrayList<String>();

  @Inject
  @Permissions
  private Map<String, List<String>> programSiteMap;

  @Inject
  private IdentitySession identitySession;

  @Inject
  private Identity identity;

  @Inject
  private Authorizations auth;

  @Inject
  private EntityManager em;
  
  @Inject
  @Current
  private AcquireUserInformation currentUser;
  
  @Inject
  private SuperAdminUtility superAdminUtil;
  
  @Inject
  private AdminUtility adminUtil;

  /**
   * Default constructor.
   */
  public PermissionManagerImpl()
  {
    super();
  }

  @PostConstruct
  public void fetchPrograms() throws IdentityException
  {
    Collection<Group> programs =
        identitySession.getPersistenceManager().findGroup(TYPE_PROGRAM);
    for (Group program : programs)
    {
      programList.add(program.getName());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getUserPrograms()
   */
  @Override
  public List<String> getUserPrograms() throws IdentityException
  {
    List<String> programList = new ArrayList<String>();

    if (this.identity.isLoggedIn())
    {
      RelationshipManager rm = identitySession.getRelationshipManager();
      Collection<Group> userPrograms =
          rm.findRelatedGroups(identity.getUser(), TYPE_PROGRAM, null);
      for (Group program : userPrograms)
      {
        programList.add(program.getName());
      }
    }
    return programList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#setUserCurrentProgram
   * (java.lang.String)
   */
  @Override
  public void setUserCurrentProgram(String program)
  {
    this.userCurrentProgram = program;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#getUserCurrentProgram()
   */
  @Override
  @Produces
  @Named("currentProgram")
  public String getUserCurrentProgram()
  {
    return this.userCurrentProgram;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getAllPrograms()
   */
  @Override
  public List<String> getAllPrograms() throws IdentityException
  {
    return programList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getAllSites()
   */
  @Override
  public List<String> getAllSites() throws IdentityException
  {
    List<String> siteList = new ArrayList<String>();
    Collection<Group> sites =
        identitySession.getPersistenceManager().findGroup(TYPE_SITE);
    for (Group site : sites)
    {
      siteList.add(site.getName());
    }
    return siteList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getProgramSites()
   */
  @Override
  public List<String> getProgramSites() throws IdentityException
  {
    return this.programSiteMap.get(this.getProgram());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#getUserProgramSites()
   */
  @Override
  public List<String> getUserProgramSites() throws IdentityException
  {
    RelationshipManager rm = identitySession.getRelationshipManager();
    Collection<Group> programSites =
        rm.findAssociatedGroups(identitySession.getPersistenceManager()
            .findGroup(this.getProgram(), TYPE_PROGRAM), TYPE_SITE, true, true,
            null);
    Collection<Group> userSites =
        rm.findAssociatedGroups(this.getUser().getIdentityId(), TYPE_SITE, null);
    programSites.retainAll(userSites);
    List<String> siteList = new ArrayList<String>();
    for (Group site : programSites)
    {
      siteList.add(site.getName());
    }
    Collections.sort(siteList);
    return siteList;
  }

  @SessionScoped
  @Produces
  @Named("currentSites")
  @Override
  public List<String> getCurrentSites() throws IdentityException,
      FeatureNotSupportedException
  {
    List<String> siteList = new ArrayList<String>();
    if (auth.isSuperAdmin() || auth.isAdmin(this.getUserCurrentProgram()))
    {
      this.setUser(this.currentUser);
      this.setProgram(this.getUserCurrentProgram());
      siteList = this.getProgramSites();
    }
    else
    {
      RelationshipManager rm = identitySession.getRelationshipManager();
      Collection<Group> programSites =
          rm.findAssociatedGroups(identitySession.getPersistenceManager()
              .findGroup(this.getUserCurrentProgram(), TYPE_PROGRAM),
              TYPE_SITE, true, true, null);
      Collection<Group> userSites =
          rm.findRelatedGroups(identity.getUser().getId(), TYPE_SITE, null);
      programSites.retainAll(userSites);

      for (Group site : programSites)
      {
        siteList.add(site.getName());
      }
    }
    return siteList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getUser()
   */
  @Override
  public AcquireUserInformation getUser()
  {
    return this.user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#setUser(java.lang.String)
   */
  @Override
  public void setUser(AcquireUserInformation username)
      throws IdentityException, FeatureNotSupportedException
  {
    if ((username == null || this.user == null)
        || !username.getIdentityId().equals(this.user.getIdentityId()))
    {
      this.user = username;
      this.setProgram(null);
      this.setSite(null);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getSite()
   */
  @Override
  public String getSite()
  {
    return this.site;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#setSite(java.lang.String)
   */
  @Override
  public void setSite(String sitename) throws IdentityException,
      FeatureNotSupportedException
  {
    if (sitename == null)
    {
      this.site = sitename;
      this.setRole(null);
    }
    else if (!sitename.equals(this.site))
    {
      this.site = sitename;
      List<String> roles = this.listRoles();
      if (!roles.isEmpty())
      {
        this.setRole(roles.get(0));
      }
      else
      {
        this.setRole(PermissionManagerImpl.BLOCKED);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getProgram()
   */
  @Override
  public String getProgram()
  {
    return this.program;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#setProgram(java.lang.
   * String)
   */
  @Override
  public void setProgram(String programname) throws IdentityException,
      FeatureNotSupportedException
  {
    if (programname == null)
    {
      this.program = programname;
      this.setFunctions(null);
      this.setSite(null);
      this.setProgramRole(null);
      this.setRole(null);
    }
    else if (!programname.equals(this.program))
    {
      this.program = programname;
      this.setFunctions(this.listFunctions());
      List<String> roles = this.listProgramRoles();
      if (!roles.isEmpty())
      {
        this.setProgramRole(roles.get(0));
      }
      else
      {
        this.setProgramRole(PermissionManagerImpl.NO_SELECTION);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getRoles()
   */
  @Override
  public List<String> getRoleChoices()
  {
    List<String> roles = new ArrayList<String>();
    roles.add("PHI");
    roles.add("Non PHI");
    roles.add(PermissionManagerImpl.BLOCKED);
    return roles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getFunctions()
   */
  @Override
  public List<String> getFunctionChoices()
  {
    List<String> functions = new ArrayList<String>();
    functions.add("Announcement Management");
    functions.add("RAC Chair");
    functions.add("Pathology Tab");
    functions.add("RAC Committee");
    functions.add("Shipment Form");
    return functions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getRole()
   */
  @Override
  public String getRole()
  {
    return this.role;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#setRole(java.lang.String)
   */
  @Override
  public void setRole(String roleName)
  {
    this.role = roleName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getFunction()
   */
  @Override
  public List<String> getFunctions()
  {
    return this.function;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.PermissionManager#setFunction(java.lang
   * .String)
   */
  @Override
  public void setFunctions(List<String> functionName) throws IdentityException,
      FeatureNotSupportedException
  {
    this.function = functionName;
    if (functionName != null)
    {
      List<String> currentFunctions = this.listFunctions();
      this.addedFunctions = new ArrayList<String>(functionName);
      this.addedFunctions.removeAll(currentFunctions);
      this.removedFunctions = new ArrayList<String>(currentFunctions);
      this.removedFunctions.removeAll(functionName);
    }
  }

  /**
   * 
   * 
   * @throws FeatureNotSupportedException 
   * @throws IdentityException 
   * @throws AddRelationshipException 
   * @throws UserNotFoundException 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#addRole()
   */
  @Override
  @Admin
  public void addRole() throws IdentityException, FeatureNotSupportedException,
      UserNotFoundException, AddRelationshipException 
  
  {
    this.clearRoles();
    if (!this.getRole().equals(PermissionManagerImpl.BLOCKED))
    {
      this.adminUtil.addRelationship(this.getUser().getIdentityId(), this.getRole(),
          this.getSite(), TYPE_SITE);

    }
  }

  @Override
  @Admin
  public void clearRoles() throws IdentityException,
      FeatureNotSupportedException
  {

    Group site =
        this.identitySession.getPersistenceManager().findGroup(this.getSite(),
            TYPE_SITE);
    for (String role : this.listRoles())
    {
      this.identitySession.getRoleManager().removeRole(role,
          this.getUser().getIdentityId(), site.getKey());

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#addFunction()
   */
  @Override
  @Admin
  public void updateFunctions() throws IdentityException,
      FeatureNotSupportedException
  {
    Group program =
        this.identitySession.getPersistenceManager().findGroup(
            this.getProgram(), TYPE_PROGRAM);
    User user =
        this.identitySession.getPersistenceManager().findUser(
            this.getUser().getIdentityId());
    for (String name : this.addedFunctions)
    {
      identitySession.getRoleManager().createRole(name,
          this.getUser().getIdentityId(), program.getKey());
    }

    for (String remove : this.removedFunctions)
    {
      RoleType type = identitySession.getRoleManager().getRoleType(remove);
      identitySession.getRoleManager().removeRole(type, user, program);
    }

  }

  @Admin
  @Override
  public void clearProgramRoles() throws IdentityException,
      FeatureNotSupportedException
  {
    Group program =
        this.identitySession.getPersistenceManager().findGroup(
            this.getProgram(), TYPE_PROGRAM);
    for (String role : this.listProgramRoles())
    {
      identitySession.getRoleManager().removeRole(role,
          this.getUser().getIdentityId(), program.getKey());
    }
  }

  @Admin
  @Override
  public void clearFunctions() throws IdentityException,
      FeatureNotSupportedException
  {
    Group program =
        this.identitySession.getPersistenceManager().findGroup(
            this.getProgram(), TYPE_PROGRAM);
    User user =
        this.identitySession.getPersistenceManager().findUser(
            this.getUser().getIdentityId());
    for (String function : this.listFunctions())
    {
      RoleType type = identitySession.getRoleManager().getRoleType(function);
      identitySession.getRoleManager().removeRole(type, user, program);
    }
  }

  @Override
  @Admin
  public void removeRole() throws IdentityException,
      FeatureNotSupportedException
  {
    Group program =
        this.identitySession.getPersistenceManager().findGroup(
            this.getProgram(), TYPE_PROGRAM);
    Group site =
        this.identitySession.getPersistenceManager().findGroup(this.getSite(),
            TYPE_SITE);
    User user =
        this.identitySession.getPersistenceManager().findUser(
            this.getUser().getIdentityId());
    RoleType type =
        identitySession.getRoleManager().getRoleType(this.getRole());
    identitySession.getRoleManager().removeRole(type, user, site);
    identitySession.getRoleManager().removeRole(type, user, program);

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#addSiteToProgram()
   */
  @Override
  @SuperAdmin
  public void addSiteToProgram() throws IdentityException
  {
    Group program =
        identitySession.getPersistenceManager().findGroup(this.getProgram(),
            TYPE_PROGRAM);
    Group site =
        identitySession.getPersistenceManager().findGroup(this.getSite(),
            TYPE_SITE);
    identitySession.getRelationshipManager().associateGroups(program, site);
    this.programSiteMap.get(program.getName()).add(site.getName());
  }

  public boolean isSiteInProgram(String site, String program)
      throws IdentityException
  {
    boolean member = false;
    if (this.programSiteMap.containsKey(program))
    {
      member = this.programSiteMap.get(program).contains(site);
    }

    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.PermissionManager#getProgramRoles()
   */
  @Override
  public List<String> getProgramRoleChoices()
  {
    List<String> functions = new ArrayList<String>();
    functions.add(PermissionManagerImpl.NO_SELECTION);
    functions.add("Admin");
    functions.add("Public Data");
    return functions;
  }

  /**
   * @return the programRole
   */
  public String getProgramRole()
  {
    return this.programRole;
  }

  /**
   * @param programRole
   *          the programRole to set
   */
  public void setProgramRole(String programRole)
  {
    this.programRole = programRole;
  }

  @Admin
  @Override
  public void addProgramRole() throws IdentityException,
      FeatureNotSupportedException
  {
    this.clearProgramRoles();
    if (!this.getProgramRole().equals(PermissionManagerImpl.NO_SELECTION))
    {
      Group program =
          this.identitySession.getPersistenceManager().findGroup(
              this.getProgram(), TYPE_PROGRAM);
      identitySession.getRoleManager().createRole(this.getProgramRole(),
          this.getUser().getIdentityId(), program.getKey());
    }
  }

  @Admin
  @Override
  public void removeProgramRole() throws IdentityException,
      FeatureNotSupportedException
  {
    Group program =
        this.identitySession.getPersistenceManager().findGroup(
            this.getProgram(), TYPE_PROGRAM);
    User identityUser =
        this.identitySession.getPersistenceManager().findUser(
            this.getUser().getIdentityId());
    RoleType type =
        identitySession.getRoleManager().getRoleType(this.getProgramRole());
    identitySession.getRoleManager().removeRole(type, identityUser, program);
  }

  @Admin
  @Override
  public List<String> listRoles() throws IdentityException,
      FeatureNotSupportedException
  {
    List<String> roleList = new ArrayList<String>();
    if (this.getSite() != null && !this.getSite().isEmpty()
        && this.getProgram() != null && !this.getProgram().isEmpty()
        && this.isSiteInProgram(site, program))
    {

      Collection<RoleType> types =
          identitySession.getRoleManager().findRoleTypes(
              identitySession.getPersistenceManager().findUser(
                  this.getUser().getIdentityId()),
              identitySession.getPersistenceManager().findGroup(this.getSite(),
                  TYPE_SITE));
      for (RoleType type : types)
      {
        roleList.add(type.getName());
      }
    }

    return roleList;
  }

  @Admin
  @Override
  public List<String> listProgramRoles() throws IdentityException,
      FeatureNotSupportedException
  {
    List<String> programRoles =
        fetchProgramRoles(this.getUser().getIdentityId(), this.getProgram());
    programRoles.retainAll(this.getProgramRoleChoices());
    return programRoles;
  }

  @Admin
  @Override
  public List<String> listFunctions() throws IdentityException,
      FeatureNotSupportedException
  {
    List<String> programRoles =
        fetchProgramRoles(this.getUser().getIdentityId(), this.getProgram());
    programRoles.retainAll(this.getFunctionChoices());
    return programRoles;
  }

  private List<String> fetchProgramRoles(String userName, String program)
      throws IdentityException, FeatureNotSupportedException
  {
    List<String> roleList = new ArrayList<String>();

    if (program != null && !program.isEmpty())
    {
      Collection<RoleType> types =
          identitySession.getRoleManager().findRoleTypes(
              identitySession.getPersistenceManager().findUser(userName),
              identitySession.getPersistenceManager().findGroup(program,
                  TYPE_PROGRAM));
      for (RoleType type : types)
      {
        roleList.add(type.getName());
      }
    }
    return roleList;
  }


}
