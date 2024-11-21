package miu.asd.reservationmanagement.config;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.dto.request.EmployeeRequestDto;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.model.Role;
import miu.asd.reservationmanagement.repository.EmployeeRepository;
import miu.asd.reservationmanagement.repository.RoleRepository;
import miu.asd.reservationmanagement.service.EmployeeService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SetupInitialData implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;

	private final RoleRepository roleRepository;
	private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        System.out.println("SetupInitialData");
        // create roles
        createRoleIfNotFound(RoleEnum.MANAGER);
        createRoleIfNotFound(RoleEnum.TECHNICIAN);
        createRoleIfNotFound(RoleEnum.CUSTOMER);

        // create default manager
        EmployeeRequestDto manager = new EmployeeRequestDto();
        manager.setFirstName("But");
        manager.setLastName("Bui");
        manager.setPassword("1234567890");
        manager.setPhoneNumber("1234567890");
        manager.setRole(RoleEnum.MANAGER);
        manager.setEmail("butbui@example.com");
        createManagerIfNotFound(manager);

        alreadySetup = true;
    }

    private final void createManagerIfNotFound(EmployeeRequestDto manager) {
        Optional<Employee> optionalEmployee =
                employeeRepository.findByPhoneNumberAndStatus(
                        manager.getPhoneNumber(), UserStatusEnum.ACTIVE);
        if (!optionalEmployee.isPresent()) {
            employeeService.saveEmployee(manager);
        }
    }

    private Role createRoleIfNotFound(final RoleEnum roleEnum) {
        Role role = roleRepository.findByRole(roleEnum);
        if (role == null) {
            Role r = new Role();
            r.setRole(roleEnum);
            role = roleRepository.save(r);
        }
        return role;
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
