package com.edadursun.otorentacar.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupCustomBottomBar(navController)

        updateDrawerSelection(R.id.homeFragment)
        updateCustomBottomBarSelection(R.id.homeFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateDrawerSelection(destination.id)
            updateCustomBottomBarSelection(destination.id)
        }

        setupDrawerClicks()
    }

    private fun setupCustomBottomBar(navController: NavController) {
        binding.navHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
        }

        binding.navBookings.setOnClickListener {
            if (navController.currentDestination?.id != R.id.myBookingsFragment) {
                navController.navigate(R.id.myBookingsFragment)
            }
        }

        binding.navContact.setOnClickListener {
            if (navController.currentDestination?.id != R.id.contactFragment) {
                navController.navigate(R.id.contactFragment)
            }
        }
    }

    private fun resetCustomBottomBar() {
        binding.navHomeIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))
        binding.navBookingsIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))
        binding.navContactIndicator.setCardBackgroundColor(getColor(android.R.color.transparent))

        binding.ivNavHome.setColorFilter(getColor(R.color.bottom_nav_inactive))
        binding.ivNavBookings.setColorFilter(getColor(R.color.bottom_nav_inactive))
        binding.ivNavContact.setColorFilter(getColor(R.color.bottom_nav_inactive))

        binding.tvNavHome.setTextColor(getColor(R.color.bottom_nav_inactive))
        binding.tvNavBookings.setTextColor(getColor(R.color.bottom_nav_inactive))
        binding.tvNavContact.setTextColor(getColor(R.color.bottom_nav_inactive))
    }

    private fun updateCustomBottomBarSelection(destinationId: Int) {
        resetCustomBottomBar()

        when (destinationId) {
            R.id.homeFragment,
            R.id.aboutFragment,
            R.id.servicesFragment,
            R.id.allVehiclesFragment,
            R.id.vehicleDetailFragment,
            R.id.extrasFragment,
            R.id.reservationDetailFragment,
            R.id.bookingDetailFragment -> {
                binding.navHomeIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavHome.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavHome.setTextColor(getColor(R.color.bottom_nav_active))
            }

            R.id.myBookingsFragment -> {
                binding.navBookingsIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavBookings.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavBookings.setTextColor(getColor(R.color.bottom_nav_active))
            }

            R.id.contactFragment -> {
                binding.navContactIndicator.setCardBackgroundColor(getColor(R.color.bottom_nav_selected_bg))
                binding.ivNavContact.setColorFilter(getColor(R.color.bottom_nav_active))
                binding.tvNavContact.setTextColor(getColor(R.color.bottom_nav_active))
            }
        }
    }

    private fun setupDrawerClicks() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.drawerMenu.ivDrawerClose.setOnClickListener {
            closeDrawer()
        }

        binding.drawerMenu.menuHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
            closeDrawer()
        }

        binding.drawerMenu.menuBookings.setOnClickListener {
            if (navController.currentDestination?.id != R.id.myBookingsFragment) {
                navController.navigate(R.id.myBookingsFragment)
            }
            closeDrawer()
        }

        binding.drawerMenu.menuAbout.setOnClickListener {
            if (navController.currentDestination?.id != R.id.aboutFragment) {
                navController.navigate(R.id.aboutFragment)
            }
            closeDrawer()
        }

        binding.drawerMenu.menuServices.setOnClickListener {
            if (navController.currentDestination?.id != R.id.servicesFragment) {
                navController.navigate(R.id.servicesFragment)
            }
            closeDrawer()
        }

        binding.drawerMenu.menuContact.setOnClickListener {
            if (navController.currentDestination?.id != R.id.contactFragment) {
                navController.navigate(R.id.contactFragment)
            }
            closeDrawer()
        }
    }

    private fun resetDrawerMenuStyles() {
        val transparent = android.R.color.transparent
        val textColor = getColor(R.color.text_secondary)

        binding.drawerMenu.menuHome.setBackgroundResource(transparent)
        binding.drawerMenu.menuBookings.setBackgroundResource(transparent)
        binding.drawerMenu.menuAbout.setBackgroundResource(transparent)
        binding.drawerMenu.menuServices.setBackgroundResource(transparent)
        binding.drawerMenu.menuContact.setBackgroundResource(transparent)

        binding.drawerMenu.tvMenuHome.setTextColor(textColor)
        binding.drawerMenu.tvMenuBookings.setTextColor(textColor)
        binding.drawerMenu.tvMenuAbout.setTextColor(textColor)
        binding.drawerMenu.tvMenuServices.setTextColor(textColor)
        binding.drawerMenu.tvMenuContact.setTextColor(textColor)

        binding.drawerMenu.ivMenuHome.setColorFilter(textColor)
        binding.drawerMenu.ivMenuBookings.setColorFilter(textColor)
        binding.drawerMenu.ivMenuAbout.setColorFilter(textColor)
        binding.drawerMenu.ivMenuServices.setColorFilter(textColor)
        binding.drawerMenu.ivMenuContact.setColorFilter(textColor)
    }

    private fun updateDrawerSelection(selectedId: Int) {
        resetDrawerMenuStyles()

        when (selectedId) {
            R.id.homeFragment -> {
                binding.drawerMenu.menuHome.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuHome.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuHome.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.myBookingsFragment -> {
                binding.drawerMenu.menuBookings.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuBookings.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuBookings.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.aboutFragment -> {
                binding.drawerMenu.menuAbout.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuAbout.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuAbout.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.servicesFragment -> {
                binding.drawerMenu.menuServices.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuServices.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuServices.setColorFilter(getColor(R.color.primary_green_dark))
            }

            R.id.contactFragment -> {
                binding.drawerMenu.menuContact.setBackgroundResource(R.drawable.bg_menu_item_selected)
                binding.drawerMenu.tvMenuContact.setTextColor(getColor(R.color.primary_green_dark))
                binding.drawerMenu.ivMenuContact.setColorFilter(getColor(R.color.primary_green_dark))
            }
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        } else {
            super.onBackPressed()
        }
    }
}