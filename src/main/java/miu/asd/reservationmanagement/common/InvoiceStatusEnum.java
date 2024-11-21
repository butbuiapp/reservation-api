package miu.asd.reservationmanagement.common;

public enum InvoiceStatusEnum {
    DRAFT("Draft"),
    CANCELLED("Cancelled"),
    PENDING("Pending"),
    PAID("Paid"),
    DELETED("Deleted");

    private final String status;

    InvoiceStatusEnum(String status) {
        this.status = status;
    }
}
