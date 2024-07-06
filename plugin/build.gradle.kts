import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.7"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
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
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.thenextlvl.net/snapshots")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("org.incendo:cloud-paper:2.0.0-beta.9")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-beta.9")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.45"))

    implementation(project(":api"))
    implementation("net.thenextlvl.core:i18n:1.0.18")
    implementation("net.thenextlvl.core:files:1.0.5")
    implementation("net.thenextlvl.core:paper:1.3.5")
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
        // admin perm-pack
        register("protect.admin") {
            description = "Allows players to manage all areas and bypass all restrictions"
            children = listOf(
                "protect.command.area.create",
                "protect.command.area.delete",
                "protect.command.area.manage",
                "protect.bypass.admin"
            )
        }

        // restriction bypass perm-pack
        register("protect.bypass.admin") {
            description = "Allows players to bypass any restriction"
            children = listOf(
                "protect.bypass.build",
                "protect.bypass.break",
                "protect.bypass.interact",
                "protect.bypass.entity-interact",
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
        register("protect.command.area.manage") {
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
        register("protect.command.area") {
            description = "Allows players to interact with areas in a non-destructive manner"
            children = listOf(
                "protect.command.area.flag.info",
                "protect.command.area.info",
                "protect.command.area.list"
            )
        }

        // flags perm-pack
        register("protect.command.area.flag") {
            description = "Allows players to manage area flags"
            children = listOf(
                "protect.command.area.flag.info",
                "protect.command.area.flag.set",
                "protect.command.area.flag.unset"
            )
        }

        // schematics perm-pack
        register("protect.command.area.schematic") {
            description = "Allows players to manage area schematics"
            children = listOf(
                "protect.command.area.schematic.delete",
                "protect.command.area.schematic.load",
                "protect.command.area.schematic.save"
            )
        }
    }
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("Protect")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            val versions: List<String> = (property("paperVersion") as String)
                .split(",")
                .map { it.trim() }
            platformVersions.set(versions)
            dependencies {
                url("FastAsyncWorldEdit", "https://hangar.papermc.io/IntellectualSites/FastAsyncWorldEdit") {
                    required.set(true)
                }
            }
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            username = System.getenv("RELEASES_USER")
            password = System.getenv("RELEASES_TOKEN")
        }
    }
}