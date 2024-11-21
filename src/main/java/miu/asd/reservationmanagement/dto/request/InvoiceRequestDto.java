package miu.asd.reservationmanagement.dto.request;

import lombok.*;
import miu.asd.reservationmanagement.model.NailService;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequestDto {
    private LocalDateTime issuedDate;
    private double totalAmount;
    private List<NailService> services;
}
