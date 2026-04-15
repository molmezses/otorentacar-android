package com.edadursun.otorentacar.ui.home

import android.app.AlertDialog
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Kullanıcının seçtiği alış / dönüş tarih-saat bilgileri
    private val pickupDateCalendar = Calendar.getInstance()
    private val dropOffDateCalendar = Calendar.getInstance()
    private val pickupTimeCalendar = Calendar.getInstance()
    private val dropOffTimeCalendar = Calendar.getInstance()

    // Home ekranına ait ViewModel
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // Ekran ilk açıldığında başlangıç kurulumları
        setupInitialDateTime()
        setupFeaturedVehicles()
        setupClicks()
        setupInitialState()

        // ViewModel state'lerini dinle ve lokasyonları çek
        observeLocations()
        setupLocationDropdowns()
        viewModel.fetchLocations()
    }

    private fun setupInitialState() {
        // Başlangıçta farklı drop-off alanı kapalı gelir
        binding.switchDrop.isChecked = false
        binding.layoutDropOffSection.visibility = View.GONE
    }

    private fun setupClicks() {
        // Drawer menüyü aç
        binding.topBar.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        // Araç Bul butonu: tarih kontrolü yapar ve araç listesine gider
        binding.btnFindCar.setOnClickListener {
            //dskmfsdgmksdgmdsmfksdgmsdh
            val pickupDateTime = getPickupDateTime()
            val dropOffDateTime = getDropOffDateTime()

            binding.tvDateValidationError.visibility = View.GONE

            // Dönüş tarihi, alış tarihinden sonra olmalı
            if (!dropOffDateTime.after(pickupDateTime)) {
                binding.tvDateValidationError.text =
                    "Dönüş tarihi, alış tarihinden sonra olmalıdır."
                binding.tvDateValidationError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putLong("pickupMillis", pickupDateTime.timeInMillis)
                putLong("dropoffMillis", dropOffDateTime.timeInMillis)
            }

            findNavController().navigate(R.id.allVehiclesFragment, bundle)
        }

        // Farklı yerde bırak switch'i açılırsa drop-off alanını göster
        binding.switchDrop.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutDropOffSection.visibility = View.VISIBLE
            } else {
                binding.layoutDropOffSection.visibility = View.GONE
            }
        }

        // Alış tarihi seçimi
        binding.ivPickupDate.setOnClickListener {
            showDatePicker(
                calendar = pickupDateCalendar,
                onDateSelected = { updatePickupDateText() }
            )
        }

        // Alış saati seçimi
        binding.ivPickupTime.setOnClickListener {
            showTimePicker(
                calendar = pickupTimeCalendar,
                onTimeSelected = { updatePickupTimeText() }
            )
        }

        // Dönüş tarihi seçimi
        binding.ivDropOffDate.setOnClickListener {
            showDatePicker(
                calendar = dropOffDateCalendar,
                onDateSelected = { updateDropOffDateText() }
            )
        }

        // Dönüş saati seçimi
        binding.ivDropOffTime.setOnClickListener {
            showTimePicker(
                calendar = dropOffTimeCalendar,
                onTimeSelected = { updateDropOffTimeText() }
            )
        }


        // Tüm araçları gör ekranına git
        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.allVehiclesFragment)
        }
    }

    // ViewModel'deki mevcut lokasyon listesini al
    private fun showLocationPicker(isPickup: Boolean) {
        val locations = viewModel.locations.value

        if (locations.isEmpty()) return

        // Dialogda göstermek için sadece isim listesini çıkar
        val locationNames = locations.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setItems(locationNames) { _, which ->
                val selectedLocation = locations[which]

                // Kullanıcının seçimine göre pickup veya dropoff state'ini güncelle
                if (isPickup) {
                    viewModel.selectPickupLocation(selectedLocation)
                } else {
                    viewModel.selectDropOffLocation(selectedLocation)
                }
            }
            .show()
    }

    // Tarih hatası gösterimini temizler
    private fun clearDateError() {
        binding.tvDateValidationError.visibility = View.GONE
    }

    //seçilen veriyi tekrar UI’a yazar
    private fun observeLocations() {
        // Seçilen pickup lokasyonu değişirse texti güncelle
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedPickupLocation.collect { location ->
                binding.actPickupLocation.setText(location?.name ?: "", false)
            }
        }

        // Seçilen dropoff lokasyonu değişirse texti güncelle
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDropOffLocation.collect { location ->
                binding.actDropOffLocation.setText(location?.name ?: "", false)
            }
        }


        // API'den gelen lokasyon listesi değişirse burada dinlenir
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.locations.collect { locations ->
                android.util.Log.d("HOME_TEST", "locations size: ${locations.size}")
            }
        }

        // Home ekranının genel loading/error durumunu dinler
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HomeUiState.Idle -> Unit
                    is HomeUiState.Loading -> Unit
                    is HomeUiState.Success -> Unit
                    is HomeUiState.Error -> {
                        // İstersen burada toast/snackbar gösterebilirsin
                    }
                }
            }
        }
    }

    private fun setupLocationDropdowns() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.locations.collect { locations ->
                android.util.Log.d("DROPDOWN_TEST", "locations geldi: ${locations.size}")

                if (locations.isEmpty()) return@collect

                val locationNames = locations.map { it.name }

                val pickupAdapter = android.widget.ArrayAdapter(
                    requireContext(),
                    R.layout.item_location_dropdown,
                    locationNames
                )

                val dropOffAdapter = android.widget.ArrayAdapter(
                    requireContext(),
                    R.layout.item_location_dropdown,
                    locationNames
                )

                binding.actPickupLocation.setAdapter(pickupAdapter)
                binding.actDropOffLocation.setAdapter(dropOffAdapter)
                binding.actPickupLocation.setDropDownBackgroundResource(R.drawable.bg_location_dropdown_item)
                binding.actDropOffLocation.setDropDownBackgroundResource(R.drawable.bg_location_dropdown_item)

                binding.actPickupLocation.setOnClickListener {
                    android.util.Log.d("DROPDOWN_TEST", "pickup tıklandı")
                    binding.actPickupLocation.showDropDown()
                }

                binding.actDropOffLocation.setOnClickListener {
                    android.util.Log.d("DROPDOWN_TEST", "dropoff tıklandı")
                    binding.actDropOffLocation.showDropDown()
                }

                binding.actPickupLocation.setOnItemClickListener { _, _, position, _ ->
                    viewModel.selectPickupLocation(locations[position])
                }

                binding.actDropOffLocation.setOnItemClickListener { _, _, position, _ ->
                    viewModel.selectDropOffLocation(locations[position])
                }
            }
        }
    }

    // İlk açılışta varsayılan tarih-saat değerlerini ayarla
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

    //Date pickerı göster
    private fun showDatePicker(
        calendar: Calendar,
        onDateSelected: () -> Unit
    ) {
        // Ortak tarih seçici
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

    //Time pickerı göster
    private fun showTimePicker(
        calendar: Calendar,
        onTimeSelected: () -> Unit
    ) {
        // Ortak saat seçici
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


    // Seçilen pickup tarihini UI'a yaz
    private fun updatePickupDateText() {
        binding.tvPickupDate.text = formatDateForCard(pickupDateCalendar)
        clearDateError()
    }

    // Seçilen dropoff tarihini UI'a yaz
    private fun updateDropOffDateText() {
        binding.tvDropOffDate.text = formatDateForCard(dropOffDateCalendar)
        clearDateError()
    }

    // Seçilen pickup saatini UI'a yaz
    private fun updatePickupTimeText() {
        binding.tvPickupTime.text = formatTimeForCard(pickupTimeCalendar)
        clearDateError()
    }

    // Seçilen dropoff saatini UI'a yaz
    private fun updateDropOffTimeText() {
        binding.tvDropOffTime.text = formatTimeForCard(dropOffTimeCalendar)
        clearDateError()
    }

    // Pickup değiştiğinde gerekirse dropoff'u mantıklı tarihe çeker
    private fun updateDropOffIfNeeded() {
        val pickup = getPickupDateTime()
        val dropoff = getDropOffDateTime()

        if (!dropoff.after(pickup)) {
            dropOffDateCalendar.timeInMillis = pickupDateCalendar.timeInMillis
            dropOffDateCalendar.add(Calendar.DAY_OF_MONTH, 1)

            dropOffTimeCalendar.set(
                Calendar.HOUR_OF_DAY,
                pickupTimeCalendar.get(Calendar.HOUR_OF_DAY)
            )
            dropOffTimeCalendar.set(Calendar.MINUTE, pickupTimeCalendar.get(Calendar.MINUTE))

            updateDropOffDateText()
            updateDropOffTimeText()
        }
    }

    // Kart içindeki tarih formatı: 09 Nis\n2026
    private fun formatDateForCard(calendar: Calendar): String {
        val day = SimpleDateFormat("dd", Locale("tr")).format(calendar.time)
        val month = SimpleDateFormat("MMM", Locale("tr")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        val year = SimpleDateFormat("yyyy", Locale("tr")).format(calendar.time)
        return "$day $month\n$year"
    }

    // Kart içindeki saat formatı: 10:00
    private fun formatTimeForCard(calendar: Calendar): String {
        return SimpleDateFormat("HH:mm", Locale("tr")).format(calendar.time)
    }

    // Pickup tarih ve saatini tek Calendar içinde birleştirir
    private fun getPickupDateTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = pickupDateCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, pickupTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, pickupTimeCalendar.get(Calendar.MINUTE))
        return calendar
    }

    // Dropoff tarih ve saatini tek Calendar içinde birleştirir
    private fun getDropOffDateTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dropOffDateCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, dropOffTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, dropOffTimeCalendar.get(Calendar.MINUTE))
        return calendar
    }


    private fun setupFeaturedVehicles() {
        // Şimdilik dummy verilerle öne çıkan araçları göster
        val vehicles = DummyDataProvider.getFeaturedVehicles()
        binding.rvFeaturedVehicles.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFeaturedVehicles.adapter = FeaturedVehicleAdapter(vehicles)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment view yok olurken binding'i temizle
        _binding = null
    }
}