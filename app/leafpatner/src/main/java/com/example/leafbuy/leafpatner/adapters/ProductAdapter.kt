package com.example.leafbuy.leafpatner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.leafbuy.leafpatner.R
import com.example.leafbuy.leafpatner.models.ProductModel

class ProductAdapter(
    private val productList: MutableList<ProductModel>,
    private val onDeleteClick: (ProductModel) -> Unit,
    private val onUpdate: (ProductModel, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.image_)
        val tvTitle: TextView = itemView.findViewById(R.id.plant_title)
        val etTitle: EditText = itemView.findViewById(R.id.edit_title)

        val tvCategory: TextView = itemView.findViewById(R.id.plant_category)
        val etCategory: EditText = itemView.findViewById(R.id.edit_category)

        val tvPrice: TextView = itemView.findViewById(R.id.plant_price)
        val etPrice: EditText = itemView.findViewById(R.id.edit_price)

        val btnEdit: AppCompatButton = itemView.findViewById(R.id.btn_edit)
        val btnDelete: AppCompatButton = itemView.findViewById(R.id.btn_delete)

        var isInEditMode = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_view, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        fun updateViewMode(editMode: Boolean) {
            holder.isInEditMode = editMode
            if (editMode) {
                // Edit mode: show EditTexts, hide TextViews
                holder.tvTitle.visibility = View.GONE
                holder.etTitle.visibility = View.VISIBLE
                holder.etTitle.setText(product.title)

                holder.tvCategory.visibility = View.GONE
                holder.etCategory.visibility = View.VISIBLE
                holder.etCategory.setText(product.cat)

                holder.tvPrice.visibility = View.GONE
                holder.etPrice.visibility = View.VISIBLE
                holder.etPrice.setText(product.price.toString())

                holder.btnEdit.text = "Save"
            } else {
                // Normal mode: show TextViews, hide EditTexts
                holder.tvTitle.visibility = View.VISIBLE
                holder.etTitle.visibility = View.GONE
                holder.tvTitle.text = product.title

                holder.tvCategory.visibility = View.VISIBLE
                holder.etCategory.visibility = View.GONE
                holder.tvCategory.text = product.cat

                holder.tvPrice.visibility = View.VISIBLE
                holder.etPrice.visibility = View.GONE
                holder.tvPrice.text = "$${product.price}"

                holder.btnEdit.text = "Edit"
            }
        }

        updateViewMode(holder.isInEditMode)

        // TODO: Load image from product.img using Glide or similar lib, if you want

        holder.btnEdit.setOnClickListener {
            if (holder.isInEditMode) {
                // Save changes
                val newTitle = holder.etTitle.text.toString().trim()
                val newCat = holder.etCategory.text.toString().trim()
                val newPriceText = holder.etPrice.text.toString().trim()
                val newPrice = newPriceText.toDoubleOrNull() ?: product.price

                product.title = newTitle
                product.cat = newCat
                product.price = newPrice

                onUpdate(product, position)

                updateViewMode(false)
            } else {
                updateViewMode(true)
            }
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(product)
        }
    }
}
