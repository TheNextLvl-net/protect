plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "net.thenextlvl.protect"
version = "3.1.2"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.thenextlvl.net/snapshots")
}

dependencies {
    compileOnlyApi("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    api("net.thenextlvl:nbt:3.0.1")
    api("net.thenextlvl.core:files:3.0.1")
    api(platform("com.intellectualsites.bom:bom-newest:1.56-SNAPSHOT"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            username = System.getenv("REPOSITORY_USER")
            password = System.getenv("REPOSITORY_TOKEN")
        }
    }
}