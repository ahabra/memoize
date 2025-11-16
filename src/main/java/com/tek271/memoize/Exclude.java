package com.tek271.memoize;

import java.lang.annotation.*;

/**
 * Method parameter that should not be used as a part of the cache's key
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Exclude {
}
