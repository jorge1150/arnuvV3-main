package com.core.arnuv.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RolEnum {
    ROLE_USER("ROLE_CLIENTE"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_WALKER("ROLE_PASEADOR");

    private final String value;
}