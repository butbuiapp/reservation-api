package miu.asd.reservationmanagement.config;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.Employee;
import miu.asd.reservationmanagement.repository.CustomerRepository;
import miu.asd.reservationmanagement.repository.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // specify which provider is used
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService());

        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<Customer> optionalCustomer = customerRepository.findByPhoneNumberAndStatus(
                    username, UserStatusEnum.ACTIVE);
            if (optionalCustomer.isPresent()) return optionalCustomer.get();

            Optional<Employee> optionalEmployee = employeeRepository.findByPhoneNumberAndStatus(
                    username, UserStatusEnum.ACTIVE);
            if (optionalEmployee.isPresent()) return optionalEmployee.get();
            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}