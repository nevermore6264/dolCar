const db = require("../config/database");

class Route {
  static async create({
    startLocation,
    endLocation,
    distance,
    estimatedTime,
    basePrice,
  }) {
    const [result] = await db.execute(
      "INSERT INTO routes (start_location, end_location, distance, estimated_time, base_price) VALUES (?, ?, ?, ?, ?)",
      [startLocation, endLocation, distance, estimatedTime, basePrice]
    );
    return result.insertId;
  }

  static async findAll() {
    const [rows] = await db.execute("SELECT * FROM routes");
    return rows;
  }

  static async findById(id) {
    const [rows] = await db.execute("SELECT * FROM routes WHERE id = ?", [id]);
    return rows[0];
  }

  static async searchRoutes(startLocation, endLocation) {
    const [rows] = await db.execute(
      `
            SELECT * FROM routes 
            WHERE start_location LIKE ? AND end_location LIKE ?
        `,
      [`%${startLocation}%`, `%${endLocation}%`]
    );
    return rows;
  }

  static async getPopularRoutes() {
    const [rows] = await db.execute(`
            SELECT r.*, COUNT(t.id) as trip_count
            FROM routes r
            LEFT JOIN trips t ON r.id = t.route_id
            GROUP BY r.id
            ORDER BY trip_count DESC
            LIMIT 5
        `);
    return rows;
  }

  static async getAll() {
    return this.findAll();
  }

  static async getRouteVehiclesAndAssets(routeId) {
    const [rows] = await db.execute(
      `
      SELECT 
        t.id as trip_id,
        t.departure_date,
        t.departure_time,
        t.available_seats,
        t.price,
        c.id as car_id,
        c.name as car_name,
        c.type as car_type,
        c.seats as car_seats,
        c.image as car_image,
        u.id as driver_id,
        u.name as driver_name,
        u.phone as driver_phone
      FROM trips t
      JOIN cars c ON t.car_id = c.id
      JOIN users u ON t.driver_id = u.id
      WHERE t.route_id = ?
      AND t.status = 'scheduled'
      ORDER BY t.departure_date, t.departure_time
      `,
      [routeId]
    );
    return rows;
  }
}

module.exports = Route;
