package com.isdol.carpool.ui.routes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isdol.carpool.databinding.ItemVehicleBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class VehicleAdapter : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
    private var vehicles: List<VehicleAndDriver> = emptyList()

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

    class VehicleViewHolder(private val binding: ItemVehicleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(vehicle: VehicleAndDriver) {
            binding.apply {
                // Thông tin xe
                tvCarName.text = vehicle.car_name
                tvCarType.text = vehicle.car_type

                // Thông tin chủ xe
                tvOwnerName.text = vehicle.driver_name
                tvOwnerPhone.text = vehicle.driver_phone

                // Thời gian khởi hành
                tvDepartureTime.text = "Khởi hành: ${vehicle.departure_time} - ${vehicle.departure_date}"

                // Giá và số ghế trống
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                tvPrice.text = formatter.format(vehicle.price)
                tvAvailableSeats.text = "${vehicle.available_seats} ghế trống"

                // Load hình ảnh xe
                Glide.with(ivCar)
                    .load(vehicle.car_image)
                    .centerCrop()
                    .into(ivCar)
            }
        }
    }
} 