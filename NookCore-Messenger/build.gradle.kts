dependencies {
  api(project(":NookCore-Core"))
  api(project(":NookCore-Event"))
  api(project(":NookCore-Player"))
  compileOnly(libs.jedis)
  compileOnly(libs.paperApi)
  compileOnly(libs.waterfall)
  compileOnly(libs.velocity)
}