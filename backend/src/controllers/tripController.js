const Trip = require("../models/Trip");
const Route = require("../models/Route");
const Booking = require("../models/Booking");

// Tìm kiếm chuyến đi
const searchTrips = async (req, res) => {
  try {
    const { startLocation, endLocation, date } = req.query;

    if (!startLocation || !endLocation || !date) {
      return res.status(400).json({
        message: "Please provide start location, end location and date",
      });
    }

    // Tìm các tuyến đường phù hợp
    const routes = await Route.searchRoutes(startLocation, endLocation);
    if (routes.length === 0) {
      return res.status(404).json({
        message: "No routes found for this journey",
      });
    }

    // Tìm các chuyến đi cho mỗi tuyến đường
    const trips = await Trip.searchTrips(startLocation, endLocation, date);

    // Nhóm các chuyến đi theo tuyến đường
    const tripsByRoute = routes.map((route) => {
      const routeTrips = trips.filter((trip) => trip.route_id === route.id);
      return {
        route: {
          id: route.id,
          start_location: route.start_location,
          end_location: route.end_location,
          distance: route.distance,
          estimated_time: route.estimated_time,
          base_price: route.base_price,
        },
        trips: routeTrips.map((trip) => ({
          id: trip.id,
          departure_time: trip.departure_time,
          available_seats: trip.available_seats,
          price: trip.price,
          driver: {
            name: trip.driver_name,
            phone: trip.driver_phone,
          },
          car: {
            name: trip.car_name,
            type: trip.car_type,
            seats: trip.car_seats,
          },
        })),
      };
    });

    res.json(tripsByRoute);
  } catch (error) {
    console.error("Search trips error:", error);
    res.status(500).json({ message: "Server error" });
  }
};

// Lấy chi tiết chuyến đi
const getTripDetails = async (req, res) => {
  try {
    const tripId = req.params.id;
    const trip = await Trip.findById(tripId);

    if (!trip) {
      return res.status(404).json({ message: "Trip not found" });
    }

    // Lấy danh sách đặt chỗ cho chuyến đi này
    const bookings = await Booking.findByTripId(tripId);

    const tripDetails = {
      id: trip.id,
      route: {
        start_location: trip.start_location,
        end_location: trip.end_location,
        distance: trip.distance,
        estimated_time: trip.estimated_time,
      },
      departure: {
        date: trip.departure_date,
        time: trip.departure_time,
      },
      driver: {
        name: trip.driver_name,
        phone: trip.driver_phone,
      },
      car: {
        name: trip.car_name,
        type: trip.car_type,
        seats: trip.car_seats,
      },
      price: trip.price,
      available_seats: trip.available_seats,
      status: trip.status,
      bookings: bookings.map((booking) => ({
        id: booking.id,
        user_name: booking.user_name,
        seats_booked: booking.seats_booked,
        pickup_location: booking.pickup_location,
        dropoff_location: booking.dropoff_location,
        status: booking.status,
      })),
    };

    res.json(tripDetails);
  } catch (error) {
    console.error("Get trip details error:", error);
    res.status(500).json({ message: "Server error" });
  }
};

// Lấy danh sách chuyến đi phổ biến
const getPopularTrips = async (req, res) => {
  try {
    const popularRoutes = await Route.getPopularRoutes();
    const trips = [];

    // Lấy chuyến đi gần nhất cho mỗi tuyến đường phổ biến
    for (const route of popularRoutes) {
      const routeTrips = await Trip.searchTrips(
        route.start_location,
        route.end_location,
        new Date().toISOString().split("T")[0]
      );

      if (routeTrips.length > 0) {
        trips.push({
          route: {
            id: route.id,
            start_location: route.start_location,
            end_location: route.end_location,
            distance: route.distance,
            estimated_time: route.estimated_time,
            base_price: route.base_price,
          },
          next_trip: {
            id: routeTrips[0].id,
            departure_time: routeTrips[0].departure_time,
            available_seats: routeTrips[0].available_seats,
            price: routeTrips[0].price,
          },
        });
      }
    }

    res.json(trips);
  } catch (error) {
    console.error("Get popular trips error:", error);
    res.status(500).json({ message: "Server error" });
  }
};

module.exports = {
  searchTrips,
  getTripDetails,
  getPopularTrips,
};
