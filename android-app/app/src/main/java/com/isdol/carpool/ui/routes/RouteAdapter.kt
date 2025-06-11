package com.isdol.carpool.ui.routes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isdol.carpool.databinding.ItemRouteBinding

class RouteAdapter(private val onRouteClick: (Route) -> Unit) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {
    private var routes: List<Route> = emptyList()

    fun submitList(list: List<Route>?) {
        routes = list ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position])
    }

    override fun getItemCount(): Int = routes.size

    inner class RouteViewHolder(private val binding: ItemRouteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.tvRouteName.text = "${route.start_location} - ${route.end_location}"
            binding.tvDistance.text = "Khoảng cách: ${route.distance} km"
            
            binding.root.setOnClickListener {
                onRouteClick(route)
            }
        }
    }
} 