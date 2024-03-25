package com.example.stockmarketbot.service;

import com.example.stockmarketbot.integration.stockmarket.response.StockMarketResponse;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.integration.stockmarket.request.TransactionFilter;

import java.util.List;

public interface StockMarketService {
    StockMarketResponse getBalanceByCurrency(String participantId, Object currency, String login, String password);

    List<GetTransactionsByFilterResponse> getTransactionsByFilter(String participantId, String login, String password, TransactionFilter transactionFilter);
}
