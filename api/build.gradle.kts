plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "net.thenextlvl.protect"
version = "3.1.0"

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
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    compileOnly("net.thenextlvl.core:paper:2.3.0-pre4")

    api("net.thenextlvl:nbt:3.0.0-pre1")
    api("net.thenextlvl.core:files:3.0.0")
    api(platform("com.intellectualsites.bom:bom-newest:1.55"))
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