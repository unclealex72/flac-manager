resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Intellij support

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-RC1")

// Release plugin

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.4")
