package miu.asd.reservationmanagement.service.impl;

import miu.asd.reservationmanagement.common.ServiceStatusEnum;
import miu.asd.reservationmanagement.exception.RecordAlreadyExistsException;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.model.NailService;
import miu.asd.reservationmanagement.repository.NailServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NailServiceServiceImplTest {
    @Mock
    private NailServiceRepository nailServiceRepository;
    @InjectMocks
    private NailServiceServiceImpl nailServiceService;

    @Test
    void testSaveService_newService_shouldSaveSuccessfully() {
        // Arrange
        NailService newService = new NailService();
        newService.setName("Manicure");

        when(nailServiceRepository.findByNameAndStatus("Manicure", ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());
        when(nailServiceRepository.save(newService)).thenReturn(newService);

        // Act
        NailService savedService = nailServiceService.saveService(newService);

        // Assert
        assertEquals(ServiceStatusEnum.ACTIVE, savedService.getStatus());
        verify(nailServiceRepository).save(newService);
    }

    @Test
    void testSaveService_duplicateService_shouldThrowException() {
        // Arrange
        NailService existingService = new NailService();
        existingService.setName("Pedicure");
        existingService.setStatus(ServiceStatusEnum.ACTIVE);

        when(nailServiceRepository.findByNameAndStatus("Pedicure", ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingService));

        NailService newService = new NailService();
        newService.setName("Pedicure");

        // Act & Assert
        RecordAlreadyExistsException exception = assertThrows(
                RecordAlreadyExistsException.class,
                () -> nailServiceService.saveService(newService)
        );
        assertEquals("Nail service already exists", exception.getMessage());
        verify(nailServiceRepository, never()).save(any(NailService.class));
    }

    @Test
    void testUpdateService_existingService_shouldUpdateSuccessfully() {
        // Arrange
        Integer serviceId = 1;
        NailService existingService = new NailService();
        existingService.setId(serviceId);
        existingService.setName("Pedicure");

        NailService updatedService = new NailService();
        updatedService.setName("Manicure");
        updatedService.setPrice(25.0);
        updatedService.setDuration(30);
        updatedService.setDescription("Updated Description");

        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingService));
        when(nailServiceRepository.findByNameAndStatus("Manicure", ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act
        nailServiceService.updateService(serviceId, updatedService);

        // Assert
        assertEquals("Manicure", existingService.getName());
        assertEquals(25.0, existingService.getPrice());
        assertEquals(30, existingService.getDuration());
        assertEquals("Updated Description", existingService.getDescription());
        verify(nailServiceRepository).save(existingService);
    }

    @Test
    void testUpdateService_serviceNotFound_shouldThrowException() {
        // Arrange
        Integer serviceId = 1;
        NailService updatedService = new NailService();
        updatedService.setName("Manicure");

        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> nailServiceService.updateService(serviceId, updatedService)
        );
        assertEquals("Nail service not found", exception.getMessage());
        verify(nailServiceRepository, never()).save(any());
    }

    @Test
    void testUpdateService_duplicateName_shouldThrowException() {
        // Arrange
        Integer serviceId = 1;
        NailService existingService = new NailService();
        existingService.setId(serviceId);
        existingService.setName("Pedicure");

        NailService conflictingService = new NailService();
        conflictingService.setId(2);
        conflictingService.setName("Manicure");

        NailService updatedService = new NailService();
        updatedService.setName("Manicure");

        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingService));
        when(nailServiceRepository.findByNameAndStatus("Manicure", ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.of(conflictingService));

        // Act & Assert
        RecordAlreadyExistsException exception = assertThrows(
                RecordAlreadyExistsException.class,
                () -> nailServiceService.updateService(serviceId, updatedService)
        );
        assertEquals("Nail service already exists", exception.getMessage());
        verify(nailServiceRepository, never()).save(any());
    }

    @Test
    void testDeleteServiceById_serviceExists_shouldMarkAsDeleted() {
        // Arrange
        Integer serviceId = 1;
        NailService existingService = new NailService();
        existingService.setId(serviceId);
        existingService.setStatus(ServiceStatusEnum.ACTIVE);

        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingService));

        // Act
        nailServiceService.deleteServiceById(serviceId);

        // Assert
        assertEquals(ServiceStatusEnum.DELETED, existingService.getStatus());
        verify(nailServiceRepository).save(existingService);
    }

    @Test
    void testDeleteServiceById_serviceNotFound_shouldThrowException() {
        // Arrange
        Integer serviceId = 1;

        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> nailServiceService.deleteServiceById(serviceId)
        );
        assertEquals("Nail service not found", exception.getMessage());
        verify(nailServiceRepository, never()).save(any());
    }

    @Test
    void testGetAllServices_shouldReturnActiveServices() {
        // Arrange
        NailService service1 = new NailService();
        service1.setId(1);
        service1.setName("Manicure");
        service1.setStatus(ServiceStatusEnum.ACTIVE);

        NailService service2 = new NailService();
        service2.setId(2);
        service2.setName("Pedicure");
        service2.setStatus(ServiceStatusEnum.ACTIVE);

        List<NailService> mockServices = Arrays.asList(service1, service2);
        when(nailServiceRepository.findAllByStatusIs(ServiceStatusEnum.ACTIVE)).thenReturn(mockServices);

        // Act
        List<NailService> result = nailServiceService.getAllServices();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Manicure", result.get(0).getName());
        assertEquals("Pedicure", result.get(1).getName());
    }

    @Test
    void testGetAllServices_noActiveServices_shouldReturnEmptyList() {
        // Arrange
        when(nailServiceRepository.findAllByStatusIs(ServiceStatusEnum.ACTIVE)).thenReturn(List.of());

        // Act
        List<NailService> result = nailServiceService.getAllServices();

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    void testGetServiceById_serviceExists_shouldReturnService() {
        // Arrange
        Integer serviceId = 1;
        NailService mockService = new NailService();
        mockService.setId(serviceId);
        mockService.setName("Manicure");
        mockService.setStatus(ServiceStatusEnum.ACTIVE);

        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.of(mockService));

        // Act
        NailService result = nailServiceService.getServiceById(serviceId);

        // Assert
        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        assertEquals("Manicure", result.getName());
    }

    @Test
    void testGetServiceById_serviceDoesNotExist_shouldThrowException() {
        // Arrange
        Integer serviceId = 1;
        when(nailServiceRepository.findByIdAndStatus(serviceId, ServiceStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> nailServiceService.getServiceById(serviceId));

        assertEquals("Nail service not found", exception.getMessage());
    }
}