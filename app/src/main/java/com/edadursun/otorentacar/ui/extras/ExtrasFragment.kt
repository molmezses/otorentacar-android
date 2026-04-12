package com.edadursun.otorentacar.ui.extras

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.local.DummyDataProvider
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.data.model.InsurancePackage
import com.edadursun.otorentacar.databinding.FragmentExtrasBinding
import com.edadursun.otorentacar.ui.extras.adapter.ExtraServiceAdapter
import com.edadursun.otorentacar.ui.extras.adapter.InsurancePackageAdapter

class ExtrasFragment : Fragment(R.layout.fragment_extras) {

    private var _binding: FragmentExtrasBinding? = null
    private val binding get() = _binding!!

    private var insurancePackages: MutableList<InsurancePackage> = mutableListOf()
    private var extraServices: MutableList<ExtraService> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExtrasBinding.bind(view)

        insurancePackages = DummyDataProvider.getInsurancePackages().toMutableList()
        extraServices = DummyDataProvider.getExtraServices()

        setupVehicleSummary()
        setupInsuranceRecycler()
        setupExtrasRecycler()
        setupClicks()
        updateSummary()
    }

    private fun setupVehicleSummary() {
        binding.tvSelectedVehicleName.text = "Renault Clio"
        binding.tvSelectedVehicleType.text = "Economy Hatchback"
        //binding.tvRentalDays.text = getString(R.string.extras_rental_days)
    }

    private fun setupInsuranceRecycler() {
        binding.rvInsurancePackages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInsurancePackages.adapter = InsurancePackageAdapter(insurancePackages) {
            updateSummary()
        }
    }

    private fun setupExtrasRecycler() {
        binding.rvExtraServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExtraServices.adapter = ExtraServiceAdapter(extraServices) {
            updateSummary()
        }
    }

    private fun updateSummary() {
        val vehiclePrice = 3750
        val extraPrice = calculateExtraPrice()
        val total = vehiclePrice + extraPrice

        binding.tvVehiclePrice.text = "₺3.750"
        binding.tvExtraPrice.text = formatPrice(extraPrice)
        binding.tvTotalPrice.text = formatPrice(total)
    }

    private fun calculateExtraPrice(): Int {
        var total = 0

        insurancePackages.filter { it.isSelected }.forEach {
            total += parsePrice(it.price) * 3
        }

        extraServices.forEach {
            total += parsePrice(it.price) * it.quantity * 3
        }

        return total
    }

    private fun parsePrice(price: String): Int {
        return price
            .replace("₺", "")
            .replace(".", "")
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0
    }

    private fun formatPrice(value: Int): String {
        return "₺" + "%,d".format(value).replace(",", ".")
    }

    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.reservationDetailFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}