package miu.asd.reservationmanagement.service.impl;

import miu.asd.reservationmanagement.common.AppointmentStatusEnum;
import miu.asd.reservationmanagement.common.InvoiceStatusEnum;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.dto.request.*;
import miu.asd.reservationmanagement.dto.response.AppointmentResponseDto;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.model.*;
import miu.asd.reservationmanagement.repository.AppointmentRepository;
import miu.asd.reservationmanagement.repository.CustomerRepository;
import miu.asd.reservationmanagement.repository.EmployeeRepository;
import miu.asd.reservationmanagement.repository.LoyaltyPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private LoyaltyPointRepository loyaltyPointRepository;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void testSaveAppointment_withoutInvoice_shouldSaveAppointment() {
        // Arrange
        String phoneNumber = "1234567890";
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder().phoneNumber(phoneNumber).build();
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDate(LocalDate.of(2024, 11, 28));
        requestDto.setTime(LocalTime.of(10, 30));
        requestDto.setCustomer(customerRequestDto);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setPhoneNumber(phoneNumber);
        customer.setStatus(UserStatusEnum.ACTIVE);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatusEnum.BOOKED);
        appointment.setCustomer(customer);

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(customer));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        appointmentService.saveAppointment(requestDto);

        // Assert
        verify(appointmentRepository).save(Mockito.argThat(app ->
                app.getDate().equals(LocalDate.of(2024, 11, 28)) &&
                        app.getTime().equals(LocalTime.of(10, 30)) &&
                        app.getStatus() == AppointmentStatusEnum.BOOKED
        ));
    }

    @Test
    void testSaveAppointment_customerNotFound_shouldThrowException() {
        // Arrange
        String phoneNumber = "1234567890";
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder().phoneNumber(phoneNumber).build();
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDate(LocalDate.of(2024, 11, 28));
        requestDto.setTime(LocalTime.of(10, 30));
        requestDto.setCustomer(customerRequestDto);

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.saveAppointment(requestDto));

        assertEquals("Customer not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testSaveAppointment_withInvoice_shouldSaveInvoiceAsDraft() {
        // Arrange
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDate(LocalDate.of(2024, 11, 30));
        requestDto.setTime(LocalTime.of(10, 30));
        // set customer
        String phoneNumber = "1234567890";
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder().phoneNumber(phoneNumber).build();
        requestDto.setCustomer(customerRequestDto);

        // create services
        NailService service1 = new NailService();
        service1.setName("Manicure");
        service1.setPrice(25.0);
        service1.setDuration(30);

        NailService service2 = new NailService();
        service2.setName("Pedicure");
        service2.setPrice(50.0);
        service2.setDuration(60);

        List<NailService> services = Arrays.asList(service1, service2);

        InvoiceRequestDto invoiceRequestDto = InvoiceRequestDto.builder()
                .issuedDate(LocalDateTime.of(2024, 11, 28, 10, 10, 10))
                .totalAmount(100)
                .services(services)
                .build();
        // set invoice
        requestDto.setInvoice(invoiceRequestDto);

        // output
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setPhoneNumber(phoneNumber);
        mockCustomer.setStatus(UserStatusEnum.ACTIVE);

        Invoice invoice = Invoice.builder()
                .issuedDate(LocalDateTime.of(2024, 11, 28, 10, 10, 10))
                .totalAmount(100)
                .services(services)
                .status(InvoiceStatusEnum.DRAFT)
                .build();
        Appointment mockAppointment = new Appointment();
        mockAppointment.setCustomer(mockCustomer);
        mockAppointment.setInvoice(invoice);

        when(customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE))
                .thenReturn(Optional.of(mockCustomer));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        // Act
        appointmentService.saveAppointment(requestDto);

        // Assert
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        assertEquals(InvoiceStatusEnum.DRAFT, invoice.getStatus());
    }

    @Test
    void testUpdateAppointment_withValidData_shouldUpdateAppointment() {
        // Arrange
        Long appointmentId = 1L;
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDate(LocalDate.of(2024, 11, 30));
        requestDto.setTime(LocalTime.of(10, 30));
        requestDto.setTechnician(EmployeeRequestDto.builder().id(1L).build()); // Technician ID
        requestDto.setNotes("Updated notes");

        // output
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);

        Employee technician = new Employee();
        technician.setId(1L);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(technician));

        // Act
        appointmentService.updateAppointment(appointmentId, requestDto);

        // Assert
        assertEquals(LocalDate.of(2024, 11, 30), existingAppointment.getDate());
        assertEquals(LocalTime.of(10, 30), existingAppointment.getTime());
        assertEquals("Updated notes", existingAppointment.getNotes());
        assertEquals(technician, existingAppointment.getTechnician());

        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void testUpdateAppointment_appointmentNotFound_shouldThrowException() {
        // Arrange
        Long appointmentId = 1L;
        AppointmentRequestDto requestDto = new AppointmentRequestDto();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.updateAppointment(appointmentId, requestDto));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testUpdateAppointment_technicianNotFound_shouldThrowException() {
        // Arrange
        Long appointmentId = 1L;
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDate(LocalDate.of(2024, 11, 30));
        requestDto.setTime(LocalTime.of(10, 30));
        requestDto.setTechnician(EmployeeRequestDto.builder().id(1L).build()); // Technician ID
        requestDto.setNotes("Updated notes");

        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.updateAppointment(appointmentId, requestDto));

        assertEquals("Technician not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testCancelAppointment_withValidId_shouldUpdateStatusToCancelled() {
        // Arrange
        Long appointmentId = 1L;
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);
        existingAppointment.setStatus(AppointmentStatusEnum.BOOKED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // Act
        appointmentService.cancelAppointment(appointmentId);

        // Assert
        assertEquals(AppointmentStatusEnum.CANCELLED, existingAppointment.getStatus());
    }

    @Test
    void testCancelAppointment_appointmentNotFound_shouldThrowException() {
        // Arrange
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.cancelAppointment(appointmentId));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testCompleteAppointment_withExistingLoyaltyPoints_shouldUpdatePoints() {
        // Arrange
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatusEnum.BOOKED);

        Customer customer = new Customer();
        customer.setId(1L);
        appointment.setCustomer(customer);

        Invoice invoice = new Invoice();
        invoice.setTotalAmount(100.0);
        appointment.setInvoice(invoice);

        LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
        loyaltyPoint.setEarnedPoint(50);
        loyaltyPoint.setCustomer(customer);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(loyaltyPointRepository.findByCustomerId(customer.getId())).thenReturn(Optional.of(loyaltyPoint));

        // Act
        appointmentService.completeAppointment(appointmentId);

        // Assert
        assertEquals(AppointmentStatusEnum.DONE, appointment.getStatus());
        assertEquals(150, loyaltyPoint.getEarnedPoint());
    }

    @Test
    void testCompleteAppointment_withoutExistingLoyaltyPoints_shouldCreatePoints() {
        // Arrange
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatusEnum.BOOKED);

        Customer customer = new Customer();
        customer.setId(1L);
        appointment.setCustomer(customer);

        Invoice invoice = new Invoice();
        invoice.setTotalAmount(200.0);
        appointment.setInvoice(invoice);

        LoyaltyPoint loyaltyPoint = LoyaltyPoint.builder()
                .earnedPoint((int) appointment.getInvoice().getTotalAmount())
                .customer(appointment.getCustomer())
                .build();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(loyaltyPointRepository.findByCustomerId(customer.getId())).thenReturn(Optional.empty());
        when(loyaltyPointRepository.save(loyaltyPoint)).thenReturn(loyaltyPoint);

        // Act
        appointmentService.completeAppointment(appointmentId);

        // Assert
        assertEquals(AppointmentStatusEnum.DONE, appointment.getStatus());
        assertEquals((int)invoice.getTotalAmount(), loyaltyPoint.getEarnedPoint());
    }

    @Test
    void testCompleteAppointment_withNoInvoice_shouldNotUpdateOrCreatePoints() {
        // Arrange
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatusEnum.BOOKED);

        Customer customer = new Customer();
        customer.setId(1L);
        appointment.setCustomer(customer);

        appointment.setInvoice(null);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // Act
        appointmentService.completeAppointment(appointmentId);

        // Assert
        assertEquals(AppointmentStatusEnum.DONE, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
        verify(loyaltyPointRepository, never()).save(any(LoyaltyPoint.class));
    }

    @Test
    void testCompleteAppointment_withNonExistentAppointment_shouldThrowException() {
        // Arrange
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.completeAppointment(appointmentId));
        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
        verify(loyaltyPointRepository, never()).save(any());
    }

    @Test
    void testGetAppointmentsByCustomerId_withValidCustomerId_shouldReturnAppointments() {
        // Arrange
        Long customerId = 1L;

        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setDate(LocalDate.of(2024, 11, 25));
        appointment1.setTime(LocalTime.of(10, 0));

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDate(LocalDate.of(2024, 11, 24));
        appointment2.setTime(LocalTime.of(14, 30));

        List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findAllByCustomerIdOrderByDateDescTimeDesc(customerId)).thenReturn(mockAppointments);

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAppointmentsByCustomerId(customerId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 11, 25), result.get(0).getDate());
        assertEquals(LocalTime.of(10, 0), result.get(0).getTime());
        assertEquals(LocalDate.of(2024, 11, 24), result.get(1).getDate());
        assertEquals(LocalTime.of(14, 30), result.get(1).getTime());
    }

    @Test
    void testGetAppointmentsByCustomerId_withNoAppointments_shouldReturnEmptyList() {
        // Arrange
        Long customerId = 1L;

        when(appointmentRepository.findAllByCustomerIdOrderByDateDescTimeDesc(customerId)).thenReturn(List.of());

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAppointmentsByCustomerId(customerId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAppointmentsByCustomerPhone_withValidPhoneNumber_shouldReturnAppointments() {
        // Arrange
        String phoneNumber = "1234567890";

        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setDate(LocalDate.of(2024, 11, 25));
        appointment1.setTime(LocalTime.of(10, 0));

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDate(LocalDate.of(2024, 11, 24));
        appointment2.setTime(LocalTime.of(14, 30));

        List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findAllByCustomerPhoneNumberOrderByDateDescTimeDesc(phoneNumber)).thenReturn(mockAppointments);

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAppointmentsByCustomerPhone(phoneNumber);

        // Assert
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 11, 25), result.get(0).getDate());
        assertEquals(LocalTime.of(10, 0), result.get(0).getTime());
        assertEquals(LocalDate.of(2024, 11, 24), result.get(1).getDate());
        assertEquals(LocalTime.of(14, 30), result.get(1).getTime());
    }

    @Test
    void testGetAppointmentsByCustomerPhone_withNoAppointments_shouldReturnEmptyList() {
        // Arrange
        String phoneNumber = "1234567890";

        when(appointmentRepository.findAllByCustomerPhoneNumberOrderByDateDescTimeDesc(phoneNumber)).thenReturn(List.of());

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAppointmentsByCustomerPhone(phoneNumber);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAppointmentById_withValidId_shouldReturnAppointmentResponseDto() {
        // Arrange
        Long appointmentId = 1L;

        Appointment mockAppointment = new Appointment();
        mockAppointment.setId(appointmentId);
        mockAppointment.setNotes("Test Notes");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act
        AppointmentResponseDto result = appointmentService.getAppointmentById(appointmentId);

        // Assert
        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        assertEquals("Test Notes", result.getNotes());
    }

    @Test
    void testGetAppointmentById_withInvalidId_shouldThrowResourceNotFoundException() {
        // Arrange
        Long invalidId = 99L;

        when(appointmentRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.getAppointmentById(invalidId);
        });

        assertEquals("Appointment not found", exception.getMessage());
    }

    @Test
    void testSearchAppointment_withValidDate_shouldReturnAppointments() {
        // Arrange
        LocalDate appointmentDate = LocalDate.of(2024, 11, 30);

        AppointmentSearchRequestDto searchRequestDto = new AppointmentSearchRequestDto();
        searchRequestDto.setAppointmentDate(appointmentDate);

        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setDate(appointmentDate);

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDate(appointmentDate);

        List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findAllByDateOrderByTimeDesc(appointmentDate)).thenReturn(mockAppointments);

        // Act
        List<AppointmentResponseDto> result = appointmentService.searchAppointment(searchRequestDto);

        // Assert
        assertEquals(2, result.size());
        assertEquals(appointment1.getId(), result.get(0).getId());
        assertEquals(appointment2 .getId(), result.get(1).getId());

    }

    @Test
    void testSearchAppointment_withNoAppointments_shouldReturnEmptyList() {
        // Arrange
        LocalDate appointmentDate = LocalDate.of(2024, 11, 30);

        AppointmentSearchRequestDto searchRequestDto = new AppointmentSearchRequestDto();
        searchRequestDto.setAppointmentDate(appointmentDate);

        when(appointmentRepository.findAllByDateOrderByTimeDesc(appointmentDate)).thenReturn(List.of());

        // Act
        List<AppointmentResponseDto> result = appointmentService.searchAppointment(searchRequestDto);

        // Assert
        assertEquals(0, result.size());
    }
}