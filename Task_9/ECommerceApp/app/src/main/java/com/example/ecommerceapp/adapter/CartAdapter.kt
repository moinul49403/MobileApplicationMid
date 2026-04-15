package com.example.ecommerceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.databinding.ItemCartBinding
import com.example.ecommerceapp.model.Product
import java.util.Locale

class CartAdapter(
    private val cartItems: MutableList<Product>,
    private val onRemove: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productImage.setImageResource(product.imageRes)
            binding.productName.text = product.name
            binding.productPrice.text = String.format(Locale.US, "$%.2f", product.price)
            binding.btnRemove.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                    onRemove()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size
}
