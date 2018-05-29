package ru.steklopod

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._
import com.tsukaby.bean_validation_scala.ScalaValidatorFactory
import ru.steklopod.entities.{Game, Helper, Player}
import ru.steklopod.repositories.{GameRepository, PlayerRepository}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

trait GameJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  //TODO
  implicit val fieldFormat = new JsonFormat[Helper] {
    override def read(json: JsValue): Helper = Helper.fromString(json.convertTo[String])

    override def write(obj: Helper): JsValue = JsString(obj.toString)
  }

  implicit val gameFormat = jsonFormat9(Game.apply)
  implicit val playerFormat = jsonFormat3(Player.apply)
}


trait Api extends GameJsonSupport /*with WithAuth */ {
  val gameRepository: GameRepository
  val playerRepository: PlayerRepository
  val validator = ScalaValidatorFactory.validator

  val route = {
    pathPrefix("game") {
      path(LongNumber) { id =>
        //        withAuth {
        get {
          parameterMap { paramsMap =>
            onSuccess(gameRepository.getGame(id)) {
              case Some(game) =>
                complete(StatusCodes.OK -> JsObject(game.toJson.asJsObject.fields ++ Map("params" -> paramsMap.toJson)))
              case None =>
                complete(StatusCodes.NotFound)
            }
          }
        }
        // }
      }
    } ~ post {
      entity(as[Game]) { game =>
        complete {
          gameRepository.createGame(game).map(_ => StatusCodes.OK)
        }
      }
    }

    pathPrefix("user") {
      post {
        entity(as[Player]) {
          player =>
            val violations = validator.validate(player)
            if (violations.nonEmpty) {
              complete(StatusCodes.BadRequest) // “username” must be greater then 3 and less then 20 characters"
            } else {
              complete(playerRepository.createPlayer(player).map(_ => StatusCodes.OK))
            }
        }
      }
    }
  }
}


//Todo
trait WithAuth {
  def withAuth: Directive0 = optionalHeaderValueByName("auth").flatMap {
    case Some(k) if k == "123" => pass
    case _ => complete(StatusCodes.Unauthorized)
  }
}

