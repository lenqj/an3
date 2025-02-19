package SINU.sinu.service;

import SINU.sinu.dto.MyUserAuthDTO;
import SINU.sinu.entities.EmailPayload;
import SINU.sinu.repository.EmailPayloadRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailPayloadRepository emailPayloadRepository;
    public EmailServiceImpl(EmailPayloadRepository emailPayloadRepository) {
        this.emailPayloadRepository = emailPayloadRepository;
    }

    @Override
    public void sendEmail(EmailPayload emailPayload) {
        EmailPayload payload = emailPayloadRepository.save(emailPayload);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json"));
        headers.setBearerAuth("a/b");

        HttpEntity<EmailPayload> request = new HttpEntity<>(payload, headers);
        restTemplate.postForObject("http://localhost:8081/emailsender", request, String.class);
    }

    @Override
    public void sendRegistrationEmail(MyUserAuthDTO myUserAuthDTO) {
        sendEmail(EmailPayload.builder()
                .subject("Salut, " + myUserAuthDTO.getUsername() + "!")
                .recipient(myUserAuthDTO.getEmailAddress())
                .body(myUserAuthDTO.getUsername() + " te-ai inregistrat cu succes!")
                .firstName(myUserAuthDTO.getFirstName())
                .lastName(myUserAuthDTO.getLastName())
                .build());
    }
}
