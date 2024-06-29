plugins {
  alias(libs.plugins.grgitPublish)
  alias(libs.plugins.shadowJar)
  alias(libs.plugins.grgit)
  id("java")
  id("maven-publish")
}

val versionCode = "1.0.0"

version = "${versionCode}-${grgit.head().abbreviatedId}"
group = "com.nookure.core"

repositories {
  mavenCentral()
}

allprojects {
  version = rootProject.version.toString()
  group = rootProject.group.toString()
}

subprojects {
  apply<JavaPlugin>()
  apply(plugin = "com.github.johnrengelman.shadow")
  apply(plugin = "maven-publish")
  apply(plugin = "java-library")

  repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.nookure.com")
    maven("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
  }

  dependencies {
    compileOnly(rootProject.libs.guice)
    compileOnly(rootProject.libs.adventureApi)
    compileOnly(rootProject.libs.miniMessage)
    compileOnly(rootProject.libs.caffeine)
  }

  java {
    withJavadocJar()
    withSourcesJar()
  }

  tasks {
    withType<JavaCompile> {
      options.encoding = "UTF-8"
    }

    withType<Javadoc> {
      options.encoding = "UTF-8"
    }
  }

  publishing {
    repositories {
      maven {
        name = "local"
        url = uri("${rootProject.rootDir}/maven")
      }
    }

    publications {
      create<MavenPublication>("mavenJava") {
        groupId = project.group.toString()
        artifactId = project.name
        version = rootProject.version.toString()

        from(components["java"])
      }
    }
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

gitPublish {
  repoUri = "https://github.com/Nookure/maven.git"
  branch = "main"
  fetchDepth = null
  commitMessage = "NookureCore $version"

  contents {
    from("${rootProject.rootDir}/maven")
  }

  preserve {
    include("**")
  }
}
