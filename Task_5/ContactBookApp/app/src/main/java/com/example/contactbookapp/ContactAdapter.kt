package com.example.contactbookapp

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val allContacts: MutableList<Contact>
) : ArrayAdapter<Contact>(context, 0, allContacts), Filterable {

    private var filteredContacts: MutableList<Contact> = allContacts.toMutableList()

    private val avatarColors = arrayOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4",
        "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107",
        "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#1ABC9C", "#2ECC71",
        "#3498DB", "#34495E", "#16A085", "#27AE60", "#2980B9"
    )

    private inner class ViewHolder(
        val tvAvatar: TextView,
        val tvName: TextView,
        val tvPhone: TextView,
        val ivCall: ImageView
    )

    override fun getCount(): Int = filteredContacts.size

    override fun getItem(position: Int): Contact = filteredContacts[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View
        val holder: ViewHolder

        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(
                tvAvatar = rowView.findViewById(R.id.tvAvatar),
                tvName = rowView.findViewById(R.id.tvName),
                tvPhone = rowView.findViewById(R.id.tvPhone),
                ivCall = rowView.findViewById(R.id.ivCall)
            )
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = rowView.tag as ViewHolder
        }

        val contact = getItem(position)
        holder.tvAvatar.text = contact.initial
        holder.tvName.text = contact.name
        holder.tvPhone.text = contact.phone
        holder.ivCall.contentDescription = "Call ${contact.name}"

        val initialChar = contact.initial.firstOrNull()?.uppercaseChar() ?: '?'
        val colorIndex = if (initialChar in 'A'..'Z') initialChar - 'A' else 0
        val avatarDrawable = holder.tvAvatar.background.mutate() as GradientDrawable
        avatarDrawable.setColor(android.graphics.Color.parseColor(avatarColors[colorIndex]))

        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim().orEmpty()
                val results = FilterResults()
                val contactsToShow = if (query.isEmpty()) {
                    allContacts.toMutableList()
                } else {
                    allContacts.filter {
                        it.name.contains(query, ignoreCase = true)
                    }.toMutableList()
                }
                results.values = contactsToShow
                results.count = contactsToShow.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredContacts = (results?.values as? MutableList<Contact>) ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }

    fun refreshData() {
        filteredContacts = allContacts.toMutableList()
        notifyDataSetChanged()
    }
}
