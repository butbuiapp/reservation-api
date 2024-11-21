package miu.asd.reservationmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;
import miu.asd.reservationmanagement.common.UserStatusEnum;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private LocalDate dob;
    private UserStatusEnum status;
    private Integer earnedPoint;
}
