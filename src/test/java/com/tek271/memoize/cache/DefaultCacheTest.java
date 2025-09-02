package com.tek271.memoize.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DefaultCacheTest {
  private static final int MAX_SIZE=4;
  private static final long TIME_TO_LIVE= 400;
  private static final TimeUnit TIME_UNIT= TimeUnit.MILLISECONDS;
  
  private ICache cache;

  @BeforeEach
  protected void beforeEach() {
    cache= new DefaultCache(MAX_SIZE, TIME_TO_LIVE, TIME_UNIT);
    cache.put("1", "a");
    cache.put("2", "b");
    cache.put("3", "c");
    cache.put("4", "d");
  }

  @AfterEach
  protected void tearDown() {
    cache= null;
  }

  @Test
  public void testClear() {
    assertEquals(4, cache.size());
    cache.clear();
    assertEquals(0, cache.size());
  }

  @Test
  public void testMaxSize() {
    assertEquals(4, cache.getMaxSize());
    cache.put("5", "e");
    assertEquals(4, cache.getMaxSize());
  }

  @Test
  public void testRemoveExpired() throws Exception {
    Thread.sleep(500);
    cache.removeExpired();
    assertEquals(0, cache.size());
  }

  @Test
  public void testPut() throws Exception {
    cache.put("5", "e");
    Object val= cache.get("5");
    assertEquals("e", val);
    assertNull(cache.get("1"));
    Thread.sleep(200);
    cache.put("6", "f");
    Thread.sleep(300);
    cache.put("7", "g");
    assertEquals(2, cache.size());
  }

  @Test
  public void testGet() {
    assertEquals("b", cache.get("2"));
    assertNull(cache.get("zz"));
  }

  @Test
  public void testRemove() {
    assertEquals("a", cache.remove("1"));
    assertNull(cache.get("a"));
  }

}
