package com.dhawan.cheftastic.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="ingredients")
data class IngredientEntity (
    @ColumnInfo(name="idIngredient") @PrimaryKey var ingredientId:String,
    @ColumnInfo(name="strIngredient")var ingredientName:String
)