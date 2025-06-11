const Route = require("../models/Route");

// Lấy tất cả route
exports.getAllRoutes = async (req, res) => {
  try {
    const routes = await Route.getAll();
    res.json(routes);
  } catch (err) {
    res.status(500).json({ message: "Server error" });
  }
};

// Lấy danh sách xe và tài xế của route
exports.getRouteVehiclesAndAssets = async (req, res) => {
  try {
    const { routeId } = req.params;

    if (!routeId) {
      return res.status(400).json({ message: "Route ID is required" });
    }

    const vehiclesAndAssets = await Route.getRouteVehiclesAndAssets(routeId);
    res.json(vehiclesAndAssets);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Server error" });
  }
};
