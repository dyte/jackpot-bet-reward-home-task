package com.sporty.bet_jackpot.util;

import com.sporty.bet_jackpot.dto.BetRequest;

import java.math.BigDecimal;

public class BetRequestGenerator {

    public static BetRequest generateBetRequest() {
        return new BetRequest("betId-1", "userId-1", 1L, new BigDecimal(1_000L));
    }

    public static BetRequest generateBetRequest_NullBetId() {
        return new BetRequest(null, "userId-1", 1L, new BigDecimal(1_000L));
    }

    public static BetRequest generateBetRequest_BlankBetId() {
        return new BetRequest("", "userId-1", 1L, new BigDecimal(1_000L));
    }

    public static BetRequest generateBetRequest_BlankUserId() {
        return new BetRequest("betId-1", "", 1L, new BigDecimal(1_000L));
    }

    public static BetRequest generateBetRequest_NullJackPotIdId() {
        return new BetRequest("betId-1", "userId-1", null, new BigDecimal(1_000L));
    }

    public static BetRequest generateBetRequest_NullBetAmount() {
        return new BetRequest("betId-1", "userId-1", 1L, null);
    }
}
