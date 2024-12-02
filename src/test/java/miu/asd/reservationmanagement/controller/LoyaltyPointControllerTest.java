package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.service.LoyaltyPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LoyaltyPointControllerTest {
    @Mock
    private LoyaltyPointService loyaltyPointService;

    @InjectMocks
    private LoyaltyPointController loyaltyPointController;

    @Test
    void testGetCustomerPointByPhoneNumber() {
        String phoneNumber = "123456789";
        // Arrange
        Mockito.when(loyaltyPointService.getCustomerPointByPhoneNumber(phoneNumber)).thenReturn(100);
        // Act
        ResponseEntity<Integer> response = loyaltyPointController.getCustomerPointByPhoneNumber(phoneNumber);

        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() == 100;
    }
}