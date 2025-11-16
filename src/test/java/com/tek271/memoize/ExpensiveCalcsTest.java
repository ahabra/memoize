package com.tek271.memoize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tek271.memoize.ExpensiveCalcs.*;
import static org.junit.jupiter.api.Assertions.*;

public class ExpensiveCalcsTest {

  private static ExpensiveCalcs getProxiedExpensiveCalcs() {
    return RememberFactory.createProxy(ExpensiveCalcs.class);
  }

  @BeforeEach
  public void beforeEach() {
    clearCountersAndCache();
  }

  @Test
  public void testVoidNoParams() {
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();
    ec.voidIsNotMemoized();
    assertEquals(1, COUNTERS.voidIsNotMemoized);
  }

  @Test
  public void testNoAnnotation() throws Exception {
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();
    long t1 = ec.noAnnotation();
    long t2 = ec.noAnnotation();
    assertEquals(2, COUNTERS.noAnnotation);
    assertNotEquals(t1, t2);
  }

  @Test
  public void testNoParams() throws Exception {
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();
    long t1 = ec.noParams();
    long t2 = ec.noParams();
    long t3 = ec.noParams();
    assertEquals(1, COUNTERS.noParams);
    assertEquals(t1, t2);
    assertEquals(t1, t3);
  }

  @Test
  public void testWithParams() throws Exception {
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();
    String s1 = ec.withParams("abdul");
    assertEquals("ABDUL", s1);

    String s2 = ec.withParams("abdul");
    assertEquals("ABDUL", s2);

    assertEquals(1, COUNTERS.withParams);

    String s3 = ec.withParams("java");
    assertEquals("JAVA", s3);
    assertEquals(2, COUNTERS.withParams);
  }

  @Test
  public void testGetSalary() {
    // the annotation param at index 0 is excluded in ExpensiveCalcs.getSalary()
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();

    // first use new lookups
    ec.getSalary("fake db connection1", "abdul");
    int s2 = ec.getSalary("fake db connection2", "tom");
    ec.getSalary("fake db connection3", "jeff");
    ec.getSalary("fake db connection4", "scott");

    // now use items that should have been cached
    int s5 = ec.getSalary("fake db connection5", "tom");
    ec.getSalary("fake db connection6", "abdul");

    assertEquals(s2, s5);
    assertEquals(4, COUNTERS.getSalary);
  }

  @Test
  public void testCacheMaxSize() {
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();

    // first use new lookups, the annotation's maxSize=2
    ec.limitedCacheSize(1);
    assertEquals(4, ec.limitedCacheSize(2));

    // now exceed cache size, which will cause 1 & 2 to be flushed
    ec.limitedCacheSize(3);
    ec.limitedCacheSize(4);

    // if cache size limitation was not enforced, this should be retreived from cache
    // and the LOG.size would have been 4 instead of 5
    ec.limitedCacheSize(1);
    assertEquals(5, COUNTERS.limitedCacheSize);
  }

  @Test
  public void testDecorate() throws Exception {
    ExpensiveCalcs ec = new ExpensiveCalcs();
    ExpensiveCalcs decorated = RememberFactory.decorate(ec);
    assertNotSame(ec, decorated);

    String s1 = decorated.withParams("abdul");
    assertEquals("ABDUL", s1);

    String s2 = decorated.withParams("abdul");
    assertEquals("ABDUL", s2);
    assertEquals(1, COUNTERS.withParams);

    String s3 = decorated.withParams("java");
    assertEquals("JAVA", s3);
    assertEquals(2, COUNTERS.withParams);
  }


  @Test
  public void testDecorateNullShouldThrowNPE() {
    assertThrows(NullPointerException.class, () -> RememberFactory.decorate(null));
  }

  @Test
  void slowCallIsCached() throws Exception {
    ExpensiveCalcs ec = getProxiedExpensiveCalcs();
    long t0 = System.currentTimeMillis();
    int v1 = ec.slowCall(10);
    long t1 = System.currentTimeMillis();
    int v2 = ec.slowCall(10);
    long t2 = System.currentTimeMillis();

    assertEquals(v1, v2);

    long d1 = t1 - t0;
    long d2 = t2 - t1;

    assertTrue(d1> 450, "d1=" + d1);
    assertTrue(d2 < 20, "d2=" + d2);
  }
}
