package com.tek271.memoize.cache;

import com.tek271.memoize.Remember;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AllCacheTest {

  AllCache sut;

  @Remember
  int add(int a, int b) {
    return a + b;
  }

  Method addMethod() {
    try {
      return this.getClass().getDeclaredMethod("add", int.class, int.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testCache() {
    Method method = addMethod();
    Object[] args1 = {1, 2};
    Object[] args2 = {10, 20};

    sut = new AllCache();
    assertNull(sut.getCachedValue(method, args1));

    sut.put(method, args1, 3);
    sut.put(method, args2, 30);

    assertEquals(3, sut.getCachedValue(method, args1).value);
    assertEquals(30, sut.getCachedValue(method, args2).value);
  }

  @Test
  void singletonIsAccessible() {
    AllCache single = AllCache.single();
    assertNotNull(single);
    assertSame(single, AllCache.single());
  }

}