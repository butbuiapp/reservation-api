package miu.asd.reservationmanagement.controller;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.dto.request.LoginRequestDto;
import miu.asd.reservationmanagement.dto.response.LoginResponseDto;
import miu.asd.reservationmanagement.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void loginAdmin() {
        // Assign
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .phoneNumber("1234567890").password("pass")
                .build();
        LoginResponseDto loginResponseDto = new LoginResponseDto("accessToken");
        Mockito.when(authService.login(RoleEnum.MANAGER, loginRequestDto))
                .thenReturn(loginResponseDto);
        // Act
        ResponseEntity<LoginResponseDto> response = authController.loginAdmin(loginRequestDto);
        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(loginResponseDto);
    }

    @Test
    void loginCustomer() {
        // Assign
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .phoneNumber("1234567890").password("pass")
                .build();
        LoginResponseDto loginResponseDto = new LoginResponseDto("accessToken");
        Mockito.when(authService.login(RoleEnum.CUSTOMER, loginRequestDto))
                .thenReturn(loginResponseDto);
        // Act
        ResponseEntity<LoginResponseDto> response = authController.loginCustomer(loginRequestDto);
        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(loginResponseDto);
    }
}