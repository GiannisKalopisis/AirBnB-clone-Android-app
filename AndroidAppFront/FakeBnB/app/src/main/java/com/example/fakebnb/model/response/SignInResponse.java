package com.example.fakebnb.model.response;

import com.example.fakebnb.enums.RoleName;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Set;


public class SignInResponse {

    private boolean success;
    private String message;
    private UserData object;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserData getObject() {
        return object;
    }

    public void setObject(UserData object) {
        this.object = object;
    }


    public class UserData implements Serializable {
        @SerializedName("id")
        private Long id;

        @SerializedName("jwtToken")
        private String jwtToken;

        @SerializedName("tokenType")
        private String tokenType;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("firstName")
        private String firstName;

        @SerializedName("lastName")
        private String lastName;

        @SerializedName("roleNames")
        private Set<RoleName> roleNames;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public void setJwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

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

        public Set<RoleName> getRoleNames() {
            return roleNames;
        }

        public void setRoleNames(Set<RoleName> roleNames) {
            this.roleNames = roleNames;
        }
    }
}
