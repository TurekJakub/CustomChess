package org.connection;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

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
        s.sendConfirmationEmail("jakub.turek@student.gyarab.cz","jkhjchHGhkjbJHB");

    }
    public void sendConfirmationEmail(String emailAddress,String urlToken){
        Email email = EmailBuilder.startingBlank()
                .from("CustomChess", "customchess@gmail.com")
                .to("Customer", emailAddress)
                .withSubject("Potvrzení registrace")
                .withHTMLText("<a href=\"https://www.w3schools.com\">Visit W3Schools</a>")
                //.withPlainText("Dobrý den,\\n jsme rádi, že jste se rozhodli pro naše služby. http://127.0.0.1:8000/validation/" + urlToken)
                .buildEmail();
        mailer.sendMail(email);
    }

}
