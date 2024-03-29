import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.paper.PaperPluginDescription

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
    implementation("net.thenextlvl.core:i18n:1.0.14")
    implementation("net.thenextlvl.core:files:1.0.4")
    implementation("net.thenextlvl.core:paper:1.2.6")
    implementation("net.thenextlvl.core:adapters:1.0.8")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.shadowJar {
    archiveBaseName.set("protect")
    minimize()
}

paper {
    name = "Protect"
    main = "net.thenextlvl.protect.ProtectPlugin"
    apiVersion = "1.20"
    authors = listOf("NonSwag")
    website = "https://thenextlvl.net"
    serverDependencies {
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
    permissions {
        register("protect.command.area") {
            description = "Allows players to use the area command"
            children = listOf(
                    "protect.command.area.create",
                    "protect.command.area.delete",
                    "protect.command.area.manage"
            )
        }
        register("protect.command.area.manage") {
            description = "Allows players to manage protected areas"
            children = listOf(
                    "protect.command.area.rename",
                    "protect.command.area.redefine",
                    "protect.command.area.flag"
            )
        }
        register("protect.bypass") {
            description = "Allows players to bypass any restriction"
            children = listOf(
                    "protect.bypass.build",
                    "protect.bypass.break",
                    "protect.bypass.interact",
                    "protect.bypass.entity-interact",
                    "protect.bypass.trample",
                    "protect.bypass.enter",
                    "protect.bypass.leave"
            )
        }
        register("protect.admin") {
            description = "Allows players to bypass all restrictions and access to all commands"
            children = listOf(
                    "protect.bypass",
                    "protect.command.area"
            )
        }
    }
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("Protect")
        version.set(project.version as String)
        channel.set(if (isRelease) "Release" else "Snapshot")
        if (extra.has("HANGAR_API_TOKEN"))
            apiKey.set(extra["HANGAR_API_TOKEN"] as String)
        else apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                val versions: List<String> = (property("paperVersion") as String)
                        .split(",")
                        .map { it.trim() }
                platformVersions.set(versions)
                dependencies {
                    url("WorldEdit", "https://ci.athion.net/job/FastAsyncWorldEdit/") {
                        required.set(false)
                    }
                }
            }
        }
    }
}