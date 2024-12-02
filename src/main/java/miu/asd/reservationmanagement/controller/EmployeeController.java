package miu.asd.reservationmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.dto.response.EmployeeResponseDto;
import miu.asd.reservationmanagement.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.EMPLOYEE_URL)
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, String>> createEmployee(@Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        employeeService.saveEmployee(employeeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Employee created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Map<String, String>> updateEmployee(@PathVariable Long id,
                                            @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        employeeService.updateEmployee(id, employeeRequestDto);
        return ResponseEntity.ok().body(Map.of("message", "Employee updated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public ResponseEntity<List<EmployeeResponseDto>> getActiveEmployees() {
        List<EmployeeResponseDto> employees = employeeService.getActiveEmployees();
        return ResponseEntity.ok().body(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TECHNICIAN')")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDto employeeResponseDto = employeeService.getEmployeeById(id);
        return ResponseEntity.ok().body(employeeResponseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok().body(Map.of("message", "Employee deleted successfully"));
    }

    @PutMapping("/change-password/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        employeeService.changePassword(id, changePasswordRequestDto);
        return ResponseEntity.ok().body(Map.of("message", "Password changed successfully"));
    }

}
