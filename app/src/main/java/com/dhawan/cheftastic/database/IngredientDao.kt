package com.dhawan.cheftastic.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IngredientDao {

    @Insert
    fun insertIngredient(ingredientEntity: IngredientEntity)

    @Delete
    fun deleteIngredient(ingredientEntity: IngredientEntity)

    @Query("SELECT * FROM ingredients")
    fun getAllIngredients():List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE idIngredient = :ingredientId")
    fun  getIngredientById(ingredientId:String):IngredientEntity

}