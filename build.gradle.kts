plugins {
    kotlin("jvm") version "2.0.10"
}

group = "com.akryvtsun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.telegram:telegrambots:6.8.0")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.11.1")
}

val libsDir = "$buildDir/libs"

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes["Main-Class"] = "echo.MainKt"
            attributes["Class-Path"] = configurations.runtimeClasspath.get()
                .map { "libs/${it.name}" }
                .joinToString(" ")
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        // Copy runtime dependencies to the libs directory inside the JAR
        doFirst {
            val libDir = File(libsDir)
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
