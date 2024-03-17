package com.example.stockmarketbot.service;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.response.StockMarketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
}
