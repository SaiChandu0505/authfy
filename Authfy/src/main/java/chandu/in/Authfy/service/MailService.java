package chandu.in.Authfy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import static chandu.in.Authfy.constants.Constants.WELCOME_SUBJECT;
import static chandu.in.Authfy.constants.Constants.WELCOME_TEXT_TEMPLATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final MailSender mailSender;
    @Value("{spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("chandusai.k97@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(WELCOME_SUBJECT);
        mailMessage.setText("Hi " + name + WELCOME_TEXT_TEMPLATE);
        mailSender.send(mailMessage);
    }
}
