plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.dokka") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.sylphmc"
version = "1.1.0"

dependencies {

    compileOnly("com.sylphmc:raccicore:0.3.1")
    compileOnly("com.sylphmc:sylph:0.1.5")
    compileOnly("net.citizensnpcs:citizensapi:2.0.28-SNAPSHOT")
    compileOnly("com.willfp:EcoEnchants:8.14.0")
    compileOnly(files("../API/GoldenCrates.jar"))
    compileOnly(files("../API/NexEngine.jar"))

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.6.0")

    api("com.charleskorn.kaml", "kaml", "0.37.0") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.jetbrains.kotlinx", "kotlinx-serialization-core")
    }

    compileOnly("org.purpurmc.purpur:purpur-api:1.18-R0.1-SNAPSHOT")

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

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand("version" to project.version)
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileKotlin {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    val devServer by registering(Jar::class) {
        dependsOn(shadowJar)
        destinationDirectory.set(File("${System.getProperty("user.home")}/Desktop/Minecraft/DevServer/plugins/"))
        archiveClassifier.set("all")
        from(zipTree(shadowJar.get().outputs.files.singleFile))
    }

    val sourcesJar by registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val javadocJar by registering(Jar::class) {
        dependsOn("javadoc")
        archiveClassifier.set("javadoc")
        from(javadoc.get().outputs.files.first())
    }

    dokkaHtml {
        outputDirectory.set(File("$buildDir/../docs"))
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    build {
        dependsOn(devServer)
        dependsOn(publishToMavenLocal)
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
    // NBT
    maven("https://repo.codemc.org/repository/maven-public/")
}