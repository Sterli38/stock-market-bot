package com.example.stockmarketbot.service;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.integration.stockmarket.request.GetBalanceByCurrencyRequest;
import com.example.stockmarketbot.integration.stockmarket.request.GetTransactionsByFilterRequest;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.integration.stockmarket.response.GetBalanceByCurrencyResponse;
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

    public GetBalanceByCurrencyResponse getBalanceByCurrency(String login, String password, GetBalanceByCurrencyRequest request) {
        String url = getFinalUrl("/transactional/getBalanceByCurrency");
        GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = new GetBalanceByCurrencyResponse();

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // устанавливаем тип
        httpHeaders.setBasicAuth(login, password);

        HttpEntity<GetBalanceByCurrencyRequest> entity = new HttpEntity<>(request, httpHeaders); // сущность

        try {
            getBalanceByCurrencyResponse = restTemplate.exchange(url, HttpMethod.GET, entity, GetBalanceByCurrencyResponse.class).getBody();// вынести в метод doGet
        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());// добавить исключение
        }
        if(getBalanceByCurrencyResponse == null) {
            throw new RestClientException("answer from stockMarket service was not received");
        }
        return getBalanceByCurrencyResponse;
    }

    public List<GetTransactionsByFilterResponse> getTransactionsByFilter(String login, String password, GetTransactionsByFilterRequest request) {
        String url = getFinalUrl("/transactional/getTransactions");

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
        if(entity1.getBody() == null) {
            throw new RestClientException("answer from stockMarket service was not received");
        }
        return entity1.getBody();
    }

    private String getFinalUrl(String endpoint) {
        return applicationProperties.getStockMarketServiceUrl() + endpoint;
    }

}
