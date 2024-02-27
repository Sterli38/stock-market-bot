package com.example.stockmarketbot.service;

import com.example.stockmarketbot.response.StockMarketResponse;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestForExperiment {
    @Autowired
    public StockMarketService stockMarketService;

    @Test
    public void test() {
//        StockMarketResponse actualResponse = stockMarketService.getBalanceByCurrency("1", "EURsdas", "egor", "egor");
//        System.out.println(actualResponse.toString());
    }
}
