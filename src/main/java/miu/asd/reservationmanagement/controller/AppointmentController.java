package miu.asd.reservationmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.dto.request.AppointmentRequestDto;
import miu.asd.reservationmanagement.dto.request.AppointmentSearchRequestDto;
import miu.asd.reservationmanagement.dto.response.AppointmentResponseDto;
import miu.asd.reservationmanagement.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.APPOINTMENT_URL)
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createAppointment(
            @Valid @RequestBody AppointmentRequestDto appointmentRequestDto) {
        appointmentService.saveAppointment(appointmentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Appointment created successfully"));
    }

    @PutMapping("{appointmentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<?> updateAppointment(@PathVariable Long appointmentId,
                                               @Valid @RequestBody AppointmentRequestDto appointmentRequestDto) {
        appointmentService.updateAppointment(appointmentId, appointmentRequestDto);
        return ResponseEntity.ok().body(Map.of("message", "Appointment updated successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<?> getAppointmentsByCustomerId(@PathVariable Long customerId) {
        List<AppointmentResponseDto> appointmentResponseDtos = appointmentService.getAppointmentsByCustomerId(customerId);
        return ResponseEntity.ok().body(appointmentResponseDtos);
    }

    @GetMapping("/customer/phone/{phoneNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getAppointmentsByCustomerPhone(@PathVariable String phoneNumber) {
        List<AppointmentResponseDto> appointmentResponseDtos =
                appointmentService.getAppointmentsByCustomerPhone(phoneNumber);
        return ResponseEntity.ok().body(appointmentResponseDtos);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAppointmentsByDate(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        List<AppointmentResponseDto> appointmentResponseDtos =
                appointmentService.searchAppointment(searchRequestDto);
        return ResponseEntity.ok().body(appointmentResponseDtos);
    }

    @GetMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok().body(Map.of("message", "Appointment cancelled successfully"));
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId) {
        return ResponseEntity.ok().body(appointmentService.getAppointmentById(appointmentId));
    }

    @GetMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> completeAppointment(@PathVariable Long appointmentId) {
        appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok().body(Map.of("message", "Appointment completed successfully"));
    }
}
