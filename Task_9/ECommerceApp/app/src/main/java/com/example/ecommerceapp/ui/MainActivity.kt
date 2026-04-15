package com.example.ecommerceapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.ProductAdapter
import com.example.ecommerceapp.model.Product
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var chipGroup: ChipGroup
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView
    private lateinit var btnToggleView: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var btnCart: ImageButton
    private lateinit var cartBadge: TextView
    private lateinit var emptyState: LinearLayout
    private lateinit var skeletonLayout: LinearLayout

    private var isGridMode: Boolean = false
    private val cartItems = mutableListOf<Product>()
    private var deletedItem: Product? = null
    private var deletedPosition: Int = -1
    private var currentCategory: String = "All"
    private var currentQuery: String = ""

    private val products = listOf(
        Product(1, "iPhone 14", 999.99, 4.5f, "Electronics", R.drawable.ic_launcher_foreground),
        Product(2, "Samsung Galaxy", 899.99, 4.3f, "Electronics", R.drawable.ic_launcher_foreground),
        Product(3, "MacBook Pro", 2499.99, 4.8f, "Electronics", R.drawable.ic_launcher_foreground),
        Product(4, "Blue Jeans", 49.99, 4.2f, "Clothing", R.drawable.ic_launcher_foreground),
        Product(5, "White T-Shirt", 29.99, 4.0f, "Clothing", R.drawable.ic_launcher_foreground),
        Product(6, "Winter Jacket", 199.99, 4.6f, "Clothing", R.drawable.ic_launcher_foreground),
        Product(7, "Clean Code", 39.99, 4.9f, "Books", R.drawable.ic_launcher_foreground),
        Product(8, "Design Patterns", 49.99, 4.7f, "Books", R.drawable.ic_launcher_foreground),
        Product(9, "Kotlin Programming", 59.99, 4.8f, "Books", R.drawable.ic_launcher_foreground),
        Product(10, "Organic Coffee", 14.99, 4.4f, "Food", R.drawable.ic_launcher_foreground),
        Product(11, "Green Tea", 9.99, 4.3f, "Food", R.drawable.ic_launcher_foreground),
        Product(12, "Dark Chocolate", 19.99, 4.5f, "Food", R.drawable.ic_launcher_foreground),
        Product(13, "LEGO Set", 79.99, 4.7f, "Toys", R.drawable.ic_launcher_foreground),
        Product(14, "RC Car", 89.99, 4.4f, "Toys", R.drawable.ic_launcher_foreground),
        Product(15, "Board Game", 34.99, 4.6f, "Toys", R.drawable.ic_launcher_foreground)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        showSkeletonLoading()
        setupRecyclerView()
        setupChipGroup()
        setupSearchView()
        setupToolbarButtons()
        setupItemTouchHelper()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        chipGroup = findViewById(R.id.chipGroupCategory)
        toolbar = findViewById(R.id.toolbar)
        searchView = findViewById(R.id.searchView)
        btnToggleView = findViewById(R.id.btnToggleView)
        btnSearch = findViewById(R.id.btnSearch)
        btnCart = findViewById(R.id.btnCart)
        cartBadge = findViewById(R.id.cartBadge)
        emptyState = findViewById(R.id.emptyState)
        skeletonLayout = findViewById(R.id.skeletonLayout)
        setSupportActionBar(toolbar)
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            products = products.toMutableList(),
            isGridMode = false,
            onAddToCart = { product ->
                if (!cartItems.contains(product)) {
                    cartItems.add(product)
                }
                updateCartBadge()
                Snackbar.make(recyclerView, "${product.name} added to cart", Snackbar.LENGTH_SHORT).show()
            },
            onRemoveItem = { }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupChipGroup() {
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedChipId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val category = when (checkedChipId) {
                R.id.chipElectronics -> "Electronics"
                R.id.chipClothing -> "Clothing"
                R.id.chipBooks -> "Books"
                R.id.chipFood -> "Food"
                R.id.chipToys -> "Toys"
                else -> "All"
            }
            currentCategory = category
            adapter.filterByCategory(category)
            if (currentQuery.isNotEmpty()) {
                applyQueryFilter()
            }
            checkEmptyState()
        }
    }

    private fun setupSearchView() {
        btnSearch.setOnClickListener {
            if (searchView.visibility == View.VISIBLE) {
                searchView.visibility = View.GONE
                searchView.setQuery("", false)
                currentQuery = ""
                adapter.filterByCategory(currentCategory)
                checkEmptyState()
            } else {
                searchView.visibility = View.VISIBLE
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.orEmpty()
                currentQuery = query
                adapter.filterByCategory(currentCategory)
                adapter.filterByQuery(query)
                checkEmptyState()
                return true
            }
        })
    }

    private fun setupToolbarButtons() {
        btnToggleView.setOnClickListener {
            isGridMode = !isGridMode
            adapter.updateViewMode(isGridMode)
            recyclerView.layoutManager = if (isGridMode) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }
            btnToggleView.setImageResource(
                if (isGridMode) android.R.drawable.ic_menu_sort_by_size
                else android.R.drawable.ic_menu_view
            )
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
            startActivity(intent)
        }
    }

    private fun setupItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                adapter.moveItem(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                deletedItem = adapter.getProduct(position)
                deletedPosition = position
                adapter.removeItem(position)
                checkEmptyState()

                Snackbar.make(recyclerView, "Item removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        deletedItem?.let {
                            adapter.addItem(deletedPosition, it)
                            checkEmptyState()
                        }
                    }
                    .show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun updateCartBadge() {
        cartBadge.text = cartItems.size.toString()
    }

    private fun checkEmptyState() {
        if (adapter.itemCount == 0) {
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showSkeletonLoading() {
        skeletonLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            skeletonLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }, 2000)
    }

    private fun applyQueryFilter() {
        adapter.filterByQuery(currentQuery)
    }
}
