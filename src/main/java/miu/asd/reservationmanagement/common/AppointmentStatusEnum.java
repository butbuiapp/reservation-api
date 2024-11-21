package miu.asd.reservationmanagement.common;

public enum AppointmentStatusEnum {
    BOOKED("Booked"),
    DONE("Done"),
    CANCELLED("Cancelled"),
    DELETED("Deleted");

    private final String status;

    AppointmentStatusEnum(String status) {
        this.status = status;
    }
}
