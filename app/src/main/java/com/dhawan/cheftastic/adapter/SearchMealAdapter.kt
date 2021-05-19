package com.dhawan.cheftastic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.activity.FoodList
import com.dhawan.cheftastic.activity.RecipeActivity
import com.dhawan.cheftastic.model.Ingredients
import com.dhawan.cheftastic.model.Meals
import com.squareup.picasso.Picasso

class SearchMealAdapter(val context: Context, private var itemList: ArrayList<Meals>): RecyclerView.Adapter<SearchMealAdapter.SearchMealViewHolder>() {

    class SearchMealViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.txtSearchMeal)
        val image: ImageView = view.findViewById(R.id.imgSearchMeal)
        val layout: CardView = view.findViewById(R.id.layoutSearchMeal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_meals_item, parent, false)

        return SearchMealViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SearchMealViewHolder, position: Int) {
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