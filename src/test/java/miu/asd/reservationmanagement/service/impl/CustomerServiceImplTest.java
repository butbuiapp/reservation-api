package miu.asd.reservationmanagement.service.impl;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.exception.InvalidPasswordException;
import miu.asd.reservationmanagement.exception.RecordAlreadyExistsException;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.Role;
import miu.asd.reservationmanagement.repository.CustomerRepository;
import miu.asd.reservationmanagement.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSaveCustomer_successfulCreation() {
        // Arrange
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("password")
                .build();
        when(customerRepository.findByPhoneNumberAndStatus("1111111111", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());
        when(roleRepository.findByRole(RoleEnum.CUSTOMER))
                .thenReturn(Optional.of(new Role(1, RoleEnum.CUSTOMER)));
        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        // Act
        customerService.saveCustomer(customerRequestDto);

        // Assert
        verify(customerRepository).save(Mockito.argThat(customer ->
                customer.getPhoneNumber().equals("1111111111") &&
                        customer.getPassword().equals("encodedPassword") &&
                        customer.getStatus() == UserStatusEnum.ACTIVE
        ));
    }

    @Test
    void testSaveCustomer_duplicatePhoneNumber_shouldThrowException() {
        // Arrange
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();

        when(customerRepository.findByPhoneNumberAndStatus(
                "1111111111",
                            UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(new Customer()));

        // Act & Assert
        RecordAlreadyExistsException exception = Assertions.assertThrows(
                RecordAlreadyExistsException.class,
                () -> customerService.saveCustomer(customerRequestDto)
        );
        Assertions.assertEquals("Phone number already used", exception.getMessage());
    }

    @Test
    void testSaveCustomer_missingRole_throwsException() {
        // Arrange
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();
        when(customerRepository.findByPhoneNumberAndStatus("1111111111", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());
        when(roleRepository.findByRole(RoleEnum.CUSTOMER))
                .thenReturn(Optional.empty()); // No role found

        // Act & Assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.saveCustomer(customerRequestDto)
        );
        Assertions.assertEquals("Role not found for CUSTOMER", exception.getMessage());
    }

    @Test
    void testUpdateCustomer_successfulUpdate() {
        // Arrange
        Long id = 1L;
        Customer existingCustomer = Customer.builder()
                .firstName("Old First name")
                .lastName("Old last name")
                .phoneNumber("1111111111")
                .email("old@example.com")
                .build();
        CustomerRequestDto updateDto = CustomerRequestDto.builder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .phoneNumber("2222222222")
                .email("new@example.com")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        when(customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByPhoneNumberAndStatus("2222222222", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act
        customerService.updateCustomer(id, updateDto);

        // Assert
        verify(customerRepository).save(Mockito.argThat(customer ->
                customer.getFirstName().equals("NewFirst") &&
                        customer.getLastName().equals("NewLast") &&
                        customer.getPhoneNumber().equals("2222222222") &&
                        customer.getEmail().equals("new@example.com") &&
                        customer.getDob().equals(LocalDate.of(1990, 1, 1))
        ));

    }

    @Test
    void testUpdateCustomer_customerNotFound_shouldThrowException() {
        // Arrange
        Long id = 1L;
        CustomerRequestDto updateDto = CustomerRequestDto.builder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .phoneNumber("2222222222")
                .email("new@example.com")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        when(customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE)).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.updateCustomer(id, updateDto)
        );
        // Assert
        Assertions.assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testUpdateCustomer_duplicatePhoneNumber_shouldThrowException() {
        // Arrange
        Long id = 1L;
        Customer existingCustomer = Customer.builder()
                .firstName("Old First name")
                .lastName("Old last name")
                .phoneNumber("1111111111")
                .email("old@example.com")
                .build();
        Customer otherCustomer = Customer.builder()
                .id(2L)
                .firstName("Other First name")
                .lastName("Other last name")
                .phoneNumber("2222222222")
                .email("other@example.com")
                .build();
        CustomerRequestDto updateDto = CustomerRequestDto.builder()
                .phoneNumber("2222222222")
                .build();

        when(customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByPhoneNumberAndStatus("2222222222", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(otherCustomer));

        // Act
        RecordAlreadyExistsException exception = Assertions.assertThrows(
                RecordAlreadyExistsException.class,
                () -> customerService.updateCustomer(id, updateDto)
        );
        // Assert
        Assertions.assertEquals("Phone number already exists", exception.getMessage());
    }

    @Test
    void deleteCustomerById_successfulDelete() {
        // Arrange
        Long id = 1L;
        Customer existingCustomer = Customer.builder()
                .firstName("First name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .email("test@example.com")
                .build();
        when(customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE)).
                thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.deleteCustomerById(id);

        // Assert
        verify(customerRepository).save(Mockito.argThat(savedCustomer ->
                savedCustomer.getStatus() == UserStatusEnum.DELETED
        ));
    }

    @Test
    void testGetActiveCustomers_foundCustomers() {
        // Arrange
        Customer customer1 = Customer.builder()
                .id(1L)
                .firstName("First name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .email("test@example.com")
                .status(UserStatusEnum.ACTIVE)
                .build();
        Customer customer2 = Customer.builder()
                .id(2L)
                .firstName("First name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .email("test@example.com")
                .status(UserStatusEnum.ACTIVE)
                .build();
        List<Customer> customerList = List.of(customer1, customer2);

        when(customerRepository.findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE))
                .thenReturn(customerList);

        // Act
        List<CustomerResponseDto> response = customerService.getActiveCustomers();

        // Assert
        assertEquals(2, response.size());
        assertEquals(1, response.get(0).getId());
        assertEquals(2, response.get(1).getId());
    }

    @Test
    void testGetActiveCustomers_noCustomersFound() {
        // Arrange
        when(customerRepository.findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE))
                .thenReturn(Collections.emptyList());

        // Act
        List<CustomerResponseDto> response = customerService.getActiveCustomers();

        // Assert
        assertEquals(0, response.size());
        verify(customerRepository, times(1)).findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE);
    }

    @Test
    void testGetCustomerById_customerFound() {
        // Arrange
        Long id = 1L;
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .status(UserStatusEnum.ACTIVE)
                .build();

        when(customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE)).thenReturn(Optional.of(customer));

        // Act
        CustomerResponseDto response = customerService.getCustomerById(id);

        // Assert
        assertEquals("First name", response.getFirstName());
        assertEquals("Last name", response.getLastName());
        assertEquals("1234567890", response.getPhoneNumber());
    }

    @Test
    void testGetCustomerById_customerNotFound() {
        // Arrange
        Long id = 1L;
        when(customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(id)
        );
        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testGetCustomerByPhone_customerFound() {
        // Arrange
        String phoneNumber = "1234567890";
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .status(UserStatusEnum.ACTIVE)
                .build();

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));

        // Act
        CustomerResponseDto response = customerService.getCustomerByPhone(phoneNumber);

        // Assert
        assertEquals("First name", response.getFirstName());
        assertEquals("Last name", response.getLastName());
        assertEquals("1234567890", response.getPhoneNumber());
        assertEquals(phoneNumber, response.getPhoneNumber());
    }

    @Test
    void testGetCustomerByPhone_customerNotFound() {
        // Arrange
        String phoneNumber = "1234567890";

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerByPhone(phoneNumber)
        );
        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testChangePassword_passwordsDoNotMatch() {
        // Arrange
        ChangePasswordRequestDto dto = ChangePasswordRequestDto.builder()
                .phoneNumber("1234567890")
                .oldPassword("oldPass")
                .newPassword("newPass")
                .confirmPassword("differentPass")
                .build();

        // Act & Assert
        InvalidPasswordException exception = assertThrows(
                InvalidPasswordException.class,
                () -> customerService.changePassword("1234567890", dto)
        );
        assertEquals("Passwords do not match.", exception.getMessage());
        verifyNoInteractions(customerRepository, passwordEncoder);
    }

    @Test
    void testChangePassword_incorrectPhoneNumber() {
        // Arrange
        String phoneNumber = "1234567890";
        ChangePasswordRequestDto dto = ChangePasswordRequestDto.builder()
                .phoneNumber("1234567891")
                .oldPassword("oldPass")
                .newPassword("newPass")
                .confirmPassword("newPass")
                .build();
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .status(UserStatusEnum.ACTIVE)
                .build();

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));

        // Act & Assert
        InvalidPasswordException exception = assertThrows(
                InvalidPasswordException.class,
                () -> customerService.changePassword(phoneNumber, dto)
        );
        assertEquals("The phone number is incorrect.", exception.getMessage());
        verify(customerRepository, times(1)).findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE);
    }

    @Test
    void testChangePassword_incorrectOldPassword() {
        // Arrange
        String phoneNumber = "1234567890";
        ChangePasswordRequestDto dto = ChangePasswordRequestDto.builder()
                .phoneNumber("1234567890")
                .oldPassword("oldPass")
                .newPassword("newPass")
                .confirmPassword("newPass")
                .build();
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .password("encodedOldPass")
                .status(UserStatusEnum.ACTIVE)
                .build();

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(dto.getOldPassword(), customer.getPassword())).thenReturn(false);

        // Act & Assert
        InvalidPasswordException exception = assertThrows(
                InvalidPasswordException.class,
                () -> customerService.changePassword(phoneNumber, dto)
        );
        assertEquals("The old password is incorrect.", exception.getMessage());
    }

    @Test
    void testChangePassword_success() {
        // Arrange
        String phoneNumber = "1234567890";
        ChangePasswordRequestDto dto = ChangePasswordRequestDto.builder()
                .phoneNumber("1234567890")
                .oldPassword("oldPass")
                .newPassword("newPass")
                .confirmPassword("newPass")
                .build();
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .password("encodedOldPass")
                .status(UserStatusEnum.ACTIVE)
                .build();

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(dto.getOldPassword(), customer.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encodedNewPass");

        // Act
        customerService.changePassword(phoneNumber, dto);

        // Assert
        assertEquals("encodedNewPass", customer.getPassword());
    }
}