dependencies {
  api(project(":NookCore-Player"))
  api(project(":NookCore-Core"))
  api(project(":NookCore-Logger"))
  compileOnly(libs.configurateYaml)
  compileOnly(libs.paperApi)
  compileOnly(project(":NookCore-Config"))
}