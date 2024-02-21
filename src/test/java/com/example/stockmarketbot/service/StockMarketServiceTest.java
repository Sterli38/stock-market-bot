package com.example.stockmarketbot.service;

import com.example.stockmarketbot.response.StockMarketResponse;
import com.example.stockmarketbot.util.TransactionFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StockMarketServiceTest {
    @Autowired
    private StockMarketServiceImpl stockMarketService;

    @Test
    public void getBalanceByCurrencyTest() {
        StockMarketResponse stockMarketResponse = stockMarketService.getBalanceByCurrency("1", "EUR", "egor", "egor");
        Assertions.assertEquals("43.33", stockMarketResponse.getCurrencyBalance());
    }

    @Test
    public void getTransactionsByFilter() {
        TransactionFilter transactionFilter = new TransactionFilter();
        System.out.println(stockMarketService.getTransactionsByFilter("1", "egor", "egor", transactionFilter));
    }
}
