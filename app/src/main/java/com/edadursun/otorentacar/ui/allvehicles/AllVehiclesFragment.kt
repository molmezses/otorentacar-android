package com.edadursun.otorentacar.ui.allvehicles

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.core.currency.CurrencyFormatter
import com.edadursun.otorentacar.core.currency.DisplayCurrency
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.databinding.FragmentAllVehiclesBinding
import com.edadursun.otorentacar.ui.allvehicles.adapter.AllVehiclesAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.launch

class AllVehiclesFragment : Fragment(R.layout.fragment_all_vehicles) {

    private var _binding: FragmentAllVehiclesBinding? = null
    private val binding get() = _binding!!

    private var originalVehicles: List<Vehicle> = emptyList()
    private var currentVehicles: List<Vehicle> = emptyList()
    private var selectedCurrency: DisplayCurrency = DisplayCurrency.EURO

    private val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("Europe/Istanbul")

    private var pickupMillis: Long = 0L
    private var dropoffMillis: Long = 0L
    private var pickupLocationId: Int = 0
    private var dropOffLocationId: Int = 0

    private val viewModel: AllVehiclesViewModel by viewModels()

    @StringRes
    private var selectedSortOptionResId: Int = R.string.recommended

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllVehiclesBinding.bind(view)

        pickupMillis = arguments?.getLong("pickupMillis") ?: 0L
        dropoffMillis = arguments?.getLong("dropoffMillis") ?: 0L
        pickupLocationId = arguments?.getInt("pickupLocationId") ?: 0
        dropOffLocationId = arguments?.getInt("dropOffLocationId") ?: 0

        setupHeader()
        setupRecyclerView()
        setupClicks()
        observeVehicles()

        val pickUpDateTime = formatApiDateTime(pickupMillis)
        val dropOffDateTime = formatApiDateTime(dropoffMillis)

        android.util.Log.d("SEARCH_PRICE_TEST", "pickupMillis = $pickupMillis")
        android.util.Log.d("SEARCH_PRICE_TEST", "dropoffMillis = $dropoffMillis")
        android.util.Log.d("SEARCH_PRICE_TEST", "pickupLocationId = $pickupLocationId")
        android.util.Log.d("SEARCH_PRICE_TEST", "dropOffLocationId = $dropOffLocationId")

        viewModel.fetchVehicles(
            pickUpDateTime = pickUpDateTime,
            dropOffDateTime = dropOffDateTime,
            pickUpLocationPointId = pickupLocationId.toString(),
            dropOffLocationPointId = dropOffLocationId.toString(),
            pickupMillis = pickupMillis,
            dropoffMillis = dropoffMillis
        )
    }

    private fun setupHeader() {
        binding.tvVehicleCount.text = getString(R.string.vehicle_count_format, currentVehicles.size)
        updateCurrencyLabel()

        if (pickupMillis != 0L) {
            val pickupCalendar = Calendar.getInstance().apply { timeInMillis = pickupMillis }
            val formatted =
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(pickupCalendar.time)
            binding.tvSelectedDate.text = "\u2022 $formatted"
        } else {
            binding.tvSelectedDate.text = getString(R.string.default_selected_date)
        }
    }

    private fun setupRecyclerView() {
        binding.rvAllVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllVehicles.adapter = AllVehiclesAdapter(emptyList(), selectedCurrency) {
            selectedVehicle -> navigateToExtras(selectedVehicle)
        }
    }

    private fun refreshVehicleList(newList: List<Vehicle>) {
        currentVehicles = newList
        binding.rvAllVehicles.adapter = AllVehiclesAdapter(currentVehicles, selectedCurrency) {
            selectedVehicle -> navigateToExtras(selectedVehicle)
        }
        setupHeader()
    }

    private fun navigateToExtras(vehicle: Vehicle) {
        android.util.Log.d("ALL_VEHICLES_IMAGE_TEST", "vehicle.imageUrl=${vehicle.imageUrl}")

        val bundle = Bundle().apply {
            putInt("vehicleModelId", vehicle.id)
            putString("vehicleName", vehicle.name)
            putString("vehicleType", vehicle.type)
            putString("vehicleTransmission", vehicle.transmission)
            putString("vehicleFuel", vehicle.fuel)
            putString("vehicleTag", vehicle.tag)
            putString(
                "vehicleDailyPrice",
                CurrencyFormatter.format(vehicle.dailyPriceAmount, vehicle.currencyCode, selectedCurrency)
            )
            putString(
                "vehicleTotalPrice",
                CurrencyFormatter.format(vehicle.totalPriceAmount, vehicle.currencyCode, selectedCurrency)
            )
            putDouble("vehicleDailyPriceAmount", vehicle.dailyPriceAmount)
            putDouble("vehicleTotalPriceAmount", vehicle.totalPriceAmount)
            putString("vehicleCurrencyCode", vehicle.currencyCode)
            putInt("vehicleCurrencyId", vehicle.currencyId)
            putString("vehicleImageUrl", vehicle.imageUrl)
            putString("displayCurrency", selectedCurrency.name)

            putLong("pickupMillis", pickupMillis)
            putLong("dropoffMillis", dropoffMillis)

            putInt("pickupLocationId", pickupLocationId)
            putInt("dropOffLocationId", dropOffLocationId)
        }

        findNavController().navigate(R.id.extrasFragment, bundle)
    }

    private fun setupClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardFilter.setOnClickListener {
            binding.cardFilter.isEnabled = false
            binding.cardFilter.isClickable = false
            binding.cardFilter.alpha = 0.5f
        }

        binding.cardCurrency.setOnClickListener {
            showCurrencyMenu(it)
        }

        binding.cardSort.setOnClickListener {
            showSortMenu(it)
        }
    }

    private fun observeVehicles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AllVehiclesUiState.Idle -> Unit
                    is AllVehiclesUiState.Loading -> Unit
                    is AllVehiclesUiState.Success -> {
                        originalVehicles = state.vehicles.sortedBy { it.orderNo }
                        applyCurrentSortAndRefresh()
                    }
                    is AllVehiclesUiState.Error -> {
                        android.util.Log.e("SEARCH_PRICE_TEST", state.message)
                    }
                }
            }
        }
    }

    private fun formatApiDateTime(millis: Long): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).apply {
            timeZone = turkeyTimeZone
        }.format(millis)
    }

    private fun updateCurrencyLabel() {
        binding.tvCurrencyLabel.text = getString(
            when (selectedCurrency) {
                DisplayCurrency.EURO -> R.string.currency_euro
                DisplayCurrency.TL -> R.string.currency_tl
            }
        )
    }

    private fun showCurrencyMenu(anchor: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_currency_dropdown, null, false)

        val popupWidth = resources.getDimensionPixelSize(R.dimen.currency_dropdown_width)
        val popupWindow = PopupWindow(
            popupView,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 0f
        }

        val euroView = popupView.findViewById<TextView>(R.id.tvCurrencyEuro)
        val tlView = popupView.findViewById<TextView>(R.id.tvCurrencyTl)

        fun styleCurrencyRows() {
            styleDropdownOption(euroView, selectedCurrency == DisplayCurrency.EURO)
            styleDropdownOption(tlView, selectedCurrency == DisplayCurrency.TL)
        }

        fun selectCurrency(currency: DisplayCurrency) {
            selectedCurrency = currency
            updateCurrencyLabel()
            applyCurrentSortAndRefresh()
            popupWindow.dismiss()
        }

        styleCurrencyRows()
        euroView.setOnClickListener { selectCurrency(DisplayCurrency.EURO) }
        tlView.setOnClickListener { selectCurrency(DisplayCurrency.TL) }

        popupWindow.showAsDropDown(anchor, anchor.width - popupWidth, 12)
    }

    private fun styleDropdownOption(view: TextView, isSelected: Boolean) {
        if (isSelected) {
            view.setTextColor(requireContext().getColor(R.color.primary_green_dark))
            view.setBackgroundResource(R.drawable.bg_vehicle_sort_dropdown)
        } else {
            view.setTextColor(requireContext().getColor(R.color.text_primary))
            view.background = null
        }
    }

    private fun showSortMenu(anchor: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_sort_dropdown, null, false)
        val horizontalPagePadding = resources.getDimensionPixelSize(R.dimen.spacing_xl) * 2
        val popupWidth = minOf(
            resources.getDimensionPixelSize(R.dimen.sort_dropdown_width),
            resources.displayMetrics.widthPixels - horizontalPagePadding
        )

        val popupWindow = PopupWindow(
            popupView,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 0f
        }

        val recommendedView = popupView.findViewById<View>(R.id.tvRecommended)
        val priceAscView = popupView.findViewById<View>(R.id.tvPriceAsc)
        val priceDescView = popupView.findViewById<View>(R.id.tvPriceDesc)
        val alphabeticalView = popupView.findViewById<View>(R.id.tvAlphabetical)

        fun styleSelected(view: View, isSelected: Boolean) {
            if (view is TextView) {
                if (isSelected) {
                    view.setTextColor(requireContext().getColor(R.color.primary_green_dark))
                    view.setBackgroundResource(R.drawable.bg_vehicle_sort_dropdown)
                } else {
                    view.setTextColor(requireContext().getColor(R.color.text_primary))
                    view.background = null
                }
            }
        }

        styleSelected(recommendedView, selectedSortOptionResId == R.string.recommended)
        styleSelected(priceAscView, selectedSortOptionResId == R.string.sort_price_asc)
        styleSelected(priceDescView, selectedSortOptionResId == R.string.sort_price_desc)
        styleSelected(alphabeticalView, selectedSortOptionResId == R.string.sort_az)

        fun selectOption(@StringRes labelResId: Int) {
            selectedSortOptionResId = labelResId
            binding.tvSortLabel.text = getString(labelResId)
            applyCurrentSortAndRefresh()
            popupWindow.dismiss()
        }

        recommendedView.setOnClickListener { selectOption(R.string.recommended) }
        priceAscView.setOnClickListener { selectOption(R.string.sort_price_asc) }
        priceDescView.setOnClickListener { selectOption(R.string.sort_price_desc) }
        alphabeticalView.setOnClickListener { selectOption(R.string.sort_az) }

        popupWindow.showAsDropDown(anchor, anchor.width - popupWidth, 12)
    }

    private fun applyCurrentSortAndRefresh() {
        val sortedList = when (selectedSortOptionResId) {
            R.string.sort_price_asc -> originalVehicles.sortedBy { priceForSort(it) }
            R.string.sort_price_desc -> originalVehicles.sortedByDescending { priceForSort(it) }
            R.string.sort_az -> originalVehicles.sortedBy { it.name.lowercase(Locale.getDefault()) }
            else -> originalVehicles
        }

        refreshVehicleList(sortedList)
    }

    private fun priceForSort(vehicle: Vehicle): Double {
        val sourceCurrency = vehicle.currencyCode.uppercase(Locale.ROOT)
        val isSourceTl = sourceCurrency == "TRY" || sourceCurrency == "TL"
        return when (selectedCurrency) {
            DisplayCurrency.EURO -> {
                if (isSourceTl) {
                    vehicle.dailyPriceAmount / CurrencyFormatter.EUR_TO_TRY_RATE
                } else {
                    vehicle.dailyPriceAmount
                }
            }
            DisplayCurrency.TL -> {
                if (isSourceTl) {
                    vehicle.dailyPriceAmount
                } else {
                    vehicle.dailyPriceAmount * CurrencyFormatter.EUR_TO_TRY_RATE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
