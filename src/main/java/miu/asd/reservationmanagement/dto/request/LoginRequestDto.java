package miu.asd.reservationmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String password;
}
