package petclinic.server.routes

sealed trait AppError extends Throwable

object AppError {

  case object MissingBodyError extends AppError

  case class JsonDecodingError(message: String) extends AppError

}
