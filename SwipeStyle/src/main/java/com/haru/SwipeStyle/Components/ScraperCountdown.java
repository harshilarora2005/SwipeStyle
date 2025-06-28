package com.haru.SwipeStyle.Components;

import org.springframework.stereotype.Component;
import java.util.concurrent.CountDownLatch;

@Component
public class ScraperCountdown{

    private final CountDownLatch latch = new CountDownLatch(4);

    public void markScraperDone() {
        latch.countDown();
        System.out.println("Scraper marked done. Remaining: " + latch.getCount());
    }

    public void awaitCompletion() throws InterruptedException {
        latch.await();
    }

    public boolean awaitCompletion(long timeoutMillis) throws InterruptedException {
        return latch.await(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    public boolean isCompleted() {
        return latch.getCount() == 0;
    }
}
