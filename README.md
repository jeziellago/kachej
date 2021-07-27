![](logo_kachej.png)

# Kachej ![CI](https://github.com/jeziellago/kachej/workflows/CI/badge.svg?branch=master)

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

### Put objects

Create Cache instance:

```kotlin
val cache = Cache.of(
    parentDir = File("/cache/users"),
    timeToLive = 60,
    liveUnit = TimeUnit.MINUTES
)
```

#### Put single object:

```kotlin
// create file "user_123"
cache.put("user_123", user)
    .catch { error -> /* error */ }
    .collect { /* success */ }
```

#### Put Collection:

```kotlin
// create file "user_collection"
val userCollection = cacheOf(user1, user2, ...)

cache.put("user_collection", userCollection)
    .catch { error -> /* error */ }
    .collect { /* success */ }
```

#### Put Map:

```kotlin
// create file "user_map"
val userMap = cacheOf(
    "bob" to user1,
    "ana" to user2
)

cache.put("user_map", userMap)
    .catch { error -> /* error */ }
    .collect { /* success */ }
```

### Get objects

#### Single object

```kotlin
cache.get<User>("user_123")
    .catch { error -> /* error */ }
    .collect { user -> /* success */ }
```

#### Collection

```kotlin
cache.get<CacheableList<User>>("user_collection")
    .catch { error -> /* error */ }
    .collect { list -> /* success */ }
```

#### Map

```kotlin
cache.get<CacheableMap>("user_map")
    .catch { error -> /* error */ }
    .collect { map -> /* success */ }
```

### Clear

```kotlin
cache.clear("user_collection")
    .catch { error -> /* error */ }
    .collect { /* success */ }
```

or

```kotlin
cache.clearAll()
    .catch { error -> /* error */ }
    .collect { /* success */ }
```

## Dependency

- Project `build.gradle`

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

- Module `build.gradle`

```
dependencies {
    implementation 'com.github.jeziellago:kachej:VERSION`
}
```
