package com.tek271.memoize.cache;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tek271.memoize.cache.MethodArgs.excludeArgsByIndex;
import static org.junit.jupiter.api.Assertions.*;

class MethodArgsTest {

  @Test
  void excludeArgsByIndex_returnsEmptyListIfNoArgs() {
    Object[] args = {};
    int[] excludedParameters = {0, 1};
    assertTrue(excludeArgsByIndex(null, null).isEmpty());
    assertTrue(excludeArgsByIndex(null, excludedParameters).isEmpty());
    assertTrue(excludeArgsByIndex(args, excludedParameters).isEmpty());
  }


  @Test
  void excludeArgsByIndex_returnsAllArgsAsListIfNoExclusion() {
    Object[] args = {42, "foo", "bar"};
    int[] excludedParameters = {};
    List<Object> expected = Arrays.asList(args);
    assertEquals(expected, excludeArgsByIndex(args, excludedParameters));
    assertEquals(expected, excludeArgsByIndex(args, null));
  }

  @Test
  void excludeArgsByIndex_returnsEmptyListIfAllParamsAreExcluded() {
    Object[] args = {42, "foo", "bar"};
    int[] excludedParameters = {0, 1, 2};
    assertTrue(excludeArgsByIndex(args, excludedParameters).isEmpty());
  }

  @Test
  void excludeArgsByIndex_returnsNotExcludedParams() {
    Object[] args = {42, "foo", "bar"};
    int[] excludedParameters = {0, 1};
    List<Object> expected = List.of("bar");
    List<Object> actual = excludeArgsByIndex(args, excludedParameters);
    assertEquals(expected, actual);
  }

}