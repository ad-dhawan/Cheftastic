package com.dhawan.cheftastic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.model.IngredientsRecipe

class IngredientsRecipeAdapter(val context: Context, private val itemList: ArrayList<IngredientsRecipe>): RecyclerView.Adapter<IngredientsRecipeAdapter.IngredientsViewHolder>() {

    class IngredientsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.txtIngredient)
        val layout: LinearLayout = view.findViewById(R.id.layoutIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredients_measurement_item, parent, false)

        return IngredientsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val ingredient = itemList[position].name

        holder.name.text = ingredient
    }

}