package com.example.stockmarketbot.integration.stockmarket.request;

import lombok.Data;

@Data
public class GetBalanceByCurrencyRequest extends ParticipantRequest {
    private Object currency;
}
