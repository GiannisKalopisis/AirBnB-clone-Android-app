package com.dit.airbnb.csv_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Column;
import com.opencsv.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegCSV {

    @CsvBindByName(column = "firstName")
    private String firstName;

    @CsvBindByName(column = "lastName")
    private String lastName;

    @CsvBindByName(column = "username")
    private String username;

    @CsvBindByName(column = "password")
    private String password;

    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByName(column = "phone")
    private String phone;

    // if 1 -> user
    // if 2 -> host
    // if 3 -> user + host
    @CsvBindByName(column = "roleTypeNumber")
    private Integer roleTypeNumber;

}
