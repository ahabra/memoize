package com.tek271.memoize.cache;

import com.tek271.memoize.Remember;
import com.tek271.memoize.utils.BoundedTimedCache;
import com.tek271.memoize.utils.BoundedTimedCache.CacheValue;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class SingleMethodCache<V> {
  private final BoundedTimedCache<MethodArgs, V> cache;
  private final Remember remember;

  public SingleMethodCache(Method method) {
    this.remember = method.getDeclaredAnnotation(Remember.class);
    if (this.remember == null) {
      throw new IllegalArgumentException("Method " + method + " must be annotated with @Remember");
    }
    this.cache = createCache(this.remember);

  }

  private static <V> BoundedTimedCache<MethodArgs, V> createCache(Remember remember) {
    if (remember == null) {
      throw new IllegalArgumentException("Remember annotation cannot be null");
    }
    int maxSize = remember.maxSize();
    long timeToLive = remember.timeToLive();
    TimeUnit timeUnit = remember.timeUnit();
    return new BoundedTimedCache<>(maxSize, timeToLive, timeUnit);
  }

  private CacheValue<V> get(MethodArgs methodArgs) {
    return cache.get(methodArgs);
  }

  public CacheValue<V> get(Object[] args) {
    return get(new MethodArgs(args, this.remember));
  }

  private void put(MethodArgs methodArgs, V value) {
    cache.put(methodArgs, value);
  }

  public void put(Object[] args, V value) {
    put(new MethodArgs(args, this.remember), value);
  }

  public void clear() {
    cache.clear();
  }


}
