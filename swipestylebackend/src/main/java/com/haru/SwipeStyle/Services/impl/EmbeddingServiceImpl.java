package com.haru.SwipeStyle.Services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.SwipeStyle.Services.EmbeddingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddingServiceImpl implements EmbeddingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String EMBEDDING_API_URL = "http://localhost:8000/embed";
    private final Map<String, List<Double>> embeddingCache = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(EmbeddingServiceImpl.class);

    @Override
    @Cacheable("embeddings")
    public List<Double> getEmbedding(String altText) {
        if (embeddingCache.containsKey(altText)) {
            return embeddingCache.get(altText);
        }
        try {
            Map<String, String> request = Map.of("alt_text", altText);
            Map<String, List<Double>> response = restTemplate.postForObject(
                    EMBEDDING_API_URL, request, Map.class);
            List<Double> embedding = response.get("embedding");

            embeddingCache.put(altText, embedding);
            return embedding;
        } catch (Exception e) {
            log.error("Failed to get embedding for: {}", altText, e);
            throw new RuntimeException("Embedding service unavailable", e);
        }
    }

    @Override
    public String toJson(List<Double> vector) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(vector);
    }
}
