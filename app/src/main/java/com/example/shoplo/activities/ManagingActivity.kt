package com.example.shoplo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.setupWithNavController
import com.example.shoplo.R
import com.example.shoplo.databinding.ActivityManagingBinding
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class ManagingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityManagingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Setup the toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Handle navigation icon click
        toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "Navigation Icon Clicked", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }

        toolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.notification -> {
                    Toast.makeText(this, "Notification action clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.settings -> {
                    Toast.makeText(this, "Settings action clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> {
                    Toast.makeText(this, "No action clicked", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }

        val navController = findNavController(R.id.managingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_navigation_menu, menu)
        return true
    }

}
