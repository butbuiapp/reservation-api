package miu.asd.reservationmanagement.service;

import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.dto.response.EmployeeResponseDto;

import java.util.List;

public interface EmployeeService {
    void saveEmployee(EmployeeRequestDto employeeRequestDto);
    void updateEmployee(Long id, EmployeeRequestDto employeeRequestDto);
    void deleteEmployeeById(Long id);
    List<EmployeeResponseDto> getActiveEmployees();
    EmployeeResponseDto getEmployeeById(Long id);
    void changePassword(Long id, ChangePasswordRequestDto dto);
}
