const express = require("express");
const router = express.Router();
const routeController = require("../controllers/routeController");

// GET /api/routes - Lấy danh sách lộ trình
router.get("/", routeController.getAllRoutes);

// GET /api/routes/:routeId/vehicles - Lấy danh sách xe và tài xế của route
router.get("/:routeId/vehicles", routeController.getRouteVehiclesAndAssets);

module.exports = router;
