const db = require("../config/database");

class Car {
  static async create({
    name,
    type,
    price,
    seats,
    image,
    status = "available",
  }) {
    const [result] = await db.execute(
      "INSERT INTO cars (name, type, price, seats, image, status) VALUES (?, ?, ?, ?, ?, ?)",
      [name, type, price, seats, image, status]
    );
    return result.insertId;
  }

  static async findAll() {
    const [rows] = await db.execute("SELECT * FROM cars");
    return rows;
  }

  static async findById(id) {
    const [rows] = await db.execute("SELECT * FROM cars WHERE id = ?", [id]);
    return rows[0];
  }

  static async updateStatus(id, status) {
    const [result] = await db.execute(
      "UPDATE cars SET status = ? WHERE id = ?",
      [status, id]
    );
    return result.affectedRows > 0;
  }
}

module.exports = Car;
