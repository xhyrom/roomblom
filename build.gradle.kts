import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.21"
    application
}

group = "me.xhyrom.mumblum"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
    maven("https://m2.duncte123.dev/releases")
    maven("https://repo.jopga.me/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.2")
    //implementation("com.sedmelluq:lavaplayer:1.3.78")
    //implementation("com.github.jagrosh:lavaplayer:jmusicbot-SNAPSHOT")
    //implementation("com.github.freyacodes:Lavalink-Client:cde746afc")
    //implementation(files("libs/Lavalink-Client.jar"))
    implementation("com.fredboat.lavalink.client:Lavalink-Client:ea092bd6410752a95e9460affc844015e73a904b-SNAPSHOT")
    implementation("com.github.walkyst:lavaplayer-fork:1.3.98.4")

    implementation("com.github.the-codeboy:Piston4J:0.0.7")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    implementation("com.dunctebot:sourcemanagers:1.8.2")
    implementation("com.github.TopiSenpai.LavaSrc:lavasrc:3.1.7")

    implementation("com.github.top-gg:java-sdk:2.1.2")
}

tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to "me.xhyrom.mumblum.Bot")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes("Main-Class" to "me.xhyrom.mumblum.Bot")
    }
}

application {
    mainClass.set("me.xhyrom.mumblum.Bot")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}