package com.tek271.memoize.utils;


import com.tek271.memoize.Exclude;
import com.tek271.memoize.Remember;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectionTools {

  public static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  public static <T> T newInstance(Class<T> targetClass) {
    Constructor<T> constructor = getConstructor(targetClass);
    return newInstance(constructor);
  }

  private static <T> T newInstance(Constructor<T> constructor) {
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

  /**
   * Check if given method is not void and has @Remember annotation.
   * Note that if a method has no parameters, it can still be memoized.
   **/
  public static boolean isMemoizable(Method method) {
    // if method is void then no memoize
    if (method == null || method.getReturnType() == Void.TYPE) return false;

    Remember remember = method.getAnnotation(Remember.class);
    return remember != null;
  }

  public static Set<Integer> findIndexesOfExcludedParameters(Method method) {
    Set<Integer> result = new HashSet<>();
    Parameter[] parameters = method.getParameters();

    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].getDeclaredAnnotation(Exclude.class) != null) {
        result.add(i);
      }
    }
    return result;
  }

  public static List<Method> findListOfMemoizedMethods(Class<?> targetClass) {
    Method[] methods = targetClass.getDeclaredMethods();
    List<Method> result = new ArrayList<>();
    for (Method method : methods) {
      if (isMemoizable(method)) {
        result.add(method);
      }
    }
    return result;
  }

}
