const Booking = require("../models/Booking");
const Car = require("../models/Car");

const createBooking = async (req, res) => {
  try {
    const { carId, startDate, endDate, totalPrice } = req.body;
    const userId = req.user.id;

    // Check if car is available
    const car = await Car.findById(carId);
    if (!car || car.status !== "available") {
      return res.status(400).json({ message: "Car is not available" });
    }

    // Create booking
    const bookingId = await Booking.create({
      userId,
      carId,
      startDate,
      endDate,
      totalPrice,
    });

    // Update car status
    await Car.updateStatus(carId, "booked");

    res
      .status(201)
      .json({ id: bookingId, message: "Booking created successfully" });
  } catch (error) {
    res.status(500).json({ message: "Server error" });
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
