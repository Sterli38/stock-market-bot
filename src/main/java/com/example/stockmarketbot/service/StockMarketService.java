package com.example.stockmarketbot.service;

import com.example.stockmarketbot.integration.stockmarket.request.GetBalanceByCurrencyRequest;
import com.example.stockmarketbot.integration.stockmarket.request.GetTransactionsByFilterRequest;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.integration.stockmarket.response.StockMarketResponse;

import java.util.List;

public interface StockMarketService {
    StockMarketResponse getBalanceByCurrency(String login, String password, GetBalanceByCurrencyRequest request);

    List<GetTransactionsByFilterResponse> getTransactionsByFilter(String login, String password, GetTransactionsByFilterRequest getTransactionsByFilterRequest);
}
