package com.tek271.memoize.cache;

import java.util.Arrays;
import java.util.Objects;

public class MethodArgs {
  /** timeStamp intentionally is NOT used in equals() and hashCode() */
  private long timeStamp;
  private final Object[] args;

  // TODO handle excluded args
  public MethodArgs(Object[] args) {
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

  public boolean isExpired(long timeToLiveMillis) {
    return System.currentTimeMillis() - timeStamp > timeToLiveMillis;
  }

  public void refreshTimeStamp() {
    this.timeStamp = System.currentTimeMillis();
  }


}
