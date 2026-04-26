package com.edadursun.otorentacar.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.LayoutItemFavoriteVehicleBinding

// Favorilere eklenen araçları RecyclerView içinde göstermek için kullanılan adapter
class FavoritesAdapter(
    private val items: List<Vehicle>,
    private val onFavoriteClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    // Tek bir favori araç kartını temsil eden ViewHolder
    inner class FavoritesViewHolder(
        private val binding: LayoutItemFavoriteVehicleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Vehicle verisini favori kartına bağlar
        fun bind(item: Vehicle) {
            binding.tvVehicleName.text = item.name
            binding.tvVehicleType.text = item.tag
            binding.tvVehiclePrice.text = item.dailyPrice

            // Eğer araç için image url geldiyse resmi internetten yükle
            if (item.imageUrl.isNotBlank()) {
                Glide.with(binding.ivVehicle.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_directions_car)
                    .error(R.drawable.ic_directions_car)
                    .into(binding.ivVehicle)
            } else {
                // Url yoksa local drawable göster
                binding.ivVehicle.setImageResource(item.imageResId)
            }

            // Kalp ikonuna basılınca dışarıya seçilen favori aracını gönder
            binding.ivFav.setOnClickListener {
                onFavoriteClick(item)
            }

            // Kalbin bulunduğu kart alanına basılınca da aynı işlem çalışsın
            binding.cardFav.setOnClickListener {
                onFavoriteClick(item)
            }
        }
    }

    // XML layout'u inflate edip ViewHolder oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = LayoutItemFavoriteVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoritesViewHolder(binding)
    }

    // Listedeki ilgili veriyi ilgili ViewHolder'a bağlar
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Toplam favori araç sayısını döndürür
    override fun getItemCount(): Int = items.size
}