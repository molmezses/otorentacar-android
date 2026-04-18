package com.edadursun.otorentacar.ui.bookingdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.remote.response.ReservationExtraItemResponse
import com.edadursun.otorentacar.databinding.FragmentBookingDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BookingDetailFragment : Fragment(R.layout.fragment_booking_detail) {

    // ViewBinding referansı
    private var _binding: FragmentBookingDetailBinding? = null
    private val binding get() = _binding!!

    // Önceki ekrandan gelen rezervasyon bilgileri
    private var reservationCode: String = ""
    private var reservationStatus: String = ""
    private var vehicleName: String = ""
    private var vehicleInfo: String = ""
    private var pickupDateTime: String = ""
    private var dropOffDateTime: String = ""
    private var fullName: String = ""
    private var phone: String = ""
    private var birthDate: String = ""
    private var email: String = ""
    private var flightCode: String = ""
    private var totalPrice: Double = 0.0

    private var extraList: ArrayList<ReservationExtraItemResponse> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingDetailBinding.bind(view)

        // Bundle ile gelen verileri oku
        readArguments()

        // Ekranı doldur
        setupUi()

        renderExtras()

        // Tıklama olaylarını ayarla
        setupClicks()
    }

    // MyBookings ekranından gelen verileri alır
    private fun readArguments() {
        reservationCode = arguments?.getString("reservationCode").orEmpty()
        reservationStatus = arguments?.getString("reservationStatus").orEmpty()
        vehicleName = arguments?.getString("vehicleName").orEmpty()
        vehicleInfo = arguments?.getString("vehicleInfo").orEmpty()
        pickupDateTime = arguments?.getString("pickupDateTime").orEmpty()
        dropOffDateTime = arguments?.getString("dropOffDateTime").orEmpty()
        fullName = arguments?.getString("fullName").orEmpty()
        phone = arguments?.getString("phone").orEmpty()
        birthDate = arguments?.getString("birthDate").orEmpty()
        email = arguments?.getString("email").orEmpty()
        flightCode = arguments?.getString("flightCode").orEmpty()
        totalPrice = arguments?.getDouble("totalPrice") ?: 0.0

        @Suppress("DEPRECATION")
        extraList =
            arguments?.getSerializable("extraList") as? ArrayList<ReservationExtraItemResponse>
                ?: arrayListOf()
    }


    // Gelen verileri ekrana basar
    private fun setupUi() {
        val (name, surname) = splitFullName(fullName)

        binding.tvReservationCode.text = reservationCode
        binding.tvReservationStatus.text = reservationStatus

        binding.tvVehicleName.text = vehicleName
        binding.tvVehicleInfo.text = vehicleInfo

        binding.tvPickupDateTime.text = formatReservationDateTime(pickupDateTime)
        binding.tvDropOffDateTime.text = formatReservationDateTime(dropOffDateTime)

        binding.tvName.text = name
        binding.tvSurname.text = surname
        binding.tvPhone.text = phone
        binding.tvBirthDate.text = birthDate
        binding.tvEmail.text = email
        binding.tvFlightCode.text = if (flightCode.isBlank()) "-" else flightCode

        val extrasTotal = extraList.sumOf { it.extra.price * it.count }
        val rentalPrice = (totalPrice - extrasTotal).coerceAtLeast(0.0)

        binding.tvRentalPrice.text = "€${formatPrice(rentalPrice)}"
        binding.tvTaxPrice.text = "€${formatPrice(extrasTotal)}"
        binding.tvTotalPrice.text = "€${formatPrice(totalPrice)}"
    }

    private fun renderExtras() {
        binding.layoutExtrasContainer.removeAllViews()

        if (extraList.isEmpty()) {
            binding.cardExtras.visibility = View.GONE
            return
        }

        binding.cardExtras.visibility = View.VISIBLE

        extraList.forEachIndexed { index, item ->
            val itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_item_selected_extra, binding.layoutExtrasContainer, false)

            val tvExtraName = itemView.findViewById<android.widget.TextView>(R.id.tvExtraName)
            val tvExtraPrice = itemView.findViewById<android.widget.TextView>(R.id.tvExtraPrice)
            val tvExtraQuantity = itemView.findViewById<android.widget.TextView>(R.id.tvExtraQuantity)
            val viewDivider = itemView.findViewById<View>(R.id.viewDivider)

            tvExtraName.text = item.extra.name
            tvExtraPrice.text = "€${formatPrice(item.extra.price)}"
            tvExtraQuantity.text = "Adet: ${item.count}"
            viewDivider.visibility = if (index == extraList.lastIndex) View.GONE else View.VISIBLE

            binding.layoutExtrasContainer.addView(itemView)
        }
    }

    // Geri butonunu çalıştırır
    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.myBookingsFragment)
        }

        binding.cardBack.setOnClickListener {
            findNavController().navigate(R.id.myBookingsFragment)
        }
    }

    // Ad soyadı iki ayrı alana böler
    private fun splitFullName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split("\\s+".toRegex())
        if (parts.isEmpty()) return "" to ""
        if (parts.size == 1) return parts.first() to ""
        return parts.dropLast(1).joinToString(" ") to parts.last()
    }

    // API'den gelen tarih formatını ekrana uygun hale getirir
    private fun formatReservationDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr"))
            val parsedDate = inputFormat.parse(dateTime)
            if (parsedDate != null) outputFormat.format(parsedDate) else dateTime
        } catch (e: Exception) {
            dateTime
        }
    }

    // Double fiyatı ekrana uygun string formatına çevirir
    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", price)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}