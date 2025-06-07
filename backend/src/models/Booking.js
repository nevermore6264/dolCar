const db = require("../config/database");

class Booking {
  static async create({
    userId,
    carId,
    startDate,
    endDate,
    totalPrice,
    status = "pending",
  }) {
    const [result] = await db.execute(
      "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)",
      [userId, carId, startDate, endDate, totalPrice, status]
    );
    return result.insertId;
  }

  static async findByUserId(userId) {
    const [rows] = await db.execute(
      `
            SELECT b.*, c.name as car_name, c.type as car_type, c.image as car_image 
            FROM bookings b 
            JOIN cars c ON b.car_id = c.id 
            WHERE b.user_id = ?
        `,
      [userId]
    );
    return rows;
  }

  static async findById(id) {
    const [rows] = await db.execute(
      `
            SELECT b.*, c.name as car_name, c.type as car_type, c.image as car_image,
                   u.name as user_name, u.phone as user_phone
            FROM bookings b 
            JOIN cars c ON b.car_id = c.id 
            JOIN users u ON b.user_id = u.id
            WHERE b.id = ?
        `,
      [id]
    );
    return rows[0];
  }

  static async updateStatus(id, status) {
    const [result] = await db.execute(
      "UPDATE bookings SET status = ? WHERE id = ?",
      [status, id]
    );
    return result.affectedRows > 0;
  }
}

module.exports = Booking;
