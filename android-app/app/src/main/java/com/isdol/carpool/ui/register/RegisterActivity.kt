package com.isdol.carpool.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.R
import com.isdol.carpool.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRegisterButton()
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val isDriver = binding.rbDriver.isChecked
            val isPassenger = binding.rbPassenger.isChecked

            if (validateInput(fullName, phone, password, confirmPassword, isDriver, isPassenger)) {
                // TODO: Implement actual registration logic with backend
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun validateInput(
        fullName: String,
        phone: String,
        password: String,
        confirmPassword: String,
        isDriver: Boolean,
        isPassenger: Boolean
    ): Boolean {
        if (fullName.isEmpty()) {
            binding.tilFullName.error = getString(R.string.name_required)
            return false
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = getString(R.string.phone_required)
            return false
        }

        if (!isValidPhoneNumber(phone)) {
            binding.tilPhone.error = getString(R.string.invalid_phone)
            return false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.password_required)
            return false
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.password_not_match)
            return false
        }

        if (!isDriver && !isPassenger) {
            Toast.makeText(this, getString(R.string.role_required), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{10}$"))
    }
} 