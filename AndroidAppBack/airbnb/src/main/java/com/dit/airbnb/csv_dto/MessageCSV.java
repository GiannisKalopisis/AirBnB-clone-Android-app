package com.dit.airbnb.csv_dto;

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

    @CsvBindByName(column = "senderUsername")
    private String senderUsername;

    @CsvBindByName(column = "receiverUsername")
    private String receiverUsername;

    @CsvBindByName(column = "content")
    private String content;
}
