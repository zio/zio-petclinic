package petclinic.server

/** Here we have defined our own error type, called AppError, which is a subtype
  * of Throwable. The purpose of this is to make errors more descriptive and
  * easier to understand and therefore easier handle.
  */
sealed trait AppError extends Throwable

object AppError {

  case object MissingBodyError extends AppError

  final case class JsonDecodingError(message: String) extends AppError

  final case class InvalidIdError(message: String) extends AppError

}
