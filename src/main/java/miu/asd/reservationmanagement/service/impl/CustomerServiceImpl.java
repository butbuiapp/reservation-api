package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.CustomerRequestDto;
import miu.asd.reservationmanagement.dto.response.CustomerResponseDto;
import miu.asd.reservationmanagement.exception.InvalidPasswordException;
import miu.asd.reservationmanagement.exception.RecordAlreadyExistsException;
import miu.asd.reservationmanagement.exception.ResourceNotFoundException;
import miu.asd.reservationmanagement.mapper.CustomerMapper;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.Role;
import miu.asd.reservationmanagement.repository.CustomerRepository;
import miu.asd.reservationmanagement.repository.RoleRepository;
import miu.asd.reservationmanagement.service.CustomerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveCustomer(CustomerRequestDto customerRequestDto) {
        Optional<Customer> optionalCustomer =
                customerRepository.findByPhoneNumberAndStatus(
                        customerRequestDto.getPhoneNumber(),
                        UserStatusEnum.ACTIVE);
        if (optionalCustomer.isPresent()) {
            throw new RecordAlreadyExistsException("Phone number already used");
        }
        // map dto to entity
        Customer customer = CustomerMapper.MAPPER.dtoToEntity(customerRequestDto);
        customer.setStatus(UserStatusEnum.ACTIVE);
        // get role
        Optional<Role> optionalRole = roleRepository.findByRole(RoleEnum.CUSTOMER);
        if (!optionalRole.isPresent()) {
            throw new ResourceNotFoundException("Role not found for CUSTOMER");
        }
        customer.setRole(optionalRole.get());
        // encode password
        customer.setPassword(passwordEncoder.encode(customerRequestDto.getPassword()));
        customerRepository.save(customer);
    }

    @Override
    public void updateCustomer(Long id, CustomerRequestDto customerRequestDto) {
        Optional<Customer> optionalCustomer = findById(id);
        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            // find customer by phone number
            Optional<Customer> phoneNumber =
                    customerRepository.findByPhoneNumberAndStatus(
                            customerRequestDto.getPhoneNumber(),
                            UserStatusEnum.ACTIVE);
            if (phoneNumber.isPresent() &&
                    phoneNumber.get().getId() != existingCustomer.getId()) {
                throw new RecordAlreadyExistsException("Phone number already exists");
            } else {
                // update customer
                existingCustomer.setFirstName(customerRequestDto.getFirstName());
                existingCustomer.setLastName(customerRequestDto.getLastName());
                existingCustomer.setEmail(customerRequestDto.getEmail());
                existingCustomer.setDob(customerRequestDto.getDob());
                existingCustomer.setPhoneNumber(customerRequestDto.getPhoneNumber());
                customerRepository.save(existingCustomer);
            }
        } else {
            throw new ResourceNotFoundException("Customer not found");
        }
    }

    @Override
    public void deleteCustomerById(Long id) {
        Optional<Customer> optionalCustomer = findById(id);
        Customer customer = optionalCustomer.get();
        customer.setStatus(UserStatusEnum.DELETED);
        customerRepository.save(customer);
    }

    @Override
    public List<CustomerResponseDto> getActiveCustomers() {
        List<Customer> customers = customerRepository.findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE);
        return customers.stream().map(CustomerMapper.MAPPER::entityToDto).collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDto getCustomerById(Long id) {
        Optional<Customer> optionalCustomer = findById(id);
        return CustomerMapper.MAPPER.entityToDto(optionalCustomer.get());
    }

    @Override
    public CustomerResponseDto getCustomerByPhone(String phoneNumber) {
        Optional<Customer> optionalCustomer = findByPhone(phoneNumber);
        return CustomerMapper.MAPPER.entityToDto(optionalCustomer.get());
    }

    @Override
    public void changePassword(String phoneNumber, ChangePasswordRequestDto dto) {
        // check new password and confirm password
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidPasswordException("Passwords do not match.");
        }
        Customer customer = findByPhone(phoneNumber).get();
        // check phone number
        if (!customer.getPhoneNumber().equals(dto.getPhoneNumber())) {
            throw new InvalidPasswordException("The phone number is incorrect.");
        }
        // check old password
        if (!passwordEncoder.matches(dto.getOldPassword(), customer.getPassword())) {
            throw new InvalidPasswordException("The old password is incorrect.");
        }
        // save new password
        customer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        customerRepository.save(customer);
    }

    private Optional<Customer> findByPhone(String phoneNumber) {
        Optional<Customer> optionalCustomer =
                customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE);
        if (optionalCustomer.isPresent()) {
            return optionalCustomer;
        }
        throw new ResourceNotFoundException("Customer not found");
    }

    private Optional<Customer> findById(Long id) {
        Optional<Customer> optionalCustomer =
                customerRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE);
        if (optionalCustomer.isPresent()) {
            return optionalCustomer;
        }
        throw new ResourceNotFoundException("Customer not found");
    }
}
