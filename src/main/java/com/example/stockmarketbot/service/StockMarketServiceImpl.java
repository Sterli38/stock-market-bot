package com.example.stockmarketbot.service;

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

    public StockMarketResponse getBalanceByCurrency(String participantId, Object currency) {
        String url = "http://localhost:8080/transactional/getBalanceByCurrency"; // Вынести в отдельный файл
        StockMarketResponse stockMarketResponse = new StockMarketResponse();

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // устанавливаем тип

        Map<String, Object> bodyParamMap = new HashMap<>(); // body запроса
        bodyParamMap.put("participant_id", participantId);
        bodyParamMap.put("currency", currency);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyParamMap, httpHeaders); // сущность

        try {
            stockMarketResponse = restTemplate.exchange(url, HttpMethod.GET, entity, StockMarketResponse.class, bodyParamMap).getBody();
        } catch (RestClientException e) {
            throw new RestClientException("Error while sending request to WebCurrencyService");// добавить исключение
        }
        if(stockMarketResponse == null) {
            throw new RestClientException("answer from stock market service was not received");
        }
        return stockMarketResponse;
    }
}