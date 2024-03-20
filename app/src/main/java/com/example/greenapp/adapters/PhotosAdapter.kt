package com.example.greenapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.databinding.ItemPhotoBinding
import com.squareup.picasso.Picasso

class PhotosAdapter(
    private val photos: List<String>,
    private val onClickPhoto: (String) -> Unit,
) :
    RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    class PhotosViewHolder(private val photosBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(photosBinding.root) {

        val imageView = photosBinding.photosPagePhotoIv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context))
        return PhotosViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val photo = photos[position]
        Picasso.get().load(photo).into(holder.imageView)
        holder.itemView.setOnClickListener {
            onClickPhoto(photo)
        }
    }

}