publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "com.nookure.core"
      artifactId = "core"
      version = rootProject.version.toString()

      from(components["java"])
    }
  }
}