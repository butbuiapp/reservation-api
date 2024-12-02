package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.dto.response.EmployeeResponseDto;
import miu.asd.reservationmanagement.service.EmployeeService;
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
class EmployeeControllerTest {
    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Test
    void testCreateEmployee() {
        EmployeeRequestDto employeeRequestDto = EmployeeRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();
        ResponseEntity<Map<String, String>> response = employeeController.createEmployee(employeeRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Employee created successfully", response.getBody().get("message"));
    }

    @Test
    void testUpdateEmployee() {
        EmployeeRequestDto employeeRequestDto = EmployeeRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();
        ResponseEntity<Map<String, String>> response = employeeController.updateEmployee(1L, employeeRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee updated successfully", response.getBody().get("message"));
    }

    @Test
    void testGetActiveEmployees() {
        EmployeeResponseDto employeeResponseDto1 = EmployeeResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();
        EmployeeResponseDto employeeResponseDto2 = EmployeeResponseDto.builder()
                .firstName("first name 2")
                .lastName("last name 2")
                .phoneNumber("1234567891")
                .email("test2@example.com")
                .build();
        List<EmployeeResponseDto> employeeResponseDtos = Arrays.asList(employeeResponseDto1,
                employeeResponseDto2);

        Mockito.when(employeeService.getActiveEmployees()).thenReturn(employeeResponseDtos);

        ResponseEntity<List<EmployeeResponseDto>> response = employeeController.getActiveEmployees();
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(employeeResponseDtos);
    }

    @Test
    void testGetEmployeeById() {
        EmployeeResponseDto employeeResponseDto = EmployeeResponseDto.builder()
                .firstName("first name 1")
                .lastName("last name 1")
                .phoneNumber("1234567890")
                .email("test2@example.com")
                .build();

        Mockito.when(employeeService.getEmployeeById(Mockito.anyLong())).thenReturn(employeeResponseDto);

        ResponseEntity<EmployeeResponseDto> response = employeeController.getEmployeeById(Mockito.anyLong());
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(employeeResponseDto);
    }

    @Test
    void testDeleteEmployee() {
        ResponseEntity<Map<String, String>> response = employeeController.deleteEmployee(Mockito.anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee deleted successfully", response.getBody().get("message"));
    }

    @Test
    void testChangePassword() {
        ChangePasswordRequestDto changePasswordRequestDto =
                ChangePasswordRequestDto.builder()
                        .phoneNumber("1234567890")
                        .oldPassword("11111111")
                        .newPassword("11111112")
                        .confirmPassword("11111112")
                        .build();
        ResponseEntity<Map<String, String>> response = employeeController.changePassword(1L, changePasswordRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody().get("message"));
    }
}