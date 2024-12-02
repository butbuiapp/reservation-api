package miu.asd.reservationmanagement.repository;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRole(RoleEnum roleEnum);
}
