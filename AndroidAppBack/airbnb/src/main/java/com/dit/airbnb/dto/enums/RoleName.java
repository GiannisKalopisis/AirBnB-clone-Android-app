package com.dit.airbnb.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RoleName {
    ROLE_USER,
    ROLE_HOST;

    public static RoleName getRoleName(String value) {
        if (value.equals("ROLE_USER")) {
            return ROLE_USER;
        } else if (value.equals("ROLE_HOST")) {
            return ROLE_HOST;
        } else {
            return null;
        }
    }

}
