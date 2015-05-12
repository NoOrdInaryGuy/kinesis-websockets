package directives

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server._
import Directives._

import scala.annotation.tailrec

object CustomDirectives {

  def pathToShardKey: Directive1[String] =
    Directive { inner =>
      extractUnmatchedPath { path =>
        val pathString = pathToShardKeyString(path)
        inner(Tuple1(pathString))
      }
    }

  def pathToShardKeyString(path: Uri.Path): String = {

    @tailrec
    def recPathToString(acc: String, path: Uri.Path): String = {
      path match {
        case Path.Slash(tail) => recPathToString(acc, tail)
        case Path.Segment(head, tail) =>
          val newAccumulator = if (acc.isEmpty) head else acc ++ "-" ++ head
          recPathToString(newAccumulator, tail)
        case Path.Empty => acc
      }
    }
    recPathToString("", path)
  }

}
