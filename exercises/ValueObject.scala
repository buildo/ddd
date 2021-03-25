case class Movie(
  title: String, // can introduce MovieTitle and similar below
  year: Int,
)

@enum trait TicketType {
  object Adult
  object Student
  object Child
}

case class Screening(
  startsAt: Instant,
  room: Room,
)

case class Seat(
  row: Int,
  column: Int,
)

case class Money(
  amount: BigDecimal,
  currency: Currency,
)

case class Cinema(
  // ...
)

// should be an entity?
case class Ticket(
  movie: Movie,
  screening: Screening,
  seat: Seat,
  price: Money, // clarify relationship with ticket type
  ticketType: TicketType,
)

case class Reservation(
  customer: Customer,
  cinema: Cinema,
  tickets: List[Ticket],
  totalPrice: Money,
  expiresAt: Instant,
  confirmedAt: Instant,
)
