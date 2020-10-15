![](logo_kachej.png)
# Kachej ![CI](https://github.com/jeziellago/kachej/workflows/CI/badge.svg?branch=master)  [ ![Download](https://api.bintray.com/packages/jeziellago/kachej/kachej/images/download.svg) ](https://bintray.com/jeziellago/kachej/kachej/_latestVersion) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/c6d894f3ef6642adb1dec80f88ff2aad)](https://www.codacy.com/gh/jeziellago/kachej/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jeziellago/kachej&amp;utm_campaign=Badge_Grade)

Write objects as files (to cache purpose) backed by Kotlin coroutines.
## Why?
- This tool is an alternative to build cache without intermediate format (as JSON, XML or other).
- Why not? 😎

## How it works?
Transform objects in files to cache them, to restore after, or make anything.
```kotlin
// Create Serializable object
data class User(val name: String, val lastName: String) : Serializable

val user = User("Jeziel", "Lago")
```
### Write objects
Create Kachej instance:
```kotlin
val cache = Kachej(
    parentDir = File("/cache/users"), 
    timeToLive = 60, 
    liveUnit = TimeUnit.MINUTES
)
```
#### Write single object:
```kotlin
// create file "user_123"
cache.write("user_123", user) {
    onSuccess { /* success */ }
    onFailure { error -> /* error */ }
}
```
#### Write Collection:
```kotlin
// create file "user_collection"
val userCollection = CacheableList<User>(
    listOf(user1, user2, ...)
)
cache.write("user_collection", userCollection) {
    onSuccess { /* success */ }
    onFailure { error -> /* error */ }
}
```
#### Write Map:
```kotlin
// create file "user_map"
val userCollection = CacheableMap(
    mapOf(
        "bob" to user1,
        "ana" to user2,
        ...
    )
)
cache.write("user_map", userCollection) {
    onSuccess { /* success */ }
    onFailure { error -> /* error */ }
}
```
### Read/restore objects
#### Single object
```kotlin
cache.read<User>("user_123") {
    onSuccess { user -> /* success */ }
    onFailure { error -> /* error */ }
}
```
#### Collection
```kotlin
cache.read<CacheableList<User>>("user_collection") {
    onSuccess { list -> /* success */ }
    onFailure { error -> /* error */ }
}
```
#### Map
```kotlin
cache.read<CacheableMap>("user_map") {
    onSuccess { map -> /* success */ }
    onFailure { error -> /* error */ }
}
```
### Clean
```kotlin
cache.clean("user_collection")
```
or
```kotlin
cache.cleanAll()
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
