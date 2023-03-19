package org.connection;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;


import java.io.File;
import java.io.IOException;

public class EmailSender {
    private final Mailer mailer;

    public EmailSender() {
        mailer = (Mailer) MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "jakubturek32@gmail.com", "aqenczznvodwjhvc")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer();
    }

    public static void main(String[] args) {
        EmailSender s = new EmailSender();
        System.out.println(new String(Base64.encodeBase64URLSafe("jakub".getBytes())));
        s.sendConfirmationEmail("jakub.turek@student.gyarab.cz", "ioL/jkhjchHGhkjbJHB");
        s.sendResetPasswordEmail("jakub.turek@student.gyarab.cz", "ioL/jkhjchHGhkjbJHB");

    }

    public void sendResetPasswordEmail(String emailAddress, String url) {
        sendEmail(emailAddress, "Změna Hesla","reset/"+url,"PasswordResetEmail.html");
    }

    public void sendConfirmationEmail(String emailAddress, String url) {

        sendEmail(emailAddress, "Potvrzení registrace","validation/"+url,"ConfirmationEmail.html");
    }

    private void sendEmail(String emailAddress, String subject, String url, String templateName) {
        File emailTemplate = new File("./EmailTemplates/" + templateName);
        Document document;
        try {
            document = Jsoup.parse(emailTemplate);
        } catch (IOException e) {
            throw new RuntimeException("Nepodařilo se načíst šablonu emailu " + templateName + "ze složky EmailTemplates");
        }
        document.getElementById("confirmationUrl").attr("href", "http://localhost:8080/" + url);
        Email email = EmailBuilder.startingBlank()
                .from("CustomChess", "customchess@gmail.com")
                .to("Customer", emailAddress)
                .withSubject(subject)
                .withHTMLText(document.html())
                .buildEmail();
        mailer.sendMail(email);
    }


}
