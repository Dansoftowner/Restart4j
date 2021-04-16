# Restart4j

Library for restarting your JVM application.
You will most likely use it for restarting your GUI application programmatically.

It works no matter how your application is running: 
 * From Jar
 * From an IDE
 * From a unique packaged form
 * etc...

### Requirements, Compatibility
* **Java 8** or higher
* Works on the 3 major platforms: **Windows**, **MacOS**, **Linux**

### Installation

#### Maven example

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.Dansoftowner</groupId>
    <artifactId>Restart4j</artifactId>
    <version>1.0</version>
</dependency>
```

#### Gradle example

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    implementation 'com.github.Dansoftowner:Restart4j:1.0'
}
```

### Some code examples

#### Simple restart
```java
final ApplicationRestart appRestart = ApplicationRestart.builder().build();
appRestart.restartApp();
```

#### Restart with more options
```java
final ApplicationRestart appRestart = ApplicationRestart.builder()
        .beforeNewProcessCreated(() -> System.out.println("New process will be created..."))
        .beforeCurrentProcessTerminated(() -> System.out.println("The app will be terminated"))
        .build();
appRestart.restartApp();
```

### Demo applications
Two demo applications can be found in the project:
 * [SimpleDemo](src/test/java/com/restart4j/SimpleDemo.java)
 * [ComplexDemo](src/test/java/com/restart4j/ComplexDemo.java)

![Running SimpleDemo](demo/SimpleDemo.gif)

# Projects using `Restart4j`
If this library is used by your project, let me know in the `Discussions` and I will mention that in this section.

### Used libraries

 * [SLF4j](http://www.slf4j.org/) - Simple Logging Facade for java
 * [OSHI](https://github.com/oshi/oshi) - Operating System & Hardware information
 * [Jetbrains annotations](https://github.com/JetBrains/java-annotations) - Annotations for JVM-based languages


