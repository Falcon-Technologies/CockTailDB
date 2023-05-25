package com.sofit.task.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofit.task.api.RetrofitInstance
import com.sofit.task.db.local.AppDatabase
import com.sofit.task.db.local.data.FavouriteEntity
import com.sofit.task.db.local.data.MergedData
import com.sofit.task.db.model.Cocktail
import com.sofit.task.util.PreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    var searchQuery: String = "Margarita"

    val json: MutableLiveData<String?>
        get() = _json

    val _json: MutableLiveData<String?> = MutableLiveData()


    val _favourites: MutableLiveData<MutableList<FavouriteEntity?>?> = MutableLiveData()
    var tempFavourites = _favourites.value


    val favourites: LiveData<MutableList<FavouriteEntity?>?>
        get() = _favourites

    val _currentFavourite: MutableLiveData<FavouriteEntity?> = MutableLiveData()

    val currentFavourite: LiveData<FavouriteEntity?>
        get() = _currentFavourite

    private val _cocktails: MutableLiveData<List<Cocktail>> = MutableLiveData()
    val cocktails: LiveData<List<Cocktail>>
        get() = _cocktails

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    // Retrieving a list of all of our cocktails based on the provided searchQuery
    fun getCocktails(searchQuery: String) {
        // a coroutine function can only be called from a coroutine,
        // so we make one:
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _isLoading.postValue(true)
                    val fetchedCocktails: List<Cocktail> =
                        RetrofitInstance.api.getCocktails(searchQuery).drinks

                    println("elements: fetchedCocktails ${fetchedCocktails.size}")
                    if (PreferenceUtil.searchByAlphabet) {
                        val filteredList: List<Cocktail> = fetchedCocktails.filter {
                            it.strDrink.startsWith(
                                searchQuery[0], ignoreCase = true
                            )
                        }
                        println("elements: filtered ${filteredList.size}")
                        _cocktails.postValue(filteredList)
                    } else {
                        _cocktails.postValue(fetchedCocktails)
                    }
                    val favourite = database?.favouriteDao()?.getAll()

                    _favourites.postValue(favourite)

                    _isLoading.postValue(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database?.favouriteDao()?.insertFavourite(favouriteEntity)

                _currentFavourite.postValue(favouriteEntity)

                tempFavourites?.add(favouriteEntity)
                _favourites.postValue(tempFavourites)
            }
        }
    }

    fun removeFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                database?.favouriteDao()?.removeFavourite(favouriteEntity.id)
                _currentFavourite.postValue(null)
                tempFavourites?.remove(favouriteEntity)
                _favourites.postValue(tempFavourites)
            }
        }
    }

    fun getFavourite(favouriteId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favourite = database?.favouriteDao()?.getFavouriteById(favouriteId)

                favourite?.let {
                    _currentFavourite.postValue(it)
                    Log.i("Favourite", "Cocktail Returned from DB" + it.strDrink)
                    //exists = true;
                }
            }
        }
    }

    fun getAllFavourites() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favourites = database?.favouriteDao()?.getAll()

                favourites?.let {
                    _favourites.postValue(it)
                }
            }
        }
    }


    // Here's our implementation of our MediatorLiveData
    // I'm using the mediator to allow accessing two data streams at once

    // the view seemed to have difficulty observing two different data streams,
    // so the mediator allows us to combine them into one and observe them at the same time
    fun fetchData(): MediatorLiveData<MergedData> {
        val liveDataMerger = MediatorLiveData<MergedData>()
        // we've already defined our sealed MergedData class, now we add our sources to it
        liveDataMerger.addSource(cocktails) {
            if (it != null) {
                liveDataMerger.value = MergedData.CocktailData(it)
            }
        }
        liveDataMerger.addSource(favourites) {
            if (it != null) {
                liveDataMerger.value = MergedData.FavouriteData(it)
            }
        }
        return liveDataMerger
    }

}