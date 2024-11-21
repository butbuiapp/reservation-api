package miu.asd.reservationmanagement.repository;

import miu.asd.reservationmanagement.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByCustomerIdOrderByDateDescTimeDesc(Long customerId);
    List<Appointment> findAllByCustomerPhoneNumberOrderByDateDescTimeDesc(String phoneNumber);
    List<Appointment> findAllByDateOrderByTimeDesc(LocalDate date);
}
