package miu.asd.reservationmanagement.repository;

import miu.asd.reservationmanagement.common.ServiceStatusEnum;
import miu.asd.reservationmanagement.model.NailService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NailServiceRepository extends JpaRepository<NailService, Integer> {
    List<NailService> findAllByStatusIs(ServiceStatusEnum status);
    Optional<NailService> findByIdAndStatus(Integer id, ServiceStatusEnum status);
    Optional<NailService> findByNameAndStatus(String name, ServiceStatusEnum status);
}
