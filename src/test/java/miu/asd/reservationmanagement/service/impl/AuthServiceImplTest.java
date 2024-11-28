package miu.asd.reservationmanagement.service.impl;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.dto.request.LoginRequestDto;
import miu.asd.reservationmanagement.dto.response.LoginResponseDto;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLoginAsCustomer_successfulLogin() {
        // Arrange
        RoleEnum role = RoleEnum.CUSTOMER;
        String phoneNumber = "1234567890";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().phoneNumber(phoneNumber).password("password").build();

        Authentication authenticationMock = mock(Authentication.class);
        Customer customerMock = mock(Customer.class);
        when(authenticationMock.getPrincipal()).thenReturn(customerMock);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);

        String token = "mocked-jwt-token";
        when(jwtService.generateToken(customerMock)).thenReturn(token);

        // Act
        LoginResponseDto response = authService.login(role, loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getAccessToken());
    }

    @Test
    void testLoginAsEmployee_successfulLogin() {
        // Arrange
        RoleEnum role = RoleEnum.MANAGER;
        String phoneNumber = "1234567890";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().phoneNumber(phoneNumber).password("password").build();

        Authentication authenticationMock = mock(Authentication.class);
        Employee employeeMock = mock(Employee.class);
        when(authenticationMock.getPrincipal()).thenReturn(employeeMock);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);

        String token = "mocked-jwt-token";
        when(jwtService.generateToken(employeeMock)).thenReturn(token);

        // Act
        LoginResponseDto response = authService.login(role, loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getAccessToken());
    }

    @Test
    void testLogin_invalidCredentials_shouldThrowException() {
        // Arrange
        RoleEnum role = RoleEnum.CUSTOMER;
        String phoneNumber = "1234567890";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .phoneNumber(phoneNumber).password("wrongPassword").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(role, loginRequestDto));
        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }
}