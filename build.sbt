name := "siigna-base"

version := "nightly"

organization := "com.siigna"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.2", "2.10.0")

scalaSource in Compile <<= (baseDirectory in Compile)(_ / "src")

//publishTo := Some(Resolver.file("file",  new File( "../rls" )) )
publishTo := Some(Resolver.sftp("Siigna rls", "rls.siigna.com", 22, "/srv/rls") as ("siigna", new File("../budapest/jenkins.rsa")))

resolvers += "Siigna" at "http://rls.siigna.com"

libraryDependencies ++= Seq(
  "com.siigna" %% "siigna-main" % "nightly"
)
