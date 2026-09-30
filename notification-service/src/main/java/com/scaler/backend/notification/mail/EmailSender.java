package com.scaler.backend.notification.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Thin wrapper around the plain JavaMail API. Gmail's SMTP relay (with an
 * app password, since Gmail no longer accepts the account password itself
 * for this) is what this was built against, but any SMTP host works by
 * overriding notification.mail.host/port.
 */
@Slf4j
@Component
public class EmailSender {

    @Value("${notification.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${notification.mail.port:587}")
    private String port;

    @Value("${notification.mail.username:}")
    private String username;

    @Value("${notification.mail.app-password:}")
    private String appPassword;

    public void send(String to, String subject, String body) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(appPassword)) {
            log.warn("notification.mail.username/app-password are not configured - skipping email to {}", to);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            log.info("Sent email to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}
