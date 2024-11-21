package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.config.ApplicationConfig;
import miu.asd.reservationmanagement.dto.request.ChangePasswordRequestDto;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.dto.response.EmployeeResponseDto;
import miu.asd.reservationmanagement.exception.InvalidPasswordException;
import miu.asd.reservationmanagement.exception.NotFoundException;
import miu.asd.reservationmanagement.exception.RecordAlreadyExistsException;
import miu.asd.reservationmanagement.mapper.EmployeeMapper;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.model.Role;
import miu.asd.reservationmanagement.repository.EmployeeRepository;
import miu.asd.reservationmanagement.repository.RoleRepository;
import miu.asd.reservationmanagement.service.EmployeeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveEmployee(EmployeeRequestDto employeeRequestDto) {
        Optional<Employee> optionalEmployee =
                employeeRepository.findByPhoneNumberAndStatus(
                        employeeRequestDto.getPhoneNumber(),
                        UserStatusEnum.ACTIVE);
        if (optionalEmployee.isPresent()) {
            throw new RecordAlreadyExistsException("Phone number already used");
        }
        // map dto to entity
        Employee employee = EmployeeMapper.MAPPER.dtoToEntity(employeeRequestDto);
        employee.setStatus(UserStatusEnum.ACTIVE);
        // encode password
        employee.setPassword(passwordEncoder.encode(employeeRequestDto.getPassword()));
        // get role
        Role role = roleRepository.findByRole(employeeRequestDto.getRole());
        employee.setRole(role);
        employeeRepository.save(employee);
    }

    @Override
    public void updateEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Optional<Employee> optionalEmployee = findById(id);
        if (optionalEmployee.isPresent()) {
            Employee existingEmployee = optionalEmployee.get();
            // find employee by phone number
            Optional<Employee> phoneNumber =
                    employeeRepository.findByPhoneNumberAndStatus(
                            employeeRequestDto.getPhoneNumber(),
                            UserStatusEnum.ACTIVE);
            if (phoneNumber.isPresent() &&
                    phoneNumber.get().getId() != existingEmployee.getId()) {
                throw new RecordAlreadyExistsException("Phone number already exists");
            } else {
                // update customer
                existingEmployee.setFirstName(employeeRequestDto.getFirstName());
                existingEmployee.setLastName(employeeRequestDto.getLastName());
                existingEmployee.setEmail(employeeRequestDto.getEmail());
                existingEmployee.setDob(employeeRequestDto.getDob());
                existingEmployee.setPhoneNumber(employeeRequestDto.getPhoneNumber());
                if (!existingEmployee.getRole().getRole().equals(employeeRequestDto.getRole())) {
                    // get role
                    Role role = roleRepository.findByRole(employeeRequestDto.getRole());
                    existingEmployee.setRole(role);
                }
                employeeRepository.save(existingEmployee);
            }
        }
    }

    @Override
    public void deleteEmployeeById(Long id) {
        Optional<Employee> optionalEmployee = findById(id);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setStatus(UserStatusEnum.DELETED);
            employeeRepository.save(employee);
        }
    }

    @Override
    public List<EmployeeResponseDto> getActiveEmployees() {
        List<Employee> employees = employeeRepository.findAllByStatusIsOrderByLastName(UserStatusEnum.ACTIVE);
        return employees.stream().map(EmployeeMapper.MAPPER::entityToDto).collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        Optional<Employee> optionalEmployee = findById(id);
        return EmployeeMapper.MAPPER.entityToDto(optionalEmployee.get());
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequestDto dto) {
        // check new password and confirm password
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidPasswordException("Passwords do not match.");
        }
        Employee employee = findById(id).get();
        // check phone number
        if (!employee.getPhoneNumber().equals(dto.getPhoneNumber())) {
            throw new InvalidPasswordException("The phone number is incorrect.");
        }

        // check old password
        if (!passwordEncoder.matches(dto.getOldPassword(), employee.getPassword())) {
            throw new InvalidPasswordException("The old password is incorrect.");
        }

        // save new password
        employee.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        employeeRepository.save(employee);
    }

    private Optional<Employee> findById(Long id) {
        Optional<Employee> optionalEmployee =
                employeeRepository.findByIdAndStatus(id, UserStatusEnum.ACTIVE);
        if (optionalEmployee.isPresent()) {
            return optionalEmployee;
        }
        throw new NotFoundException("Employee not found");
    }
}
