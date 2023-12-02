import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.0"
    application
}

group = "dev.xhyrom.roomblom"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
    maven("https://m2.duncte123.dev/releases")
    maven("https://repo.jopga.me/releases")
    maven("https://maven.arbjerg.dev/snapshots")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.12")
    implementation("dev.schlaubi.lavakord:kord:5.1.7")
    implementation("dev.schlaubi.lavakord:jda:5.1.7")

    implementation("com.github.the-codeboy:Piston4J:0.0.7")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    implementation("com.dunctebot:sourcemanagers:1.8.2")
    implementation("com.github.TopiSenpai.LavaSrc:lavasrc:3.1.7")

    implementation("com.github.top-gg:java-sdk:2.1.2")
    implementation("redis.clients:jedis:4.3.1")
}

tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to "dev.xhyrom.roomblom.Bot")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes("Main-Class" to "dev.xhyrom.roomblom.Bot")
    }
}

application {
    mainClass.set("dev.xhyrom.roomblom.Bot")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}