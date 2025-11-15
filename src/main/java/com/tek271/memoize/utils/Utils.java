/*
Technology Exponent (Tek271) Memoizer
Copyright (C) 2007  Abdul Habra
www.tek271.com

This file is part of Tek271 Memoizer

Tek271 Memoizer is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; version 2.

Tek271 Memoizer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Tek271 Memoizer; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

You can contact the author at ahabra at yahoo.com
*/

package com.tek271.memoize.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Some utility static methods.
 * @author Abdul Habra
 * @version 1.1
 */
public class Utils {

  /** Get the arguments that will be used as part of the key to this method's cache */  
  public static List<Object> getRelevantArguments(Object[] args,
                                                  int[] excludedParameters) {
    if (UtilsArrays.isArrayEmpty(args)) return new ArrayList<>();
    if (UtilsArrays.isArrayEmpty(excludedParameters)) return Arrays.asList(args);
    
    List<Object> relevantArgs= new ArrayList<>();
    for (int i = 0, n = args.length; i < n; i++) {
      if (! UtilsArrays.isContain(excludedParameters, i)) {
        relevantArgs.add(args[i]);
      }
    }
    return relevantArgs;
  }
  
  
}
