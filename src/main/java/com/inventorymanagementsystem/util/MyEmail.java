package com.inventorymanagementsystem.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.scene.control.Alert.AlertType;

import java.util.Properties;

public class MyEmail {

    public static boolean sendEmail(String fromEmail, String password, String toEmail, String subject, String body) {
        String host = getSmtpHost(fromEmail);

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully!");
            MyAlert.showAlert(AlertType.INFORMATION, "Successfully Sent Email",
                    "Email from " + fromEmail + " to " + toEmail + " was sent successfully!");
            return true;

        } catch (MessagingException e) {
            MyAlert.showAlert(AlertType.ERROR, "Error sending Email",
                    "Email from " + fromEmail + " to " + toEmail + " was not sent!" +
                            "\nError: " + e.getMessage());
            System.out.println("MessagingException Error: " + e.getMessage());
            return false;
        }
    }

    private static String getSmtpHost(String fromEmail) {
        String domain = fromEmail.substring(fromEmail.indexOf("@") + 1).toLowerCase();
        System.out.println("Email is being sent from " + domain);

        return switch (domain) {
            case "gmail.com" -> "smtp.gmail.com";
            case "proton.me", "protonmail.com", "protonmail.ch", "pm.me" -> "smtp.protonmail.com";
            case "yahoo.com" -> "smtp.mail.yahoo.com";
            case "outlook.com", "hotmail.com" -> "smtp.office365.com";
            default -> "Other";
        };
    }
}
