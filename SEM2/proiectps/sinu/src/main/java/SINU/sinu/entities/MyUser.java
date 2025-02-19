package SINU.sinu.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public class MyUser {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Integer ID;
    @NotBlank(message = "Username cannot be null")
    @Column(unique=true)
    private String username;

    @NotBlank(message = "First name cannot be null")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be null")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be null")
    @Column(unique=true)
    private String emailAddress;

    @NotNull(message = "Password cannot be null")
    private String password;

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;



}
