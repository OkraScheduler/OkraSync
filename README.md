# OkraSync

Okra implementation using synchronous MongoDB Java Driver.
This is the fastest synchronous Okra implementation ever made.

[![codecov](https://codecov.io/gh/OkraScheduler/OkraSync/branch/master/graph/badge.svg)](https://codecov.io/gh/OkraScheduler/OkraSync)

[![Build Status](https://travis-ci.org/OkraScheduler/OkraSync.svg?branch=master)](https://travis-ci.org/OkraScheduler/OkraSync)

### Requirements

* Java 8
* MongoDB Synchronous Driver

### Note 

Pull Requests are always welcome! We will always review and accept them really fast.

### Binaries

[![](https://jitpack.io/v/OkraScheduler/OkraSync.svg)](https://jitpack.io/#OkraScheduler/OkraSync)

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
	    <artifactId>OkraSync</artifactId>
	    <version>x.y.z</version>
	</dependency>

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
