plugins {
    id("kotlin")
    id("maven-publish")
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
            version = "0.2.0"
            artifact(sourcesJar)
        }
    }
}

dependencies {
    implementation(Libs.kotlin)
    implementation(Libs.kotlin)
    implementation(Libs.coroutines)
    testImplementation(Libs.turbine)
    testImplementation(Libs.junit4)
}
