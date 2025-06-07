const express = require("express");
const router = express.Router();
const {
  searchTrips,
  getTripDetails,
  getPopularTrips,
} = require("../controllers/tripController");
const { auth } = require("../middleware/auth");

// Public routes
router.get("/search", searchTrips);
router.get("/popular", getPopularTrips);
router.get("/:id", getTripDetails);

module.exports = router;
