plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

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

    api("net.thenextlvl:nbt:4.0.0-pre1")
    api("net.thenextlvl.core:files:4.0.0-pre1")
    api(platform("com.intellectualsites.bom:bom-newest:1.56-SNAPSHOT"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = "protect"
        groupId = "net.thenextlvl"
        pom.scm {
            val repository = "TheNextLvl-net/protect"
            url.set("https://github.com/$repository")
            connection.set("scm:git:git://github.com/$repository.git")
            developerConnection.set("scm:git:ssh://github.com/$repository.git")
        }
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