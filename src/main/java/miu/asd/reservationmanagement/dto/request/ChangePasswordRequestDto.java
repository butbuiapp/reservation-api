package miu.asd.reservationmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangePasswordRequestDto {
    @NotBlank
    @Length(min= 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;
    @NotBlank
    @Length(min= 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String oldPassword;
    @NotBlank
    @Length(min= 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String newPassword;
    @NotBlank
    @Length(min= 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String confirmPassword;
}
