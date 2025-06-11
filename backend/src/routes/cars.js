const express = require("express");
const router = express.Router();
const {
  createCar,
  getAllCars,
  getCarById,
  updateCarStatus,
} = require("../controllers/carController");
const { auth } = require("../middleware/auth");

router.get("/", getAllCars);
router.get("/:id", getCarById);
router.post("/", auth, createCar);
router.patch("/:id/status", auth, updateCarStatus);

module.exports = router;
