package miu.asd.reservationmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.CUSTOMER_URL)
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("register")
    public ResponseEntity<Map<String, String>> createCustomer(@Valid
                                                                  @RequestBody CustomerRequestDto customerRequestDto) {
        customerService.saveCustomer(customerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Customer created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, String>> updateCustomer(@PathVariable Long id,
                                            @Valid @RequestBody CustomerRequestDto customerRequestDto) {
        customerService.updateCustomer(id, customerRequestDto);
        return ResponseEntity.ok().body(Map.of("message", "Customer updated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<CustomerResponseDto>> getActiveCustomers() {
        List<CustomerResponseDto> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok().body(customers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        CustomerResponseDto customerDto = customerService.getCustomerById(id);
        return ResponseEntity.ok().body(customerDto);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @GetMapping("search/{phoneNumber}")
    public ResponseEntity<CustomerResponseDto> getCustomerByPhone(@PathVariable String phoneNumber) {
        CustomerResponseDto customerDto = customerService.getCustomerByPhone(phoneNumber);
        return ResponseEntity.ok().body(customerDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        return ResponseEntity.ok().body(Map.of("message", "Customer deleted successfully"));
    }

    @PutMapping("/change-password/{phoneNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, String>> changePasswordByPhone(
            @PathVariable String phoneNumber,
            @Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        customerService.changePassword(phoneNumber, changePasswordRequestDto);
        return ResponseEntity.ok().body(Map.of("message", "Password changed successfully"));
    }
}
