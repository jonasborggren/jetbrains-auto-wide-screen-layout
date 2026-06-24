plugins {
    id("org.jetbrains.intellij.platform") version "2.0.0"
    kotlin("jvm") version "2.0.0"
}

val gitTag = System.getenv("GITHUB_REF_NAME") ?: ""
val projectVersion = if (gitTag.startsWith("v")) gitTag.substring(1) else "1.0.0"

group = "com.borggren"
version = projectVersion

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.2")
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        id.set("com.borggren.autowidescreen")
        name.set("Auto Widescreen Layout")
        version.set(projectVersion)

        
        vendor {
            name.set("Borggren")
            url.set("https://github.com/borggren/jetbrains-auto-wide-screen-layout")
        }

        ideaVersion {
            sinceBuild.set("242")
            untilBuild.set(provider { null as String? })
        }
    }
    
    publishing {
        // Prepare for marketplace publishing
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }
}

