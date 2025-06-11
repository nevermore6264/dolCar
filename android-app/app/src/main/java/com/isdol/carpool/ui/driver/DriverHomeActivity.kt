package com.isdol.carpool.ui.driver

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.databinding.ActivityDriverHomeBinding
import com.isdol.carpool.ui.profile.ProfileActivity
import com.isdol.carpool.ui.trips.TripHistoryActivity

class DriverHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.btnTripHistory.setOnClickListener {
            val intent = Intent(this, TripHistoryActivity::class.java)
            startActivity(intent)
        }
    }
} 