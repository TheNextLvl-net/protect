import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("maven-publish")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.0"
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_19
    sourceCompatibility = JavaVersion.VERSION_19
}

group = project(":api").group
version = project(":api").version

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.thenextlvl.net/snapshots")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation("cloud.commandframework:cloud-paper:1.8.3")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.8.3")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.43"))

    implementation(project(":api"))
    implementation("net.thenextlvl.core:i18n:1.0.14")
    implementation("net.thenextlvl.core:files:1.0.5-pre2")
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
                "protect.command.area.schematic",
                "protect.command.area.priority",
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

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            if (extra.has("RELEASES_USER"))
                username = extra["RELEASES_USER"].toString()
            if (extra.has("RELEASES_PASSWORD"))
                password = extra["RELEASES_PASSWORD"].toString()
        }
    }
}