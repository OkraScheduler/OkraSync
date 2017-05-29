# OkraSimple

Okra implementation using MongoDB Java Driver.
This is the fastest Okra implementation ever made.

[![codecov](https://codecov.io/gh/OkraScheduler/OkraSimple/branch/master/graph/badge.svg)](https://codecov.io/gh/OkraScheduler/OkraSimple)

[![Build Status](https://travis-ci.org/OkraScheduler/OkraSimple.svg?branch=master)](https://travis-ci.org/OkraScheduler/OkraSimple)

### Requirements

* Java 8
* MongoDB

### Note 

Pull Requests are always welcome! We will always review and accept them really fast.

### Binaries

[![](https://jitpack.io/v/OkraScheduler/OkraSimple.svg)](https://jitpack.io/#OkraScheduler/OkraSimple)

#### Gradle
build.gradle
```groovy
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```

```groovy
    dependencies {
        compile 'com.github.OkraScheduler:OkraSync:x.y.z'
    }
```

#### Maven
```xml
	<dependency>
	    <groupId>com.github.OkraScheduler</groupId>
	    <artifactId>OkraSimple</artifactId>
	    <version>x.y.z</version>
	</dependency>

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```