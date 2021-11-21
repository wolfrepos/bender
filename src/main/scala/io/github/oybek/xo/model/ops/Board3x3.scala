package io.github.oybek.xo.model.ops

import io.github.oybek.xo.model.Outcome.{Draw, Owin, Xwin}
import io.github.oybek.xo.model.XO.{O, X}
import io.github.oybek.xo.model.{Board, Coord, Outcome, XO}

import scala.language.implicitConversions

object Board3x3 {
  implicit val ops: BoardOps[Board] = new BoardOps[Board] {
    override def put(board: Board, coord: Coord, turn: XO): Either[String, Board] =
      () match {
        case _ if board.xs.contains(coord)   => Left("Клетка занята братан")
        case _ if board.os.contains(coord)   => Left("Клетка занята братан")
        case _ if coord.x < 0 || coord.x > 2 => Left("Нет такой клетки братан")
        case _ if coord.y < 0 || coord.y > 2 => Left("Нет такой клетки братан")
        case _ if turn == X                  => Right(Board(coord::board.xs, board.os))
        case _ if turn == O                  => Right(Board(board.xs, coord::board.os))
      }

    override def outcome(board: Board): Option[Outcome] = {
      val xWin = winCoords(board.xs)
      val oWin = winCoords(board.os)
      val busyCells = board.xs.length + board.os.length
      if (xWin) Some(Xwin)
      else if (oWin) Some(Owin)
      else if (busyCells == 9) Some(Draw)
      else None
    }

    override def turn(board: Board): Option[XO] =
      if (freeCells(board).isEmpty) None
      else if (board.xs.length == board.os.length) Some(X)
      else Some(O)

    override def freeCells(board: Board): List[Coord] =
      List
        .tabulate(3, 3)(Coord)
        .flatten
        .filterNot(c => board.xs.contains(c) || board.os.contains(c))

    private def winCoords(coords: List[Coord]): Boolean =
      coords.combinations(3).exists { xs =>
        xs.forall(_.x == xs.head.x) ||
          xs.forall(_.y == xs.head.y) ||
          xs.toSet == Set(Coord(0, 0), Coord(1, 1), Coord(2, 2)) ||
          xs.toSet == Set(Coord(0, 2), Coord(1, 1), Coord(2, 0))
      }
  }
}
