plugins {
    kotlin("jvm") version "2.0.10"
}

group = "com.akryvtsun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val telegramSdkVersion = "7.10.0"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.telegram:telegrambots-client:$telegramSdkVersion")
    implementation("org.telegram:telegrambots-longpolling:$telegramSdkVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

val libsDirProvider = layout.buildDirectory.file("libs")

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes["Main-Class"] = "guru.MainKt"
            attributes["Class-Path"] = configurations.runtimeClasspath.get()
                .map { "libs/${it.name}" }
                .joinToString(" ")
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        // Copy runtime dependencies to the libs directory inside the JAR
        doFirst {
            val libDir = libsDirProvider.get().asFile
            if (!libDir.exists()) {
                libDir.mkdirs()
            }
            configurations.runtimeClasspath.get().forEach { file ->
                copy {
                    from(file)
                    into(libDir)
                }
            }
        }

        // Include the dependencies in the JAR
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}
