package com.dit.airbnb.util;

import com.dit.airbnb.dto.enums.RoleName;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleNameEnumConverter implements Converter<String, RoleName> {
    @Override
    public RoleName convert(@NotNull String value) {
        return RoleName.valueOf(value);
    }
}