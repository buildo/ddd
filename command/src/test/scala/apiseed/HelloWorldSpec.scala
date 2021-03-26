package ddd

import munit.FunSuite

class ConfirmReservationTest extends FunSuite {
  test("A customer can confirm a reservation for an available seat") {
    val seat = Seat(Row(1), SeatNumber(1))
    val confirmReservation = ConfirmReservation(seats = List(seat), screening = Screening())
    val availableSeats = List(seat)
    val handler = ConfirmReservationHandler.create(availableSeats)
    val result = handler.handle(confirmReservation)
    val expectedResult = Right(ReservationReceipt())
    assertEquals(result, expectedResult)
  }

  test("A customer cannot confirm a reservation for an unavailable seat") {
    val seat = Seat(Row(1), SeatNumber(1))
    val confirmReservation = ConfirmReservation(seats = List(seat), screening = Screening())
    val availableSeats = List()
    val handler = ConfirmReservationHandler.create(availableSeats)
    val result = handler.handle(confirmReservation)
    val expectedResult = Left(UnavailableSeats(List(seat)))
    assertEquals(result, expectedResult)
  }
}

// can make constructor private to enforce valid data
case class Row(value: Int) extends AnyVal
case class SeatNumber(value: Int) extends AnyVal

case class Seat(row: Row, seatNumber: SeatNumber)

case class Screening()

case class ConfirmReservation(
  seats: List[Seat],
  screening: Screening,
)

case class UnavailableSeats(seats: List[Seat])

case class ReservationReceipt()

trait ConfirmReservationHandler {
  def handle(confirmReservation: ConfirmReservation): Either[UnavailableSeats, ReservationReceipt]
}

object ConfirmReservationHandler {
  def create(availableSeats: List[Seat]) = new ConfirmReservationHandler {
    override def handle(
      confirmReservation: ConfirmReservation,
    ): Either[UnavailableSeats, ReservationReceipt] = {
      val unavailableSeats =
        confirmReservation.seats.filterNot(seat => availableSeats.contains(seat))
      if (unavailableSeats.nonEmpty)
        Left(UnavailableSeats(List(unavailableSeats)))
      else
        Right(ReservationReceipt())
    }
  }
}
