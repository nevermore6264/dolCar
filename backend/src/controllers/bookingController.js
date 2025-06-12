const Booking = require("../models/Booking");
const Car = require("../models/Car");
const Trip = require("../models/Trip");

const createBooking = async (req, res) => {
  try {
    console.log("--- Bắt đầu createBooking ---");
    const { trip_id, seats_booked, pickup_location, dropoff_location } =
      req.body;
    console.log("Body:", req.body);
    const user_id = req.user.id;
    console.log("User ID:", user_id);

    const trip = await Trip.getById(trip_id);
    console.log("Trip:", trip);
    if (!trip) {
      console.log("Không tìm thấy chuyến xe");
      return res.status(404).json({ message: "Không tìm thấy chuyến xe" });
    }
    if (!trip.car_id) {
      console.log("Chuyến xe không có car_id");
      return res.status(400).json({ message: "Chuyến xe không có car_id" });
    }
    const car = await Car.findById(trip.car_id);
    console.log("Car:", car);
    if (!car || car.status !== "available") {
      console.log("Car is not available");
      return res.status(400).json({ message: "Car is not available" });
    }

    // Create booking
    const booking = await Booking.create({
      trip_id: trip.id,
      user_id: user_id,
      seats_booked: seats_booked,
      status: "pending",
      total_price: Number(trip.price) * Number(seats_booked),
      pickup_location: pickup_location,
      dropoff_location: dropoff_location,
      created_at: new Date(),
      updated_at: new Date(),
    });
    console.log("Booking đã tạo:", booking);

    // Update car status
    await Car.updateStatus(trip.car_id, "booked");

    console.log("--- Đã qua tất cả kiểm tra, chuẩn bị tạo booking ---");

    res.status(201).json(booking);
  } catch (error) {
    console.error("Error in createBooking:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
};

const getUserBookings = async (req, res) => {
  try {
    const bookings = await Booking.findByUserId(req.user.id);
    res.json(bookings);
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

const getBookingById = async (req, res) => {
  try {
    const booking = await Booking.findById(req.params.id);
    if (!booking) {
      return res.status(404).json({ message: "Booking not found" });
    }

    // Check if user is authorized to view this booking
    if (booking.user_id !== req.user.id && req.user.role !== "admin") {
      return res.status(403).json({ message: "Not authorized" });
    }

    res.json(booking);
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

const updateBookingStatus = async (req, res) => {
  try {
    const { status } = req.body;
    const booking = await Booking.findById(req.params.id);

    if (!booking) {
      return res.status(404).json({ message: "Booking not found" });
    }

    // Update booking status
    const success = await Booking.updateStatus(req.params.id, status);
    if (!success) {
      return res
        .status(400)
        .json({ message: "Failed to update booking status" });
    }

    // If booking is cancelled or completed, update car status
    if (status === "cancelled" || status === "completed") {
      await Car.updateStatus(booking.car_id, "available");
    }

    res.json({ message: "Booking status updated successfully" });
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

module.exports = {
  createBooking,
  getUserBookings,
  getBookingById,
  updateBookingStatus,
};
