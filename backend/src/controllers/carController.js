const Car = require("../models/Car");

const createCar = async (req, res) => {
  try {
    const { name, type, price, seats, image } = req.body;
    const carId = await Car.create({ name, type, price, seats, image });
    res.status(201).json({ id: carId, message: "Car created successfully" });
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

const getAllCars = async (req, res) => {
  try {
    const cars = await Car.findAll();
    res.json(cars);
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

const getCarById = async (req, res) => {
  try {
    const car = await Car.findById(req.params.id);
    if (!car) {
      return res.status(404).json({ message: "Car not found" });
    }
    res.json(car);
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

const updateCarStatus = async (req, res) => {
  try {
    const { status } = req.body;
    const success = await Car.updateStatus(req.params.id, status);
    if (!success) {
      return res.status(404).json({ message: "Car not found" });
    }
    res.json({ message: "Car status updated successfully" });
  } catch (error) {
    res.status(500).json({ message: "Server error" });
  }
};

module.exports = {
  createCar,
  getAllCars,
  getCarById,
  updateCarStatus,
};
