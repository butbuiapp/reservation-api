package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.common.UserStatusEnum;
import miu.asd.reservationmanagement.exception.NotFoundException;
import miu.asd.reservationmanagement.model.Customer;
import miu.asd.reservationmanagement.model.LoyaltyPoint;
import miu.asd.reservationmanagement.repository.CustomerRepository;
import miu.asd.reservationmanagement.repository.LoyaltyPointRepository;
import miu.asd.reservationmanagement.service.LoyaltyPointService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoyaltyPointServiceImpl implements LoyaltyPointService {
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Integer getCustomerPointByPhoneNumber(String phoneNumber) {
        Integer earnedPoint = 0;
        Optional<Customer> optionalCustomer =
                customerRepository.findByPhoneNumberAndStatus(phoneNumber, UserStatusEnum.ACTIVE);
        if (optionalCustomer.isPresent()) {
            Optional<LoyaltyPoint> optionalLoyaltyPoint =
                    loyaltyPointRepository.findByCustomerId(optionalCustomer.get().getId());
            if (optionalLoyaltyPoint.isPresent()) {
                earnedPoint = optionalLoyaltyPoint.get().getEarnedPoint();
            }
        }
        return earnedPoint;
    }
}
