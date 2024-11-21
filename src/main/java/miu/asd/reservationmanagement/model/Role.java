package miu.asd.reservationmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import miu.asd.reservationmanagement.common.RoleEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
