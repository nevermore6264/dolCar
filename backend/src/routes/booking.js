const express = require("express");
const router = express.Router();
const Booking = require("../models/Booking");
const Trip = require("../models/Trip");
const auth = require("../middleware/auth");

// Đặt xe
router.post("/", auth, async (req, res) => {
  try {
    const { trip_id, seats_booked } = req.body;
    const user_id = req.user.id;

    // Kiểm tra chuyến xe có tồn tại không
    const trip = await Trip.getById(trip_id);
    if (!trip) {
      return res.status(404).json({ message: "Không tìm thấy chuyến xe" });
    }

    // Kiểm tra số ghế còn trống
    if (trip.available_seats < seats_booked) {
      return res.status(400).json({ message: "Số ghế còn trống không đủ" });
    }

    // Tạo booking mới
    const booking = await Booking.create({
      trip_id,
      user_id,
      seats_booked,
      status: "pending",
      booking_date: new Date(),
    });

    // Cập nhật số ghế trống
    await Trip.updateAvailableSeats(
      trip_id,
      trip.available_seats - seats_booked
    );

    res.status(201).json(booking);
  } catch (error) {
    console.error("Error creating booking:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
});

// Lấy danh sách đặt xe của user
router.get("/my-bookings", auth, async (req, res) => {
  try {
    const user_id = req.user.id;
    const bookings = await Booking.getByUserId(user_id);
    res.json(bookings);
  } catch (error) {
    console.error("Error getting user bookings:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
});

// Hủy đặt xe
router.put("/:id/cancel", auth, async (req, res) => {
  try {
    const booking_id = req.params.id;
    const user_id = req.user.id;

    // Kiểm tra booking có tồn tại và thuộc về user không
    const booking = await Booking.getById(booking_id);
    if (!booking || booking.user_id !== user_id) {
      return res.status(404).json({ message: "Không tìm thấy đặt xe" });
    }

    // Kiểm tra trạng thái booking
    if (booking.status !== "pending") {
      return res.status(400).json({ message: "Không thể hủy đặt xe này" });
    }

    // Cập nhật trạng thái booking
    await Booking.updateStatus(booking_id, "cancelled");

    // Cập nhật lại số ghế trống
    const trip = await Trip.getById(booking.trip_id);
    await Trip.updateAvailableSeats(
      booking.trip_id,
      trip.available_seats + booking.seats_booked
    );

    res.json({ message: "Hủy đặt xe thành công" });
  } catch (error) {
    console.error("Error cancelling booking:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
});

module.exports = router;
