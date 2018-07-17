package controllers

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import java.net.URI
import scala.util.Try

import sys.process._

class DownloadingActor(out: ActorRef) extends Actor {

  override def receive: Receive = {
    case msg: String =>
      download(msg, out)
  }

  def download(link: String, logger: ActorRef) = {
    val captureLog = (log: String) => logger ! log

    "which wget".lineStream
      .headOption
      .foreach(cmd => {
        Try {
         val url = new URI(link).toURL.toString
         url.split("/").lastOption
          .map(_.replaceAll("\\s+", ""))
          .foreach { filename =>
            println(s"Saving to ${Conf.tmpDir}/$filename")
            val command = s"$cmd --no-check-certificate -t 3 -c -O ${Conf.tmpDir}${File.separator}$filename $link"
            println("Executing: " + command)
            val exitCode = command ! ProcessLogger(captureLog, captureLog)

            if (exitCode == 0) captureLog(s"Downloaded:$filename")
            else captureLog(s"Unable to download from $link")
          }
        }.getOrElse {
          captureLog("Invalid URL: " + link)
        }
      })
  }
}

object DownloadingActor {
  def props(out: ActorRef) = Props(new DownloadingActor(out))
}
