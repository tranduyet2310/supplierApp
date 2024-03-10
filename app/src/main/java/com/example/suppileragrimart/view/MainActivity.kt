package com.example.suppileragrimart.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        setupWithNavController(bottomNavigationView, navController)

        setSupportActionBar(findViewById(R.id.toolbar))
        val config = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, config)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.profileFragment -> supportActionBar?.hide()
                else -> {
                    supportActionBar?.show()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        val menuItem = menu!!.findItem(R.id.notificationFragment)
        val actionView = menuItem.actionView;

        val notificationBadge = actionView?.findViewById<TextView>(R.id.tvCartBadge)
        notificationBadge?.text = "3"

        actionView?.setOnClickListener(View.OnClickListener {
            onOptionsItemSelected(menuItem)
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }
}