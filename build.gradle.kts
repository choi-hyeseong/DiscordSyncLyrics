import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = URI("https://m2.dv8tion.net/releases")
    }
    maven {
        url = URI("https://maven.lavalink.dev/snapshots")
    }
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:3.2.0")
    implementation("com.google.code.gson:gson:2.7")
    implementation("net.dv8tion:JDA:5.0.0-beta.23")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("dev.arbjerg:lavaplayer:0eaeee195f0315b2617587aa3537fa202df07ddc-SNAPSHOT") //현재 latest 버젼에서 오류 발생.
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}