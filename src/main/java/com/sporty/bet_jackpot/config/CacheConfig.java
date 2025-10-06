package com.sporty.bet_jackpot.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for caching in the application.
 * Provides cache management for frequently accessed data.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * Creates a cache manager for the application.
     * Uses ConcurrentMapCacheManager for simple in-memory caching.
     * 
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Define cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "jackpots",           // Cache for jackpot entities
            "jackpot-contributions", // Cache for jackpot contributions
            "jackpot-rewards",    // Cache for jackpot rewards
            "contribution-strategies", // Cache for strategy instances
            "reward-strategies"    // Cache for strategy instances
        ));
        
        // Allow dynamic cache creation
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
