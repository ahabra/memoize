package com.tek271.memoize.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionTools {

  public static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  public static <T> T newInstance(Class<T> targetClass) {
    Constructor<T> constructor = getConstructor(targetClass);
    return newInstance(constructor);
  }

  public static <T> T newInstance(Constructor<T> constructor) {
    try {
      return constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> Constructor<T> getConstructor(Class<T> targetClass) {
    try {
      return targetClass.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object invokeMethod(Object target, Method method, Object... args) {
    try {
      return method.invoke(target, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }


}
