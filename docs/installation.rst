.. _installation:

Installation
============

To install Okra is simple:

Maven
-----

.. code-block:: xml

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


Gradle
------

.. code-block:: groovy

   dependencies {
       compile 'com.github.OkraScheduler:OkraSync:x.y.z'
   }

   repositories {
       maven {
           url 'https://jitpack.io'
       }
   }