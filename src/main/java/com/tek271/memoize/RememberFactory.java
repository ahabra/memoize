package com.tek271.memoize;

import com.tek271.memoize.cache.AllCache;
import com.tek271.memoize.utils.ReflectionTools;
import com.tek271.memoize.utils.Utils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.List;

import static com.tek271.memoize.utils.ReflectionTools.getClassLoader;

public class RememberFactory {
  // TODO: if a multiple proxies of the same class are created, do cache the methods
  // TODO: for each instance, or for all?

  // TODO: decorate an existing object


  public static <T> T createProxy(Class<T> targetClass) {
    List<Method> methods = Utils.findListOfMemoizedMethods(targetClass);
    if (methods.isEmpty()) {
      return ReflectionTools.newInstance(targetClass);
    }
    DynamicType.Builder<T> subclass = new ByteBuddy().subclass(targetClass);

    for (Method method : methods) {
      subclass = subclass
          .method(ElementMatchers.is(method))
          .intercept(MethodDelegation.to(Interceptor.class));
    }


    Class<? extends T> cls = subclass.make()
        .load(getClassLoader())
        .getLoaded();

    return ReflectionTools.newInstance(cls);
  }

  /**
   * Clear the cache in AllCache.single().
   * Useful for unit testing.
   * @since 1.1
   */
  public static void clearCache() {
    AllCache.single().clear();
  }

}
