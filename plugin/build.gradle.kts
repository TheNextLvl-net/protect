import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-rc2"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
    id("com.modrinth.minotaur") version "2.+"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

group = project(":api").group
version = project(":api").version

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.nexomc.com/snapshots")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    compileOnly("com.nexomc:nexo:1.10.0-dev.3")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.1.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))

    implementation(project(":api"))
    implementation("net.thenextlvl.core:files:3.0.0")
    implementation("net.thenextlvl.core:i18n:3.2.0")
    implementation("net.thenextlvl.core:nbt:2.3.2")
    implementation("net.thenextlvl.core:paper:2.2.1")
}

tasks.shadowJar {
    relocate("org.bstats", "net.thenextlvl.protect.bstats")
    archiveBaseName.set("protect")
}

paper {
    name = "Protect"
    main = "net.thenextlvl.protect.ProtectPlugin"
    description = "Protect certain areas or entire worlds"
    apiVersion = "1.21"
    foliaSupported = true
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("NonSwag")
    website = "https://thenextlvl.net"
    serverDependencies {
        register("FastAsyncWorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
        register("Nexo") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
    permissions {

        // admin perm-pack
        register("protect.admin") {
            description = "Admin permissions for Protect"
            children = listOf(
                "protect.bypass.admin",
                "protect.command.area.create",
                "protect.command.area.delete",
                "protect.command.area.info",
                "protect.command.area.list",
                "protect.command.area.priority",
                "protect.commands.area.flag",
                "protect.commands.area.group",
                "protect.commands.area.members",
                "protect.commands.area.owner",
                "protect.commands.area.parent",
                "protect.commands.area.schematic"
            )
        }

        // restriction bypass perm-pack
        register("protect.bypass.admin") {
            description = "Allows players to bypass any restriction"
            children = listOf(
                "protect.bypass.attack",
                "protect.bypass.destroy",
                "protect.bypass.enter",
                "protect.bypass.entity-interact",
                "protect.bypass.entity-shear",
                "protect.bypass.interact",
                "protect.bypass.leave",
                "protect.bypass.physical-interact",
                "protect.bypass.place",
                "protect.bypass.trample"
            )
        }

        // flags perm-pack
        register("protect.commands.area.flag") {
            description = "Allows players to manage area flags"
            children = listOf(
                "protect.command.area.flag.info",
                "protect.command.area.flag.list",
                "protect.command.area.flag.reset",
                "protect.command.area.flag.set",
                "protect.command.area.protect"
            )
        }

        // groups perm-pack
        register("protect.commands.area.group") {
            description = "Allows players to manage area groups"
            children = listOf(
                "protect.command.area.group.add",
                "protect.command.area.group.create",
                "protect.command.area.group.delete",
                "protect.command.area.group.list",
                "protect.command.area.group.redefine",
                "protect.command.area.group.remove"
            )
        }

        // members perm-pack
        register("protect.commands.area.members") {
            description = "Allows players to manage area members"
            children = listOf(
                "protect.command.area.members.add",
                "protect.command.area.members.list",
                "protect.command.area.members.remove"
            )
        }

        // owner perm-pack
        register("protect.commands.area.owner") {
            description = "Allows players to manage area owners"
            children = listOf(
                "protect.command.area.owner.remove",
                "protect.command.area.owner.set"
            )
        }

        // parent perm-pack
        register("protect.commands.area.parent") {
            description = "Allows players to manage area parents"
            children = listOf(
                "protect.command.area.parent.remove",
                "protect.command.area.parent.set"
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

        register("protect.command.area.create") { children = listOf("protect.command.area") }
        register("protect.command.area.delete") { children = listOf("protect.command.area") }
        register("protect.command.area.flag") { children = listOf("protect.command.area") }
        register("protect.command.area.group") { children = listOf("protect.command.area") }
        register("protect.command.area.info") { children = listOf("protect.command.area") }
        register("protect.command.area.list") { children = listOf("protect.command.area") }
        register("protect.command.area.members") { children = listOf("protect.command.area") }
        register("protect.command.area.owner") { children = listOf("protect.command.area") }
        register("protect.command.area.parent") { children = listOf("protect.command.area") }
        register("protect.command.area.priority") { children = listOf("protect.command.area") }
        register("protect.command.area.protect") { children = listOf("protect.command.area") }
        register("protect.command.area.redefine") { children = listOf("protect.command.area") }
        register("protect.command.area.schematic") { children = listOf("protect.command.area") }
        register("protect.command.area.teleport") { children = listOf("protect.command.area") }

        register("protect.command.area.flag.info") { children = listOf("protect.command.area.flag") }
        register("protect.command.area.flag.list") { children = listOf("protect.command.area.flag") }
        register("protect.command.area.flag.reset") { children = listOf("protect.command.area.flag") }
        register("protect.command.area.flag.set") { children = listOf("protect.command.area.flag") }

        register("protect.command.area.group.add") { children = listOf("protect.command.area.group") }
        register("protect.command.area.group.create") { children = listOf("protect.command.area.group") }
        register("protect.command.area.group.delete") { children = listOf("protect.command.area.group") }
        register("protect.command.area.group.list") { children = listOf("protect.command.area.group") }
        register("protect.command.area.group.redefine") { children = listOf("protect.command.area.group") }
        register("protect.command.area.group.remove") { children = listOf("protect.command.area.group") }

        register("protect.command.area.members.add") { children = listOf("protect.command.area.members") }
        register("protect.command.area.members.list") { children = listOf("protect.command.area.members") }
        register("protect.command.area.members.remove") { children = listOf("protect.command.area.members") }

        register("protect.command.area.owner.remove") { children = listOf("protect.command.area.owner") }
        register("protect.command.area.owner.set") { children = listOf("protect.command.area.owner") }

        register("protect.command.area.parent.remove") { children = listOf("protect.command.area.parent") }
        register("protect.command.area.parent.set") { children = listOf("protect.command.area.parent") }

        register("protect.command.area.schematic.delete") { children = listOf("protect.command.area.schematic") }
        register("protect.command.area.schematic.load") { children = listOf("protect.command.area.schematic") }
        register("protect.command.area.schematic.save") { children = listOf("protect.command.area.schematic") }
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
        changelog = System.getenv("CHANGELOG")
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
            dependencies {
                hangar("FastAsyncWorldEdit") {
                    required.set(true)
                }
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("YNoH2pBx")
    changelog = System.getenv("CHANGELOG")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    syncBodyFrom.set(rootProject.file("README.md").readText())
    loaders.add("paper")
    dependencies {
        required.project("fastasyncworldedit")
    }
}