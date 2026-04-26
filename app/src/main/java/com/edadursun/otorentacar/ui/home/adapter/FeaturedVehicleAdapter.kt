package com.edadursun.otorentacar.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.local.FavoritesManager
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.LayoutItemFeaturedVehicleBinding

// Ana sayfadaki öne çıkan araçları göstermek için kullanılan adapter
class FeaturedVehicleAdapter(
    private val items: List<Vehicle>
) : RecyclerView.Adapter<FeaturedVehicleAdapter.FeaturedVehicleViewHolder>() {

    // Tek bir araç kartını temsil eden ViewHolder
    inner class FeaturedVehicleViewHolder(
        private val binding: LayoutItemFeaturedVehicleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Vehicle verisini kart tasarımına bağlar
        fun bind(item: Vehicle) {
            binding.tvVehicleName.text = item.name
            binding.tvVehicleType.text = item.type
            binding.tvTransmission.text = item.transmission
            binding.tvFuel.text = item.fuel
            binding.tvPrice.text = item.dailyPrice

            // Eğer API'den resim url'i geldiyse Glide ile yükle
            if (item.imageUrl.isNotBlank()) {
                Glide.with(binding.ivVehicle.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_directions_car)
                    .error(R.drawable.ic_directions_car)
                    .into(binding.ivVehicle)
            } else {
                // Url yoksa local placeholder göster
                binding.ivVehicle.setImageResource(item.imageResId)
            }

            // Kart ilk açıldığında araç favorilerde mi kontrol et ve ikonunu güncelle
            updateFavoriteIcon(item)

            // Kalp ikonuna basılınca favoriye ekle / favoriden çıkar
            binding.ivFav.setOnClickListener {
                FavoritesManager.toggleFavorite(item)
                updateFavoriteIcon(item)
            }

            // Kalbin bulunduğu kart alanına basılınca da aynı işlem çalışsın
            binding.cardFav.setOnClickListener {
                FavoritesManager.toggleFavorite(item)
                updateFavoriteIcon(item)
            }
        }

        // Araç favorilerdeyse dolu kalp, değilse boş kalp gösterir
        private fun updateFavoriteIcon(item: Vehicle) {
            val isFavorite = FavoritesManager.isFavorite(item.id)

            binding.ivFav.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_fill
                else R.drawable.ic_favorite
            )
        }
    }

    // XML layout'u inflate edip ViewHolder oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedVehicleViewHolder {
        val binding = LayoutItemFeaturedVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeaturedVehicleViewHolder(binding)
    }

    // Listedeki ilgili veriyi ilgili ViewHolder'a bağlar
    override fun onBindViewHolder(holder: FeaturedVehicleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Listede kaç araç olduğunu döndürür
    override fun getItemCount(): Int = items.size
}