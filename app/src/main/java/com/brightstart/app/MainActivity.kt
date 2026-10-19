package com.brightstart.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.brightstart.app.databinding.ActivityMainBinding
import com.brightstart.app.utils.SessionEvents

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // The API says our token is no longer valid: send the user back to log in
        SessionEvents.expired.observe(this) { expired ->
            if (expired == true) {
                SessionEvents.consumed()
                if (navController.currentDestination?.id != R.id.loginFragment) {
                    Toast.makeText(this, "Your session has expired. Please log in again.", Toast.LENGTH_LONG).show()
                    navController.navigate(
                        R.id.loginFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                    )
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideNav = setOf(
                R.id.loginFragment,
                R.id.registerFragment
            )
            binding.bottomNav.visibility =
                if (destination.id in hideNav) View.GONE else View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}