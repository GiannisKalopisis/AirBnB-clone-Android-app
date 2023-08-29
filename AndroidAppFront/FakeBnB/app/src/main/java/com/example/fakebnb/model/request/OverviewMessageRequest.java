package com.example.fakebnb.model.request;

import com.example.fakebnb.enums.RoleName;

public class OverviewMessageRequest {

    private RoleName roleName;

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }
}
