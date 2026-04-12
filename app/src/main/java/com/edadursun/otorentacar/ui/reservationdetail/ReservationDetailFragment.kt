package com.edadursun.otorentacar.ui.reservationdetail

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.util.Patterns
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.FragmentReservationDetailBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservationDetailFragment : Fragment(R.layout.fragment_reservation_detail) {

    private var _binding: FragmentReservationDetailBinding? = null
    private val binding get() = _binding!!

    private val birthDateCalendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservationDetailBinding.bind(view)

        setupInitialData()
        setupClicks()
    }

    private fun setupInitialData() {
        binding.etFullName.setText("Mehmet Yılmaz")
        binding.etPhone.setText("+90 5XX XXX XX XX")
        binding.etEmail.setText("mehmet@email.com")
        binding.etBirthDate.setText("9 Apr 2026")
        binding.etFlightCode.setText("TK 1923")
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
                // şimdilik sadece başarılı kabul ediyoruz
                // sonra başarı ekranı ya da ödeme ekranı açabiliriz
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
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
        ).show()
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}