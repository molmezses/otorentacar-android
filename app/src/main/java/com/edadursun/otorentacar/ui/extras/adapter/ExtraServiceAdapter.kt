package com.edadursun.otorentacar.ui.extras.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.databinding.LayoutItemExtraServiceBinding

class ExtraServiceAdapter(
    private val items: MutableList<ExtraService>,
    private val onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<ExtraServiceAdapter.ExtraServiceViewHolder>() {

    inner class ExtraServiceViewHolder(
        private val binding: LayoutItemExtraServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExtraService) {
            binding.tvServiceName.text = item.name
            binding.tvServiceDescription.text = item.description
            binding.tvServicePrice.text = item.price
            binding.tvQuantity.text = item.quantity.toString()

            updateSelectionUI(item)

            binding.btnPlus.setOnClickListener {
                item.quantity += 1
                binding.tvQuantity.text = item.quantity.toString()
                updateSelectionUI(item)
                onQuantityChanged()
            }

            binding.btnMinus.setOnClickListener {
                if (item.quantity > 0) {
                    item.quantity -= 1
                    binding.tvQuantity.text = item.quantity.toString()
                    updateSelectionUI(item)
                    onQuantityChanged()
                }
            }
        }

        private fun updateSelectionUI(item: ExtraService) {
            if (item.quantity > 0) {
                binding.ivCheck.visibility = View.VISIBLE
                binding.cardSelect.setCardBackgroundColor(
                    binding.root.context.getColor(R.color.primary_green_light)
                )
                binding.cardSelect.strokeWidth = 0
            } else {
                binding.ivCheck.visibility = View.GONE
                binding.cardSelect.setCardBackgroundColor(
                    binding.root.context.getColor(R.color.bg_screen)
                )
                binding.cardSelect.strokeWidth = 2
                binding.cardSelect.strokeColor =
                    binding.root.context.getColor(R.color.bg_divider)
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
}