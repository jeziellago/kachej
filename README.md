# Kachej
Write objects as files using Kotlin Coroutines.

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
val kachej = Kachej()

// or using parentDir
val kachej = Kachej(parentDir = File("/tmp/users"))
```
Use Kotlin Coroutines and write it:
```kotlin
launch { // or async, or use suspend functions, feel free
    kachej.write("user.kachej", user) // create file "user.kachej"
}
```
### Restore objects
```kotlin
launch { // or async, or use suspend functions, feel free
    val user: User = kachej.read<User>("user.kachej")
}
```
## Add dependencies
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
    implementation 'com.github.jeziellago:kachej:0.1.0'
}
```
