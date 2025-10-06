package com.sporty.bet_jackpot.controller;

import com.sporty.bet_jackpot.dto.RewardResponse;
import com.sporty.bet_jackpot.service.CacheManagementService;
import com.sporty.bet_jackpot.service.JackpotRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
public class RewardController {
    
    private final JackpotRewardService rewardService;
    private final CacheManagementService cacheManagementService;
    
    @GetMapping("/evaluate/{betId}")
    public ResponseEntity<RewardResponse> evaluateReward(@PathVariable String betId) {
        log.info("Evaluating reward for bet: {}", betId);
        
        RewardResponse response = rewardService.evaluateReward(betId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        log.info("Clearing all caches");
        cacheManagementService.clearAllCaches();
        return ResponseEntity.ok(Map.of("message", "All caches cleared successfully"));
    }
    
    @PostMapping("/cache/clear/{jackpotId}")
    public ResponseEntity<Map<String, String>> clearJackpotCache(@PathVariable Long jackpotId) {
        log.info("Clearing cache for jackpot: {}", jackpotId);
        cacheManagementService.clearJackpotCache(jackpotId);
        return ResponseEntity.ok(Map.of("message", "Cache cleared for jackpot: " + jackpotId));
    }
    
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, String>> getCacheStats() {
        String stats = cacheManagementService.getCacheStats();
        return ResponseEntity.ok(Map.of("stats", stats));
    }
}