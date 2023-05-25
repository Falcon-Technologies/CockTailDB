package com.sofit.task.db.local.data

import com.sofit.task.db.model.Cocktail

sealed class MergedData {
    data class CocktailData(val cocktailItems: List<Cocktail>): MergedData()
    data class FavouriteData(val favouriteItems: MutableList<FavouriteEntity?>?): MergedData()
}