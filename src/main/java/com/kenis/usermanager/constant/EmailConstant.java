package com.kenis.usermanager.constant;
/**
 * A class containing constants related to sending email messages.
 *
 * @author Mohamed Ali Kenis
 */
public class EmailConstant {

    /**
     * The simple mail transfer protocol to use for sending email messages.
     */
    public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL = "smtps";

    /**
     * The username to use when authenticating with the email server.
     */
    public static final String USERNAME = "infojuniorro@gmail.com";

    /**
     * The password to use when authenticating with the email server.
     */
    public static final String PASSWORD = "N99gmail1@";

    /**
     * The email address to use as the "from" address when sending email messages.
     */
    public static final String FROM_EMAIL = "support@getarrays.com";

    /**
     * The email address to use as the "cc" (carbon copy) address when sending email messages.
     */
    public static final String CC_EMAIL = "";

    /**
     * The subject line to use for email messages.
     */
    public static final String EMAIL_SUBJECT = "Get Arrays, LLC - New Password";

    /**
     * The Gmail SMTP server to use for sending email messages.
     */
    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";

    /**
     * The SMTP host property for the JavaMail API.
     */
    public static final String SMTP_HOST = "mail.smtp.host";

    /**
     * The SMTP authentication property for the JavaMail API.
     */
    public static final String SMTP_AUTH = "mail.smtp.auth";

    /**
     * The SMTP port property for the JavaMail API.
     */
    public static final String SMTP_PORT = "mail.smtp.port";

    /**
     * The default port to use for sending email messages.
     */
    public static final int DEFAULT_PORT = 465;

    /**
     * The SMTP starttls enable property for the JavaMail API.
     */
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

    /**
     * The SMTP starttls required property for the JavaMail API.
     */
    public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
}
