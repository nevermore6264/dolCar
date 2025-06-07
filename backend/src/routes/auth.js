const express = require("express");
const router = express.Router();
const {
  register,
  registerDriver,
  login,
  getProfile,
  updateDriverInfo,
} = require("../controllers/authController");
const { auth } = require("../middleware/auth");

// Public routes
router.post("/register", register);
router.post("/register/driver", registerDriver);
router.post("/login", login);

// Protected routes
router.get("/profile", auth, getProfile);
router.patch("/driver/info", auth, updateDriverInfo);

module.exports = router;
