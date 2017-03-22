package net.manub.ftb

import doobie.imports._
import cats._, cats.data._, cats.implicits._
import fs2.interop.cats._

object DatabaseOperations {

  val drop: Update0 = sql"DROP TABLE IF EXISTS todo".update

  val create: Update0 =
    sql"""
          CREATE TABLE todo (
            id    SERIAL  PRIMARY KEY,
            title VARCHAR,
            completed BOOLEAN
          )
      """.update

  def insert(todo: PostedTodo): Update0 = sql"""INSERT INTO todo (title, completed) VALUES (${todo.title}, FALSE)""".update

  val allTodos: Query0[Todo] = sql"""SELECT title, completed from todo""".query[Todo]

  val deleteAllTodos: Update0 = sql"""DELETE FROM todo""".update

  val dropAndCreateTable: ConnectionIO[Int] = drop.run *> create.run

}
