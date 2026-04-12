package com.edadursun.otorentacar.ui.extras.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.InsurancePackage
import com.edadursun.otorentacar.databinding.LayoutItemInsurancePackageBinding

class InsurancePackageAdapter(
    private val items: MutableList<InsurancePackage>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<InsurancePackageAdapter.InsuranceViewHolder>() {

    inner class InsuranceViewHolder(
        private val binding: LayoutItemInsurancePackageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InsurancePackage) {
            binding.tvPackageName.text = item.name
            binding.tvPackageDescription.text = item.description
            binding.tvPackagePrice.text = item.price
            binding.tvPackageDays.text = item.dayText

            updateSelectionUI(item)

            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    item.isSelected = !item.isSelected
                    notifyItemChanged(adapterPosition)
                    onSelectionChanged()
                }
            }
        }

        private fun updateSelectionUI(item: InsurancePackage) {
            if (item.isSelected) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceViewHolder {
        val binding = LayoutItemInsurancePackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InsuranceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InsuranceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}