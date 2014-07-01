import com.github.retronym.SbtOneJar._

oneJarSettings

name := "twitter"

version := "1.0"

scalaVersion := "2.10.2"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= {
  val sprayV = "1.2.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor"     % "2.2.3",
    "joda-time"         %  "joda-time"      % "2.3",
    "org.joda"          % "joda-convert"    % "1.2",
    "io.spray"          % "spray-http"      % sprayV,
    "io.spray"          % "spray-httpx"     % sprayV,
    "io.spray"          % "spray-util"      % sprayV,
    "io.spray"          % "spray-client"    % sprayV,
    "io.spray"          % "spray-can"       % sprayV,
    "io.spray"          %% "spray-json"     % "1.2.6",
    "org.scalatest"     % "scalatest_2.10"  % "2.1.3" % "test",
    "org.mockito"       % "mockito-core"    % "1.9.5")
}


