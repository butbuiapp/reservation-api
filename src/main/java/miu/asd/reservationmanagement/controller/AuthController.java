package miu.asd.reservationmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.dto.request.LoginRequestDto;
import miu.asd.reservationmanagement.dto.response.LoginResponseDto;
import miu.asd.reservationmanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.AUTHENTICATION_URL)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponseDto> loginAdmin(@Valid @RequestBody LoginRequestDto req) {
        LoginResponseDto loginResponseDto = authService.login(RoleEnum.MANAGER, req);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/customer/login")
    public ResponseEntity<LoginResponseDto> loginCustomer(@Valid @RequestBody LoginRequestDto req) {
        LoginResponseDto loginResponseDto = authService.login(RoleEnum.CUSTOMER, req);
        return ResponseEntity.ok(loginResponseDto);
    }

}
