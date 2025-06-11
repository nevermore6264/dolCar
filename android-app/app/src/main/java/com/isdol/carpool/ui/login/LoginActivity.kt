package com.isdol.carpool.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.isdol.carpool.R
import com.isdol.carpool.databinding.ActivityLoginBinding
import com.isdol.carpool.ui.register.RegisterActivity
import com.isdol.carpool.ui.routes.RouteListActivity
import com.isdol.carpool.network.ApiConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.isdol.carpool.ui.driver.DriverHomeActivity

data class LoginRequest(
    val phone: String,
    val password: String
)
data class LoginResponse(
    val token: String?,
    val user: Any?
)

interface AuthApiLogin {
    @POST("api/auth/login")
    fun login(@Body body: LoginRequest): Call<LoginResponse>
}

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authApi = retrofit.create(AuthApiLogin::class.java)

        setupLoginButton(authApi)
        setupRegisterButton()
    }

    private fun setupLoginButton(authApi: AuthApiLogin) {
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                val phone = binding.etPhone.text.toString()
                val password = binding.etPassword.text.toString()
                val request = LoginRequest(phone, password)
                authApi.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body()?.token != null) {
                            val role = response.body()?.user?.let {
                                // Nếu user là object Map hoặc JsonObject, lấy role
                                if (it is Map<*, *>) it["role"]?.toString() else null
                            } ?: "passenger"
                            if (role == "driver") {
                                val intent = Intent(this@LoginActivity, DriverHomeActivity::class.java)
                                intent.putExtra("token", response.body()?.token)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this@LoginActivity, RouteListActivity::class.java)
                                intent.putExtra("token", response.body()?.token)
                                startActivity(intent)
                            }
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Lỗi: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })
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