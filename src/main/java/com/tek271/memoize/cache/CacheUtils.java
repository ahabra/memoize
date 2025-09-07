package com.tek271.memoize.cache;

import com.tek271.memoize.Remember;

import java.lang.reflect.Method;

public class CacheUtils {

  public  static ICacheFactory getCacheFactory(ICacheFactory cacheFactory) {
    if (cacheFactory!=null) return cacheFactory;
    return DefaultCacheFactory.getInstance();
  }

  /**
   * Clear the given cache. When cacheFactory is null, then the DefaultCacheFactory
   * is cleared. Useful for unit testing.
   * @param cacheFactory The cache factory to clear
   * @since 1.1
   */
  public static void clearCache(ICacheFactory cacheFactory) {
    if (cacheFactory==null) {
      cacheFactory= DefaultCacheFactory.getInstance();
    }
    cacheFactory.clear();
  }

  /**
   * Clear the cache in the DefaultCacheFactory.
   * Useful for unit testing.
   * @since 1.1
   */
  public static void clearCache() {
    clearCache(null);
  }

  /** There is a cache for each memoized method ! */
  public static ICache getCache(ICacheFactory cacheFactory, Method method, Remember remember) {
    String methodDesc= method.toGenericString();
    return cacheFactory.getCache(methodDesc, remember.maxSize(),
        remember.timeToLive(), remember.timeUnit());
  }

}
