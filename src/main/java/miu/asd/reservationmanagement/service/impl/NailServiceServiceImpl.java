package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.ServiceStatusEnum;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.exception.RecordAlreadyExistsException;
import miu.asd.reservationmanagement.model.NailService;
import miu.asd.reservationmanagement.repository.NailServiceRepository;
import miu.asd.reservationmanagement.service.NailServiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NailServiceServiceImpl implements NailServiceService {
    private final NailServiceRepository nailServiceRepository;

    @Override
    public NailService saveService(NailService service) {
        Optional<NailService> optionalNailService = findByName(service.getName());
        if (optionalNailService.isPresent()) {
            throw new RecordAlreadyExistsException("Nail service already exists");
        } else {
            service.setStatus(ServiceStatusEnum.ACTIVE);
            return nailServiceRepository.save(service);
        }
    }

    @Override
    public void updateService(Integer id, NailService service) {
        findById(id).ifPresentOrElse(
                existingService -> {
                    Optional<NailService> optionalNailService = findByName(service.getName());
                    if (optionalNailService.isPresent() &&
                            optionalNailService.get().getId() != existingService.getId()) {
                        throw new RecordAlreadyExistsException("Nail service already exists");
                    } else {
                        existingService.setName(service.getName());
                        existingService.setPrice(service.getPrice());
                        existingService.setDuration(service.getDuration());
                        existingService.setDescription(service.getDescription());
                        nailServiceRepository.save(existingService);
                    }
                },
                () -> {
                    throw new ResourceNotFoundException("Nail service not found");
                }
        );
    }

    @Override
    public void deleteServiceById(Integer id) {
        findById(id).ifPresentOrElse(
                existingService -> {
                    existingService.setStatus(ServiceStatusEnum.DELETED);
                    nailServiceRepository.save(existingService);
                },
                () -> {
                    throw new ResourceNotFoundException("Nail service not found");
                }
        );
    }

    @Override
    public List<NailService> getAllServices() {
        return nailServiceRepository.findAllByStatusIs(ServiceStatusEnum.ACTIVE);
    }

    @Override
    public NailService getServiceById(Integer id) {
        Optional<NailService> optionalNailService = findById(id);
        if (optionalNailService.isPresent()) {
            return optionalNailService.get();
        } else {
            throw new ResourceNotFoundException("Nail service not found");
        }
    }

    private Optional<NailService> findById(Integer id) {
        return nailServiceRepository.findByIdAndStatus(id, ServiceStatusEnum.ACTIVE);
    }

    private Optional<NailService> findByName(String name) {
        return nailServiceRepository.findByNameAndStatus(name, ServiceStatusEnum.ACTIVE);
    }
}
