package miu.asd.reservationmanagement.common;

public enum ServiceStatusEnum {
    ACTIVE("Active"),
    DELETED("Deleted");

    private final String status;

    ServiceStatusEnum(String status) {
        this.status = status;
    }
}
