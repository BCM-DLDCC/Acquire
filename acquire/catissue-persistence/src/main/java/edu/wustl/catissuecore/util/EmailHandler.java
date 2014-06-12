/**
 * <p>Title: EmailHandler Class>
 * <p>Description:	EmailHandler is used to send emails during user signup, creation, forgot password.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.catissuecore.util;

import javax.mail.MessagingException;

import edu.wustl.catissuecore.domain.ReportedProblem;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.EmailDetails;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.SecurityManagerFactory;


/**
 * EmailHandler is used to send emails during user signup, creation, forgot password.
 * @author gautam_shetty
 */
public class EmailHandler
{
	private static final Logger logger = Logger.getCommonLogger(EmailHandler.class);
    /**
     * Creates and sends the user registration approval emails to user and the administrator.
     * @param user The user whose registration is approved.
     * @param roleOfUser Role of the user.
     */
    public void sendApprovalEmail(final User user) throws ApplicationException
    {
        final String subject = ApplicationProperties
        					.getValue("userRegistration.approve.subject");

        String body = "Dear " + user.getLastName() +
			        "," + user.getFirstName() +
			        "\n\n"+ ApplicationProperties.getValue("userRegistration.approved.body.start") +
			        getUserDetailsEmailBody(user); // Get the user details in the body of the email.

        //Send login details email to the user.
        sendLoginDetailsEmail(user, body);
        String migratedToWustlkey = "";
		if(user.getTargetIdpLoginName()!=null)
		{
			migratedToWustlkey = ApplicationProperties.getValue("migration.msg");
		}

		body = body + "\n\n" + migratedToWustlkey + "\n\n" +ApplicationProperties.getValue("userRegistration.thank.body.end") +
		"\n\n" + ApplicationProperties.getValue("email.catissuecore.team");

		//Send the user registration details email to the administrator.
        final boolean emailStatus = sendEmailToAdministrator(subject, body);

		if (emailStatus)
        {
            logger.info(ApplicationProperties
                    .getValue("user.approve.email.success")
                    + user.getLastName() + " " + user.getFirstName());
        }
        else
        {
            logger.info(ApplicationProperties
                    .getValue("user.approve.email.failure")
                    + user.getLastName() + " " + user.getFirstName());
        }
    }

    /**
     * Returns the users details to be incorporated in the email.
     * @param user The user object.
     * @return the users details to be incorporated in the email.
     */
    private String getUserDetailsEmailBody(final User user)
    {
        final String userDetailsBody = "\n\n" + ApplicationProperties.getValue("user.loginName")+ Constants.SEPARATOR + user.getLoginName() +
						"\n\n" + ApplicationProperties.getValue("user.lastName")+ Constants.SEPARATOR + user.getLastName() +
						"\n\n" + ApplicationProperties.getValue("user.firstName")+ Constants.SEPARATOR + user.getFirstName() +
						"\n\n" + ApplicationProperties.getValue("user.street")+ Constants.SEPARATOR + user.getAddress().getStreet() +
						"\n\n" + ApplicationProperties.getValue("user.city")+ Constants.SEPARATOR + user.getAddress().getCity() +
						"\n\n" + ApplicationProperties.getValue("user.zipCode")+ Constants.SEPARATOR + user.getAddress().getZipCode() +
						"\n\n" + ApplicationProperties.getValue("user.state")+ Constants.SEPARATOR + user.getAddress().getState() +
						"\n\n" + ApplicationProperties.getValue("user.country")+ Constants.SEPARATOR + user.getAddress().getCountry() +
						"\n\n" + ApplicationProperties.getValue("user.phoneNumber")+ Constants.SEPARATOR + user.getAddress().getPhoneNumber() +
						"\n\n" + ApplicationProperties.getValue("user.faxNumber")+ Constants.SEPARATOR + user.getAddress().getFaxNumber() +
						"\n\n" + ApplicationProperties.getValue("user.emailAddress")+ Constants.SEPARATOR + user.getEmailAddress() +
						"\n\n" + ApplicationProperties.getValue("user.institution")+ Constants.SEPARATOR + user.getInstitution().getName() +
						"\n\n" + ApplicationProperties.getValue("user.department")+ Constants.SEPARATOR + user.getDepartment().getName() +
						"\n\n" + ApplicationProperties.getValue("user.cancerResearchGroup")+ Constants.SEPARATOR + user.getCancerResearchGroup().getName();

        return userDetailsBody;
    }

    /**
     * Creates and sends the user registration rejection emails to user and the administrator.
     * @param user The user whose registration is rejected.
     */
    public void sendRejectionEmail(final User user)
    {
        final String subject = ApplicationProperties.getValue("userRegistration.reject.subject");

        String body = "Dear " + user.getLastName()
        + "," + user.getFirstName()
        + "\n\n"+ ApplicationProperties.getValue("userRegistration.reject.body.start");

        //Append the comments given by the administrator, if any.
        if ((user.getComments() != null)
                && (!"".equals(user.getComments())))
        {
            body = body + "\n\n" + ApplicationProperties.getValue("userRegistration.reject.comments")
            					 + user.getComments();
        }

        body = body + "\n\n"+ ApplicationProperties.getValue("userRegistration.thank.body.end")
        			+ "\n\n" + ApplicationProperties.getValue("email.catissuecore.team");

        final boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body);

        if (emailStatus)
        {
            logger.info(ApplicationProperties
                    .getValue("user.reject.email.success")
                    + user.getLastName() + " " + user.getFirstName());
        }
        else
        {
            logger.info(ApplicationProperties
                    .getValue("user.reject.email.success")
                    + user.getLastName() + " " + user.getFirstName());
        }
    }

    /**
     * Creates and sends the user signup request received email to the user and the administrator.
     * @param user The user registered for the membership.
     */
    public void sendUserSignUpEmail(final User user)
    {
        final String subject = ApplicationProperties.getValue("userRegistration.request.subject");

        final String body = "Dear "+ user.getLastName()+","+ user.getFirstName() + "\n\n" +
					  ApplicationProperties.getValue("userRegistration.request.body.start") + "\n" +
					  getUserDetailsEmailBody(user) +
					  "\n\n\t" + ApplicationProperties.getValue("userRegistration.request.body.end") +
					  "\n\n" + ApplicationProperties.getValue("email.catissuecore.team");

        final boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body);

        if (emailStatus)
        {
            logger.info(ApplicationProperties
                    .getValue("userRegistration.email.success")
                    + user.getLastName() + " " + user.getFirstName());
        }
        else
        {
            logger.info(ApplicationProperties
                    .getValue("userRegistration.email.failure")
                    + user.getLastName() + " " + user.getFirstName());
        }
    }

    /**
     * Creates and sends the login details email to the user.
     * Returns true if the email is successfully sent else returns false.
     * @param user The user whose login details are to be sent.
     * @param userDetailsBody User registration details.
     * @return true if the email is successfully sent else returns false.
     * @throws DAOException
     */
    public boolean sendLoginDetailsEmail(final User user, final String userDetailsBody) throws ApplicationException
    {
        boolean emailStatus = false;

        try
        {
            final String subject = ApplicationProperties
								.getValue("loginDetails.email.subject");

			String body = "Dear " + user.getFirstName()
			    				  + " " + user.getLastName();

			if (userDetailsBody != null)
			{
			    body = userDetailsBody;
			}

			final gov.nih.nci.security.authorization.domainobjects.User csmUser = SecurityManagerFactory.getSecurityManager().getUserById(user.getCsmUserId().toString());

//			List pwdList = new ArrayList(user.getPasswordCollection());
//			Collections.sort(pwdList);
//			Password password = (Password) pwdList.get(0);
//			String roleOfUser = SecurityManager.getInstance(EmailHandler.class)
//							.getUserRole(user.getCsmUserId().longValue()).getName();
			body = body + "\n\n" + ApplicationProperties.getValue("forgotPassword.email.body.start")
				+ "\n\t "+ ApplicationProperties.getValue("user.loginName")+ Constants.SEPARATOR + user.getLoginName()
    		    + "\n\t "+ ApplicationProperties.getValue("user.password")+ Constants.SEPARATOR + csmUser.getPassword()
			    + "\n\t "+ ApplicationProperties.getValue("user.role")// +  Constants.SEPARATOR + roleOfUser
			    + "\n\n" + ApplicationProperties.getValue("email.catissuecore.team");

			emailStatus = sendEmailToUser(user.getEmailAddress(), subject, body);
			logEmailStatus(user, emailStatus);
        }
        catch(final SMException smExp)
        {
        	logger.error(smExp.getMessage(), smExp);
            throw AppUtility.getApplicationException(smExp,smExp.getErrorKeyAsString(),  smExp.getMessage());
        }

        return emailStatus;
    }

	/**
	 * method logs email status
	 * @param user
	 * @param emailStatus
	 */
	private void logEmailStatus(final User user, final boolean emailStatus)
	{
		if (emailStatus)
		{
			logger.info(ApplicationProperties
			    .getValue("user.loginDetails.email.success")
			    + user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.info(ApplicationProperties
			    .getValue("user.loginDetails.email.failure")
			    + user.getLastName() + " " + user.getFirstName());
		}
	}

    /**
     * Sends email to the administrator and the user who reported the problem.
     * @param reportedProblem The problem reported.
     */
    public void sendReportedProblemEmail(final ReportedProblem reportedProblem)
    {
        // Send the reported problem to administrator and the user who reported it.
        final String body = ApplicationProperties.getValue("email.reportProblem.body.start") +
        			  "\n " + ApplicationProperties.getValue("reportedProblem.from") + " : " + reportedProblem.getFrom() +
        			  "\n " + ApplicationProperties.getValue("reportedProblem.title") + " : " + reportedProblem.getSubject() +
        			  "\n " + ApplicationProperties.getValue("reportedProblem.message") + " : " + reportedProblem.getMessageBody() +
        			  "\n\n" + ApplicationProperties.getValue("email.catissuecore.team");

        final String subject = ApplicationProperties.getValue("email.reportProblem.subject");

        final boolean emailStatus = sendEmailToUserAndAdministrator(reportedProblem.getFrom(), subject, body);

        if (emailStatus)
		{
			logger.info(ApplicationProperties
			    .getValue("reportedProblem.email.success"));
		}
		else
		{
			logger.info(ApplicationProperties
			    .getValue("reportedProblem.email.failure"));
		}
    }

    /**
     * Sends email to the user with the email address passed.
     * Returns true if the email is successfully sent else returns false.
     * @param userEmailAddress Email address of the user.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return true if the email is successfully sent else returns false.
     */
    private boolean sendEmailToUser(final String userEmailAddress, final String subject, String body)
    {
    	 final String mailServer = XMLPropertyHandler.getValue("email.mailServer");
 		final String sendFromEmailAddress = XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress");
 		final String appUrl = CommonServiceLocator.getInstance().getAppURL();
 		body = body + "\n\n" + ApplicationProperties.getValue("loginDetails.catissue.url.message") +
 		appUrl;

 		/*SendEmail email = new SendEmail();
         boolean emailStatus = email.sendmail(userEmailAddress, sendFromEmailAddress,
 				                				mailServer, subject, body);*/
         final EmailDetails emailDetails= new EmailDetails();
         emailDetails.setToAddress(new String[]{userEmailAddress});
         emailDetails.setSubject(subject);
         emailDetails.setBody(body);
         SendEmail email;
         boolean emailStatus;
 		try
 		{
 			email = new SendEmail(mailServer,sendFromEmailAddress);
 			emailStatus=email.sendMail(emailDetails);
 		}
 		catch (final MessagingException messExcp)
 		{
 			emailStatus=false;
 			logger.error(messExcp.getMessage(),messExcp);
 		}
         return emailStatus;
    }

    /**
     * Sends email to the administrator and user with the email address passed.
     * Returns true if the email is successfully sent else returns false.
     * @param userEmailAddress Email address of the user.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return true if the email is successfully sent else returns false.
     */
    private boolean sendEmailToUserAndAdministrator(final String userEmailAddress, final String subject, String body)
    {
    	final String adminEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
        final String sendFromEmailAddress = XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress");
        final String mailServer = XMLPropertyHandler.getValue("email.mailServer");
        final String appUrl = CommonServiceLocator.getInstance().getAppURL();
        body = body + "\n\n" + ApplicationProperties.getValue("loginDetails.catissue.url.message") +
        appUrl;

        /*SendEmail email = new SendEmail();
        boolean emailStatus = email.sendmail(userEmailAddress, adminEmailAddress,
                							 null, sendFromEmailAddress, mailServer, subject, body);*/
        final EmailDetails emailDetails= new EmailDetails();
        emailDetails.setToAddress(new String[]{userEmailAddress});
        emailDetails.setCcAddress(new String[]{adminEmailAddress});
        emailDetails.setSubject(subject);
        emailDetails.setBody(body);
        SendEmail email;
        boolean emailStatus;
		try
		{
			email = new SendEmail(mailServer,sendFromEmailAddress);
			emailStatus=email.sendMail(emailDetails);
		}
		catch (final MessagingException messExcp)
		{
			emailStatus=false;
			logger.error(messExcp.getMessage(),messExcp);
		}
        return emailStatus;
    }

    /**
     * Sends email to the administrator.
     * Returns true if the email is successfully sent else returns false.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return true if the email is successfully sent else returns false.
     */
    private boolean sendEmailToAdministrator(final String subject, String body)
    {
    	 final String adminEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
         final String sendFromEmailAddress = XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress");
         final String mailServer = XMLPropertyHandler.getValue("email.mailServer");
         final String appUrl = CommonServiceLocator.getInstance().getAppURL();
         body = body + "\n\n" + ApplicationProperties.getValue("loginDetails.catissue.url.message") +
         appUrl;

        /* SendEmail email = new SendEmail();
         boolean emailStatus = email.sendmail(adminEmailAddress,
                 								sendFromEmailAddress, mailServer, subject, body);*/
         final EmailDetails emailDetails= new EmailDetails();
         emailDetails.setToAddress(new String[]{adminEmailAddress});
         emailDetails.setSubject(subject);
         emailDetails.setBody(body);
         SendEmail email;
         boolean emailStatus;
 		try
 		{
 			email = new SendEmail(mailServer,sendFromEmailAddress);
 			emailStatus=email.sendMail(emailDetails);
 		}
 		catch (final MessagingException messExcp)
 		{
 			emailStatus=false;
 			logger.error(messExcp.getMessage(),messExcp);
 		}
         return emailStatus;
    }

    /**
     * Sends email to Administrator and CC to Scientist on successful placement of order.
     *
     * Returns true if mail is sent successfully.
     * @param none
     * @return boolean indicating true/false
     */

    public boolean sendEmailForOrderingPlacement(final String ccEmailAddress,final String emailBody,final String subject)
    {
    	final String toEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
    	final String sendFromEmailAddress = XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress");
    	final String mailServer = XMLPropertyHandler.getValue("email.mailServer");

    	/*SendEmail email = new SendEmail();
    	boolean emailStatus = email.sendmail(toEmailAddress, ccEmailAddress,null,
					sendFromEmailAddress, mailServer, subject, emailBody);*/
    	final EmailDetails emailDetails= new EmailDetails();
        emailDetails.setToAddress(new String[]{toEmailAddress});
        emailDetails.setCcAddress(new String[]{ccEmailAddress});
        emailDetails.setSubject(subject);
        emailDetails.setBody(emailBody);
        SendEmail email;
        boolean emailStatus;
		try
		{
			email = new SendEmail(mailServer,sendFromEmailAddress);
			emailStatus=email.sendMail(emailDetails);
		}
		catch (final MessagingException messExcp)
		{
			emailStatus=false;
			logger.error(messExcp.getMessage(),messExcp);
		}
    	return emailStatus;
    }
    /**
     * Sends email to Scientist and cc to Admin on distribution of the order.
     * @return
     */

    public boolean sendEmailForOrderDistribution(final String body,final String toEmailAddress,final String fromEmailAddress, final String ccEmailAddress, final String bccEmailAddress,  final String subject)
    {
    	final String mailServer = XMLPropertyHandler.getValue("email.mailServer");
        /*SendEmail email = new SendEmail();
        logger.info("Email body..........  \n"  + body);
        System.out.println("Email body..........  \n"  + body);
        boolean emailStatus = email.sendmail(toEmailAddress, ccEmailAddress, bccEmailAddress,
    			fromEmailAddress, mailServer, subject, body);*/
        final EmailDetails emailDetails= new EmailDetails();
        emailDetails.setToAddress(new String[]{toEmailAddress});
        emailDetails.setCcAddress(new String[]{ccEmailAddress});
        emailDetails.setBccAddress(new String[]{bccEmailAddress});
        emailDetails.setSubject(subject);
        emailDetails.setBody(body);
        SendEmail email;
        boolean emailStatus;
		try
		{
			email = new SendEmail(mailServer,fromEmailAddress);
			emailStatus=email.sendMail(emailDetails);
		}
		catch (final MessagingException messExcp)
		{
			emailStatus=false;
			logger.error(messExcp.getMessage(),messExcp);
		}

    	logger.info("EmailStatus  "  + emailStatus);
        return emailStatus;
    }


}