const db = require("../config/database");

class Trip {
  static async create({
    routeId,
    driverId,
    carId,
    departureDate,
    departureTime,
    availableSeats,
    price,
  }) {
    const [result] = await db.execute(
      "INSERT INTO trips (route_id, driver_id, car_id, departure_date, departure_time, available_seats, price) VALUES (?, ?, ?, ?, ?, ?, ?)",
      [
        routeId,
        driverId,
        carId,
        departureDate,
        departureTime,
        availableSeats,
        price,
      ]
    );
    return result.insertId;
  }

  static async findById(id) {
    const [rows] = await db.execute(
      `
            SELECT t.*, 
                   r.start_location, r.end_location, r.distance, r.estimated_time,
                   u.name as driver_name, u.phone as driver_phone,
                   c.name as car_name, c.type as car_type, c.seats as car_seats
            FROM trips t
            JOIN routes r ON t.route_id = r.id
            JOIN users u ON t.driver_id = u.id
            JOIN cars c ON t.car_id = c.id
            WHERE t.id = ?
        `,
      [id]
    );
    return rows[0];
  }

  static async searchTrips(startLocation, endLocation, date) {
    const [rows] = await db.execute(
      `
            SELECT t.*, 
                   r.start_location, r.end_location, r.distance, r.estimated_time,
                   u.name as driver_name, u.phone as driver_phone,
                   c.name as car_name, c.type as car_type, c.seats as car_seats
            FROM trips t
            JOIN routes r ON t.route_id = r.id
            JOIN users u ON t.driver_id = u.id
            JOIN cars c ON t.car_id = c.id
            WHERE r.start_location LIKE ? 
            AND r.end_location LIKE ?
            AND t.departure_date = ?
            AND t.status = 'scheduled'
            AND t.available_seats > 0
        `,
      [`%${startLocation}%`, `%${endLocation}%`, date]
    );
    return rows;
  }

  static async getDriverTrips(driverId) {
    const [rows] = await db.execute(
      `
            SELECT t.*, 
                   r.start_location, r.end_location,
                   COUNT(b.id) as total_bookings
            FROM trips t
            JOIN routes r ON t.route_id = r.id
            LEFT JOIN bookings b ON t.id = b.trip_id
            WHERE t.driver_id = ?
            GROUP BY t.id
            ORDER BY t.departure_date DESC, t.departure_time DESC
        `,
      [driverId]
    );
    return rows;
  }

  static async updateStatus(id, status) {
    const [result] = await db.execute(
      "UPDATE trips SET status = ? WHERE id = ?",
      [status, id]
    );
    return result.affectedRows > 0;
  }

  static async updateAvailableSeats(id, seats) {
    const [result] = await db.execute(
      "UPDATE trips SET available_seats = available_seats - ? WHERE id = ?",
      [seats, id]
    );
    return result.affectedRows > 0;
  }
}

module.exports = Trip;
