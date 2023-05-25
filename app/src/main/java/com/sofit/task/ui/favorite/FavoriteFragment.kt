package com.sofit.task.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.sofit.task.MainActivity
import com.sofit.task.R
import com.sofit.task.adapter.FavoritesListAdapter
import com.sofit.task.databinding.FragmentFavoriteBinding
import com.sofit.task.db.local.data.FavouriteEntity


class FavouritesFragment : Fragment(), FavoritesListAdapter.ListItemListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var adapter: FavoritesListAdapter

    private var favouriteItems: MutableList<FavouriteEntity?>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FavouritesViewModel::class.java]

        with(binding.recyclerView) {
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )

        }

        viewModel.favourites.observe(viewLifecycleOwner) {
            if (viewModel.favourites.value != null) {
                // Check if our data has been received
                if (it != null) {
                    if (it.isNotEmpty()) {
                        this.favouriteItems = it
                        // Our loader will be visible until our favouriteItems are retrieved
                        binding.progressBar.visibility = View.GONE
                        if (favouriteItems != null) {
                            viewModel.getCocktails(favouriteItems)
                            adapter = FavoritesListAdapter(favouriteItems, this@FavouritesFragment)
                            binding.recyclerView.adapter = adapter

                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.noFavouritesSaved.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        favouriteItems = null
        (activity as MainActivity).findViewById<MaterialTextView>(R.id.title).text =
            getString(R.string.favorite_recipes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveClick(
        favourite: FavouriteEntity, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int
    ) {
        Log.i(
            "FavouriteExistence",
            "Removing favorite: ${favourite.id} ${favourite.strDrink} / adapterFavorite: $adapterFavouriteId"
        )
        if (binding.recyclerView.childCount == position) {
            favouriteItems?.remove(favourite)
            viewModel.removeFavourite(favourite)
            adapter.notifyItemRemoved(position)
        } else {
            favouriteItems?.remove(favourite)
            viewModel.removeFavourite(favourite)
            adapter.notifyItemRemoved(position)
        }

        if (favouriteItems?.isEmpty() == true) {
            binding.noFavouritesSaved.visibility = View.VISIBLE
        }
    }

}