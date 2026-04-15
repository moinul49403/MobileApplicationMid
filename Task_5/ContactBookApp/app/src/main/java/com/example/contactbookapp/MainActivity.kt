package com.example.contactbookapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: ContactAdapter

    private val contacts = mutableListOf(
        Contact("Ava Thompson", "+1 202-555-0101", "ava.thompson@example.com"),
        Contact("Benjamin Carter", "+1 202-555-0102", "ben.carter@example.com"),
        Contact("Charlotte Evans", "+1 202-555-0103", "charlotte.evans@example.com"),
        Contact("Daniel Brooks", "+1 202-555-0104", "daniel.brooks@example.com"),
        Contact("Emma Foster", "+1 202-555-0105", "emma.foster@example.com"),
        Contact("Felix Nguyen", "+1 202-555-0106", "felix.nguyen@example.com"),
        Contact("Grace Patel", "+1 202-555-0107", "grace.patel@example.com"),
        Contact("Henry Wilson", "+1 202-555-0108", "henry.wilson@example.com")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)
        tvEmpty = findViewById(R.id.tvEmpty)
        fab = findViewById(R.id.fab)

        adapter = ContactAdapter(this, contacts)
        listView.adapter = adapter

        updateEmptyState()

        fab.setOnClickListener {
            showAddContactDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position)
            Toast.makeText(
                this,
                "Name: ${contact.name}\nPhone: ${contact.phone}\nEmail: ${contact.email}",
                Toast.LENGTH_LONG
            ).show()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position)
            AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete ${contact.name}?")
                .setPositiveButton("Delete") { _, _ ->
                    contacts.remove(contact)
                    adapter.refreshData()
                    updateEmptyState()
                    Toast.makeText(this, "${contact.name} deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText) {
                    updateEmptyState()
                }
                return true
            }
        })
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val etName = dialogView.findViewById<EditText>(R.id.etDialogName)
        val etPhone = dialogView.findViewById<EditText>(R.id.etDialogPhone)
        val etEmail = dialogView.findViewById<EditText>(R.id.etDialogEmail)

        AlertDialog.Builder(this)
            .setTitle("Add New Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    val newContact = Contact(
                        name = name,
                        phone = phone,
                        email = email
                    )
                    contacts.add(newContact)
                    adapter.refreshData()
                    updateEmptyState()
                    Toast.makeText(this, "$name added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Name and phone are required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateEmptyState() {
        if (adapter.count == 0) {
            tvEmpty.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }
}