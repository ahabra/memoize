package com.tek271.memoize;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

import static com.tek271.memoize.utils.ByteBuddyUtils.getValueOfMemoizedMethod;
import static com.tek271.memoize.utils.ByteBuddyUtils.logInterception;

public class Interceptor {
  private static final boolean IS_LOG_INTERCEPTOR = false;

  /**
   * @param proxy The proxy object created by ByteBuddy
   * @param originalMethod The original method that is getting proxied
   * @param args arguments passed to the method
   * @param superMethod The new proxy method created by ByteBuddy
   * @return The result of calling the superMethod or its cached value
   */
  @RuntimeType
  public static Object intercept(@This Object proxy,
                                 @Origin Method originalMethod,
                                 @AllArguments Object[] args,
                                 @SuperMethod Method superMethod) {
    if (IS_LOG_INTERCEPTOR) {
      logInterception(proxy, originalMethod, args, superMethod);
    }

    return getValueOfMemoizedMethod(proxy, originalMethod, args, superMethod);
  }


}
