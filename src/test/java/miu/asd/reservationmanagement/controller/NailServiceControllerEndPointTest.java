package miu.asd.reservationmanagement.controller;

import com.google.gson.Gson;
import miu.asd.reservationmanagement.common.Constant;
import miu.asd.reservationmanagement.common.ServiceStatusEnum;
import miu.asd.reservationmanagement.config.JwtFilterMockConfig;
import miu.asd.reservationmanagement.config.MockUserUtils;
import miu.asd.reservationmanagement.model.NailService;
import miu.asd.reservationmanagement.service.NailServiceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = {JwtFilterMockConfig.class})
@AutoConfigureMockMvc
class NailServiceControllerEndPointTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NailServiceService nailServiceService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = MockUserUtils.getMockManager();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(userDetails);
        Mockito.when(securityFilterChain.matches(Mockito.any())).thenReturn(true);
        Mockito.when(authenticationProvider.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).
                thenReturn(authentication);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateService_created() throws Exception {
        // input
        NailService input = NailService.builder()
                .name("Manicure")
                .price(20d)
                .duration(30)
                .description("Nail treatment")
                .build();

        // output
        NailService expected = NailService.builder()
                .id(1)
                .name("Manicure")
                .price(20d)
                .duration(30)
                .description("Nail treatment")
                .status(ServiceStatusEnum.ACTIVE)
                .build();
        // mock
        Mockito.when(nailServiceService.saveService(input)).thenReturn(expected);

        mockMvc.perform(
                MockMvcRequestBuilders.post(Constant.SERVICE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(input)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(new Gson().toJson(expected)));

    }

    @Test
    void updateService() {
    }

    @Test
    void getAllServices() {
    }

    @Test
    void getServiceById() {
    }

    @Test
    void deleteService() {
    }
}