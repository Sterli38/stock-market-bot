package com.example.stockmarketbot.service;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.response.StockMarketResponse;
import com.example.stockmarketbot.response.StockMarketResponseGetTransactionsByFilter;
import com.example.stockmarketbot.util.TransactionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketServiceImpl implements StockMarketService {
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    public StockMarketResponse getBalanceByCurrency(String participantId, Object currency, String login, String password) {
        String url = applicationProperties.getStockMarketServiceUrl() + "/transactional/getBalanceByCurrency";
        StockMarketResponse stockMarketResponse = new StockMarketResponse();

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // устанавливаем тип

        Map<String, Object> bodyParamMap = new HashMap<>(); // body запроса
        bodyParamMap.put("participant_id", participantId);
        bodyParamMap.put("currency", currency);
        httpHeaders.setBasicAuth(login, password);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyParamMap, httpHeaders); // сущность

        try {
            stockMarketResponse = restTemplate.exchange(url, HttpMethod.GET, entity, StockMarketResponse.class, bodyParamMap).getBody();
        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());// добавить исключение
        }
        if(stockMarketResponse == null) {
            throw new RestClientException("answer from stockMarket service was not received");
        }
        return stockMarketResponse;
    }

    public List<StockMarketResponseGetTransactionsByFilter> getTransactionsByFilter(String participantId, String login, String password, TransactionFilter transactionFilter) {
        String url = applicationProperties.getStockMarketServiceUrl() + "/transactional/getTransactions";
        StockMarketResponseGetTransactionsByFilter stockMarketResponseGetTransactionsByFilter = new StockMarketResponseGetTransactionsByFilter();

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> bodyParamMap = new HashMap<>();
        bodyParamMap.put("participant_id", participantId);

        bodyParamMap.put("operation_type", transactionFilter.getOperationType());
        bodyParamMap.put("after", transactionFilter.getAfter());
        bodyParamMap.put("before", transactionFilter.getBefore());
        bodyParamMap.put("received_min_amount", transactionFilter.getReceivedMinAmount());
        bodyParamMap.put("received_max_amount", transactionFilter.getReceivedMaxAmount());
        bodyParamMap.put("given_min_amount", transactionFilter.getGivenMinAmount());
        bodyParamMap.put("given_max_amount", transactionFilter.getGivenMaxAmount());
        bodyParamMap.put("received_currencies", transactionFilter.getReceivedCurrencies());
        bodyParamMap.put("given_currencies", transactionFilter.getGivenCurrencies());

        httpHeaders.setBasicAuth(login, password);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyParamMap, httpHeaders);
        ResponseEntity<List<StockMarketResponseGetTransactionsByFilter>> entity1;

        try {
           entity1 = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<StockMarketResponseGetTransactionsByFilter>>() {
            }, bodyParamMap);
        } catch (RestClientException exception) {
            throw new RestClientException(exception.getMessage());// добавить исключение
        }
        if(stockMarketResponseGetTransactionsByFilter == null) {
            throw new RestClientException("answer from stockMarket service was not received");
        }
        return entity1.getBody();
    }
}
