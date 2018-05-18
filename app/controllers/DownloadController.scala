package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer

@Singleton
class DownloadController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  def ws = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out =>
      DownloadingActor.props(out)
    }
  }
}
