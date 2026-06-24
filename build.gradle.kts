plugins {
    id("org.jetbrains.intellij.platform") version "2.0.0"
    kotlin("jvm") version "2.0.0"
}

group = "com.borggren"
version = "1.0.0"

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
        version.set("1.0.0")
        
        description.set("Automatically toggles the 'Widescreen Tool Window Layout' option depending on whether the IDE window itself is wide.")
        
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

