package com.sporty.bet_jackpot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Service for managing cache operations.
 * Provides methods to clear, inspect, and manage caches.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheManagementService {
    
    private final CacheManager cacheManager;
    
    /**
     * Clears all caches.
     */
    public void clearAllCaches() {
        log.info("Clearing all caches");
        cacheManager.getCacheNames().forEach(this::clearCache);
    }
    
    /**
     * Clears a specific cache by name.
     * @param cacheName the name of the cache to clear
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cleared cache: {}", cacheName);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
    }
    
    /**
     * Clears the jackpot cache for a specific jackpot ID.
     * @param jackpotId the jackpot ID to clear from cache
     */
    public void clearJackpotCache(Long jackpotId) {
        Cache cache = cacheManager.getCache("jackpots");
        if (cache != null) {
            cache.evict(jackpotId);
            log.info("Evicted jackpot from cache: {}", jackpotId);
        }
    }
    
    /**
     * Gets cache statistics.
     * @return cache statistics as a string
     */
    public String getCacheStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Cache Statistics:\n");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                stats.append(String.format("- %s: %s\n", cacheName, cache.getNativeCache().getClass().getSimpleName()));
            }
        });
        
        return stats.toString();
    }
}
