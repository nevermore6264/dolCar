package com.isdol.carpool.ui.trips

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.databinding.ActivityTripListBinding

class TripListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Implement trip list functionality
    }
} 