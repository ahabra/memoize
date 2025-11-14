package com.tek271.memoize.utils;

import com.tek271.memoize.cache.AllCache;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import static com.tek271.memoize.utils.ReflectionTools.getClassLoader;

public class ByteBuddyUtils {

  public static <T> DynamicType.Builder<T> subclass(Class<T> aClass) {
    return new ByteBuddy().subclass(aClass);
  }

  public static <T> T createInstance(DynamicType.Builder<T> subclass) {
    Class<? extends T> cls = subclass.make()
        .load(getClassLoader())
        .getLoaded();
    return ReflectionTools.newInstance(cls);
  }

  public static DynamicType.Builder<?> addField(DynamicType.Builder<?> builder, String fieldName, Type fieldType) {
    return builder.defineField(fieldName, fieldType, Visibility.PUBLIC);
  }


  /**
   * Get the return value of a memoized method, either from the cache or by invoking the method.
   *
   * @param proxy          The proxy object created by ByteBuddy
   * @param originalMethod The original method that is getting proxied
   * @param args           arguments passed to the method
   * @param superMethod    The new proxy method created by ByteBuddy
   * @return The result of calling the superMethod or its cached value
   */
  public static Object getValueOfMemoizedMethod(Object proxy,
                                                Method originalMethod,
                                                Object[] args,
                                                Method superMethod) {
    AllCache allCache = AllCache.single();
    BoundedTimedCache.CacheValue<?> cachedValue = allCache.getCachedValue(originalMethod, args);
    if (cachedValue != null) {
      return cachedValue.value;
    }

    Object result = ReflectionTools.invokeMethod(proxy, superMethod, args);
    allCache.put(originalMethod, args, result);
    return result;
  }

  public static void logInterception(Object proxy,
                                     Method originalMethod,
                                     Object[] args,
                                     Method superMethod) {
    System.out.println("intercept:");
    System.out.println("  proxy          = " + proxy);
    System.out.println("  originalMethod= " + originalMethod);
    System.out.println("  superMethod   = " + superMethod);
    System.out.println("  args          = " + Arrays.toString(args));
  }

}
