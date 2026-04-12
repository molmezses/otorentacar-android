package com.edadursun.otorentacar.ui.allvehicles.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.LayoutItemAllVehicleBinding

class AllVehiclesAdapter(
    private val items: List<Vehicle>,
    private val onSelectClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<AllVehiclesAdapter.AllVehiclesViewHolder>() {

    inner class AllVehiclesViewHolder(
        private val binding: LayoutItemAllVehicleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Vehicle) {
            binding.tvTag.text = item.tag
            binding.tvVehicleName.text = item.name
            binding.tvVehicleType.text = item.type
            binding.tvTransmission.text = item.transmission
            binding.tvFuel.text = item.fuel
            binding.tvPassenger.text = item.passengerCount
            binding.tvBag.text = item.bagCount
            binding.tvPrice.text = item.totalPrice
            binding.ivVehicle.setImageResource(item.imageResId)

            binding.btnSelect.setOnClickListener {
                onSelectClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllVehiclesViewHolder {
        val binding = LayoutItemAllVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllVehiclesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllVehiclesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}