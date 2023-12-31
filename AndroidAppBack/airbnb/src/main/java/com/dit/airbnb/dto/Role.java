package com.dit.airbnb.dto;


import com.dit.airbnb.dto.enums.RoleName;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    //@NaturalId
    private RoleName name;

    @Getter
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<UserReg> userRegs = new HashSet<>();

    public Role() {
    }

    public Role(RoleName name) {
        this.name = name;
    }

    public Role(RoleName name, Set<UserReg> userRegs) {
        this.name = name;
        this.userRegs = userRegs;
    }

    public void setUserRegs(Set<UserReg> userRegs) {
        this.userRegs = userRegs;
    }
}