package com.example.ecommerceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.databinding.ItemProductGridBinding
import com.example.ecommerceapp.databinding.ItemProductListBinding
import com.example.ecommerceapp.model.Product
import java.util.Collections
import java.util.Locale

class ProductAdapter(
    private val products: MutableList<Product>,
    private var isGridMode: Boolean = false,
    private val onAddToCart: (Product) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val allProducts = products.toMutableList()

    companion object {
        const val VIEW_TYPE_LIST = 0
        const val VIEW_TYPE_GRID = 1
    }

    inner class ListViewHolder(
        private val binding: ItemProductListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productImage.setImageResource(product.imageRes)
            binding.productName.text = product.name
            binding.categoryTag.text = product.category
            binding.ratingBar.rating = product.rating
            binding.productPrice.text = String.format(Locale.US, "$%.2f", product.price)
            binding.btnAddToCart.text = if (product.inCart) "In Cart" else "Add"
            binding.btnAddToCart.isEnabled = !product.inCart
            binding.btnAddToCart.setOnClickListener {
                if (!product.inCart) {
                    product.inCart = true
                    onAddToCart(product)
                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }
    }

    inner class GridViewHolder(
        private val binding: ItemProductGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productImage.setImageResource(product.imageRes)
            binding.productName.text = product.name
            binding.productPrice.text = String.format(Locale.US, "$%.2f", product.price)
            binding.btnAddToCart.isEnabled = !product.inCart
            binding.btnAddToCart.alpha = if (product.inCart) 0.5f else 1f
            binding.btnAddToCart.setOnClickListener {
                if (!product.inCart) {
                    product.inCart = true
                    onAddToCart(product)
                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridMode) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GRID) {
            val binding = ItemProductGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            GridViewHolder(binding)
        } else {
            val binding = ItemProductListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListViewHolder -> holder.bind(products[position])
            is GridViewHolder -> holder.bind(products[position])
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateViewMode(isGrid: Boolean) {
        isGridMode = isGrid
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String) {
        val filteredList = if (category == "All") {
            allProducts.toList()
        } else {
            allProducts.filter { it.category == category }
        }
        updateWithDiff(filteredList)
    }

    fun filterByQuery(query: String) {
        val lowerQuery = query.lowercase(Locale.getDefault())
        val filteredList = if (lowerQuery.isBlank()) {
            products.toList()
        } else {
            products.filter {
                it.name.lowercase(Locale.getDefault()).contains(lowerQuery) ||
                    it.category.lowercase(Locale.getDefault()).contains(lowerQuery)
            }
        }
        updateWithDiff(filteredList)
    }

    fun removeItem(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
        onRemoveItem(position)
    }

    fun addItem(position: Int, product: Product) {
        products.add(position, product)
        notifyItemInserted(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(products, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(products, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getProduct(position: Int): Product = products[position]

    private fun updateWithDiff(newList: List<Product>) {
        val diffCallback = ProductDiffCallback(products, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        products.clear()
        products.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
