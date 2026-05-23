import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

import com.vanniktech.maven.publish.DeploymentValidation
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    java
    signing
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.vanniktech.maven.publish") version "0.36.0"
    id("com.diffplug.spotless") version "8.3.0"
}

group = "io.github.lijinhong11"
version = property("version") as String

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.devs.beer/")
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.momirealms.net/releases/")
    maven("https://repo.oraxen.com/releases")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://api.modrinth.com/maven/")
    maven("https://repo.auxilor.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-20260109.190012-50")

    // libraries
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.bstats:bstats-bukkit:3.2.1")

    // placeholders
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.1.0")

    // content provider
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("com.nexomc:nexo:1.16.1")
    compileOnly("net.momirealms:craft-engine-core:0.0.67")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.67")
    compileOnly("io.th0rgal:oraxen:1.207.0")
    compileOnly("net.Indyuce:MMOItems-API:6.10.1-SNAPSHOT")
    compileOnly("io.lumine:MythicLib-dist:1.7.1-SNAPSHOT")
    compileOnly("maven.modrinth:SCore:5.25.7.19")
    compileOnly("com.willfp:eco:6.77.3")
    compileOnly("com.willfp:EcoItems:5.66.0")
    compileOnly("com.willfp:libreforge:4.79.0:all")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }

    // lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.runServer {
    minecraftVersion("1.21.1")
}

spotless {
    java {
        palantirJavaFormat()
        removeUnusedImports()
        importOrder()
        trimTrailingWhitespace()
        expandWildcardImports()
    }
}

val targetJavaVersion = 21

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    toolchain {
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).links("https://hub.spigotmc.org/javadocs/spigot/")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.runServer {
    minecraftVersion("1.21.1")
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true, validateDeployment = DeploymentValidation.PUBLISHED)

    signAllPublications()

    coordinates("io.github.lijinhong11", "MittelLib", project.version.toString())

    pom {
        name.set(project.name)
        description.set("A library for Mittel series")
        url.set("https://github.com/lijinhong11/MittelLib")

        licenses {
            license {
                name.set("GPL 3.0 License")
                url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
            }
        }

        developers {
            developer {
                id.set("lijinhong11")
                name.set("Jinhong Li")
                email.set("tygfhk@outlook.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/lijinhong11/MittelLib.git")
            developerConnection.set("scm:git:ssh://github.com:lijinhong11/MittelLib.git")
            url.set("https://github.com/lijinhong11/MittelLib")
        }
    }

    configureBasedOnAppliedPlugins(
        // configures the -javadoc artifact, possible values:
        // - `JavadocJar.None()` don't publish this artifact
        // - `JavadocJar.Empty()` publish an empty jar
        // - `JavadocJar.Javadoc()` to publish standard javadocs
        // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
        javadocJar = JavadocJar.Javadoc(),
        // configures the -sources artifact, possible values:
        // - `SourcesJar.None()` don't publish this artifact
        // - `SourcesJar.Empty()` publish an empty jar
        // - `SourcesJar.Sources()` publish the sources
        sourcesJar = SourcesJar.Sources()
    )
}

signing {
    useGpgCmd()
}