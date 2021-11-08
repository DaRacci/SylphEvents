rootProject.name = "SylphEvents"
enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    repositories {
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from("me.racci:LibraryCatalog:1.1")
        }
    }
}