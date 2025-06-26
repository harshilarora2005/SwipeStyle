package com.haru.SwipeStyle.Services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.SwipeStyle.Services.EmbeddingService;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

public class EmbeddingServiceImpl implements EmbeddingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String EMBEDDING_API_URL = "http://localhost:8000/embed";
    @Override
    public List<Double> getEmbedding(String altText) {
        Map<String, String> request = Map.of("alt_text", altText);
        Map<String, List<Double>> response = restTemplate.postForObject(
                EMBEDDING_API_URL, request, Map.class);
        return response.get("embedding");
    }

    @Override
    public String toJson(List<Double> vector) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(vector);
    }
}
