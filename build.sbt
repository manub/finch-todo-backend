name := "finch-todo-backend"

organization := "net.manub"

scalaVersion := "2.12.1"

val finchVersion = "0.13.1"
val doobieVersion = "0.4.1"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core"           % finchVersion,
  "com.github.finagle" %% "finch-circe"          % finchVersion,
  "org.tpolecat"       %% "doobie-core-cats"     % doobieVersion,
  "org.tpolecat"       %% "doobie-postgres-cats" % doobieVersion,
  "io.circe"           %% "circe-generic"        % "0.7.0",
  "com.twitter"        %% "twitter-server"       % "1.27.0",
  "com.github.finagle" %% "finch-test"           % finchVersion % Test
)
