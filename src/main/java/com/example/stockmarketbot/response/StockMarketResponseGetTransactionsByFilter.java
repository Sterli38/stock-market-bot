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
    private Double receivedAmount;
    private String givenCurrency;
    private Double givenAmount;
    private Double commission;
}
