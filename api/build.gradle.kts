plugins {
    id("maven-publish")
}

dependencies {
    compileOnlyApi("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
}


tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("--add-reads", "net.thenextlvl.protect=ALL-UNNAMED"))
}

tasks.withType<Test>().configureEach {
    jvmArgs("--add-reads", "net.thenextlvl.protect=ALL-UNNAMED")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--add-reads", "net.thenextlvl.protect=ALL-UNNAMED")
}

tasks.javadoc {
    val options = options as StandardJavadocDocletOptions
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:")
    options.addStringOption("-add-reads", "net.thenextlvl.protect=ALL-UNNAMED")
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
