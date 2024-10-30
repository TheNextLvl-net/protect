import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

group = project(":api").group
version = project(":api").version

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.50"))

    implementation(project(":api"))
    implementation("net.thenextlvl.core:i18n:1.0.20")
    implementation("net.thenextlvl.core:files:2.0.0")
    implementation("net.thenextlvl.core:paper:1.5.2")
    implementation("net.thenextlvl.core:adapters:1.0.9")

    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.shadowJar {
    relocate("org.bstats", "net.thenextlvl.protect.bstats")
    archiveBaseName.set("protect")
}

paper {
    name = "Protect"
    main = "net.thenextlvl.protect.ProtectPlugin"
    description = "Protect certain areas or entire worlds"
    apiVersion = "1.20"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("NonSwag")
    website = "https://thenextlvl.net"
    serverDependencies {
        register("FastAsyncWorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
    permissions {
        // restriction bypass perm-pack
        register("protect.bypass.admin") {
            description = "Allows players to bypass any restriction"
            children = listOf(
                "protect.bypass.build",
                "protect.bypass.break",
                "protect.bypass.interact",
                "protect.bypass.entity-interact",
                "protect.bypass.physical-interact",
                "protect.bypass.trample",
                "protect.bypass.enter",
                "protect.bypass.leave",
                "protect.bypass.empty-bucket",
                "protect.bypass.fill-bucket",
                "protect.bypass.empty-bottle",
                "protect.bypass.fill-bottle",
                "protect.bypass.wash-banner",
                "protect.bypass.wash-shulker",
                "protect.bypass.wash-armor"
            )
        }

        // area management perm-pack
        register("protect.commands.area.manage") {
            description = "Allows players to manage existing areas"
            children = listOf(
                "protect.command.area",
                "protect.command.area.flag",
                "protect.command.area.priority",
                "protect.command.area.redefine",
                // "protect.command.area.rename",
                "protect.command.area.schematic"
            )
        }

        // non-destructive area perm-pack
        register("protect.commands.area") {
            description = "Allows players to interact with areas in a non-destructive manner"
            children = listOf(
                "protect.command.area.flag.info",
                "protect.command.area.info",
                "protect.command.area.list"
            )
        }

        // flags perm-pack
        register("protect.commands.area.flag") {
            description = "Allows players to manage area flags"
            children = listOf(
                "protect.command.area.flag.info",
                "protect.command.area.flag.set",
                "protect.command.area.flag.reset",
                "protect.command.area.protect"
            )
        }

        // schematics perm-pack
        register("protect.commands.area.schematic") {
            description = "Allows players to manage area schematics"
            children = listOf(
                "protect.command.area.schematic.delete",
                "protect.command.area.schematic.load",
                "protect.command.area.schematic.save"
            )
        }
        register("protect.command.area")
    }
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("Protect")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
            dependencies {
                url("FastAsyncWorldEdit", "https://hangar.papermc.io/IntellectualSites/FastAsyncWorldEdit") {
                    required.set(true)
                }
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("YNoH2pBx")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    loaders.add("paper")
    dependencies {
        required.project("fastasyncworldedit")
    }
}