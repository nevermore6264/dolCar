package com.isdol.carpool.network

import retrofit2.http.*

interface BookingApi {
    @POST("api/bookings")
    suspend fun createBooking(
        @Body bookingRequest: BookingRequest
    ): BookingResponse

    @GET("api/bookings/my-bookings")
    suspend fun getMyBookings(): List<BookingResponse>

    @PUT("api/bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: Int
    ): CancelBookingResponse
}

data class BookingRequest(
    val trip_id: Int,
    val seats_booked: Int
)

data class BookingResponse(
    val id: Int,
    val trip_id: Int,
    val user_id: Int,
    val seats_booked: Int,
    val status: String,
    val booking_date: String,
    val departure_date: String,
    val departure_time: String,
    val price: Double,
    val car_name: String,
    val car_type: String,
    val driver_name: String,
    val driver_phone: String
)

data class CancelBookingResponse(
    val message: String
) 