package com.tek271.memoize;

import com.tek271.memoize.utils.ReflectionTools;
import com.tek271.memoize.utils.Utils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.List;

public class RememberFactory2 {
  // TODO: if a multiple proxies of the same class are created, do cache the methods
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
          .intercept(MethodDelegation.to(Interceptor2.class));
    }


    ClassLoader classLoader = ReflectionTools.getClassLoader();
    Class<? extends T> cls = subclass
        .make()
        .load(classLoader)
        .getLoaded();

    return ReflectionTools.newInstance(cls);
  }


}
