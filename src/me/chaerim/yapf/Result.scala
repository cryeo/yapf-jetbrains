package me.chaerim.yapf

abstract class Result(val code: Int,
                      val message: String,
                      val detail: Option[String] = None,
                      val shouldNotify: Boolean  = true)

object Result {
  case object NotFoundExecutable   extends Result(1000, "YAPF executable is not found")
  case object UnformattableFile    extends Result(1001, "File is unformattable")
  case object AlreadyFormattedCode extends Result(1002, "Code is already formatted", shouldNotify = false)

  case class IllegalYapfResult(override val detail: Option[String])
      extends Result(2000, "YAPF result is illegal", detail)
  case class FailedToRunCommand(override val detail: Option[String])
      extends Result(3000, "Failed to run command", detail)
}
