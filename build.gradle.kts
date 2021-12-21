plugins {
    `java-library`
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.dokka") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.sylphmc"
version = "2.0.0"

allprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        // Citizens API
        maven("https://repo.citizensnpcs.co")
        // Purpur
//        maven("https://repo.purpurmc.org/snapshots")
        // ACF
        maven("https://repo.aikar.co/content/groups/aikar/")
        // Kyori
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        // NBT
        maven("https://repo.codemc.org/repository/maven-public/")
    }

    dependencies {

        compileOnly("com.sylphmc:raccicore:0.3.2")
        compileOnly("com.sylphmc:sylph:0.1.5")
        compileOnly("net.citizensnpcs:citizensapi:2.0.28-SNAPSHOT")
        compileOnly("com.willfp:EcoEnchants:8.14.0")
        compileOnly("com.github.angeschossen:LandsAPI:5.15.2")
        compileOnly(files("../API/GoldenCrates.jar"))
        compileOnly(files("../API/NexEngine.jar"))

        compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
        compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.6.0")

        compileOnly("org.purpurmc.purpur:purpur-api:1.18.1-R0.1-SNAPSHOT")

        testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks {

        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(17)
        }

        shadowJar {
            relocate("de.tr7zw.changeme.nbtapi", "me.racci.libs.nbtapi")
            relocate("de.tr7zw.annotations", "me.racci.libs.annotations")
        }

        compileKotlin {
            kotlinOptions.suppressWarnings = true
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        }

    }

}