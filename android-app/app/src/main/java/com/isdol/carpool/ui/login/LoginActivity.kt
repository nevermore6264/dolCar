package com.isdol.carpool.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.R
import com.isdol.carpool.databinding.ActivityLoginBinding
import com.isdol.carpool.ui.register.RegisterActivity
import com.isdol.carpool.ui.trips.TripListActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginButton()
        setupRegisterButton()
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                val phone = binding.etPhone.text.toString()
                val password = binding.etPassword.text.toString()
                // TODO: Implement login logic
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRegisterButton() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(): Boolean {
        val phone = binding.etPhone.text.toString()
        val password = binding.etPassword.text.toString()

        if (phone.isEmpty()) {
            binding.tilPhone.error = getString(R.string.error_empty_field)
            return false
        }
        binding.tilPhone.error = null

        if (!isValidPhoneNumber(phone)) {
            binding.tilPhone.error = getString(R.string.error_invalid_phone)
            return false
        }
        binding.tilPhone.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_field)
            return false
        }
        binding.tilPassword.error = null

        return true
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{10}$"))
    }
} 