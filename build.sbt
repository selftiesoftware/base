name := "siigna-base"

version := "stable"

organization := "com.siigna"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.2", "2.10.0")

scalaSource in Compile <<= (baseDirectory in Compile)(_ / "src")

//publishTo := Some(Resolver.file("file",  new File( "../rls" )) )
publishTo := Some(Resolver.sftp("Siigna rls", "80.71.132.98", 12022, "/var/www/public_html") as ("www-data", new File("../budapest/jenkins.rsa")))

resolvers += "Siigna" at "http://rls.siigna.com"

libraryDependencies ++= Seq(
  "com.siigna" %% "siigna-main" % "stable"
)
