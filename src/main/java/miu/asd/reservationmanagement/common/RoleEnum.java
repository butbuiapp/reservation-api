package miu.asd.reservationmanagement.common;

import lombok.Getter;

@Getter
public enum RoleEnum {
    MANAGER("ROLE_MANAGER"),
    TECHNICIAN("ROLE_TECHNICIAN"),
    CUSTOMER("ROLE_CUSTOMER");

    private final String authority;

    RoleEnum(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return String.valueOf(authority);
    }
}
