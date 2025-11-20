# Java Tek271 Memoizer

A Java open source memoization library to cache the results of slow methods using annotations.

## History of this library
I initially created this library back in 2007 using Java 5 and Ant build, this was before Github 
existed. I hosted the code on my own site, but it was a headache to keep it up-to-date, so by
2009, I stopped updating it. However, I always intended to update it and publish it on Github,
so here it is again.

* Here is the [old code before migrating to github](old/old-1.1.zip)
* Here is the [old programmer's introduction](old/tek271.memoizer.intro.html)

The remainder of this document describes the new and updated version.

## Introduction
If a function produces the same output given the same inputs, and if this function is slow,
it makes sense for the function to cache its outputs to avoid repeated evaluations. This caching
behavior is called [Memoization](https://en.wikipedia.org/wiki/Memoization) 
_(yes, there is no __r__)_.

This library provides a simple mechanism to memoize any Java method using `@Remember` annotation.

## Motivation
Suppose that you have a method which reads some authorization string from database for a given login:

```java
// This is not ideal code, it is meant only to show a concept
public String readAuthorizationFromDb(String loginId) {
  // Never do this if loginId is coming from user input!!
  String sql = "select authorization from security where login_id='" + loginId + "'";
  try (Connection con = getDbConnection()) {
    return readFromDb(con, sql);
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

If this function is called many times, with the same loginId, a programmer may consider 
this kind of optimization:

```java
Map<String, String> cache= new HashMap<>();     // <<<

public String readAuthorizationFromDb(String loginId) {
  if (cache.containsKey(loginId)) {             // <<<
    return cache.get(loginId);                  // <<<
  }                                             // <<<
  
  // Never do this if loginId is coming from user input!!
  String sql = "select authorization from security where login_id='" + loginId + "'";
  try (Connection con = getDbConnection()) {
    String auth = readFromDb(con, sql);
    cache.put(loginId, auth);                    // <<<
    return auth;
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

This solution could work for a simple program, however it suffers from several problems such as:
1. There is no control on the size of the map. If the method is called many times with different 
    loginIds, the size of map can increase and consume a lot of memory.
2. There is no control on expiring items in the cache. Once a loginId is put in the map it stays
    there, if the database contents change, there is no way to update the content of the map 
    besides restarting the program.
3. Having cache handling code in every method that needs caching is annoying and error prone.


Other solutions include:
1. Using an Aspects-oriented programming library like [AspectJ](https://eclipse.dev/aspectj/)
2. Using [Spring](https://spring.io/) framework `@Cacheable` annotation

Both AspectJ and Spring are highly used and safe options.

This library however, is a simple and specialized solution for memoization in Java.

## Setup
Add the following dependency to your maven's `pom.xml`:

```xml
<dependency>
    <groupId>com.tek271</groupId>
    <artifactId>memoize</artifactId>
    <version>2.0.1</version>
</dependency>
```

You can also directly download the source form https://github.com/ahabra/memoize and use it.
Note that this library uses [Byte Buddy](https://bytebuddy.net) for byte code instrumentation.

## Usage
Let's see how the same `readAuthorizationFromDb()` will look with the Tek271 Memoizer:

```java
import com.tek271.memoize.Remember;

class AuthDao {
  @Remember
  public String readAuthorizationFromDb(String loginId) {
    // Never do this if loginId is coming from user input!!
    String sql = "select authorization from security where login_id='" + loginId + "'";
    try (Connection con = getDbConnection()) {
      return readFromDb(con, sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  
}

```
As you can see, the only needed addition is the `@Remember` annotation before the method's declaration.
Now, calling `readAuthorizationFromDb()` with the same `loginId` will not cause a DB read for each
call.

To invoke the `readAuthorizationFromDb()` method:

```java
import com.tek271.memoize.RememberFactory;

// Create an instance of AuthDao
AuthDao authDao = RememberFactory.createProxy(AuthDao.class);
String auth = authDao.readAuthorizationFromDb('some-user-login');
```

## API Details
This library provides two static methods and two annotation, which will be discussed next.

### RememberFactory.createProxy()
It is defined as:

```java
package com.tek271.memoize;

public class RememberFactory {

  public static <T> T createProxy(Class<T> targetClass) {
    //...
  }
}
```

Creates a caching (memoizing) proxy instance for a class that contains methods with `@Remember` annotation. 
Calling these methods will cause them to be cached.
The `targetClass` must provide a _parameter-less_ constructor.

### RememberFactory.decorate()
It is defined as:

```java
package com.tek271.memoize;

public class RememberFactory {

  public static <T> T decorate(T objectToDecorate) {
    //...
  }
}
```
Creates a caching (memoizing) decorator for an existing object. The object's class definition
should contain methods with `@Remember` annotation.

Usually, you will call the `decorate()` method when you have an object already created by some
other library (e.g. _Spring_).

### @Remember Annotation
You can apply the `@Remember` annotation on methods that are:

1. Not final
2. Not static
3. Not void
4. For the same parameters values, the method must always return the same value
5. The method must not have any side effects like setting fields or properties
6. The method's parameters must support correct `equals()` and `hashCode()` methods

The  `@Remember` annotation provides the following optional parameters:

1. `maxSize`: int. Default value = 128. The maximum size of cache for the given method.
2. `timeToLive`: long. Default value = 2. The period of time after which, cached return values of the method will expire.
3. `timeUnit`: TimeUnit. Default value = TimeUnit.MINUTES

For example, if we need to memoize a method and cache up to 1000 values for up to one hour:

```java
@Remember(maxSize=1000, timeToLive=1, timeUnit=TimeUnit.HOURS)
```

### @Exclude Annotation
Sometimes, not all method parameters should be used as a part of the cache's key.
You can apply the `@Exclude` annotation on these parameters. For example:

```java
import com.tek271.memoize.Exclude;
import com.tek271.memoize.Remember;

@Remember
String readUserAddress(@Exclude Connection con,
                       String userName) {
    // ...
}
```
The Connection object is not something that you should include in a cache's key.

## References
1. Byte buddy: https://bytebuddy.net
2. Using Byte Buddy for proxy creation: https://www.javacodegeeks.com/2022/02/using-byte-buddy-for-proxy-creation.html
3. Create Proxies Dynamically Using CGLIB Library: https://objectcomputing.com/resources/publications/sett/november-2005-create-proxies-dynamically-using-cglib-library

## Changes
1. Version 1.0,  2007.03.01. First public release
2. Version 1.01, 2007.03.05. Some JavaDocs fixes and spelling mistakes.
3. Version 1.1,  2009.06.27
   1. Updated dependency to latest cglib jar (version 2.2)
   2. Added RememberFactory.clearCache() methods to ease unit testing.
   3. Added an optimization proposed by _Christian Semrau_.
   4. Added Generics support to RememberFactory.createProxy()
   5. Added RememberFactory.decorate() to decorate a given object (rather than class). This feature was requested by _Patrick McMichael_.
4. Version 2.0.0, 2025.11.18
   1. Total re-write
   2. Migrate from cglib to Byte Buddy
   3. Use Maven build
   4. Added @Exclude annotation
   5. Publish on maven central
5. Version 2.0.1, small typo fixes


