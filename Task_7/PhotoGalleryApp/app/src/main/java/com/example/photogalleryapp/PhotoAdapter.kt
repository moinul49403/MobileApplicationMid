package com.example.photogalleryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

class PhotoAdapter(
    private val context: Context,
    private val photos: MutableList<Photo>
) : BaseAdapter() {

    var isSelectionMode: Boolean = false

    override fun getCount(): Int = photos.size

    override fun getItem(position: Int): Photo = photos[position]

    override fun getItemId(position: Int): Long = photos[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
            holder = ViewHolder(
                ivPhoto = view.findViewById(R.id.ivPhoto),
                tvTitle = view.findViewById(R.id.tvPhotoTitle),
                checkBox = view.findViewById(R.id.checkBox),
                selectionOverlay = view.findViewById(R.id.selectionOverlay)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val photo = getItem(position)
        holder.ivPhoto.setImageResource(photo.resourceId)
        holder.tvTitle.text = photo.title

        if (isSelectionMode) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = photo.isSelected
            holder.selectionOverlay.visibility = if (photo.isSelected) View.VISIBLE else View.GONE
        } else {
            holder.checkBox.visibility = View.GONE
            holder.selectionOverlay.visibility = View.GONE
            photo.isSelected = false
        }

        return view
    }

    fun getSelectedPhotos(): List<Photo> = photos.filter { it.isSelected }

    fun deleteSelected() {
        photos.removeAll { it.isSelected }
        notifyDataSetChanged()
    }

    fun clearSelections() {
        photos.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun addPhoto(photo: Photo) {
        photos.add(0, photo)
        notifyDataSetChanged()
    }

    private class ViewHolder(
        val ivPhoto: ImageView,
        val tvTitle: TextView,
        val checkBox: CheckBox,
        val selectionOverlay: View
    )
}
