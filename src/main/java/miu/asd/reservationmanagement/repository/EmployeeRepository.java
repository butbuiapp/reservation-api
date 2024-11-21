package miu.asd.reservationmanagement.repository;

import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByPhoneNumberAndStatus(String phoneNumber, UserStatusEnum status);
    Optional<Employee> findByIdAndStatus(Long id, UserStatusEnum status);
    List<Employee> findAllByStatusIsOrderByLastName(UserStatusEnum status);
}
