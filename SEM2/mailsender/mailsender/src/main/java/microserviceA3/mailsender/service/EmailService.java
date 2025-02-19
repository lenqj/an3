package microserviceA3.mailsender.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import microserviceA3.mailsender.model.EmailPayload;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;


    public void sendEmail(EmailPayload emailPayload) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(emailPayload.getRecipient());
            mimeMessageHelper.setSubject(emailPayload.getSubject());
            String message = "<div style=\"font-family: Arial, sans-serif\">" +
                    "<h2 style=\"color: red\"><strong>Salutare, " + emailPayload.getFirstName() + " " +
                    emailPayload.getLastName() + "!</strong></h2>" +
                    "<p>" + emailPayload.getBody() + "</p>" +
                    "</div>";
            mimeMessageHelper.setText(message, true);
            if (emailPayload.getAttachment() != null){
                ByteArrayResource attachment = new ByteArrayResource(emailPayload.getAttachment());
                mimeMessageHelper.addAttachment("attach." + emailPayload.getFileType(), attachment);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
