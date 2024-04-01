package com.example.stockmarketbot.integration.stockmarket.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetBalanceByCurrencyResponse {
    private String currencyBalance;
}
