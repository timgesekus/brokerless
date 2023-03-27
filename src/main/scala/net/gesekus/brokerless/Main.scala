package net.gesekus.brokerless

import org.slf4j.LoggerFactory
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.{Server, ServerConfig, HttpApp}
import net.gesekus.brokerless.service.TrackRepo

object Main extends ZIOAppDefault:
  val log = LoggerFactory.getLogger(ZioHttpInterpreter.getClass.getName)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    val serverOptions: ZioHttpServerOptions[Any] =
      ZioHttpServerOptions.customiseInterceptors
        .serverLog(
          DefaultServerLog[Task](
            doLogWhenReceived = msg => ZIO.succeed(log.debug(msg)),
            doLogWhenHandled = (msg, error) => ZIO.succeed(error.fold(log.debug(msg))(err => log.debug(msg, err))),
            doLogAllDecodeFailures = (msg, error) => ZIO.succeed(error.fold(log.debug(msg))(err => log.debug(msg, err))),
            doLogExceptions = (msg: String, ex: Throwable) => ZIO.succeed(log.debug(msg, ex)),
            noLog = ZIO.unit
          )
        )
       // .metricsInterceptor(Endpoints.prometheusMetrics.metricsInterceptor())
        .options
    val app: HttpApp[TrackRepo, Throwable] = ZioHttpInterpreter(serverOptions).toHttp(Endpoints.businessEndpoints)
    val supportApp = ZioHttpInterpreter(serverOptions).toHttp(Endpoints.supportEndpoints)
    val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)

    (
      for
        actualPort <- Server.install(app.withDefaultErrorResponse ++ supportApp.withDefaultErrorResponse)
        _ <- Console.printLine(s"Go to http://localhost:${actualPort}/docs to open SwaggerUI. Press ENTER key to exit.")
        _ <- Console.readLine
      yield ()
    ).provide(
      ServerConfig.live(ServerConfig.default.port(port)),
      Server.live,
      TrackRepo.layer
    ).exitCode
