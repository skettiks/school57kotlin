package ru.tbank.education.school.homework

/**
 * Исключение, которое выбрасывается при попытке забронировать занятое место
 */
class SeatAlreadyBookedException(message: String) : Exception(message)

/**
 * Исключение, которое выбрасывается при попытке забронировать место при отсутствии свободных мест
 */
class NoAvailableSeatException(message: String) : Exception(message)

data class BookedSeat(
    val movieId: String, // идентификатор фильма
    val seat: Int, // номер места
    var isBooked: Boolean = false // Забронировано ли место или нет
)

class MovieBookingService(
    private val maxQuantityOfSeats: Int // Максимальное кол-во мест
) {
    init {
        require(maxQuantityOfSeats > 0) {
            "Максимальное количество мест должно быть положительным"
        }
    }

    private val bookings = mutableListOf<BookedSeat>()

    /**
     * Бронирует указанное место для фильма.
     *
     * @param movieId идентификатор фильма
     * @param seat номер места
     * @throws IllegalArgumentException если номер места вне допустимого диапазона
     * @throws NoAvailableSeatException если нет больше свободных мест
     * @throws SeatAlreadyBookedException если место уже забронировано
     */
    fun bookSeat(movieId: String, seat: Int) {
        if (seat < 1 || seat > maxQuantityOfSeats) {
            throw IllegalArgumentException("Номер места должен быть от 1 до $maxQuantityOfSeats")
        }

        val existingBooking = bookings.find { it.movieId == movieId && it.seat == seat }
        val bookedSeatsCount = bookings.count { it.movieId == movieId && it.isBooked }

        if (bookedSeatsCount >= maxQuantityOfSeats) {
            throw NoAvailableSeatException("Нет свободных мест для фильма $movieId")
        }

        if (existingBooking != null) {
            if (existingBooking.isBooked) {
                throw SeatAlreadyBookedException("Место $seat уже забронировано для фильма $movieId")
            } else {
                existingBooking.isBooked = true
            }
        } else {
            bookings.add(BookedSeat(movieId, seat, true))
        }
    }

    /**
     * Отменяет бронь указанного места.
     *
     * @param movieId идентификатор фильма
     * @param seat номер места
     * @throws NoSuchElementException если место не было забронировано
     */
    fun cancelBooking(movieId: String, seat: Int) {
        val existingBooking = bookings.find { it.movieId == movieId && it.seat == seat }
            ?: throw NoSuchElementException("Место $seat не было забронировано для фильма $movieId")

        if (!existingBooking.isBooked) {
            throw NoSuchElementException("Бронь места $seat уже отменена для фильма $movieId")
        }

        existingBooking.isBooked = false
    }

    /**
     * Проверяет, забронировано ли место
     *
     * @return true если место занято, false иначе
     */
    fun isSeatBooked(movieId: String, seat: Int): Boolean {
        return bookings.any { it.movieId == movieId && it.seat == seat && it.isBooked }
    }
}