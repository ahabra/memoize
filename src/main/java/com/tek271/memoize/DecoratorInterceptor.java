package com.tek271.memoize;

import com.tek271.memoize.cache.AllCache;
import com.tek271.memoize.utils.BoundedTimedCache;
import com.tek271.memoize.utils.ReflectionTools;
import com.tek271.memoize.utils.Utils;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public class DecoratorInterceptor {

  @RuntimeType
  public static Object intercept(@This Object self,
                                 @Origin Method originalMethod,
                                 @AllArguments Object[] args,
                                 @SuperMethod Method superMethod) {
    if (Utils.isMemoized(originalMethod)) {
      return getValueOfMemoizedMethod(self, originalMethod, args, superMethod);
    }

    Object objectToDecorate = RememberFactory.getObjectToDecorate(self);
    return ReflectionTools.invokeMethod(objectToDecorate, originalMethod, args);
  }

  private static Object getValueOfMemoizedMethod(Object self,
                                                 Method originalMethod,
                                                 Object[] args,
                                                 Method superMethod) {
    AllCache allCache = AllCache.single();
    BoundedTimedCache.CacheValue<?> cachedValue = allCache.getCachedValue(originalMethod, args);
    if (cachedValue != null) {
      return cachedValue.value;
    }

    Object result = ReflectionTools.invokeMethod(self, superMethod, args);
    allCache.put(originalMethod, args, result);
    return result;
  }
}
