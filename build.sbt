organization  := "com.gu"

name := "sequence-hlist"

version := "0.1-SNAPSHOT"

val scalaSettings = Seq(
  scalaVersion := "2.12.6"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.9.0",
  "com.chuusai" %% "shapeless" % "2.3.3"
)

lazy val root = (project in file(".")).aggregate(takeaway, bowling, testing)

lazy val takeaway = project.settings(scalaSettings).settings(
  libraryDependencies ++= Seq(
//    "com.typesafe.play" %% "play-json" % "2.6.8",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe"   % "config" % "1.3.2",
    "com.twilio.sdk" % "twilio" % "7.15.5"
  )
)

lazy val bowling = project.settings(scalaSettings).settings(
  libraryDependencies ++= Seq(
    //    "com.typesafe.play" %% "play-json" % "2.6.8",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe"   % "config" % "1.3.2"
  )
)

lazy val testing = project.settings(scalaSettings).settings(
  libraryDependencies ++= Seq(
  )
)

lazy val scissors = project.settings(scalaSettings).settings(
  libraryDependencies ++= Seq(
  )
)

lazy val step = project.settings(scalaSettings).settings(
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.6.10",
    "org.typelevel" %% "cats-core" % "1.4.0",
    "com.amazonaws" % "aws-lambda-java-core" % "1.2.0"
  )
).settings(
  assemblyJarName := "step.jar"
)

lazy val abs = project.settings(scalaSettings).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.4.0",
    "com.chuusai" %% "shapeless" % "2.3.3"
  )
)
