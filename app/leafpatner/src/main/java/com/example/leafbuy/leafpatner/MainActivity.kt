package com.example.leafbuy.leafpatner

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafbuy.leafpatner.adapters.ProductAdapter
import com.example.leafbuy.leafpatner.models.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<ProductModel>()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var addProductBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        addProductBtn = findViewById(R.id.addProductbtn)

        adapter = ProductAdapter(
            productList,
            onDeleteClick = { product -> deleteProduct(product) },
            onUpdate = { product, position -> updateProduct(product, position) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadProducts()

        addProductBtn.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun loadProducts() {
        progressBar.visibility = View.VISIBLE
        listenerRegistration = db.collection("products")
            .addSnapshotListener { snapshot, e ->
                progressBar.visibility = View.GONE
                if (e != null) {
                    Toast.makeText(this, "Error loading products: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Firestore listen failed", e)
                    return@addSnapshotListener
                }

                productList.clear()
                for (doc in snapshot!!.documents) {
                    val product = doc.toObject(ProductModel::class.java)
                    product?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun showAddProductDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.edit_title)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val etPrice = dialogView.findViewById<EditText>(R.id.edit_price)
        val etImage = dialogView.findViewById<EditText>(R.id.edit_image)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Product")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val addBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addBtn.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val category = spinnerCategory.selectedItem.toString()
                val price = etPrice.text.toString().toDoubleOrNull()
                val imgUrl = etImage.text.toString().trim()

                if (title.isEmpty() || category.isEmpty() || price == null || imgUrl.isEmpty()) {
                    Toast.makeText(this, "Fill all fields correctly", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val product = ProductModel(title, category, price, imgUrl)
                db.collection("products")
                    .add(product)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Add failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

    private fun deleteProduct(product: ProductModel) {
        db.collection("products")
            .whereEqualTo("title", product.title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    doc.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Deleted: ${product.title}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Delete error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProduct(product: ProductModel, position: Int) {
        db.collection("products")
            .whereEqualTo("title", product.title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val docRef = querySnapshot.documents[0].reference
                    val updatedMap = mapOf(
                        "title" to product.title,
                        "cat" to product.cat,
                        "price" to product.price,
                        "img" to product.img
                    )
                    docRef.update(updatedMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Updated: ${product.title}", Toast.LENGTH_SHORT).show()
                            adapter.notifyItemChanged(position)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Product not found for update", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Update error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
