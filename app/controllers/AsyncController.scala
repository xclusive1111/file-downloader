package controllers

import javax.inject._

import akka.actor.ActorSystem
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.Try

@Singleton
class AsyncController @Inject()
  (cc: ControllerComponents, actorSystem: ActorSystem, ws: WSClient)
  (implicit exec: ExecutionContext)  extends AbstractController(cc) {

  def download(link: String, timeout: Int) = Action.async {
    println("Downloading: " + link + " with timeout " + timeout)
    val filename = link.split("/").lastOption.getOrElse("download.zip")
    println("Saving to: " + filename)
    Try(
      ws.url(link)
        .withRequestTimeout(timeout.millis)
        .execute()
        .map(res => Ok.chunked(res.bodyAsSource)
          .withHeaders(
            CONTENT_TYPE -> "application/x-download",
            CONTENT_DISPOSITION -> s"attachment; filename=$filename"
          )
        )
    ).getOrElse({Future.apply(Ok(s"Unable to download $link"))})
  }
}
