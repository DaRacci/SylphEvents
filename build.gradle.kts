plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.sylphmc"
version = "1.1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    // Citizens API
    maven("https://repo.citizensnpcs.co")
    // Purpur
    maven("https://repo.purpurmc.org")
    // ACF
    maven("https://repo.aikar.co/content/groups/aikar/")
    // Kyori
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {

    compileOnly("com.sylphmc:raccicore:0.3.1")
    compileOnly("com.sylphmc:sylph:0.1.5")
    compileOnly("org.purpurmc.purpur:purpur-api:1.18-R0.1-SNAPSHOT")
    compileOnly("net.citizensnpcs:citizensapi:2.0.28-SNAPSHOT")
    compileOnly("com.willfp:EcoEnchants:8.14.0")
    compileOnly(files("../API/GoldenCrates.jar"))
    compileOnly(files("../API/NexEngine.jar"))

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")

}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks {

    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    val devServer by registering(Jar::class) {
        dependsOn(shadowJar)
        destinationDirectory.set(File("${System.getenv("HOME")}/Desktop/Minecraft/DevServer/plugins/"))
        archiveClassifier.set("all")
        from(zipTree(shadowJar.get().outputs.files.singleFile))
    }

    val sourcesJar by registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    artifacts {
        archives(sourcesJar)
    }

    build {
        if(System.getenv("CI") != "true") {
            dependsOn(publishToMavenLocal)
            dependsOn(devServer)
        }
    }

}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/DaRacci/SylphEvents")
            credentials {
                username = System.getenv("USERNAME") ?: findProperty("USERNAME").toString()
                password = System.getenv("TOKEN") ?: findProperty("TOKEN").toString()
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()
        }
    }
}