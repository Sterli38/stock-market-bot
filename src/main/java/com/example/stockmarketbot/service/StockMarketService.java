package com.example.stockmarketbot.service;

import com.example.stockmarketbot.response.StockMarketResponse;
import com.example.stockmarketbot.response.StockMarketResponseGetTransactionsByFilter;
import com.example.stockmarketbot.util.TransactionFilter;

import java.util.List;

public interface StockMarketService {
    StockMarketResponse getBalanceByCurrency(String participantId, Object currency, String login, String password);

    List<StockMarketResponseGetTransactionsByFilter> getTransactionsByFilter(String participantId, String login, String password, TransactionFilter transactionFilter);
}
