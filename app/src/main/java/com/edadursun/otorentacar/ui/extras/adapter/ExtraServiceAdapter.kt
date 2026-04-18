package com.edadursun.otorentacar.ui.extras.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.databinding.LayoutItemChildAgeBinding
import com.edadursun.otorentacar.databinding.LayoutItemExtraServiceBinding
import java.util.Locale

class ExtraServicesAdapter(
    private var items: List<ExtraService>,
    private val rentalDays: Int,
    private val onSingleSelectClick: (Int) -> Unit,
    private val onPlusClick: (Int) -> Unit,
    private val onMinusClick: (Int) -> Unit,
    private val onChildAgeChanged: (Int, Int, String) -> Unit
) : RecyclerView.Adapter<ExtraServicesAdapter.ExtraServiceViewHolder>() {

    // Her çocuk yaş inputunu serviceId-index anahtarıyla tutar
    // Böylece validasyon sırasında ilgili inputa error basabiliriz
    private val childAgeInputs = mutableMapOf<String, com.google.android.material.textfield.TextInputLayout>()

    inner class ExtraServiceViewHolder(
        private val binding: LayoutItemExtraServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExtraService) {
            binding.tvServiceName.text = item.name
            binding.tvServiceDescription.text = item.description
            binding.tvServicePrice.text = "${item.currencySymbol}${formatPrice(item.price)}"
            binding.ivCheck.visibility = if (item.isSelected) View.VISIBLE else View.GONE

            val isBabySeat = item.name.contains("Bebek Koltuğu", ignoreCase = true)

            // Her bind öncesi o service'e ait eski input referanslarını temizle
            childAgeInputs.keys.removeAll { it.startsWith("${item.id}-") }
            binding.layoutChildAgeContainer.removeAllViews()

            if (item.maxCount > 1) {
                // Adetli hizmetlerde sayaç görünür, gün yazısı gizlenir
                binding.layoutCounter.visibility = View.VISIBLE
                binding.tvRentalDays.visibility = View.GONE

                binding.tvQuantity.text = item.quantity.toString()

                binding.btnPlus.alpha = if (item.quantity >= item.maxCount) 0.4f else 1f
                binding.btnMinus.alpha = if (item.quantity <= 0) 0.4f else 1f

                // Adetli hizmetlerde check kutusuna tıklama kullanılmaz
                binding.cardSelect.setOnClickListener(null)

                binding.btnPlus.setOnClickListener {
                    if (item.quantity < item.maxCount) {
                        onPlusClick(item.id)
                    }
                }

                binding.btnMinus.setOnClickListener {
                    if (item.quantity > 0) {
                        onMinusClick(item.id)
                    }
                }
            } else {
                // Tek seçimlik hizmetlerde sayaç gizlenir, gün yazısı görünür
                binding.layoutCounter.visibility = View.GONE
                binding.tvRentalDays.visibility = View.VISIBLE
                binding.tvRentalDays.text = "$rentalDays gün"

                binding.cardSelect.setOnClickListener {
                    onSingleSelectClick(item.id)
                }
            }

            // Bebek koltuğu seçilmişse, adet kadar çocuk yaşı alanı göster
            if (isBabySeat && item.quantity > 0) {
                binding.layoutChildAgeContainer.visibility = View.VISIBLE

                repeat(item.quantity) { index ->
                    val childBinding = LayoutItemChildAgeBinding.inflate(
                        LayoutInflater.from(binding.root.context),
                        binding.layoutChildAgeContainer,
                        false
                    )

                    childBinding.tvChildLabel.text = "${index + 1}. Çocuk Yaşı"
                    childBinding.etChildAge.setText(item.childAges.getOrNull(index).orEmpty())

                    val key = "${item.id}-$index"
                    childAgeInputs[key] = childBinding.tilChildAge

                    childBinding.etChildAge.doAfterTextChanged { text ->
                        childBinding.tilChildAge.error = null
                        onChildAgeChanged(item.id, index, text?.toString().orEmpty())
                    }

                    binding.layoutChildAgeContainer.addView(childBinding.root)
                }
            } else {
                binding.layoutChildAgeContainer.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtraServiceViewHolder {
        val binding = LayoutItemExtraServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExtraServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExtraServiceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ExtraService>) {
        items = newItems
        notifyDataSetChanged()
    }

    // Bebek koltuğu için çocuk yaş alanlarını kontrol eder
    // Eksik varsa ilgili TextInputLayout altında hata gösterir
    fun validateChildAges(currentItems: List<ExtraService>): Boolean {
        var isValid = true

        val babySeatService = currentItems.find {
            it.name.contains("Bebek Koltuğu", ignoreCase = true)
        } ?: return true

        if (babySeatService.quantity == 0) return true

        repeat(babySeatService.quantity) { index ->
            val value = babySeatService.childAges.getOrNull(index).orEmpty().trim()
            val key = "${babySeatService.id}-$index"
            val inputLayout = childAgeInputs[key]

            if (value.isEmpty()) {
                inputLayout?.error = "Lütfen yaş girin."
                isValid = false
            } else {
                inputLayout?.error = null
            }
        }

        return isValid
    }

    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", price)
        }
    }
}