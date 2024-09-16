import java.net.URI

plugins {
    kotlin("jvm") version "2.0.10"
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
//    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("dev.arbjerg:lavaplayer:2.2.1")
    implementation("org.slf4j:slf4j-simple:2.0.16")
//    implementation("com.github.natanbc:lavadsp:0.7.4")
    implementation("dev.lavalink.youtube:common:1.7.2")
    implementation("com.github.JustRed23:lavadsp:0.7.7-1")
}

tasks.test {
    useJUnitPlatform()
}