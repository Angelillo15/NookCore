publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "com.nookure.core"
      artifactId = "database"
      version = rootProject.version.toString()

      from(components["java"])
    }
  }
}