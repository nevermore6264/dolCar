package com.isdol.carpool.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.R
import com.isdol.carpool.databinding.ActivityRegisterBinding
import com.isdol.carpool.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val name: String,
    val email: String?,
    val password: String,
    val phone: String,
    val role: String
)

data class RegisterResponse(
    val token: String?,
    val user: Any?
)

interface AuthApi {
    @POST("api/auth/register")
    fun register(@Body body: RegisterRequest): Call<RegisterResponse>
}

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authApi = retrofit.create(AuthApi::class.java)

        setupRegisterButton(authApi)
    }

    private fun setupRegisterButton(authApi: AuthApi) {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val isDriver = binding.rbDriver.isChecked
            val isPassenger = binding.rbPassenger.isChecked

            if (validateInput(
                    fullName,
                    email,
                    phone,
                    password,
                    confirmPassword,
                    isDriver,
                    isPassenger
                )
            ) {
                val request = RegisterRequest(
                    name = fullName,
                    email = if (email.isEmpty()) null else email,
                    password = password,
                    phone = phone,
                    role = if (isDriver) "driver" else "passenger"
                )
                authApi.register(request).enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Đăng ký thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Đăng ký thất bại!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Lỗi: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                        t.printStackTrace()
                    }
                })
            }
        }
    }

    private fun validateInput(
        fullName: String,
        email: String,
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
        binding.tilFullName.error = null

        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            return false
        }
        binding.tilEmail.error = null

        if (phone.isEmpty()) {
            binding.tilPhone.error = getString(R.string.phone_required)
            return false
        }
        binding.tilPhone.error = null

        if (!isValidPhoneNumber(phone)) {
            binding.tilPhone.error = getString(R.string.invalid_phone)
            return false
        }
        binding.tilPhone.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.password_required)
            return false
        }
        binding.tilPassword.error = null

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.password_not_match)
            return false
        }
        binding.tilConfirmPassword.error = null

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