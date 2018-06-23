import Dependencies._

val geotoolsVersion = "18.2"

resolvers ++= Seq(
  "java3d" at "http://maven.geotoolkit.org/",
  "geosolutions" at "http://maven.geo-solutions.it/",
  "osgeo" at "http://download.osgeo.org/webdav/geotools/",
  "maven" at "http://central.maven.org/maven2/"
)

val geotools: Seq[ModuleID] = Seq(
  "org.geotools" % "geotools" % geotoolsVersion,
  "org.geotools" % "gt-cql" % geotoolsVersion,
  "org.geotools" % "gt-geotiff" % geotoolsVersion,
  "org.geotools" % "gt-image" % geotoolsVersion,
  "org.geotools" % "gt-epsg-hsql" % geotoolsVersion,
  "org.geotools" % "gt-referencing" % geotoolsVersion,
  "org.geotools" % "gt-shapefile" % geotoolsVersion,
  "org.geotools" % "gt-jp2k" % geotoolsVersion,
  "org.geotools" % "gt-imageio-ext-gdal" % geotoolsVersion,
  "org.geotools.jdbc" % "gt-jdbc-postgis" % geotoolsVersion,
  // JAI extensions
  "org.jaitools" % "jt-vectorize" % "1.4.0",
  "javax.media" % "jai_core" % "1.1.3",
  "com.github.jai-imageio" % "jai-imageio-core" % "1.3.1",
  "it.geosolutions.imageio-ext" % "imageio-ext" % "1.1.20",
  "it.geosolutions.imageio-ext" % "imageio-ext-tiff" % "1.1.20"
)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.escalatesoft",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "scalatype",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    ) ++ geotools
  )
