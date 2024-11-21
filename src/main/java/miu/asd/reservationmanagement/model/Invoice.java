package miu.asd.reservationmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import miu.asd.reservationmanagement.common.InvoiceStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime issuedDate;
    private double totalAmount;
    private LocalDateTime paidDate;

    @ManyToMany
    @JoinTable(name = "invoice_item",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<NailService> services;

    @Enumerated(EnumType.STRING)
    private InvoiceStatusEnum status;
}
