package miu.asd.reservationmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@Builder
public class CustomerRequestDto {
    private Long id;

    @NotBlank(message = "First Name is required")
    @Length(min= 1, max = 50, message = "First Name must be within 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Length(min= 1, max = 50, message = "Last Name must be within 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Length(min= 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;

    @Length(min= 0, max = 50, message = "Email must be within 50 characters")
    @Email(message = "Enter a valid email address")
    private String email;
    private LocalDate dob;

    @NotBlank(message = "Password is required")
    @Length(min= 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String password;
}
