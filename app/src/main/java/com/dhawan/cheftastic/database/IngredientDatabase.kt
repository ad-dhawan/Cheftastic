package com.dhawan.cheftastic.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IngredientEntity::class],version = 1)
abstract class IngredientDatabase: RoomDatabase() {

    abstract fun ingredientDao():IngredientDao

}