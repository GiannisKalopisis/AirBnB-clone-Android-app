package com.dit.airbnb.dto;


import com.dit.airbnb.csv_dto.UserRegCSV;
import com.dit.airbnb.request.user_reg.SignUpRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_reg", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class UserReg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userRegHost", cascade = CascadeType.ALL)
    private Set<Apartment> apartments = new HashSet<>();

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userReg", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

    // here is the "user role" user, the user started the conversion
    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firstSenderUserReg", cascade = CascadeType.ALL)
    private Set<Chat> firstSenderChats = new HashSet<>();

    // here is the "host role" user, the user received
    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firstReceiverUserReg", cascade = CascadeType.ALL)
    private Set<Chat> firstReceiverChats = new HashSet<>();

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creatorUserReg", cascade = CascadeType.ALL)
    private Set<BookingReview> bookingReviews = new HashSet<>();

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "senderUserReg", cascade = CascadeType.ALL)
    private Set<Message> sentMessages = new HashSet<>();

    @Getter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image; // may be null

    @Getter
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    public UserReg(SignUpRequest signUpRequest) {
        this.firstName = signUpRequest.getFirstName();
        this.lastName = signUpRequest.getLastName();
        this.username = signUpRequest.getUsername();
        this.email = signUpRequest.getEmail();
        this.password = signUpRequest.getPassword();
        this.phone = signUpRequest.getPhone();
    }

    public UserReg(UserRegCSV userRegCSV) {
        this.firstName = userRegCSV.getFirstName();
        this.lastName = userRegCSV.getLastName();
        this.username = userRegCSV.getUsername();
        this.email = userRegCSV.getEmail();
        this.password = userRegCSV.getPassword();
        this.phone = userRegCSV.getPhone();
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public String toString() {
        return "UserReg{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
