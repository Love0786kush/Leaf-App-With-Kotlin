package com.example.leafbuy.love0786kush

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafbuy.love0786kush.Adapter.PlantAdapter
import com.example.leafbuy.love0786kush.Modal.PlantItem
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerRecommended: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: PlantAdapter
    private lateinit var searchEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerRecommended = findViewById(R.id.recyclerRecommended)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        searchEdit = findViewById(R.id.edit_search)

        setupRecommendedRecycler()
        setupBottomNavigation()
    }

    private fun setupRecommendedRecycler() {
        val query = FirebaseDatabase.getInstance()
            .reference
            .child("plants")

        val options = FirebaseRecyclerOptions.Builder<PlantItem>()
            .setQuery(query, PlantItem::class.java)
            .build()

        adapter = PlantAdapter(options, this)

        recyclerRecommended.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerRecommended.adapter = adapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_explore -> Toast.makeText(this, "Explore", Toast.LENGTH_SHORT).show()
                R.id.nav_wishlist -> Toast.makeText(this, "Wishlist", Toast.LENGTH_SHORT).show()
                R.id.nav_cart -> Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
