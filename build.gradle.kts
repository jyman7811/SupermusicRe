import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = uri("https://jitpack.io"))
    maven(url = uri("https://m2.dv8tion.net/releases"))
    maven(url = uri("https://maven.lavalink.dev/releases"))
    maven(url = uri("https://maven.lavalink.dev/snapshots"))
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:6.4.2")
    implementation("dev.arbjerg:lavaplayer:2.2.7")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("dev.lavalink.youtube:v2:1.18.1")
    implementation("com.github.JustRed23:lavadsp:0.7.7-1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
    implementation("moe.kyokobot.libdave:adapter-jda:0.1.2")
    implementation("moe.kyokobot.libdave:impl-jni:0.1.2")

    // 실행 환경에 맞춰 필요한 네이티브 환경을 추가하세요. (아래는 Windows 64비트 예시)
    implementation("moe.kyokobot.libdave:natives-win-x86-64:0.1.2")
}

tasks.test {
    useJUnitPlatform()
}