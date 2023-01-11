package com.kenis.usermanager.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.kenis.usermanager.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.*;

/**
 * This class represents the service for sending emails.
 * It has the following methods:
 *
 * <ul>
 *     <li>{@link #sendNewPasswordEmail(String, String, String)} sends a new password email to the specified email address.</li>
 * </ul>
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
@Service
public class EmailService {
    /**
     * The username to use when authenticating with the email server.
     * you can you the Constant provided in Email Constant
     * for the purpose of not sharing my username & password I will use the one in the
     * ENVIRONMENT
     */
    @Value("${gmail.username}")
    private String ENVIRONMENT_USERNAME;

    /**
     * The password to use when authenticating with the email server.
     * you can you the Constant provided in Email Constant
     * for the purpose of not sharing my username & password I will use the one in the
     * ENVIRONMENT
     */
    @Value("${gmail.password}")
    private String ENVIRONMENT_PASSWORD ;

    /**
     * Sends a new password email to the specified email address.
     *
     * @param firstName the first name of the recipient.
     * @param password  the new password.
     * @param email     the email address of the recipient.
     * @throws MessagingException if an error occurs while sending the email.
     */
    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, ENVIRONMENT_USERNAME, ENVIRONMENT_PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    /**
     * Creates an email message with the specified recipient, subject, and text using the JavaMail API
     *
     * @param firstName the first name of the recipient.
     * @param password  the new password.
     * @param email     the email address of the recipient.
     * @return the email message.
     * @throws MessagingException if an error occurs while creating the email message.
     */
    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + ", \n \n Your new account password is: " + password + "\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    /**
     * This method returns an instance of a JavaMail {@link Session} object with the specified email server properties.
     * The email server used in this implementation is Gmail, so the host and default port are set accordingly.
     * Additionally, the SMTP transport layer security (TLS) is enabled and required for this session.
     *
     * @return an instance of a JavaMail {@code Session} object with the specified email server properties.
     */
    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }
}
