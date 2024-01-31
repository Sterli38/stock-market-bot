package com.example.stockmarketbot.service;

import com.example.stockmarketbot.response.StockMarketResponse;

public interface StockMarketService {
    StockMarketResponse getBalanceByCurrency(String participantId, Object currency, String login, String password);
}
