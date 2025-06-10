package com.haru.SwipeStyle.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UrlProvider {

    @Value("#{'${app.service.urls}'.split(',')}")
    private List<String> urls;

    private int currentIndex = 0;

    public synchronized String getNextUrl() {
        String url = urls.get(currentIndex);
        currentIndex = (currentIndex + 1) % urls.size();
        return url;
    }
}

