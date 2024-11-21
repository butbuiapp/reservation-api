package miu.asd.reservationmanagement.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDto {
    @NotNull(message = "Date is required")
    @Future(message = "Date must be after today")
    private LocalDate date;

    @NotNull(message = "Time is required")
    private LocalTime time;
    private CustomerRequestDto customer;
    private EmployeeRequestDto technician;

    @Length(min = 0, max = 255, message = "Notes must be within 255 characters")
    private String notes;
    private InvoiceRequestDto invoice;
}
