package com.sofit.task.db.model

data class CocktailResponse (
    val drinks: List<Cocktail>,
    val ingredients: List<Ingredient>
)