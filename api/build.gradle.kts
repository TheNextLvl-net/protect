plugins {
    id("java")
    id("maven-publish")
}

group = "net.thenextlvl.protect"
version = "3.0.2"

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
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core") {
        exclude("org.jetbrains", "annotations")
    }
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT") {
        exclude("org.jetbrains", "annotations")
    }

    implementation("net.thenextlvl.core:nbt:2.2.15")
    implementation("net.thenextlvl.core:files:2.0.1")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))

    annotationProcessor("org.projectlombok:lombok:1.18.36")
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