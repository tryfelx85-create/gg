package com.strengthsmp.core.ability;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Per-actor, per-ability mutable state. One instance is held per (actorId, abilityId)
 * pair by AbilityService, so Ability implementations stay stateless and reusable
 * across every actor who equips them.
 *
 * Generic counter/tick-stamp storage covers combo counts, stun stacks, and
 * cooldown-expiry timestamps without each ability needing a bespoke state class.
 */
public final class AbilityRuntimeState {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> timestamps = new ConcurrentHashMap<>();

    public int getCounter(String key) {
        AtomicInteger c = counters.get(key);
        return c == null ? 0 : c.get();
    }

    public int incrementCounter(String key) {
        return counters.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void resetCounter(String key) {
        counters.computeIfAbsent(key, k -> new AtomicInteger(0)).set(0);
    }

    /** Returns true and records "now" if currently past the stored expiry for key (i.e. not on cooldown). */
    public boolean isReady(String key, long nowMillis) {
        AtomicLong expiry = timestamps.get(key);
        return expiry == null || nowMillis >= expiry.get();
    }

    public void setCooldown(String key, long nowMillis, long durationMillis) {
        timestamps.computeIfAbsent(key, k -> new AtomicLong(0)).set(nowMillis + durationMillis);
    }

    public long remainingCooldownMillis(String key, long nowMillis) {
        AtomicLong expiry = timestamps.get(key);
        if (expiry == null) return 0;
        return Math.max(0, expiry.get() - nowMillis);
    }

    /**
     * Records "now" as the last time this event happened (e.g. last combo hit).
     * Distinct from setCooldown: this stores a plain timestamp, not an expiry.
     */
    public void markEvent(String key, long nowMillis) {
        timestamps.computeIfAbsent(key, k -> new AtomicLong(0)).set(nowMillis);
    }

    /** Milliseconds since markEvent was last called for key, or Long.MAX_VALUE if never. */
    public long millisSinceEvent(String key, long nowMillis) {
        AtomicLong last = timestamps.get(key);
        if (last == null) return Long.MAX_VALUE;
        return nowMillis - last.get();
    }
}
