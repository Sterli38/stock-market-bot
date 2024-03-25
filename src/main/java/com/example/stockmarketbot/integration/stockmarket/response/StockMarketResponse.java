package com.example.stockmarketbot.integration.stockmarket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // переделать ?
public class StockMarketResponse {
    @JsonProperty("currency_balance") // исправить
    private String currencyBalance;
}
