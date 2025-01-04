plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://maven.lavalink.dev/releases")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("dev.arbjerg:lavaplayer:2.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("dev.lavalink.youtube:v2:1.11.2")
    implementation("com.github.JustRed23:lavadsp:0.7.7-1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
}

tasks.test {
    useJUnitPlatform()
}