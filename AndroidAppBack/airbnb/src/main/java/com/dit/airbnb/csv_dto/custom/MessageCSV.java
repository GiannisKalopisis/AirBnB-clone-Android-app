package com.dit.airbnb.csv_dto.custom;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageCSV {

    @CsvBindByName(column = "senderId")
    private Long senderId;

    @CsvBindByName(column = "receiverId")
    private Long receiverId;

    @CsvBindByName(column = "content")
    private String content;
}
