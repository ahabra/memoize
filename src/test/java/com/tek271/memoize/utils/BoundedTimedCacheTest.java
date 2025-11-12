package com.tek271.memoize.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class BoundedTimedCacheTest {
  BoundedTimedCache<Integer, String> sut;


  @Test
  void cacheSizeIsLimited() {
    sut = new BoundedTimedCache<>(3, Duration.of(5, SECONDS));
    sut.put(1, "one");
    sut.put(2, "two");
    sut.put(3, "three");
    sut.put(4, "four");

    assertEquals(3, sut.size());

    sut.put(5, "five");
    assertEquals(3, sut.size());
  }

  @Test
  void oldestEntryIsRemovedWhenSizeReachesMax() {
    sut = new BoundedTimedCache<>(2, Duration.of(5, SECONDS));
    sut.put(1, "one");
    sut.put(2, "two");
    sut.put(3, "three");

    assertEquals(2, sut.size());
    assertNull(sut.get(1));
    assertEquals("two", sut.get(2).value);
    assertEquals("three", sut.get(3).value);
  }

  @Test
  void entriesAreRemovedWhenTheyPassTheirTTL() throws InterruptedException {
    sut = new BoundedTimedCache<>(4, Duration.of(1, MILLIS));
    sut.put(1, "one");
    sut.put(2, "two");
    Thread.sleep(2);

    assertEquals(0, sut.size());
  }

  @Test
  void remove_willRemoveByKey() {
    sut = new BoundedTimedCache<>(2, Duration.of(5, SECONDS));
    sut.put(1, "one");
    sut.put(2, "two");

    sut.remove(3);
    assertEquals(2, sut.size());

    String removed = sut.remove(2);
    assertEquals(1, sut.size());
    assertEquals("one", sut.get(1).value);
    assertEquals("two", removed);
  }

  @Test
  void clearRemovesAll() {
    sut = new BoundedTimedCache<>(2, Duration.of(5, SECONDS));
    sut.put(1, "one");
    sut.put(2, "two");

    sut.clear();
    assertEquals(0, sut.size());
  }

  @Test
  void keySetReturnsNoneExpiredKeys() throws InterruptedException {
    sut = new BoundedTimedCache<>(4, Duration.of(1, MILLIS));
    sut.put(1, "one");
    sut.put(2, "two");
    Thread.sleep(2);
    sut.put(3, "three");

    Set<Integer> keys = sut.keySet();
    assertEquals(1, keys.size());
    assertTrue(keys.contains(3));
  }

  @Test
  void sizeChangesAfterExpiry() throws InterruptedException {
    sut = new BoundedTimedCache<>(4, Duration.of(1, MILLIS));
    sut.put(1, "one");
    sut.put(2, "two");
    assertEquals(2, sut.size());
    Thread.sleep(2);

    assertEquals(0, sut.size());
  }

  @Test
  void isEmpty_trueAfterExpiry() throws InterruptedException {
    sut = new BoundedTimedCache<>(4, Duration.of(1, MILLIS));
    assertTrue(sut.isEmpty());
    sut.put(1, "one");
    assertFalse(sut.isEmpty());
    Thread.sleep(2);
    assertTrue(sut.isEmpty());
  }

  @Test
  void containsKey_willNotFindExpiredKeys() throws InterruptedException {
    sut = new BoundedTimedCache<>(4, Duration.of(1, MILLIS));
    sut.put(1, "one");
    sut.put(2, "two");
    assertTrue(sut.containsKey(1));
    assertTrue(sut.containsKey(2));
    assertFalse(sut.containsKey(3));

    Thread.sleep(2);
    assertFalse(sut.containsKey(1));
    assertFalse(sut.containsKey(2));
  }



}