package miu.asd.reservationmanagement.repository;

import miu.asd.reservationmanagement.model.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {
    Optional<LoyaltyPoint> findByCustomerId(Long customerId);
    Optional<LoyaltyPoint> findByCustomerPhoneNumber(String phoneNumber);
}
