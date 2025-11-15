package com.tek271.memoize.utils;


import com.tek271.memoize.Remember;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

  public static Field getField(Object target, String fieldName) {
    if (target == null) {
      throw new NullPointerException("target is null");
    }
    if (fieldName == null) {
      throw new NullPointerException("fieldName is null");
    }
    try {
      return target.getClass().getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setFieldValue(Object target, Field field, Object value) {
    try {
      field.set(target, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setFieldValue(Object target, String fieldName, Object value) {
    if (target == null) {
      throw new NullPointerException("target is null");
    }
    Field field = getField(target, fieldName);
    setFieldValue(target, field, value);
  }

  public static <T> T getFieldValue(Object target, String fieldName) {
    Field field = getField(target, fieldName);
    try {
      //noinspection unchecked
      return (T) field.get(target);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }


  /** Check if given method is not void and has @Remember annotation */
  public static boolean isMemoized(Method method) {
    // if method is void then no memoize
    if (method.getReturnType() == Void.TYPE) return false;

    Remember ann = method.getAnnotation(Remember.class);
    return ann != null;
  }

  public static List<Method> findListOfMemoizedMethods(Class<?> targetClass) {
    Method[] methods = targetClass.getDeclaredMethods();
    List<Method> result = new ArrayList<>();
    for (Method method : methods) {
      if (isMemoized(method)) {
        result.add(method);
      }
    }
    return result;
  }

}
