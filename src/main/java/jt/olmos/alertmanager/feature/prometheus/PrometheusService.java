package jt.olmos.alertmanager.feature.prometheus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrometheusService {

    @Async
    public CompletableFuture<String> fetchDataFromExternalApi(String query) {
        log.info("Fetching data for query: {} on thread: {}", query, Thread.currentThread());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture("Data for " + query);
    }
}
