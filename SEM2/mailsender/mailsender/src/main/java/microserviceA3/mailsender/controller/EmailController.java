package microserviceA3.mailsender.controller;


import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import microserviceA3.mailsender.model.EmailPayload;
import microserviceA3.mailsender.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

@RestController
@RequestMapping("emailsender")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailPayload emailPayload, Errors errors) throws MessagingException {

        if(!isValidAuthorization(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token");
        }
        if(errors.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Objects.requireNonNull(errors.getFieldError()).getDefaultMessage());
        }
        emailService.sendEmail(emailPayload);
        return ResponseEntity.status(HttpStatus.OK).body("Email successfully sent!");
    }

    private boolean isValidAuthorization(String authorizationHeader) {
        if (authorizationHeader == null) {
            return false;
        }
        String[] uuids = authorizationHeader.split("/");
        return uuids.length == 2;
    }
}
