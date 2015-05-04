import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build
{
  val appSettings = Seq(
    version := "0.1",
    scalaVersion := "2.11.6",
    libraryDependencies ++= Seq(
      "org.sedis" %% "sedis" % "1.2.2"
    ),
    resolvers := Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "org.sedis"           at "http://pk11-scratch.googlecode.com/svn/trunk"
    )
  )
  val main = Project("JotItDownBackend", file("."), settings = appSettings).enablePlugins(play.PlayScala)
  override def rootProject = Some(main)
}
