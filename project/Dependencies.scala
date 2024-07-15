import sbt._

object Dependencies {

  private val gatlingVersion = "3.6.1"

  val test = Seq(
    "com.typesafe"           % "config"                    % "1.4.2",
    "uk.gov.hmrc"           %% "performance-test-runner"   % "5.6.0",
    "io.gatling"             % "gatling-test-framework"    % gatlingVersion,
    "com.typesafe.play"     %% "play-ahc-ws-standalone"    % "2.1.10",
    "com.typesafe.play"     %% "play-ws-standalone-json"   % "2.1.2",
    "com.github.pureconfig" %% "pureconfig"                % "0.17.2",
    "io.gatling.highcharts"  % "gatling-charts-highcharts" % gatlingVersion
  ).map(_ % Test)

}
