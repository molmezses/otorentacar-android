package com.edadursun.otorentacar.ui.extras.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.databinding.LayoutItemExtraServiceBinding
import java.util.Locale

class ExtraServicesAdapter(
    private var items: List<ExtraService>,
    private val rentalDays: Int,
    private val onSingleSelectClick: (Int) -> Unit,
    private val onPlusClick: (Int) -> Unit,
    private val onMinusClick: (Int) -> Unit
) : RecyclerView.Adapter<ExtraServicesAdapter.ExtraServiceViewHolder>() {

    // Tek bir ek hizmet kartını temsil eden ViewHolder
    inner class ExtraServiceViewHolder(
        private val binding: LayoutItemExtraServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Gelen hizmet bilgisini kart tasarımına yazar
        fun bind(item: ExtraService) {
            binding.tvServiceName.text = item.name
            binding.tvServiceDescription.text = item.description
            binding.tvServicePrice.text = "${item.currencySymbol}${formatPrice(item.price)}"

            // Hizmet seçiliyse tik işaretini göster
            binding.ivCheck.visibility = if (item.isSelected) View.VISIBLE else View.GONE

            // Eğer bu hizmet adetli seçilebiliyorsa (ör: bebek koltuğu)
            if (item.maxCount > 1) {
                // Sayaç görünür, gün sayısı gizli olur
                binding.layoutCounter.visibility = View.VISIBLE
                binding.tvRentalDays.visibility = View.GONE

                // Mevcut adet ekrana yazılır
                binding.tvQuantity.text = item.quantity.toString()

                // Artı ve eksi butonları sınır durumuna göre soluklaştırılır
                binding.btnPlus.alpha = if (item.quantity >= item.maxCount) 0.4f else 1f
                binding.btnMinus.alpha = if (item.quantity <= 0) 0.4f else 1f

                // Bu durumda cardSelect tıklaması kullanılmaz
                binding.cardSelect.setOnClickListener(null)

                // Artı butonu, maxCount aşılmadan adedi arttırır
                binding.btnPlus.setOnClickListener {
                    if (item.quantity < item.maxCount) {
                        onPlusClick(item.id)
                    }
                }

                // Eksi butonu, 0'ın altına düşmeden adedi azaltır
                binding.btnMinus.setOnClickListener {
                    if (item.quantity > 0) {
                        onMinusClick(item.id)
                    }
                }
            } else {
                // Tek seçimlik hizmetlerde sayaç gizlenir, gün sayısı gösterilir
                binding.layoutCounter.visibility = View.GONE
                binding.tvRentalDays.visibility = View.VISIBLE
                binding.tvRentalDays.text = "$rentalDays gün"

                // Kart seçimine basılınca hizmet seçilir / kaldırılır
                binding.cardSelect.setOnClickListener {
                    onSingleSelectClick(item.id)
                }
            }
        }
    }

    // XML dosyasından item tasarımını oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtraServiceViewHolder {
        val binding = LayoutItemExtraServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExtraServiceViewHolder(binding)
    }

    // Listedeki ilgili veriyi ilgili ViewHolder'a bağlar
    override fun onBindViewHolder(holder: ExtraServiceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Toplam item sayısını döndürür
    override fun getItemCount(): Int = items.size

    // Liste güncellendiğinde adapter verisini yeniler
    fun updateList(newItems: List<ExtraService>) {
        items = newItems
        notifyDataSetChanged()
    }

    // Fiyatı gereksiz .0 olmadan string'e çevirir
    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", price)
        }
    }
}