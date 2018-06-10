package ru.steklopod

import org.scalatest.FunSuite
import ru.steklopod.entities.Game
import ru.steklopod.repositories.{DBGameRepository, GameDb}
import ru.steklopod.util.Helper
import ru.steklopod.util.MyJsonProtocol._
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._


class JsonTest extends FunSuite {

  test("Vector[Int] => String => Vector[Int]") {
    val sizeVector = Vector(3, 4)

    val strArraySize: String = Helper.vectorToStr(sizeVector)
    println(strArraySize)

    val vectorFomStr: Vector[Int] = Helper.strToVector(strArraySize)

    println(vectorFomStr)

  }


  test("JSON read") {
    val source =
      """
    {
      "opponent": "vasya",
      "size": [3,3],
      "first_step_by": "vasya",
      "crosses_length_to_win": 3
    }
               """
    val jsonAst = source.parseJson // or JsonParser(source)
    val game = jsonAst.convertTo[Game]
    println(game)
  }

  test("JSON write") {
    GameDb.init()

    val sizeOfField = Vector(3, 3)
    val game = new Game(
      "Vasya", None, false,
      "Vasya, Nagibator", 0,
      sizeOfField, 3,
      Helper.makeFieldFromSize(sizeOfField)
    )
    val gameFromDB = Await.result(DBGameRepository.getGame(1L), 2 second).get

    val marshalled = game.toJson
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>NEW")
    println(marshalled)
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

    val marshalledFromDb = gameFromDB.toJson
    println("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<DB")
    println(marshalledFromDb)
    println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
  }

}


