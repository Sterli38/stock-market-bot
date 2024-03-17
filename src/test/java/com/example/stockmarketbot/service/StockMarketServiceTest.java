package com.example.stockmarketbot.service;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.response.StockMarketResponse;
import com.example.stockmarketbot.response.StockMarketResponseGetTransactionsByFilter;
import com.example.stockmarketbot.util.TransactionFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StockMarketServiceTest {
    @Autowired
    public ApplicationProperties applicationProperties;
    @Autowired
    public StockMarketServiceImpl stockMarketService;
    @Mock
    public RestTemplate restTemplate;
    @Autowired
    public RestTemplate originalRestTemplate;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(stockMarketService, "restTemplate", restTemplate);
    }

    @AfterEach
    public void after() {
        ReflectionTestUtils.setField(stockMarketService, "restTemplate", originalRestTemplate);
    }

    @Test
    public void getBalanceByCurrencyTest() {
        String url = applicationProperties.getStockMarketServiceUrl() + "/transactional/getBalanceByCurrency";

        StockMarketResponse expectedStockMarketResponse = new StockMarketResponse();
        expectedStockMarketResponse.setCurrencyBalance("43.33");

        ResponseEntity<StockMarketResponse> responseEntity = new ResponseEntity<>(expectedStockMarketResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), any(), any(), eq(StockMarketResponse.class), anyMap()))
                .thenReturn(responseEntity);


        StockMarketResponse actualResponse = stockMarketService.getBalanceByCurrency("1", "EUR", "egor", "egor");

        Assertions.assertEquals(expectedStockMarketResponse, actualResponse);

        }

    @Test
    public void getTransactionsByFilter() {
        String url = applicationProperties.getStockMarketServiceUrl() + "/transactional/getTransactions";

        StockMarketResponseGetTransactionsByFilter depositingEur = new StockMarketResponseGetTransactionsByFilter();
        depositingEur.setId(1L);
        depositingEur.setDate(new Date(1694044800000L));
        depositingEur.setReceivedCurrency("EUR");
        depositingEur.setReceivedAmount(50.0);
        depositingEur.setCommission(2.5);

        StockMarketResponseGetTransactionsByFilter depositingRub = new StockMarketResponseGetTransactionsByFilter();
        depositingRub.setId(2L);
        depositingRub.setDate(new Date(1694044800000L));
        depositingRub.setReceivedCurrency("RUB");
        depositingRub.setReceivedAmount(150000.0);
        depositingRub.setCommission(0.0);

        List<StockMarketResponseGetTransactionsByFilter> expectedResponse = new ArrayList<>();
        expectedResponse.add(depositingEur);
        expectedResponse.add(depositingRub);

        ResponseEntity<List<StockMarketResponseGetTransactionsByFilter>> entity1 = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), any(), any(), eq(new ParameterizedTypeReference<List<StockMarketResponseGetTransactionsByFilter>>() {}), anyMap()))
                .thenReturn(entity1);

        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setOperationType("DEPOSITING");

        List<StockMarketResponseGetTransactionsByFilter> actualResponse = stockMarketService.getTransactionsByFilter("1", "egor", "egor", transactionFilter);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }
}
