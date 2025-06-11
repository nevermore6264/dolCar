package com.isdol.carpool.ui.routes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.isdol.carpool.databinding.ActivityRouteListBinding
import com.isdol.carpool.network.ApiConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer
import java.util.regex.Pattern

// Data models
data class Route(
    val id: Int,
    val start_location: String,
    val end_location: String,
    val distance: Double
)

data class VehicleAndDriver(
    val trip_id: Int,
    val departure_date: String,
    val departure_time: String,
    val available_seats: Int,
    val price: Double,
    val car_id: Int,
    val car_name: String,
    val car_type: String,
    val car_seats: Int,
    val car_image: String,
    val driver_id: Int,
    val driver_name: String,
    val driver_phone: String
)

// Retrofit API
interface RouteApi {
    @GET("api/routes")
    fun getRoutes(): Call<List<Route>>

    @GET("api/routes/{routeId}/vehicles")
    fun getRouteVehiclesAndDrivers(@retrofit2.http.Path("routeId") routeId: Int): Call<List<VehicleAndDriver>>
}

class RouteListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRouteListBinding
    private lateinit var adapter: RouteAdapter
    private var originalRoutes: List<Route> = emptyList()
    private lateinit var routeApi: RouteApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRetrofit()
        setupRecyclerView()
        setupSearchView()
        fetchRoutes()
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        routeApi = retrofit.create(RouteApi::class.java)
    }

    private fun setupRecyclerView() {
        adapter = RouteAdapter { route ->
            val intent = Intent(this, RouteDetailActivity::class.java)
            intent.putExtra("routeId", route.id)
            startActivity(intent)
        }
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterRoutes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRoutes(newText)
                return true
            }
        })
    }

    private fun normalizeString(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized)
            .replaceAll("")
            .lowercase()
            .trim()
    }

    private fun filterRoutes(query: String?) {
        if (query.isNullOrBlank()) {
            adapter.submitList(originalRoutes)
            return
        }

        val normalizedQuery = normalizeString(query)
        val filteredList = originalRoutes.filter { route ->
            val startLocation = normalizeString(route.start_location)
            val endLocation = normalizeString(route.end_location)
            val fullRoute = normalizeString("${route.start_location} - ${route.end_location}")
            
            startLocation.contains(normalizedQuery) ||
            endLocation.contains(normalizedQuery) ||
            fullRoute.contains(normalizedQuery)
        }
        adapter.submitList(filteredList)
    }

    private fun fetchRoutes() {
        routeApi.getRoutes().enqueue(object : Callback<List<Route>> {
            override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                if (response.isSuccessful && response.body() != null) {
                    originalRoutes = response.body()!!
                    adapter.submitList(originalRoutes)
                } else {
                    Toast.makeText(this@RouteListActivity, "Không lấy được dữ liệu lộ trình", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Route>>, t: Throwable) {
                Toast.makeText(this@RouteListActivity, "Lỗi: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchRouteVehiclesAndDrivers(routeId: Int) {
        routeApi.getRouteVehiclesAndDrivers(routeId).enqueue(object : Callback<List<VehicleAndDriver>> {
            override fun onResponse(call: Call<List<VehicleAndDriver>>, response: Response<List<VehicleAndDriver>>) {
                if (response.isSuccessful && response.body() != null) {
                    val vehiclesAndDrivers = response.body()!!
                    if (vehiclesAndDrivers.isEmpty()) {
                        Toast.makeText(this@RouteListActivity, "Không có xe nào cho lộ trình này", Toast.LENGTH_SHORT).show()
                    } else {
                        // TODO: Show vehicles and drivers in a dialog or new activity
                        showVehiclesAndDriversDialog(vehiclesAndDrivers)
                    }
                } else {
                    Toast.makeText(this@RouteListActivity, "Không lấy được dữ liệu xe và tài xế", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<VehicleAndDriver>>, t: Throwable) {
                Toast.makeText(this@RouteListActivity, "Lỗi: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showVehiclesAndDriversDialog(vehiclesAndDrivers: List<VehicleAndDriver>) {
        // TODO: Implement dialog to show vehicles and drivers
        val message = vehiclesAndDrivers.joinToString("\n\n") { vehicle ->
            """
            Xe: ${vehicle.car_name} (${vehicle.car_type})
            Tài xế: ${vehicle.driver_name}
            Số điện thoại: ${vehicle.driver_phone}
            Giờ khởi hành: ${vehicle.departure_time}
            Ngày khởi hành: ${vehicle.departure_date}
            Giá: ${vehicle.price} VNĐ
            Ghế trống: ${vehicle.available_seats}
            """.trimIndent()
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
} 