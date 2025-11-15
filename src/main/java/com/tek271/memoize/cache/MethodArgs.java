package com.tek271.memoize.cache;

import com.tek271.memoize.Remember;
import com.tek271.memoize.utils.ArrayTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MethodArgs {
  /** timeStamp intentionally is NOT used in equals() and hashCode() */
  private long timeStamp;
  private final Object[] args;

  // TODO handle excluded args
  public MethodArgs(Object[] args, Remember remember) {
    this.timeStamp = System.currentTimeMillis();
    this.args = args;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    MethodArgs that = (MethodArgs) other;
    return Objects.deepEquals(args, that.args);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(args);
  }

  @Override
  public String toString() {
    return "MethodArgs{ timeStamp=" + timeStamp + ", "  + Arrays.toString(args) + '}';
  }

  static List<Object> excludeArgs(Object[] args, Remember remember) {
    List<Object> relevantArgs = excludeArgsByIndex(args, remember.excludedParametersIndex());

    return relevantArgs;
  }

  static List<Object> excludeArgsByIndex(Object[] args, int[] excludedParametersIndex) {
    if (ArrayTools.isEmpty(args)) return new ArrayList<>();
    if (ArrayTools.isEmpty(excludedParametersIndex)) return Arrays.asList(args);

    List<Object> result = new ArrayList<>();
    for (int i = 0; i < args.length; i++) {
      if (!ArrayTools.isContain(excludedParametersIndex, i)) {
        result.add(args[i]);
      }
    }
    return result;
  }

}
