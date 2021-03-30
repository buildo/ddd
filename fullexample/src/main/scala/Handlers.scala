package ddd

trait CommandHandler {
  handle(command: Command): List[Event]
}

object CommandHandler {
  def create(allEventsFromSource: ReservationId => List[Event]): CommandHandler =
    new CommandHandler {
      override def handle(command: Command) = command match {
        case Command.ReserveSeats(screeningId, seats) => {
          // here we should check that seats are available using a Screening aggregate?
          val reservation = new Reservation(List())
          val events = reservation.reserve(screeningId, seats)
          events
        }
        case Command.CheckReservationAfter12Minutes(reservationId) => {
          // not modelling "scheduler" external system for now
          List(Event.TwelveMinutesPassed(reservationId))
        }
        case Command.PayReservation(reservationId, customerInfo) => {
          val reservation = new Reservation(allEventsFromSource(reservationId))
          val events = reservation.pay(reservationId, customerInfo)
          events
        }
        case Command.CancelReservationIfNotPaid(reservationId) => {
          val reservation = new Reservation(allEventsFromSource(reservationId))
          val events = reservation.cancelIfNotPaid(reservationId, customerInfo)
          events
        }
      }
    }
}
