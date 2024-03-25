package com.example.stockmarketbot.integration.stockmarket.request;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class TransactionFilter {
    private String operationType;
    private Date after;
    private Date before;
    private Double receivedMinAmount;
    private Double receivedMaxAmount;
    private Double givenMinAmount;
    private Double givenMaxAmount;
    private Set<String> givenCurrencies;
    private Set<String> receivedCurrencies;
}
