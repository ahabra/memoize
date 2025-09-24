package com.tek271.memoize;

import com.tek271.memoize.utils.ReflectionTools;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Interceptor2 {
  @RuntimeType
  public static Object intercept(@This Object self,
                                 @Origin Method originalMethod,
                                 @AllArguments Object[] args,
                                 @SuperMethod Method superMethod) {
    System.out.println("intercept:");
    System.out.println("  self          = " + self);
    System.out.println("  originalMethod= " + originalMethod);
    System.out.println("  superMethod   = " + superMethod);
    System.out.println("  args          = " + Arrays.toString(args));


    return ReflectionTools.invokeMethod(self, superMethod, args);
  }

}
