package miu.asd.reservationmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.model.NailService;
import miu.asd.reservationmanagement.service.NailServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.SERVICE_URL)
public class NailServiceController {
    private final NailServiceService nailServiceService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createService(@Valid @RequestBody NailService nailService) {
        NailService createdService = nailServiceService.saveService(nailService);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateService(@PathVariable Integer id,
                                           @Valid @RequestBody NailService nailService) {
        nailServiceService.updateService(id, nailService);
        return ResponseEntity.ok().body(Map.of("message", "Nail service updated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> getAllServices() {
        List<NailService> services = nailServiceService.getAllServices();
        return ResponseEntity.ok().body(services);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getServiceById(@PathVariable Integer id) {
        NailService nailService = nailServiceService.getServiceById(id);
        return ResponseEntity.ok().body(nailService);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteService(@PathVariable Integer id) {
        nailServiceService.deleteServiceById(id);
        return ResponseEntity.ok().body(Map.of("message", "Nail service deleted successfully"));
    }
}

