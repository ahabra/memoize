package com.tek271.memoize.cache;

import com.tek271.memoize.utils.BoundedTimedCache.CacheValue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AllCache {
  private enum Singleton {
    INSTANCE;
    final AllCache allCache = new AllCache();
  }

  private final Map<Method, SingleMethodCache<Object>> map;

  AllCache() {
    this.map = new HashMap<>();
  }

  public static AllCache single() {
    return Singleton.INSTANCE.allCache;
  }

  public CacheValue<?> getCachedValue(Method method, Object[] args) {
    SingleMethodCache<?> singleMethodCache = map.get(method);
    if (singleMethodCache == null) {
      return null;
    }
    return singleMethodCache.get(args);
  }

  public void put(Method method, Object[] args, Object value) {
    SingleMethodCache<Object> singleMethodCache = map.get(method);
    if (singleMethodCache == null) {
      singleMethodCache = new SingleMethodCache<>(method);
      map.put(method, singleMethodCache);
    }
    singleMethodCache.put(args, value);
  }

  public void clear() {
    map.values().forEach(SingleMethodCache::clear);
    map.clear();
  }

}
