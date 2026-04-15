package com.example.ecommerceapp.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.CartAdapter
import com.example.ecommerceapp.model.Product
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var totalPrice: TextView
    private lateinit var btnCheckout: MaterialButton
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        totalPrice = findViewById(R.id.totalPrice)
        btnCheckout = findViewById(R.id.btnCheckout)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Shopping Cart"

        val receivedItems = intent.getParcelableArrayListExtra<Product>("cartItems")
        if (receivedItems != null) {
            cartItems.addAll(receivedItems)
        }

        cartAdapter = CartAdapter(cartItems) {
            updateTotal()
        }

        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        recyclerViewCart.adapter = cartAdapter

        btnCheckout.setOnClickListener {
            Snackbar.make(recyclerViewCart, "Order placed successfully!", Snackbar.LENGTH_SHORT).show()
            finish()
        }

        updateTotal()
    }

    private fun updateTotal() {
        val total = cartItems.sumOf { it.price }
        totalPrice.text = String.format(Locale.US, "Total: $%.2f", total)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
