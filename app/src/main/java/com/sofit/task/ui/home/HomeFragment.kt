package com.sofit.task.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.sofit.task.MainActivity
import com.sofit.task.R
import com.sofit.task.adapter.CocktailsListAdapter
import com.sofit.task.databinding.FragmentHomeBinding
import com.sofit.task.db.local.data.FavouriteEntity
import com.sofit.task.db.local.data.MergedData
import com.sofit.task.db.model.Cocktail
import com.sofit.task.util.PreferenceUtil

class HomeFragment : Fragment(), CocktailsListAdapter.ListItemListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var adapter: CocktailsListAdapter
    private var cocktailItems: List<Cocktail>? = null
    private var favouriteItems: MutableList<FavouriteEntity?>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataThroughSearch()
        selectedRadioButton()

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val liveData = viewModel.fetchData()
        viewModel.getCocktails(viewModel.searchQuery)

        with(binding.recyclerView) {
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )        }

        liveData.observe(viewLifecycleOwner) {
            when (it) {
                is MergedData.CocktailData -> cocktailItems = it.cocktailItems
                is MergedData.FavouriteData -> favouriteItems = it.favouriteItems
                else -> {}
            }

            if (cocktailItems?.isNotEmpty() == true) {
                binding.noItemFound.visibility = View.GONE

                if (favouriteItems != null) {
                    adapter = CocktailsListAdapter(
                        cocktailItems,
                        favouriteItems,
                        this@HomeFragment
                    )
                    binding.recyclerView.adapter = adapter
                }
            } else {
                binding.noItemFound.visibility = View.VISIBLE
            }

            if (it == null) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).findViewById<MaterialTextView>(R.id.title).text =
            getString(R.string.drink_recipes)
    }

    private fun getDataThroughSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (PreferenceUtil.searchByAlphabet) {
                    p0?.let {
                        if (it.length > 1) {
                            Toast.makeText(
                                requireContext(),
                                "Search will be based upon First Alphabet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        viewModel.searchQuery = it[0].toString()
                        viewModel.getCocktails(viewModel.searchQuery)
                    }
                    return true
                }

                viewModel.searchQuery = p0.toString()
                viewModel.getCocktails(viewModel.searchQuery)

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i("Search text:", "$newText")

                return false
            }
        })
    }

    private fun selectedRadioButton() {
        if (PreferenceUtil.searchByAlphabet) binding.byAlphabet.isChecked = true
        else binding.byName.isChecked = true

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.byName -> PreferenceUtil.searchByAlphabet = false
                R.id.byAlphabet -> PreferenceUtil.searchByAlphabet = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onFavoriteClick(
        cocktail: Cocktail,
        isFavourite: Boolean,
        adapterFavouriteId: Int?,
        position: Int
    ) {
        // every time you click, run getFavourite on the cocktailId of this specific list item
        // viewModel.getFavourite then sets the value of currentFavourite
        if (favouriteItems?.contains(
                FavouriteEntity(
                    cocktail.idDrink,
                    cocktail.strDrink,
                    cocktail.strInstructions,
                    cocktail.strDrinkThumb
                )
            ) == true
        ) {
            Log.i(
                "FavouriteExistence",
                "Cocktail already exists, unsaving : ${cocktail.idDrink} / adapterfavourite: $adapterFavouriteId"
            )
            favouriteItems?.remove(
                FavouriteEntity(
                    cocktail.idDrink,
                    cocktail.strDrink,
                    cocktail.strInstructions,
                    cocktail.strDrinkThumb
                )
            )
            viewModel.removeFavourite(
                FavouriteEntity(
                    cocktail.idDrink,
                    cocktail.strDrink,
                    cocktail.strInstructions,
                    cocktail.strDrinkThumb
                )
            )
            adapter = CocktailsListAdapter(cocktailItems, favouriteItems, this@HomeFragment)

            //adapter.notifyItemChanged(position);
            //adapter.notifyDataSetChanged()
        } else {
            Log.i(
                "FavouriteExistence",
                "Cocktail does not already exist, saving: ${cocktail.idDrink} / adapterfavourite: $adapterFavouriteId"
            )
            // If this cocktailId does not already correspond with an existing favourite
            favouriteItems?.add(
                FavouriteEntity(
                    cocktail.idDrink,
                    cocktail.strDrink,
                    cocktail.strInstructions,
                    cocktail.strDrinkThumb
                )
            )
            viewModel.saveFavourite(
                FavouriteEntity(
                    cocktail.idDrink,
                    cocktail.strDrink,
                    cocktail.strInstructions,
                    cocktail.strDrinkThumb
                )
            )
            adapter = CocktailsListAdapter(cocktailItems, favouriteItems, this@HomeFragment)

        }
    }
}