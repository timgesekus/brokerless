package net.gesekus.brokerless

import sttp.tapir.*

import sttp.tapir.Codec.JsonCodec
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.Task
import zio.RIO
import zio.ZIO
import service.TrackRepo.{given, *}
import service.Track
import net.gesekus.brokerless.service.TrackRepo
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full
import sttp.tapir.ztapir.ZPartialServerEndpoint
import sttp.tapir.ztapir.RichZServerEndpoint

object Endpoints:
  
  val trackListing: PublicEndpoint[Unit, Unit, List[Track], Any] = endpoint.get
    .in("track")
    .out(jsonBody[List[Track]])
  val trackListingServerEndpoint: ZServerEndpoint[TrackRepo,Any]  = trackListing.serverLogicSuccess( _ => getAll())

  val apiEndpoints = List(trackListingServerEndpoint)
  val documentEndpoint = List(trackListing)
  val docEndpoinList = SwaggerInterpreter()
   .fromEndpoints[Task](documentEndpoint, "brokerless", "1.0.0")

  val prometheusMetrics: PrometheusMetrics[Task] = PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] = prometheusMetrics.metricsEndpoint

  val supportEndpoints = docEndpoinList ++ List(metricsEndpoint)
  val businessEndpoints: List[ZServerEndpoint[TrackRepo , Any]] = apiEndpoints


