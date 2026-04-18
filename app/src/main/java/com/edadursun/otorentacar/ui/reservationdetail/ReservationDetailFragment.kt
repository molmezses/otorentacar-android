package com.edadursun.otorentacar.ui.reservationdetail

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.remote.request.AddReservationRequest
import com.edadursun.otorentacar.databinding.FragmentReservationDetailBinding
import com.edadursun.otorentacar.databinding.LayoutItemSelectedExtraBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ReservationDetailFragment : Fragment(R.layout.fragment_reservation_detail) {

    private var _binding: FragmentReservationDetailBinding? = null
    private val binding get() = _binding!!

    private val birthDateCalendar = Calendar.getInstance()
    private val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("Europe/Istanbul")

    private val viewModel: ReservationDetailViewModel by viewModels()

    // Extras ekranından gelen veriler
    private var vehicleName: String = ""
    private var vehicleInfo: String = ""
    private var vehicleTag: String = ""
    private var vehicleDailyPrice: String = ""
    private var vehicleTotalPrice: String = ""

    private var pickupMillis: Long = 0L
    private var dropoffMillis: Long = 0L
    private var rentalDays: Int = 1

    private var rentalPrice: String = ""
    private var extraPrice: String = ""
    private var totalPrice: String = ""

    // API için gerekli id alanları
    private var vehicleModelId: Int = 0
    private var pickupLocationId: Int = 0
    private var dropOffLocationId: Int = 0
    private var currencyId: String = "4"
    private var paymentMethodId: String = "3"

    // Seçilen ek hizmetler ve çocuk yaşları
    private var selectedExtras: ArrayList<String> = arrayListOf()
    private var childrenAge: ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservationDetailBinding.bind(view)

        readArguments()
        setupInitialData()
        renderSelectedExtras()
        setupClicks()
        observeReservationState()
    }

    // Extras ekranından gelen verileri alır
    private fun readArguments() {
        vehicleName = arguments?.getString("vehicleName").orEmpty()
        vehicleInfo = arguments?.getString("vehicleInfo").orEmpty()
        vehicleTag = arguments?.getString("vehicleTag").orEmpty()
        vehicleDailyPrice = arguments?.getString("vehicleDailyPrice").orEmpty()
        vehicleTotalPrice = arguments?.getString("vehicleTotalPrice").orEmpty()

        pickupMillis = arguments?.getLong("pickupMillis") ?: 0L
        dropoffMillis = arguments?.getLong("dropoffMillis") ?: 0L
        rentalDays = arguments?.getInt("rentalDays") ?: 1

        rentalPrice = arguments?.getString("rentalPrice").orEmpty()
        extraPrice = arguments?.getString("extraPrice").orEmpty()
        totalPrice = arguments?.getString("totalPrice").orEmpty()

        vehicleModelId = arguments?.getInt("vehicleModelId") ?: 0
        pickupLocationId = arguments?.getInt("pickupLocationId") ?: 0
        dropOffLocationId = arguments?.getInt("dropOffLocationId") ?: 0

        selectedExtras = arguments?.getStringArrayList("selectedExtras") ?: arrayListOf()
        childrenAge = arguments?.getStringArrayList("childrenAge") ?: arrayListOf()

    }

    // Ekran ilk açıldığında verileri UI'a basar
    private fun setupInitialData() {
        binding.tvVehicleName.text = vehicleName
        binding.tvVehicleInfo.text = vehicleInfo

        binding.tvPickupDateTime.text = formatReservationDateTime(pickupMillis)
        binding.tvDropOffDateTime.text = formatReservationDateTime(dropoffMillis)

        binding.tvRentalPrice.text = rentalPrice
        binding.tvExtraPrice.text = extraPrice
        binding.tvTotalPrice.text = totalPrice

        // Form alanları boş başlasın
        binding.etFullName.setText("")
        binding.etPhone.setText("")
        binding.etEmail.setText("")
        binding.etBirthDate.setText("")
        binding.etFlightCode.setText("")
    }

    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnCompleteReservation.setOnClickListener {
            if (validateInputs()) {
                val token = TokenStore.token.orEmpty()
                if (token.isBlank()) return@setOnClickListener

                val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
                val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
                val email = binding.etEmail.text?.toString()?.trim().orEmpty()
                val birthDate = binding.etBirthDate.text?.toString()?.trim().orEmpty()
                val flightCode = binding.etFlightCode.text?.toString()?.trim().orEmpty()

                val (name, surname) = splitFullName(fullName)

                val dynamicFields = buildDynamicFields()

                val request = AddReservationRequest(
                    token = token,
                    pickUpLocationPointId = pickupLocationId.toString(),
                    dropOffLocationPointId = dropOffLocationId.toString(),
                    vehicleModelId = vehicleModelId.toString(),
                    pickUpDateTime = formatApiDateTime(pickupMillis),
                    dropOffDateTime = formatApiDateTime(dropoffMillis),
                    name = name,
                    surname = surname,
                    phone1 = phone,
                    email = email,
                    birthDate = formatBirthDateForApi(birthDate),
                    flightNo = flightCode,
                    totalPrice = cleanPriceForApi(totalPrice),
                    currencyId = currencyId,
                    paymentMethodId = paymentMethodId,
                    dynamicFields = dynamicFields
                )
                Log.d("ADD_RESERVATION_REQUEST", request.toString())

                viewModel.addReservation(request)
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                birthDateCalendar.set(Calendar.YEAR, year)
                birthDateCalendar.set(Calendar.MONTH, month)
                birthDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val formattedDate = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
                    .format(birthDateCalendar.time)

                binding.etBirthDate.setText(formattedDate)
                binding.tilBirthDate.error = null
            },
            birthDateCalendar.get(Calendar.YEAR),
            birthDateCalendar.get(Calendar.MONTH),
            birthDateCalendar.get(Calendar.DAY_OF_MONTH)
        )

        val maxBirthDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -21)
        }

        datePickerDialog.datePicker.maxDate = maxBirthDate.timeInMillis

        datePickerDialog.show()
    }

    // Form alanlarının boş geçilmesini engeller
    private fun validateInputs(): Boolean {
        val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
        val birthDate = binding.etBirthDate.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()

        var isValid = true

        binding.tilFullName.error = null
        binding.tilPhone.error = null
        binding.tilBirthDate.error = null
        binding.tilEmail.error = null

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Lütfen ad soyad girin."
            isValid = false
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Lütfen telefon numarası girin."
            isValid = false
        }

        if (birthDate.isEmpty()) {
            binding.tilBirthDate.error = "Lütfen doğum tarihi seçin."
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Lütfen e-posta girin."
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Geçerli bir e-posta girin."
            isValid = false
        }

        return isValid
    }

    // API için full name'i name + surname olarak ayırmakta kullanacağız
    private fun splitFullName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split("\\s+".toRegex())
        if (parts.isEmpty()) return "" to ""
        if (parts.size == 1) return parts.first() to ""
        return parts.dropLast(1).joinToString(" ") to parts.last()
    }

    // Detail kartında gösterilen tarih-saat formatı
    private fun formatReservationDateTime(millis: Long): String {
        if (millis == 0L) return ""

        return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }.format(millis)
    }

    // API'nin istediği format: dd.MM.yyyy HH:mm
    private fun formatApiDateTime(millis: Long): String {
        if (millis == 0L) return ""

        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }.format(millis)
    }

    private fun observeReservationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ReservationDetailUiState.Idle -> {
                        binding.btnCompleteReservation.isEnabled = true
                        binding.btnCompleteReservation.text = "Rezervasyonu Tamamla"
                    }

                    is ReservationDetailUiState.Loading -> {
                        binding.btnCompleteReservation.isEnabled = false
                        binding.btnCompleteReservation.text = "Gönderiliyor..."
                        Log.d("ADD_RESERVATION", "Rezervasyon isteği gönderiliyor...")
                    }

                    is ReservationDetailUiState.Success -> {
                        binding.btnCompleteReservation.isEnabled = true
                        binding.btnCompleteReservation.text = "Rezervasyonu Tamamla"

                        val bundle = Bundle().apply {
                            putString("reservationCode", state.response.reservationCode)
                        }

                        findNavController().navigate(R.id.reservationSuccessFragment, bundle)
                    }

                    is ReservationDetailUiState.Error -> {
                        binding.btnCompleteReservation.isEnabled = true
                        binding.btnCompleteReservation.text = "Rezervasyonu Tamamla"

                        Log.e("ADD_RESERVATION", "Rezervasyon hatası: ${state.message}")
                    }
                }
            }
        }
    }

    private fun cleanPriceForApi(price: String): String {
        return price
            .replace("₺", "")
            .replace("€", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
    }

    private fun formatBirthDateForApi(dateText: String): String {
        return try {
            val inputFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
            val date = inputFormat.parse(dateText)
            if (date != null) outputFormat.format(date) else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun buildDynamicFields(): Map<String, String> {
        val dynamicFields = linkedMapOf<String, String>()

        // extras[id] = quantity
        val extrasCountMap = selectedExtras
            .groupingBy { it }
            .eachCount()

        extrasCountMap.forEach { (serviceId, count) ->
            dynamicFields["extras[$serviceId]"] = count.toString()
        }

        // childrenAges[1] = yaş, childrenAges[2] = yaş
        childrenAge.forEachIndexed { index, age ->
            dynamicFields["childrenAges[${index + 1}]"] = age
        }

        return dynamicFields
    }

    private fun renderSelectedExtras() {
        val extrasContainer = binding.layoutSelectedExtrasContainer
        extrasContainer.removeAllViews()

        val groupedExtras = selectedExtras.groupingBy { it }.eachCount()

        if (groupedExtras.isEmpty()) {
            binding.cardSelectedExtras.visibility = View.GONE
            return
        } else {
            binding.cardSelectedExtras.visibility = View.VISIBLE
        }

        val extraNameMap = mapOf(
            "6" to "Navigasyon Cihazı",
            "5" to "Süper Hasar Güvencesi",
            "4" to "Mini Hasar Güvencesi",
            "3" to "Ek Sürücü",
            "2" to "Bebek Koltuğu (0-3 yaş)"
        )

        val extraPriceMap = mapOf(
            "6" to "€6",
            "5" to "€6",
            "4" to "€4",
            "3" to "€5",
            "2" to "€3"
        )

        val entries = groupedExtras.entries.toList()

        entries.forEachIndexed { index, entry ->
            val itemBinding = LayoutItemSelectedExtraBinding.inflate(
                LayoutInflater.from(requireContext()),
                extrasContainer,
                false
            )

            val extraId = entry.key
            val quantity = entry.value

            itemBinding.tvExtraName.text = extraNameMap[extraId] ?: "Ek Hizmet"
            itemBinding.tvExtraPrice.text = extraPriceMap[extraId] ?: "€0"
            itemBinding.tvExtraQuantity.text = "Adet: $quantity"

            itemBinding.viewDivider.visibility =
                if (index == entries.lastIndex) View.GONE else View.VISIBLE

            extrasContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}