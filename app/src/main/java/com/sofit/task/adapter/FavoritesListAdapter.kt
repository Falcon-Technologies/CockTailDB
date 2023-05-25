package com.sofit.task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofit.task.databinding.ItemRowBinding
import com.sofit.task.db.local.data.FavouriteEntity

class FavoritesListAdapter(
    private var favouritesList: MutableList<FavouriteEntity?>?,
    private val listener: ListItemListener
) : RecyclerView.Adapter<FavoritesListAdapter.ViewHolder>() {
    var isFavourite: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount() = favouritesList!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favourite = favouritesList?.get(holder.adapterPosition)

        with(holder.binding) {
            favourite?.let {
                Glide.with(root).load(favourite.strDrinkThumb).centerCrop().into(image)
                title.text = favourite.strDrink
                description.text = favourite.strInstructions
            }

            this.favorite.isChecked = favourite != null

            favorite.setOnClickListener {
                if (favourite != null) {
                     listener.onSaveClick(
                        favourite,
                        isFavourite,
                        favourite.id,
                        holder.layoutPosition
                    )
                }
            }
        }
    }

    fun setFavourites(newFavourites: MutableList<FavouriteEntity?>) {
        favouritesList = newFavourites
    }

    fun getFavourite(id: Int): FavouriteEntity? {
        return favouritesList?.find { it?.id == id }
    }

    fun removeFavourite(id: Int) {
        favouritesList?.remove(getFavourite(id))
    }


    interface ListItemListener {

        fun onSaveClick(
            favourite: FavouriteEntity,
            isFavourite: Boolean,
            adapterFavouriteId: Int?,
            position: Int
        )
    }
}