# Java Tek271 Memoizer

A Java open source memoization library to cache the results of slow methods using annotations.

## History of this library
I initially created this library back in 2007 using Java 5 and Ant build, this was before Github 
existed. I hosted the code on my own site, but it was a headache to keep it up-to-date, so by
2009, I stopped updating it. However, I always intended to update it and publish it on Github,
so here it is again.

* Here is the [old code before migrating to github](old/old-1.1.zip)
* Here is the [old programmer's introduction](old/tek271.memoizer.intro.html)

The remaining of this document describes the new and updated version.

## Introduction
If a function produces the same output given the same inputs, and if this function is slow,
it makes sense for the function to cache its outputs to avoid repeated evaluations. This caching
behavior is called [Memoization](https://en.wikipedia.org/wiki/Memoization) 
_(Yes, there is no __r__)_.

This library provides a simple mechanism to memoize any Java method using `@Remember` annotation.

## Motivation
Suppose that you have a method that reads some authorization string from database for a given login:

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
    loginIds, the size of map can increase and run out of memory.
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
    <groupId>com.tek271.memoize</groupId>
    <artifactId>memoize</artifactId>
    <version>2.0.0</version>
</dependency>
```

You can also directly download the source form https://github.com/ahabra/memoizer and use it.

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

TODO: finish me

