package ddd

import java.util.UUID
import munit.FunSuite

class ReservationSpec extends FunSuite {

  val reservationId = ReservationId(UUID.randomUUID)
  val screeningId = ScreeningId(UUID.randomUUID)
  val seats = List(Seat(Row(1), SeatNumber(1)), Seat(Row(1), SeatNumber(2)))
  val customerInfo = CustomerInfo("Name", "Surname")

  test("if a reservation is paid then it does not expire") {
    testCommand(
      _given = List(
        Event.SeatsReserved(reservationId, screeningId, seats),
        Events.ReservationPaid(reservationId, customerInfo),
      ),
      _when = Command.CancelReservationIfNotPaid(reservationId),
      _then = List(),
    )
  }

  test("if a reservation is not paid then it expires in 12 minutes") {
    testCommand(
      _given = List(
        Event.SeatsReserved(reservationId, screeningId, seats),
      ),
      _when = Command.CancelReservationIfNotPaid(reservationId),
      _then = List(ReservationExpired(reservationId)),
    )
  }

  def testCommand(
    _given: List[Event],
    _when: Command,
    _then: List[Event],
  ): Unit = {
    val commandHandler = CommandHandler.create(_ => _given)
    val events = commandHandler.handle(_when)
    // ...
  }

}
