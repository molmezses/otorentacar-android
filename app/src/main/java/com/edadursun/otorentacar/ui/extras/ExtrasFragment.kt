package com.edadursun.otorentacar.ui.extras

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.core.currency.CurrencyFormatter
import com.edadursun.otorentacar.core.currency.DisplayCurrency
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.databinding.FragmentExtrasBinding
import com.edadursun.otorentacar.ui.extras.adapter.ExtraServicesAdapter
import kotlinx.coroutines.launch
import kotlin.math.ceil

class ExtrasFragment : Fragment(R.layout.fragment_extras) {

    // ViewBinding referansı
    private var _binding: FragmentExtrasBinding? = null
    private val binding get() = _binding!!

    // Extras ekranına ait ViewModel
    private val viewModel: ExtrasViewModel by viewModels()

    // RecyclerView adapterı
    private lateinit var adapter: ExtraServicesAdapter

    // Önceki ekrandan gelen araç bilgileri
    private var vehicleName: String = ""
    private var vehicleType: String = ""
    private var vehicleTransmission: String = ""
    private var vehicleFuel: String = ""
    private var vehicleTag: String = ""
    private var vehicleDailyPrice: String = ""
    private var vehicleTotalPrice: String = ""
    private var vehicleDailyPriceAmount: Double = 0.0
    private var vehicleTotalPriceAmount: Double = 0.0
    private var vehicleCurrencyCode: String = "EUR"
    private var vehicleCurrencyId: Int = 4
    private var totalPriceForApi: String = ""
    private var vehicleImageUrl: String = ""
    private var displayCurrency: DisplayCurrency = DisplayCurrency.EURO

    // Önceki ekrandan gelen tarih bilgileri
    private var pickupMillis: Long = 0L
    private var dropoffMillis: Long = 0L
    private var rentalDays: Int = 1

    // API için gerekli id alanları
    private var vehicleModelId: Int = 0
    private var pickupLocationId: Int = 0
    private var dropOffLocationId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExtrasBinding.bind(view)

        // Önceki ekrandan gelen verileri oku
        readArguments()

        // Ekran kurulumları
        setupHeader()
        setupRecyclerView()
        setupClicks()
        observeData()

        // API'den ek hizmetleri çek
        viewModel.fetchExtraServices()
    }

    // Bundle ile gelen verileri alır
    private fun readArguments() {
        vehicleName = arguments?.getString("vehicleName").orEmpty()
        vehicleType = arguments?.getString("vehicleType").orEmpty()
        vehicleTransmission = arguments?.getString("vehicleTransmission").orEmpty()
        vehicleFuel = arguments?.getString("vehicleFuel").orEmpty()
        vehicleTag = arguments?.getString("vehicleTag").orEmpty()
        vehicleDailyPrice = arguments?.getString("vehicleDailyPrice").orEmpty()
        vehicleTotalPrice = arguments?.getString("vehicleTotalPrice").orEmpty()
        vehicleDailyPriceAmount = arguments?.getDouble("vehicleDailyPriceAmount") ?: 0.0
        vehicleTotalPriceAmount = arguments?.getDouble("vehicleTotalPriceAmount") ?: 0.0
        vehicleCurrencyCode = arguments?.getString("vehicleCurrencyCode").orEmpty().ifBlank { "EUR" }
        vehicleCurrencyId = arguments?.getInt("vehicleCurrencyId") ?: 4
        displayCurrency = arguments?.getString("displayCurrency")
            ?.let { runCatching { DisplayCurrency.valueOf(it) }.getOrNull() }
            ?: DisplayCurrency.EURO

        pickupMillis = arguments?.getLong("pickupMillis") ?: 0L
        dropoffMillis = arguments?.getLong("dropoffMillis") ?: 0L

        vehicleModelId = arguments?.getInt("vehicleModelId") ?: 0
        pickupLocationId = arguments?.getInt("pickupLocationId") ?: 0
        dropOffLocationId = arguments?.getInt("dropOffLocationId") ?: 0
        vehicleImageUrl = arguments?.getString("vehicleImageUrl").orEmpty()

        // Alış ve dönüşe göre kaç gün kiralama olduğunu hesapla
        rentalDays = calculateRentalDays(pickupMillis, dropoffMillis)

        android.util.Log.d(
            "EXTRAS_ARGS",
            "vehicleModelId=$vehicleModelId, pickupLocationId=$pickupLocationId, dropOffLocationId=$dropOffLocationId"
        )

    }


    // Üstteki seçili araç kartı ve ilk fiyat alanlarını doldurur
    private fun setupHeader() {
        binding.tvSelectedVehicleName.text = vehicleName
        binding.tvSelectedVehicleType.text = vehicleType
        binding.tvRentalDays.text = getString(R.string.rental_days_booking_format, rentalDays)
        binding.tvVehiclePrice.text = vehicleTotalPrice
        binding.tvExtraPrice.text = formatDisplayPrice(0.0)
        binding.tvTotalPrice.text = vehicleTotalPrice
        totalPriceForApi = formatPlainPrice(
            if (vehicleTotalPriceAmount > 0.0) {
                CurrencyFormatter.convertAmount(
                    amount = vehicleTotalPriceAmount,
                    sourceCurrencyCode = vehicleCurrencyCode,
                    displayCurrency = DisplayCurrency.EURO
                )
            } else {
                parseDisplayPrice(vehicleTotalPrice)
            }
        )

        if (vehicleImageUrl.isNotBlank()) {
            Glide.with(requireContext())
                .load(vehicleImageUrl)
                .placeholder(R.drawable.ic_directions_car)
                .error(R.drawable.ic_directions_car)
                .into(binding.ivSelectedVehicle)
        } else {
            binding.ivSelectedVehicle.setImageResource(R.drawable.ic_directions_car)
        }
    }

    // RecyclerView ve adapter bağlantısını kurar
    private fun setupRecyclerView() {
        adapter = ExtraServicesAdapter(
            items = emptyList(),
            rentalDays = rentalDays,
            displayCurrency = displayCurrency,
            onSingleSelectClick = { serviceId ->
                viewModel.toggleSingleSelection(serviceId)
            },
            onPlusClick = { serviceId ->
                viewModel.increaseQuantity(serviceId)
            },
            onMinusClick = { serviceId ->
                viewModel.decreaseQuantity(serviceId)
            },
            onChildAgeChanged = { serviceId, index, value ->
                viewModel.updateChildAge(serviceId, index, value)
            }
        )

        binding.rvExtraServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExtraServices.adapter = adapter
    }

    // Geri ve devam et butonlarının tıklamalarını yönetir
    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            val currentServices = viewModel.services.value

            // Bebek koltuğu seçildiyse çocuk yaş alanları dolu mu kontrol et
            if (!adapter.validateChildAges(currentServices)) return@setOnClickListener

            // Reservation Detail ekranına taşınacak verileri hazırla
            val bundle = Bundle().apply {
                putString("vehicleName", vehicleName)
                putString("vehicleType", vehicleType)
                putString("vehicleDailyPrice", vehicleDailyPrice)
                putString("vehicleTotalPrice", vehicleTotalPrice)
                putString("displayCurrency", displayCurrency.name)
                putLong("pickupMillis", pickupMillis)
                putLong("dropoffMillis", dropoffMillis)
                putInt("rentalDays", rentalDays)
                putString("extraPrice", binding.tvExtraPrice.text.toString())
                putString("totalPrice", binding.tvTotalPrice.text.toString())
                putString("totalPriceForApi", totalPriceForApi)
                putString("vehicleInfo", "$vehicleTransmission | $vehicleFuel")
                putString("vehicleTag", vehicleTag)
                putString("rentalPrice", binding.tvVehiclePrice.text.toString())
                putString("vehicleImageUrl", vehicleImageUrl)

                // API tarafında da kullanılacak id bilgileri
                putInt("vehicleModelId", vehicleModelId)
                putInt("pickupLocationId", pickupLocationId)
                putInt("dropOffLocationId", dropOffLocationId)
                putString("currencyId", vehicleCurrencyId.toString())

                // Seçilen ek hizmetleri ve çocuk yaşlarını gönder
                putStringArrayList("selectedExtras", ArrayList(buildSelectedExtras(currentServices)))
                putStringArrayList("childrenAge", ArrayList(buildChildrenAges(currentServices)))
            }

            findNavController().navigate(R.id.reservationDetailFragment, bundle)
        }
    }

    // Seçilen ek hizmetlerin id listesini oluşturur
    // Adetli hizmetlerde id, adet kadar eklenir
    private fun buildSelectedExtras(services: List<ExtraService>): List<String> {
        val selectedExtras = mutableListOf<String>()

        services.forEach { service ->
            if (service.isSelected && service.quantity > 0) {
                repeat(service.quantity) {
                    selectedExtras.add(service.id.toString())
                }
            }
        }

        return selectedExtras
    }

    // Bebek koltuğu seçildiyse girilen çocuk yaşlarını döndürür
    private fun buildChildrenAges(services: List<ExtraService>): List<String> {
        val babySeatService = services.find {
            it.name.contains("Bebek Koltuğu", ignoreCase = true)
        } ?: return emptyList()

        return babySeatService.childAges.filter { it.isNotBlank() }
    }

    // ViewModel'den gelen ek hizmet listesini dinler
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.services.collect { services ->
                adapter.updateList(services)
                updatePriceSummary(services)
            }
        }
    }

    // Seçilen hizmetlere göre ek hizmet toplamını ve genel toplamı hesaplar
    private fun updatePriceSummary(services: List<ExtraService>) {
        val extrasTotal = services.sumOf { service ->
            if (!service.isSelected || service.quantity == 0) {
                0.0
            } else {
                val serviceTotal = when {
                    // Adetli hizmetlerde fiyat = fiyat x adet x gün
                    service.maxCount > 1 -> service.price * service.quantity * rentalDays

                    // Günlük hizmetlerde fiyat = fiyat x gün
                    isDailyPrice(service) -> service.price * rentalDays

                    // Sabit fiyatlı hizmetlerde tek ücret alınır
                    else -> service.price
                }

                CurrencyFormatter.convertAmount(
                    amount = serviceTotal,
                    sourceCurrencyCode = service.currencyCode,
                    displayCurrency = displayCurrency
                )
            }
        }

        // Araç toplam fiyatını sayıya çevir
        val extrasTotalForApi = services.sumOf { service ->
            if (!service.isSelected || service.quantity == 0) {
                0.0
            } else {
                val serviceTotal = when {
                    service.maxCount > 1 -> service.price * service.quantity * rentalDays
                    isDailyPrice(service) -> service.price * rentalDays
                    else -> service.price
                }

                CurrencyFormatter.convertAmount(
                    amount = serviceTotal,
                    sourceCurrencyCode = service.currencyCode,
                    displayCurrency = DisplayCurrency.EURO
                )
            }
        }

        val vehicleTotalValue = if (vehicleTotalPriceAmount > 0.0) {
            CurrencyFormatter.convertAmount(
                amount = vehicleTotalPriceAmount,
                sourceCurrencyCode = vehicleCurrencyCode,
                displayCurrency = displayCurrency
            )
        } else {
            parseDisplayPrice(vehicleTotalPrice)
        }
        val vehicleTotalForApi = if (vehicleTotalPriceAmount > 0.0) {
            CurrencyFormatter.convertAmount(
                amount = vehicleTotalPriceAmount,
                sourceCurrencyCode = vehicleCurrencyCode,
                displayCurrency = DisplayCurrency.EURO
            )
        } else {
            parseDisplayPrice(vehicleTotalPrice)
        }

        // Genel toplam = araç + ekstra hizmetler
        val grandTotal = vehicleTotalValue + extrasTotal
        totalPriceForApi = formatPlainPrice(vehicleTotalForApi + extrasTotalForApi)

        binding.tvExtraPrice.text = formatDisplayPrice(extrasTotal)
        binding.tvTotalPrice.text = formatDisplayPrice(grandTotal)
    }

    // Milisaniye farkına göre kiralama gün sayısını hesaplar
    private fun calculateRentalDays(pickupMillis: Long, dropoffMillis: Long): Int {
        val diffMillis = dropoffMillis - pickupMillis
        if (diffMillis <= 0L) return 1

        val diffHours = diffMillis / (1000.0 * 60 * 60)
        return ceil(diffHours / 24.0).toInt().coerceAtLeast(1)
    }

    // Fiyat string'ini sayıya çevirir
    private fun isDailyPrice(service: ExtraService): Boolean {
        val type = service.priceCalculationType.lowercase()
        return type.contains("daily") || (type.contains("g") && type.contains("nl"))
    }

    private fun parseDisplayPrice(price: String): Double {
        val cleaned = price
            .replace("₺", "")
            .replace("€", "")
            .replace("â‚º", "")
            .replace("â‚¬", "")
            .replace("Toplam", "")
            .trim()

        val normalized = when (displayCurrency) {
            DisplayCurrency.TL -> cleaned.replace(".", "").replace(",", ".")
            DisplayCurrency.EURO -> cleaned.replace(",", "")
        }

        return normalized.toDoubleOrNull() ?: 0.0
    }

    // Double fiyatı ekrana uygun string formatına çevirir
    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", price)
        }
    }

    // Fragment kapanırken binding temizlenir
    private fun formatDisplayPrice(amount: Double): String {
        return CurrencyFormatter.format(
            amount = amount,
            sourceCurrencyCode = CurrencyFormatter.currencyCodeFor(displayCurrency),
            displayCurrency = displayCurrency
        )
    }

    private fun formatPlainPrice(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", amount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
