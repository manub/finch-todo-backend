package net.manub.ftb

import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import doobie.imports._
import cats._, cats.data._, cats.implicits._
import fs2.interop.cats._
import DatabaseOperations._
object Main extends TwitterServer {

  private val xa = DriverManagerTransactor[IOLite](
    "org.postgresql.Driver", "jdbc:postgresql:postgres", "postgres", "password"
  )

  val getTodo: Endpoint[List[Todo]] = get(/) {
    Ok(allTodos.list.transact(xa).unsafePerformIO)
  }

  val postTodo: Endpoint[PostedTodo] = post(/ :: jsonBody[PostedTodo]) { todo: PostedTodo =>
    insert(todo).run.transact(xa).unsafePerformIO
    Ok(todo)
  }

  val deleteTodo: Endpoint[String] = delete(/) {
    deleteAllTodos.run.transact(xa).unsafePerformIO
    Ok("deleted")
  }

  def main(): Unit = {

    dropAndCreateTable.transact(xa).unsafePerformIO

    val api = getTodo :+: postTodo :+: deleteTodo

    val policy = Cors.Policy(
      allowsOrigin = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST", "OPTION", "DELETE")),
      allowsHeaders = _ => Some(Seq("Content-Type", "Origin", "Accept"))
    )

    val filter = new Cors.HttpFilter(policy)
    val server = Http.server
      .configured(Stats(statsReceiver))
      .serve(":8081", filter.andThen(api.toService))

    onExit(server.close())

    Await.ready(adminHttpServer)
  }
}
