package miu.asd.reservationmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequestDto {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String password;
}
