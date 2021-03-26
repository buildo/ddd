package ddd

import munit.FunSuite

class ConfirmReservationTest extends FunSuite {
  test("A customer can confirm a reservation for an available seat") {
    val seat = Seat(Row(1), SeatNumber(1))
    confirmReservationCommandHandlerTest(
      given = List(),
      _when = ConfirmReservation(seats = List(seat), screening = Screening()),
      _then = List(ReservationConfirmed(), SeatTaken(Seat(Row(1), SeatNumber(1)))),
    )
  }

  test("A customer cannot confirm a reservation for an unavailable seat") {
    val seat = Seat(Row(1), SeatNumber(1))
    confirmReservationCommandHandlerTest(
      given = List(SeatTaken(seat)),
      _when = ConfirmReservation(seats = List(seat), screening = Screening()),
      _then = List(ReservationFailed(unavailableSeats = List(seat))),
    )
  }

  test("A customer can see available seats") {
    val seat11 = Seat(Row(1), SeatNumber(1))
    val seat12 = Seat(Row(1), SeatNumber(2))
    val events = List(
      ScreeningScheduled(seats = List(seat11, seat12)),
      SeatTaken(seat11),
    )
    val readModel = AvailableSeatsReadModel.create(events)
    val availableSeats = readModel.availableSeats()
    val expectedAvailableSeats = List(seat12)
    assertEquals(availableSeats, expectedAvailableSeats)
  }

  private def confirmReservationCommandHandlerTest(
    given: List[Event],
    _when: ConfirmReservation,
    _then: List[Event],
  ): Unit = {
    val handler = ConfirmReservationHandler.create(given)
    val returnedEvents = handler.handle(_when)
    assertEquals(returnedEvents, _then)
  }
}

sealed trait Event

case class ScreeningScheduled(seats: List[Seat]) extends Event
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

trait AvailableSeatsReadModel {
  def availableSeats(): List[Seat]
}

object AvailableSeatsReadModel {
  def create(events: List[Event]) = new AvailableSeatsReadModel {
    private val _availableSeats = {
      // FIXME this ignores the order of events
      val createdSeats = events.collect { case ScreeningScheduled(seats) => seats }.flatten
      val takenSeats = events.collect { case SeatTaken(s) => s }
      createdSeats.filterNot(takenSeats.contains)
    }
    override def availableSeats(): List[Seat] = _availableSeats
  }
}
