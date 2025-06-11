package com.isdol.carpool.ui.routes

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

// Data model
data class Route(
    val id: Int,
    val start_location: String,
    val end_location: String,
    val distance: Double
)

// Retrofit API
interface RouteApi {
    @GET("api/routes")
    fun getRoutes(): Call<List<Route>>
}

class RouteListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRouteListBinding
    private lateinit var adapter: RouteAdapter
    private var originalRoutes: List<Route> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        fetchRoutes()
    }

    private fun setupRecyclerView() {
        adapter = RouteAdapter()
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
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RouteApi::class.java)
        api.getRoutes().enqueue(object : Callback<List<Route>> {
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
} 