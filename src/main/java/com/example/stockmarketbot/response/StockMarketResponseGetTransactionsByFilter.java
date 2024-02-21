package com.example.stockmarketbot.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // переделать ?
public class StockMarketResponseGetTransactionsByFilter {
    private Long id;
    private String operationType;
    private Date date;
    private String receivedCurrency;
    private String receivedAmount;
    private String givenCurrency;
    private String givenAmount;
    private Double commission;
}
