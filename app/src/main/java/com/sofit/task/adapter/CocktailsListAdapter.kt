package com.sofit.task.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofit.task.R
import com.sofit.task.databinding.ItemRowBinding
import com.sofit.task.db.local.data.FavouriteEntity
import com.sofit.task.db.model.Cocktail

class CocktailsListAdapter(
    private val cocktailsList: List<Cocktail>?,
    private var favouritesList: MutableList<FavouriteEntity?>?,
    private val listener: ListItemListener,
) : RecyclerView.Adapter<CocktailsListAdapter.ViewHolder>() {
    var favourite: FavouriteEntity? = null
    var isFavourite: Boolean = false
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount() = cocktailsList!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = cocktailsList?.get(holder.adapterPosition)

        if (!favouritesList?.isEmpty()!! && cocktail != null) {
            favourite = getFavourite(cocktail.idDrink)
        }


        with(holder.binding) {
            cocktail?.let {
                Glide.with(root).load(cocktail.strDrinkThumb)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(image)

                title.text = cocktail.strDrink
                description.text = cocktail.strInstructions

            }

            this@with.favorite.isChecked = favourite != null

            this@with.favorite.setOnClickListener {
                if (cocktail != null) {
                    listener.onFavoriteClick(cocktail, isFavourite, favourite?.id, position)
                }
            }
        }
    }

    private fun getFavourite(id: Int): FavouriteEntity? {
        return favouritesList?.find { it?.id == id }
    }

    interface ListItemListener {
        fun onFavoriteClick(
            cocktail: Cocktail,
            isFavourite: Boolean,
            adapterFavouriteId: Int?,
            position: Int
        )
    }
}