package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.dto.request.LoginRequestDto;
import miu.asd.reservationmanagement.dto.response.LoginResponseDto;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.service.AuthService;
import miu.asd.reservationmanagement.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponseDto login(RoleEnum role, LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getPhoneNumber(),
                        loginRequestDto.getPassword()
                )
        );
        UserDetails userDetails = null;
        if (role.equals(RoleEnum.CUSTOMER)) {
            userDetails = (Customer) authentication.getPrincipal();
        } else {
            userDetails = (Employee) authentication.getPrincipal();
        }

        String token = jwtService.generateToken(userDetails);
        return new LoginResponseDto(token);
    }

}
