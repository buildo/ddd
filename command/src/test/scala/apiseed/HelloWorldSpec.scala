package ddd

import munit.FunSuite

class ConfirmReservationTest extends FunSuite {
  test("A customer can confirm a reservation for an available seat") {
    val seat = Seat(Row(1), SeatNumber(1))
    val confirmReservation = ConfirmReservation(seats = List(seat), screening = Screening())
    val events = List()
    val handler = ConfirmReservationHandler.create(events)
    val returnedEvents = handler.handle(confirmReservation)
    val expectedEvents = List(
      ReservationConfirmed(),
      SeatTaken(Seat(Row(1), SeatNumber(1))),
    )
    assertEquals(returnedEvents, expectedEvents)
  }

  test("A customer cannot confirm a reservation for an unavailable seat") {
    val seat = Seat(Row(1), SeatNumber(1))
    val confirmReservation = ConfirmReservation(seats = List(seat), screening = Screening())
    val events = List(SeatTaken(seat))
    val handler = ConfirmReservationHandler.create(events)
    val returnedEvents = handler.handle(confirmReservation)
    val expectedEvents = List(ReservationFailed(unavailableSeats = List(seat)))
    assertEquals(returnedEvents, expectedEvents)
  }
}

sealed trait Event

case class SeatTaken(seat: Seat) extends Event
case class ReservationConfirmed() extends Event
case class ReservationFailed(unavailableSeats: List[Seat]) extends Event

// can make constructor private to enforce valid data
case class Row(value: Int) extends AnyVal
case class SeatNumber(value: Int) extends AnyVal

case class Seat(row: Row, seatNumber: SeatNumber)

case class Screening()

case class ConfirmReservation(
  seats: List[Seat],
  screening: Screening,
)

trait ConfirmReservationHandler {
  def handle(events: ConfirmReservation): List[Event]
}

object ConfirmReservationHandler {
  def create(events: List[Event]) = new ConfirmReservationHandler {
    override def handle(
      confirmReservation: ConfirmReservation,
    ): List[Event] = {
      val availableSeats = AvailableSeats.create(events)
      val unavailableSeatsInReservation =
        confirmReservation.seats.filterNot(availableSeats.seatIsAvailable)
      if (unavailableSeatsInReservation.nonEmpty)
        List(ReservationFailed(unavailableSeats = unavailableSeatsInReservation))
      else
        ReservationConfirmed() :: confirmReservation.seats.map(s => SeatTaken(s))
    }
  }
}

trait AvailableSeats {
  def seatIsAvailable(seat: Seat): Boolean
}

object AvailableSeats {
  def create(events: List[Event]) = new AvailableSeats {
    private val takenSeats = events.collect { case SeatTaken(s) => s }
    override def seatIsAvailable(seat: Seat): Boolean = !takenSeats.contains(seat)
  }
}
