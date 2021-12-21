plugins {
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

dependencies {
    runtimeOnly(project(":Everbright2021"))

    api("com.charleskorn.kaml", "kaml", "0.37.0") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.jetbrains.kotlinx", "kotlinx-serialization-core")
    }
}

tasks {

    withType<Jar> {
        this.archiveBaseName.set(rootProject.name)
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand("version" to rootProject.version)
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    shadowJar {
        dependencies {
            dependency(":Everbright2021")
        }
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
        outputDirectory.set(File("${rootProject.buildDir}/../docs"))
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