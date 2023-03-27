package  net.gesekus.brokerless.service

import zio._
import ZIO.succeed
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Track(id: Int, x: Int, y: Int)

trait TrackRepo {
  def getAll(): ZIO[Any, Nothing, List[Track]]
}

case class TrackRepoImpl() extends TrackRepo {
  def getAll(): ZIO[Any, Nothing, List[Track]] = succeed(
    List(
      Track(1, 3, 4),
      Track(2, 3, 5),
      Track(3, 2, 3)
    )
  )
}

object TrackRepo:
  val layer = ZLayer.succeed(TrackRepoImpl())
  def getAll(): ZIO[TrackRepo, Nothing, List[Track]] =
    ZIO.serviceWithZIO[TrackRepo](_.getAll())
  given trackDecoder: JsonDecoder[Track] = DeriveJsonDecoder.gen[Track]
  given trackEncoder: JsonEncoder[Track] = DeriveJsonEncoder.gen[Track]
