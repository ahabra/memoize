package com.tek271.memoize.utils;

import java.util.Objects;

public class UtilsArrays {
  
  /** Check of the array is null or zero-length */
  public static boolean isArrayEmpty(Object[] array) {
    return array==null || array.length==0;
  }

  /** Check of the array is null or zero-length */
  public static boolean isArrayEmpty(int[] array) {
    return array==null || array.length==0;
  }

  /** Find the index of target in array, -1 if not found */ 
  public static int indexOfInt(int[] array, int target) {
    if (array==null) return -1;

    for (int i=0,n=array.length; i<n; i++) {
      if (array[i]==target) return i;
    }
    return -1;
  }
  
  /** Check if the given array contains target */  
  public static boolean isContain(int[]array, int target) {
    return indexOfInt(array, target) >= 0;
  }

  /** Find the index of target in array, -1 if not found */
  public static <T> int indexOf(final T[] array, T target) {
    if (array==null) return -1;
    for (int i=0,n=array.length; i<n; i++) {
      if (Objects.equals(array[i], target)) {
        return i;
      }
    }
    return -1;
  }

}
