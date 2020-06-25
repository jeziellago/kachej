![](logo_kachej.png)
# Kachej ![CI](https://github.com/jeziellago/kachej/workflows/CI/badge.svg?branch=master)  [ ![Download](https://api.bintray.com/packages/jeziellago/kachej/kachej/images/download.svg) ](https://bintray.com/jeziellago/kachej/kachej/_latestVersion)

Write objects as files using Kotlin Flow.
## Why?
- This tool is an alternative to cache objects without having to convert to JSON, XML or other.
- Why not? 😎

## How it works?
Transform objects in files to cache them, to restore after, or make anything.
```kotlin
// Create Serializable object
data class User(val name: String, val lastName: String) : Serializable

val user = User("Jeziel", "Lago")
```
### Write objects
```kotlin
// Create Kachej instance
val cache = Kachej(
    parentDir = File("/cache/users"), 
    timeToLive = 60, 
    liveUnit = TimeUnit.MINUTES
)
```
Using Kotlin Flow write it:
```kotlin
cache.write("user.cache", user) // create file "user.cache"
    .catch { /* Error */ }
    .collect { /* Success */ }

```
### Restore objects
```kotlin
cache.read<User>("user.cache")
    .catch { /* Error */ }
    .collect { user ->
        // do something
    }
```
Use Flow operators to transformations:
```kotlin
cache.read<User>("user.cache")
    .map { /* ... */ }
    .flatMapConcat { /* .... */ }
    .catch { /* Error */ }
    .collect { 
        // do something
    }
```
### Clean objects
```kotlin
cache.clean("user.cache")
    .catch { /* Error */ }
    .collect { 
        // do something
    }
```
or
```kotlin
cache.cleanAll()
    .catch { /* Error */ }
    .collect { 
        // do something
    }
```
## Add dependencies
- Project `build.gradle` 
```
allprojects {
    repositories {
        jcenter()
    }
}
```
- Module `build.gradle` 
```
dependencies {
    implementation 'com.kachej:kachej:0.1.2'
}
```
