resolvers += Resolver.mavenLocal
publishMavenStyle := true
crossPaths := false
publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
publishTo := {
  Some(MavenCache("local-maven", file("~/.m2/repository")))
}

name := "rule-sequence"
version := "0.1"
scalaVersion := "2.13.7"
idePackagePrefix := Some("com.hayden")

ThisBuild / organization := "com.hayden"
ThisBuild / version      := "1.0.0"

name := "rule-sequence"

libraryDependencies ++= Seq(
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.8.1",
  "org.assertj" % "assertj-core" % "3.9.1"
)