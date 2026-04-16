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
import java.util.TimeZone

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Kullanıcının seçtiği alış / dönüş tarih-saat bilgileri
    private val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("Europe/Istanbul")
    private val pickupDateCalendar = Calendar.getInstance(turkeyTimeZone)
    private val dropOffDateCalendar = Calendar.getInstance(turkeyTimeZone)
    private val pickupTimeCalendar = Calendar.getInstance(turkeyTimeZone)
    private val dropOffTimeCalendar = Calendar.getInstance(turkeyTimeZone)

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

            val pickupLocation = viewModel.selectedPickupLocation.value

            if (pickupLocation == null) {
                binding.tvDateValidationError.text = "Lütfen alış lokasyonu seçin."
                binding.tvDateValidationError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val dropOffLocation = if (
                binding.switchDrop.isChecked &&
                viewModel.selectedDropOffLocation.value != null
            ) {
                viewModel.selectedDropOffLocation.value
            } else {
                pickupLocation
            }
            
            val pickupDateTime = getPickupDateTime()
            val dropOffDateTime = getDropOffDateTime()

            binding.tvDateValidationError.visibility = View.GONE

            val now = Calendar.getInstance(turkeyTimeZone)

            val pickupIsToday =
                pickupDateTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        pickupDateTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

            // Eğer alış tarihi bugünse, alış saati şu andan en az 1 saat sonrası olmalı
            if (pickupIsToday) {
                val minPickupTime = now.clone() as Calendar
                minPickupTime.add(Calendar.HOUR_OF_DAY, 1)

                if (pickupDateTime.before(minPickupTime)) {
                    binding.tvDateValidationError.text =
                        "Bugün için alış saati en erken şu andan 1 saat sonrası olabilir."
                    binding.tvDateValidationError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                // Bugün alınan araç için iade tarihi en az yarın olmalı
                val minDropOffDate = pickupDateTime.clone() as Calendar
                minDropOffDate.add(Calendar.DAY_OF_MONTH, 1)

                if (dropOffDateTime.before(minDropOffDate)) {
                    binding.tvDateValidationError.text =
                        "Bugün alınan araç için iade tarihi en az yarın olmalıdır."
                    binding.tvDateValidationError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
            } else {
                // Pickup bugün değilse, iade tarihi-saati pickup'tan önce olamaz
                if (dropOffDateTime.before(pickupDateTime)) {
                    binding.tvDateValidationError.text =
                        "İade saati, alış saatinden önce olamaz."
                    binding.tvDateValidationError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
            }

            val bundle = Bundle().apply {
                putLong("pickupMillis", pickupDateTime.timeInMillis)
                putLong("dropoffMillis", dropOffDateTime.timeInMillis)
                putInt("pickupLocationId", pickupLocation.id)
                putInt("dropOffLocationId", dropOffLocation!!.id)
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
        val now = Calendar.getInstance(turkeyTimeZone)

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
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->

                val tempCalendar = Calendar.getInstance(turkeyTimeZone)
                tempCalendar.timeInMillis = calendar.timeInMillis
                tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                tempCalendar.set(Calendar.MINUTE, minute)
                tempCalendar.set(Calendar.SECOND, 0)
                tempCalendar.set(Calendar.MILLISECOND, 0)

                // Eğer pickup saatini seçiyorsak ve pickup tarihi bugünse
                if (calendar === pickupTimeCalendar) {
                    val pickupDate = Calendar.getInstance(turkeyTimeZone).apply {
                        timeInMillis = pickupDateCalendar.timeInMillis
                    }

                    val now = Calendar.getInstance(turkeyTimeZone)
                    val sameDayAsToday =
                        pickupDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                                pickupDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

                    if (sameDayAsToday) {
                        val minPickupTime = now.clone() as Calendar
                        minPickupTime.add(Calendar.HOUR_OF_DAY, 1)

                        if (tempCalendar.before(minPickupTime)) {
                            binding.tvDateValidationError.text =
                                "Bugün için alış saati en erken şu andan 1 saat sonrası olabilir."
                            binding.tvDateValidationError.visibility = View.VISIBLE
                            return@TimePickerDialog
                        }
                    }
                }

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

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
        updateDropOffIfNeeded()
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
        updateDropOffIfNeeded()
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
        val today = Calendar.getInstance(turkeyTimeZone)

        val pickupIsToday =
            pickup.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    pickup.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        if (pickupIsToday) {
            val minDropOff = pickup.clone() as Calendar
            minDropOff.add(Calendar.DAY_OF_MONTH, 1)

            if (dropoff.before(minDropOff)) {
                dropOffDateCalendar.timeInMillis = minDropOff.timeInMillis
                dropOffTimeCalendar.timeInMillis = minDropOff.timeInMillis

                updateDropOffDateText()
                updateDropOffTimeText()
            }
        } else {
            if (dropoff.before(pickup)) {
                dropOffDateCalendar.timeInMillis = pickup.timeInMillis
                dropOffTimeCalendar.timeInMillis = pickup.timeInMillis

                updateDropOffDateText()
                updateDropOffTimeText()
            }
        }
    }

    // Kart içindeki tarih formatı: 09 Nis\n2026
    private fun formatDateForCard(calendar: Calendar): String {
        val dayFormat = SimpleDateFormat("dd", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }
        val monthFormat = SimpleDateFormat("MMM", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }
        val yearFormat = SimpleDateFormat("yyyy", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }

        val day = dayFormat.format(calendar.time)
        val month = monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        val year = yearFormat.format(calendar.time)

        return "$day $month\n$year"
    }

    // Kart içindeki saat formatı: 10:00
    private fun formatTimeForCard(calendar: Calendar): String {
        return SimpleDateFormat("HH:mm", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }.format(calendar.time)
    }

    // Pickup tarih ve saatini tek Calendar içinde birleştirir
    private fun getPickupDateTime(): Calendar {
        val calendar = Calendar.getInstance(turkeyTimeZone)
        calendar.timeInMillis = pickupDateCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, pickupTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, pickupTimeCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    // Dropoff tarih ve saatini tek Calendar içinde birleştirir
    private fun getDropOffDateTime(): Calendar {
        val calendar = Calendar.getInstance(turkeyTimeZone)
        calendar.timeInMillis = dropOffDateCalendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, dropOffTimeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, dropOffTimeCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
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