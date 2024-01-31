package com.example.stockmarketbot.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockMarketResponse {
    @JsonProperty("currency_balance") // УБРАТЬ ?
    private String currencyBalance;
}
