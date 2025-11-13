package com.tek271.memoize.utils;

import org.junit.jupiter.api.Test;

import static com.tek271.memoize.utils.ReflectionTools.getFieldValue;
import static com.tek271.memoize.utils.ReflectionTools.setFieldValue;
import static org.junit.jupiter.api.Assertions.*;

class ReflectionToolsTest {
  int field1 = 1;

  @Test
  void testSetFieldValue() {
    setFieldValue(this, "field1", 10);
    assertEquals(10, field1);
  }

  @Test
  void testGetFieldValue() {
    assertEquals(1, (int) getFieldValue(this, "field1"));
    field1 = 5;
    assertEquals(5, (int) getFieldValue(this, "field1"));
  }

}