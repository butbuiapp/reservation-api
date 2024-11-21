package miu.asd.reservationmanagement.repository;

import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneNumberAndStatus(String phoneNumber, UserStatusEnum status);
    Optional<Customer> findByIdAndStatus(Long id, UserStatusEnum status);
    List<Customer> findAllByStatusIsOrderByLastName(UserStatusEnum status);
}
