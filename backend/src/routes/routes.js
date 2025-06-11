const express = require("express");
const router = express.Router();
const routeController = require("../controllers/routeController");

// GET /api/routes - Lấy danh sách lộ trình
router.get("/", routeController.getAllRoutes);

module.exports = router;
