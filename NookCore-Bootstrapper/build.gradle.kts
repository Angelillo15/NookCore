plugins {
  id("net.kyori.blossom") version "2.1.0"
}

dependencies {
  compileOnly(libs.paperApi)
}

sourceSets {
  main {
    blossom {
      javaSources {
        property("version", rootProject.version.toString())
      }
    }
  }
}