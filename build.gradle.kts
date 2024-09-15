plugins {
    kotlin("jvm") version "2.0.10"
}

group = "com.akryvtsun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.telegram:telegrambots:6.8.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}