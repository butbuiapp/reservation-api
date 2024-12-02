package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.service.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testCreateCustomer() {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();
        ResponseEntity<Map<String, String>> response = customerController.createCustomer(customerRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Customer created successfully", response.getBody().get("message"));
    }

    @Test
    void testUpdateCustomer() {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();
        ResponseEntity<Map<String, String>> response = customerController.updateCustomer(1L, customerRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer updated successfully", response.getBody().get("message"));
    }

    @Test
    void testGetActiveCustomers() {
        CustomerResponseDto customerResponseDto1 = CustomerResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();
        CustomerResponseDto customerResponseDto2 = CustomerResponseDto.builder()
                .firstName("first name 2")
                .lastName("last name 2")
                .phoneNumber("1234567891")
                .email("test2@example.com")
                .build();
        List<CustomerResponseDto> customerResponseDtos = Arrays.asList(customerResponseDto1,
                customerResponseDto2);

        Mockito.when(customerService.getActiveCustomers()).thenReturn(customerResponseDtos);

        ResponseEntity<List<CustomerResponseDto>> response = customerController.getActiveCustomers();
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(customerResponseDtos);
    }

    @Test
    void testGetCustomerById() {
        CustomerResponseDto customerResponseDto = CustomerResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();

        Mockito.when(customerService.getCustomerById(Mockito.anyLong())).thenReturn(customerResponseDto);

        ResponseEntity<CustomerResponseDto> response = customerController.getCustomerById(Mockito.anyLong());
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(customerResponseDto);
    }

    @Test
    void testGetCustomerByPhone() {
        CustomerResponseDto customerResponseDto = CustomerResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();

        Mockito.when(customerService.getCustomerByPhone(Mockito.anyString())).thenReturn(customerResponseDto);

        ResponseEntity<CustomerResponseDto> response = customerController.getCustomerByPhone(Mockito.anyString());
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(customerResponseDto);
    }

    @Test
    void testDeleteCustomer() {
        ResponseEntity<Map<String, String>> response = customerController.deleteCustomer(Mockito.anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer deleted successfully",
                ((Map<String, String>)response.getBody()).get("message"));
    }

    @Test
    void testChangePasswordByPhone() {
        ChangePasswordRequestDto changePasswordRequestDto =
                ChangePasswordRequestDto.builder()
                        .phoneNumber("1234567890")
                        .oldPassword("11111111")
                        .newPassword("11111112")
                        .confirmPassword("11111112")
                        .build();
        ResponseEntity<Map<String, String>> response = customerController.changePasswordByPhone(
                               "1234567890",
                                            changePasswordRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully",
                ((Map<String, String>)response.getBody()).get("message"));
    }
}