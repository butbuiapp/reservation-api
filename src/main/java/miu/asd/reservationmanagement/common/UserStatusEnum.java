package miu.asd.reservationmanagement.common;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    ACTIVE("Active"),
    DELETED("Deleted");

    private final String status;

    UserStatusEnum(String status) {
        this.status = status;
    }

}
