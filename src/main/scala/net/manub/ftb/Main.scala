package net.manub.ftb

import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

object Main extends TwitterServer {

  case class Todo(title: String)

  val getTodo: Endpoint[List[Todo]] = get(/) {
    Ok(List.empty[Todo])
  }

  val postTodo: Endpoint[Todo] = post(/ :: jsonBody[Todo]) { todo: Todo =>
    Ok(todo)
  }

  val deleteTodo: Endpoint[String] = delete(/) {
    Ok("deleted")
  }

  def main(): Unit = {

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
