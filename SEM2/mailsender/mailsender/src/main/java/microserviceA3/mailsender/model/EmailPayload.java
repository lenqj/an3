package microserviceA3.mailsender.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailPayload {
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotBlank(message = "First name must not be blank")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @NotNull(message = "Recipient email must not be null")
    @Email(message = "Invalid email format")
    private String recipient;

    @NotNull(message = "Subject must not be null")
    @NotBlank(message = "Subject must not be blank")
    private String subject;

    @NotNull(message = "Body must not be null")
    @NotBlank(message = "Body must not be blank")
    private String body;
    private byte[] attachment;
    private String fileType;
}