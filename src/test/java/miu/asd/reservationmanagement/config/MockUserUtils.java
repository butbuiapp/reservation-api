package miu.asd.reservationmanagement.config;

import miu.asd.reservationmanagement.common.RoleEnum;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.model.Role;

public class MockUserUtils {
    public static Employee getMockManager() {
        Employee manager = new Employee();
        manager.setFirstName("But");
        manager.setLastName("Bui");
        manager.setPassword("1234567890");
        manager.setPhoneNumber("1234567890");

        Role role = new Role();
        role.setRole(RoleEnum.MANAGER);
        manager.setRole(role);
        manager.setEmail("butbui@example.com");
        return manager;
    }

    public static Customer getMockCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Joe");
        customer.setLastName("Don");
        customer.setPassword("1234567890");
        customer.setPhoneNumber("1234567890");

        Role role = new Role();
        role.setRole(RoleEnum.CUSTOMER);
        customer.setRole(role);
        customer.setEmail("butbui@example.com");
        return customer;
    }
}
