package miu.asd.reservationmanagement.mapper;

import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {
    CustomerMapper MAPPER = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "loyaltyPoint.earnedPoint", target = "earnedPoint")
    CustomerResponseDto entityToDto(Customer entity);
    Customer dtoToEntity(CustomerRequestDto dto);
}
