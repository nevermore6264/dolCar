package com.isdol.carpool.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Hiển thị thông tin tài xế ở đây
    }
} 