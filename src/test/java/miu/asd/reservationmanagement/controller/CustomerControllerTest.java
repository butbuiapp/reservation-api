package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.service.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateCustomer_created() {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();
        ResponseEntity<?> response = customerController.createCustomer(customerRequestDto);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Customer created successfully",
                ((Map<String, String>)response.getBody()).get("message"));
    }


    @Test
    void updateCustomer() {
    }

    @Test
    void getActiveCustomers() {
    }

    @Test
    void getCustomerById() {
    }

    @Test
    void getCustomerByPhone() {
    }

    @Test
    void deleteCustomer() {
    }

    @Test
    void changePasswordByPhone() {
    }
}