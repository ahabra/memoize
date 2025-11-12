package com.tek271.memoize;

import com.tek271.memoize.cache.AllCache;
import com.tek271.memoize.utils.BoundedTimedCache.CacheValue;
import com.tek271.memoize.utils.ReflectionTools;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Interceptor {
  private static final boolean IS_LOG_INTERCEPTOR = false;

  @RuntimeType
  public static Object intercept(@This Object self,
                                 @Origin Method originalMethod,
                                 @AllArguments Object[] args,
                                 @SuperMethod Method superMethod) {
    if (IS_LOG_INTERCEPTOR) {
      logInterception(self, originalMethod, args, superMethod);
    }

    AllCache allCache = AllCache.single();
    CacheValue<?> cachedValue = allCache.getCachedValue(originalMethod, args);
    if (cachedValue != null) {
      return cachedValue.value;
    }

    Object result = ReflectionTools.invokeMethod(self, superMethod, args);
    allCache.put(originalMethod, args, result);
    return result;
  }

  private static void logInterception(Object self,
                               Method originalMethod,
                               Object[] args,
                               Method superMethod) {
    System.out.println("intercept:");
    System.out.println("  self          = " + self);
    System.out.println("  originalMethod= " + originalMethod);
    System.out.println("  superMethod   = " + superMethod);
    System.out.println("  args          = " + Arrays.toString(args));
  }

}
