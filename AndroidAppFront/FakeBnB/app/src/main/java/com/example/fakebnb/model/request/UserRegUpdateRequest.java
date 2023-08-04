package com.example.fakebnb.model.request;

import com.example.fakebnb.enums.RoleName;

import java.util.Set;

public class UserRegUpdateRequest {

    private String firstName;

    private String lastName;

    private String phone;

    private Set<RoleName> roleNames;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<RoleName> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<RoleName> roleNames) {
        this.roleNames = roleNames;
    }
}
