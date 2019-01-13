package bluebomb.urlshortener.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomCacheManager {
    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        // Expire after access because they not change, expiration is only for resources liberation
        // Low size and low time to expire
        CaffeineCache qrCache = buildCacheExpireAfterAccess(
                "qrCodesCache", ticker, 10, 128
        );

        // High size and high time to expire
        CaffeineCache osDetectorCache = buildCacheExpireAfterAccess(
                "osDetectorCache", ticker, 1440, 2048
        );

        // High size and high time to expire
        CaffeineCache browserDetectorCache = buildCacheExpireAfterAccess(
                "browserDetectorCache", ticker, 1440, 2048
        );

        // Expire after write because it change
        // High size and low time to expire
        CaffeineCache htmlPagesCache = buildCacheExpireAfterWrite(
                "htmlPagesCache", ticker, 10, 2048
        );

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(qrCache, osDetectorCache, browserDetectorCache, htmlPagesCache));
        return manager;
    }

    /**
     * Build cache with expire after write policies
     *
     * @param name            cache name
     * @param ticker          ticker
     * @param minutesToExpire minutes to expire
     * @param size            cache size
     * @return created cache
     */
    private CaffeineCache buildCacheExpireAfterWrite(String name, Ticker ticker, int minutesToExpire, int size) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
                .maximumSize(size)
                .ticker(ticker)
                .build());
    }

    /**
     * Build cache with expire after access policies
     *
     * @param name            cache name
     * @param ticker          ticker
     * @param minutesToExpire minutes to expire
     * @param size            cache size
     * @return created cache
     */
    private CaffeineCache buildCacheExpireAfterAccess(String name, Ticker ticker, int minutesToExpire, int size) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterAccess(minutesToExpire, TimeUnit.MINUTES)
                .maximumSize(size)
                .ticker(ticker)
                .build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
