package com.tek271.memoize;

import com.tek271.memoize.utils.ReflectionTools;
import com.tek271.memoize.utils.Utils;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

import static com.tek271.memoize.utils.ByteBuddyUtils.getValueOfMemoizedMethod;
import static com.tek271.memoize.utils.ByteBuddyUtils.logInterception;

public class DecoratorInterceptor {
  private static final boolean IS_LOG_INTERCEPTOR = false;

  @RuntimeType
  public static Object intercept(@This Object proxy,
                                 @Origin Method originalMethod,
                                 @AllArguments Object[] args,
                                 @SuperMethod Method superMethod) {
    if (IS_LOG_INTERCEPTOR) {
      logInterception(proxy, originalMethod, args, superMethod);
    }

    if (Utils.isMemoized(originalMethod)) {
      return getValueOfMemoizedMethod(proxy, originalMethod, args, superMethod);
    }

    Object objectToDecorate = RememberFactory.getObjectToDecorate(proxy);
    return ReflectionTools.invokeMethod(objectToDecorate, originalMethod, args);
  }

}
