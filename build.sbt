val tapirVersion = "1.2.9"

lazy val brokerless = (project in file(".")).settings(
  Seq(
    name := "brokerless",
    version := "0.1.0-SNAPSHOT",
    organization := "net.gesekus",
    scalaVersion := "3.2.2",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.6",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "dev.zio" %% "zio-test" % "2.0.5" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.5" % Test,
      "com.softwaremill.sttp.client3" %% "zio-json" % "3.8.13" % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  )
)
