package com.tek271.memoize;

import com.tek271.memoize.cache.AllCache;
import com.tek271.memoize.utils.ByteBuddyUtils;
import com.tek271.memoize.utils.ReflectionTools;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.List;

import static com.tek271.memoize.utils.ByteBuddyUtils.*;
import static com.tek271.memoize.utils.ReflectionTools.findListOfMemoizedMethods;

public class RememberFactory {

  /**
   * Create a caching (memoizing) proxy for an object that contains methods with <code>@Remember</code> annotation.
   * Calling these methods will cause them to be cached.
   * @param <T> The type of the object
   * @param targetClass The class to create a proxy for. The class must provide a
   * parameter-less constructor.
   * @return An object of the type <code>targetClass</code> where methods that are
   * annotated by <code>Remember</code> will be cached.
   * @since 1.0
   */
  public static <T> T createProxy(Class<T> targetClass) {
    if (targetClass == null) {
      throw new NullPointerException("Memoizer cannot proxy a null object");
    }
    List<Method> methods = findListOfMemoizedMethods(targetClass);
    if (methods.isEmpty()) {
      return ReflectionTools.newInstance(targetClass);
    }
    DynamicType.Builder<T> subclass = subclass(targetClass);

    for (Method method : methods) {
      subclass = subclass
          .method(ElementMatchers.is(method))
          .intercept(MethodDelegation.to(Interceptor.TypeInterceptor.class));
    }

    return createInstance(subclass);
  }


  /** A field name that is unlikely to be used in app code */
  private static final String OBJECT_TO_DECORATE_FIELD = "__objectToDecorate__փ_ϣ_ሥ_ਟ_ꦒ_";

  /**
   * Memoize methods of an existing object. Allows integrating with other frameworks like
   * <i>Spring</i>.
   * @param <T> The type of the object
   * @param objectToDecorate The object should have at least one Remember annotation
   * @return an object of type T which decorates <code>objectToDecorate</code>. Method
   * invocations will be routed to the objectToDecorate after checking for memoization.
   * @since 1.1
   */
  public static <T> T decorate(T objectToDecorate) {
    if (objectToDecorate == null) {
      throw new NullPointerException("Memoizer cannot decorate a null object");
    }
    Class<?> targetClass = objectToDecorate.getClass();
    List<Method> methods = findListOfMemoizedMethods(targetClass);
    if (methods.isEmpty()) {
      return objectToDecorate;
    }

    DynamicType.Builder<?> subclass = subclass(targetClass);
    subclass = ByteBuddyUtils.addField(subclass, OBJECT_TO_DECORATE_FIELD, targetClass);
    subclass = subclass.method(ElementMatchers.any())
        .intercept(MethodDelegation.to(Interceptor.DecoratorInterceptor.class));


    //noinspection unchecked
    T result = (T) createInstance(subclass);
    setObjectToDecorate(result, objectToDecorate);
    return result;
  }

  private static <T> void setObjectToDecorate(T result, T objectToDecorate) {
    ReflectionTools.setFieldValue(result, OBJECT_TO_DECORATE_FIELD, objectToDecorate);
  }

  static <T> T getObjectToDecorate(T object) {
    return ReflectionTools.getFieldValue(object, OBJECT_TO_DECORATE_FIELD);
  }

  /**
   * Clear the cache in AllCache.single().
   * Useful for unit testing.
   *
   * @since 1.1
   */
  public static void clearCache() {
    AllCache.single().clear();
  }

}
