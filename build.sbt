lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.ikempf",
      scalaVersion := "2.12.6",
      version := "0.1.0-SNAPSHOT"
    )),
  scalacOptions += "-Ypartial-unification",
  name := "reactor",
  libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"
)
