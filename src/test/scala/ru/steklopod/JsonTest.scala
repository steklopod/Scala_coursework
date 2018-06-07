package ru.steklopod

import org.scalatest.FunSuite
import ru.steklopod.entities.{Game, Helper}
import spray.json.{DefaultJsonProtocol, _}

trait MyJsonProtocol extends DefaultJsonProtocol {
  implicit val gameFormat = new JsonWriter[Game] {
    def write(g: Game): JsValue = {
      JsObject(
        "id" ->  g.id.toJson,
        "next_step" -> JsNumber(g.nextStep),
        "won" -> g.won.toJson,
        "finished" -> JsBoolean(g.finished),
        "players" -> JsString(g.players),
        "steps" -> JsNumber(g.steps),
        "size" -> JsString(g.size),
        "crosses_length_to_win" -> JsNumber(g.crossesLengthToWin),
        "field" -> JsString(g.fieldPlay)
      )
    }
  }
}

class JsonTest extends FunSuite with MyJsonProtocol {
  test("JSON") {
    val game = new Game(1, None, false, "1, 2", 0, Helper.ThreeByThree.toString, 3, "0, 0, 0, 0, 0, 0, 0, 0, 0")
    val marshalled = game.toJson
    println(marshalled)
  }

}
