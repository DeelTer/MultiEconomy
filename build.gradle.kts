plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.1"
    id("io.freefair.lombok") version "9.5.0"
}

group = "ru.deelter"
version = "1.0"
description = "Multi-currency economy plugin with MySQL/SQLite/H2 and Vault support"

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/creatorfromhell/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/creatorfromhell/")
    maven("https://jitpack.io")
}

dependencies {

    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.2.0")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.16")

    compileOnly("org.jetbrains:annotations:24.1.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        enabled = false
    }

    shadowJar {
        relocate("com.zaxxer.hikari", "ru.deelter.multieconomy.libs.hikari")
        relocate("com.github.benmanes.caffeine", "ru.deelter.multieconomy.libs.caffeine")
        relocate("com.mysql", "ru.deelter.multieconomy.libs.mysql")
        relocate("org.sqlite", "ru.deelter.multieconomy.libs.sqlite")
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.3")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }
}