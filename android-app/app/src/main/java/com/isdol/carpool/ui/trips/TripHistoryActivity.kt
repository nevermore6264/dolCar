package com.isdol.carpool.ui.trips

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.databinding.ActivityTripHistoryBinding

class TripHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Hiển thị lịch sử đặt xe ở đây
    }
} 