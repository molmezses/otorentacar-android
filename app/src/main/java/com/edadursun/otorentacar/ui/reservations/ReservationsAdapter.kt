package com.edadursun.otorentacar.ui.reservations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.data.model.ReservationUiModel
import com.edadursun.otorentacar.databinding.LayoutItemReservationBinding

// Rezervasyonlarım ekranındaki rezervasyon kartlarını göstermek için kullanılan adapter
class ReservationsAdapter(
    private val items: List<ReservationUiModel>,
    private val onDetailClick: (ReservationUiModel) -> Unit,
    private val onDeleteClick: (ReservationUiModel) -> Unit
) : RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {

    // Tek bir rezervasyon kartını temsil eden ViewHolder
    inner class ReservationsViewHolder(
        private val binding: LayoutItemReservationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // ReservationUiModel içindeki verileri karttaki alanlara yazar
        fun bind(item: ReservationUiModel) {
            binding.tvReservationCode.text = item.reservationCode
            binding.tvVehicleName.text = item.vehicleName
            binding.tvStatus.text = item.statusText
            binding.tvPickupLocation.text = item.pickupLocation
            binding.tvDropOffLocation.text = item.dropOffLocation
            binding.tvDateRange.text = item.dateRangeText
            binding.tvTotalPrice.text = item.totalPriceText

            // Detayı Gör butonuna basılınca dışarıya ilgili rezervasyonu gönder
            binding.btnDetail.setOnClickListener {
                onDetailClick(item)
            }

            // Çöp kutusu ikonuna basılınca silme işlemi için dışarıya rezervasyonu gönder
            binding.cardDelete.setOnClickListener {
                onDeleteClick(item)
            }

            binding.ivDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    // XML layout'u inflate edip ViewHolder oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val binding = LayoutItemReservationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReservationsViewHolder(binding)
    }

    // Listedeki ilgili veriyi ilgili ViewHolder'a bağlar
    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Toplam rezervasyon sayısını döndürür
    override fun getItemCount(): Int = items.size
}