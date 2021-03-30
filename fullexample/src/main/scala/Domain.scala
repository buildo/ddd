package ddd

import java.util.UUID
import java.time.Instant

case class ScreeningId(value: UUID) extends AnyVal

case class ReservationId(value: UUID) extends AnyVal

case class Row(value: Int) extends AnyVal
case class SeatNumber(value: Int) extends AnyVal
case class Seat(row: Row, seatNumber: SeatNumber)

case class TimeInterval(from: Instant, to: Instant)

case class CustomerInfo(name: String, surname: String)

sealed trait Command

object Command {
  case class ReserveSeats(screeningId: ScreeningId, seats: List[Seat]) extends Command
  case class CheckReservationAfter12Minutes(reservationId: ReservationId) extends Command
  case class PayReservation(reservationId: ReservationId, customerInfo: CustomerInfo)
      extends Command
  case class CancelReservationIfNotPaid(reservationId: ReservationId) extends Command
}

sealed trait Event

object Event {
  case class SeatsReserved(
    reservationId: ReservationId,
    screeningId: ScreeningId,
    seats: List[Seat],
  ) extends Event
  case class ReservationPaid(
    reservationId: ReservationId,
    customerInfo: CustomerInfo,
  ) extends Event
  case class TwelveMinutesPassed(
    reservationId: ReservationId,
  ) extends Event
  case class ReservationExpired(
    reservationId: ReservationId,
  ) extends Event
}

sealed trait Query

object Query {
  case class MovieScreenings(inInterval: TimeInterval) extends Query
  case class AvailableSeats(screeningId: ScreeningId) extends Query
  case class Reservation(reservationId: ReservationId) extends Query
}

sealed trait ReservationState
object ReservationState {
  case object Pending extends ReservationState
  case object Paid extends ReservationState
  case object Expired extends ReservationState
}

class Reservation(history: List[Event]) {
  val reservationState: ReservationState =
    if (history.collectFirst { case Event.ReservationExpired(_) => () }.isDefined)
      ReservationState.Expired
    else if (history.collectFirst { case Event.ReservationPaid(_) => () }.isDefined)
      ReservationState.Paid
    else
      ReservationState.Pending

  def reserve(screeningId: ScreeningId, seats: List[Seat]): List[Event] =
    if (checkAvailability(screeningId, seats)) {
      val reservationId = ReservationId(UUID.randomUUID())
      List(Event.SeatsReserved(reservationId, screeningId, seats))
    } else List()

  def pay(reservationId: ReservationId, customerInfo: CustomerInfo): List[Event] =
    List(Event.ReservationPaid(reservationId, customerInfo))

  def cancelIfNotPaid(reservationId: ReservationId): List[Event] =
    if (reservationState == ReservationState.Pending) List()
    else List(Event.ReservationExpired(reservationId))

  private def checkAvailability(screeningId: ScreeningId, seats: List[Seat]) = true
}
