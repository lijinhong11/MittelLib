import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

import com.vanniktech.maven.publish.DeploymentValidation
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import io.github.lijinhong11.nexusmcpublisher.VersionTag
import java.nio.charset.StandardCharsets

plugins {
    java
    signing
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.vanniktech.maven.publish") version "0.36.0"
    id("com.diffplug.spotless") version "8.9.0"
    id("io.freefair.lombok") version "9.5.0"
    id("io.github.lijinhong11.nexusmcpublisher") version "1.0.3"
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
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    // libraries
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("io.github.lijinhong11:MDatabase:1.2.1")
    implementation("com.ezylang:EvalEx:3.7.0")

    // placeholders
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.1.0")

    // content provider
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("com.nexomc:nexo:1.27.0")
    compileOnly("net.momirealms:craft-engine-core:26.8")
    compileOnly("net.momirealms:craft-engine-bukkit:26.8")
    compileOnly("io.th0rgal:oraxen:1.218.0")
    compileOnly("net.Indyuce:MMOItems-API:6.10.1-SNAPSHOT")
    compileOnly("io.lumine:MythicLib-dist:1.7.1-SNAPSHOT")
    compileOnly("maven.modrinth:SCore:5.25.7.19")
    compileOnly("com.willfp:eco:6.77.3")
    compileOnly("com.willfp:EcoItems:5.66.0")
    compileOnly("com.willfp:libreforge:4.79.0:all")
    compileOnly("io.lumine:Mythic-Dist:5.12.1")

    //other hooks
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }
    compileOnly("org.black_ixx:playerpoints:3.3.5")
}

tasks.runServer {
    minecraftVersion("26.1.2")
}

spotless {
    java {
        cleanthat()

        palantirJavaFormat()

        removeUnusedImports()
        importOrder()
        formatAnnotations()

        trimTrailingWhitespace()

        licenseHeaderFile(file("header.txt"))
    }
}

val targetJavaVersion = 25

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

nexusMCPublisher {
    resourceId.set("17efc48a-7609-482c-abce-70ae684542bb")
    versionTag.set(VersionTag.RELEASE)
    versionTitle = project.property("version") as String
    changelog.set(file("changelog.txt").readLines(StandardCharsets.UTF_8).joinToString("\n"))
    mcVersions.set(listOf("26.1", "26.1.1", "26.1.2", "26.2"))
    token = System.getenv("NEXUSMC_API_TOKEN")
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
