package com.dhawan.cheftastic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.activity.RecipeActivity
import com.dhawan.cheftastic.model.Meals
import com.squareup.picasso.Picasso

class MealAdapter(val context: Context, private val itemList: ArrayList<Meals>): RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.txtMealName)
        val image: ImageView = view.findViewById(R.id.imgMealImage)
        val layout: CardView = view.findViewById(R.id.layoutMeal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)

        return MealViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = itemList[position]
        holder.name.text = meal.name
        Picasso.get().load(meal.image).into(holder.image)

        holder.layout.setOnClickListener {
            val openIntent = Intent(context, RecipeActivity::class.java)
            openIntent.putExtra("idMeal", meal.id)
            context.startActivity(openIntent)
        }
    }

}