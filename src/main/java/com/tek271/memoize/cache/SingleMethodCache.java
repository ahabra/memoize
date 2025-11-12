package com.tek271.memoize.cache;

import com.tek271.memoize.Remember;
import com.tek271.memoize.utils.BoundedTimedCache;
import com.tek271.memoize.utils.BoundedTimedCache.CacheValue;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class SingleMethodCache<V> {
  private final BoundedTimedCache<MethodArgs, V> cache;

  public SingleMethodCache(Remember remember) {
    this.cache = createCache(remember);
  }

  public SingleMethodCache(Method method) {
    Remember remember = method.getDeclaredAnnotation(Remember.class);
    if (remember == null) {
      throw new IllegalArgumentException("Method " + method + " must be annotated with @Remember");
    }
    this.cache = createCache(remember);
  }

  private BoundedTimedCache<MethodArgs, V> createCache(Remember remember) {
    if (remember == null) {
      throw new IllegalArgumentException("Remember annotation cannot be null");
    }
    int maxSize = remember.maxSize();
    long timeToLive = remember.timeToLive();
    TimeUnit timeUnit = remember.timeUnit();
    return new BoundedTimedCache<>(maxSize, timeToLive, timeUnit);
  }

  public CacheValue<V> get(MethodArgs methodArgs) {
    return cache.get(methodArgs);
  }

  public CacheValue<V> get(Object[] args) {
    return get(new MethodArgs(args));
  }

  public void put(MethodArgs methodArgs, V value) {
    cache.put(methodArgs, value);
  }

  public void put(Object[] args, V value) {
    put(new MethodArgs(args), value);
  }

  public void clear() {
    cache.clear();
  }


}
