package com.tek271.memoize.cache;

import java.util.*;

public class MethodArgs {
  /** timeStamp intentionally is NOT used in equals() and hashCode() */
  private final long timeStamp;
  private final List<Object> args = new ArrayList<>();

  public MethodArgs(Object[] args, Set<Integer> indexesOfExcludedParameters) {
    this.timeStamp = System.currentTimeMillis();
    for (int i = 0; i < args.length; i++) {
      if (!indexesOfExcludedParameters.contains(i)) {
        this.args.add(args[i]);
      }
    }
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
    return args.hashCode();
  }

  @Override
  public String toString() {
    return "MethodArgs{ timeStamp=" + timeStamp + ", "  + args + '}';
  }


}
