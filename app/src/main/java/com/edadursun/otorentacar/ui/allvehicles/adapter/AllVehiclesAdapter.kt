package com.edadursun.otorentacar.ui.allvehicles.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.LayoutItemAllVehicleBinding

// Araç listesini RecyclerView içinde göstermek için kullanılan adapter
class AllVehiclesAdapter(
    private val items: List<Vehicle>,
    private val onSelectClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<AllVehiclesAdapter.AllVehiclesViewHolder>() {

    // Tek bir araç kartını temsil eden ViewHolder
    inner class AllVehiclesViewHolder(
        private val binding: LayoutItemAllVehicleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Vehicle içindeki verileri kart tasarımındaki alanlara yazar
        fun bind(item: Vehicle) {
            binding.tvTag.text = item.tag
            binding.tvVehicleName.text = item.name
            binding.tvVehicleType.text = item.type
            binding.tvTransmission.text = item.transmission
            binding.tvFuel.text = item.fuel
            binding.tvPassenger.text = item.passengerCount
            binding.tvBag.text = item.bagCount
            binding.tvPrice.text = item.dailyPrice
            binding.total.text = item.totalPrice

            // Eğer API'den gerçek resim url'i geldiyse onu yükle
            if (item.imageUrl.isNotBlank()) {
                Glide.with(binding.ivVehicle.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_directions_car)
                    .error(R.drawable.ic_directions_car)
                    .into(binding.ivVehicle)
            } else {
                // Resim yoksa local placeholder göster
                binding.ivVehicle.setImageResource(item.imageResId)
            }

            // Kullanıcı seç butonuna basınca dışarıya seçilen aracı gönder
            binding.btnSelect.setOnClickListener {
                onSelectClick(item)
            }
        }
    }

    // XML tasarımını inflate edip ViewHolder oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllVehiclesViewHolder {
        val binding = LayoutItemAllVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllVehiclesViewHolder(binding)
    }

    // Listedeki ilgili aracı ilgili ViewHolder'a bağlar
    override fun onBindViewHolder(holder: AllVehiclesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Toplam araç sayısını döndürür
    override fun getItemCount(): Int = items.size
}