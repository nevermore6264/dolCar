const db = require("../config/database");

class Booking {
  static async create(bookingData) {
    const { trip_id, user_id, seats_booked, status, created_at, updated_at } =
      bookingData;
    console.log("Booking.create data:", bookingData);
    console.log("trip_id:", trip_id);
    console.log("user_id:", user_id);
    console.log("seats_booked:", seats_booked);
    console.log("status:", status);
    console.log("created_at:", created_at);
    console.log("updated_at:", updated_at);

    const [result] = await db.execute(
      `INSERT INTO bookings (trip_id, user_id, seats_booked, status, created_at, updated_at)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [trip_id, user_id, seats_booked, status, created_at, updated_at]
    );

    return this.getById(result.insertId);
  }

  static async getById(id) {
    const [rows] = await db.execute(
      `SELECT b.*, t.departure_date, t.departure_time, t.price,
              c.name as car_name, c.type as car_type,
              u.name as driver_name, u.phone as driver_phone
       FROM bookings b
       JOIN trips t ON b.trip_id = t.id
       JOIN cars c ON t.car_id = c.id
       JOIN users u ON t.driver_id = u.id
       WHERE b.id = ?`,
      [id]
    );

    return rows[0];
  }

  static async getByUserId(userId) {
    const [rows] = await db.execute(
      `SELECT b.*, t.departure_date, t.departure_time, t.price,
              c.name as car_name, c.type as car_type,
              u.name as driver_name, u.phone as driver_phone
       FROM bookings b
       JOIN trips t ON b.trip_id = t.id
       JOIN cars c ON t.car_id = c.id
       JOIN users u ON t.driver_id = u.id
       WHERE b.user_id = ?
       ORDER BY b.created_at DESC`,
      [userId]
    );

    return rows;
  }

  static async updateStatus(id, status) {
    await db.execute(`UPDATE bookings SET status = ? WHERE id = ?`, [
      status,
      id,
    ]);

    return this.getById(id);
  }
}

module.exports = Booking;
