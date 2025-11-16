package com.tek271.memoize.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

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

  @Test
  void isMemoizable_returnsFalseIfMethodIsNull() {
    assertFalse(ReflectionTools.isMemoizable(null));
  }

  @SuppressWarnings("unused")
  void voidMethod() {}

  @Test
  void isMemoizable_returnsFalseIfMethodIsVoid() throws NoSuchMethodException {
    Method method = this.getClass().getDeclaredMethod("voidMethod");
    assertFalse(ReflectionTools.isMemoizable(method));
  }

  int intMethod1() {
    return 42;
  }

  @Test
  void isMemoizable_returnsFalseIfMethodIsNotAnnotated() throws NoSuchMethodException {
    Method method = this.getClass().getDeclaredMethod("intMethod1");
    assertFalse(ReflectionTools.isMemoizable(method));
  }


}