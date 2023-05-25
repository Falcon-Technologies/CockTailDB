package com.sofit.task.db.model

data class Cocktail(
    val idDrink: Int,
    val strDrink: String,
    val strInstructions: String,
    val strDrinkThumb: String,
    var ingredients: MutableMap<String, String>
)