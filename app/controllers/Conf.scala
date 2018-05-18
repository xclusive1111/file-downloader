package controllers

object Conf {
  val tmpDir = System.getProperties.getOrDefault("TEMP", System.getProperty("java.io.tmpdir"))
}
