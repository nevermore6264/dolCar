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
