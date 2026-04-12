package com.edadursun.otorentacar.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.local.DummyDataProvider
import com.edadursun.otorentacar.databinding.FragmentHomeBinding
import com.edadursun.otorentacar.ui.home.adapter.FeaturedVehicleAdapter
import com.edadursun.otorentacar.ui.main.MainActivity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val pickupDateCalendar = Calendar.getInstance()
    private val dropOffDateCalendar = Calendar.getInstance()
    private val pickupTimeCalendar = Calendar.getInstance()
    private val dropOffTimeCalendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupInitialDateTime()
        setupFeaturedVehicles()
        setupClicks()
        setupInitialState()
    }

    private fun setupInitialState() {
        binding.switchDrop.isChecked = false
        binding.layoutDropOffSection.visibility = View.GONE
    }

    private fun setupClicks() {
        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.btnFindCar.setOnClickListener {
            val pickupDateTime = getPickupDateTime()
            val dropOffDateTime = getDropOffDateTime()

            binding.tvDateValidationError.visibility = View.GONE

            if (!dropOffDateTime.after(pickupDateTime)) {
                binding.tvDateValidationError.text = "Dönüş tarihi, alış tarihinden sonra olmalıdır."
                binding.tvDateValidationError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putLong("pickupMillis", pickupDateTime.timeInMillis)
                putLong("dropoffMillis", dropOffDateTime.timeInMillis)
            }

            findNavController().navigate(R.id.allVehiclesFragment, bundle)
        }

        binding.switchDrop.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutDropOffSection.visibility = View.VISIBLE
            } else {
                binding.layoutDropOffSection.visibility = View.GONE
            }
        }

        binding.ivPickupDate.setOnClickListener {
            showDatePicker(
                calendar = pickupDateCalendar,
                onDateSelected = { updatePickupDateText() }
            )
        }

        binding.ivPickupTime.setOnClickListener {
            showTimePicker(
                calendar = pickupTimeCalendar,
                onTimeSelected = { updatePickupTimeText() }
            )
        }

        binding.ivDropOffDate.setOnClickListener {
            showDatePicker(
                calendar = dropOffDateCalendar,
                onDateSelected = { updateDropOffDateText() }
            )
        }

        binding.ivDropOffTime.setOnClickListener {
            showTimePicker(
                calendar = dropOffTimeCalendar,
                onTimeSelected = { updateDropOffTimeText() }
            )
        }

        //Tümünü gör
        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.allVehiclesFragment)
        }
    }

    private fun clearDateError() {
        binding.tvDateValidationError.visibility = View.GONE
    }

    private fun setupFeaturedVehicles() {
        val vehicles = DummyDataProvider.getFeaturedVehicles()
        binding.rvFeaturedVehicles.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFeaturedVehicles.adapter = FeaturedVehicleAdapter(vehicles)
    }



    private fun setupInitialDateTime() {
        val now = Calendar.getInstance()

        pickupDateCalendar.timeInMillis = now.timeInMillis
        pickupTimeCalendar.timeInMillis = now.timeInMillis

        dropOffDateCalendar.timeInMillis = now.timeInMillis
        dropOffDateCalendar.add(Calendar.DAY_OF_MONTH, 3)

        dropOffTimeCalendar.timeInMillis = now.timeInMillis

        updatePickupDateText()
        updatePickupTimeText()
        updateDropOffDateText()
        updateDropOffTimeText()
    }

    private fun showDatePicker(
        calendar: Calendar,
        onDateSelected: () -> Unit
    ) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(
        calendar: Calendar,
        onTimeSelected: () -> Unit
    ) {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updatePickupDateText() {
        binding.tvPickupDate.text = formatDateForCard(pickupDateCalendar)
        clearDateError()
    }

    private fun updateDropOffDateText() {
        binding.tvDropOffDate.text = formatDateForCard(dropOffDateCalendar)
        clearDateError()
    }

    private fun updatePickupTimeText() {
        binding.tvPickupTime.text = formatTimeForCard(pickupTimeCalendar)
        clearDateError()
    }

    private fun updateDropOffTimeText() {
        binding.tvDropOffTime.text = formatTimeForCard(dropOffTimeCalendar)
        clearDateError()
    }

    private fun updateDropOffIfNeeded() {
        val pickup = getPickupDateTime()
        val dropoff = getDropOffDateTime()

        if (!dropoff.after(pickup)) {
            dropOffDateCalendar.timeInMillis = pickupDateCalendar.timeInMillis
            dropOffDateCalendar.add(Calendar.DAY_OF_MONTH, 1)

            dropOffTimeCalendar.set(Calendar.HOUR_OF_DAY, pickupTimeCalendar.get(Calendar.HOUR_OF_DAY))
            dropOffTimeCalendar.set(Calendar.MINUTE, pickupTimeCalendar.get(Calendar.MINUTE))

            updateDropOffDateText()
            updateDropOffTimeText()
        }
    }

    private fun formatDateForCard(calendar: Calendar): String {
        val day = SimpleDateFormat("dd", Locale("tr")).format(calendar.time)
        val month = SimpleDateFormat("MMM", Locale("tr")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        val year = SimpleDateFormat("yyyy", Locale("tr")).format(calendar.time)
        return "$day $month\n$year"
    }

    private fun formatTimeForCard(calendar: Calendar): String {
        return SimpleDateFormat("HH:mm", Locale("tr")).format(calendar.time)
    }

    private fun getPickupDateTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = pickupDateCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, pickupTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, pickupTimeCalendar.get(Calendar.MINUTE))
        return calendar
    }

    private fun getDropOffDateTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dropOffDateCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, dropOffTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, dropOffTimeCalendar.get(Calendar.MINUTE))
        return calendar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}