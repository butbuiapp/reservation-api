package miu.asd.reservationmanagement.service.impl;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.dto.response.EmployeeResponseDto;
import miu.asd.reservationmanagement.exception.RecordAlreadyExistsException;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.model.Role;
import miu.asd.reservationmanagement.repository.EmployeeRepository;
import miu.asd.reservationmanagement.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void testSaveEmployee_phoneNumberAlreadyExists() {
        // Arrange
        EmployeeRequestDto dto = EmployeeRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .build();

        when(employeeRepository.findByPhoneNumberAndStatus("1111111111", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(new Employee()));

        // Act & Assert
        RecordAlreadyExistsException exception = assertThrows(
                RecordAlreadyExistsException.class,
                () -> employeeService.saveEmployee(dto)
        );
        assertEquals("Phone number already used", exception.getMessage());
        verifyNoInteractions(passwordEncoder, roleRepository);
    }

    @Test
    void testSaveEmployee_roleNotFound() {
        // Arrange
        EmployeeRequestDto dto = EmployeeRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("1111111111")
                .role(RoleEnum.TECHNICIAN)
                .build();

        when(employeeRepository.findByPhoneNumberAndStatus("1111111111", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());
        when(roleRepository.findByRole(RoleEnum.TECHNICIAN)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.saveEmployee(dto)
        );
        assertEquals("Role not found for EMPLOYEE", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testSaveEmployee_success() {
        // Arrange
        EmployeeRequestDto dto = EmployeeRequestDto.builder()
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("1111111111")
                .password("password")
                .role(RoleEnum.TECHNICIAN)
                .build();
        Role role = new Role(1, RoleEnum.TECHNICIAN);

        when(employeeRepository.findByPhoneNumberAndStatus("1111111111", UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());
        when(roleRepository.findByRole(RoleEnum.TECHNICIAN)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        employeeService.saveEmployee(dto);

        // Assert
        verify(employeeRepository).save(Mockito.argThat(employee ->
                employee.getPhoneNumber().equals("1111111111") &&
                        employee.getPassword().equals("encodedPassword") &&
                        employee.getStatus() == UserStatusEnum.ACTIVE
        ));
    }

    @Test
    void testUpdateEmployee_successfulUpdate() {
        // Arrange
        Long employeeId = 1L;
        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("first name");
        existingEmployee.setLastName("last name");
        existingEmployee.setPhoneNumber("1111111111");
        existingEmployee.setRole(new Role(2, RoleEnum.TECHNICIAN));

        EmployeeRequestDto requestDto = new EmployeeRequestDto();
        requestDto.setPhoneNumber("1111111111");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setRole(RoleEnum.MANAGER);

        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.findByPhoneNumberAndStatus(requestDto.getPhoneNumber(), UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingEmployee));
        when(roleRepository.findByRole(requestDto.getRole())).thenReturn(Optional.of(new Role(1, RoleEnum.MANAGER)));

        // Act
        employeeService.updateEmployee(employeeId, requestDto);

        // Assert
        verify(employeeRepository).save(Mockito.argThat(employee ->
                employee.getFirstName().equals(requestDto.getFirstName()) &&
                        employee.getLastName().equals(requestDto.getLastName()) &&
                        employee.getPhoneNumber().equals(requestDto.getPhoneNumber())
        ));
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    @Test
    void testUpdateEmployee_phoneNumberAlreadyExists() {
        // Arrange
        Long employeeId = 1L;
        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setPhoneNumber("1111111111");

        Employee anotherEmployee = new Employee();
        anotherEmployee.setId(2L);
        anotherEmployee.setPhoneNumber("2222222222");

        EmployeeRequestDto requestDto = new EmployeeRequestDto();
        requestDto.setPhoneNumber("2222222222");

        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.findByPhoneNumberAndStatus(requestDto.getPhoneNumber(), UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(anotherEmployee));

        // Act & Assert
        assertThrows(RecordAlreadyExistsException.class,
                () -> employeeService.updateEmployee(employeeId, requestDto));

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_roleNotFound() {
        // Arrange
        Long employeeId = 1L;
        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setRole(new Role(2, RoleEnum.TECHNICIAN));

        EmployeeRequestDto requestDto = new EmployeeRequestDto();
        requestDto.setRole(RoleEnum.MANAGER);

        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(existingEmployee));
        when(roleRepository.findByRole(RoleEnum.MANAGER)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(employeeId, requestDto));

        verify(employeeRepository, never()).save(any());
        Assertions.assertEquals("Role not found for EMPLOYEE", exception.getMessage());
    }

    @Test
    void testUpdateEmployee_employeeNotFound() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE)).thenReturn(Optional.empty());

        EmployeeRequestDto requestDto = new EmployeeRequestDto();

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(employeeId, requestDto));

        verify(employeeRepository, never()).save(any());
        Assertions.assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void testDeleteEmployeeById_employeeNotFound() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.deleteEmployeeById(employeeId)
        );
        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployeeById_success() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setStatus(UserStatusEnum.ACTIVE);

        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployeeById(employeeId);

        // Assert
        assertEquals(UserStatusEnum.DELETED, employee.getStatus());
    }

    @Test
    void testGetActiveEmployees_noEmployeesFound() {
        // Arrange
        when(employeeRepository.findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE)).thenReturn(new ArrayList<>());

        // Act
        List<EmployeeResponseDto> response = employeeService.getActiveEmployees();

        // Assert
        assertTrue(response.isEmpty());
    }

    @Test
    void testGetActiveEmployees_employeesFound() {
        // Arrange
        List<Employee> employees = new ArrayList<>();
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employees.add(employee1);

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employees.add(employee2);

        when(employeeRepository.findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE)).thenReturn(employees);
        // Act
        List<EmployeeResponseDto> response = employeeService.getActiveEmployees();

        // Assert
        assertEquals(2, response.size());
        assertEquals("John", response.get(0).getFirstName());
        assertEquals("Doe", response.get(0).getLastName());
        assertEquals("Jane", response.get(1).getFirstName());
        assertEquals("Smith", response.get(1).getLastName());
    }

    @Test
    void testGetEmployeeById_employeeExists() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        EmployeeResponseDto employeeResponseDto = EmployeeResponseDto.builder()
                .id(employeeId)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .build();

        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponseDto response = employeeService.getEmployeeById(employeeId);

        // Assert
        assertEquals(employeeId, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void testGetEmployeeById_employeeDoesNotExist() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findByIdAndStatus(employeeId, UserStatusEnum.ACTIVE)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId)
        );
        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void changePassword() {
    }
}