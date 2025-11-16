package com.tek271.memoize;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExpensiveCalcs {
  public static class Counters {
    int voidIsNotMemoized;
    int noAnnotation;
    int noParams;
    int withParams;
    int getSalary;
    int limitedCacheSize;
    int slowCall;

    void clear() {
      voidIsNotMemoized = 0;
      noAnnotation = 0;
      noParams = 0;
      withParams = 0;
      getSalary = 0;
      limitedCacheSize = 0;
      slowCall = 0;
    }
  }

  final static Counters COUNTERS = new Counters();

  public static void clearCountersAndCache() {
    COUNTERS.clear();
    RememberFactory.clearCache();
  }

  @Remember
  public void voidIsNotMemoized() {
    COUNTERS.voidIsNotMemoized++;
  }

  public long noAnnotation() throws Exception {
    Thread.sleep(10);
    COUNTERS.noAnnotation++;
    return System.currentTimeMillis();
  }

  @Remember
  public long noParams() throws Exception {
    COUNTERS.noParams++;
    return System.currentTimeMillis();
  }

  @Remember
  public String withParams(String param1) throws Exception {
    COUNTERS.withParams++;
    return param1.toUpperCase();
  }

  private static final Map<String, Integer> SALARIES = Map.of(
      "abdul", 100,
      "doug", 200,
      "scott", 300,
      "tom", 400,
      "jeff", 500
  );

  private static int lookupSalary(String name) {
    return SALARIES.getOrDefault(name, -1);
  }

  @Remember
  public int getSalary(@Exclude Object dbConnection, String name) {
    COUNTERS.getSalary++;
    return lookupSalary(name);
  }

  @Remember(
      maxSize = 2,
      timeToLive = 60,
      timeUnit = TimeUnit.SECONDS
  )
  public int limitedCacheSize(int a) {
    COUNTERS.limitedCacheSize++;
    return a * a;
  }

  @Remember
  public int slowCall(int a) throws Exception {
    COUNTERS.slowCall++;
    Thread.sleep(500);
    return a * a;
  }


}
