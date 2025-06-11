const express = require("express");
const router = express.Router();
const {
  createBooking,
  getUserBookings,
  getBookingById,
  updateBookingStatus,
} = require("../controllers/bookingController");
const { auth } = require("../middleware/auth");

router.post("/", auth, createBooking);
router.get("/my-bookings", auth, getUserBookings);
router.get("/:id", auth, getBookingById);
router.patch("/:id/status", auth, updateBookingStatus);

module.exports = router;
