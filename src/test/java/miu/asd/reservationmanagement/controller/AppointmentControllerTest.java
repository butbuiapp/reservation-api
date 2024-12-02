package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.dto.request.AppointmentRequestDto;
import miu.asd.reservationmanagement.dto.request.AppointmentSearchRequestDto;
import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.dto.response.AppointmentResponseDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.dto.response.EmployeeResponseDto;
import miu.asd.reservationmanagement.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {
    @Mock
    private AppointmentService appointmentService;
    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void createAppointment() {
        CustomerRequestDto customer = CustomerRequestDto.builder().id(1L).build();
        EmployeeRequestDto employee = EmployeeRequestDto.builder().id(1L).build();
        AppointmentRequestDto appointmentRequestDto = AppointmentRequestDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        ResponseEntity<Map<String, String>> response = appointmentController.createAppointment(appointmentRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Appointment created successfully", response.getBody().get("message"));
    }

    @Test
    void updateAppointment() {
        Long id = 1L;
        CustomerRequestDto customer = CustomerRequestDto.builder().id(1L).build();
        EmployeeRequestDto employee = EmployeeRequestDto.builder().id(1L).build();
        AppointmentRequestDto appointmentRequestDto = AppointmentRequestDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        ResponseEntity<Map<String, String>> response =
                appointmentController.updateAppointment(id, appointmentRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Appointment updated successfully", response.getBody().get("message"));
    }

    @Test
    void getAppointmentsByCustomerId() {
        Long customerId = 1L;
        CustomerResponseDto customer = CustomerResponseDto.builder()
                .id(customerId)
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();
        EmployeeResponseDto employee = EmployeeResponseDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1234567891")
                .email("1234567891@example.com")
                .build();
        AppointmentResponseDto appointmentResponseDto1 = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        AppointmentResponseDto appointmentResponseDto2 = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        List<AppointmentResponseDto> appointmentResponseDtos =
                Arrays.asList(appointmentResponseDto1, appointmentResponseDto2);

        Mockito.when(appointmentService.getAppointmentsByCustomerId(customerId)).thenReturn(appointmentResponseDtos);
        // Act
        ResponseEntity<List<AppointmentResponseDto>> response =
                appointmentController.getAppointmentsByCustomerId(customerId);
        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(appointmentResponseDtos);
    }

    @Test
    void getAppointmentsByCustomerPhone() {
        String phoneNumber = "1234567890";
        CustomerResponseDto customer = CustomerResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber(phoneNumber)
                .email("test2@example.com")
                .build();
        EmployeeResponseDto employee = EmployeeResponseDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1234567891")
                .email("1234567891@example.com")
                .build();
        AppointmentResponseDto appointmentResponseDto1 = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        AppointmentResponseDto appointmentResponseDto2 = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 11, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        List<AppointmentResponseDto> appointmentResponseDtos =
                Arrays.asList(appointmentResponseDto1, appointmentResponseDto2);

        Mockito.when(appointmentService.getAppointmentsByCustomerPhone(phoneNumber)).thenReturn(appointmentResponseDtos);
        // Act
        ResponseEntity<List<AppointmentResponseDto>> response =
                appointmentController.getAppointmentsByCustomerPhone(phoneNumber);
        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(appointmentResponseDtos);
    }

    @Test
    void getAppointmentsByDate() {
        AppointmentSearchRequestDto appointmentSearchRequestDto = AppointmentSearchRequestDto.builder()
                .appointmentDate(LocalDate.of(2024, 12, 26)).build();
        CustomerResponseDto customer = CustomerResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();
        EmployeeResponseDto employee = EmployeeResponseDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1234567891")
                .email("1234567891@example.com")
                .build();
        AppointmentResponseDto appointmentResponseDto1 = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        AppointmentResponseDto appointmentResponseDto2 = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();
        List<AppointmentResponseDto> appointmentResponseDtos =
                Arrays.asList(appointmentResponseDto1, appointmentResponseDto2);

        Mockito.when(appointmentService.searchAppointment(appointmentSearchRequestDto))
                .thenReturn(appointmentResponseDtos);
        // Act
        ResponseEntity<List<AppointmentResponseDto>> response =
                appointmentController.getAppointmentsByDate(appointmentSearchRequestDto);
        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(appointmentResponseDtos);
    }

    @Test
    void cancelAppointment() {
        // Act
        ResponseEntity<Map<String, String>> response = appointmentController.cancelAppointment(1L);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Appointment cancelled successfully", response.getBody().get("message"));
    }

    @Test
    void getAppointmentById() {
        // Assign
        CustomerResponseDto customer = CustomerResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();
        EmployeeResponseDto employee = EmployeeResponseDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1234567891")
                .email("1234567891@example.com")
                .build();
        AppointmentResponseDto appointmentResponseDto = AppointmentResponseDto.builder()
                .date(LocalDate.of(2024, 12, 26))
                .time(LocalTime.of(9, 0, 0))
                .customer(customer)
                .technician(employee)
                .build();

        Mockito.when(appointmentService.getAppointmentById(1L))
                .thenReturn(appointmentResponseDto);
        // Act
        ResponseEntity<AppointmentResponseDto> response =
                appointmentController.getAppointmentById(1L);
        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(appointmentResponseDto);
    }

    @Test
    void completeAppointment() {
        // Act
        ResponseEntity<Map<String, String>> response = appointmentController.completeAppointment(1L);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Appointment completed successfully", response.getBody().get("message"));
    }
}