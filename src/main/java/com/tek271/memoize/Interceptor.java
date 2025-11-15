package com.tek271.memoize;

import com.tek271.memoize.utils.ReflectionTools;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

import static com.tek271.memoize.utils.ByteBuddyUtils.getValueOfMemoizedMethod;
import static com.tek271.memoize.utils.ByteBuddyUtils.logInterception;
import static com.tek271.memoize.utils.ReflectionTools.invokeMethod;
import static com.tek271.memoize.utils.ReflectionTools.isMemoized;

public class Interceptor {
  private static final boolean IS_LOG_INTERCEPTOR = false;

  /**
   * Intercept calls to methods given base type
   */
  public static class TypeInterceptor {
    /**
     * @param proxy The proxy object created by ByteBuddy
     * @param originalMethod The original method that is getting proxied
     * @param superMethod The new proxy method created by ByteBuddy
     * @param args arguments passed to the method
     * @return The result of calling the superMethod or its cached value
     */
    @RuntimeType
    public static Object intercept(@This Object proxy,
                                   @Origin Method originalMethod,
                                   @SuperMethod Method superMethod,
                                   @AllArguments Object[] args) {
      if (IS_LOG_INTERCEPTOR) {
        logInterception(proxy, originalMethod, args, superMethod);
      }

      return getValueOfMemoizedMethod(proxy, originalMethod, args, superMethod);
    }
  }

  /** Intercept calls to methods on a given object */
  public static class DecoratorInterceptor {
    /**
     * @param proxy The proxy object created by ByteBuddy
     * @param originalMethod The original method that is getting proxied
     * @param superMethod The new proxy method created by ByteBuddy
     * @param args arguments passed to the method
     * @return The result of calling the superMethod or its cached value
     */
    @RuntimeType
    public static Object intercept(@This Object proxy,
                                   @Origin Method originalMethod,
                                   @SuperMethod Method superMethod,
                                   @AllArguments Object[] args) {
      if (IS_LOG_INTERCEPTOR) {
        logInterception(proxy, originalMethod, args, superMethod);
      }

      if (isMemoized(originalMethod)) {
        return getValueOfMemoizedMethod(proxy, originalMethod, args, superMethod);
      }

      Object objectToDecorate = RememberFactory.getObjectToDecorate(proxy);
      return invokeMethod(objectToDecorate, originalMethod, args);
    }

  }



}
