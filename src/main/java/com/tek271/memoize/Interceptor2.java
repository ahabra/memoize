package com.tek271.memoize;

import com.tek271.memoize.utils.ReflectionTools;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public class Interceptor2 {
  @RuntimeType
  public static Object intercept(@This Object self,
                                 @Origin Method method,
                                 @AllArguments Object[] args,
                                 @SuperMethod Method superMethod) {
    System.out.println("intercept:");
    System.out.println("  originalMethod=" + method);
    System.out.println("  superMethod   =" + superMethod);
    return ReflectionTools.invokeMethod(self, superMethod, args);
  }

}
