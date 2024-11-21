package miu.asd.reservationmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import miu.asd.reservationmanagement.common.ServiceStatusEnum;
import org.hibernate.validator.constraints.Length;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "service")
public class NailService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    @NotBlank(message = "Name is required")
    @Length(min= 1, max = 50, message = "Name must be within 50 characters")
    private String name;
    @Digits(integer = 3, fraction = 0, message = "Price must not be greater than 3 digits")
    private Double price;
    @Digits(integer = 3, fraction = 0, message = "Duration must not be greater than 3 digits")
    private Integer duration;
    private String description;

    @Enumerated(EnumType.STRING)
    private ServiceStatusEnum status;
}
