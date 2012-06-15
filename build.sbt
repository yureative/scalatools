name := "scalatools"

version := "0.0.1"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.7.2" % "test",
  "org.slf4j" % "slf4j-api" %  "1.6.4",
  "org.slf4j" % "slf4j-simple" %  "1.6.4",
  "commons-io" % "commons-io" % "2.3"
)
