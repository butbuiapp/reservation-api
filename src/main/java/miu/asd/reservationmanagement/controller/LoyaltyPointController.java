package miu.asd.reservationmanagement.controller;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.service.LoyaltyPointService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.POINT_URL)
@RequiredArgsConstructor
public class LoyaltyPointController {
    private final LoyaltyPointService loyaltyPointService;

    @GetMapping("customer/{phoneNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Integer> getCustomerPointByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(loyaltyPointService.getCustomerPointByPhoneNumber(phoneNumber));
    }
}
