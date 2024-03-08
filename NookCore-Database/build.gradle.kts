dependencies {
  api(libs.jooq)
  api(libs.hikariCP)
  compileOnly(libs.configurateYaml)
  api(project(":NookCore-Logger"))
  api(project(":NookCore-Core"))
}
