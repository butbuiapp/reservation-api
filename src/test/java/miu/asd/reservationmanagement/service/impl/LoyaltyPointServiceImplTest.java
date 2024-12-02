package miu.asd.reservationmanagement.service.impl;

import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.LoyaltyPoint;
import miu.asd.reservationmanagement.repository.CustomerRepository;
import miu.asd.reservationmanagement.repository.LoyaltyPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoyaltyPointServiceImplTest {
    @Mock
    private LoyaltyPointRepository loyaltyPointRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private LoyaltyPointServiceImpl loyaltyPointService;

    @Test
    void testGetCustomerPointByPhoneNumber_customerWithPoints() {
        // Arrange
        String phoneNumber = "1111111111";
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setPhoneNumber(phoneNumber);

        LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
        loyaltyPoint.setEarnedPoint(100);

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));
        when(loyaltyPointRepository.findByCustomerId(customer.getId()))
                .thenReturn(Optional.of(loyaltyPoint));

        // Act
        Integer points = loyaltyPointService.getCustomerPointByPhoneNumber(phoneNumber);

        // Assert
        assertEquals(100, points);
    }

    @Test
    void testGetCustomerPointByPhoneNumber_customerWithoutPoints() {
        // Arrange
        String phoneNumber = "2222222222";
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setPhoneNumber(phoneNumber);

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));
        when(loyaltyPointRepository.findByCustomerId(customer.getId()))
                .thenReturn(Optional.empty());

        // Act
        Integer points = loyaltyPointService.getCustomerPointByPhoneNumber(phoneNumber);

        // Assert
        assertEquals(0, points);
    }

    @Test
    void testGetCustomerPointByPhoneNumber_customerNotFound() {
        // Arrange
        String phoneNumber = "3333333333";

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act
        Integer points = loyaltyPointService.getCustomerPointByPhoneNumber(phoneNumber);

        // Assert
        assertEquals(0, points);
    }
}