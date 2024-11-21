package miu.asd.reservationmanagement.mapper;

import miu.asd.reservationmanagement.dto.request.AppointmentRequestDto;
import miu.asd.reservationmanagement.dto.response.AppointmentResponseDto;
import miu.asd.reservationmanagement.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppointmentMapper {
    AppointmentMapper MAPPER = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "technician.role.role", target = "technician.role")
    @Mapping(source = "status", target = "status")
    AppointmentResponseDto entityToDto(Appointment appointment);
    Appointment dtoToEntity(AppointmentRequestDto appointmentRequestDto);
}
