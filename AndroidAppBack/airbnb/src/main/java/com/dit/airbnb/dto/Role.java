package com.dit.airbnb.dto;


import com.dit.airbnb.dto.enums.RoleName;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public Set<UserReg> getUserRegs() {
        return userRegs;
    }

    public void setUserRegs(Set<UserReg> userRegs) {
        this.userRegs = userRegs;
    }
}