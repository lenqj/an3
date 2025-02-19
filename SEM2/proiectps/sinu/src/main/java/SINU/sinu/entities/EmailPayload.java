package SINU.sinu.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class EmailPayload {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer ID;
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
    @NotNull(message = "Attachment must not be null")
    private byte[] attachment;
    @NotNull(message = "File type must not be null")
    @NotBlank(message = "File type must not be blank")
    private String fileType;

}
