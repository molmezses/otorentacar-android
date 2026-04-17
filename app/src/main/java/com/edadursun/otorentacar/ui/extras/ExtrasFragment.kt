package com.edadursun.otorentacar.ui.extras

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.databinding.FragmentExtrasBinding
import com.edadursun.otorentacar.ui.extras.adapter.ExtraServicesAdapter
import kotlinx.coroutines.launch
import kotlin.math.ceil

class ExtrasFragment : Fragment(R.layout.fragment_extras) {

    // ViewBinding referansı
    private var _binding: FragmentExtrasBinding? = null
    private val binding get() = _binding!!

    // ViewModel bağlantısı
    private val viewModel: ExtrasViewModel by viewModels()

    // RecyclerView adapterı
    private lateinit var adapter: ExtraServicesAdapter

    // Önceki ekrandan gelen araç ve tarih bilgileri
    private var vehicleName: String = ""
    private var vehicleType: String = ""
    private var vehicleDailyPrice: String = ""
    private var vehicleTotalPrice: String = ""
    private var pickupMillis: Long = 0L
    private var dropoffMillis: Long = 0L
    private var rentalDays: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExtrasBinding.bind(view)

        // Bundle ile gelen verileri oku
        readArguments()

        // Ekran başlangıç ayarları
        setupHeader()
        setupRecyclerView()
        setupClicks()
        observeData()

        // API'den ek hizmetleri getir
        viewModel.fetchExtraServices()
    }

    // Önceki ekrandan gönderilen verileri alır
    private fun readArguments() {
        vehicleName = arguments?.getString("vehicleName").orEmpty()
        vehicleType = arguments?.getString("vehicleType").orEmpty()
        vehicleDailyPrice = arguments?.getString("vehicleDailyPrice").orEmpty()
        vehicleTotalPrice = arguments?.getString("vehicleTotalPrice").orEmpty()
        pickupMillis = arguments?.getLong("pickupMillis") ?: 0L
        dropoffMillis = arguments?.getLong("dropoffMillis") ?: 0L

        // Alış ve dönüş tarihine göre kiralama gün sayısını hesapla
        rentalDays = calculateRentalDays(pickupMillis, dropoffMillis)
    }

    // Üst araç kartı ve alt fiyat özetinin ilk halini doldurur
    private fun setupHeader() {
        binding.tvSelectedVehicleName.text = vehicleName
        binding.tvSelectedVehicleType.text = vehicleType
        binding.tvRentalDays.text = "$rentalDays gün kiralama"
        binding.tvVehiclePrice.text = vehicleTotalPrice
        binding.tvExtraPrice.text = "€0"
        binding.tvTotalPrice.text = vehicleTotalPrice
    }

    // RecyclerView ve adapter ayarları
    private fun setupRecyclerView() {
        adapter = ExtraServicesAdapter(
            items = emptyList(),
            rentalDays = rentalDays,
            onSingleSelectClick = { serviceId ->
                viewModel.toggleSingleSelection(serviceId)
            },
            onPlusClick = { serviceId ->
                viewModel.increaseQuantity(serviceId)
            },
            onMinusClick = { serviceId ->
                viewModel.decreaseQuantity(serviceId)
            }
        )

        binding.rvExtraServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExtraServices.adapter = adapter
    }

    // Geri tuşu ve devam et butonu tıklamaları
    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Devam Et butonuna basılınca rezervasyon detay ekranına geç
        binding.btnContinue.setOnClickListener {
            val bundle = Bundle().apply {
                putString("vehicleName", vehicleName)
                putString("vehicleType", vehicleType)
                putString("vehicleDailyPrice", vehicleDailyPrice)
                putString("vehicleTotalPrice", vehicleTotalPrice)
                putLong("pickupMillis", pickupMillis)
                putLong("dropoffMillis", dropoffMillis)
                putInt("rentalDays", rentalDays)
                putString("extraPrice", binding.tvExtraPrice.text.toString())
                putString("totalPrice", binding.tvTotalPrice.text.toString())
            }

            findNavController().navigate(R.id.reservationDetailFragment, bundle)
        }
    }

    // ViewModel'deki hizmet listesini dinler
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.services.collect { services ->
                adapter.updateList(services)
                updatePriceSummary(services)
            }
        }
    }

    // Seçilen hizmetlere göre ek hizmet toplamı ve genel toplamı hesaplar
    private fun updatePriceSummary(services: List<ExtraService>) {
        val extrasTotal = services.sumOf { service ->
            if (!service.isSelected || service.quantity == 0) {
                0.0
            } else {
                when {
                    // Adetli hizmetlerde fiyat = fiyat x adet x gün
                    service.maxCount > 1 -> service.price * service.quantity * rentalDays

                    // Günlük hizmetlerde fiyat = fiyat x gün
                    service.priceCalculationType.lowercase() == "günlük" -> service.price * rentalDays

                    // Sabit ücretli hizmetlerde sadece tek fiyat
                    else -> service.price
                }
            }
        }

        // Araç toplam fiyatını sayıya çevir
        val vehicleTotalValue = parsePrice(vehicleTotalPrice)

        // Genel toplam = araç fiyatı + ek hizmetler
        val grandTotal = vehicleTotalValue + extrasTotal

        binding.tvExtraPrice.text = "€${formatPrice(extrasTotal)}"
        binding.tvTotalPrice.text = "€${formatPrice(grandTotal)}"
    }

    // Alış ve dönüş tarihine göre kaç gün kiralama olduğunu hesaplar
    private fun calculateRentalDays(pickupMillis: Long, dropoffMillis: Long): Int {
        val diffMillis = dropoffMillis - pickupMillis
        if (diffMillis <= 0L) return 1

        val diffHours = diffMillis / (1000.0 * 60 * 60)
        return ceil(diffHours / 24.0).toInt().coerceAtLeast(1)
    }

    // Fiyat string'inden para birimlerini temizleyip sayıya çevirir
    private fun parsePrice(price: String): Double {
        return price
            .replace("₺", "")
            .replace("€", "")
            .replace("Toplam", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }

    // Double fiyat değerini ekrana uygun string'e çevirir
    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", price)
        }
    }

    // Fragment kapanırken binding temizlenir
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}