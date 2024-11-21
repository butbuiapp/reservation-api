package miu.asd.reservationmanagement.dto.request;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSearchRequestDto {
    LocalDate appointmentDate;
}
