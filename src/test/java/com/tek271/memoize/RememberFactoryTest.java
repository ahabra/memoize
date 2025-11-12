package com.tek271.memoize;

import com.tek271.memoize.cache.AllCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RememberFactoryTest {

  public static class MemoizedClass {
    int addCounter;

    @Remember
    public int add(int a, int b) {
      addCounter++;
      return a + b;
    }

  }

  @BeforeEach
  public void beforeEach() {
    AllCache.single().clear();
  }

  @Test
  void callingMemoizedMethodWithSameParamsCausesSingleInvocation() {
    MemoizedClass proxy = RememberFactory.createProxy(MemoizedClass.class);
    assertEquals(3, proxy.add(1, 2));
    assertEquals(3, proxy.add(1, 2));
    assertEquals(3, proxy.add(1, 2));
    assertEquals(1, proxy.addCounter);
  }

  @Test
  void testMemoization() {
    MemoizedClass proxy = RememberFactory.createProxy(MemoizedClass.class);

    assertEquals(3, proxy.add(1, 2));
    assertEquals(3, proxy.add(1, 2));
    assertEquals(3, proxy.add(1, 2));
    assertEquals(4, proxy.add(2, 2));
    assertEquals(3, proxy.add(1, 2));

    for (int i = 0; i < 10; i++) {
      proxy.add(20, 30);
    }

    assertEquals(3, proxy.addCounter);
  }
}