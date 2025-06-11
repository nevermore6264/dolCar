package com.isdol.carpool.ui.routes

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.isdol.carpool.databinding.DialogBookVehicleBinding
import com.isdol.carpool.databinding.ItemVehicleBinding
import com.isdol.carpool.network.BookingApi
import com.isdol.carpool.network.BookingRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class VehicleAdapter(
    private val bookingApi: BookingApi
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
    private var vehicles: List<VehicleAndDriver> = emptyList()
    private var onBookingSuccess: (() -> Unit)? = null

    fun setOnBookingSuccessListener(listener: () -> Unit) {
        onBookingSuccess = listener
    }

    fun submitList(list: List<VehicleAndDriver>?) {
        vehicles = list ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(vehicles[position])
    }

    override fun getItemCount(): Int = vehicles.size

    inner class VehicleViewHolder(private val binding: ItemVehicleBinding) : RecyclerView.ViewHolder(binding.root) {
        private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        private val timeFormatter = SimpleDateFormat("HH:mm", Locale("vi", "VN"))

        fun bind(vehicle: VehicleAndDriver) {
            binding.apply {
                // Thông tin xe
                tvCarName.text = vehicle.car_name
                tvCarType.text = vehicle.car_type

                // Thông tin chủ xe
                tvOwnerName.text = vehicle.driver_name
                tvOwnerPhone.text = vehicle.driver_phone

                // Format ngày và giờ từ ISO 8601
                val formattedDate = try {
                    val date = isoDateFormat.parse(vehicle.departure_date)
                    dateFormatter.format(date)
                } catch (e: Exception) {
                    vehicle.departure_date
                }
                val formattedTime = try {
                    val date = isoDateFormat.parse(vehicle.departure_date)
                    timeFormatter.format(date)
                } catch (e: Exception) {
                    vehicle.departure_time
                }
                
                tvDepartureTime.text = "Khởi hành: $formattedTime - $formattedDate"

                // Giá và số ghế trống
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                tvPrice.text = formatter.format(vehicle.price)
                tvAvailableSeats.text = "${vehicle.available_seats} ghế trống"

                // Thêm sự kiện click để đặt xe
                root.setOnClickListener {
                    showBookingDialog(vehicle)
                }
            }
        }

        private fun showBookingDialog(vehicle: VehicleAndDriver) {
            val context = binding.root.context
            val dialogBinding = DialogBookVehicleBinding.inflate(LayoutInflater.from(context))
            
            // Format ngày và giờ từ ISO 8601
            val formattedDate = try {
                val date = isoDateFormat.parse(vehicle.departure_date)
                dateFormatter.format(date)
            } catch (e: Exception) {
                vehicle.departure_date
            }
            
            val formattedTime = try {
                val date = isoDateFormat.parse(vehicle.departure_date)
                timeFormatter.format(date)
            } catch (e: Exception) {
                vehicle.departure_time
            }
            
            // Hiển thị thông tin xe
            dialogBinding.tvVehicleInfo.text = """
                Xe: ${vehicle.car_name} (${vehicle.car_type})
                Tài xế: ${vehicle.driver_name}
                Số điện thoại: ${vehicle.driver_phone}
                Giờ khởi hành: $formattedTime
                Ngày khởi hành: $formattedDate
                Giá: ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(vehicle.price)}
                Ghế trống: ${vehicle.available_seats}
            """.trimIndent()

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnBook.setOnClickListener {
                val seatsText = dialogBinding.etSeats.text.toString()
                if (seatsText.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập số ghế", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val seats = seatsText.toIntOrNull()
                if (seats == null || seats <= 0) {
                    Toast.makeText(context, "Số ghế không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (seats > vehicle.available_seats) {
                    Toast.makeText(context, "Số ghế còn trống không đủ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Gọi API đặt xe
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val bookingRequest = BookingRequest(
                            trip_id = vehicle.trip_id,
                            seats_booked = seats
                        )
                        bookingApi.createBooking(bookingRequest)
                        
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            Toast.makeText(context, "Đặt xe thành công", Toast.LENGTH_SHORT).show()
                            onBookingSuccess?.invoke()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            dialog.show()
        }
    }
} 