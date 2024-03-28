import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

java {
    targetCompatibility = JavaVersion.VERSION_19
    sourceCompatibility = JavaVersion.VERSION_19
}

group = "net.thenextlvl.protect"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation("cloud.commandframework:cloud-paper:1.8.3")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.8.3")

    implementation(project(":api"))
    implementation("net.thenextlvl.core:files:1.0.4")
    implementation("net.thenextlvl.core:paper:1.2.6")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

paper {
    name = "Protect"
    main = "net.thenextlvl.protect.ProtectPlugin"
    apiVersion = "1.20"
    authors = listOf("NonSwag")
    website = "https://thenextlvl.net"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    libraries = listOf(
            "cloud.commandframework:cloud-paper:1.8.3",
            "cloud.commandframework:cloud-minecraft-extras:1.8.3"
    )
    serverDependencies {
        register("WorldEdit") {
            required = true
        }
    }
}

}