package com.isdol.carpool.ui.routes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.isdol.carpool.databinding.ActivityRouteDetailBinding
import com.isdol.carpool.network.ApiConfig
import com.isdol.carpool.network.BookingApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRouteDetailBinding
    private lateinit var adapter: VehicleAdapter
    private lateinit var routeApi: RouteApi
    private lateinit var bookingApi: BookingApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRetrofit()
        setupRecyclerView()

        val routeId = intent.getIntExtra("routeId", -1)
        if (routeId != -1) {
            fetchVehiclesAndDrivers(routeId)
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy lộ trình", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        routeApi = retrofit.create(RouteApi::class.java)
        bookingApi = retrofit.create(BookingApi::class.java)
    }

    private fun setupRecyclerView() {
        adapter = VehicleAdapter(bookingApi)
        adapter.setOnBookingSuccessListener {
            // Refresh danh sách xe sau khi đặt xe thành công
            val routeId = intent.getIntExtra("routeId", -1)
            if (routeId != -1) {
                fetchVehiclesAndDrivers(routeId)
            }
        }
        binding.rvVehicles.layoutManager = LinearLayoutManager(this)
        binding.rvVehicles.adapter = adapter
    }

    private fun fetchVehiclesAndDrivers(routeId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        binding.rvVehicles.visibility = View.GONE

        routeApi.getRouteVehiclesAndDrivers(routeId).enqueue(object : Callback<List<VehicleAndDriver>> {
            override fun onResponse(call: Call<List<VehicleAndDriver>>, response: Response<List<VehicleAndDriver>>) {
                binding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body() != null) {
                    val vehiclesAndDrivers = response.body()!!
                    if (vehiclesAndDrivers.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvVehicles.visibility = View.VISIBLE
                        adapter.submitList(vehiclesAndDrivers)
                    }
                } else {
                    Toast.makeText(this@RouteDetailActivity, "Không lấy được dữ liệu xe và tài xế", Toast.LENGTH_SHORT).show()
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<VehicleAndDriver>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
                Toast.makeText(this@RouteDetailActivity, "Lỗi: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
} 