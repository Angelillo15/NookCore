dependencies {
  api(libs.hikariCP)
  api(libs.ebean)
  compileOnly(libs.configurateYaml)
  api(project(":NookCore-Logger"))
  api(project(":NookCore-Core"))
}
