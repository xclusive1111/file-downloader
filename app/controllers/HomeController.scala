package controllers

import java.io.File
import javax.inject._

import controllers.Conf.tmpDir
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.Try
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: MessagesControllerComponents, conf: Configuration)(implicit assetsFinder: AssetsFinder, ex: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val downloadForm = Form (
    mapping(
      "file" -> nonEmptyText
    )(DownloadForm.apply)(DownloadForm.unapply)
  )

  def download = Action(parse.form(downloadForm)) { implicit request =>
    val file = request.body.file
    val path = s"$tmpDir${File.separator}$file"
    println("Downloading from: " + path)
    Try (
      Ok.sendFile(
        content= new File(path),
        inline = false
      )
    ).getOrElse(Ok("Unable to download file " + file))
  }

  def index = Action { implicit request: MessagesRequest[AnyContent] =>
    val url = conf.get[String]("host") + ":" + conf.get[String]("port") + "/ws"
    Ok(views.html.main(url, downloadForm))
  }
}

case class DownloadForm(file: String)
