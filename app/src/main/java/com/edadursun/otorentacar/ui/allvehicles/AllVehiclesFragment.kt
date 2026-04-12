package com.edadursun.otorentacar.ui.allvehicles

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.local.DummyDataProvider
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.FragmentAllVehiclesBinding
import com.edadursun.otorentacar.ui.allvehicles.adapter.AllVehiclesAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AllVehiclesFragment : Fragment(R.layout.fragment_all_vehicles) {

    private var _binding: FragmentAllVehiclesBinding? = null
    private val binding get() = _binding!!

    private var originalVehicles: List<Vehicle> = emptyList()
    private var currentVehicles: List<Vehicle> = emptyList()

    private var pickupMillis: Long = 0L
    private var dropoffMillis: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllVehiclesBinding.bind(view)

        pickupMillis = arguments?.getLong("pickupMillis") ?: 0L
        dropoffMillis = arguments?.getLong("dropoffMillis") ?: 0L

        originalVehicles = DummyDataProvider.getVehicleList()
        currentVehicles = originalVehicles

        setupHeader()
        setupRecyclerView()
        setupClicks()
    }

    private fun setupHeader() {
        val vehicleCount = currentVehicles.size
        binding.tvVehicleCount.text = "$vehicleCount araç bulundu"

        if (pickupMillis != 0L) {
            val pickupCalendar = Calendar.getInstance().apply { timeInMillis = pickupMillis }
            val formatted = SimpleDateFormat("dd MMM yyyy", Locale("tr")).format(pickupCalendar.time)
            binding.tvSelectedDate.text = "• $formatted"
        } else {
            binding.tvSelectedDate.text = "• 09 Nis 2026"
        }
    }

    private fun setupRecyclerView() {
        binding.rvAllVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllVehicles.adapter = AllVehiclesAdapter(currentVehicles) {
            findNavController().navigate(R.id.extrasFragment)
        }
    }

    private fun refreshVehicleList(newList: List<Vehicle>) {
        currentVehicles = newList
        binding.rvAllVehicles.adapter = AllVehiclesAdapter(currentVehicles) {
            findNavController().navigate(R.id.extrasFragment)
        }
        setupHeader()
    }

    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Filtreleme sonraki adımda eklenecek", Toast.LENGTH_SHORT).show()
        }

        binding.cardSort.setOnClickListener {
            showSortMenu(it)
        }
    }

    private fun showSortMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menu.add(0, 1, 0, "Önerilen")
        popupMenu.menu.add(0, 2, 1, "Fiyat Artan")
        popupMenu.menu.add(0, 3, 2, "Fiyat Azalan")
        popupMenu.menu.add(0, 4, 3, "A'dan Z'ye")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    binding.tvSortLabel.text = "Önerilen"
                    refreshVehicleList(originalVehicles)
                    true
                }

                2 -> {
                    binding.tvSortLabel.text = "Fiyat Artan"
                    refreshVehicleList(
                        originalVehicles.sortedBy { parsePrice(it.totalPrice) }
                    )
                    true
                }

                3 -> {
                    binding.tvSortLabel.text = "Fiyat Azalan"
                    refreshVehicleList(
                        originalVehicles.sortedByDescending { parsePrice(it.totalPrice) }
                    )
                    true
                }

                4 -> {
                    binding.tvSortLabel.text = "A'dan Z'ye"
                    refreshVehicleList(
                        originalVehicles.sortedBy { it.name }
                    )
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }



    private fun parsePrice(price: String): Int {
        return price
            .replace("₺", "")
            .replace(".", "")
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}