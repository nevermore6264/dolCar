const db = require("../config/database");
const bcrypt = require("bcryptjs");

class User {
  static async create({
    name,
    email,
    password,
    phone,
    role = "passenger",
    driverLicense = null,
    carId = null,
  }) {
    const hashedPassword = await bcrypt.hash(password, 10);
    const [result] = await db.execute(
      "INSERT INTO users (name, email, password, phone, role, driver_license, car_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
      [name, email, hashedPassword, phone, role, driverLicense, carId]
    );
    return result.insertId;
  }

  static async findByEmail(email) {
    const [rows] = await db.execute("SELECT * FROM users WHERE email = ?", [
      email,
    ]);
    return rows[0];
  }

  static async findById(id) {
    const [rows] = await db.execute("SELECT * FROM users WHERE id = ?", [id]);
    return rows[0];
  }

  static async updateDriverInfo(userId, { driverLicense, carId }) {
    const [result] = await db.execute(
      "UPDATE users SET driver_license = ?, car_id = ?, role = 'driver' WHERE id = ?",
      [driverLicense, carId, userId]
    );
    return result.affectedRows > 0;
  }

  static async getDriverProfile(userId) {
    const [rows] = await db.execute(
      `
      SELECT u.*, c.name as car_name, c.type as car_type, c.seats as car_seats
      FROM users u
      LEFT JOIN cars c ON u.car_id = c.id
      WHERE u.id = ? AND u.role = 'driver'
    `,
      [userId]
    );
    return rows[0];
  }

  static async getPassengerProfile(userId) {
    const [rows] = await db.execute(
      `
      SELECT u.*, 
             COUNT(b.id) as total_bookings,
             SUM(CASE WHEN b.status = 'completed' THEN 1 ELSE 0 END) as completed_bookings
      FROM users u
      LEFT JOIN bookings b ON u.id = b.user_id
      WHERE u.id = ? AND u.role = 'passenger'
      GROUP BY u.id
    `,
      [userId]
    );
    return rows[0];
  }
}

module.exports = User;
