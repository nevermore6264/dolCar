const express = require("express");
const router = express.Router();
const {
  createCar,
  getAllCars,
  getCarById,
  updateCarStatus,
} = require("../controllers/carController");
const { auth, isAdmin } = require("../middleware/auth");

router.get("/", getAllCars);
router.get("/:id", getCarById);
router.post("/", auth, isAdmin, createCar);
router.patch("/:id/status", auth, isAdmin, updateCarStatus);

module.exports = router;
