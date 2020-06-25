plugins {
    id("kotlin")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
    jacoco
}

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
    }

    test {
        java.srcDir("src/test/kotlin")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<JacocoReport> {
    classDirectories.from("$buildDir/classes/kotlin/main")
    sourceDirectories.from("$projectDir/src/main/kotlin")

    reports {
        xml.isEnabled = true
        html.isEnabled = true
        xml.destination = File("$buildDir/jacoco/coverage.xml")
    }

    executionData.from("$buildDir/jacoco/test.exec")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

artifacts {
    archives(sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            groupId = "com.kachej"
            artifactId = "kachej"
            version = "0.1.2"
            artifact(sourcesJar)
        }
    }
}

bintray {
    val pUser = if (hasProperty("user")) property("user") as String else ""
    val pKey = if (hasProperty("key")) property("key") as String else ""
    user = pUser
    key = pKey
    setPublications("release")
    with(pkg) {
        name = "kachej"
        repo = "kachej"
        websiteUrl = "https://github.com/jeziellago/kachej"
        issueTrackerUrl = "https://github.com/jeziellago/kachej/issues"
        vcsUrl = "https://github.com/jeziellago/kachej.git"
        publicDownloadNumbers = true
        setLicenses("Apache-2.0")
        desc = "An alternative to cache objects as files easily using Kotlin Flow."
        version.name = "0.1.2"
        version.vcsTag = "0.1.2"
        publish = true
        override = true
    }
}

dependencies {
    implementation(Libs.kotlin)
    implementation(Libs.kotlin)
    implementation(Libs.coroutines)
    testImplementation(Libs.junit4)
}
