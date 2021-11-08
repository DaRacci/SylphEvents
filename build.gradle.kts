plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("version-catalog")
    kotlin("jvm")                       version "1.6.0-RC2"
    id("com.github.johnrengelman.shadow")   version "7.1.0"
}



repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {

    compileOnly(libs.racciCore)
    compileOnly(libs.purpurAPI)
    compileOnly(libs.plugin.citizensAPI)
    compileOnly(libs.plugin.ecoEnchants)
    compileOnly(files("../API/GoldenCrates.jar"))
    compileOnly(files("../API/NexEngine.jar"))

    testImplementation(libs.jupiterAPI)
    testRuntimeOnly(libs.jupiterEngine)

}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks {

    compileKotlin {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    build {
        dependsOn(shadowJar)
        dependsOn(publishToMavenLocal)
    }

    val devServer by registering(Jar::class) {
        dependsOn(shadowJar)
        destinationDirectory.set(File("${System.getProperty("user.home")}/Desktop/Minecraft/Sylph/Development/plugins/"))
        archiveClassifier.set("")
        from(shadowJar)
    }

    val sourcesJar by registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    artifacts {
        archives(sourcesJar)
        archives(jar)
    }

    getByName<Test>("test") {
        useJUnitPlatform()
    }

}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/DaRacci/SylphEvents")
            credentials {
                password = System.getenv("TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            //artifactId = project.name.toLowerCase()
        }
    }
}


group = findProperty("group")!!
version = findProperty("version")!!