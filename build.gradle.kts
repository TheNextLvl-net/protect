plugins {
    id("java")
    id("java-library")
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    configurations.compileClasspath {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }

    tasks.compileJava {
        options.release.set(21)
    }

    group = "net.thenextlvl.protect"

    repositories {
        mavenCentral()
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.thenextlvl.net/releases")
        maven("https://repo.thenextlvl.net/snapshots")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
        compileOnlyApi(platform("com.intellectualsites.bom:bom-newest:1.57-SNAPSHOT"))

        api("net.thenextlvl:nbt:4.3.4")
    }
}
