import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "net.thenextlvl.protect"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.9")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    compileOnly("cloud.commandframework:cloud-paper:1.8.3")
    compileOnly("cloud.commandframework:cloud-minecraft-extras:1.8.3")

    implementation(project(":api"))
    implementation("net.thenextlvl.core:core-api:3.1.10")
    implementation("net.thenextlvl.core:bukkit-core:1.0.0")

    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

bukkit {
    name = "Protect"
    main = "net.thenextlvl.protect.Protect"
    apiVersion = "1.19"
    authors = listOf("NonSwag")
    depend = listOf("WorldEdit")
    website = "https://thenextlvl.net"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    libraries = listOf(
            "cloud.commandframework:cloud-paper:1.8.3",
            "cloud.commandframework:cloud-minecraft-extras:1.8.3"
    )
}