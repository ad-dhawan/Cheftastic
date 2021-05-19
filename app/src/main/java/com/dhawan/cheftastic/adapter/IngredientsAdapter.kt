package com.dhawan.cheftastic.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.activity.FoodList
import com.dhawan.cheftastic.activity.IngredientDetail
import com.dhawan.cheftastic.database.IngredientDatabase
import com.dhawan.cheftastic.database.IngredientEntity
import com.dhawan.cheftastic.model.Ingredients

class IngredientsAdapter(val context: Context, private var itemList: ArrayList<Ingredients>): RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    class IngredientsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.txtIngredients)
        val layout: LinearLayout = view.findViewById(R.id.layoutIngredients)
        val button: Button = view.findViewById(R.id.btnAddToList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredients_item, parent, false)

        return IngredientsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val ingredient = itemList[position]

        holder.name.text = ingredient.name

        holder.layout.setOnClickListener {
            val openIntent = Intent(context, IngredientDetail::class.java)
            openIntent.putExtra("strIngredient", ingredient.name)
            context.startActivity(openIntent)
        }

        val ingredientEntity = IngredientEntity(
            ingredient.id,
            ingredient.name
        )

        holder.button.setOnClickListener {
            if (!DBAsynTask(context,ingredientEntity,1).execute().get()){

                val result = DBAsynTask(context,ingredientEntity,2).execute().get()

                if(result){
                    Toast.makeText(context,"Added to list", Toast.LENGTH_SHORT).show()

                    holder.button.text = "Added"
                    holder.button.background = context.getDrawable(R.drawable.added_background)
                }else{
                    Toast.makeText(context,"Error! Try Again", Toast.LENGTH_SHORT).show()
                }
            } else {

                val result=DBAsynTask(context,ingredientEntity,3).execute().get()

                if(result){
                    Toast.makeText(context,"Removed from list",Toast.LENGTH_SHORT).show()

                    holder.button.text = "Add"
                    holder.button.background = context.getDrawable(R.drawable.add_background)
                }else{
                    Toast.makeText(context,"Error! Try Again",Toast.LENGTH_SHORT).show()
                }

            }

        }

        val checkAdded=DBAsynTask(context,ingredientEntity,1).execute()
        val isAdded=checkAdded.get()

        if(isAdded){
            holder.button.text = "Added"
            holder.button.background = context.getDrawable(R.drawable.added_background)

        }else{
            holder.button.text = "Add"
            holder.button.background = context.getDrawable(R.drawable.add_background)
        }

    }

    fun filterList(filteredList: ArrayList<Ingredients>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    class DBAsynTask(val context: Context, val ingredientEntity: IngredientEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, IngredientDatabase::class.java, "ingredient-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant*/


            when (mode) {
                1 -> {
                    val restaurant: IngredientEntity? = db.ingredientDao()
                        .getIngredientById(ingredientEntity.ingredientId)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.ingredientDao().insertIngredient(ingredientEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.ingredientDao().deleteIngredient(ingredientEntity)
                    db.close()
                    return true
                }
                else->return false

            }

        }

    }

}