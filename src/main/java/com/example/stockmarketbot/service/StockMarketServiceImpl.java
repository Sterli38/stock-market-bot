package com.example.stockmarketbot.service;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.integration.stockmarket.request.GetBalanceByCurrencyRequest;
import com.example.stockmarketbot.integration.stockmarket.request.GetTransactionsByFilterRequest;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.integration.stockmarket.response.StockMarketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketServiceImpl implements StockMarketService {
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    public StockMarketResponse getBalanceByCurrency(String login, String password, GetBalanceByCurrencyRequest request) {
        String url = getFinalUrl("/transactional/getBalanceByCurrency");
        StockMarketResponse stockMarketResponse = new StockMarketResponse();

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // устанавливаем тип
        httpHeaders.setBasicAuth(login, password);

        HttpEntity<GetBalanceByCurrencyRequest> entity = new HttpEntity<>(request, httpHeaders); // сущность

        try {
            stockMarketResponse = restTemplate.exchange(url, HttpMethod.GET, entity, StockMarketResponse.class).getBody();// вынести в метод doGet
        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());// добавить исключение
        }
        if(stockMarketResponse == null) {
            throw new RestClientException("answer from stockMarket service was not received");
        }
        return stockMarketResponse;
    }

    public List<GetTransactionsByFilterResponse> getTransactionsByFilter(String login, String password, GetTransactionsByFilterRequest request) {
        String url = getFinalUrl("/transactional/getTransactions");
        GetTransactionsByFilterResponse getTransactionsByFilterResponse = new GetTransactionsByFilterResponse();

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(login, password);

        HttpEntity<GetTransactionsByFilterRequest> entity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<List<GetTransactionsByFilterResponse>> entity1;

        try {
           entity1 = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
           });
        } catch (RestClientException exception) {
            throw new RestClientException(exception.getMessage());// добавить исключение
        }
        if(getTransactionsByFilterResponse == null) {
            throw new RestClientException("answer from stockMarket service was not received");
        }
        return entity1.getBody();
    }

    private String getFinalUrl(String endpoint) {
        return applicationProperties.getStockMarketServiceUrl() + endpoint;
    }

}
