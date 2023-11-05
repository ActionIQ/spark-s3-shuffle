//
// Copyright 2022- IBM Inc. All rights reserved
// SPDX-License-Identifier: Apache2.0
//

version := "0.9.5-aiq1"
scalaVersion := sys.env.getOrElse("SCALA_VERSION", "2.12.15")
organization := "com.ibm"
name := "spark-s3-shuffle"
val sparkVersion = sys.env.getOrElse("SPARK_VERSION", "3.3.2-aiq44")

enablePlugins(GitVersioning, BuildInfoPlugin)
enablePlugins(PublishToArtifactory)

// Git
git.useGitDescribe := true
git.uncommittedSignifier := Some("DIRTY")

// Build info
buildInfoObject := "SparkS3ShuffleBuild"
buildInfoPackage := "com.ibm"
buildInfoKeys ++= Seq[BuildInfoKey](
  BuildInfoKey.action("buildTime") {
    System.currentTimeMillis
  },
  BuildInfoKey.action("sparkVersion") {
    sparkVersion
  }
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hadoop-cloud" % sparkVersion % "compile",
  )

libraryDependencies ++= (if (scalaBinaryVersion.value == "2.12") Seq(
  "junit" % "junit" % "4.13.2" % Test,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "ch.cern.sparkmeasure" %% "spark-measure" % "0.18" % Test,
  "org.scalacheck" %% "scalacheck" % "1.15.2" % Test,
  "org.mockito" % "mockito-core" % "3.4.6" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
  )
else Seq())

javacOptions ++= Seq("-source", "17", "-target", "17")
javaOptions ++= Seq(
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  "--add-opens=java.base/java.math=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
  "--add-opens=java.base/java.io=ALL-UNNAMED",
  "--add-opens=java.base/java.net=ALL-UNNAMED",
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
  "--add-opens=java.base/sun.security.action=ALL-UNNAMED",
  "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED",
)

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "aiq-artifacts".at("s3://s3-us-east-1.amazonaws.com/aiq-artifacts/releases"),
  "Artifactory".at("https://actioniq.jfrog.io/artifactory/aiq-sbt-local/"),
  DefaultMavenRepository,
  Resolver.mavenLocal,
)

javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
scalacOptions ++= Seq("-deprecation", "-unchecked")

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "_" + sv.binary + "-" + sparkVersion + "_" + module.revision + "." + artifact.extension
}

assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
assembly / assemblyJarName := s"${name.value}_${scalaBinaryVersion.value}-${sparkVersion}_${version}-with-dependencies.jar"
assembly / assemblyOption ~= {
  _.withIncludeScala(false)
}

lazy val lib = (project in file("."))
