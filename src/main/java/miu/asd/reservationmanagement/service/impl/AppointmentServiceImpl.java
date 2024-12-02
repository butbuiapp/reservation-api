package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.AppointmentStatusEnum;
import miu.asd.reservationmanagement.common.InvoiceStatusEnum;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.dto.request.AppointmentRequestDto;
import miu.asd.reservationmanagement.dto.request.AppointmentSearchRequestDto;
import miu.asd.reservationmanagement.dto.response.AppointmentResponseDto;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.mapper.AppointmentMapper;
import miu.asd.reservationmanagement.model.*;
import miu.asd.reservationmanagement.repository.*;
import miu.asd.reservationmanagement.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final NailServiceRepository nailServiceRepository;

    @Override
    @Transactional
    public void saveAppointment(AppointmentRequestDto appointmentRequestDto) {
        // save appointment
        Appointment appointment = AppointmentMapper.MAPPER.dtoToEntity(appointmentRequestDto);
        appointment.setStatus(AppointmentStatusEnum.BOOKED);

        // get customer by phone
        Customer customer = customerRepository.findByPhoneNumberAndStatus(
                appointmentRequestDto.getCustomer().getPhoneNumber(),
                UserStatusEnum.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        appointment.setCustomer(customer);

        // Save invoice if present
        Invoice invoice = appointment.getInvoice();
        if (invoice != null) {
            invoice.setStatus(InvoiceStatusEnum.DRAFT);
            appointment.setInvoice(invoice);
        }
        appointmentRepository.save(appointment);
    }

    @Override
    public void updateAppointment(Long id, AppointmentRequestDto appointmentRequestDto) {
        Appointment existingAppointment = findById(id);
        existingAppointment.setDate(appointmentRequestDto.getDate());
        existingAppointment.setTime(appointmentRequestDto.getTime());
        // get employee
        Employee employee = employeeRepository.findById(appointmentRequestDto.getTechnician().getId()).
                orElseThrow(() -> new ResourceNotFoundException("Technician not found"));
        existingAppointment.setTechnician(employee);
        existingAppointment.setNotes(appointmentRequestDto.getNotes());

        // update service
        if (appointmentRequestDto.getInvoice() == null) {
            if (existingAppointment.getInvoice() != null) {
                existingAppointment.getInvoice().getServices().clear();
            }
        } else {

            // get list of service Ids
            List<Integer> serviceIds = appointmentRequestDto.getInvoice().getServices().stream()
                    .map(s -> s.getId()).toList();
            List<NailService> services = nailServiceRepository.findAllById(serviceIds);

            Invoice existingInvoice = existingAppointment.getInvoice();
            if (existingInvoice == null) existingInvoice = new Invoice();
            existingInvoice.setServices(services);
            existingInvoice.setStatus(InvoiceStatusEnum.DRAFT);
            existingInvoice.setTotalAmount(appointmentRequestDto.getInvoice().getTotalAmount());
            existingInvoice.setIssuedDate(appointmentRequestDto.getInvoice().getIssuedDate());
            existingAppointment.setInvoice(existingInvoice);

        }
        appointmentRepository.save(existingAppointment);
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = findById(id);
        appointment.setStatus(AppointmentStatusEnum.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void completeAppointment(Long id) {
        Appointment appointment = findById(id);
        // update appointment status
        appointment.setStatus(AppointmentStatusEnum.DONE);
        // update invoice status
        if (appointment.getInvoice() != null) {
            appointment.getInvoice().setStatus(InvoiceStatusEnum.PAID);
            appointment.getInvoice().setPaidDate(LocalDateTime.now());
        }
        appointmentRepository.save(appointment);

        // update earned point
        Optional<LoyaltyPoint> optionalLoyaltyPoint =
                loyaltyPointRepository.findByCustomerId(appointment.getCustomer().getId());
        if (optionalLoyaltyPoint.isPresent()) {
            LoyaltyPoint loyaltyPoint = optionalLoyaltyPoint.get();
            // update
            if (appointment.getInvoice() != null) {
                Integer newPoint = loyaltyPoint.getEarnedPoint() +
                        (int) appointment.getInvoice().getTotalAmount();
                loyaltyPoint.setEarnedPoint(newPoint);
                loyaltyPointRepository.save(loyaltyPoint);
            }
        } else {
            // create
            if (appointment.getInvoice() != null) {
                LoyaltyPoint loyaltyPoint = LoyaltyPoint.builder()
                        .earnedPoint((int) appointment.getInvoice().getTotalAmount())
                        .customer(appointment.getCustomer())
                        .build();
                loyaltyPointRepository.save(loyaltyPoint);
            }
        }
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByCustomerId(Long customerId) {
        List<Appointment> appointments = appointmentRepository.findAllByCustomerIdOrderByDateDescTimeDesc(customerId);
        return appointments.stream().map(AppointmentMapper.MAPPER::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByCustomerPhone(String phoneNumber) {
        List<Appointment> appointments =
                appointmentRepository.findAllByCustomerPhoneNumberOrderByDateDescTimeDesc(phoneNumber);

        List<AppointmentResponseDto> appointmentResponseDtos = appointments.stream().map(
                AppointmentMapper.MAPPER::entityToDto).collect(Collectors.toList());
        return appointmentResponseDtos;
    }

    @Override
    public AppointmentResponseDto getAppointmentById(Long id) {
        return AppointmentMapper.MAPPER.entityToDto(findById(id));
    }

    @Override
    public List<AppointmentResponseDto> searchAppointment(AppointmentSearchRequestDto dto) {
        List<Appointment> appointments = appointmentRepository.findAllByDateOrderByTimeDesc(dto.getAppointmentDate());
        return appointments.stream().map(AppointmentMapper.MAPPER::entityToDto).collect(Collectors.toList());
    }

    private Appointment findById(Long id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            return optionalAppointment.get();
        } else {
            throw new ResourceNotFoundException("Appointment not found");
        }
    }
}
