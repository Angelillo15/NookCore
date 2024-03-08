dependencies {
  compileOnly(libs.waterfall)
  compileOnly(libs.velocity)
  compileOnly(libs.paperApi)
  compileOnly(libs.adventureApi)
  compileOnly(libs.miniMessage)
  compileOnly(libs.adventureBukkit)
  compileOnly(libs.adventureBungee)
  compileOnly(project(":NookCore-Core"))
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "com.nookure.core"
      artifactId = "logger"
      version = rootProject.version.toString()

      from(components["java"])
    }
  }
}