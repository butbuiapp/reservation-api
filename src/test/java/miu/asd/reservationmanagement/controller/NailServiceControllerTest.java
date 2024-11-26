package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.model.NailService;
import miu.asd.reservationmanagement.service.NailServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NailServiceControllerTest {
    @Mock
    private NailServiceService nailServiceService;
    @InjectMocks
    private NailServiceController nailServiceController;

    @Test
    void createService() {
        // Arrange
        NailService nailService = NailService.builder()
                .name("Gel").price(30d).duration(50).description("Nail Gel")
                .build();

        NailService createdNailService = NailService.builder()
                .id(1).name("Gel").price(30d).duration(50).description("Nail Gel")
                .build();

        Mockito.when(nailServiceService.saveService(nailService)).thenReturn(createdNailService);

        // Act
        ResponseEntity<NailService> response = nailServiceController.createService(nailService);

        // Assert
        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody().equals(createdNailService);
    }

    @Test
    void updateService() {
        // Arrange
        NailService nailService = NailService.builder()
                .name("Gel").price(30d).duration(50).description("Nail Gel")
                .build();

        // Act
        ResponseEntity<Map<String, String>> response = nailServiceController.updateService(1, nailService);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nail service updated successfully", response.getBody().get("message"));
    }

    @Test
    void getAllServices() {
        // Arrange
        NailService nailService1 = NailService.builder()
                .name("Gel").price(30d).duration(50).description("Nail Gel")
                .build();
        NailService nailService2 = NailService.builder()
                .name("Manicure").price(25d).duration(60).description("Manicure service")
                .build();
        List<NailService> nailServices = Arrays.asList(nailService1, nailService2);
        Mockito.when(nailServiceService.getAllServices()).thenReturn(nailServices);

        // Act
        ResponseEntity<List<NailService>> response = nailServiceController.getAllServices();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(nailServices, response.getBody());
    }

    @Test
    void getServiceById() {
        // Arrange
        Integer id = 1;
        NailService nailService = NailService.builder()
                .id(id)
                .name("Manicure").price(25d).duration(60).description("Manicure service")
                .build();
        Mockito.when(nailServiceService.getServiceById(id)).thenReturn(nailService);

        // Act
        ResponseEntity<NailService> response = nailServiceController.getServiceById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(nailService, response.getBody());
    }

    @Test
    void deleteService() {
        // Act
        ResponseEntity<Map<String, String>> response = nailServiceController.deleteService(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nail service deleted successfully", response.getBody().get("message"));
    }
}