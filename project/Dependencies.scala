import sbt._

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "performance-test-runner" % "6.2.0",
    "org.playframework" %% "play-ahc-ws-standalone"  % "3.0.5",
    "org.playframework" %% "play-ws-standalone-json" % "3.0.5",
    "org.apache.pekko"  %% "pekko-stream"            % "1.0.3",
    "ch.qos.logback"     % "logback-classic"         % "1.5.6"
  ).map(_ % Test)

}
