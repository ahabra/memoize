package com.tek271.memoize;

import com.tek271.memoize.cache.AllCache;
import com.tek271.memoize.utils.ReflectionTools;
import com.tek271.memoize.utils.Utils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static com.tek271.memoize.utils.ReflectionTools.getClassLoader;

public class RememberFactory {
  // TODO: if multiple proxies of the same class are created, do we cache the methods
  // TODO: for each instance, or for all?

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

  static final String OBJECT_TO_DECORATE_ADDED_FIELD = "__objectToDecorate__";

  public static <T> T decorate(T objectToDecorate) {
    if (objectToDecorate == null) {
      throw new NullPointerException("Memoizer cannot decorate a null object");
    }
    Class<?> targetClass = objectToDecorate.getClass();
    List<Method> methods = Utils.findListOfMemoizedMethods(targetClass);
    if (methods.isEmpty()) {
      return objectToDecorate;
    }

    DynamicType.Builder<?> subclass = new ByteBuddy().subclass(targetClass);
    subclass = addFieldForObjectToDecorate(subclass, targetClass);
    subclass = subclass.method(ElementMatchers.any())
        .intercept(MethodDelegation.to(DecoratorInterceptor.class));

    Class<?> cls = subclass.make()
        .load(getClassLoader())
        .getLoaded();

    //noinspection unchecked
    T result = (T) ReflectionTools.newInstance(cls);
    setObjectToDecorate(result, objectToDecorate);
    return result;
  }

  private static DynamicType.Builder<?> addFieldForObjectToDecorate(DynamicType.Builder<?> builder, Type fieldType) {
    return builder.defineField(OBJECT_TO_DECORATE_ADDED_FIELD, fieldType, Visibility.PUBLIC);
  }

  private static <T> void setObjectToDecorate(T result, T objectToDecorate) {
    ReflectionTools.setFieldValue(result, OBJECT_TO_DECORATE_ADDED_FIELD, objectToDecorate);
  }

  static <T> T getObjectToDecorate(T object) {
    return ReflectionTools.getFieldValue(object, OBJECT_TO_DECORATE_ADDED_FIELD);
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
