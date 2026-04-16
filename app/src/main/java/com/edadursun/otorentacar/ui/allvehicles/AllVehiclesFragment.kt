package com.edadursun.otorentacar.ui.allvehicles

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.TimeZone

class AllVehiclesFragment : Fragment(R.layout.fragment_all_vehicles) {

    private var _binding: FragmentAllVehiclesBinding? = null
    private val binding get() = _binding!!

    private var originalVehicles: List<Vehicle> = emptyList()
    private var currentVehicles: List<Vehicle> = emptyList()

    private val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("Europe/Istanbul")

    // Önceki ekrandan gelen tarih ve lokasyon bilgileri
    private var pickupMillis: Long = 0L
    private var dropoffMillis: Long = 0L
    private var pickupLocationId: Int = 0
    private var dropOffLocationId: Int = 0

    private val viewModel: AllVehiclesViewModel by viewModels()

    // Kullanıcının seçtiği sıralama seçeneğini tutar
    private var selectedSortOption: String = "Önerilen"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllVehiclesBinding.bind(view)

        // Bundle içinden gelen verileri al
        pickupMillis = arguments?.getLong("pickupMillis") ?: 0L
        dropoffMillis = arguments?.getLong("dropoffMillis") ?: 0L
        pickupLocationId = arguments?.getInt("pickupLocationId") ?: 0
        dropOffLocationId = arguments?.getInt("dropOffLocationId") ?: 0

        // Ekran ilk ayarları
        setupHeader()
        setupRecyclerView()
        setupClicks()
        observeVehicles()

        // API'nin istediği tarih formatına çevir
        val pickUpDateTime = formatApiDateTime(pickupMillis)
        val dropOffDateTime = formatApiDateTime(dropoffMillis)

        // Test amaçlı loglar
        android.util.Log.d("SEARCH_PRICE_TEST", "pickupMillis = $pickupMillis")
        android.util.Log.d("SEARCH_PRICE_TEST", "dropoffMillis = $dropoffMillis")
        android.util.Log.d("SEARCH_PRICE_TEST", "pickupLocationId = $pickupLocationId")
        android.util.Log.d("SEARCH_PRICE_TEST", "dropOffLocationId = $dropOffLocationId")

        // ViewModel üzerinden araçları getir
        viewModel.fetchVehicles(
            pickUpDateTime = pickUpDateTime,
            dropOffDateTime = dropOffDateTime,
            pickUpLocationPointId = pickupLocationId.toString(),
            dropOffLocationPointId = dropOffLocationId.toString(),
            pickupMillis = pickupMillis,
            dropoffMillis = dropoffMillis
        )
    }

    // Üst kısımdaki araç sayısı ve tarih bilgisini günceller
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

    // RecyclerView başlangıç ayarları
    private fun setupRecyclerView() {
        binding.rvAllVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllVehicles.adapter = AllVehiclesAdapter(emptyList()) {
            findNavController().navigate(R.id.extrasFragment)
        }
    }

    // Yeni gelen araç listesiyle adapter'ı yeniler
    private fun refreshVehicleList(newList: List<Vehicle>) {
        currentVehicles = newList
        binding.rvAllVehicles.adapter = AllVehiclesAdapter(currentVehicles) {
            findNavController().navigate(R.id.extrasFragment)
        }
        setupHeader()
    }

    // Geri, filtre ve sıralama tıklamalarını ayarlar
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

    // ViewModel'den gelen araç listesini dinler
    private fun observeVehicles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AllVehiclesUiState.Idle -> Unit

                    is AllVehiclesUiState.Loading -> Unit

                    is AllVehiclesUiState.Success -> {
                        // Önerilen sıralama için orderNo'ya göre sırala
                        originalVehicles = state.vehicles.sortedBy { it.orderNo }
                        currentVehicles = originalVehicles
                        refreshVehicleList(originalVehicles)
                    }

                    is AllVehiclesUiState.Error -> {
                        android.util.Log.e("SEARCH_PRICE_TEST", state.message)
                    }
                }
            }
        }
    }

    // Milisaniye cinsinden tarihi API'nin istediği formata çevirir
    private fun formatApiDateTime(millis: Long): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }.format(millis)
    }

    // Sıralama seçeneklerini custom dropdown olarak gösterir
    private fun showSortMenu(anchor: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_sort_dropdown, null, false)

        val popupWindow = PopupWindow(
            popupView,
            anchor.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 0f
        }

        // Dropdown içindeki seçenek view'leri
        val recommendedView = popupView.findViewById<View>(R.id.tvRecommended)
        val priceAscView = popupView.findViewById<View>(R.id.tvPriceAsc)
        val priceDescView = popupView.findViewById<View>(R.id.tvPriceDesc)
        val alphabeticalView = popupView.findViewById<View>(R.id.tvAlphabetical)

        // Seçili olan sıralama seçeneğini görsel olarak vurgular
        fun styleSelected(view: View, isSelected: Boolean) {
            if (view is android.widget.TextView) {
                if (isSelected) {
                    view.setTextColor(requireContext().getColor(R.color.primary_green_dark))
                    view.setBackgroundResource(R.drawable.bg_vehicle_sort_dropdown)
                } else {
                    view.setTextColor(requireContext().getColor(R.color.text_primary))
                    view.background = null
                }
            }
        }

        // Mevcut seçimi işaretle
        styleSelected(recommendedView, selectedSortOption == "Önerilen")
        styleSelected(priceAscView, selectedSortOption == "Fiyat Artan")
        styleSelected(priceDescView, selectedSortOption == "Fiyat Azalan")
        styleSelected(alphabeticalView, selectedSortOption == "A'dan Z'ye")

        // Kullanıcı bir sıralama seçtiğinde label'ı değiştirir ve listeyi yeniler
        fun selectOption(label: String, sortedList: List<Vehicle>) {
            selectedSortOption = label
            binding.tvSortLabel.text = label
            refreshVehicleList(sortedList)
            popupWindow.dismiss()
        }

        // Önerilen sıralama
        recommendedView.setOnClickListener {
            selectOption("Önerilen", originalVehicles)
        }

        // Günlük fiyat artan sıralama
        priceAscView.setOnClickListener {
            selectOption(
                "Fiyat Artan",
                originalVehicles.sortedBy { parsePrice(it.dailyPrice) }
            )
        }

        // Günlük fiyat azalan sıralama
        priceDescView.setOnClickListener {
            selectOption(
                "Fiyat Azalan",
                originalVehicles.sortedByDescending { parsePrice(it.dailyPrice) }
            )
        }

        // İsme göre alfabetik sıralama
        alphabeticalView.setOnClickListener {
            selectOption(
                "A'dan Z'ye",
                originalVehicles.sortedBy { it.name }
            )
        }

        // Dropdown'ı butonun altında aç
        popupWindow.showAsDropDown(anchor, 0, 12)
    }

    // Fiyat string'inden para birimini temizleyip sayıya çevirir
    private fun parsePrice(price: String): Double {
        return price
            .replace("₺", "")
            .replace("€", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }

    // ViewBinding temizliği
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}