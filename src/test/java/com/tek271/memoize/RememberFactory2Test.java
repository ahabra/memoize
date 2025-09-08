package com.tek271.memoize;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RememberFactory2Test {

  public static class MemoizedClass {

    @Remember
    public int add(int a, int b) {
      return a + b;
    }

  }

  @Test
  void testMemoization() {
    MemoizedClass proxy = RememberFactory2.createProxy(MemoizedClass.class, null);

    int result = proxy.add(1,2);

    assertEquals(3 ,result);
  }
}