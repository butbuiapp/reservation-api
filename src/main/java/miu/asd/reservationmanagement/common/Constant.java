package miu.asd.reservationmanagement.common;

public class Constant {
    public static final String API_URL_v1_PREFIX = "/api/v1";

    public static final String SERVICE_URL = API_URL_v1_PREFIX + "/services";
    public static final String CUSTOMER_URL = API_URL_v1_PREFIX + "/customers";
    public static final String EMPLOYEE_URL = API_URL_v1_PREFIX + "/employees";
    public static final String APPOINTMENT_URL = API_URL_v1_PREFIX + "/appointments";
    public static final String AUTHENTICATION_URL = API_URL_v1_PREFIX + "/auth";
    public static final String POINT_URL = API_URL_v1_PREFIX + "/point";

    public static final long TOKEN_EXPIRATION_DURATION = 3600000; // 1 hour
}
