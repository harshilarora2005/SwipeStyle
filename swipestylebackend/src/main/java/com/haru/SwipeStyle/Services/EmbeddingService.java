package com.haru.SwipeStyle.Services;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface EmbeddingService {
    public List<Double> getEmbedding(String altText);
    public String toJson(List<Double> vector) throws JsonProcessingException;

}
