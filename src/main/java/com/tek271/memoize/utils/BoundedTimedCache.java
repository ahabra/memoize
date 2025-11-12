package com.tek271.memoize.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BoundedTimedCache<K, V> {
  private static final int INITIAL_CAPACITY = 16;
  private static final float LOAD_FACTOR = 0.75f;
  private static final boolean LRU_ORDER = true;

  static class TimedValue<V> {
    final V value;
    /** timeStamp intentionally is NOT used in equals() and hashCode() */
    final long timeStampNanos;

    TimedValue(V value) {
      this.value = value;
      this.timeStampNanos = System.nanoTime();
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || getClass() != other.getClass()) {
        return false;
      }
      TimedValue<?> that = (TimedValue<?>) other;
      return Objects.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(value);
    }

  }

  public static class CacheValue<V> {
    public final V value;

    private CacheValue(V value) {
      this.value = value;
    }

    public static <V> CacheValue<V> of(V value) {
      return new CacheValue<>(value);
    }

    @Override
    public String toString() {
      return "CacheValue.value=" + value;
    }
  }


  public final int maxSize;
  public final Duration timeToLive;
  public final long timeToLiveNanos;
  private final Map<K, TimedValue<V>> map;

  public BoundedTimedCache(int maxSize, Duration timeToLive) {
    this.maxSize = maxSize;
    this.timeToLive = timeToLive;
    this.timeToLiveNanos = timeToLive.toNanos();

    this.map = new LinkedHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, LRU_ORDER) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, TimedValue<V>> entry) {
        removeExpired();
        return this.size() > maxSize;
      }
    };
  }

  public BoundedTimedCache(int maxSize, long timeToLive, TimeUnit timeUnit) {
    this(maxSize, toDuration(timeToLive, timeUnit));
  }

  private static Duration toDuration(long amount, TimeUnit timeUnit) {
    ChronoUnit chronoUnit = timeUnit.toChronoUnit();
    return Duration.of(amount, chronoUnit);
  }

  private synchronized void removeExpired() {
    map.entrySet().removeIf(e -> isExpired(e.getValue()));
  }

  private boolean isExpired(TimedValue<V> timedValue) {
    return System.nanoTime() - timedValue.timeStampNanos > timeToLiveNanos;
  }

  public synchronized void put(K key, V value) {
    removeExpired();
    map.put(key, new TimedValue<>(value));
  }

  public CacheValue<V> get(K key) {
    removeExpired();
    TimedValue<V> timedValue = map.get(key);
    if (timedValue == null) {
      return null;
    }
    return CacheValue.of(timedValue.value);
  }

  public synchronized V remove(K key) {
    TimedValue<V> oldValue = map.remove(key);
    if (oldValue == null) {
      return null;
    }
    removeExpired();
    return oldValue.value;
  }

  public synchronized void clear() {
    map.clear();
  }

  public Set<K> keySet() {
    removeExpired();
    return map.keySet();
  }

  public int size() {
    removeExpired();
    return map.size();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public boolean containsKey(K key) {
    return keySet().contains(key);
  }

}
