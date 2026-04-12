package com.edadursun.otorentacar.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.LayoutItemFeaturedVehicleBinding

class FeaturedVehicleAdapter(
    private val items: List<Vehicle>
) : RecyclerView.Adapter<FeaturedVehicleAdapter.FeaturedVehicleViewHolder>() {

    inner class FeaturedVehicleViewHolder(
        private val binding: LayoutItemFeaturedVehicleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Vehicle) {
            binding.tvVehicleName.text = item.name
            binding.tvVehicleType.text = item.type
            binding.tvTransmission.text =item.transmission
            binding.tvFuel.text = item.fuel
            binding.tvPrice.text = item.dailyPrice
            binding.ivVehicle.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedVehicleViewHolder {
        val binding = LayoutItemFeaturedVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeaturedVehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedVehicleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}