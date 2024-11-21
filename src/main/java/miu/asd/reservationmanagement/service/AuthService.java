package miu.asd.reservationmanagement.service;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.dto.request.LoginRequestDto;
import miu.asd.reservationmanagement.dto.response.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(RoleEnum role, LoginRequestDto loginRequestDto);
}
